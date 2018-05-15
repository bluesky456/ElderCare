package com.ovvi.remotelocation.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.panosdk.plugin.indoor.util.ScreenUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ovvi.remotelocation.R;
import com.ovvi.remotelocation.base.BaseMapActivity;
import com.ovvi.remotelocation.base.Common;
import com.ovvi.remotelocation.bean.HistoryTrackResult;
import com.ovvi.remotelocation.gson.CommonGson;
import com.ovvi.remotelocation.gson.HistoryTrackGson;
import com.ovvi.remotelocation.utils.CommonUtil;
import com.ovvi.remotelocation.utils.HttpUtil;
import com.ovvi.remotelocation.utils.HttpUtil.onResponseCode;
import com.ovvi.remotelocation.utils.LogUtils;
import com.ovvi.remotelocation.utils.MarkerInfoUtil;
import com.ovvi.remotelocation.utils.PreferenceHelper;

/**
 * 历史轨迹主界面
 * 
 * @author chensong
 * 
 */
public class HistoryTrackActivity extends BaseMapActivity implements OnClickListener {

    private final static String TAG = "HistoryTrackActivity";

    private LocationApplication context;
    private HttpUtil client;
    private Handler handler = new Handler();
    private static GsonBuilder builder = new GsonBuilder();
    private static Gson gson = builder.create();
    /** 用户位置信息 */
    private List<HistoryTrackResult> trackResults;

    /** 查看的当前用户id */
    private int currentId;
    /** 选择查看轨迹的日期 */
    private String selectDate;
    /** 当前日期 */
    private String currentDate;

    private PopupWindow pWindow;

    private Calendar calendar;
    private int year;
    private int month;
    private int day;

    private List<MarkerInfoUtil> infos = new ArrayList<MarkerInfoUtil>();

    private BDLocation location;
    private BDLocation lastLocation;

    private boolean isFirstLoc = true;

    private Marker marker;
    private List<Marker> markerLists = new ArrayList<Marker>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        context = (LocationApplication) getApplicationContext();
        client = new HttpUtil(context);
        currentId = getIntent().getIntExtra("userId", 0);

        showBackwardView(R.drawable.top_bar_back);
        hideForwardView();
        showDropView();
        setTitle(R.string.track_text);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        currentDate = formatDate(year, month + 1, day);

