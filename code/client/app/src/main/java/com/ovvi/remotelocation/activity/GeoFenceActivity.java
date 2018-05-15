package com.ovvi.remotelocation.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.ActionBar.LayoutParams;
import android.app.Notification;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.baidu.panosdk.plugin.indoor.util.ScreenUtils;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.api.fence.CreateFenceRequest;
import com.baidu.trace.api.fence.CreateFenceResponse;
import com.baidu.trace.api.fence.DeleteFenceRequest;
import com.baidu.trace.api.fence.DeleteFenceResponse;
import com.baidu.trace.api.fence.FenceListResponse;
import com.baidu.trace.api.fence.HistoryAlarmResponse;
import com.baidu.trace.api.fence.MonitoredStatus;
import com.baidu.trace.api.fence.MonitoredStatusByLocationResponse;
import com.baidu.trace.api.fence.MonitoredStatusInfo;
import com.baidu.trace.api.fence.MonitoredStatusResponse;
import com.baidu.trace.api.fence.OnFenceListener;
import com.baidu.trace.api.fence.UpdateFenceResponse;
import com.baidu.trace.model.CoordType;
import com.baidu.trace.model.StatusCodes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ovvi.remotelocation.R;
import com.ovvi.remotelocation.adapter.AddressListAdapter;
import com.ovvi.remotelocation.base.BaseMapActivity;
import com.ovvi.remotelocation.base.Common;
import com.ovvi.remotelocation.bean.GeoFenceResult;
import com.ovvi.remotelocation.gson.GeoFenceGson;
import com.ovvi.remotelocation.model.AddressListInfo;
import com.ovvi.remotelocation.utils.CommonUtil;
import com.ovvi.remotelocation.utils.HttpUtil;
import com.ovvi.remotelocation.utils.HttpUtil.onResponseCode;
import com.ovvi.remotelocation.utils.LogUtils;
import com.ovvi.remotelocation.utils.MapUtil;
import com.ovvi.remotelocation.utils.MarkerInfoUtil;
import com.ovvi.remotelocation.utils.PreferenceHelper;

/**
 * 地理围栏
 * 
 * @author chensong
 * 
 */
