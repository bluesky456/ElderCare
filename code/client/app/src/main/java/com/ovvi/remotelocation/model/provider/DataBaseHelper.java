package com.ovvi.remotelocation.model.provider;

import com.ovvi.remotelocation.model.provider.DataBaseConstants.KeyValue;
import com.ovvi.remotelocation.model.provider.DataBaseConstants.NoticesColumn;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

	public class Tables {
		public final static String TABLE_NOTICES = KeyValue.NOTICES;
	}
	private static  String databaseName = "OvviLocation.db";
	private static int databaseVersion = 1;
	private Context mContext;

	DataBaseHelper(Context paramContext) {
        super(paramContext, databaseName, null, databaseVersion);
        this.mContext = paramContext;
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + Tables.TABLE_NOTICES);
		onCreate(db);
	}
	
	@Override
	public void onCreate(SQLiteDatabase sqlitedatabase) {
		sqlitedatabase.execSQL("CREATE TABLE IF NOT EXISTS " + Tables.TABLE_NOTICES + " (" +
				NoticesColumn._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				NoticesColumn.ID + " INTEGER NOT NULL DEFAULT 0," +
				NoticesColumn.FROM_ID + " INTEGER NOT NULL DEFAULT 0," +
				NoticesColumn.TO_ID + " INTEGER NOT NULL DEFAULT 0," +
				NoticesColumn.MSG + " TEXT," +
				NoticesColumn.TYPE + " INTEGER NOT NULL DEFAULT 0," +
				NoticesColumn.STATE + " INTEGER NOT NULL DEFAULT 0," +
				NoticesColumn.CREATE_TIME+ " TEXT," +
				NoticesColumn.OPTION + " INTEGER NOT NULL DEFAULT 0" +
        		");");
	}
}
