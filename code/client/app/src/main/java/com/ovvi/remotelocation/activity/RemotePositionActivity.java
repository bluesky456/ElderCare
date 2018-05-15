package com.ovvi.remotelocation.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ovvi.remotelocation.R;
import com.ovvi.remotelocation.base.BaseMapActivity;
import com.ovvi.remotelocation.base.Common;
import com.ovvi.remotelocation.bean.RemoteLocationResult;
import com.ovvi.remotelocation.gson.CommonGson;
import com.ovvi.remotelocation.gson.RemoteLocationGson;
import com.ovvi.remotelocation.utils.CommonUtil;
import com.ovvi.remotelocation.utils.HttpUtil;
import com.ovvi.remotelocation.utils.HttpUtil.onResponseCode;
import com.ovvi.remotelocation.utils.LogUtils;
import com.ovvi.remotelocation.utils.MarkerInfoUtil;
import com.ovvi.remotelocation.utils.PreferenceHelper;

import android.content.ContentResolver;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 远程定位主界面
 * 
 * @author chensong
 * 
 */
public class RemotePositionActivity extends BaseMapActivity implements OnClickListener {

    private static final String TAG = "RemotePositionActivity";

    boolean isFirstLoc = true; // 是否首次定位

    private GeoCoder geoCoder;

    private PopupWindow popupWindow;

    private HttpUtil client;
    private Handler handler = new Handler();
    private static GsonBuilder builder = new GsonBuilder();
    private static Gson gson = builder.create();

    /** 当前用户id */
    private int current_id;
    /** 当前位置经度 */
    private double cur_longitude;
    /** 当前位置纬度 */
    private double cur_latitude;
    /** 前一个位置经度 */
    private double pre_longitude = 0;
    /** 前一个位置纬度 */
    private double pre_latitude = 0;

    private BDLocation location;
    private BDLocation lastLocation;

    /** 位置描述 */
    private String descLocation;
    /** 街道描述 */
    private String descStreet;
    private MarkerInfoUtil infoUtil;
    private Marker marker;
    private BitmapDescriptor bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        // 获取地图控件引用
        client = new HttpUtil(getApplicationContext());
        current_id = getIntent().getIntExtra("userId", 0);
        showBackwardView(R.drawable.top_bar_back);
        showForwardView(R.drawable.location_refresh);
        if (CommonUtil.isOldMode()) {
            setTitle(R.string.my_location);
        } else {
            setTitle(R.string.remote_text);
        }

