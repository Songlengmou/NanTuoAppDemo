package com.example.admin.nantuoappdemo.activity;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.admin.nantuoappdemo.R;
import com.example.admin.nantuoappdemo.bean.ChooseCityInterface;
import com.example.admin.nantuoappdemo.othermanager.MyApplication;
import com.example.admin.nantuoappdemo.othermanager.RxActivity;
import com.example.admin.nantuoappdemo.utils.BLEVideoUtils;
import com.example.admin.nantuoappdemo.utils.BitmapUtil;
import com.example.admin.nantuoappdemo.utils.ChooseCityUtil;
import com.example.admin.nantuoappdemo.utils.CommonUtil;
import com.example.admin.nantuoappdemo.utils.NetUtil;
import com.vise.baseble.ViseBle;
import com.vise.baseble.callback.IConnectCallback;
import com.vise.baseble.callback.scan.IScanCallback;
import com.vise.baseble.core.DeviceMirror;
import com.vise.baseble.exception.BleException;
import com.vise.baseble.model.BluetoothLeDevice;
import com.vise.baseble.model.BluetoothLeDeviceStore;
import com.vise.baseble.utils.BleUtil;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 主页          TODO 检测蓝牙弹出视频  和 视频框问题            按下返回键每次都会重复，不能一次性解决
 * <p>
 * 此界面用来展示如何结合定位SDK实现定位，并使用MyLocationOverlay绘制定位位置 同时展示如何使用自定义图标绘制并点击时弹出泡泡
 */
public class MainActivity extends RxActivity implements SensorEventListener {
    private static final String LTAG = MainActivity.class.getSimpleName();
    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private MyLocationConfiguration.LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;
    private String mCurrentCity, mCurrentProvince, mCurrentDistrict, actionbarCity, actionbarProvince, actionbarDistrict;
    private GeoCoder geoCoder;

    @Bind(R.id.videoView)
    VideoView videoView;
    @Bind(R.id.bmapView)
    MapView mMapView;

    BaiduMap mBaiduMap;

    // UI相关
    @Bind(R.id.btn_video)
    Button btn_video;
    @Bind(R.id.btn_map_choose_dialog)
    Button chooseDialog;
    @Bind(R.id.btn_map_chooses)
    Button requestLocButton;
    boolean isFirstLoc = true; // 是否首次定位
    private MyLocationData locData;
    private SDKReceiver mReceiver;
    private MenuItem cityItem;

    private MyApplication demoApp;

