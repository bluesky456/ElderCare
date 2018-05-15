package com.ovvi.remotelocation.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Environment;
import android.telephony.TelephonyManager;

import com.ovvi.remotelocation.base.Common;

public class CommonUtil {

    /** 地球半径 */
    private static final double EARTH_RADIUS = 6378137.0;

    public static String PhotoDir = Environment.getExternalStorageDirectory()
            + "/location/image/";

    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return "";
    }

    /**
     * 校验double数值是否为0
     * 
     */
    public static boolean isEqualToZero(double value) {
        return Math.abs(value - 0.0) < 0.01 ? true : false;
    }

    /**
     * 经纬度是否为(0,0)点
     */
    public static boolean isZeroPoint(double latitude, double longitude) {
        return isEqualToZero(latitude) && isEqualToZero(longitude);
    }

    /**
     * 将字符串转为时间戳
     */
    public static long toTimeStamp(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        Date date;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
        return date.getTime() / 1000;
    }

    /**
     * 获取设备IMEI码
     */
    public static String getImei(Context context) {
        String imei;
        try {
            imei = ((TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        } catch (Exception e) {
            imei = "myTrace";
        }
        if (imei == null) {
            return "0000";
        }
        return imei;
    }

    /** 获取手机号码 */
    public static String getNativePhoneNumber(Context context) {
        String nativePhoneNumber = "N/A";
        nativePhoneNumber = ((TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
        return nativePhoneNumber;
    }

    /** 获取设备品牌 */
    public static String getPhoneBrand() {
        return android.os.Build.BRAND;
    }

    /** 获取设备型号 */
    public static String getPhoneModel() {
        return android.os.Build.MODEL;
    }

    /** 检查网络状态 */
    public static boolean checkNetworkState(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        State mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        // 如果3G、wifi、2G等网络状态是连接的，则退出，否则显示提示信息进入网络设置界面
        if (mobile == State.CONNECTED || mobile == State.CONNECTING) {
            return true;
        }
        if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
            return true;
        }
        return false;
    }

    /**
     * 判断请求的json数据result是否为null
     * 
     * @param json
     * @return
     */
    public static boolean isJsonDataResultEmpty(String json) {
        if (json == null) {
            return true;
        }
        String[] data = json.split(",");
        String[] result = data[2].split(":");
        if (result[1].length() > 3) {
            return false;
        } else {
            return true;
        }
    }

    /** 老人端：1 ，子女端：2 */
    public static boolean isOldMode() {
        if (Common.type == 1) {
            return true;
        } else {
            return false;
        }
    }

    /** 时间戳转字符串 */
    public static String getDateFromTime(long timeStamp) {
        String timeString = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long l = Long.valueOf(timeStamp);
        timeString = sdf.format(new Date(l));
        return timeString;
    }

    /**
     * 两点之间的距离
     * 
     * @param longitude1
     *            经度1
     * @param latitude1
     *            维度1
     * @param longitude2
     *            经度2
     * @param latitude2
     *            维度2
     * @return
     */
    public static double getDistance(double longitude1, double latitude1,
            double longitude2, double latitude2) {
        double Lat1 = rad(latitude1);
        double Lat2 = rad(latitude2);
        double a = Lat1 - Lat2;
        double b = rad(longitude1) - rad(longitude2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(Lat1)
                * Math.cos(Lat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

}
