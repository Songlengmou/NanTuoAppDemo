package com.example.admin.nantuoappdemo.activity.footprint;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.api.entity.OnEntityListener;
import com.baidu.trace.api.track.LatestPoint;
import com.baidu.trace.api.track.LatestPointResponse;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.model.LocationMode;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.PushMessage;
import com.baidu.trace.model.StatusCodes;
import com.baidu.trace.model.TraceLocation;
import com.example.admin.nantuoappdemo.R;
import com.example.admin.nantuoappdemo.activity.ChooseMapFunctionActivity;
import com.example.admin.nantuoappdemo.activity.login.LoginActivity;
import com.example.admin.nantuoappdemo.bean.CurrentLocation;
import com.example.admin.nantuoappdemo.othermanager.MyApplication;
import com.example.admin.nantuoappdemo.othermanager.RxActivity;
import com.example.admin.nantuoappdemo.receiver.TrackReceiver;
import com.example.admin.nantuoappdemo.utils.CommonUtil;
import com.example.admin.nantuoappdemo.utils.Constants;
import com.example.admin.nantuoappdemo.utils.MapUtil;
import com.example.admin.nantuoappdemo.utils.ViewUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 轨迹实时追踪
 */
public class TracingActivity extends RxActivity implements View.OnClickListener, SensorEventListener {
    private MyApplication trackApp = null;

    private ViewUtil viewUtil = null;

    @Bind(R.id.btn_trace)
    Button traceBtn = null;

    @Bind(R.id.btn_gather)
    Button gatherBtn = null;

    private PowerManager powerManager = null;

    private PowerManager.WakeLock wakeLock = null;

    private TrackReceiver trackReceiver = null;

    private SensorManager mSensorManager;

    private Double lastX = 0.0;
    private int mCurrentDirection = 0;

    /**
     * 地图工具
     */
    private MapUtil mapUtil = null;

    /**
     * 轨迹服务监听器
     */
    private OnTraceListener traceListener = null;

    /**
     * 轨迹监听器(用于接收纠偏后实时位置回调)
     */
    private OnTrackListener trackListener = null;

    /**
     * Entity监听器(用于接收实时定位回调)
     */
    private OnEntityListener entityListener = null;

    /**
     * 实时定位任务
     */
    private RealTimeHandler realTimeHandler = new RealTimeHandler();

    private RealTimeLocRunnable realTimeLocRunnable = null;

    /**
     * 打包周期
     */
    public int packInterval = Constants.DEFAULT_PACK_INTERVAL;

    /**
     * 轨迹点集合
     */
    private List<LatLng> trackPoints;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_tracing;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        actionBar();

        initListener();
        trackApp = (MyApplication) getApplicationContext();
        viewUtil = new ViewUtil();
        mapUtil = MapUtil.getInstance();
        mapUtil.init((MapView) findViewById(R.id.tracing_mapView));
        mapUtil.setCenter(mCurrentDirection);//设置地图中心点
        startRealTimeLoc(Constants.LOC_INTERVAL);//实时定位
        powerManager = (PowerManager) trackApp.getSystemService(Context.POWER_SERVICE);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);// 获取传感器管理服务

        traceBtn.setOnClickListener(this);
        gatherBtn.setOnClickListener(this);
        setTraceBtnStyle();
        setGatherBtnStyle();

        trackPoints = new ArrayList<>();
    }

    @Override
    protected void loadData() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.trace_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.trace_setting:
                ViewUtil.startActivityForResult(TracingActivity.this, TracingOptionsActivity.class, Constants
                        .REQUEST_CODE);
                break;
