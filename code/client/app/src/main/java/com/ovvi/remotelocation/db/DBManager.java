package com.ovvi.remotelocation.db;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;

import com.ovvi.remotelocation.bean.Members;
import com.ovvi.remotelocation.model.User;
import com.ovvi.remotelocation.utils.LogUtils;

/**
 * 数据库操作类
 * 
 * @author chensong
 * 
 */
public class DBManager {

    private static final String TAG = "DBManager";
    private DatabaseHelper dbOpenHelper;// 创建DatabaseHelper对象
    private SQLiteDatabase sqliteDatabase;// 创建SQLiteDatabase对象

    /** 数据库名 */
    public static final String DB_NAME = "user.db";

    /** 数据库版本 */
    public static final int VERSION = 1;

    private static DBManager dbManager;

    /**
     * 获取DBManager的实例。
     */
    public synchronized static DBManager getInstance(Context context) {
        if (dbManager == null) {
            dbManager = new DBManager(context);
        }
        return dbManager;
    }

    /**
     * 构造方法私有化
     * 
     * @param context
     */
    private DBManager(Context context) {
        // 初始化DatabaseHelper对象
        dbOpenHelper = new DatabaseHelper(context, DB_NAME, null, VERSION);
        // 以读写方法打开数据库，不仅仅是写，getReadableDatabase()是只读
        sqliteDatabase = dbOpenHelper.getWritableDatabase();
    }

    /**
     * 插入用户数据
     * 
     * @param username
     *            用户名
     * @param password
     *            用户密码
     */
    public void dbInsertUser(String nickname, String userName) {
        String sql = "insert into t_user values (null,?,?,null,2,0)";
        // 传递过来的username与password分别按顺序替换上面sql语句的两个?，自动转换类型，下同，不再赘述
        Object bindArgs[] = new Object[] { userName, nickname };
        // 执行这条无返回值的sql语句,手机号就是username
        sqliteDatabase.execSQL("insert into t_user values (null,?,?,null,?,0)",
                new String[] { userName, nickname, String.valueOf(2) });

        // sqliteDatabase.execSQL("insert into t_user values (null,?,?,null,2,0)",
        // bindArgs);
    }

    /**
     * 插入用户数据
     * 
     * @param username
     *            用户名
     * @param token
     *            用户登录证明
     */
    public void dbInsertUserToken(String username, String token, String type) {
        String sql = "insert into t_user values (null,?,?,?,?,0)";
        // 传递过来的username与password分别按顺序替换上面sql语句的两个?，自动转换类型，下同，不再赘述
        Object bindArgs[] = new Object[] { username, token };
        // 执行这条无返回值的sql语句,手机号就是username
        sqliteDatabase.execSQL("insert into t_user values (null,?,?,?,?,0)",
                new String[] { username, username, token, type });
    }

    /**
     * 插入成员数据
     * 
     * @param username
     *            用户名
     * @param phone
     *            用户密码
     */
    public void dbInsertMember(byte[] bitmap, String username, String nickname) {
        String sql = "insert into memberinfo values (null,?,?,?,0)";
        // 传递过来的username与password分别按顺序替换上面sql语句的两个?，自动转换类型，下同，不再赘述
        Object bindArgs[] = new Object[] { bitmap, username, nickname };
        // 执行这条无返回值的sql语句,手机号就是username
        LogUtils.d(TAG, "dbInsertMember bitmap=" + bitmap);

        ContentValues cv = new ContentValues();
        cv.put("image", bitmap);
        cv.put("name", username);
        cv.put("phone", nickname);
        sqliteDatabase.insert("memberinfo", null, cv);
        // sqliteDatabase.execSQL("insert into memberinfo values (null,?,?,?,0)",
        // new String[] { bitmabToBytes(bitmap).toString(), username, phone });
    }

    /**
     * 插入成员数据
     * 
     * @param username
     *            用户名
     * @param phone
     *            用户密码
     */
    public void dbInsertPopuplist(String bitmap, String username, String nickname) {
        String sql = "insert into popupinfo values (null,?,?,?,0)";
        // 传递过来的username与password分别按顺序替换上面sql语句的两个?，自动转换类型，下同，不再赘述
        Object bindArgs[] = new Object[] { bitmap, username, nickname };
        // 执行这条无返回值的sql语句,手机号就是username
        sqliteDatabase.execSQL("insert into memberinfo values (null,?,?,?,0)",
                new String[] { bitmap, username, nickname });
    }

    /**
     * 插入位置信息数据
     * 
     * @param number
     *            号码
     * @param address
     *            地理位置
     * @param longitude
     *            经度
     * @param latitude
     *            维度
     * @param date
     *            日期
     */
    public void dbInsertLocation(String number, String address, double longitude,
            double latitude, String date) {
        String sql = "insert into Location values (null,?,?,?,?,?)";
        // 传递过来的username与password分别按顺序替换上面sql语句的两个?，自动转换类型，下同，不再赘述
        Object bindArgs[] = new Object[] { address, longitude, latitude, date, number };
        // 执行这条无返回值的sql语句,默认手机号就是username
        sqliteDatabase.execSQL("insert into Location values (null,?,?,?,?,?)",
                new Object[] { address, longitude, latitude, date, number });

    }

