package com.ovvi.remotelocation.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.baidu.location.BDLocation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ovvi.remotelocation.R;
import com.ovvi.remotelocation.base.Common;
import com.ovvi.remotelocation.bean.Notice;
import com.ovvi.remotelocation.constants.Constant;
import com.ovvi.remotelocation.gson.CommonGson;
import com.ovvi.remotelocation.gson.NoticeReportGson;
import com.ovvi.remotelocation.gson.NoticesGson;
import com.ovvi.remotelocation.model.logic.RemoteLocationLogicManager;
import com.ovvi.remotelocation.model.logic.RemoteLocationLogicPolicy;
import com.ovvi.remotelocation.utils.CommonUtil;
import com.ovvi.remotelocation.utils.DateUtils;
import com.ovvi.remotelocation.utils.HttpUtil;
import com.ovvi.remotelocation.utils.HttpUtil.onResponseCode;
import com.ovvi.remotelocation.utils.LogUtils;
import com.ovvi.remotelocation.utils.NetUtils;
import com.ovvi.remotelocation.utils.PreferenceHelper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.ArrayMap;

public class MessageService extends Service {

    private final static String TAG = "MessageService";
    /** 远程定位消息处理通知 */
    private static final int MSG_PROCESS_LOCATION_NOTICE = 1001;
    /** MessageService使能消息 */
    private static final int MSG_ENABLE_MESSAGE_SERVICE = 1002;
    private static final int MSG_INIT_REQUESTALARM = 1003;
    private MessageServiceHandler mServiceHandler;
    private Looper mServiceLooper;
    private HttpUtil httpClient;
    private Context mContext;
    AlarmManager mAlarmManager;
    PendingIntent mAlarmPending;
    ArrayMap<Integer, Notice> noticePendings = new ArrayMap<Integer, Notice>();
    NoticeProcessor processor = new NoticeProcessor();

    /** 获取push消息的间隔，默认20秒，灭屏时为120秒 */
    private static int requestInterval = 10 * 1000;
    private BroadcastReceiver mIntentReceiver;

    /** LocationService消息句柄 */
    private Messenger locationMessenger;
    private Gson gson;

    private Messenger serviceMessenger = new Messenger(new MessengerHandler());

    // 以bind形式开启service，故有ServiceConnection接收回调
    ServiceConnection mConnLocationService = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtils.d(TAG, "onServiceConnected");
            locationMessenger = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            locationMessenger = null;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        httpClient = new HttpUtil(mContext);
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        gson = new GsonBuilder().create();
        HandlerThread thread = new HandlerThread(MessageService.class.getName(),
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new MessageServiceHandler(mServiceLooper);
        initReceiverCatcher();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.i(TAG, "onStartCommand: intent.getaction()=" + intent.getAction()
                + ", startId=" + startId);

        if (!NetUtils.isNetworkAvailable(this.getApplicationContext())) {
            LogUtils.d(TAG, "Network not available!");
            Message message = mServiceHandler.obtainMessage(MSG_INIT_REQUESTALARM);
			mServiceHandler.sendMessage(message);
            return Service.START_REDELIVER_INTENT;
        }

        if (Constant.ACTION_ENABLE_MESSAGE_SERVICE.equals(intent.getAction())) {
            Message msg = mServiceHandler.obtainMessage(MSG_ENABLE_MESSAGE_SERVICE);
            mServiceHandler.sendMessage(msg);
        }
        return Service.START_REDELIVER_INTENT;
    }

    public class MessageServiceHandler extends BaseServiceHandler {
        public MessageServiceHandler(Looper looper) {
            super();
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
            case MSG_ENABLE_MESSAGE_SERVICE:
                enableMessageService(mContext);

                // 重新设置alarm,触发下次唤醒
                Message message = mServiceHandler.obtainMessage(MSG_INIT_REQUESTALARM);
                mServiceHandler.sendMessage(message);
                break;

            case MSG_INIT_REQUESTALARM:
                // 重新设置alarm
                initRequestAlarm(mContext, false);
                break;

            case MSG_PROCESS_LOCATION_NOTICE:
                if (null == locationMessenger) {
                    enableLocationService(mContext);
                    Message msg1 = mServiceHandler
                            .obtainMessage(MSG_ENABLE_MESSAGE_SERVICE);
                    mServiceHandler.sendMessageDelayed(msg1, 1000);
                } else {
                    LogUtils.d(TAG, "send MSG_LOCATION_REQUEST_FROM_SERVER");
                    Message msg2 = Message.obtain(null,
                            Constant.MSG_LOCATION_REQUEST_FROM_SERVER);
                    msg2.replyTo = serviceMessenger;
                    try {
                        locationMessenger.send(msg2);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;

            default:
                break;
            }
        }
    }

