package com.ovvi.remotelocation.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    /**
     * 成员信息表建表语句
     */
    public static final String CREATE_PERSON = "create table memberinfo ("
            + "_id integer primary key autoincrement, " + "image BLOB, "
            + "name VARCHAR(255), " + "nickname VARCHAR(1), " + "isDel INTEGER DEFAULT 0"
            + ")";

    /**
     * user表建表语句
     */
    public static final String CREATE_USER = "create table if not exists t_user("
            + "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," + "userName VARCHAR(255),"
            + "nickname VARCHAR(11)," + "token VARCHAR(255)," + "type INTEGER,"
            + "isDel INTEGER DEFAULT 0" + ")";

    /**
     * Location表建表语句
     */
    public static final String CREATE_LOCATION = "create table Location ("
            + "_id integer primary key autoincrement, " + "address text, "
            + "longitude real, " + "latitude real, " + "dateTime text, " + "number text)";

    public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_USER); // 创建User表
        db.execSQL(CREATE_PERSON); // 创建person表
        db.execSQL(CREATE_LOCATION); // 创建Location表
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 数据库版本更新
    }

}