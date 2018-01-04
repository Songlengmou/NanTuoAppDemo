package com.example.admin.nantuoappdemo.activity.footprint;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.admin.nantuoappdemo.R;
import com.example.admin.nantuoappdemo.activity.ChooseMapFunctionActivity;
import com.example.admin.nantuoappdemo.activity.login.LoginActivity;
import com.example.admin.nantuoappdemo.othermanager.MyApplication;
import com.example.admin.nantuoappdemo.othermanager.RxActivity;

import java.util.List;

import butterknife.Bind;

public class TraceMainActivity extends RxActivity {
    private MyApplication trackApp;
    @Bind(R.id.btn_trace)
    Button trace;
    @Bind(R.id.btn_query)
    Button query;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_trace_main;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        actionBar();

        trackApp = (MyApplication) getApplicationContext();


        trace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TraceMainActivity.this, TracingActivity.class);
                startActivity(intent);
            }
        });

        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TraceMainActivity.this, TrackQueryActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        // 适配android M，检查权限
//        List<String> permissions = new ArrayList<>();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isNeedRequestPermissions(permissions)) {
//            requestPermissions(permissions.toArray(new String[permissions.size()]), 0);
//        }
    }

    private boolean isNeedRequestPermissions(List<String> permissions) {
        // 定位精确位置
        addPermission(permissions, Manifest.permission.ACCESS_FINE_LOCATION);
        // 存储权限
        addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        // 读取手机状态
        addPermission(permissions, Manifest.permission.READ_PHONE_STATE);
        return permissions.size() > 0;
    }

    private void addPermission(List<String> permissionsList, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        CommonUtil.saveCurrentLocation(trackApp);
//        if (trackApp.trackConf.contains("is_trace_started")
//                && trackApp.trackConf.getBoolean("is_trace_started", true)) {
//            // 退出app停止轨迹服务时，不再接收回调，将OnTraceListener置空
//            trackApp.mClient.setOnTraceListener(null);
//            trackApp.mClient.stopTrace(trackApp.mTrace, null);
//        } else {
//            System.out.println(456);
//            trackApp.mClient.clear();
//        }
//        trackApp.isTraceStarted = false;
//        trackApp.isGatherStarted = false;
//        SharedPreferences.Editor editor = trackApp.trackConf.edit();
//        editor.remove("is_trace_started");
//        editor.remove("is_gather_started");
//        editor.apply();
//
//        BitmapUtil.clear();
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
                startActivity(new Intent(TraceMainActivity.this, ChooseMapFunctionActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
