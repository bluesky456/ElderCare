package com.ovvi.remotelocation.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.Process;
import android.os.RemoteException;
import android.util.ArraySet;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.ovvi.remotelocation.bean.MessengerBean;
import com.ovvi.remotelocation.constants.Constant;
import com.ovvi.remotelocation.utils.LogUtils;
import com.ovvi.remotelocation.utils.NetUtils;
import com.ovvi.remotelocation.utils.PreferenceHelper;

/**
 * @author lantian
 *
 */
public class LocationService extends Service {
	private final static String TAG = "LocationService";
	private Messenger serviceMessenger = new Messenger(new MessengerHandler());
	LocationClient mLocationClient;
	private LocationServiceHandler mServiceHandler;
	private Looper mServiceLooper;
	ArraySet<MessengerBean> messengerTargets = new ArraySet<MessengerBean>();
	private PowerManager.WakeLock mWakeLock;
	private Context mContext;
	/** 定位完成消息 */
	public static final int MSG_LOCATION_RECEIVED = 1001;

	/** LocationService运行状态*/
    private static LocationServiceState locationServiceState = LocationServiceState.LOCATION_SERVICE_IS_PAUSED;
    enum LocationServiceState {
    	LOCATION_SERVICE_IS_PAUSED,
    	LOCATION_SERVICE_IS_RUNNING
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        HandlerThread thread = new HandlerThread(LocationService.class.getName(),
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new LocationServiceHandler(mServiceLooper);
		mLocationClient = new LocationClient(getApplicationContext());
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return serviceMessenger.getBinder();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtils.d(TAG, "onStartCommand");
//		if (!NetUtils.isNetworkAvailable(this.getApplicationContext())) {
//			LogUtils.d(TAG, "Network not available!");
//			return -1;
//		}
//
//        Message msg = mServiceHandler.obtainMessage();
//        msg.arg1 = startId;
//        msg.obj = intent;
//        mServiceHandler.sendMessage(msg);
        return Service.START_STICKY;
    }

    public LocationClientOption getDefaultLocationClientOption() {
        LocationClientOption mOption = new LocationClientOption();
		mOption.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        mOption.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        mOption.setScanSpan(3000);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        mOption.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
        mOption.setIsNeedLocationDescribe(true);// 可选，设置是否需要地址描述
        mOption.setNeedDeviceDirect(false);// 可选，设置是否需要设备方向结果
        mOption.setLocationNotify(false);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        mOption.setIgnoreKillProcess(true);// 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        mOption.setIsNeedLocationDescribe(true);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        mOption.setIsNeedLocationPoiList(true);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        mOption.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集

        mOption.setIsNeedAltitude(false);// 可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用

        return mOption;
    }

    private void enableLocationClient() {
        // mLocationClient.registerNotify(myListener);
        // 配置定位
        LocationClientOption option = new LocationClientOption();
        int locationMode = PreferenceHelper.getInt(mContext, "locationMode");
        switch (locationMode) {
        case 0:
            option.setOpenGps(true);// 打开Gps
            option.setLocationMode(com.baidu.location.LocationClientOption.LocationMode.Hight_Accuracy);
            break;
        case 1:
            option.setOpenGps(true);// 打开Gps
            option.setLocationMode(com.baidu.location.LocationClientOption.LocationMode.Device_Sensors);
            break;
        case 2:
            option.setOpenGps(false);
            option.setLocationMode(com.baidu.location.LocationClientOption.LocationMode.Battery_Saving);
            break;

        default:
            break;
        }
        option.setCoorType("bd09ll");// 坐标类型
        option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
        option.setIsNeedLocationDescribe(true);
        option.setScanSpan(3000);// 1000毫秒定位一次
        option.setLocationNotify(true);
        option.setIsNeedLocationPoiList(true);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(mBDLocationListener);
        mLocationClient.start();
	}
	
    private class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case Constant.MSG_LOCATION_REQUEST_FROM_LOCAL:
                case Constant.MSG_LOCATION_REQUEST_FROM_SERVER:
                	LogUtils.d(TAG, "handleMessage:msg.what:" + msg.what);
                	
                	// 没有网络时直接返回异常，是否需要取决于业务需求是否需要无网络定位，后续评估。
                	if (!NetUtils.isNetworkAvailable(getApplicationContext())) {
            			LogUtils.d(TAG, "Network not available!");
            			try {
            				Message replyMsg = Message.obtain(null, Constant.MSG_LOCATION_RESPONSE_NETWORK_NOT_AVAILABLE);
							msg.replyTo.send(replyMsg);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
            			return;
            		}
                	
                	// 生成响应的MessengerBean对象保存起来。
                	MessengerBean bean = new MessengerBean();
                	bean.messenger = msg.replyTo;
                	bean.targetId = msg.what + 1;
                	synchronized (messengerTargets) {
                		if (!messengerTargets.contains(bean)) {
                			messengerTargets.add(bean);
                		}
                	}
                	
                	//启动定位
                	if (LocationServiceState.LOCATION_SERVICE_IS_PAUSED == locationServiceState) {
                		acquireWakeLock(mContext);
                		enableLocationClient();
                		locationServiceState = LocationServiceState.LOCATION_SERVICE_IS_RUNNING;
					}
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
    
    /**
     * 获取唤醒锁，定位过程需要持续唤醒
     * @param context 上下文
     */
    synchronized private void acquireWakeLock(Context context){
    	releaseWakeLock();
    	LogUtils.d(TAG, "releaseWakeLock first before acquire");

        if(null == mWakeLock){
            PowerManager power = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = power.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LocationService.class.getName());
            mWakeLock.setReferenceCounted(false);
            mWakeLock.acquire(10000); //保险起见，使用超时锁
            LogUtils.d(TAG, "acquireWakeLock");
        }
    }
    