//            case R.id.history:
//                ViewUtil.startActivityForResult(TracingActivity.this, TrackQueryActivity.class, Constants
//                        .REQUEST_QUERY_CODE);
////                ViewUtil.startActivity(TracingActivity.this, TrackQueryActivity.class);
////                finish();
//                break;
            case android.R.id.home:
                startActivity(new Intent(TracingActivity.this, TraceMainActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        initListener();
        trackApp = (MyApplication) getApplicationContext();
        viewUtil = new ViewUtil();
        mapUtil = MapUtil.getInstance();
        mapUtil.init((MapView) findViewById(R.id.tracing_mapView));
        mapUtil.setCenter(mCurrentDirection);//设置地图中心点
        startRealTimeLoc(Constants.LOC_INTERVAL);//实时定位
        powerManager = (PowerManager) trackApp.getSystemService(Context.POWER_SERVICE);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);// 获取传感器管理服务

        traceBtn.setOnClickListener(this);
        gatherBtn.setOnClickListener(this);
        setTraceBtnStyle();
        setGatherBtnStyle();

        trackPoints = new ArrayList<>();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //每次方向改变，重新给地图设置定位数据，用上一次的经纬度
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {// 方向改变大于1度才设置，以免地图上的箭头转动过于频繁
            mCurrentDirection = (int) x;
            if (!CommonUtil.isZeroPoint(CurrentLocation.latitude, CurrentLocation.longitude)) {
                mapUtil.updateMapLocation(new LatLng(CurrentLocation.latitude, CurrentLocation.longitude), (float) mCurrentDirection);
            }
        }
        lastX = x;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_trace:
                if (trackApp.isTraceStarted) {
                    trackApp.mClient.stopTrace(trackApp.mTrace, traceListener);//停止服务
                    stopRealTimeLoc();
                } else {
                    trackApp.mClient.startTrace(trackApp.mTrace, traceListener);//开始服务
                    if (Constants.DEFAULT_PACK_INTERVAL != packInterval) {//设置间隔
                        stopRealTimeLoc();
                        startRealTimeLoc(packInterval);
                    }
                }
                break;
            case R.id.btn_gather:
                if (trackApp.isGatherStarted) {
                    trackApp.mClient.stopGather(traceListener);
                } else {
                    trackApp.mClient.setInterval(Constants.DEFAULT_GATHER_INTERVAL, packInterval);
                    trackApp.mClient.startGather(traceListener);//开启采集
                }
                break;
            default:
                break;
        }

    }

    /**
     * 设置服务按钮样式
     */
    private void setTraceBtnStyle() {
        boolean isTraceStarted = trackApp.trackConf.getBoolean("is_trace_started", false);
        if (isTraceStarted) {
            traceBtn.setText(R.string.stop_trace);
            traceBtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color
                    .white, null));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                traceBtn.setBackground(ResourcesCompat.getDrawable(getResources(),
                        R.mipmap.bg_btn_sure, null));
            } else {
                traceBtn.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),
                        R.mipmap.bg_btn_sure, null));
            }
        } else {
            traceBtn.setText(R.string.start_trace);
            traceBtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.layout_title, null));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                traceBtn.setBackground(ResourcesCompat.getDrawable(getResources(),
                        R.mipmap.bg_btn_cancel, null));
            } else {
                traceBtn.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),
                        R.mipmap.bg_btn_cancel, null));
            }
        }
    }

    /**
     * 设置采集按钮样式
     */
    private void setGatherBtnStyle() {
        boolean isGatherStarted = trackApp.trackConf.getBoolean("is_gather_started", false);
        if (isGatherStarted) {
            gatherBtn.setText(R.string.stop_gather);
            gatherBtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                gatherBtn.setBackground(ResourcesCompat.getDrawable(getResources(),
                        R.mipmap.bg_btn_sure, null));
            } else {
                gatherBtn.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),
                        R.mipmap.bg_btn_sure, null));
            }
        } else {
            gatherBtn.setText(R.string.start_gather);
            gatherBtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.layout_title, null));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                gatherBtn.setBackground(ResourcesCompat.getDrawable(getResources(),
                        R.mipmap.bg_btn_cancel, null));
            } else {
                gatherBtn.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),
                        R.mipmap.bg_btn_cancel, null));
            }
        }
    }

    /**
     * 实时定位任务
     *
     * @author baidu
     */
    class RealTimeLocRunnable implements Runnable {

        private int interval = 0;

        public RealTimeLocRunnable(int interval) {
            this.interval = interval;
        }

        @Override
        public void run() {
            trackApp.getCurrentLocation(entityListener, trackListener);
            realTimeHandler.postDelayed(this, interval * 1000);
        }
    }

    public void startRealTimeLoc(int interval) {
        realTimeLocRunnable = new RealTimeLocRunnable(interval);
        realTimeHandler.post(realTimeLocRunnable);
    }

    public void stopRealTimeLoc() {
        if (null != realTimeHandler && null != realTimeLocRunnable) {
            realTimeHandler.removeCallbacks(realTimeLocRunnable);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_QUERY_CODE) {
//            mapUtil.getInstance().onResume();
            return;
        }
        if (null == data) {
            return;
        }

        if (data.hasExtra("locationMode")) {
            LocationMode locationMode = LocationMode.valueOf(data.getStringExtra("locationMode"));
            trackApp.mClient.setLocationMode(locationMode);//定位模式
        }
        trackApp.mTrace.setNeedObjectStorage(false);

        if (data.hasExtra("gatherInterval") && data.hasExtra("packInterval")) {
            int gatherInterval = data.getIntExtra("gatherInterval", Constants.DEFAULT_GATHER_INTERVAL);
            int packInterval = data.getIntExtra("packInterval", Constants.DEFAULT_PACK_INTERVAL);
            TracingActivity.this.packInterval = packInterval;
            trackApp.mClient.setInterval(gatherInterval, packInterval);//设置频率
        }
    }

    private void initListener() {

        trackListener = new OnTrackListener() {

            @Override
            public void onLatestPointCallback(LatestPointResponse response) {
                Log.d(Constants.TAG, "onLatestPointCallback status:" + response.getStatus());
                //经过服务端纠偏后的最新的一个位置点，回调
                if (StatusCodes.SUCCESS != response.getStatus()) {
                    return;
                }

                LatestPoint point = response.getLatestPoint();
                if (null == point || CommonUtil.isZeroPoint(point.getLocation().getLatitude(), point.getLocation()
                        .getLongitude())) {
                    return;
                }
                LatLng currentLatLng = mapUtil.convertTrace2Map(point.getLocation());
                Log.d(Constants.TAG, "onLatestPointCallback currentLatLng:" + currentLatLng);
                if (null == currentLatLng) {
                    return;
                }

                //当前经纬度
                CurrentLocation.locTime = point.getLocTime();
                CurrentLocation.latitude = currentLatLng.latitude;
                CurrentLocation.longitude = currentLatLng.longitude;
                Log.d(Constants.TAG, "onLatestPointCallback trackPoints:" + trackPoints);
                if (trackPoints == null) {
                    return;
                }
                trackPoints.add(currentLatLng);

                mapUtil.drawHistoryTrack(trackPoints, false, mCurrentDirection);//时时动态的画出运动轨迹

            }
        };

        entityListener = new OnEntityListener() {

            @Override
            public void onReceiveLocation(TraceLocation location) {
                //本地LBSTraceClient客户端获取的位置

                if (StatusCodes.SUCCESS != location.getStatus() || CommonUtil.isZeroPoint(location.getLatitude(),
                        location.getLongitude())) {
                    return;
                }
                LatLng currentLatLng = mapUtil.convertTraceLocation2Map(location);
                if (null == currentLatLng) {
                    return;
                }
                CurrentLocation.locTime = CommonUtil.toTimeStamp(location.getTime());
                CurrentLocation.latitude = currentLatLng.latitude;
                CurrentLocation.longitude = currentLatLng.longitude;

                if (null != mapUtil) {
                    mapUtil.updateMapLocation(currentLatLng, mCurrentDirection);//显示当前位置
                    mapUtil.animateMapStatus(currentLatLng);//缩放
                }

            }

        };

        traceListener = new OnTraceListener() {
            /**
             * 绑定服务回调接口
             * @param errorNo  状态码
             * @param message 消息
             *                <p>
             *                <pre>0：成功 </pre>
             *                <pre>1：失败</pre>
             */
            @Override
            public void onBindServiceCallback(int errorNo, String message) {
                viewUtil.showToast(TracingActivity.this,
                        String.format("onBindServiceCallback, errorNo:%d, message:%s ", errorNo, message));
            }

            /**
             * 开启服务回调接口
             * @param errorNo 状态码
             * @param message 消息
             *                <p>
             *                <pre>0：成功 </pre>
             *                <pre>10000：请求发送失败</pre>
             *                <pre>10001：服务开启失败</pre>
             *                <pre>10002：参数错误</pre>
             *                <pre>10003：网络连接失败</pre>
             *                <pre>10004：网络未开启</pre>
             *                <pre>10005：服务正在开启</pre>
             *                <pre>10006：服务已开启</pre>
             */
            @Override
            public void onStartTraceCallback(int errorNo, String message) {
                if (StatusCodes.SUCCESS == errorNo || StatusCodes.START_TRACE_NETWORK_CONNECT_FAILED <= errorNo) {
                    trackApp.isTraceStarted = true;
                    SharedPreferences.Editor editor = trackApp.trackConf.edit();
                    editor.putBoolean("is_trace_started", true);
                    editor.apply();
                    setTraceBtnStyle();
                    registerReceiver();
                }
                viewUtil.showToast(TracingActivity.this,
                        String.format("onStartTraceCallback, errorNo:%d, message:%s ", errorNo, message));
            }

            /**
             * 停止服务回调接口
             * @param errorNo 状态码
             * @param message 消息
             *                <p>
             *                <pre>0：成功</pre>
             *                <pre>11000：请求发送失败</pre>
             *                <pre>11001：服务停止失败</pre>
             *                <pre>11002：服务未开启</pre>
             *                <pre>11003：服务正在停止</pre>
             */
            @Override
            public void onStopTraceCallback(int errorNo, String message) {
                if (StatusCodes.SUCCESS == errorNo || StatusCodes.CACHE_TRACK_NOT_UPLOAD == errorNo) {
                    trackApp.isTraceStarted = false;
                    trackApp.isGatherStarted = false;
                    // 停止成功后，直接移除is_trace_started记录（便于区分用户没有停止服务，直接杀死进程的情况）
                    SharedPreferences.Editor editor = trackApp.trackConf.edit();
                    editor.remove("is_trace_started");
                    editor.remove("is_gather_started");
                    editor.apply();
                    setTraceBtnStyle();
                    setGatherBtnStyle();
                    unregisterPowerReceiver();
                }
                viewUtil.showToast(TracingActivity.this,
                        String.format("onStopTraceCallback, errorNo:%d, message:%s ", errorNo, message));
            }

            /**
             * 开启采集回调接口
             * @param errorNo 状态码
             * @param message 消息
             *                <p>
             *                <pre>0：成功</pre>
             *                <pre>12000：请求发送失败</pre>
             *                <pre>12001：采集开启失败</pre>
             *                <pre>12002：服务未开启</pre>
             */
            @Override
            public void onStartGatherCallback(int errorNo, String message) {
                if (StatusCodes.SUCCESS == errorNo || StatusCodes.GATHER_STARTED == errorNo) {
                    trackApp.isGatherStarted = true;
                    SharedPreferences.Editor editor = trackApp.trackConf.edit();
                    editor.putBoolean("is_gather_started", true);
                    editor.apply();
                    setGatherBtnStyle();
                }
                viewUtil.showToast(TracingActivity.this,
                        String.format("onStartGatherCallback, errorNo:%d, message:%s ", errorNo, message));
            }

            /**
             * 停止采集回调接口
             * @param errorNo 状态码
             * @param message 消息
             *                <p>
             *                <pre>0：成功</pre>
             *                <pre>13000：请求发送失败</pre>
             *                <pre>13001：采集停止失败</pre>
             *                <pre>13002：服务未开启</pre>
             */
            @Override
            public void onStopGatherCallback(int errorNo, String message) {
                if (StatusCodes.SUCCESS == errorNo || StatusCodes.GATHER_STOPPED == errorNo) {
                    trackApp.isGatherStarted = false;
                    SharedPreferences.Editor editor = trackApp.trackConf.edit();
                    editor.remove("is_gather_started");
                    editor.apply();
                    setGatherBtnStyle();
                    if (trackPoints.size() >= 1) {
                        mapUtil.drawEndPoint(trackPoints.get(trackPoints.size() - 1));
                    }


                }
                viewUtil.showToast(TracingActivity.this,
                        String.format("onStopGatherCallback, errorNo:%d, message:%s ", errorNo, message));
            }

            /**
             * 推送消息回调接口
             *
             * @param messageType 状态码
             * @param pushMessage 消息
             *                  <p>
             *                  <pre>0x01：配置下发</pre>
             *                  <pre>0x02：语音消息</pre>
             *                  <pre>0x03：服务端围栏报警消息</pre>
             *                  <pre>0x04：本地围栏报警消息</pre>
             *                  <pre>0x05~0x40：系统预留</pre>
             *                  <pre>0x41~0xFF：开发者自定义</pre>
             */
            @Override
            public void onPushCallback(byte messageType, PushMessage pushMessage) {
//                if (messageType < 0x03 || messageType > 0x04) {
//                    viewUtil.showToast(TracingActivity.this, pushMessage.getMessage());
//                    return;
//                }
//                FenceAlarmPushInfo alarmPushInfo = pushMessage.getFenceAlarmPushInfo();
//                if (null == alarmPushInfo) {
//                    viewUtil.showToast(TracingActivity.this,
//                            String.format("onPushCallback, messageType:%d, messageContent:%s ", messageType,
//                                    pushMessage));
//                    return;
//                }
//                StringBuffer alarmInfo = new StringBuffer();
//                alarmInfo.append("您于")
//                        .append(CommonUtil.getHMS(alarmPushInfo.getCurrentPoint().getLocTime() * 1000))
//                        .append(alarmPushInfo.getMonitoredAction() == MonitoredAction.enter ? "进入" : "离开")
//                        .append(messageType == 0x03 ? "云端" : "本地")
//                        .append("围栏：").append(alarmPushInfo.getFenceName());

//                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
//                    Notification notification = new Notification.Builder(trackApp)
//                            .setContentTitle(getResources().getString(R.string.alarm_push_title))
//                            .setContentText(alarmInfo.toString())
//                            .setSmallIcon(R.mipmap.icon_app)
//                            .setWhen(System.currentTimeMillis()).build();
//                    notificationManager.notify(notifyId++, notification);
//                }
            }

            @Override
            public void onInitBOSCallback(int errorNo, String message) {
                viewUtil.showToast(TracingActivity.this,
                        String.format("onInitBOSCallback, errorNo:%d, message:%s ", errorNo, message));
            }
        };

    }

    static class RealTimeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    /**
     * 注册广播（电源锁、GPS状态）
     */
    private void registerReceiver() {
        if (trackApp.isRegisterReceiver) {
            return;
        }

        if (null == wakeLock) {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "track upload");
        }
        if (null == trackReceiver) {
            trackReceiver = new TrackReceiver(wakeLock);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(StatusCodes.GPS_STATUS_ACTION);
        trackApp.registerReceiver(trackReceiver, filter);
        trackApp.isRegisterReceiver = true;

    }

    private void unregisterPowerReceiver() {
        if (!trackApp.isRegisterReceiver) {
            return;
        }
        if (null != trackReceiver) {
            trackApp.unregisterReceiver(trackReceiver);
        }
        trackApp.isRegisterReceiver = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        startRealTimeLoc(packInterval);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapUtil.onResume();

        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_UI);

        // 在Android 6.0及以上系统，若定制手机使用到doze模式，请求将应用添加到白名单。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = trackApp.getPackageName();
            boolean isIgnoring = powerManager.isIgnoringBatteryOptimizations(packageName);
            if (!isIgnoring) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                try {
                    startActivity(intent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapUtil.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopRealTimeLoc();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapUtil.clear();
        stopRealTimeLoc();
        trackPoints.clear();
        trackPoints = null;
        mapUtil.clear();
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
}
