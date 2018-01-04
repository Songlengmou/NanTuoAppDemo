package com.example.admin.nantuoappdemo.activity.social;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.nantuoappdemo.R;
import com.example.admin.nantuoappdemo.adapter.PrivateMessageAdapter;
import com.example.admin.nantuoappdemo.fragment.CountenanceFragment;
import com.example.admin.nantuoappdemo.fragment.PrivateImageSelectFragment;
import com.example.admin.nantuoappdemo.othermanager.RxActivity;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMVideoMessageBody;

import org.wlf.filedownloader.FileDownloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 个人聊天详情页
 */
public class PrivateMessageActivity extends RxActivity implements View.OnClickListener, EMMessageListener, EMCallBack, AdapterView.OnItemLongClickListener {
    String newFileDir = Environment
            .getExternalStorageDirectory()
            .getAbsolutePath()
            + File.separator
            + "FileDownloader";

    private TextView titleName;
    private ListView msgShowList;
    private EditText textEdit;
    private ImageView imageView;
    private Button sendBtn, btn_pictures, btn_photo, btn_luX, btn_biao;
    private String userName, groupId;
    ArrayList<EMMessage> messages = new ArrayList<>();
    private PrivateMessageAdapter privateMessageAdapter;
    EMConversation conversation;
    /************
     * 点击发送图片按钮所用的对象
     *********************/
    FragmentManager fragmentManager;
    PrivateImageSelectFragment privateImageSelectFragment;
    String text;
    FragmentTransaction transaction;

    //拍照
    private static final int OPEN_IMAGE_CAMERA = 1001;
    private File file;
    private static final int OPEN_VIDEO_CAPTURE = 1002;

    private static final String TAG = "PrivateMessageActivity";

    /***表情包**/
    private FrameLayout a, aa;
    private CountenanceFragment countenanceFragment;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_private_message;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        EMClient.getInstance().chatManager().addMessageListener(this);
        userName = getIntent().getStringExtra("userName");
        groupId = getIntent().getStringExtra("groupId");
        initView();
        setTitleName();
        initListView();
        InitFragment();
        actionBar();
    }

    @Override
    protected void loadData() {

    }

    //-------------------标题栏的返回键  选择框-------------------------------------------
    private void actionBar() {
        try {
            //getSupportActionBar().setHomeAsUpIndicator();//设置返回键的图片(很少用)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(userName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //选择框
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                intent.putExtra("text", text);
                intent.putExtra("userName", userName);
                setResult(RESULT_OK, intent);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //--------------------------------------------------------------

    /************
     * 点击发送图片按钮所用的
     *************************/
    private void InitFragment() {
        fragmentManager = getSupportFragmentManager();
        privateImageSelectFragment = new PrivateImageSelectFragment();
    }

    /********************************************************/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(this);
    }

    @Override
    public void onBackPressed() {
        //text = editText.getText().toString();
        Intent intent = new Intent();
        intent.putExtra("text", text);
        intent.putExtra("userName", userName);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    private void initView() {
        textEdit = findViewById(R.id.private_message_editText);
        msgShowList = findViewById(R.id.private_message_lv);
        sendBtn = findViewById(R.id.private_message_send_btn);
        imageView = findViewById(R.id.private_message_title_right);
        imageView.setOnClickListener(this);

        countenanceFragment = new CountenanceFragment();
        a = findViewById(R.id.a);
        aa = findViewById(R.id.aq);
        //------------------------草稿-------------------------------------
        userName = getIntent().getStringExtra("userName");
        text = getIntent().getStringExtra("text");

        if (!TextUtils.isEmpty(text)) {
            textEdit.setText(text);
            textEdit.setSelection(textEdit.getText().length());
        }

        textEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                text = s.toString();
            }
        });
        //-------------------------------------------------------------
        btn_pictures = findViewById(R.id.btn_pictures);
        btn_photo = findViewById(R.id.btn_photo);
        btn_luX = findViewById(R.id.btn_luX);
        btn_biao = findViewById(R.id.btn_biao);
        btn_pictures.setOnClickListener(this);
        btn_photo.setOnClickListener(this);
        btn_luX.setOnClickListener(this);
        btn_biao.setOnClickListener(this);

        msgShowList.setOnItemLongClickListener(this);
        msgShowList.setSelection(msgShowList.getBottom());// 实现滑动list的效果
        sendBtn.setOnClickListener(this);
        btn_photo = findViewById(R.id.btn_photo);

        titleName = findViewById(R.id.private_message_title_name);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.private_message_send_btn:
                // String str = textEdit.getText().toString();
                String edtext = getEdtext(textEdit);
                try {
                    sendTxt(edtext);

                    privateMessageAdapter.upData(messages);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                textEdit.setText("");
                conversation.setExtField(""); //-------草稿--------
                privateMessageAdapter.notifyDataSetChanged();
                break;
            case R.id.private_message_title_right://TODO  有点小bug
//                Intent intent = new Intent(PrivateMessageActivity.this, GroupActivity.class);
//                intent.putExtra("groupId", groupId);
//                startActivity(intent);
                break;
            /************点击底部的发送图片按钮所进行的判断*********************/
            case R.id.btn_pictures:
                a.setVisibility(View.VISIBLE);
                aa.setVisibility(View.GONE);
                //判断底部fragment是否添加过  如果有则 删除fragment 反之 添加
                if (privateImageSelectFragment.isAdded()) {
                    closeImgFragment();
                } else {
                    transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.a, privateImageSelectFragment);
                    transaction.addToBackStack("message_bottom_fragment_lay");
                    transaction.commit();
                }

                break;
            case R.id.btn_photo:
                Intent intent1 = new Intent();
                intent1.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
