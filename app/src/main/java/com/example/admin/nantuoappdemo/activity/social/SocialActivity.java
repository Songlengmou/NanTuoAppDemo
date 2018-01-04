package com.example.admin.nantuoappdemo.activity.social;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.admin.nantuoappdemo.R;
import com.example.admin.nantuoappdemo.activity.ChooseMapFunctionActivity;
import com.example.admin.nantuoappdemo.activity.login.LoginActivity;
import com.example.admin.nantuoappdemo.fragment.LinkManFragment;
import com.example.admin.nantuoappdemo.fragment.MessageFragment;
import com.example.admin.nantuoappdemo.fragment.SetFragment;
import com.example.admin.nantuoappdemo.othermanager.RxActivity;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.NetUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 聊天整体界面控制
 */
public class SocialActivity extends RxActivity implements View.OnClickListener, EMConnectionListener, EMMessageListener {
    @Bind(R.id.tv_message)
    TextView tv_message;
    @Bind(R.id.tv_linkman)
    TextView tv_linkman;
    @Bind(R.id.tv_set)
    TextView tv_set;
    @Bind(R.id.tv_textView)
    TextView tv_textView;
    @Bind(R.id.viewPager_pager)
    ViewPager viewpager;

    private MessageFragment mf;
    private LinkManFragment lmf;
    private SetFragment sf;
    FragmentManager fm;

    private List<Fragment> list = new ArrayList<>();
    private HashMap<String, String> textMap = new HashMap<>();
    private String str;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_social;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        actionBar();

        EMClient.getInstance().addConnectionListener(this);// 注册消息监听

        mf = new MessageFragment();
        lmf = new LinkManFragment();
        sf = new SetFragment();
        list.add(mf);
        list.add(lmf);
        list.add(sf);
        initFragment();
    }

    @Override
    protected void loadData() {

    }

    private void initFragment() {
        fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        FragmentPagerAdapter fpa = new FragmentPagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                return list.get(position);
            }

            @Override
            public int getCount() {
                return list.size();
            }
        };
        viewpager.setAdapter(fpa);
        viewpager.setCurrentItem(0);
    }

    @OnClick({R.id.tv_message, R.id.tv_linkman, R.id.tv_set})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_message:
                viewpager.setCurrentItem(0);
                break;
            case R.id.tv_linkman:
                viewpager.setCurrentItem(1);
                break;
            case R.id.tv_set:
                viewpager.setCurrentItem(2);
                break;
        }
    }

    @Override
    public void onConnected() {
        tv_textView.setVisibility(View.GONE);
    }

    @Override
    public void onDisconnected(final int error) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (error == EMError.USER_REMOVED) {
                    // 显示帐号已经被移除
                    toastShow(SocialActivity.this, "帐号已经被移除");
                    tv_textView.setText(R.string.remove_user);
                    tv_textView.setVisibility(View.VISIBLE);
                } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                    // 显示帐号在其他设备登录
                    toastShow(SocialActivity.this, "帐号在其他设备登录");
                    tv_textView.setText(R.string.no_login_user);
                    tv_textView.setVisibility(View.VISIBLE);
                } else {
                    if (NetUtils.hasNetwork(SocialActivity.this)) {
                        //连接不到聊天服务器
                        toastShow(SocialActivity.this, "连接不到聊天服务器");
                        tv_textView.setText(R.string.not_service);
                        tv_textView.setVisibility(View.VISIBLE);
                    } else {
                        //当前网络不可用，请检查网络设置
                        toastShow(SocialActivity.this, "当前网络不可用，请检查网络设置");
                        tv_textView.setText(R.string.not_wifi);
                        tv_textView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    //------------------------草稿-------------------------------------
    public void intentPrivateMessage(String userName) {
        Intent intent = new Intent(this, PrivateMessageActivity.class);
        intent.putExtra("userName", userName);
        if (!TextUtils.isEmpty(str))
            intent.putExtra("text", textMap.get(str));
        //startActivity(intent);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 101:
                str = data.getStringExtra("userName");
                textMap.put(str, data.getStringExtra("text"));
                try {
                    if (TextUtils.isEmpty(data.getStringExtra("text"))) {
                        textMap.remove(data.getStringExtra("userName"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mf.setChatText(textMap);
                break;
        }
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
                startActivity(new Intent(SocialActivity.this, ChooseMapFunctionActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
