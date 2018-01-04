package com.example.admin.nantuoappdemo.activity.offline;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.admin.nantuoappdemo.R;
import com.example.admin.nantuoappdemo.activity.ChooseMapFunctionActivity;
import com.example.admin.nantuoappdemo.activity.MainActivity;
import com.example.admin.nantuoappdemo.othermanager.RxActivity;

import butterknife.Bind;

public class OfflineMainActivity extends RxActivity {
    @Bind(R.id.bmapViews)
    MapView mMapView;
    @Bind(R.id.btn_myOfflin)
    Button btn_myOfflin;

    private BaiduMap mBaiduMap;

    // 定位相关声明
    public LocationClient locationClient = null;
    //自定义图标
    private BitmapDescriptor mCurrentMarker;
    boolean isFirstLoc = true;// 是否首次定位
    public BDLocationListener myListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);    //设置定位数据
            //自定义图标
            MyLocationConfiguration configuration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, mCurrentMarker);
            mBaiduMap.setMyLocationConfiguration(configuration);

            if (isFirstLoc) {
                isFirstLoc = false;

                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 16);   //设置地图中心点以及缩放级别
                mBaiduMap.animateMapStatus(u);
            }
        }
    };

    @Override
    protected int getLayoutResource() {
        SDKInitializer.initialize(getApplicationContext());
        return R.layout.activity_offline;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        actionBar();

        //注册监听函数
        initListener();
        mBaiduMap = mMapView.getMap();
        //开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        locationClient = new LocationClient(getApplicationContext()); // 实例化LocationClient类
        locationClient.registerLocationListener(myListener); // 注册监听函数

        this.setLocationOption();   //设置定位参数
        locationClient.start(); // 开始定位

        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL); // 设置为一般地图
        // baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE); //设置为卫星地图
        mBaiduMap.setTrafficEnabled(true); //开启交通图
    }

    @Override
    protected void loadData() {

    }

    private void initListener() {
        btn_myOfflin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OfflineMainActivity.this, OfflineMapActivity.class));
            }
        });
    }

//    private void initDT() {
//        //设定郑州中心点  34.754674, 113.631062
//        //北京 116.415767,39.917149
//        //113.31161,40.096821
//        LatLng cenpt = new LatLng(37.871976, 112.550642);
//        //定义地图状态
//        MapStatus mMapStatus = new MapStatus.Builder()
//                .target(cenpt)
//                .zoom(14)
//                .build();
//        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
//        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
//        //改变地图状态
//        mBaiduMap.setMapStatus(mMapStatusUpdate);
//    }

    @Override
    protected void onDestroy() {
        //退出时销毁定位
        locationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        // TODO Auto-generated method stub
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
//        mMapView.onDestroy();
        mMapView = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    /**
     * 设置定位参数
     */
    private void setLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开GPS
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
        option.setCoorType("bd09ll"); // 返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(5000); // 设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true); // 返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true); // 返回的定位结果包含手机机头的方向

        //初始化图标
        mCurrentMarker = BitmapDescriptorFactory.fromResource(R.mipmap.navi_map_gps_locked);
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
                startActivity(new Intent(OfflineMainActivity.this, ChooseMapFunctionActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