        requestLocation();
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
            showPopUpOverlay(ll);
        }
    }

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
        case R.id.button_backward:
            onBackPressed();
            break;
        case R.id.drop:
            if (pWindow != null && pWindow.isShowing()) {
                pWindow.dismiss();
            }
            selectDateWindow(v);
            break;
        default:
            break;
        }
    }

    /**
     * 弹出日期选择框
     * 
     * @param view
     */
    protected void selectDateWindow(View view) {
        View contentview = LayoutInflater.from(context).inflate(
                R.layout.date_select_picker, null);
        pWindow = new PopupWindow(contentview, LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT, true);
        pWindow.setFocusable(true);
        pWindow.setBackgroundDrawable(new BitmapDrawable(null, ""));

        DatePicker datePicker = (DatePicker) contentview.findViewById(R.id.date_picker);
        TextView select = (TextView) contentview.findViewById(R.id.select_btn);

        select.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (pWindow != null && pWindow.isShowing()) {
                    pWindow.dismiss();
                }
                if (selectDate == null) {
                    selectDate = currentDate;
                }
                postDisplayTrack();
            }
        });
        datePicker.init(year, month, day, new OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear,
                    int dayOfMonth) {

                selectDate = formatDate(year, monthOfYear + 1, dayOfMonth);
                LogUtils.d(TAG, "selectDate==" + selectDate);
            }
        });

        int windowPos[] = popWindowPos(view, contentview);
        int xOff = 0;// 可以自己调整偏移
        windowPos[0] -= xOff;
        pWindow.showAtLocation(view, Gravity.TOP | Gravity.START, windowPos[0],
                windowPos[1]);
    }

    /**
     * 格式化日期
     * 
     * @param year
     * @param month
     * @param day
     * @return
     */
    private String formatDate(int year, int month, int day) {
        String dateString = null;
        if (month < 10) {
            if (day < 10) {
                dateString = year + "-0" + month + "-0" + day;
            } else {
                dateString = year + "-0" + month + "-" + day;
            }
        } else {
            if (day < 10) {
                dateString = year + "-" + month + "-0" + day;
            } else {
                dateString = year + "-" + month + "-" + day;
            }
        }
        return dateString;
    }

    /**
     * 计算两个日期间隔天数
     * 
     * @param startDate
     *            当前日期
     * @param endDate
     *            选择的日期
     * @return 返回正数表示日期已过，负数日期还没有过
     */
    public static int getGapCount(String currentDate, String selectDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = sdf.parse(currentDate);
            date2 = sdf.parse(selectDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // 将转换的两个时间对象转换成Calendard对象
        Calendar can1 = Calendar.getInstance();
        can1.setTime(date1);
        Calendar can2 = Calendar.getInstance();
        can2.setTime(date2);
        // 拿出两个年份
        int year1 = can1.get(Calendar.YEAR);
        int year2 = can2.get(Calendar.YEAR);
        // 天数
        int days = 0;
        Calendar can = null;
        // 如果can1 < can2
        // 减去小的时间在这一年已经过了的天数
        // 加上大的时间已过的天数
        if (can1.before(can2)) {
            days += can1.get(Calendar.DAY_OF_YEAR);
            days -= can2.get(Calendar.DAY_OF_YEAR);

            can = can1;
        } else {
            days -= can2.get(Calendar.DAY_OF_YEAR);
            days += can1.get(Calendar.DAY_OF_YEAR);
            can = can2;
        }
        for (int i = 0; i < Math.abs(year2 - year1); i++) {
            // 获取小的时间当前年的总天数
            days += can.getActualMaximum(Calendar.DAY_OF_YEAR);
            // 再计算下一年。
            can.add(Calendar.YEAR, 1);
        }
        return days;
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
     * 显示轨迹
     */
    private void postDisplayTrack() {
        int days = getGapCount(currentDate, selectDate);
        if (days > 90 || days < 0) {
            Toast.makeText(context, "选择90天以内的日期", Toast.LENGTH_SHORT).show();
        } else {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("token", PreferenceHelper.getString(context, "token"));
            if (!CommonUtil.isOldMode()) {
                map.put("uid", String.valueOf(currentId));
            }
            map.put("createTime", selectDate);
            LogUtils.d(TAG, "postDisplayTrack selectDate=" + selectDate + " token="
                    + PreferenceHelper.getString(context, "token"));
            client.postRequest(Common.task.history, Common.api.history, map);

            client.setResponse(new onResponseCode() {

                @Override
                public void setPostResponse(final String responseData) {
                    Runnable runnable = new Runnable() {

                        @Override
                        public void run() {
                            dealDisplayTrackData(responseData);
                        }
                    };
                    handler.post(runnable);
                }
            });
        }
    }

    /**
     * 处理显示轨迹的响应数据
     * 
     * @param json
     */
    private void dealDisplayTrackData(String json) {
        LogUtils.d(TAG, "dealDisplayTrackData json=" + json);
        if (!CommonUtil.isJsonDataResultEmpty(json)) {
            HistoryTrackGson response = gson.fromJson(json, HistoryTrackGson.class);
            int code = response.getCode();
            String msg = response.getMsg();

            if (code == Common.code.SUCCESS) {
                trackResults = response.getResult();
                if (marker != null) {
                    marker.remove();
                }
                setMarkerInfo(trackResults);
            }
        } else {
            CommonGson response = gson.fromJson(json, CommonGson.class);
            int code = response.getCode();
            if (code == Common.code.DATA_EMPTY) {
                for (Marker marker : markerLists) {
                    if (marker != null) {
                        marker.remove();
                    }
                }
                Toast.makeText(context, getResources().getString(R.string.data_empty),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 创建marker信息
     * 
     * @param trackResults
     */
    private void setMarkerInfo(List<HistoryTrackResult> trackResults) {
        double longitude;
        double latitude;
        String descStreet = null;
        String descLocation = null;
        String createTime;

        // 清除之前的轨迹点
        infos.clear();
        for (HistoryTrackResult result : trackResults) {
            longitude = Double.valueOf(result.getLongitude());
            latitude = Double.valueOf(result.getLatitude());
            createTime = result.getCreateTime();
            descLocation = result.getDescLocation();
            descStreet = result.getDescStreet();
            if (descStreet != null) {
                infos.add(new MarkerInfoUtil(latitude, longitude, descStreet,
                        descLocation,
                        CommonUtil.getDateFromTime(Long.valueOf(createTime))));
            }
        }
        addOverlay(infos);
    }

    // 显示marker
    private void addOverlay(List<MarkerInfoUtil> infos) {

        // 清空地图
        markerLists.clear();
        // 创建marker的显示图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_address_other);
        LatLng latLng = null;
        OverlayOptions options;
        for (MarkerInfoUtil info : infos) {
            // 获取经纬度
            latLng = new LatLng(info.getLatitude(), info.getLongitude());
            // 设置marker
            options = new MarkerOptions().position(latLng)// 设置位置
                    .icon(bitmap)// 设置图标样式
                    .zIndex(9) // 设置marker所在层级
                    .draggable(true); // 设置手势拖拽;
            // 添加marker
            marker = (Marker) addMapOverlay(options);
            markerLists.add(marker);
            // 使用marker携带info信息，当点击事件的时候可以通过marker获得info信息
            Bundle bundle = new Bundle();
            // info必须实现序列化接口
            bundle.putSerializable("info", info);
            marker.setExtraInfo(bundle);
        }
        // 将地图显示在最后一个marker的位置
        animateMapStatus(latLng);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // TODO Auto-generated method stub
        // 从marker中获取info信息
        Bundle bundle = marker.getExtraInfo();
        if (bundle == null) {
            return false;
        }
        MarkerInfoUtil infoUtil = (MarkerInfoUtil) bundle.getSerializable("info");
        // infowindow位置
        LatLng latLng = new LatLng(infoUtil.getLatitude(), infoUtil.getLongitude());
        // infowindow点击事件
        OnInfoWindowClickListener listener = new OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick() {
                // 隐藏infowindow
                hideInfoWindow();
            }
        };
        // infowindow中的布局
        TextView tv = new TextView(HistoryTrackActivity.this);
        tv.setBackgroundResource(R.color.black);
        tv.setPadding(20, 10, 20, 20);
        tv.setTextColor(android.graphics.Color.WHITE);
        LogUtils.d(TAG, "infoUtil.getDescStreet()=" + infoUtil.getDescStreet());
        tv.setText(infoUtil.getDescStreet() + infoUtil.getDescLocation() + "\n"
                + infoUtil.getCreateTime());
        tv.setGravity(Gravity.CENTER);

        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromView(tv);
        // 显示infowindow
        InfoWindow infoWindow = new InfoWindow(bitmapDescriptor, latLng, -120, listener);
        showInfoWindow(infoWindow);

        // popupPointsInfoWindow();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (selectDate == null) {
            selectDate = currentDate;
            postDisplayTrack();
        }
    }

}
