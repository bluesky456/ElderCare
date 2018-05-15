package com.ovvi.remotelocation.constants;

/**
 * @author lantian
 *
 */
public class Constant {
	public static final String PREFERENCE_NOTICE_FILE = "preference-notice";
	public static final String PREFERENCE_ADD_FAMILY_KEY = "add_family_key";
	public static final int NOTIFICATION_ADD_FAMILY_NOTICE = 100;
	
	public static final int MSG_BASE = 1000;
	/** 本地Mapview Activity定位消息通知,通知方向:MapView Activity => LocationService*/
    public static final int MSG_LOCATION_REQUEST_FROM_LOCAL = MSG_BASE + 1;
    /** 本地Mapview Activity定位消息响应,通知方向:LocationService => MapView Activity*/
    public static final int MSG_LOCATION_RESPONSE_TO_LOCAL = MSG_BASE + 2;
    /** 服务端定位push消息通知,通知方向:server => LocationService*/
    public static final int MSG_LOCATION_REQUEST_FROM_SERVER = MSG_BASE + 3;
    /** 服务端定位push消息响应,通知方向:LocationService => server*/
    public static final int MSG_LOCATION_RESPONSE_TO_SERVER = MSG_BASE + 4;
    /** LocationService 返回消息：网络异常，UI侧直接给Toast提示即可*/
    public static final int MSG_LOCATION_RESPONSE_NETWORK_NOT_AVAILABLE = MSG_BASE + 5;
    /** 服务端添加成员push消息响应,通知方向:MessageService => server*/
    public static final int MSG_ADD_FAMILY_RESPONSE_TO_SERVER = MSG_BASE + 6;
    
    /** MessageService唤醒action*/
    public static final String ACTION_MESSAGE_SERVICE_WAKEUP = "com.ovvi.intent.action.MESSAGE_SERVICE_WAKEUP";
    /** LocationService启动action*/
    public static final String ACTION_ENABLE_LOCATION_SERVICE = "com.ovvi.intent.action.ENABLE_LOCATION_SERVICE";
    /** MessageService启动action*/
    public static final String ACTION_ENABLE_MESSAGE_SERVICE = "com.ovvi.intent.action.ENABLE_MESSAGE_SERVICE";
    /** action show notice*/
    public static final String ACTION_SHOW_NOTICE = "android.intent.action.SHOW_NOTICE";
    /** notice处理完成广播 */
    public static final String ACTION_REPORT_NOTICE = "android.intent.action.REPORT_NOTICE";
    
}
