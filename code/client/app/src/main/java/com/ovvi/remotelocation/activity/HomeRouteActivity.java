package com.ovvi.remotelocation.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteLine;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.SuggestAddrInfo;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.baidu.panosdk.plugin.indoor.util.ScreenUtils;
import com.ovvi.remotelocation.R;
import com.ovvi.remotelocation.adapter.AddressListAdapter;
import com.ovvi.remotelocation.base.BaseMapActivity;
import com.ovvi.remotelocation.model.AddressListInfo;
import com.ovvi.remotelocation.utils.LogUtils;
import com.ovvi.remotelocation.utils.PreferenceHelper;

public class HomeRouteActivity extends BaseMapActivity implements OnItemClickListener {

    private static final String TAG = "HomeRouteActivity";

    private LocationApplication mContext;

    private MyOnGetRoutePlanResultListener routePlanListener = new MyOnGetRoutePlanResultListener();
    private RoutePlanSearch routePlanSearch;

    /** 家地址经度 */
    private String homeLongitude;
    /** 家地址维度 */
    private String homeLatiude;

    private LatLng startLatLng;
    private LatLng endLatLng;
    private BDLocation lastLocation;
    private BDLocation location;
    private boolean isFirstLoc = true;

    private EditText editText;
    private Button button;
    private String addressEdit;
    private Marker marker;
    private PopupWindow pWindow;
    private ListView addressListView;
    private List<AddressListInfo> addressList = new ArrayList<AddressListInfo>();
    private AddressListInfo addressInfo;
    private AddressListAdapter addressAdapter;
    /** 驾车路线覆盖物 */
    private DrivingRouteOverlay drivingRouteOverlay;
    /** 步行路线覆盖物 */
    private WalkingRouteOverlay walkingRouteOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        initView();
        mContext = (LocationApplication) getApplicationContext();