    /**
     * 求出位置表中有多少个位置点
     * 
     * @return 位置总数
     */
    public int dbGetLocationSize() {
        String sql = "select count(*) from Location";
        Cursor cursor = sqliteDatabase.rawQuery(sql, null);
        if (cursor.moveToNext())// 判断Cursor中是否有数据
        {
            return cursor.getInt(0);// 返回总记录数
        }
        return 0;// 如果没有数据，则返回0
    }

    /**
     * 求出表中有多少条用户数据
     * 
     * @return 数据总数
     */
    public int dbGetUserSize() {
        String sql = "select count(*) from t_user where isDel=0";
        Cursor cursor = sqliteDatabase.rawQuery(sql, null);
        if (cursor.moveToNext())// 判断Cursor中是否有数据
        {
            return cursor.getInt(0);// 返回总记录数
        }
        return 0;// 如果没有数据，则返回0
    }

    /**
     * 求出表中有多少条成员数据
     * 
     * @return 数据总数
     */
    public int dbGetMemberSize() {
        String sql = "select count(*) from memberinfo where isDel=0";
        Cursor cursor = sqliteDatabase.rawQuery(sql, null);
        if (cursor.moveToNext())// 判断Cursor中是否有数据
        {
            return cursor.getInt(0);// 返回总记录数
        }
        return 0;// 如果没有数据，则返回0
    }

    /**
     * 根据用户名查阅用户
     * 
     * @param username
     * @return 用户
     */
    public User dbQueryOneByUsername(String username) {
        String sql = "select * from t_user where username=?  and isDel=0";
        String[] selectionArgs = new String[] { username };
        Cursor cursor = sqliteDatabase.rawQuery(sql, selectionArgs);
        // 判断Cursor中是否有数据
        if (cursor.moveToNext()) {
            // 如果有用户，则把查到的值填充这个用户实体
            LogUtils.d(TAG, " type==" + cursor.getInt(cursor.getColumnIndex("type")));
            User user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndex("id")));
            user.setUserName(cursor.getString(cursor.getColumnIndex("userName")));
            user.setNickname(cursor.getString(cursor.getColumnIndex("nickname")));
            user.setToken(cursor.getString(cursor.getColumnIndex("token")));
            user.setType(cursor.getInt(cursor.getColumnIndex("type")));

            return user;// 返回一个用户给前台
        }
        return null;// 没有返回null
    }

    /**
     * 根据用户名，修改密码
     * 
     * @param username
     * @param newPassword
     */
    public void dbUpdatePassword(String username, String newPassword) {
        String sql = "update t_user set password=? where username=? and isDel=0";
        Object bindArgs[] = new Object[] { newPassword, username, null };
        sqliteDatabase.execSQL(sql, bindArgs);
    }

    /**
     * 查询所有用户
     * 
     * @return 所有用户
     */
    public ArrayList<User> dbQueryAllUser() {
        ArrayList<User> userArrayList = new ArrayList<User>();
        String sql = "select * from t_user where isDel=0";
        Cursor cursor = sqliteDatabase.rawQuery(sql, null);
        // 游标从头读到尾
        for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
            if (cursor.getInt(cursor.getColumnIndex("isDel")) != 1) {
                User user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndex("id")));
                user.setUserName(cursor.getString(cursor.getColumnIndex("userName")));
                user.setNickname(cursor.getString(cursor.getColumnIndex("phone")));
                userArrayList.add(user);
            }
        }
        return userArrayList;
    }

    /**
     * 查询所有家成员
     * 
     * @return 所有成员
     */
    public ArrayList<Members> dbQueryAllMember() {
        ArrayList<Members> userArrayList = new ArrayList<Members>();
        String sql = "select * from memberinfo where isDel=0";
        Cursor cursor = sqliteDatabase.rawQuery(sql, null);
        // 游标从头读到尾
        Log.d(TAG, "dbQueryAllMember ");
        for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
            if (cursor.getInt(cursor.getColumnIndex("isDel")) != 1) {
                Members mInfo = new Members();

                // mInfo.setBitmap(cursor.getBlob(cursor.getColumnIndex("image")));
                // mInfo.setName(cursor.getString(cursor.getColumnIndex("name")));
                // mInfo.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
                userArrayList.add(mInfo);
            }
        }
        return userArrayList;
    }

    /**
     * 删除用户，其实是把相应的isDel值从0改1
     * 
     * @param id
     */
    public void dbDeleteUser(int id) {
        String sql = "update t_user set isDel=1 where id=?";
        Object bindArgs[] = new Object[] { id };
        sqliteDatabase.execSQL(sql, bindArgs);

    }

    /**
     * 删除家成员，其实是把相应的isDel值从0改1
     * 
     * @param id
     */
    public void dbDeleteMember(int id) {
        String sql = "update memberinfo set isDel=1 where id=?";
        Object bindArgs[] = new Object[] { id };
        sqliteDatabase.execSQL(sql, bindArgs);

    }

    public byte[] bitmabToBytes(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }

}
