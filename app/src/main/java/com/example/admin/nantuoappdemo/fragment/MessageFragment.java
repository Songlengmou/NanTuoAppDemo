package com.example.admin.nantuoappdemo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.admin.nantuoappdemo.R;
import com.example.admin.nantuoappdemo.activity.social.SocialActivity;
import com.example.admin.nantuoappdemo.adapter.MessageListAdapter;
import com.example.admin.nantuoappdemo.callbreak.ListItemClick;
import com.example.admin.nantuoappdemo.callbreak.LoadListener;
import com.example.admin.nantuoappdemo.view.MyRefreshLayout;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMConversationListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2017/12/22.
 * <p>
 * 消息列表页
 */

public class MessageFragment extends Fragment implements EMCallBack, EMConversationListener, ListItemClick {

    private ListView main_list;

    private View view;
    MessageListAdapter cle;
    ArrayList<EMConversation> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return view = inflater.inflate(R.layout.fragment_message_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        main_list = view.findViewById(R.id.main_list);
        initListView(view);
    }

    //-----------------------下拉刷新---------------------------------------
    private void setRefreshLayout(final View view) {
//        final SwipeRefreshLayout swipeRefreshLayout;
        final MyRefreshLayout myRefreshLayout;
        myRefreshLayout = view.findViewById(R.id.Swipe_srf);
        myRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        myRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        myRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));

//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                //下拉刷新内容
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        swipeRefreshLayout.setRefreshing(false);
//                        Toast.makeText(getActivity(), "数据已更新", Toast.LENGTH_SHORT).show();
//                    }
//                }, 3000);
//            }
//        });
        myRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                myRefreshLayout.setRefreshing(false);
                cle.notifyDataSetChanged();
                Toast.makeText(getActivity(), R.string.sj, Toast.LENGTH_SHORT).show();
            }
        });

        //设置上拉加载
        myRefreshLayout.setLoadListener(new LoadListener() {
            @Override
            public void load() {
                myRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cle.notifyDataSetChanged();
                        myRefreshLayout.setLoadingView(false);
                    }
                }, 2500);
            }

            @Override
            public void setFootView(boolean loading) {
                if (loading) {
                } else {
                    main_list.removeAllViews();
                }
            }
        });
    }

    //--------------------------------------------------------------
    // 加载数据
    private void initDate() {
        list.clear();
        // 获取所有会话
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        // 把map数组添加到ArrayList中
        for (EMConversation emc : conversations.values()) {
            list.add(emc);
        }
        sort();// 调用下方的方法(排序)
    }

    //--------------------------------------------------------------
    private void initListView(View view) {
        setRefreshLayout(view);

        // 调用加载数据方法
        initDate();
        // 实例化适配器
        cle = new MessageListAdapter(getActivity(), list);
        // 给listvie设置适配器
        main_list.setAdapter(cle);

        cle.setListItemClick(this);
    }

    // 发送信息成功 回调此方法
    @Override
    public void onSuccess() {
        Toast.makeText(getActivity(), R.string.cm, Toast.LENGTH_SHORT).show();
    }

    // （回调的是EMCallBack实现的方法）
    // 发送信息失败 回调此方法
    @Override
    public void onError(int i, String s) {
        Toast.makeText(getActivity(), R.string.fm, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProgress(int i, String s) {

    }

    // 当会话个数有改变时 会触发此内容
    // 注： 会话个数无改变，内容有改变时 不会触发此方法
    @Override
    public void onCoversationUpdate() {
        // 调用加载数据方法
        initDate();
        // 运行UI线程
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                // 刷新listView
                cle.refAll(list);
            }
        });
    }

    //用接口实现的
    @Override
    public void onClick(int id) {
        list.get(id).getType();
        //((BaseActivity) getActivity()).intentPrivateMessage(list.get(id).getUserName());
        //如果写草稿就写成下方这个
        ((SocialActivity) getActivity()).intentPrivateMessage(list.get(id).conversationId());
    }

    // -------------在PopulationActivity类中的 onMessageReceived()方法中-----------
    public void notifyList() {
        // 调用加载数据方法
        initDate();
        if (cle != null) {
            // 刷新listView
            cle.refAll(list);
        }
    }

    //--------------------------------------------------------------
    // 给list集合排序的方法 (在个人聊天中标题栏显示发送消息的人或群)
    private void sort() {
        // 集合排序 的 规则 接口
        Comparator comp = new Comparator<EMConversation>() {

            @Override
            public int compare(EMConversation o1, EMConversation o2) {

                // 判不判断交换位置要根据 1 和 -1 来的
                if (o1.getLastMessage().getMsgTime() < o2.getLastMessage().getMsgTime()) {
                    return 1;
                } else if (o1.getLastMessage().getMsgTime() == o2.getLastMessage().getMsgTime()) {
                    return 0;
                } else if (o1.getLastMessage().getMsgTime() > o2.getLastMessage().getMsgTime()) {
                    return -1;
                }
                return 0;
            }

        };
        // 主要的（使用这个规格来排序）
        Collections.sort(list, comp);
    }

    //------------------------草稿-------------------------------------
    public void setChatText(HashMap<String, String> textMap) {
        cle.setTextMap(textMap);
    }
}
