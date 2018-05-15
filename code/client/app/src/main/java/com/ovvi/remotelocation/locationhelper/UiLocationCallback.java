package com.ovvi.remotelocation.locationhelper;

public interface UiLocationCallback {
	/** UI回调：向LocationService请求定位 */
	public void onRequestLocation();
	/** UI回调：LocationService返回定位结果 */
	public void onLocationResponse(Object obj);
}
