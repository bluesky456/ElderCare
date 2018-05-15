package com.ovvi.remotelocation.utils;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences 工具类
 * 
 * @author chensong
 * 
 */
public class PreferenceHelper {

    private static final String IDENTIFY = "com.OldMan";

    /**
     * 存储String类型数据
     * 
     * @param context
     * @param key
     * @param value
     */
    public static void putString(Context context, String key, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences(IDENTIFY,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 获取String类型数据
     * 
     * @param context
     * @param key
     * @return
     */
    public static String getString(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(IDENTIFY,
                Context.MODE_PRIVATE);
        return sharedPref.getString(key, "");
    }

    /**
     * 存储set类型数据
     * 
     * @param context
     * @param key
     * @param values
     */
    public static void putSet(Context context, String key, Set<String> values) {
        SharedPreferences sharedPref = context.getSharedPreferences(IDENTIFY,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet(key, values);
        editor.commit();
    }

    /**
     * 获取set类型数据
     * 
     * @param context
     * @param key
     * @return
     */
    public static Set<String> getSet(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(IDENTIFY,
                Context.MODE_PRIVATE);
        return sharedPref.getStringSet(key, new HashSet<String>());
    }

    /**
     * 存储int类型数据
     * 
     * @param context
     * @param key
     * @param value
     */
    public static void putInt(Context context, String key, int value) {
        SharedPreferences sharedPref = context.getSharedPreferences(IDENTIFY,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * 获取int类型数据
     * 
     * @param context
     * @param key
     * @return
     */
    public static int getInt(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(IDENTIFY,
                Context.MODE_PRIVATE);
        return sharedPref.getInt(key, -1);
    }

    /**
     * 删除指定数据
     * 
     * @param context
     * @param key
     */
    public static void removeData(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(IDENTIFY,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(key);
        editor.commit();
    }
}