public class GeoFenceActivity extends BaseMapActivity implements OnClickListener,
        OnItemClickListener {

    private static final String TAG = "GeoFenceActivity";
    private Handler handler = new Handler();
    private static GsonBuilder builder = new GsonBuilder();
    private static Gson gson = builder.create();

    /** 当前用户id */
    private int current_id = 0;
    /** 用户经度 */
    private double longitude;
    /** 用户纬度 */
    private double latitude;
    private String descStreet;
    private String descLocation;
    private String createTime;

    private LocationApplication context;
    private MapUtil mapUtil;
    private BDLocation location;
    private BDLocation lastLocation;
    private HttpUtil client;

    private PopupWindow pWindow;

    private boolean isFirstLoc = true;

    /** 围栏中心点控件 */
    private EditText fenceCenterEditText;
    /** 围栏半径控件 */
    private EditText fenceRadiusEditText;
    /** 缩小半径 每次减小50 */
    private ImageButton smallButton;
    /** 增大半径 每次增加50 */
    private ImageButton largeButton;
    /** 围栏中心 */
    private String fenceCenter;
    /** 围栏名称 */
    private String fenceName = "myFence";
    /** 围栏半径 */
    private int fenceRadius;

    private CreateFenceRequest cfRequest;
    private DeleteFenceRequest deleteRequest;
    /** 操作围栏时，正在操作的围栏标识fenceId */
    private long fenceKey;

    /** 此次请求围栏的唯一标识 */
    int tag = new AtomicInteger().incrementAndGet();
    /** 轨迹服务ID 需要申请 */
    public long serviceId = 156106;
    /** 圆形围栏中心点坐标（地图坐标类型） */
    private LatLng circleCenter = null;
    /** 圆心点纬度坐标 */
    private double circleLatitude = 0;
    /** 圆心点经度坐标 */
    private double circleLongitude = 0;
    /** 围栏监听器 */
    private OnFenceListener mFenceListener = null;

    /** 去躁（默认不去噪） */
    private int denoise = 0;
    /** 轨迹客户端 */
    private LBSTraceClient mTraceClient = null;

    private MarkerOptions markerOptions;
    /** 圆覆盖物 */
    private Overlay overlay;
    private Marker marker;
    private Marker marker1;

    private MarkerInfoUtil info;

    private List<AddressListInfo> addressList = new ArrayList<AddressListInfo>();
    private AddressListInfo addressInfo;
    private AddressListAdapter addressAdapter;
    private ListView addressListView;

    private NotificationCompat.Builder notification;
    private NotificationManagerCompat nm;
    private int notificationId = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = (LocationApplication) getApplicationContext();
        client = new HttpUtil(context);
        mapUtil = new MapUtil(context);
        initListener();

        showBackwardView(R.drawable.top_bar_back);
        setTitle(R.string.fence_text);
        hideForwardView();
        showDropView();

        current_id = getIntent().getIntExtra("userId", 0);

        mTraceClient = new LBSTraceClient(this);

        requestLocation();
        requestElderlyLocation();
    }

    /**
     * 初始化围栏监听
     */
    private void initListener() {

        mFenceListener = new OnFenceListener() {
            @Override
            public void onCreateFenceCallback(CreateFenceResponse response) {
                Log.d("aaaa", "onCreateFenceCallback response.status=" + response.status);
                if (response.status != StatusCodes.SUCCESS) {
                    return;
                }

                if (overlay != null) {
                    overlay.remove();
                }
                if (marker != null) {
                    marker.remove();
                }
                fenceKey = response.getFenceId();
                Log.d("aaaa", "initFenceListener  fenceName=" + fenceName
                        + "  circleCenter=" + circleCenter + "  fenceRadius="
                        + fenceRadius);
                BitmapDescriptor bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.icon_address_me);
                markerOptions = new MarkerOptions().position(circleCenter).icon(bitmap)
                        .draggable(false);
                marker = (Marker) getBaiduMap().addOverlay(markerOptions);
                OverlayOptions overlayOptions = new CircleOptions().fillColor(0x000000FF)
                        .center(circleCenter)
                        .stroke(new Stroke(5, Color.rgb(0x23, 0x19, 0xDC)))
                        .radius(fenceRadius);
                overlay = addMapOverlay(overlayOptions);
                animateMapStatus(circleCenter);

            }

            @Override
            public void onUpdateFenceCallback(UpdateFenceResponse response) {
            }

            @Override
            public void onDeleteFenceCallback(DeleteFenceResponse deleteFenceResponse) {
                if (deleteFenceResponse.getStatus() == StatusCodes.SUCCESS) {
                    return;
                }

            }

            @Override
            public void onFenceListCallback(FenceListResponse response) {
            }

            @Override
            public void onMonitoredStatusCallback(MonitoredStatusResponse response) {
                // 查询监控对象状态响应结果
                List<MonitoredStatusInfo> monitoredStatusInfos = response
                        .getMonitoredStatusInfos();
                for (MonitoredStatusInfo monitoredStatusInfo : monitoredStatusInfos) {

                    MonitoredStatus status = monitoredStatusInfo.getMonitoredStatus();// 获取状态
                    switch (status) {
                    case in:
                        // 监控的设备在围栏内
                        break;
                    case out:
                        // 监控的设备在围栏外
                        break;
                    case unknown:
                        // 监控的设备状态未知
                        break;
                    }
                }

            }

            @Override
            public void onMonitoredStatusByLocationCallback(
                    MonitoredStatusByLocationResponse response) {

            }

            @Override
            public void onHistoryAlarmCallback(HistoryAlarmResponse response) {
                Log.d("aaaa", "onHistoryAlarmCallback ");
            }
        };

    }

    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            LogUtils.d(TAG, "  runnable..........");
            requestElderlyLocation();
            alarmMessage();
            handler.postDelayed(this, 60 * 1000);
        }
    };

    private void alarmMessage() {

        int distance = (int) CommonUtil.getDistance(longitude, latitude, circleLongitude,
                circleLatitude);

        LogUtils.d(TAG, "alarmMessage  distance=" + distance + " fenceRadius="
                + fenceRadius);
        if (distance < fenceRadius) {
            // 在围栏内
            showNotification(getResources().getString(R.string.fence_in));
        } else if (distance > fenceRadius) {
            // 在围栏外
            showNotification(getResources().getString(R.string.fence_out));
        }
    }

    /**
     * 提示通知
     * 
     * @param contentText
     */
    private void showNotification(String contentText) {

        notification = new NotificationCompat.Builder(context).setAutoCancel(true)
                .setOngoing(true).setLocalOnly(true).setShowWhen(false)
                .setContentTitle(contentText).setContentText(contentText)
                .setSmallIcon(R.drawable.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        nm = NotificationManagerCompat.from(context);
        nm.notify(notificationId, notification.build());
    }

    /**
     * 请求获取老人的位置
     */
    private void requestElderlyLocation() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("token", PreferenceHelper.getString(context, "token"));
        map.put("uid", String.valueOf(current_id));
        client.postRequest(Common.task.fence, Common.api.fence, map);

        client.setResponse(new onResponseCode() {

            @Override
            public void setPostResponse(final String responseData) {
                Runnable runnable = new Runnable() {

                    @Override
                    public void run() {
                        dealFenceResponse(responseData);
                    }
                };
                handler.post(runnable);
            }
        });
    }

    /**
     * 处理围栏创建的响应数据
     * 
     * @param json
     */
    private void dealFenceResponse(String json) {

        Log.d("aaaa", "dealFenceResponse json=" + json);
        if (!CommonUtil.isJsonDataResultEmpty(json)) {
            GeoFenceGson response = gson.fromJson(json, GeoFenceGson.class);
            int code = response.getCode();
            String msg = response.getMsg();
            GeoFenceResult result = response.getResult();
            if (code == Common.code.SUCCESS) {

                latitude = Double.valueOf(result.getLatitude());
                longitude = Double.valueOf(result.getLongitude());
                createTime = result.getCreateTime();
                descLocation = result.getDescLocation();
                descStreet = result.getDescStreet();
                info = new MarkerInfoUtil(latitude, longitude, descStreet, descLocation,
                        CommonUtil.getDateFromTime(Long.valueOf(createTime)));
                addOverlay(info);
            }
        }
    }

    private void addOverlay(MarkerInfoUtil info) {
        if (marker1 != null) {
            marker1.remove();
        }
        // 创建marker的显示图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_address_other);
        LatLng latLng = null;
        OverlayOptions options;
        // 获取经纬度
        latLng = new LatLng(info.getLatitude(), info.getLongitude());
        // 设置marker
        options = new MarkerOptions().position(latLng)// 设置位置
                .icon(bitmap)// 设置图标样式
                .zIndex(9) // 设置marker所在层级
                .draggable(true); // 设置手势拖拽;
        // 添加marker
        marker1 = (Marker) addMapOverlay(options);
        // 使用marker携带info信息，当点击事件的时候可以通过marker获得info信息
        Bundle bundle = new Bundle();
        // info必须实现序列化接口
        bundle.putSerializable("info", info);
        marker1.setExtraInfo(bundle);
        // 将地图显示在最后一个marker的位置
        animateMapStatus(latLng);
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
        LatLng latLngInfo = new LatLng(infoUtil.getLatitude(), infoUtil.getLongitude());
        // infowindow点击事件
        OnInfoWindowClickListener listener = new OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick() {
                // 隐藏infowindow
                hideInfoWindow();
            }
        };
        // infowindow中的布局
        TextView tv = new TextView(GeoFenceActivity.this);
        tv.setBackgroundResource(R.color.black);
        tv.setPadding(20, 10, 20, 20);
        tv.setTextColor(android.graphics.Color.WHITE);
        LogUtils.d(TAG, "infoUtil.getDescStreet()=" + infoUtil.getDescStreet());
        tv.setText(infoUtil.getDescStreet() + infoUtil.getDescLocation() + "\n"
                + infoUtil.getCreateTime());
        tv.setGravity(Gravity.CENTER);

        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromView(tv);
        // 显示infowindow
        InfoWindow infoWindow = new InfoWindow(bitmapDescriptor, latLngInfo, -120,
                listener);
        showInfoWindow(infoWindow);

        return true;
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
        LogUtils.d(
                TAG,
                "location:[" + lastLocation.getLatitude() + ","
                        + lastLocation.getLongitude() + "]");
        if (isFirstLoc) {
            isFirstLoc = false;
            LatLng ll = new LatLng(lastLocation.getLatitude(),
                    lastLocation.getLongitude());
            LogUtils.d(TAG, " lastLocation.getLatitude()=" + lastLocation.getLatitude()
                    + " lastLocation.getLongitude()=" + lastLocation.getLongitude());
            animateMapStatus(ll);
        }
    }

    /**
     * 增加覆盖物
     * 
     * @param latLng
     */
    private void showPopUpOverlay(LatLng latLng) {

        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_address_me);

        // 构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions().position(latLng).icon(bitmap);
        // 在地图上添加Marker，并显示
        addMapOverlay(option);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
        case R.id.drop:
            if (pWindow != null && pWindow.isShowing()) {
                pWindow.dismiss();
            }
            onTitleClicked(v);
            break;
        case R.id.button_backward:
            onBackPressed();
            break;
        default:
            break;
        }
    }

    @Override
    protected void onTitleClicked(View dropView) {
        setFenceParameter(dropView);
    }

    /**
     * 偏移距离
     * 
     * @param mView
     * @param contentView
     * @return
     */
    private static int[] popWindowPos(final View mView, final View contentView) {
        final int windowPos[] = new int[2];
        final int anchorLoc[] = new int[2];
        // 获取锚点View在屏幕上的左上角坐标位置
        mView.getLocationOnScreen(anchorLoc);
        final int anchorHeight = mView.getHeight();
        // 获取屏幕的高宽
        final int screenHeight = ScreenUtils.getSreenHeight(mView.getContext());
        final int screenWidth = ScreenUtils.getScreenWidth(mView.getContext());
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 计算contentView的高宽
        final int windowHeight = contentView.getMeasuredHeight();
        final int windowWidth = contentView.getMeasuredWidth();
        // 判断需要向上弹出还是向下弹出显示
        final boolean isNeedShowUp = (screenHeight - anchorLoc[1] - anchorHeight < windowHeight);
        if (isNeedShowUp) {
            windowPos[0] = screenWidth - windowWidth;
            windowPos[1] = anchorLoc[1] - windowHeight;
        } else {
            windowPos[0] = screenWidth - windowWidth;
            windowPos[1] = anchorLoc[1] + anchorHeight;
        }
        return windowPos;
    }

    /**
     * 设置围栏参数
     * 
     * @param view
     */
    protected void setFenceParameter(View view) {
        View contentview = LayoutInflater.from(context).inflate(
                R.layout.dialog_fence_options, null);
        pWindow = new PopupWindow(contentview, LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT, true);
        pWindow.setFocusable(true);
        pWindow.setBackgroundDrawable(new BitmapDrawable(null, ""));

        fenceCenterEditText = (EditText) contentview.findViewById(R.id.fence_center);
        fenceRadiusEditText = (EditText) contentview.findViewById(R.id.fence_radius);
        Button cancelButton = (Button) contentview.findViewById(R.id.fence_reset);
        Button confirmButton = (Button) contentview.findViewById(R.id.fence_confirm);
        smallButton = (ImageButton) contentview.findViewById(R.id.zoom_small);
        largeButton = (ImageButton) contentview.findViewById(R.id.zoom_large);

        addressListView = (ListView) contentview.findViewById(R.id.center_list);
        addressList.clear();
        addressAdapter = new AddressListAdapter(getBaseContext(), addressList);
        addressListView.setAdapter(addressAdapter);
        addressListView.setOnItemClickListener(this);

        if (!"".equals(PreferenceHelper.getString(context, "geofence_circle"))) {
            fenceCenterEditText.setText(PreferenceHelper.getString(context,
                    "geofence_circle"));
        }

        smallButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("aaaa", "onCreateFenceOperate smallButton");
                fenceRadius = Integer.valueOf(fenceRadiusEditText.getText().toString());
                fenceRadius -= 50;
                if (fenceRadius < 50) {
                    fenceRadius = 0;
                }
                fenceRadiusEditText.setText(fenceRadius + "");
            }
        });

        largeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("aaaa", "onCreateFenceOperate largeButton");
                fenceRadius = Integer.valueOf(fenceRadiusEditText.getText().toString());
                fenceRadius += 50;
                fenceRadiusEditText.setText(fenceRadius + "");
            }
        });

        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("aaaa", "onCreateFenceOperate cancelButton");
                fenceCenterEditText.setText("");
                fenceRadiusEditText.setText("");
            }
        });

        confirmButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("aaaa", "onCreateFenceOperate confirmButton");
                if (TextUtils.isEmpty(fenceRadiusEditText.getText())) {
                    fenceRadius = 0;
                } else {
                    fenceRadius = Integer.valueOf(fenceRadiusEditText.getText()
                            .toString());
                }
                fenceCenter = fenceCenterEditText.getText().toString();
                if (TextUtils.isEmpty(fenceCenter)) {
                    Toast.makeText(context,
                            getResources().getString(R.string.fence_center_toast),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // 调用删除围栏的操作
                    deleteFence();
                    setFenceAddress(fenceCenter);

                }

            }
        });

        int windowPos[] = popWindowPos(view, contentview);
        int xOff = 0;// 可以自己调整偏移
        windowPos[0] -= xOff;
        pWindow.showAtLocation(view, Gravity.TOP | Gravity.START, windowPos[0],
                windowPos[1]);
    }

    /**
     * 设置围栏中心点地址
     * 
     * @param address
     */
    private void setFenceAddress(String address) {

        LogUtils.d(TAG, "setFenceAddress descStreet==" + descStreet);
        if (descStreet == null) {
            Toast.makeText(context, "获取不到该用户数据", Toast.LENGTH_SHORT).show();
            return;
        }
        SuggestionSearch msearch = SuggestionSearch.newInstance();
        msearch.setOnGetSuggestionResultListener(new OnGetSuggestionResultListener() {

            @Override
            public void onGetSuggestionResult(SuggestionResult msg) {
                if (msg == null || msg.error != SearchResult.ERRORNO.NO_ERROR) {
                    return;
                }
                addressList.clear();
                for (SuggestionResult.SuggestionInfo info : msg.getAllSuggestions()) {

                    if (info.pt == null)
                        continue;
                    addressInfo = new AddressListInfo(info.key, info.pt.latitude,
                            info.pt.longitude);
                    addressList.add(addressInfo);
                }
                Log.e("aaaa", "addressList ===" + addressList.size());
                addressAdapter.notifyDataSetChanged();

            }
        });
        SuggestionSearchOption option = new SuggestionSearchOption();
        option.keyword(address);
        option.city(descStreet.split(" ")[0]);
        msearch.requestSuggestion(option);
    }

    /**
     * 围栏创建
     * 
     * @param fenceName
     * @param radius
     */
    private void createFence() {
        Log.d("aaaa", "createFence  fenceName=" + fenceName + "  circleCenter="
                + circleCenter + "  fenceRadius=" + fenceRadius);
        cfRequest = CreateFenceRequest.buildLocalCircleRequest(tag, serviceId, fenceName,
                CommonUtil.getImei(getApplication()),
                mapUtil.convertMapToTrace(circleCenter), fenceRadius, denoise,
                CoordType.bd09ll);
        mTraceClient.createFence(cfRequest, mFenceListener);

    }

    /**
     * 删除围栏
     */
    private void deleteFence() {
        List<Long> deleteFenceIds = new ArrayList<Long>();
        deleteFenceIds.add(fenceKey);
        deleteRequest = DeleteFenceRequest.buildLocalRequest(tag, serviceId, fenceName,
                deleteFenceIds);
        mTraceClient.deleteFence(deleteRequest, mFenceListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
        LogUtils.d(TAG, "onItemClick index=" + index);
        AddressListInfo info = addressList.get(index);
        addressListView.setVisibility(View.GONE);
        fenceCenterEditText.setText(info.getAddressDesc());
        PreferenceHelper.putString(context, "geofence_circle", info.getAddressDesc());

        circleLatitude = info.getLatidude();
        circleLongitude = info.getLongitude();

        circleCenter = new LatLng(info.getLatidude(), info.getLongitude());
        if (pWindow != null && pWindow.isShowing()) {
            pWindow.dismiss();
        }
        createFence();
        handler.postDelayed(runnable, 20000);
    }

}
