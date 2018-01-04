package com.example.admin.nantuoappdemo.utils;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.trace.model.CoordType;
import com.baidu.trace.model.TraceLocation;
import com.example.admin.nantuoappdemo.bean.CurrentLocation;
import com.example.admin.nantuoappdemo.othermanager.MyApplication;

import java.util.List;

import static com.example.admin.nantuoappdemo.utils.BitmapUtil.bmEnd;
import static com.example.admin.nantuoappdemo.utils.BitmapUtil.bmStart;


/**
 * 地图工具类
 * Created by admin .
 */

public class MapUtil {

    private static MapUtil INSTANCE = new MapUtil();

    private MapStatus mapStatus = null;

    private Marker mMoveMarker = null;

    public MapView mapView = null;

    public BaiduMap baiduMap = null;

    public LatLng lastPoint = null;

    private MyLocationData locData;

    /**
     * 路线覆盖物
     */
    public Overlay polylineOverlay = null;

    private float mCurrentZoom = 18.0f;

    private MapUtil() {
    }

    public static MapUtil getInstance() {
        return INSTANCE;
    }

    public void init(MapView view) {
        mapView = view;
        baiduMap = mapView.getMap();
        mapView.showZoomControls(false);
        baiduMap.setMyLocationEnabled(true);
        baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, null));
    }

    public void onPause() {
        if (null != mapView) {
            mapView.onPause();
        }
    }

    public void onResume() {
        if (null != mapView) {
            mapView.onResume();
        }
    }

    public void clear() {
        lastPoint = null;
        if (null != mMoveMarker) {
            mMoveMarker.remove();
            mMoveMarker = null;
        }
        if (null != polylineOverlay) {
            polylineOverlay.remove();
            polylineOverlay = null;
        }
        if (null != baiduMap) {
            baiduMap.clear();
            baiduMap = null;
        }
        mapStatus = null;
        if (null != mapView) {
            mapView.onDestroy();
            mapView = null;
        }
    }

    /**
     * 将轨迹实时定位点转换为地图坐标
     */
    public static LatLng convertTraceLocation2Map(TraceLocation location) {
        if (null == location) {
            return null;
        }
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        if (Math.abs(latitude - 0.0) < 0.000001 && Math.abs(longitude - 0.0) < 0.000001) {
            return null;
        }
        LatLng currentLatLng = new LatLng(latitude, longitude);
        if (CoordType.wgs84 == location.getCoordType()) {
            LatLng sourceLatLng = currentLatLng;
            CoordinateConverter converter = new CoordinateConverter();
            converter.from(CoordinateConverter.CoordType.GPS);
            converter.coord(sourceLatLng);
            currentLatLng = converter.convert();
        }
        return currentLatLng;
    }

    /**
     * 将轨迹坐标对象转换为地图坐标对象
     */
    public static LatLng convertTrace2Map(com.baidu.trace.model.LatLng traceLatLng) {
        return new LatLng(traceLatLng.latitude, traceLatLng.longitude);
    }

    /**
     * 设置地图中心：使用已有定位信息；
     */
    public void setCenter(float direction) {
        if (!CommonUtil.isZeroPoint(CurrentLocation.latitude, CurrentLocation.longitude)) {
            LatLng currentLatLng = new LatLng(CurrentLocation.latitude, CurrentLocation.longitude);
            updateMapLocation(currentLatLng, direction);
            animateMapStatus(currentLatLng);
            return;
        }
    }

    /**
     * 设置地图中心：使用已有定位信息；
     *
     * @param trackApp
     */
    public void setCenter(MyApplication trackApp) {
        if (!CommonUtil.isZeroPoint(CurrentLocation.latitude, CurrentLocation.longitude)) {
            LatLng currentLatLng = new LatLng(CurrentLocation.latitude, CurrentLocation.longitude);
            updateStatus(currentLatLng, false);
            return;
        }
        String lastLocation = trackApp.trackConf.getString(Constants.LAST_LOCATION, null);
        if (!TextUtils.isEmpty(lastLocation)) {
            String[] locationInfo = lastLocation.split(";");
            if (!CommonUtil.isZeroPoint(Double.parseDouble(locationInfo[1]),
                    Double.parseDouble(locationInfo[2]))) {
                LatLng currentLatLng = new LatLng(Double.parseDouble(locationInfo[1]),
                        Double.parseDouble(locationInfo[2]));
                updateStatus(currentLatLng, false);
                return;
            }
        }
    }

    public void updateStatus(LatLng currentPoint, boolean showMarker) {
        if (null == baiduMap || null == currentPoint) {
            return;
        }

        if (null != baiduMap.getProjection()) {
            Point screenPoint = baiduMap.getProjection().toScreenLocation(currentPoint);
            // 点在屏幕上的坐标超过限制范围，则重新聚焦底图
            if (screenPoint.y < 200 || screenPoint.y > MyApplication.screenHeight - 500
                    || screenPoint.x < 200 || screenPoint.x > MyApplication.screenWidth - 200
                    || null == mapStatus) {
                animateMapStatus(currentPoint, 15.0f);
            }
        } else if (null == mapStatus) {
            // 第一次定位时，聚焦底图
            setMapStatus(currentPoint, 15.0f);
        }

        if (showMarker) {
            addMarker(currentPoint);
        }

    }

    /**
     * 添加地图覆盖物
     */
    public void addMarker(LatLng currentPoint) {
        if (null == mMoveMarker) {
            mMoveMarker = addOverlay(currentPoint, BitmapUtil.bmArrowPoint, null);
            return;
        }

        if (null != lastPoint) {
            moveLooper(currentPoint);
        } else {
            lastPoint = currentPoint;
            mMoveMarker.setPosition(currentPoint);
        }
    }

    public Marker addOverlay(LatLng currentPoint, BitmapDescriptor icon, Bundle bundle) {
        OverlayOptions overlayOptions = new MarkerOptions().position(currentPoint)
                .icon(icon).zIndex(9).draggable(true);
        Marker marker = (Marker) baiduMap.addOverlay(overlayOptions);
        if (null != bundle) {
            marker.setExtraInfo(bundle);
        }
        return marker;
    }

    public void animateMapStatus(LatLng point, float zoom) {
        MapStatus.Builder builder = new MapStatus.Builder();
        mapStatus = builder.target(point).zoom(zoom).build();
        baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus));
    }

    public void setMapStatus(LatLng point, float zoom) {
        MapStatus.Builder builder = new MapStatus.Builder();
        mapStatus = builder.target(point).zoom(zoom).build();
        baiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus));
    }

    public void updateMapLocation(LatLng currentPoint, float direction) {

        if (currentPoint == null || baiduMap == null) {
            return;
        }

        locData = new MyLocationData.Builder().accuracy(0).
                direction(direction).
                latitude(currentPoint.latitude).
                longitude(currentPoint.longitude).build();
        baiduMap.setMyLocationData(locData);

    }

    /**
     * 移动逻辑
     */
    public void moveLooper(LatLng endPoint) {

        mMoveMarker.setPosition(lastPoint);
        mMoveMarker.setRotate((float) CommonUtil.getAngle(lastPoint, endPoint));

        double slope = CommonUtil.getSlope(lastPoint, endPoint);
        // 是不是正向的标示（向上设为正向）
        boolean isReverse = (lastPoint.latitude > endPoint.latitude);
        double intercept = CommonUtil.getInterception(slope, lastPoint);
        double xMoveDistance = isReverse ? CommonUtil.getXMoveDistance(slope) : -1 * CommonUtil.getXMoveDistance(slope);

        for (double latitude = lastPoint.latitude; latitude > endPoint.latitude == isReverse; latitude =
                latitude - xMoveDistance) {
            LatLng latLng;
            if (slope != Double.MAX_VALUE) {
                latLng = new LatLng(latitude, (latitude - intercept) / slope);
            } else {
                latLng = new LatLng(latitude, lastPoint.longitude);
            }
            mMoveMarker.setPosition(latLng);
        }
    }

    /**
     * 绘制历史轨迹
     */
    public void drawHistoryTrack(List<LatLng> points, boolean staticLine, float direction) {
        Log.d(Constants.TAG, "drawHistoryTrack");
        try {
            if (baiduMap == null) {
                return;
            }

            // 绘制新覆盖物前，清空之前的覆盖物
            baiduMap.clear();
            if (points == null || points.size() == 0) {
                if (null != polylineOverlay) {
                    polylineOverlay.remove();
                    polylineOverlay = null;
                }
                return;
            }

            if (points.size() == 1) {
                OverlayOptions startOptions = new MarkerOptions().position(points.get(0)).icon(bmStart)
                        .zIndex(9).draggable(true);
                baiduMap.addOverlay(startOptions);
                animateMapStatus(points.get(0));
                return;
            }

            LatLng startPoint = points.get(0);
            LatLng endPoint = points.get(points.size() - 1);

            // 添加起点图标
            OverlayOptions startOptions = new MarkerOptions()
                    .position(startPoint).icon(bmStart)
                    .zIndex(9).draggable(true);

            // 添加路线（轨迹）
            OverlayOptions polylineOptions = new PolylineOptions().width(10)
                    .color(Color.BLUE).points(points);
            if (staticLine) {
                // 添加终点图标
                drawEndPoint(endPoint);
            }

            baiduMap.addOverlay(startOptions);
            polylineOverlay = baiduMap.addOverlay(polylineOptions);

            if (staticLine) {
                animateMapStatus(points);
            } else {
                updateMapLocation(points.get(points.size() - 1), direction);
                animateMapStatus(points.get(points.size() - 1));
            }

        } catch (Exception e) {
            Log.e(Constants.TAG, e.toString());
        }
    }

    public void drawEndPoint(LatLng endPoint) {
        if (baiduMap == null) {
            return;
        }
        // 添加终点图标
        OverlayOptions endOptions = new MarkerOptions().position(endPoint)
                .icon(bmEnd).zIndex(9).draggable(true);
        baiduMap.addOverlay(endOptions);
    }

    public void animateMapStatus(List<LatLng> points) {
        if (null == points || points.isEmpty() || baiduMap == null) {
            return;
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng point : points) {
            builder.include(point);
        }
        MapStatusUpdate msUpdate = MapStatusUpdateFactory.newLatLngBounds(builder.build());
        baiduMap.animateMapStatus(msUpdate);
    }

    public void animateMapStatus(LatLng point) {
        if (baiduMap == null) {
            return;
        }
        MapStatus.Builder builder = new MapStatus.Builder();
        mapStatus = builder.target(point).zoom(mCurrentZoom).build();
        baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus));
    }
}

