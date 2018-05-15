package com.ovvi.remotelocation.utils;

import java.lang.ref.WeakReference;
import java.util.List;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.ovvi.remotelocation.R;
import com.ovvi.remotelocation.base.BaseMapActivity;
import com.ovvi.remotelocation.model.CurrentLocation;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.ZoomControls;

/**
 * 地图工具类 Created by zhh .
 */

public class MapUtil {

    private static final String TAG = "MapUtil";

    private MapStatus mapStatus = null;

    private Marker mMoveMarker = null;

    private MapView mapView = null;

    private BaiduMap baiduMap = null;

    private MyLocationData locData;

    /**
     * 路线覆盖物
     */
    private Overlay polylineOverlay = null;

    public LatLng latLng;
	
	private Context context;

    private WeakReference<BaseMapActivity> activity;
    
    public static MapUtil newInstance(BaseMapActivity activity, MapView view) {
    	MapUtil instance = null;
    	try {
    		instance = new MapUtil(activity, view);
		} catch (Exception e) {
			LogUtils.e(TAG, "get MapUtil instance fail!" + e.getMessage());
		}
    	return instance;
	}

    private MapUtil(BaseMapActivity activity, MapView view) throws Exception {
    	if (null == view) {
    		throw new Exception("MapView is null");
		}
        this.activity = new WeakReference<BaseMapActivity>(activity);
        init(view);
    }
    
    public BaiduMap getBaiduMap() {
    	if (null != baiduMap) {
    		return baiduMap;			
		}
    	
    	if (null != mapView) {
			return mapView.getMap();
		} 
    	
    	return null;
    }
    public MapUtil(Context applicationContext) {
        // TODO Auto-generated constructor stub
        context = applicationContext;
    }

    public void init(MapView view) {
        mapView = view;
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(false);
        mapView.showZoomControls(true);
        MapView.setMapCustomEnable(false);
        // 去除百度logo
        int count = mapView.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mapView.getChildAt(i);
            if (child instanceof ZoomControls || child instanceof ImageView) {
                child.setVisibility(View.INVISIBLE);
            }
        }
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        
        MapStatusUpdate status = MapStatusUpdateFactory
                .newMapStatus(new MapStatus.Builder().zoom(15).build());
        baiduMap.setMapStatus(status);

        // baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
        // MyLocationConfiguration.LocationMode.FOLLOWING, true, null));
        baiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {// 缩放比例变化监听
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
            	if (null != activity && null != activity.get()) {
            		activity.get().onMapStatusChangeStart(mapStatus);
				}
            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {
                if (null != activity && null != activity.get()) {
            		activity.get().onMapStatusChange(mapStatus);
				}
            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
            	if (null != activity && null != activity.get()) {
            		activity.get().onMapStatusChangeFinish(mapStatus);
				}
            }

            @Override
            public void onMapStatusChangeStart(MapStatus arg0, int arg1) {
            	if (null != activity && null != activity.get()) {
            		activity.get().onMapStatusChangeStart(arg0, arg1);
				}
            }
        });
        
        baiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
            	if (null != activity && null != activity.get()) {
            		return activity.get().onMarkerClick(marker);
				}
            	return false;
            }
        });
        
        baiduMap.setOnMapClickListener(new OnMapClickListener() {
			
			@Override
			public boolean onMapPoiClick(MapPoi mapPoi) {
            	if (null != activity && null != activity.get()) {
            		return activity.get().onMapPoiClick(mapPoi);
				}
            	return false;
			}
			
			@Override
			public void onMapClick(LatLng latLng) {
				if (null != activity && null != activity.get()) {
            		activity.get().onMapClick(latLng);
				}
			}
		});
    }

    public void onStart() {
    }

    public void onStop() {
        if (baiduMap != null) {
            baiduMap.setMyLocationEnabled(false);
        }
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

    public void onDestroy() {
        if (null != mapView) {
            mapView.onDestroy();
            mapView = null;
        }
        if(baiduMap != null) {
            baiduMap.clear();
        }
    }

    public void clear() {
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
        }
        mapStatus = null;
        if (null != mapView) {
            mapView.onDestroy();
        }
    }


    public void showOverlay(LatLng latLng) {
        LogUtils.d(TAG, " showOverlay ");
        OverlayOptions option = new MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_address_me))
                .draggable(true);

        baiduMap.addOverlay(option);
    }

    /**
     * 将轨迹实时定位点转换为地图坐标
     */
    // public static LatLng convertTraceLocationToMap(TraceLocation location) {
    // if (null == location) {
    // return null;
    // }
    // double latitude = location.getLatitude();
    // double longitude = location.getLongitude();
    // if (Math.abs(latitude - 0.0) < 0.000001 && Math.abs(longitude - 0.0) <
    // 0.000001) {
    // return null;
    // }
    // LatLng currentLatLng = new LatLng(latitude, longitude);
    // if (CoordType.wgs84 == location.getCoordType()) {
    // LatLng sourceLatLng = currentLatLng;
    // CoordinateConverter converter = new CoordinateConverter();
    // converter.from(CoordinateConverter.CoordType.GPS);
    // converter.coord(sourceLatLng);
    // currentLatLng = converter.convert();
    // }
    // return currentLatLng;
    // }

    /**
     * 将地图坐标转换轨迹坐标
     * 
     * @param latLng
     * @return
     */
    public com.baidu.trace.model.LatLng convertMapToTrace(LatLng latLng) {
        return new com.baidu.trace.model.LatLng(latLng.latitude, latLng.longitude);
    }

    /**
     * 将轨迹坐标对象转换为地图坐标对象
     */
    public LatLng convertTraceToMap(com.baidu.trace.model.LatLng traceLatLng) {
        return new LatLng(traceLatLng.latitude, traceLatLng.longitude);
    }

    /**
     * 设置地图中心：使用已有定位信息；
     */
    public void setCenter(float direction) {
        if (!CommonUtil.isZeroPoint(CurrentLocation.latitude, CurrentLocation.longitude)) {
            LatLng currentLatLng = new LatLng(CurrentLocation.latitude,
                    CurrentLocation.longitude);
            updateMapLocation(currentLatLng, direction);
            animateMapStatus(currentLatLng);
            return;
        }
    }

    public void updateMapLocation(LatLng currentPoint, float direction) {

        try {
            if (currentPoint == null) {
                return;
            }

            locData = new MyLocationData.Builder().accuracy(0).direction(direction)
                    .latitude(currentPoint.latitude).longitude(currentPoint.longitude)
                    .build();
            baiduMap.setMyLocationData(locData);
        } catch (Exception e) {

        }

    }

    /**
     * 绘制历史轨迹
     */
    public void drawHistoryTrack(List<LatLng> points, boolean staticLine, float direction) {
        // 绘制新覆盖物前，清空之前的覆盖物
        try {

            baiduMap.clear();
            if (points == null || points.size() == 0) {
                if (null != polylineOverlay) {
                    polylineOverlay.remove();
                    polylineOverlay = null;
                }
                return;
            }

            if (points.size() == 1) {
                OverlayOptions startOptions = new MarkerOptions()
                        .position(points.get(0))
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.icon_address_me)).zIndex(9)
                        .draggable(true);
                baiduMap.addOverlay(startOptions);
                updateMapLocation(points.get(0), direction);
                animateMapStatus(points.get(0));
                return;
            }

            LatLng startPoint = points.get(0);
            LatLng endPoint = points.get(points.size() - 1);

            // 添加起点图标
            OverlayOptions startOptions = new MarkerOptions().position(startPoint)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.track_start))
                    .zIndex(9).draggable(true);

            // 添加路线（轨迹）
            OverlayOptions polylineOptions = new PolylineOptions().width(10)
                    .color(Color.BLUE).points(points);
            baiduMap.addOverlay(startOptions);
            if (staticLine) {
                // 添加终点图标
                drawEndPoint(endPoint);
            }

            polylineOverlay = baiduMap.addOverlay(polylineOptions);

            if (staticLine) {
                animateMapStatus(points);
            } else {
                updateMapLocation(points.get(points.size() - 1), direction);
                animateMapStatus(points.get(points.size() - 1));
            }

        } catch (Exception e) {

        }
    }

    public void drawEndPoint(LatLng endPoint) {
        // 添加终点图标
        OverlayOptions endOptions = new MarkerOptions().position(endPoint)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.track_end))
                .zIndex(9).draggable(true);
        baiduMap.addOverlay(endOptions);
    }

    public void animateMapStatus(List<LatLng> points) {
        if (null == points || points.isEmpty()) {
            return;
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng point : points) {
            builder.include(point);
        }
        MapStatusUpdate msUpdate = MapStatusUpdateFactory
                .newLatLngBounds(builder.build());
        baiduMap.animateMapStatus(msUpdate);
    }

    public void animateMapStatus(LatLng point) {
        try {
            MapStatus.Builder builder = new MapStatus.Builder();
            mapStatus = builder.target(point).build();
            baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus));
        } catch (Exception e) {

        }
    }

    public void refresh() {
        LatLng mapCenter = baiduMap.getMapStatus().target;
        float mapZoom = baiduMap.getMapStatus().zoom - 1.0f;
        setMapStatus(mapCenter, mapZoom);
    }

    public void setMapStatus(LatLng point, float zoom) {
        MapStatus.Builder builder = new MapStatus.Builder();
        mapStatus = builder.target(point).zoom(zoom).build();
        baiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus));
    }

}
