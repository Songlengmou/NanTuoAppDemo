package com.example.admin.nantuoappdemo.othermanager;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.nantuoappdemo.R;
import com.example.admin.nantuoappdemo.utils.ViewUtil;

import butterknife.ButterKnife;

/**
 * Created by admin on 2017/12/21.
 */

public abstract class BaseActivity extends AppCompatActivity {
    public Context mContext;
    protected ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariables();

        ViewUtil.initSystemBar(this, R.color.system_bar_bg);

        setContentView(getLayoutResource());
        mContext = this;
        ButterKnife.bind(this);
        initViews(savedInstanceState);
        loadData();
    }

    /**
     * 初始化变量，包括Intent带的数据和activity内的变量
     */
    protected void initVariables() {

    }

    /**
     * 加载layout布局文件
     */
    protected abstract int getLayoutResource();

    /**
     * 初始化控件，设置控件事件
     *
     * @param savedInstanceState
     */
    protected abstract void initViews(Bundle savedInstanceState);

    /**
     * 调用api获取数据
     */
    protected abstract void loadData();


    protected String getName() {
        return BaseActivity.class.getName();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        //TDRequestManager.getSingleton().cancelRequest(getName());
    }

    //------------------------------------------------------------------

    /**
     * 设置Activity标题
     */
    public void setTitle(int resId) {
        LinearLayout layout = findViewById(R.id.layout_top);
        TextView textView = layout.findViewById(R.id.tv_activity_title);
        textView.setText(resId);
    }

    /**
     * 设置点击监听器
     *
     * @param listener
     */
    public void setOnClickListener(View.OnClickListener listener) {
        LinearLayout layout = findViewById(R.id.layout_top);
        LinearLayout optionsButton = layout.findViewById(R.id.btn_activity_options);
        optionsButton.setOnClickListener(listener);
    }

    /**
     * 不显示设置按钮
     */
    public void setOptionsButtonInVisible() {
        LinearLayout layout = findViewById(R.id.layout_top);
        LinearLayout optionsButton = layout.findViewById(R.id.btn_activity_options);
        optionsButton.setVisibility(View.INVISIBLE);
    }

    /**
     * 回退事件
     *
     * @param v
     */
    public void onBack(View v) {
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }


    /**
     * Toast提示信息的方法
     */
    public static void toastShow(final Activity activity, final String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * @param ed
     * @return 个人详情列表中发送框的表情
     */
    public String getEdtext(EditText ed) {
        return ed.getText().toString();
    }

}