        requestLocation();
    }

    private void showPopUpOverlay(LatLng latLng) {

        bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_address_me);
        // 构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions().position(latLng).icon(bitmap);
        // 在地图上添加Marker，并显示
        List<OverlayOptions> options = new ArrayList<OverlayOptions>();
		options.add(option);
		addMapOverlays(options);
		animateMapStatus(latLng);
        if (CommonUtil.isOldMode() && lastLocation.getLatitude() != 4.9E-324) {
            showLocationInfo();
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        // super.onClick(v);
        switch (v.getId()) {
        case R.id.button_forward:
            onForward(v);
            break;

        case R.id.button_backward:
            onBackPressed();
            break;
        default:

            break;
        }
    }

    @Override
    protected void onForward(View forwardView) {
        if (CommonUtil.isOldMode()) {
            reportLocation();
            if (lastLocation != null && lastLocation.getCity() != null) {
                showLocationInfo();
                LatLng ll = new LatLng(lastLocation.getLatitude(),
                        lastLocation.getLongitude());
                animateMapStatus(ll);
            }
        } else {
            refreshLocation();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        // 从marker中获取info信息
        Bundle bundle = marker.getExtraInfo();
        if (bundle == null) {
            return false;
        }
        MarkerInfoUtil infoUtil = (MarkerInfoUtil) bundle.getSerializable("info");
        // infowindow位置
        LatLng latLng = new LatLng(infoUtil.getLatitude(), infoUtil
                .getLongitude());
        // infowindow中的布局
        TextView tv = new TextView(RemotePositionActivity.this);
        tv.setBackgroundResource(R.color.black);
        tv.setPadding(20, 10, 20, 20);
        tv.setTextColor(android.graphics.Color.WHITE);
        LogUtils.d(TAG, "infoUtil.getDescStreet()=" + infoUtil.getDescStreet());
        tv.setText(infoUtil.getDescStreet() + infoUtil.getDescLocation() + "\n"
                + infoUtil.getCreateTime());
        tv.setGravity(Gravity.CENTER);

        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromView(tv);
        // 显示infowindow
        InfoWindow infoWindow = new InfoWindow(bitmapDescriptor, latLng, -120, infoWindowClickListener);
        showInfoWindow(infoWindow);

        return true;
    }
    
    /**
     * 位置上报
     */
    private void reportLocation() {
        if (lastLocation == null) {
            return;
        }
        if (pre_latitude != 0 && pre_longitude != 0) {
            if (pre_latitude == lastLocation.getLatitude()
                    && pre_longitude == lastLocation.getLongitude()) {
                return;
            } else {
                // 两次的位置相差不足50米不上报
                if (CommonUtil.getDistance(pre_longitude, pre_latitude,
                        lastLocation.getLongitude(), lastLocation.getLatitude()) < 50) {
                    return;
                }
            }
        }
        pre_latitude = lastLocation.getLatitude();
        pre_longitude = lastLocation.getLongitude();
        HashMap<String, String> map = new HashMap<String, String>();
        if (lastLocation.getLatitude() != 0 && lastLocation.getLongitude() != 0
                && lastLocation.getLocationDescribe() != null) {

            LogUtils.d(TAG,
                    "reportLocation description=" + lastLocation.getLocationDescribe());
            map.put("longitude", String.valueOf(pre_longitude));
            map.put("latitude", String.valueOf(pre_latitude));
            map.put("descStreet", lastLocation.getCity() + " " + lastLocation.getStreet());
            map.put("descLocation", lastLocation.getLocationDescribe());
            map.put("token", PreferenceHelper.getString(getApplicationContext(), "token"));
            client.postRequest(Common.task.location_report, Common.api.location_report,
                    map);
            client.setResponse(new onResponseCode() {

                @Override
                public void setPostResponse(final String responseData) {
                    Runnable runnable = new Runnable() {

                        @Override
                        public void run() {
                            dealLocationReport(responseData);
                        }
                    };
                    handler.post(runnable);
                }
            });
        }
    }

    private void dealLocationReport(String json) {
        LogUtils.d(TAG, "dealLocationReport josn=" + json);
        CommonGson response = gson.fromJson(json, CommonGson.class);
        int code = response.getCode();
        String msg = response.getMsg();
        if (code == Common.code.SUCCESS) {

        } else {
            Toast.makeText(RemotePositionActivity.this, msg, Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 刷新远程定位
     */
    private void refreshLocation() {

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("uid", String.valueOf(current_id));
        map.put("token", PreferenceHelper.getString(getApplicationContext(), "token"));
        LogUtils.d(TAG,
                "refreshLocation  token==" + PreferenceHelper.getString(getApplicationContext(), "token"));
        client.postRequest(Common.task.remote_receive, Common.api.remote_receive, map);

        client.setResponse(new onResponseCode() {

            @Override
            public void setPostResponse(final String responseData) {
                // TODO Auto-generated method stub
                Runnable runnable = new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        dealRemoteLocationRefresh(responseData);
                    }
                };
                handler.post(runnable);
            }
        });
    }

    /**
     * 位置刷新响应
     * 
     * @param json
     */
    private void dealRemoteLocationRefresh(String json) {

        LogUtils.d(TAG, "dealRemoteLocationRefresh josn=" + json);
        if (!CommonUtil.isJsonDataResultEmpty(json)) {

            RemoteLocationGson response = gson.fromJson(json, RemoteLocationGson.class);
            int code = response.getCode();
            String msg = response.getMsg();
            RemoteLocationResult result = response.getResult();
            if (code == Common.code.SUCCESS) {
                cur_longitude = Double.valueOf(result.getLongitude());
                cur_latitude = Double.valueOf(result.getLatitude());
                descStreet = result.getDescStreet();
                descLocation = result.getDescLocation();
                String createTime = result.getCreateTime();
                infoUtil = new MarkerInfoUtil(cur_latitude, cur_longitude, descStreet,
                        descLocation,
                        CommonUtil.getDateFromTime(Long.valueOf(createTime)));
                addOverlay(infoUtil);
            } else {
                Toast.makeText(RemotePositionActivity.this, msg, Toast.LENGTH_SHORT)
                        .show();
            }

        } else {
            CommonGson response = gson.fromJson(json, CommonGson.class);
            int code = response.getCode();
            String msg = response.getMsg();

            Toast.makeText(RemotePositionActivity.this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    private void addOverlay(MarkerInfoUtil info) {
        if (marker != null) {
            marker.remove();
        }
        // 创建marker的显示图标
        bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_address_other);
        LatLng latLng = null;

        // 获取经纬度
        latLng = new LatLng(info.getLatitude(), info.getLongitude());
        // 设置marker
        OverlayOptions option = new MarkerOptions().position(latLng)// 设置位置
                .icon(bitmap)// 设置图标样式
                .zIndex(9) // 设置marker所在层级
                .draggable(true); // 设置手势拖拽;
        // 添加marker
        marker = (Marker) addMapOverlay(option);
        // 使用marker携带info信息，当点击事件的时候可以通过marker获得info信息
        Bundle bundle = new Bundle();
        // info必须实现序列化接口
        bundle.putSerializable("info", info);
        marker.setExtraInfo(bundle);
        // 将地图显示在最后一个marker的位置
        animateMapStatus(latLng);
        showLocationInfo();
    }

    /**
     * 显示位置信息
     */
    protected void showLocationInfo() {

        View popupWindowView = getLayoutInflater().inflate(
                R.layout.remote_location_address, null);
        popupWindow = new PopupWindow(popupWindowView, LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT, true);
        popupWindow.setAnimationStyle(R.style.AnimationBottomFade);
        ColorDrawable dw = new ColorDrawable(0xffffffff);
        popupWindow.setBackgroundDrawable(dw);
        popupWindow.showAtLocation(
                getLayoutInflater().inflate(R.layout.remote_location_layout, null),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

        popupWindowView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                return false;
            }

        });
        TextView address = (TextView) popupWindowView.findViewById(R.id.remote_address);
        TextView address_detail = (TextView) popupWindowView
                .findViewById(R.id.remote_address_detail);
        LogUtils.d(TAG, "showLocationInfo lastLocation=" + lastLocation);
        if (CommonUtil.isOldMode()) {
            address.setText(R.string.my_location);
            address_detail.setText(lastLocation.getCity() + lastLocation.getStreet()
                    + lastLocation.getLocationDescribe());
        } else {
            address.setText(descLocation);
            // double distance = CommonUtil.getDistance(cur_longitude,
            // cur_latitude,
            // lastLocation.getLongitude(), lastLocation.getLatitude());
            address_detail.setText(/* "距您" + distance + "米  " + */descStreet
                    + descLocation);

        }

    }

    @Override
    protected void onResume() {
    	super.onResume();
        if (!CommonUtil.isOldMode()) {
            refreshLocation();
        }
    }

    /**
     * 主动上报位置
     */
    private void positiveReportLocation() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        TimeFormat(hour);
    }

    /**
     * 判断当前所处的时间范围
     * 
     * @param hour
     */
    private void TimeFormat(int hour) {
        // 获得内容提供者
        ContentResolver mResolver = this.getContentResolver();
        // 获得系统时间制
        String timeFormat = android.provider.Settings.System.getString(mResolver,
                android.provider.Settings.System.TIME_12_24);
        LogUtils.d("aaaa", "TimeFormat hour=" + hour + "  timeFormat=" + timeFormat);
        // 判断时间制
        if ("24".equals(timeFormat)) {
            // 24小时制
            if (hour > 5 && hour < 22) {
                reportLocation();
            }
        } else {
            // 12小时制
            if (hour > 12) {
                hour -= 12;
            }
            // 获得日历
            Calendar mCalendar = Calendar.getInstance();
            if (mCalendar.get(Calendar.AM_PM) == 0) {
                LogUtils.d("aaaa", "day........ hour=" + hour);
                // 上午
                if (hour > 5) {
                    reportLocation();
                }
            } else {
                LogUtils.d("aaaa", "night........ hour=" + hour);
                // 下午
                if (hour < 10) {
                    reportLocation();
                }
            }
        }
    }

    Runnable reportLocationRunnable = new Runnable() {

        @Override
        public void run() {
            LogUtils.d("aaaa", "reportLocationRunnable .........");
            positiveReportLocation();
            handler.postDelayed(this, 30 * 60 * 1000);
        }
    };

    /**
     * 经纬转地址信息
     * 
     * @param latlng
     */
    private void latlngToAddress(LatLng latlng) {
        geoCoder = GeoCoder.newInstance();

        OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
            // 反地理编码查询结果回调函数
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    // 没有检测到结果
                    Toast.makeText(getApplication(), "抱歉，未能找到结果", Toast.LENGTH_LONG)
                            .show();
                }
                StringBuffer sb = new StringBuffer();
                List<PoiInfo> list = result.getPoiList();
                for (PoiInfo poiInfo : list) {
                    sb.append("----------------------------------------").append("\n");
                    sb.append("名称：").append(poiInfo.name).append("\n");
                    sb.append("地址：").append(poiInfo.address).append("\n");
                    sb.append("经纬度：").append(poiInfo.location).append("\n");
                    sb.append("城市：").append(poiInfo.city).append("\n");
                    sb.append("电话：").append(poiInfo.phoneNum).append("\n");
                    sb.append("邮编：").append(poiInfo.postCode).append("\n");
                }
                // 经纬度所对应的位置
                Toast.makeText(getApplication(),
                        "位置：" + result.getAddress() + " sp=" + sb.toString(),
                        Toast.LENGTH_LONG).show();
            }

            // 地理编码查询结果回调函数
            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    // 没有检测到结果
                }
            }
        };
        // 设置地理编码检索监听者
        geoCoder.setOnGetGeoCodeResultListener(listener);
        //
        ReverseGeoCodeOption reverseGeoCodeOption = new ReverseGeoCodeOption();
        reverseGeoCodeOption.location(latlng);// 设置坐标点
        geoCoder.reverseGeoCode(reverseGeoCodeOption);// 将坐标点转换为地址信息

    }
    
    @Override
    public void onLocationResponse(Object obj) {
    	if (null == obj) {
			return;
		}

		// TODO 再次请求，取决于UI是否需要连续数据，delay的时间也由UI的需求来定
		requestLocationDelayed(1000);

        location = (BDLocation) obj;
        if (lastLocation != null) {
            if (lastLocation.getLatitude() == location.getLatitude()
                    && lastLocation.getLongitude() == location.getLongitude()) {
                return;
            }
        }
        MyLocationData data = new MyLocationData.Builder().accuracy(0)
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(100).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();

        updateLocationData(data);

        lastLocation = location;
        LogUtils.d(TAG, "location:[" + lastLocation.getLatitude() + ","
                + lastLocation.getLongitude() + "]");
        if (isFirstLoc) {
            isFirstLoc = false;
            LatLng ll = new LatLng(lastLocation.getLatitude(),
                    lastLocation.getLongitude());
            LogUtils.d(
                    TAG,
                    " lastLocation.getLatitude()=" + lastLocation.getLatitude()
                            + " lastLocation.getLongitude()="
                            + lastLocation.getLongitude());
            animateMapStatus(ll);
            showPopUpOverlay(ll);
        }
    }

}