    /**
     * 初始化广播
     */
    private void initReceiverCatcher() {
        LogUtils.d(TAG, "initReceiverCatcher()");
        // 定义意图过滤器
        IntentFilter filter = new IntentFilter();
        // 屏幕灭屏广播
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        // 日期修改
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        // 关闭广播
        filter.addAction(Intent.ACTION_SHUTDOWN);
        // 屏幕高亮广播
        filter.addAction(Intent.ACTION_SCREEN_ON);
        // 屏幕解锁广播
        filter.addAction(Intent.ACTION_USER_PRESENT);
        // 通知处理完成广播
        filter.addAction(Constant.ACTION_REPORT_NOTICE);

        if (null == mIntentReceiver) {
            mIntentReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    LogUtils.d(TAG, "action:" + action);
                    if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                        // 灭屏后改为120秒一次唤醒
                        if (10 * 1000 != requestInterval) {
                            requestInterval = 10 * 1000;
                            initRequestAlarm(context, false);
                        }

                    } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                        if (10 * 1000 != requestInterval) {
                            requestInterval = 10 * 1000;
                            initRequestAlarm(context, true);
                        }
                    } else if (Intent.ACTION_SHUTDOWN.equals(action)) {
                        releaseRequestAlarm();
                    } else if (Intent.ACTION_TIME_CHANGED.equals(action)
                            || Intent.ACTION_LOCALE_CHANGED.equals(action)) {
                        initRequestAlarm(context, true);
                    } else if (Constant.ACTION_REPORT_NOTICE.equals(action)) {
                        // 通知处理完成，需要report to server
                        Message msg = Message.obtain();
                        msg.what = Constant.MSG_ADD_FAMILY_RESPONSE_TO_SERVER;
                        try {
                            serviceMessenger.send(msg);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            registerReceiver(mIntentReceiver, filter);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return serviceMessenger.getBinder();
    }

    /**
     * @param context
     *            上下文
     * @param force
     *            是否按照间隔时间强制设置alarm, 例如当用户操作过程中可强制设置alarm, 待机灭屏状态下最好为false,
     *            特别是半夜时
     */
    private void initRequestAlarm(Context context, boolean force) {
        LogUtils.d(TAG, "initRequestAlarm()");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis((System.currentTimeMillis()));
        LogUtils.d(TAG, "nowTime:" + calendar.getTime().toString());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        long triggerAtTime;
        if (force || (hour <= 23 && hour >= 6)) {
            triggerAtTime = SystemClock.elapsedRealtime() + requestInterval;
        } else {
            // 半夜时段，自动唤醒闹钟设置逻辑
            long nowTime = calendar.getTimeInMillis();
            LogUtils.d(TAG, "getSixToday:" + DateUtils.getSixToday().toString()
                    + ",getSixTomorrow:" + DateUtils.getSixTomorrow().toString());
            // 当前时间大于今天6点，则下次闹钟设置在明天早上6点；如果当前时间小于等于今天6点，则下次闹钟设置在今天6点。
            triggerAtTime = (nowTime > DateUtils.getSixToday().getTime()) ? DateUtils
                    .getSixTomorrow().getTime() : DateUtils.getSixToday().getTime();
        }
        LogUtils.d(TAG, "triggerAtTime:" + new Date(triggerAtTime));
        releaseRequestAlarm();
        mAlarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime,
                getRequestPending());
    }

    private void releaseRequestAlarm() {
        if (null != mAlarmManager) {
            mAlarmManager.cancel(getRequestPending());
        }
    }