//                intent1.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(
//                        file));
                file = new File(Environment
                        .getExternalStorageDirectory()
                        .getAbsoluteFile()
                        + "/"
                        + System.currentTimeMillis()
                        + ".jpg");
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                startActivityForResult(intent1, OPEN_IMAGE_CAMERA);
                break;
            case R.id.btn_luX:
                Intent caotureImageCamera = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                caotureImageCamera.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                startActivityForResult(caotureImageCamera, OPEN_VIDEO_CAPTURE);
                break;
            case R.id.btn_biao:
                //TODO  表情包
                Toast.makeText(PrivateMessageActivity.this, R.string.not_yet_open, Toast.LENGTH_SHORT).show();
//                a.setVisibility(View.GONE);
//                aa.setVisibility(View.VISIBLE);
//                if (countenanceFragment.isAdded()) {
//                    closeCountenanceFragment();
//                } else {
//                    openexpressionFragment();
//                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case OPEN_IMAGE_CAMERA:
                if (resultCode == RESULT_OK) {
                    //拿到 camera 拍照后的图片
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                    file = getBitMap(bitmap);

                    sendImage(file.getAbsolutePath(), false);//这里是 发送 缩略图
                }
                break;
            case OPEN_VIDEO_CAPTURE:
                //获取视频路径
                String path = getPath(data.getData());
                // 实例化
                MediaPlayer mediaPlayer = new MediaPlayer();
                // 设置数据源
                try {
                    mediaPlayer.setDataSource(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //获取时长
                int duration = mediaPlayer.getDuration();

                //实例化
                MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
                //设置数据源
                metadataRetriever.setDataSource(path);

                //获取某一帧 图像  并写入文件
                file = getBitMap(metadataRetriever.getFrameAtTime(1000));
                // 释放资源
                metadataRetriever.release();
                mediaPlayer.release();

                if (resultCode == RESULT_OK) {
                    EMMessage videoMsg = EMMessage.createVideoSendMessage(
                            getPath(data.getData())  //视频路径
//                            , Environment.getExternalStorageDirectory().getAbsolutePath()
//                                    + "/1492697485238.jpg"  //目前是固定的本地图片
                            , file.getAbsolutePath()     //视频预览图片路径
                            , duration                  //视频时长
                            , userName                 //用户名
                    );
                    sendMessage(videoMsg);
                }
                break;
        }
    }

    private File getBitMap(Bitmap bitmap) {
        try {
            //创建文件对象
            file = new File(Environment
                    .getExternalStorageDirectory(),
                    +System.currentTimeMillis()
                            + ".jpg");
            //开启这个文件的输出流
            FileOutputStream out = new FileOutputStream(file);
            //把bitmap内容写入输出流
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            System.out.print("_______保存的__sd____下___");
            try {
                //刷新输出流
                out.flush();
                //关闭输出流
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

        return file;
    }

    /**
     * 根据视频文件的uri 获取path
     *
     * @param uri 视频文件uri
     * @return 文件path
     */
    private String getPath(Uri uri) {
        //定义 需要查询的字段
        String[] projection = {MediaStore.Video.Media.DATA};
        //查询该 Uri
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        //获取 所需字段 对应的列下标
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
        //将 游标 指针移动到第一个
        cursor.moveToFirst();
        //返回 根据字段下标获取出来的数据
        return cursor.getString(column_index);
    }

    //-----
    private void closeImgFragment() {
        transaction = fragmentManager.beginTransaction();
        transaction.remove(privateImageSelectFragment);
        transaction.commit();
        //从fragment的返回栈中移除fragment
        fragmentManager.popBackStackImmediate("privateMessage_bottom_fragment_lay", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
    //-------------------发送图片-------------------------------------------

    /**
     * @param imgPath     图片路径
     * @param isThumbnail 是否发送原图  true 原图  false 缩略图
     */
    public void sendImage(String imgPath, boolean isThumbnail) {
        //创建image消息
        EMMessage message = EMMessage.createImageSendMessage(imgPath, isThumbnail, userName);
        //发送
        sendMessage(message);
    }

    private void sendMessage(EMMessage message) {
        //如果是群聊，设置chattype，默认是单聊
//                if (chatType == CHATTYPE_GROUP)
        message.setChatType(EMMessage.ChatType.Chat);
        message.setMessageStatusCallback(this);

        //发送2消息
        EMClient.getInstance()
                .chatManager()
                .sendMessage(message);
        //图片发送之后 关闭图片选择fragment
        if (privateImageSelectFragment.isAdded()) {
            closeImgFragment();
        }
        //将消息添加到 数据源 消息集合中
        messages.add(message);
        //调用刷新消息列表的方法
        privateMessageAdapter.notifyDataSetChanged();
        //调用刷新消息列表的方法
//        MessageManager.getInsatance()
//                .getMessageListListener()
//                .refChatList();
    }


    //-----------------发送消息文本的方法---------------------------------------------
    private void sendTxt(String str) {
        EMMessage message;
        if (TextUtils.isEmpty(userName)) {
            // 1.创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
            message = EMMessage.createTxtSendMessage(str, groupId);
        } else {
            message = EMMessage.createTxtSendMessage(str, userName);
        }

        // 2.如果是群聊，设置chattype，默认是单聊
        if (TextUtils.isEmpty(userName)) {
            message.setChatType(EMMessage.ChatType.GroupChat);
        }

        // 设置消息状态回调
        message.setMessageStatusCallback(this);
        // 3.发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
        text = "";
        addMessageList(message);
    }

    private void addMessageList(EMMessage message) {
        messages.add(message);
        privateMessageAdapter.notifyDataSetChanged();
        // 设置被选的Item，liseview滚动到最后
        msgShowList.setSelection(msgShowList.getBottom());
    }

    //--------------------------------------------------------------
    //设置用户名
    private void setTitleName() {
        if (TextUtils.isEmpty(groupId)) {
            titleName.setText(userName);
            //sendBtn.setVisibility(View.GONE);
        } else {
            titleName.setText(groupId);
            sendBtn.setVisibility(View.VISIBLE);
        }
    }

    private void initListView() {
        initData();
        privateMessageAdapter = new PrivateMessageAdapter(this, messages);
        msgShowList.setAdapter(privateMessageAdapter);
    }


    // 获取聊天记录
    private void initData() {
        if (TextUtils.isEmpty(groupId)) {
            // 获取单个聊天
            conversation = EMClient.getInstance()
                    .chatManager().getConversation(userName);

            // 获取此会话的所有消息
            messages = (ArrayList<EMMessage>) conversation.getAllMessages();
        } else {
            // 获取单个聊天
            conversation = EMClient.getInstance().chatManager().getConversation(groupId);
            if (conversation != null) {
                // 获取此会话的所有消息
                messages = (ArrayList<EMMessage>) conversation.getAllMessages();
            } else {
                messages = new ArrayList<>();
            }
        }
    }

    //--------------------------------------------------------------
    // listView的item 长按事件
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        // 获取点击事件的item内容数据
        EMMessage msg = (EMMessage) privateMessageAdapter.getItem((int) id);
        // 删除和某个user会话，如果需要保留聊天记录，传false
        conversation.removeMessage(msg.getMsgId());
        messages.remove((int) id);
        // 刷新listView
        privateMessageAdapter.notifyDataSetChanged();
        return false;
    }

    //-------------------(接收消息)-------------------------------
    //实现EMMessageListener接口
    //①收到消息
    @Override
    public void onMessageReceived(final List<EMMessage> list) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (EMMessage message : list) {
                    addMessageList(message);
                }
            }
        });
        //---------视频--------------------
        // 修改过 本地缩略图路径的 视频消息集合
        ArrayList<EMMessage> list1 = new ArrayList<>();
        // 遍历接受到的消息
        for (EMMessage msg :
                list) {
            // 处理视频消息
            switch (msg.getType()) {
                case VIDEO:
                    // 获得消息体
                    EMVideoMessageBody emVideo = (EMVideoMessageBody) msg.getBody();
                    // 文件名
                    String name = System.currentTimeMillis() + ".jpg";
                    // 下载视频缩略图
                    FileDownloader.createAndStart(
                            emVideo.getThumbnailUrl()
                            , newFileDir
                            , name);
                    // 把本地路径设置给消息体
                    emVideo.setLocalThumb(newFileDir + "/" + name);
                    // 把消息体添加到 消息对象中
                    msg.addBody(emVideo);
                    // 把修改过的消息对象添加到集合中
                    list1.add(msg);
                    // 处理过的消息添加到数据源
                    this.messages.add(msg);
                    break;
                default:
                    // 添加到数据源
                    this.messages.add(msg);
                    break;
            }
        }
        // 把消息导入到数据库
        EMClient.getInstance().chatManager().importMessages(list1);
        // 刷新listview
        privateMessageAdapter.notifyDataSetChanged();
        Log.e("onMessageReceived", "onMessageReceived" + list.size());

    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> list) {
        //收到透传消息
    }

    @Override
    public void onMessageRead(List<EMMessage> list) {

    }

    @Override
    public void onMessageDelivered(List<EMMessage> list) {

    }

    @Override
    public void onMessageRecalled(List<EMMessage> list) {

    }

    @Override
    public void onMessageChanged(EMMessage emMessage, Object o) {

    }

    //--------------------------------------------------------------
    //实现EMCallBack接口
    @Override
    public void onSuccess() { //控件隐藏
        Log.e("onSuccess", "onSuccess");
    }

    //一般要写一个红色叹号
    @Override
    public void onError(int i, String s) {
        Log.e("onError", "onError =" + i + " " + s);
    }

    @Override
    public void onProgress(int i, String s) {

    }


    //-------------------------------------------------
    private void closeCountenanceFragment() {
        transaction = fragmentManager.beginTransaction();
        transaction.remove(countenanceFragment);
        transaction.commit();
        //从fragment的返回棧中移除fragment
        fragmentManager
                .popBackStackImmediate(
                        "message_coutenance_fragment"
                        , FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private void openexpressionFragment() {
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.aq, countenanceFragment);
        transaction.addToBackStack("message_coutenance_fragment");
        transaction.commit();
    }

    /**
     * @param s
     */
    public void getEdit(String s) {
        String edtext = getEdtext(textEdit);
        textEdit.setText(edtext + s);
    }
}
