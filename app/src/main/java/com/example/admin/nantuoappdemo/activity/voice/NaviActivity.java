package com.example.admin.nantuoappdemo.activity.voice;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.baidu.navisdk.adapter.BNCommonSettingParam;
import com.baidu.navisdk.adapter.BNOuterLogUtil;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.example.admin.nantuoappdemo.R;
import com.example.admin.nantuoappdemo.activity.ChooseMapFunctionActivity;
import com.example.admin.nantuoappdemo.activity.login.LoginActivity;
import com.example.admin.nantuoappdemo.othermanager.RxActivity;
import com.example.admin.nantuoappdemo.utils.CommonUtil;
import com.example.admin.nantuoappdemo.utils.Constants;
import com.example.admin.nantuoappdemo.utils.NetUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 语音导航信息
 */
public class NaviActivity extends RxActivity {
    public static final String ROUTE_PLAN_NODE = "routePlanNode";
    private String mSDCardPath = null;
    private static final String APP_FOLDER_NAME = "DaoHang";
    @Bind(R.id.startNavi)
    Button startNavi;
    private final static int authBaseRequestCode = 1;
    private final static String authBaseArr[] =
            {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION};
    private AutoCompleteTextView et_st_navi, et_en_navi;
    private GeoCoder geoCoderEn, geoCoderSt;
    private LatLng enLatlng, stLatlng;
    private SuggestionSearch mSuggestionSearch = null;
    private List<String> suggestSt;
    private ArrayAdapter<String> sugAdapter = null;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_navi;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        actionBar();

