package com.example.admin.nantuoappdemo.activity.arnavigation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.example.admin.nantuoappdemo.R;
import com.example.admin.nantuoappdemo.activity.ChooseMapFunctionActivity;
import com.example.admin.nantuoappdemo.activity.login.LoginActivity;
import com.example.admin.nantuoappdemo.activity.social.SocialActivity;
import com.example.admin.nantuoappdemo.othermanager.RxActivity;
import com.example.admin.nantuoappdemo.utils.CommonUtil;
import com.example.admin.nantuoappdemo.utils.NetUtil;

import map.overlayutil.PoiOverlay;
import butterknife.Bind;

/**
 * Created by admin on 2017/12/21.
 * <p>
 * AR全景界面
 */

public class ARNavigationActivity extends RxActivity implements OnGetPoiSearchResultListener {
    @Bind(R.id.bmapViews)
    MapView mapView;
    private BaiduMap mBaiduMap;
    private PoiSearch mPoiSearch = null;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_ar_navigation;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        actionBar();

        mBaiduMap = mapView.getMap();
        // 初始化搜索模块，注册搜索事件监听
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        String latitude = CommonUtil.getSpStr(getApplicationContext(), "currentLatitude", null);
        String longitude = CommonUtil.getSpStr(getApplicationContext(), "currentLongitude", null);
        LatLng latlng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latlng);
        mBaiduMap.animateMapStatus(msu);
    }

    @Override
    protected void loadData() {

    }

    /**
     * 响应城市内搜索按钮点击事件
     */
    public void searchButtonProcess() {
        if (NetUtil.isNetworkAvailable(getApplicationContext())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = View.inflate(this, R.layout.dialog_select_indoor, null);
            final EditText city = view.findViewById(R.id.et_city_indorr);
            final EditText key = view.findViewById(R.id.et_key_indoor);
            builder.setView(view);
            builder.setTitle(R.string.search_location);
            builder.setPositiveButton(R.string.search, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mPoiSearch.searchInCity((new PoiCitySearchOption())
                            .city(city.getText().toString()).keyword(key.getText().toString()));
                }
            });
            builder.setNegativeButton(R.string.all_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.show();
        } else {
            Toast.makeText(ARNavigationActivity.this, R.string.no_wifi, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onGetPoiResult(PoiResult result) {
        if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(ARNavigationActivity.this, R.string.no_result, Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            mBaiduMap.clear();
            PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(result);
            overlay.addToMap();
            overlay.zoomToSpan();
            return;
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

            // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
            String strInfo = "在";
            for (CityInfo cityInfo : result.getSuggestCityList()) {
                strInfo += cityInfo.city;
                strInfo += ",";
            }
            strInfo += R.string.result;
            Toast.makeText(ARNavigationActivity.this, strInfo, Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult result) {
        if (result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(ARNavigationActivity.this, R.string.sorry_result, Toast.LENGTH_SHORT)
                    .show();
        } else {
            LatLng latLng = result.getLocation();
            double latitude = latLng.latitude;
            double longitude = latLng.longitude;
            Intent intent = new Intent(ARNavigationActivity.this, PanoramaActivity.class);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            startActivity(intent);
        }
    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

    private class MyPoiOverlay extends PoiOverlay {

        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int index) {
            super.onPoiClick(index);
            PoiInfo poi = getPoiResult().getAllPoi().get(index);
            mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                    .poiUid(poi.uid));
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.select_indoor_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.select_indoor:
                searchButtonProcess();
                break;
            case android.R.id.home:
                startActivity(new Intent(ARNavigationActivity.this, ChooseMapFunctionActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        if (mapView != null) {
            mapView.onResume();

            mapView.showZoomControls(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        if (mapView != null) {
            mapView.onPause();
        }
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