        requestLocation();
    }

    private void initView() {
        setTitle(R.string.house_text);
        hideForwardView();
        showDropView();

        routePlanSearch = RoutePlanSearch.newInstance();
    }

    @Override
    public void onLocationResponse(Object obj) {
        // TODO Auto-generated method stub
        if (obj == null) {
            return;
        }
        requestLocationDelayed(1000);

        location = (BDLocation) obj;
        if (lastLocation != null) {
            if (lastLocation.getLatitude() == location.getLatitude()
                    && lastLocation.getLongitude() == location.getLongitude()) {
                return;
            }
        }
        lastLocation = location;
        MyLocationData data = new MyLocationData.Builder().accuracy(0)
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(100).latitude(lastLocation.getLatitude())
                .longitude(lastLocation.getLongitude()).build();

        updateLocationData(data);

        LogUtils.d(
                TAG,
                "location:[" + lastLocation.getLatitude() + ","
                        + lastLocation.getLongitude() + "]");

        homeLatiude = PreferenceHelper.getString(mContext, "latitude");
        homeLongitude = PreferenceHelper.getString(mContext, "longitude");
        LogUtils.d(TAG, "handleMessage homeLatiude==" + homeLatiude + " homeLongitude="
                + homeLongitude);
        if (isFirstLoc) {
            isFirstLoc = false;
            LatLng ll = new LatLng(lastLocation.getLatitude(),
                    lastLocation.getLongitude());
            LogUtils.d(TAG, "handleMessage location:ll==" + ll);
            if (ll == null) {
                return;
            }
            animateMapStatus(ll);
            showPopUpOverlay(ll);
            startLatLng = new LatLng(lastLocation.getLatitude(),
                    lastLocation.getLongitude());
            if (!"".equals(homeLatiude) && !"".equals(homeLongitude)) {
                endLatLng = new LatLng(Double.valueOf(homeLatiude),
                        Double.valueOf(homeLongitude));
                showRoutePlan();
            }
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
    protected void onDestroy() {
        super.onDestroy();
        routePlanSearch.destroy();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.drop) {
            onTitleClicked(v);
        }
    }

    @Override
    protected void onTitleClicked(View forwardView) {
        LogUtils.d(TAG, "Route plan");

        setFamilyAddress(forwardView);
    }

    private void setFamilyAddress(View view) {
        View contentview = LayoutInflater.from(mContext).inflate(
                R.layout.search_address_popup, null);
        editText = (EditText) contentview.findViewById(R.id.address_edit);
        button = (Button) contentview.findViewById(R.id.search_btn);

        addressListView = (ListView) contentview.findViewById(R.id.address_list);
        addressList.clear();
        addressAdapter = new AddressListAdapter(mContext, addressList);
        addressListView.setAdapter(addressAdapter);
        addressListView.setOnItemClickListener(this);

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                addressEdit = editText.getText().toString();
                if (!TextUtils.isEmpty(addressEdit) && lastLocation != null) {
                    addressListView.setVisibility(View.VISIBLE);
                    setAddressPopup(addressEdit);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // button.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // // TODO Auto-generated method stub
        // addressEdit = editText.getText().toString();
        // if (!TextUtils.isEmpty(addressEdit) && lastLocation.getCity() !=
        // null) {
        // addressListView.setVisibility(View.VISIBLE);
        // setAddressPopup(addressEdit);
        // }
        // }
        // });

        pWindow = new PopupWindow(contentview, LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT, true);
        pWindow.setFocusable(true);
        pWindow.setBackgroundDrawable(new BitmapDrawable(null, ""));

        int windowPos[] = popWindowPos(view, contentview);
        int xOff = 20;// 可以自己调整偏移
        windowPos[0] -= xOff;
        pWindow.showAtLocation(view, Gravity.TOP | Gravity.START, windowPos[0],
                windowPos[1]);

    }

    /**
     * 设置地址
     * 
     * @param address
     */
    private void setAddressPopup(final String address) {
        LogUtils.d(TAG, "setAddressPopup address=" + address);
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
                    Log.e("aaaa", "info.city" + info.city + "info.district"
                            + info.district + "info.key" + info.key + "  info.pt="
                            + info.pt);
                    addressInfo = new AddressListInfo(info.key, info.pt.latitude,
                            info.pt.longitude);
                    addressList.add(addressInfo);
                }
                addressAdapter.notifyDataSetChanged();
            }
        });
        SuggestionSearchOption option = new SuggestionSearchOption();
        option.keyword(address);
        option.city(lastLocation.getCity());
        msearch.requestSuggestion(option);
    }

    /**
     * 
     * @param anchorView
     *            呼出window的view
     * @param contentView
     *            window的内容布局
     * @return window显示的左上角的xOff,yOff坐标
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

    private void showRoutePlan() {
        routePlanSearch.setOnGetRoutePlanResultListener(routePlanListener);
        startLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        PlanNode start = PlanNode.withLocation(new LatLng(lastLocation.getLatitude(),
                lastLocation.getLongitude()));
        PlanNode end = PlanNode.withLocation(endLatLng);
        // /**
        // * 路线规划策略，枚举类型：躲避拥堵、最短距离、较少费用、时间优先 ECAR_TIME_FIRST：时间优先
        // * ECAR_AVOID_JAM：躲避拥堵 ECAR_DIS_FIRST：最短距离 ECAR_FEE_FIRST：较少费用
        // */
        DrivingRoutePlanOption dPlanOption = new DrivingRoutePlanOption();
        dPlanOption.from(start);
        dPlanOption.to(end);
        dPlanOption.policy(DrivingRoutePlanOption.DrivingPolicy.ECAR_TIME_FIRST);
        routePlanSearch.drivingSearch(dPlanOption);// 查询

        // 步行
        // WalkingRoutePlanOption walkOption = new WalkingRoutePlanOption();
        // walkOption.from(start);
        // walkOption.to(end);
        // routePlanSearch.walkingSearch(walkOption);

    }

    /**
     * 路线规划监听
     * 
     * @author chensong
     * 
     */
    private class MyOnGetRoutePlanResultListener implements OnGetRoutePlanResultListener {

        @Override
        public void onGetBikingRouteResult(BikingRouteResult getBikingRouteResult) {
        }

        /** 驾车路线 */
        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                return;
            }
            if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                // result.getSuggestAddrInfo()
                LogUtils.d(TAG, " start or end error");
                return;
            }
            LogUtils.d(TAG, "DrivingRouteResult  result.error=" + result.error
                    + "  endLatLng=" + endLatLng);
            if (result.error == SearchResult.ERRORNO.PERMISSION_UNFINISHED) {
                // 权限鉴定未完成则再次尝试
                if (endLatLng != null) {
                    startSearch(startLatLng, endLatLng);
                }
                return;
            }

            if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                DrivingRouteLine routeLine = result.getRouteLines().get(0);
                drivingRouteOverlay = new MyDrivingRouteOverlay(getBaiduMap());
                getBaiduMap().setOnMarkerClickListener(drivingRouteOverlay);
                drivingRouteOverlay.setData(result.getRouteLines().get(0));
                drivingRouteOverlay.addToMap();
                drivingRouteOverlay.zoomToSpan();
            }

        }

        @Override
        public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

        }

        @Override
        public void onGetMassTransitRouteResult(MassTransitRouteResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                return;
            }
            if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                // result.getSuggestAddrInfo()
                return;
            }
            // result.getOrigin();//起点，含：坐标，城市信息等
            result.getDestination();// 终点
            List<MassTransitRouteLine> massTransitRouteLines = result.getRouteLines();
            for (MassTransitRouteLine massTransitRouteLine : massTransitRouteLines) {// 多条规划路线
                // massTransitRouteLine包含本线路的票价、到达时间等信息
                List<MassTransitRouteLine.TransitStep> transitSteps = massTransitRouteLine
                        .getAllStep();// 多个路段
                // TransitStep包含：本路段始终点、交通方式等信息
            }

        }

        @Override
        public void onGetTransitRouteResult(TransitRouteResult result) {// 公交路线规划回调
            String routePlanStr = null;
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(mContext, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
            }
            if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                SuggestAddrInfo mysuggest = result.getSuggestAddrInfo();
                List<CityInfo> cityInfos = mysuggest.getSuggestStartCity();
                Log.i("Tag", cityInfos.toString());
                return;
            }
            if (result.error == SearchResult.ERRORNO.NO_ERROR) {

                TransitRouteLine route = result.getRouteLines().get(0);

                TransitRouteOverlay overlay = new TransitRouteOverlay(getBaiduMap());
                overlay.setData(result.getRouteLines().get(1));
                overlay.addToMap();
                for (int i = 0; i < route.getAllStep().size(); i++) {
                    Object step = route.getAllStep().get(i);
                    routePlanStr += ((TransitRouteLine.TransitStep) step)
                            .getInstructions() + "\n";
                }

            }
            Toast.makeText(mContext, routePlanStr, Toast.LENGTH_SHORT).show();

        }

        /** 步行路线 */
        @Override
        public void onGetWalkingRouteResult(WalkingRouteResult result) {
            LogUtils.d(TAG, "tWalkingRouteResult result =" + result + "  result.error="
                    + result.error);
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                return;
            }
            if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                // result.getSuggestAddrInfo()
                return;
            }

            walkingRouteOverlay = new WalkingRouteOverlay(getBaiduMap());
            getBaiduMap().setOnMarkerClickListener(walkingRouteOverlay);
            walkingRouteOverlay.setData(result.getRouteLines().get(0));
            walkingRouteOverlay.addToMap();
            walkingRouteOverlay.zoomToSpan();

            /*
             * // 获取规划路线列表 List<WalkingRouteLine> walkingRouteLines =
             * result.getRouteLines(); // 多条规划路线 for (WalkingRouteLine
             * walkingRouteLine : walkingRouteLines) {
             * 
             * walkingRouteLine.getDistance();// 该路线距离
             * walkingRouteLine.getDuration();// 时长
             * walkingRouteLine.getStarting();// 获取起点
             * walkingRouteLine.getTerminal();// 获取终点
             * List<WalkingRouteLine.WalkingStep> walkingSteps =
             * walkingRouteLine .getAllStep();// 每条规划路线含多个路段 for
             * (WalkingRouteLine.WalkingStep walkingStep : walkingSteps) {
             * walkingStep.getEntrance();// 该步段起点 walkingStep.getExit();// 终点
             * walkingStep.getWayPoints();// 该步段起点与终点中间的点，步段有可能不是直线，折线的话有多个点 } }
             */

        }
    }

    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {
        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public int getLineColor() {
            // 蓝色的路径
            return Color.BLUE;
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            // 自定义的起点图标
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_address_me);
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            // 自定义的终点图标
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_address_home);
        }
    }

    private void startSearch(LatLng startLatLng, LatLng endLatLng) {
        PlanNode stNode = PlanNode.withLocation(startLatLng);
        PlanNode enNode = PlanNode.withLocation(endLatLng);
        routePlanSearch.drivingSearch((new DrivingRoutePlanOption()).from(stNode).to(
                enNode));
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
        AddressListInfo info = addressList.get(index);
        editText.setText(info.getAddressDesc());

        PreferenceHelper.putString(mContext, "longitude",
                String.valueOf(info.getLongitude()));
        PreferenceHelper.putString(mContext, "latitude",
                String.valueOf(info.getLatidude()));

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        addressListView.setVisibility(View.GONE);

        homeLatiude = String.valueOf(info.getLatidude());
        homeLongitude = String.valueOf(info.getLongitude());
        PreferenceHelper
        .putString(mContext, "home_address", info.getAddressDesc());
        if (!"".equals(homeLatiude) && !"".equals(homeLongitude)) {
            endLatLng = new LatLng(Double.valueOf(homeLatiude),
                    Double.valueOf(homeLongitude));
            homeAddressOverlay(new LatLng(info.getLatidude(), info.getLongitude()));
        }
    }

    /**
     * 家地址覆盖物
     * 
     * @param latLng
     */
    private void homeAddressOverlay(LatLng latLng) {

        if (marker != null) {
            marker.remove();
        }
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_address_home);
        // 构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions().position(latLng).icon(bitmap);
        // 在地图上添加Marker，并显示
        marker = (Marker) addMapOverlay(option);
        animateMapStatus(latLng);
        if (walkingRouteOverlay != null) {
            walkingRouteOverlay.removeFromMap();
        }
        if (drivingRouteOverlay != null) {
            drivingRouteOverlay.removeFromMap();
        }
        showRoutePlan();
    }
}