        geoCoderSt = GeoCoder.newInstance();
        geoCoderEn = GeoCoder.newInstance();
        geoCoderSt.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有检索到结果
                    Toast.makeText(getApplicationContext(), "没有搜索到结果", Toast.LENGTH_SHORT).show();
                    return;
                }
                stLatlng = result.getLocation();
                geoCoderEn.geocode(new GeoCodeOption().city(CommonUtil.getSpStr(getApplicationContext(), "currentCity", "")).address(et_en_navi.getText().toString().trim()));
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

            }
        });
        geoCoderEn.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有检索到结果
                    Toast.makeText(getApplicationContext(), "没有搜索到结果", Toast.LENGTH_SHORT).show();
                    return;
                }
                enLatlng = result.getLocation();
                if (BaiduNaviManager.isNaviInited()) {
                    routeplanToNavi(BNRoutePlanNode.CoordinateType.BD09LL, enLatlng, stLatlng);
                }
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

            }
        });
        et_en_navi = findViewById(R.id.et_en_navi);
        et_st_navi = findViewById(R.id.et_st_navi);
        startNavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetUtil.isNetworkAvailable(getApplicationContext())) {
                    if (TextUtils.isEmpty(et_en_navi.getText().toString().trim())) {
                        Toast.makeText(NaviActivity.this, "终点不能为空", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(et_st_navi.getText().toString().trim())) {
                        stLatlng = null;
                        geoCoderEn.geocode(new GeoCodeOption().city(CommonUtil.getSpStr(getApplicationContext(), "currentCity", "")).address(et_en_navi.getText().toString().trim()));
                    } else {
                        geoCoderSt.geocode(new GeoCodeOption().city(CommonUtil.getSpStr(getApplicationContext(), "currentCity", "")).address(et_st_navi.getText().toString().trim()));
                    }
                } else {
                    Toast.makeText(NaviActivity.this, "当前无网络", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // 初始化建议搜索模块，注册建议搜索事件监听
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(new OnGetSuggestionResultListener() {
            @Override
            public void onGetSuggestionResult(SuggestionResult res) {
                if (res == null || res.getAllSuggestions() == null) {
                    return;
                }
                suggestSt = new ArrayList<String>();
                for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
                    if (info.key != null) {
                        suggestSt.add(info.key);
                    }
                }
                sugAdapter = new ArrayAdapter<String>(NaviActivity.this, android.R.layout.simple_dropdown_item_1line, suggestSt);
                et_st_navi.setAdapter(sugAdapter);
                et_en_navi.setAdapter(sugAdapter);
                sugAdapter.notifyDataSetChanged();
            }
        });
        et_st_navi.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line));
        et_st_navi.setThreshold(1);
        et_st_navi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() <= 0) {
                    return;
                }

                /**
                 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                 */
                String currentCity = CommonUtil.getSpStr(getApplicationContext(), "currentCity", "");
                mSuggestionSearch
                        .requestSuggestion((new SuggestionSearchOption())
                                .keyword(charSequence.toString()).city(currentCity));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        et_en_navi.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line));
        et_en_navi.setThreshold(1);
        et_en_navi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() <= 0) {
                    return;
                }

                /**
                 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                 */
                String currentCity = CommonUtil.getSpStr(getApplicationContext(), "currentCity", "");
                mSuggestionSearch
                        .requestSuggestion((new SuggestionSearchOption())
                                .keyword(charSequence.toString()).city(currentCity));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        BNOuterLogUtil.setLogSwitcher(true);
        if (initDirs()) {
            initNavi();
        }
    }

    @Override
    protected void loadData() {

    }

    private void routeplanToNavi(BNRoutePlanNode.CoordinateType coType, LatLng enLatlng, LatLng stLatlng) {
        BNRoutePlanNode sNode = null;
        BNRoutePlanNode eNode = null;
        if (stLatlng == null) {
            sNode = new BNRoutePlanNode(Double.parseDouble(CommonUtil.getSpStr(getApplicationContext(), "currentLongitude", "")), Double.parseDouble(CommonUtil.getSpStr(getApplicationContext(), "currentLatitude", "")), null, null, coType);
        } else {
            sNode = new BNRoutePlanNode(stLatlng.longitude, stLatlng.latitude, null, null, coType);
        }
        eNode = new BNRoutePlanNode(enLatlng.longitude, enLatlng.latitude, null, null, coType);
        if (sNode != null && eNode != null) {
            List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
            list.add(sNode);
            list.add(eNode);
            BaiduNaviManager.getInstance().launchNavigator(this, list, 1, true, new DemoRoutePlanListener(sNode));
        }
    }

    /**
     * 内部TTS播报状态回传handler
     */
    private Handler ttsHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.what;
            switch (type) {
                case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {
                    break;
                }
                case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {
                    break;
                }
                default:
                    break;
            }
        }
    };
    /**
     * 内部TTS播报状态回调接口
     */
    private BaiduNaviManager.TTSPlayStateListener ttsPlayStateListener = new BaiduNaviManager.TTSPlayStateListener() {

        @Override
        public void playEnd() {
        }

        @Override
        public void playStart() {
        }
    };

    private boolean hasBasePhoneAuth() {
        // TODO Auto-generated method stub

        PackageManager pm = this.getPackageManager();
        for (String auth : authBaseArr) {
            if (pm.checkPermission(auth, this.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    String authinfo = null;

    private void initNavi() {

        BNOuterTTSPlayerCallback ttsCallback = null;
        // 申请权限
        if (android.os.Build.VERSION.SDK_INT >= 23) {

            if (!hasBasePhoneAuth()) {

                this.requestPermissions(authBaseArr, authBaseRequestCode);
                return;

            }
        }
        BaiduNaviManager.getInstance().init(this, mSDCardPath, APP_FOLDER_NAME, new BaiduNaviManager.NaviInitListener() {
            @Override
            public void onAuthResult(int status, String msg) {
                if (0 == status) {
                    authinfo = "key校验成功!";
                } else {
                    authinfo = "key校验失败, " + msg;
                }
                NaviActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(NaviActivity.this, authinfo, Toast.LENGTH_LONG).show();
                    }
                });
            }

            public void initSuccess() {
                Toast.makeText(NaviActivity.this, "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
                initSetting();
            }

            public void initStart() {
            }

            public void initFailed() {
                Toast.makeText(NaviActivity.this, "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
            }


        }, null, ttsHandler, ttsPlayStateListener);

    }

    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    private boolean initDirs() {
        mSDCardPath = getSdcardDir();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private void initSetting() {
        // 设置是否双屏显示
        BNaviSettingManager.setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
        // 设置导航播报模式
        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
        // 是否开启路况
        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);


        BNaviSettingManager.setIsAutoQuitWhenArrived(true);
        Bundle bundle = new Bundle();
        // 必须设置APPID，否则会静音
        bundle.putString(BNCommonSettingParam.TTS_APP_ID, Constants.TTS_APP_ID);
        BNaviSettingManager.setNaviSdkParam(bundle);
    }

    public class DemoRoutePlanListener implements BaiduNaviManager.RoutePlanListener {

        private BNRoutePlanNode mBNRoutePlanNode = null;

        public DemoRoutePlanListener(BNRoutePlanNode node) {
            mBNRoutePlanNode = node;
        }

        @Override
        public void onJumpToNavigator() {
            /*
             * 设置途径点以及resetEndNode会回调该接口
			 */
            Intent intent = new Intent(NaviActivity.this, NaviGuideActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(ROUTE_PLAN_NODE, mBNRoutePlanNode);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        @Override
        public void onRoutePlanFailed() {
            // TODO Auto-generated method stub
            Toast.makeText(NaviActivity.this, "算路失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        geoCoderEn.destroy();
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
                startActivity(new Intent(NaviActivity.this, ChooseMapFunctionActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
