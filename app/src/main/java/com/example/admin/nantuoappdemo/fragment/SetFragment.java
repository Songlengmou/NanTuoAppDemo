package com.example.admin.nantuoappdemo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.admin.nantuoappdemo.R;
import com.example.admin.nantuoappdemo.activity.login.LoginActivity;
import com.example.admin.nantuoappdemo.othermanager.BaseFragment;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by admin on 2017/12/22.
 * <p>
 * 设置对话聊天界面
 */

public class SetFragment extends BaseFragment implements View.OnClickListener, EMMessageListener {
    @Bind(R.id.username)
    EditText username;
    @Bind(R.id.count)
    EditText count;
    @Bind(R.id.main_send)
    Button main_send;
    @Bind(R.id.main_exit)
    Button main_exit;
    private EMMessage message;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_set;
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //注册消息监听
        EMClient.getInstance().chatManager().addMessageListener(this);
    }

    @OnClick({R.id.main_send, R.id.main_exit})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_exit:
                // 退出环信服务器 再次启动程序 会重新登陆
                EMClient.getInstance().logout(true);
                intentTwo(LoginActivity.class);// 返回登录界面
                break;
            case R.id.main_send:
                String userName = username.getText().toString();
                String content = count.getText().toString();
                message = EMMessage.createTxtSendMessage(content, userName);
                EMClient.getInstance().chatManager().sendMessage(message);
                break;
        }
    }

    // 跳转页面方法
    private void intentTwo(Class<?> loginActivityClass) {
        Intent intent = new Intent(getActivity(), loginActivityClass);
        getActivity().startActivity(intent);
    }

    @Override
    public void onMessageReceived(List<EMMessage> list) {

    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> list) {

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
}
