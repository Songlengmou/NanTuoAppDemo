package com.example.admin.nantuoappdemo.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.admin.nantuoappdemo.R;
import com.example.admin.nantuoappdemo.callbreak.ListItemClick;
import com.example.admin.nantuoappdemo.view.Image;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by admin on 2017/12/22.
 */

public class MessageListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<EMConversation> list;

    private HashMap<String, String> textMap = new HashMap<>();

    private ListItemClick listItemClick;

    public void setListItemClick(ListItemClick listItemClick) {
        this.listItemClick = listItemClick;
    }

    public MessageListAdapter(Context context, ArrayList<EMConversation> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_message_list, parent, false);
            viewHolder = new ViewHolder(convertView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 从数据源拿到当前item的数据
        EMConversation emConversation = (EMConversation) getItem(position);
        //从当前会话对象中 获取 最后一条消息的对象
        EMMessage latMessage = emConversation.getLastMessage();

        //-草稿-----------------------------------------
        if (!TextUtils.isEmpty(textMap.get(emConversation.conversationId()))) {
            viewHolder.content.setText("[草稿]" + textMap.get(emConversation.conversationId()));

        } else {
            //从最后一条消息对象中 获取该消息的消息类型
            EMMessage.Type type = latMessage.getType();

            switch (type) {
                case TXT:
                    //获取消息体 并强转成 文本类型消息体
                    EMTextMessageBody txtMessage = (EMTextMessageBody) latMessage.getBody();
                    //-------------------------------表情包--------------------------------------
                    String str = txtMessage.getMessage();
                    SpannableString spannableString = new SpannableString(str);

                    // 正则表达式比配字符串里是否含有表情
                    String zhengze = "\\[[^\\]]+\\]";
                    //通过传入的正则表达式来生成一个Pattern
                    Pattern sinaPatten = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE);

                    Matcher matcher = sinaPatten.matcher(spannableString);

                    while (matcher.find()) {
                        String key = matcher.group();

                        try {
                            if (Image.getImae(key) == 0) {
                                continue;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            continue;
                        }

                        int indexStart = matcher.start();
                        int indexEnd = indexStart + key.length();

                        spannableString.setSpan(
                                //new ImageSpan(context, R.drawable.li)
                                new ImageSpan(context, Image.getImae(key))
                                , indexStart
                                , indexEnd
                                , Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                        );
                    }

                    viewHolder.content.setText(spannableString);
                    //--------------------------------------------------------------------
                    //从消息体中拿到消息内容 并 设置给 控件
                    //viewHolder.content.setText(txtMessage.getMessage());
                    break;
                case IMAGE:
                    viewHolder.content.setText("[图片]");
                    break;
                case VIDEO:
                    viewHolder.content.setText("[视频]");
                    break;
                case VOICE:
                    viewHolder.content.setText("[音频]");
                    break;
            }
        }

        viewHolder.lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listItemClick != null) {
                    listItemClick.onClick(position);
                }
            }
        });

        //-------未读消息数---------
        viewHolder.name.setText(emConversation.conversationId());
        int unread = emConversation.getUnreadMsgCount();
        try {
            if (unread > 0) {
                viewHolder.unread.setVisibility(View.VISIBLE);
            } else {
                viewHolder.unread.setVisibility(View.GONE);
            }
            viewHolder.unread.setText(unread + ""); //未读消息数
        } catch (Exception e) {
            e.printStackTrace();
        }
        //------强制转化成文本信息------
        viewHolder.time.setText(getLastMsgTime(emConversation) + "");
//        String ss;
//        try {
//            // 强制转化成文本信息，如果之前消息是其他消息类型，此行代码抛出异常
//            EMTextMessageBody tetBody = (EMTextMessageBody) emConversation.getLastMessage().getBody();
//            // 拿到文本消息
//            ss = tetBody.getMessage();
//        } catch (Exception e) {
//            ss = "";
//            e.printStackTrace();
//        }
//        viewHolder.content.contentsetText(ss);
        //删除本行内容
        final View finalConvertView = convertView;
        convertView.findViewById(R.id.btn_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(position);
                notifyDataSetChanged();  //TODO  侧滑中的删除按钮有点问题,刪除后還能出現
                ((SwipeMenuLayout) finalConvertView).quickClose();
            }
        });

        viewHolder.name.setText(emConversation.conversationId());
        return convertView;
    }

    //-----------------消息发送的时间---------------------------------------------
    private String getLastMsgTime(EMConversation msg) {
        // 获取最后一条消息的时间
        long t = msg.getLastMessage().getMsgTime();
        // 最后一条消息的时间与当前时间的时间差
        long notT = new Date().getTime() - t;

        int m = m2M(notT);// 把毫秒转换成分钟
        if (m > 60) {// 判断是否大于60分钟 如果大于 转成小时
            if (m2h(m) > 24) {// 判断转成小时后 是否大于24小时
                return h2d(m2h(m)) + "天前"; // 大于24小时
            }
            return m2h(m) + "小时前"; // 分钟转小时。没大于24小时
        } else {
            if (m > 1)// 判断是否大于1分钟 如果不是 显示 刚刚
                return m + "分钟前";
            else
                return "刚刚";
        }
    }

    private int m2M(long time) {
        return (int) (time / 1000 / 60);
    }

    private int m2h(long time) {
        return (int) (time / 60);
    }

    private int h2d(long time) {
        return (int) (time / 24);
    }

    // 获取某一会话的未读消息数
    private int getUnreadMsgC(String username) {
        return EMClient.getInstance().chatManager().getConversation(username).getUnreadMsgCount();
    }

    private String getTxt(String json) {
        JSONObject jsonObject = null;
        String str = "";
        try {
            jsonObject = new JSONObject(json);
            str = jsonObject.getString("txt");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return str;
    }

    //--------------------------------------------------------------
    //生命周期
    public void refAll(List<EMConversation> list) {
        this.notifyDataSetChanged();
    }

    //--------------------------------------------------------------
    class ViewHolder {
        private TextView name, content, time, unread;
        private View lay;

        public ViewHolder(View view) {
            this.name = view.findViewById(R.id.chat_list_name);
            this.content = view.findViewById(R.id.chat_list_content);
            this.time = view.findViewById(R.id.chat_list_time);
            this.unread = view.findViewById(R.id.chat_list_unread);
            this.lay = view.findViewById(R.id.chat_list_ll);
        }
    }

    //草稿(上边也有   草稿的判断)
    public void setTextMap(HashMap<String, String> textMap) {
        this.textMap = textMap;
        notifyDataSetChanged();
    }
}