    //  同步方法,释放唤醒锁
    synchronized private void releaseWakeLock(){
        if(null != mWakeLock){
            if(mWakeLock.isHeld()) {
                mWakeLock.release();
                LogUtils.d(TAG,"releaseWakeLock");
            }

            mWakeLock=null;
        }
    }
    
	/*****
	 *
	 * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
	 *
	 */
	private BDAbstractLocationListener mBDLocationListener = new BDAbstractLocationListener() {

		@Override
		public void onReceiveLocation(BDLocation location) {
			
			Message message = mServiceHandler.obtainMessage(MSG_LOCATION_RECEIVED);
			mServiceHandler.sendMessage(message);
			
			if (null != location && location.getLocType() != BDLocation.TypeServerError) {
				LogUtils.d(TAG, "location fixed!");
				locationReceived(location);
//
//				StringBuffer sb = new StringBuffer(256);
//				sb.append("time : ");
//				/**
//				 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
//				 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
//				 */
//				sb.append(location.getTime());
//				sb.append(", locType:");// 定位类型
//				sb.append(location.getLocType());
//				sb.append(", locType description:");// *****对应的定位类型说明*****
//               sb.append(location.getLocTypeDescription());
//				sb.append(", latitude:");// 纬度
//				sb.append(location.getLatitude());
//				sb.append(", lontitude:");// 经度
//				sb.append(location.getLongitude());
//				sb.append(", radius:");// 半径
//				sb.append(location.getRadius());
//				sb.append(", CountryCode:");// 国家码
//				sb.append(location.getCountryCode());
//				sb.append(", Country:");// 国家名称
//				sb.append(location.getCountry());
//				sb.append(", citycode:");// 城市编码
//				sb.append(location.getCityCode());
//				sb.append(", city:");// 城市
//				sb.append(location.getCity());
//				sb.append(", District:");// 区
//				sb.append(location.getDistrict());
//				sb.append(", Street:");// 街道
//				sb.append(location.getStreet());
//				sb.append(", addr:");// 地址信息
//				sb.append(location.getAddrStr());
//				sb.append(", UserIndoorState:");// *****返回用户室内外判断结果*****
//				sb.append(location.getUserIndoorState());
//				sb.append(", Direction(not all devices have value):");
//				sb.append(location.getDirection());// 方向
//				sb.append(", locationdescribe:");
//               sb.append(location.getLocationDescribe());// 位置语义化信息
//				sb.append(", Poi:");// POI信息
//				if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
//					for (int i = 0; i < location.getPoiList().size(); i++) {
//						Poi poi = (Poi) location.getPoiList().get(i);
//						sb.append(poi.getName() + ";");
//					}
//				}
//				if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
//					sb.append(", speed:");
//					sb.append(location.getSpeed());// 速度 单位：km/h
//					sb.append(", satellite:");
//					sb.append(location.getSatelliteNumber());// 卫星数目
//					sb.append(", height:");
//					sb.append(location.getAltitude());// 海拔高度 单位：米
//					sb.append(", gps status:");
//                   sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
//					sb.append(", describe:");
//					sb.append("gps定位成功");
//				} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
//					// 运营商信息
//				    if (location.hasAltitude()) {// *****如果有海拔高度*****
//				        sb.append(", height:");
//	                    sb.append(location.getAltitude());// 单位：米
//				    }
//					sb.append(", operationers:");// 运营商信息
//					sb.append(location.getOperators());
//					sb.append(", describe:");
//					sb.append("网络定位成功");
//				} else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
//					sb.append(", describe:");
//					sb.append("离线定位成功，离线定位结果也是有效的");
//				} else if (location.getLocType() == BDLocation.TypeServerError) {
//					sb.append(", describe:");
//					sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
//				} else if (location.getLocType() == BDLocation.TypeNetWorkException) {
//					sb.append(", describe:");
//					sb.append("网络不同导致定位失败，请检查网络是否通畅");
//				} else if (location.getLocType() == BDLocation.TypeCriteriaException) {
//					sb.append(", describe:");
//					sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
//				}
//				LogUtils.d(TAG, sb.toString());
			}
		}

	};

	private void locationReceived(BDLocation location) {
		if (null != mLocationClient) {
			mLocationClient.stop();
		}

		synchronized (messengerTargets) {
			for (MessengerBean bean : messengerTargets) {
				if (null != bean && null != bean.messenger) {

					try {
						Message replyMsg = Message.obtain(null, bean.targetId, location);
						bean.messenger.send(replyMsg);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}

			messengerTargets.clear();
		}
	}
	
	public class LocationServiceHandler extends Handler {
		public LocationServiceHandler(Looper looper) {
			super();
		}

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case MSG_LOCATION_RECEIVED:
				releaseWakeLock();
				locationServiceState = LocationServiceState.LOCATION_SERVICE_IS_PAUSED;
				break;
			}
		}
	}
}