    /**
     * 构造广播监听类，监听 SDK key 验证以及网络异常广播
     */
    public class SDKReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            Log.d(LTAG, "action: " + s);
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                Toast.makeText(MainActivity.this, R.string.enorr_key + intent.getIntExtra
                        (SDKInitializer.SDK_BROADTCAST_INTENT_EXTRA_INFO_KEY_ERROR_CODE, 0)
                        + R.string.p_key, Toast.LENGTH_LONG).show();

            } else if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK)) {
//                Toast.makeText(MainActivity.this, R.string.success_key, Toast.LENGTH_LONG).show();

            } else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                Toast.makeText(MainActivity.this, R.string.wifi_e, Toast.LENGTH_LONG).show();
            }
        }
    }

    //------------------------------ble----------------------------
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            //更新UI
            switch (msg.what) {
                case 1:
                    videoView.setVisibility(View.INVISIBLE);
                    videoView.stopPlayback();
                    break;
                case 2:
                    videoView.setVisibility(View.VISIBLE);
                    videoView.start();
                    break;
            }
        }
    };

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

        // 注册 SDK 广播监听者
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mReceiver = new SDKReceiver();
        registerReceiver(mReceiver, iFilter);
        demoApp = (MyApplication) getApplicationContext();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);//获取传感器管理服务
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        requestLocButton.setText(R.string.ordinary);

        View.OnClickListener btnClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (mCurrentMode) {
                    case NORMAL:
                        requestLocButton.setText(R.string.follow);
                        mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
                        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                                mCurrentMode, true, mCurrentMarker));
                        MapStatus.Builder builder = new MapStatus.Builder();
                        builder.overlook(0);
                        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                        break;
                    case COMPASS:
                        requestLocButton.setText(R.string.ordinary);
                        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
                        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                                mCurrentMode, true, mCurrentMarker));
                        MapStatus.Builder builder1 = new MapStatus.Builder();
                        builder1.overlook(0);
                        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder1.build()));
                        break;
                    case FOLLOWING:
                        requestLocButton.setText(R.string.compass);
                        mCurrentMode = MyLocationConfiguration.LocationMode.COMPASS;
                        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                                mCurrentMode, true, mCurrentMarker));
                        break;
                    default:
                        break;
                }
            }
        };
        requestLocButton.setOnClickListener(btnClickListener);

        mBaiduMap = mMapView.getMap();
        geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有检索到结果
                    Toast.makeText(getApplicationContext(), R.string.noresult, Toast.LENGTH_SHORT).show();
                    return;
                }
                LatLng latlng = result.getLocation();
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latlng);
                mBaiduMap.animateMapStatus(msu);
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

            }
        });
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(getApplicationContext());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，设置定位模式，默认高精度
        //LocationMode.Hight_Accuracy：高精度；
        //LocationMode. Battery_Saving：低功耗；
        //LocationMode. Device_Sensors：仅使用设备；
        option.setCoorType("bd09ll");
        //可选，设置返回经纬度坐标类型，默认gcj02
        //gcj02：国测局坐标；
        //bd09ll：百度经纬度坐标；
        //bd09：百度墨卡托坐标；
        //海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标
        option.setOpenGps(true); // 打开gps
        option.setLocationNotify(true);
        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false
        option.setScanSpan(1000);
        option.setIsNeedAddress(true);
        mLocClient.setLocOption(option);
        mLocClient.start();
        BitmapUtil.init();

        bleVideo();

        chooseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//todo
                startActivity(new Intent(MainActivity.this, ChooseMapFunctionActivity.class));
            }
        });

        btn_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, VideoPlayActivity.class));
            }
        });
    }

    @Override
    protected void loadData() {

    }

    private void bleVideo() {
        // Activity:
        AndPermission.with(this)
                .requestCode(100)
                .permission(Manifest.permission.ACCESS_COARSE_LOCATION)
                .requestCode(200)
                .callback(listener)
                .start();

        //蓝牙相关配置修改
        ViseBle.config()
                .setScanTimeout(-1)//扫描超时时间，这里设置为永久扫描
                .setConnectTimeout(10 * 1000)//连接超时时间
                .setOperateTimeout(5 * 1000)//设置数据操作超时时间
                .setConnectRetryCount(3)//设置连接失败重试次数
                .setConnectRetryInterval(1000)//设置连接失败重试间隔时间
                .setOperateRetryCount(3)//设置数据操作失败重试次数
                .setOperateRetryInterval(1000)//设置数据操作失败重试间隔时间
                .setMaxConnectCount(3);//设置最大连接设备数量
        //蓝牙信息初始化，全局唯一，必须在应用初始化时调用
        ViseBle.getInstance().init(this);
//        checkBluetoothPermission();
        //网络视频
        String videoUrl2 = BLEVideoUtils.videoUrl;

        Uri uri = Uri.parse(videoUrl2);

        mMapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //停止视频
                videoView.setVisibility(View.INVISIBLE);
                videoView.stopPlayback();
            }
        });
        //设置视频控制器
        videoView.setMediaController(new MediaController(this));

        //播放完成回调
        videoView.setOnCompletionListener(new MyPlayerOnCompletionListener());

        //设置视频路径
        videoView.setVideoURI(uri);
    }

    private PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {
            if (requestCode == 200) {
                // TODO ...
                Log.d("device--->", "成功");
                enableBluetooth();
            }
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // 权限申请失败回调。
            if (requestCode == 200) {
                // TODO ...
                Log.d("device--->", "失败");
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Log.d("device--->", "成功");
                ViseBle.getInstance().startScan(periodScanCallback);

            }
        } else if (resultCode == RESULT_CANCELED) {
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 扫描回调
     */
    private com.vise.baseble.callback.scan.ScanCallback periodScanCallback = new com.vise.baseble.callback.scan.ScanCallback(new IScanCallback() {
        @Override
        public void onDeviceFound(final BluetoothLeDevice bluetoothLeDevice) {

            Log.d("device--->", "设备" + bluetoothLeDevice.getAddress());
            if (bluetoothLeDevice.getName() != null) {
                if (bluetoothLeDevice.getName().equals("Radioland iBeacon")) { //假如扫描到了就代表在检测范围 TODO 要随时改
                    Log.d("device--->", "发现了指定的设备" + bluetoothLeDevice.getAddress());

                    ViseBle.getInstance().connect(bluetoothLeDevice, new IConnectCallback() {
                        @Override
                        public void onConnectSuccess(DeviceMirror deviceMirror) {
                            //开始播放视频
                            Log.d("device--->", "设备连接成功" + bluetoothLeDevice.getAddress());
                            Message message = new Message();
                            message.what = 2;
                            mHandler.sendMessage(message);
                        }

                        @Override
                        public void onConnectFailure(BleException exception) {
                            Log.d("device--->", "设备连接失败" + bluetoothLeDevice.getAddress());
                            Message message = new Message();
                            message.what = 1;
                            mHandler.sendMessage(message);
                        }

                        @Override
                        public void onDisconnect(boolean isActive) {
                            Log.d("device--->", "设备断开连接" + bluetoothLeDevice.getAddress());
                            Message message = new Message();
                            message.what = 1;
                            mHandler.sendMessage(message);
                        }
                    });

                }
            }
        }

        @Override
        public void onScanFinish(BluetoothLeDeviceStore bluetoothLeDeviceStore) {

        }

        @Override
        public void onScanTimeout() {

        }
    });

    /**
     * 检查蓝牙权限
     */
//    private void checkBluetoothPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            //校验是否已具有模糊定位权限
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                PermissionManager.instance().with(this).request(new OnPermissionCallback() {
//                    @Override
//                    public void onRequestAllow(String permissionName) {
//                        enableBluetooth();
//                    }
//
//                    @Override
//                    public void onRequestRefuse(String permissionName) {
//                        finish();
//                    }
//
//                    @Override
//                    public void onRequestNoAsk(String permissionName) {
//                        finish();
//                    }
//                }, Manifest.permission.ACCESS_COARSE_LOCATION);
//            } else {
//                enableBluetooth();
//            }
//        } else {
//            enableBluetooth();
//        }
//    }

    class MyPlayerOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            Toast.makeText(MainActivity.this, R.string.finish, Toast.LENGTH_SHORT).show();
        }
    }

    private void enableBluetooth() {
        if (!BleUtil.isBleEnable(this)) {
            BleUtil.enableBluetooth(this, 1);
        } else {
            ViseBle.getInstance().startScan(periodScanCallback);
            Log.d("device--->", "开始扫描");
        }
    }

    //---------------------------------------------------------------------------------------------
    //创建选项菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        cityItem = menu.findItem(R.id.current_city);
        cityItem.setTitle(CommonUtil.getSpStr(getApplicationContext(), "currentCity", "北京市"));
        return super.onCreateOptionsMenu(menu);
    }

    //处理actionbar点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.current_city:
                ChooseCityUtil cityUtil = new ChooseCityUtil();
                String[] oldCityArray = {actionbarProvince, actionbarCity, actionbarDistrict};
                cityUtil.createDialog(this, oldCityArray, new ChooseCityInterface() {
                    @Override
                    public void sure(String[] newCityArray) {
                        //oldCityArray为传入的默认值 newCityArray为返回的结果
                        //cityItem.setTitle(newCityArray[0] + "-" + newCityArray[1] + "-" + newCityArray[2]);
                        if (NetUtil.isNetworkAvailable(getApplicationContext())) {
                            cityItem.setTitle(newCityArray[1] + "市");
                            geoCoder.geocode(new GeoCodeOption().city(newCityArray[1]).address(newCityArray[2]));
                            actionbarProvince = newCityArray[0];
                            actionbarCity = newCityArray[1];
                            actionbarDistrict = newCityArray[2];
                            Toast.makeText(getApplicationContext(), "切换到" + newCityArray[0] + "省" + newCityArray[1] + "市" + newCityArray[2] + "县", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.no_wifi, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            locData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(mCurrentLat)
                    .longitude(mCurrentLon).build();
            mBaiduMap.setMyLocationData(locData);
        }
        lastX = x;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccracy = location.getRadius();
            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            mCurrentCity = subCityProvince(location.getCity());
            mCurrentProvince = subCityProvince(location.getProvince());
            mCurrentDistrict = subCityProvince(location.getDistrict());
            if (isFirstLoc && location.getCity() != null) {
                isFirstLoc = false;
                actionbarCity = subCityProvince(location.getCity());
                actionbarProvince = (location.getProvince());
                actionbarDistrict = subCityProvince(location.getDistrict());
                cityItem.setTitle(location.getCity());
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
            String currentCity = location.getCity();
            CommonUtil.setSpStr(getApplicationContext(), "currentCity", currentCity);
            CommonUtil.setSpStr(getApplicationContext(), "currentLatitude", mCurrentLat + "");
            CommonUtil.setSpStr(getApplicationContext(), "currentLongitude", mCurrentLon + "");
        }
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
        //为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 适配android M，检查权限
        List<String> permissions = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isNeedRequestPermissions(permissions)) {
            requestPermissions(permissions.toArray(new String[permissions.size()]), 0);
        }
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
    protected void onStop() {
        //取消注册传感器监听
        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        ActivityManager.getInstance().remove(this);
        Log.e("remove", ActivityManager.getInstance().size() + "");

        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        geoCoder.destroy();
        mMapView = null;
        super.onDestroy();
        // 取消监听 SDK 广播
        unregisterReceiver(mReceiver);
        CommonUtil.saveCurrentLocation(demoApp);
        if (demoApp.trackConf.contains("is_trace_started")
                && demoApp.trackConf.getBoolean("is_trace_started", true)) {
            // 退出app停止轨迹服务时，不再接收回调，将OnTraceListener置空
            demoApp.mClient.setOnTraceListener(null);
            demoApp.mClient.stopTrace(demoApp.mTrace, null);
        } else {
            demoApp.mClient.clear();
        }
        demoApp.isTraceStarted = false;
        demoApp.isGatherStarted = false;
        SharedPreferences.Editor editor = demoApp.trackConf.edit();
        editor.remove("is_trace_started");
        editor.remove("is_gather_started");

        editor.apply();
        BitmapUtil.clear();

        //ble
        ViseBle.getInstance().stopScan(periodScanCallback);
        ViseBle.getInstance().disconnect();
        ViseBle.getInstance().clear();
    }

    private String subCityProvince(String str) {
        if (str == null) {
            return "";
        }
        return str.substring(0, str.length() - 1);
    }
}