package com.ovvi.remotelocation.service;

import java.util.ArrayList;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
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

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class TestActivity extends BaseMapActivity implements OnClickListener, OnItemClickListener {

	private static final String TAG = "FamilyAddressActivity";
	private Context mContext;
	/** 弹出搜索框 */
	private PopupWindow pWindow;
	/** 弹出地址结果 */
	private PopupWindow aWindow;
	/** 设为家庭地址按钮 */
	private EditText editText;
	private ListView addressListView;

//	private String addressEdit;

	/** 首次定位 */
	private boolean isFirstLoc = true;
	private BDLocation lastLocation;
	private BDLocation location;

	private List<AddressListInfo> addressList = new ArrayList<AddressListInfo>();
	private AddressListInfo addressInfo;
	private AddressListAdapter addressAdapter;

	private Marker marker;
	private String homeLatiude;
	private String homeLongitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		hideForwardView();
		showDropView();
		showBackwardView(R.drawable.top_bar_back);
		setTitle(R.string.address_text);

		mContext = this;
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		Bundle bundle = marker.getExtraInfo();
		if (bundle == null) {
			return false;
		}
		if (addressInfo != null) {
			saveHomeAddress();
		}
		return true;
	}

	private void showPopUpOverlay(LatLng latLng) {

		BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_address_me);
		// 构建MarkerOption，用于在地图上添加Marker
		OverlayOptions option = new MarkerOptions().position(latLng).icon(bitmap);
		// 在地图上添加Marker，并显示
		addMapOverlay(option);
	}

	@Override
	protected void onResume() {
		super.onResume();
		homeLatiude = PreferenceHelper.getString(mContext, "latitude");
		homeLongitude = PreferenceHelper.getString(mContext, "longitude");

		if (!"".equals(homeLatiude) && !"".equals(homeLongitude)) {
			addhomeAddressOverlay(new LatLng(Double.valueOf(homeLatiude), Double.valueOf(homeLongitude)));
		}
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
			showSearchPopupWindow(v);
			break;

		}
	}

	protected void showSearchPopupWindow(View view) {
		LogUtils.d(TAG, "showSearchPopupWindow ");
		View contentview = LayoutInflater.from(mContext).inflate(R.layout.search_address_popup, null);
		editText = (EditText) contentview.findViewById(R.id.address_edit);
		addressListView = (ListView) contentview.findViewById(R.id.address_list);
		addressList.clear();
		addressAdapter = new AddressListAdapter(mContext, addressList);
		addressListView.setAdapter(addressAdapter);
		addressListView.setOnItemClickListener(this);

		editText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (!TextUtils.isEmpty(s.toString()) && lastLocation != null) {
					addressListView.setVisibility(View.VISIBLE);
					setAddressPopup(s.toString());
				}
			}
		});

		pWindow = new PopupWindow(contentview, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
		pWindow.setFocusable(true);
		pWindow.setBackgroundDrawable(new BitmapDrawable(null, ""));

		int windowPos[] = popWindowPos(view, contentview);
		int xOff = 20;// 可以自己调整偏移
		windowPos[0] -= xOff;
		pWindow.showAtLocation(view, Gravity.TOP | Gravity.START, -200, windowPos[1]);
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

					if (info.pt == null) {
						continue;
					}
					addressList.add(new AddressListInfo(info.key, info.pt.latitude, info.pt.longitude));
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
	 * @param mView
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
		LogUtils.d(TAG, "onItemClick index=" + index);
		addressInfo = addressList.get(index);
		editText.setText(addressInfo.getAddressDesc());
		saveHomeAddress();
		addhomeAddressOverlay(new LatLng(addressInfo.getLatidude(), addressInfo.getLongitude()));
		addressListView.setVisibility(View.GONE);
	}

	/**
	 * 家地址覆盖物
	 * 
	 * @param latLng
	 */
	private void addhomeAddressOverlay(LatLng latLng) {
		LogUtils.d(TAG, "homeAddressOverlay marker=" + marker);
		if (marker != null) {
			marker.remove();
		}
		BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_address_home);
		// 构建MarkerOption，用于在地图上添加Marker
		OverlayOptions option = new MarkerOptions().position(latLng).icon(bitmap);
		// 在地图上添加Marker，并显示
		marker = (Marker) getBaiduMap().addOverlay(option);
		Bundle bundle = new Bundle();
		bundle.putSerializable("marker", "marker");
		marker.setExtraInfo(bundle);
		animateMapStatus(latLng);
	}

	/**
	 * 保存家地址
	 * 
	 * @param info
	 */
	protected void saveHomeAddress() {
		LogUtils.d(TAG, "saveHomeAddress addressInfo=" + addressInfo);
		View popupWindowView = getLayoutInflater().inflate(R.layout.home_address_set, null);
		aWindow = new PopupWindow(popupWindowView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
		aWindow.setAnimationStyle(R.style.AnimationBottomFade);
		ColorDrawable dw = new ColorDrawable(0xffffffff);
		aWindow.setBackgroundDrawable(dw);
		aWindow.showAtLocation(getLayoutInflater().inflate(R.layout.home_address_layout, null),
				Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

		popupWindowView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return false;
			}

		});
		TextView address = (TextView) popupWindowView.findViewById(R.id.home_address);
		LinearLayout setAddress = (LinearLayout) popupWindowView.findViewById(R.id.set_address);
		address.setText(addressInfo.getAddressDesc());
		setAddress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PreferenceHelper.putString(mContext, "longitude", String.valueOf(addressInfo.getLongitude()));
				PreferenceHelper.putString(mContext, "latitude", String.valueOf(addressInfo.getLatidude()));
				if (aWindow.isShowing()) {
					aWindow.dismiss();
				}
			}
		});
	}

	@Override
	public void onRequestLocation() {
		super.onRequestLocation();
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
				.direction(100).latitude(location.getLatitude()).longitude(location.getLongitude()).build();

		updateLocationData(data);

		lastLocation = location;
		LogUtils.d(TAG, "location:[" + location.getLatitude() + "," + location.getLongitude() + "]");
		LogUtils.d(TAG, "handleMessage isFirstLoc=" + isFirstLoc);
		if (isFirstLoc) {
			isFirstLoc = false;
			LatLng ll = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
			LogUtils.d(TAG, " location.getLatitude()=" + lastLocation.getLatitude() + " location.getLongitude()="
					+ lastLocation.getLongitude());
			// 没有设置家地址时显示当前位置
			if ("".equals(homeLatiude) && "".equals(homeLongitude)) {
				animateMapStatus(ll);
				showPopUpOverlay(ll);
			}
		}
	}
}
