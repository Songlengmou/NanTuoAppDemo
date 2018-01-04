package com.example.admin.nantuoappdemo.activity.login;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.admin.nantuoappdemo.R;
import com.example.admin.nantuoappdemo.activity.ChooseMapFunctionActivity;
import com.example.admin.nantuoappdemo.activity.MainActivity;
import com.example.admin.nantuoappdemo.activity.register.RegisterActivity;
import com.example.admin.nantuoappdemo.activity.social.SocialActivity;
import com.example.admin.nantuoappdemo.othermanager.RxActivity;
import com.example.admin.nantuoappdemo.utils.SPUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import butterknife.Bind;

import static android.text.TextUtils.isEmpty;

/**
 * 登录页
 */
public class LoginActivity extends RxActivity {
    @Bind(R.id.userName_edt)
    EditText username;
    @Bind(R.id.password_edt)
    EditText password;

    private String name, passwords;
    private boolean login = false;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_login;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        actionBar();

        //初始化sp数据库
        username.setText(SPUtils.getLastLoginUserName(this));
        password.setText(SPUtils.getLastLoginPassword(this));

        //光标位置
        username.setSelection(username.getText().toString().length());
        password.setSelection(password.getText().toString().length());

        //判断之前是否登陆过，并如果用户名改变，则清空密码
        logBefore();
    }

    @Override
    protected void loadData() {

    }

    private void logBefore() {
        // 如果登录成功过，直接进入主页面
        if (EMClient.getInstance().isLoggedInBefore()) {
            // ** 免登陆情况 加载所有本地群和会话
            //加上的话保证进了主页面会话和群组都已经load完毕
            EMClient.getInstance().groupManager().loadAllGroups();
            EMClient.getInstance().chatManager().loadAllConversations();
            login = true;
            startActivity(new Intent(LoginActivity.this, SocialActivity.class));
            return;
        }
        // 如果用户名改变，清空密码
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password.setText(null);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    //登陆
    public void login(View view) {
        if (!isNetWorkConnected(this)) {
            Toast.makeText(this, R.string.sorry_wifi, Toast.LENGTH_SHORT).show();
            return;
        }
        //获取输入的数据
        name = username.getText().toString();
        passwords = password.getText().toString();
        //判断密码正确错误
        if (isEmpty(name)) {
            Toast.makeText(LoginActivity.this, R.string.sorry_user, Toast.LENGTH_SHORT).show();
            return;
        }
        if (isEmpty(passwords)) {
            Toast.makeText(LoginActivity.this, R.string.sorry_password, Toast.LENGTH_SHORT).show();
            return;
        }

        // 调用sdk登陆方法登陆聊天服务器
        EMClient.getInstance().login(name, passwords, new EMCallBack() {
            @Override
            public void onSuccess() {
                SPUtils.setLastLoginUserName(LoginActivity.this, name);
                SPUtils.setLastLoginPassword(LoginActivity.this, passwords);
                // 进入主页面
                startActivity(new Intent(LoginActivity.this, SocialActivity.class));
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(final int code, final String message) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.and_false, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    //注册
    public void register(View view) {
        startActivityForResult(new Intent(this, RegisterActivity.class), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (login) {
            return;
        }
    }

    //检测网络是否可用
    public static boolean isNetWorkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable() && mNetworkInfo.isConnected();
            }
        }

        return false;
    }

    //返回键
    private void actionBar() {
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.app_title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(LoginActivity.this, ChooseMapFunctionActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