    private PendingIntent getRequestPending() {
        if (null == mAlarmPending) {
            Intent intent = new Intent(mContext, MessageAlarmReceiver.class)
                    .setAction(Constant.ACTION_MESSAGE_SERVICE_WAKEUP);
            mAlarmPending = PendingIntent.getBroadcast(mContext, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

        return mAlarmPending;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        releaseRequestAlarm();
        if (null != mConnLocationService) {
            mContext.unbindService(mConnLocationService);
            mConnLocationService = null;
        }
    }

    private void enableMessageService(Context context) {
        requestNotice();
    }

    private void requestNotice() {
        LogUtils.d(TAG, "requestNotice()");
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("token", PreferenceHelper.getString(getApplicationContext(), "token"));
        httpClient.setResponse(new onResponseCode() {

            @Override
            public void setPostResponse(final String responseData) {
                Runnable runnable = new Runnable() {

                    @Override
                    public void run() {
                        dealReceivedNotice(responseData);
                    }
                };

                mServiceHandler.post(runnable);
            }
        });
        httpClient.postRequest(Common.task.notice_ask, Common.api.notice_ask, map);
    }

    private void dealReceivedNotice(String responseData) {
        LogUtils.d(TAG, "dealReceivedNotice() responseData=" + responseData);
        if (CommonUtil.isJsonDataResultEmpty(responseData)) {
            return;
        }
        NoticesGson response = gson.fromJson(responseData, NoticesGson.class);
        int code = response.getCode();
        List<Notice> notices = response.getResult();
        if (code != Common.code.SUCCESS || null == notices || notices.isEmpty()) {
            return;
        }
        for (Notice bean : notices) {

            synchronized (noticePendings) {
                if (!noticePendings.containsValue(bean)) {
                    LogUtils.d(TAG, "new notice founded:" + bean.toString());
                    noticePendings.put(bean.getId(), bean);
                }
            }
        }

        if (noticePendings.size() > 0) {
            new Thread(processor, "NoticeProcessor").start();
        }
    }

    private class NoticeProcessor implements Runnable {

        @Override
        public void run() {
            synchronized (noticePendings) {
                boolean location = false;
                List<Notice> addFamily = new ArrayList<Notice>();
                for (Entry<Integer, Notice> entryItem : noticePendings.entrySet()) {
                    if (1 == entryItem.getValue().getType() && !location) {
                        // 定位请求,只请求一次
                        LogUtils.d(TAG, "request location notice");
                        location = true;
                        Message msg = mServiceHandler
                                .obtainMessage(MSG_PROCESS_LOCATION_NOTICE);
                        mServiceHandler.sendMessage(msg);
                    } else if (2 == entryItem.getValue().getType()) {
                        // 添加家庭成员消息
                        LogUtils.d(TAG, "request add family notice");
                        addFamily.add(entryItem.getValue());
                    }
                }

                if (!addFamily.isEmpty()) {

                    // 添加成员的消息不一定能实时处理，后面会统一存到数据库，这里就不做保存了。
                    for (Notice notice : addFamily) {
                        noticePendings.remove(notice.getId());
                    }

                    RemoteLocationLogicPolicy policy = RemoteLocationLogicManager
                            .getInstance(mContext).getRemoteLocationLogicPolicy();
                    boolean option = policy.addNotices(addFamily);
                    if (option) {
                        // TODO 插入数据库成功
                        LogUtils.d(TAG, "insert new notice success!");

                        Intent intent = new Intent(Constant.ACTION_SHOW_NOTICE);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                                mContext)
                                .setContentTitle("添加家人通知：")
                                .setAutoCancel(true)
                                .setDefaults(
                                        NotificationCompat.DEFAULT_LIGHTS
                                                | NotificationCompat.DEFAULT_VIBRATE)
                                .setSmallIcon(R.drawable.settings_menu_member)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                                .setUsesChronometer(false)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                        builder.setContentIntent(PendingIntent.getActivity(mContext, 0,
                                intent, PendingIntent.FLAG_UPDATE_CURRENT));

                        NotificationManagerCompat nm = NotificationManagerCompat
                                .from(mContext);
                        nm.notify(Constant.NOTIFICATION_ADD_FAMILY_NOTICE,
                                builder.build());
                    }

                }
            }
        }
    }

    private class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            HashMap<String, String> headMap = new HashMap<String, String>();
            headMap.put("token",
                    PreferenceHelper.getString(getApplicationContext(), "token"));
            HashMap<String, Object> paramMap = new HashMap<String, Object>();
            List<NoticeReportGson> list = new ArrayList<NoticeReportGson>();

            switch (msg.what) {
            case Constant.MSG_LOCATION_RESPONSE_TO_SERVER:
                LogUtils.d(TAG, "receive notice response from LocationService");
                BDLocation location = (BDLocation) msg.obj;
                LogUtils.d(TAG, "location:[" + String.valueOf(location.getLatitude())
                        + "," + String.valueOf(location.getLongitude()) + "]");

                for (Entry<Integer, Notice> entryItem : noticePendings.entrySet()) {

                    if (null == entryItem || null == entryItem.getValue()) {
                        continue;
                    }

                    if (1 != entryItem.getValue().getType()) {
                        continue;
                    }

                    Notice notice = entryItem.getValue();
                    NoticeReportGson report = new NoticeReportGson(notice.getId(), 4,
                            notice.getType(), 0, String.valueOf(location.getLongitude()),
                            String.valueOf(location.getLatitude()), location.getCity()
                                    + " " + location.getStreet(),
                            location.getLocationDescribe());
                    list.add(report);
                }
                paramMap.put("notices", gson.toJson(list));
                LogUtils.d(TAG, "notice:" + paramMap);
                httpClient.setResponse(new onResponseCode() {

                    @Override
                    public void setPostResponse(String responseData) {
                        LogUtils.d(TAG, "MessengerHandler responseData:" + responseData);
                        CommonGson response = gson.fromJson(responseData,
                                CommonGson.class);
                        if (Common.code.SUCCESS == response.getCode()) {
                            LogUtils.d(TAG, "remove location notice");
                            synchronized (noticePendings) {
                                for (Entry<Integer, Notice> entryItem : noticePendings
                                        .entrySet()) {
                                    if (null == entryItem || null == entryItem.getValue()
                                            || 1 != entryItem.getValue().getType()) {
                                        continue;
                                    }
                                    noticePendings.remove(entryItem.getKey());
                                }
                            }
                        }
                    }
                });

                httpClient.postRequests(Common.task.notice_report,
                        Common.api.notice_report, paramMap, headMap);
                break;

            case Constant.MSG_ADD_FAMILY_RESPONSE_TO_SERVER:
                LogUtils.d(TAG, "receive notice response from NoticeListActivity");
                List<Notice> optionPending = getPendingAddFamilyNotice(mContext);
                if (null == optionPending || optionPending.isEmpty()) {
                    return;
                }
                for (Notice notice : optionPending) {
                    if (null == notice) {
                        continue;
                    }
                    if (2 != notice.getType()) {
                        continue;
                    }

                    int result = 0;
                    if (5 == notice.getState()) {
                        result = 1;
                    } else if (6 == notice.getState()) {
                        result = 2;
                    } else {
                        result = 0;
                    }
                    NoticeReportGson report = new NoticeReportGson(notice.getId(), 4,
                            notice.getType(), result, "", "", "", "");
                    list.add(report);
                }
                paramMap.put("notices", gson.toJson(list));
                LogUtils.d(TAG, "notice:" + paramMap);
                httpClient.setResponse(new onResponseCode() {

                    @Override
                    public void setPostResponse(String responseData) {
                        CommonGson response = gson.fromJson(responseData,
                                CommonGson.class);
                        if (Common.code.SUCCESS == response.getCode()) {
                            LogUtils.d(TAG, "update pending notice to fixed");
                            RemoteLocationLogicPolicy policy = RemoteLocationLogicManager
                                    .getInstance(mContext).getRemoteLocationLogicPolicy();
                            policy.updatePendingToFix();
                        }
                    }
                });

                httpClient.postRequests(Common.task.notice_report,
                        Common.api.notice_report, paramMap, headMap);
                break;

            default:
                break;

            }
        }
    }

    private List<Notice> getPendingAddFamilyNotice(Context context) {
        RemoteLocationLogicPolicy policy = RemoteLocationLogicManager
                .getInstance(context).getRemoteLocationLogicPolicy();
        return policy.getPendingNotices();
    }

    private void enableLocationService(Context context) {
        LogUtils.d(TAG, "enableLocationService()");
        if (null == locationMessenger) {
            Intent intent = new Intent(context, LocationService.class);
            intent.setAction(Constant.ACTION_ENABLE_LOCATION_SERVICE);
            context.bindService(intent, mConnLocationService, Context.BIND_AUTO_CREATE);
        }
    }
}
