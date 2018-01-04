package com.example.admin.nantuoappdemo.activity.arnavigation;

import android.os.Bundle;

import com.baidu.lbsapi.BMapManager;
import com.baidu.lbsapi.panoramaview.PanoramaView;
import com.baidu.lbsapi.panoramaview.PanoramaViewListener;
import com.example.admin.nantuoappdemo.R;
import com.example.admin.nantuoappdemo.othermanager.MyApplication;
import com.example.admin.nantuoappdemo.othermanager.RxActivity;

import butterknife.Bind;

/**
 * 全景图导航
 */
public class PanoramaActivity extends RxActivity {
    @Bind(R.id.panorama)
    PanoramaView mPanoView;

    @Override
    protected int getLayoutResource() {
        // 先初始化BMapManager
        initBMapManager();
        return R.layout.activity_panorama;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

        double latitude = getIntent().getDoubleExtra("latitude", 0);
        double longitude = getIntent().getDoubleExtra("longitude", 0);
        int uid = getIntent().getIntExtra("uid", 0);
        testPanoByType(latitude, longitude, uid);
    }

    @Override
    protected void loadData() {

    }

    private void initBMapManager() {
        MyApplication app = (MyApplication) this.getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(app);
            app.mBMapManager.init(new MyApplication.MyGeneralListener());
        }
    }

    private void testPanoByType(double latitude, double longitude, int uid) {
        mPanoView.setShowTopoLink(true);
        // 测试回调函数,需要注意的是回调函数要在setPanorama()之前调用，否则回调函数可能执行异常
        mPanoView.setPanoramaViewListener(new PanoramaViewListener() {

            @Override
            public void onLoadPanoramaBegin() {

            }

            @Override
            public void onLoadPanoramaEnd(String json) {
            }

            @Override
            public void onLoadPanoramaError(String error) {
            }

            @Override
            public void onDescriptionLoadEnd(String json) {

            }

            @Override
            public void onMessage(String msgName, int msgType) {

            }

            @Override
            public void onCustomMarkerClick(String key) {

            }
        });
        mPanoView.setPanorama(longitude, latitude);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPanoView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPanoView.onResume();
    }

    @Override
    protected void onDestroy() {
        mPanoView.destroy();
        super.onDestroy();
    }
}
