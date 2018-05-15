package com.ovvi.remotelocation.base;

import java.util.List;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.ovvi.remotelocation.activity.TitleActivity;
import com.ovvi.remotelocation.constants.Constant;
import com.ovvi.remotelocation.locationhelper.LocationRequestHelper;
import com.ovvi.remotelocation.locationhelper.UiLocationCallback;
import com.ovvi.remotelocation.utils.LogUtils;
import com.ovvi.remotelocation.utils.MapUtil;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.Window;
import android.widget.FrameLayout;

public class BaseMapActivity extends TitleActivity implements UiLocationCallback {
	private static final String TAG = "BaseMapActivity";
	private MapView mMapView;
	private MapUtil mapUtil;
	private FrameLayout bodyLayout;
	private LocationRequestHelper locationHelper;

	/** UI ==> LocationService的消息通道 */
	private Messenger messengerToService;

	/** LocationService ==> UI的消息通道 */
	private Messenger messengerFromService;
	
    protected OnInfoWindowClickListener infoWindowClickListener = new OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick() {
            // 隐藏infowindow
            hideInfoWindow();
        }
    };

	// 以bind形式开启service，故有ServiceConnection接收回调
	private ServiceConnection mConn = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			LogUtils.d(TAG, "onServiceConnected");
			messengerToService = new Messenger(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			messengerToService = null;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		mMapView = new MapView(this, new BaiduMapOptions());
		bodyLayout = new FrameLayout(this);
		bodyLayout.addView(mMapView);
		setContentView(bodyLayout);

		mapUtil = MapUtil.newInstance(BaseMapActivity.this, mMapView);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (null != mMapView) {
			mMapView.onSaveInstanceState(outState);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (null != mapUtil) {
			mapUtil.onResume();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (null != mapUtil) {
			mapUtil.onPause();
		}
	}

	@Override
	protected void onDestroy() {
		super.onPause();
		if (null != mapUtil) {
			mapUtil.onDestroy();
		}
		
		// must release handler, to avoid memory leak
		if (null != locationHelper) {
			locationHelper.release();			
		}
        if (null != messengerToService) {
        	unbindService(mConn);			
		}
	}
	
    private void setupLocationService() {
        LogUtils.d(TAG, "setupLocationService from " + this.getLocalClassName());
        Intent intent = new Intent(Constant.ACTION_ENABLE_LOCATION_SERVICE);
        bindService(intent, mConn, Context.BIND_AUTO_CREATE);
    }

	@Override
	public void onRequestLocation() {
        if (null == messengerToService) {
            setupLocationService();
            locationHelper.requestLocationDelayed(500);
        } else {
            Message message = Message.obtain(null,
                    Constant.MSG_LOCATION_REQUEST_FROM_LOCAL);
            message.replyTo = messengerFromService;
            try {
            	messengerToService.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
	}

	@Override
	public void onLocationResponse(Object obj) {
	}
	
	protected BaiduMap getBaiduMap() {
		if (null != mapUtil) {
			return mapUtil.getBaiduMap();
		}
		
		return null;
	}

	/**
	 * 百度地图中marker点击回调，子类可重载
	 * @param marker
	 * @return 
	 */
	public boolean onMarkerClick(Marker marker) {
		return false;
	}

	public void onMapStatusChangeStart(MapStatus mapStatus) {
		
	}

	public void onMapStatusChange(MapStatus mapStatus) {
	}

	public void onMapStatusChangeFinish(MapStatus mapStatus) {
	}

	public void onMapStatusChangeStart(MapStatus arg0, int arg1) {
	}
	
    protected void animateMapStatus(List<LatLng> points) {
        if (null == points || points.isEmpty()) {
            return;
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng point : points) {
            builder.include(point);
        }
        MapStatusUpdate msUpdate = MapStatusUpdateFactory
                .newLatLngBounds(builder.build());
        getBaiduMap().animateMapStatus(msUpdate);
    }

    protected void animateMapStatus(LatLng point) {
        try {
            MapStatus.Builder builder = new MapStatus.Builder();
            MapStatus mapStatus = builder.target(point).build();
            getBaiduMap().animateMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus));
        } catch (Exception e) {
        }
    }
    
    protected void updateLocationData(MyLocationData data) {
    	BaiduMap map = getBaiduMap();
    	if (null != map && null != data) {
    		map.setMyLocationData(data);
		}
	}
    
    protected void addMapOverlays(List<OverlayOptions> options) {
    	BaiduMap map = getBaiduMap();
    	if (null != map && null != options && !options.isEmpty()) {
    		map.addOverlays(options);
		}
	}
    
    protected Overlay addMapOverlay(OverlayOptions option) {
    	BaiduMap map = getBaiduMap();
    	if (null != map && null != option) {
    		return map.addOverlay(option);
		}
    	return null;
	}
    
    protected void showInfoWindow(InfoWindow infoWindow) {
    	BaiduMap map = getBaiduMap();
    	if (null != map && null != infoWindow) {
    		map.showInfoWindow(infoWindow);
		}
	}
    
    protected void hideInfoWindow() {
    	BaiduMap map = getBaiduMap();
    	if (null != map) {
    		map.hideInfoWindow();
		}
    }
    
    /**
     * UI启动定位，调用后会自动拉起LocationService，定位成功后自动发送消息回调
     */
    protected void requestLocation() {
    	if (null == locationHelper) {
    		locationHelper = new LocationRequestHelper(this);
		}
    	if (null == messengerFromService) {
    		messengerFromService = new Messenger(locationHelper.getHandler());			
		}
    	locationHelper.requestLocation();
	}
    
    protected void requestLocationDelayed(long milisecends) {
    	if (null != locationHelper) {
    		locationHelper.requestLocationDelayed(milisecends);    		
    	}
	}

	public boolean onMapPoiClick(MapPoi mapPoi) {
		return false;
	}

	public Object onMapClick(LatLng latLng) {
		return null;
	}
}
