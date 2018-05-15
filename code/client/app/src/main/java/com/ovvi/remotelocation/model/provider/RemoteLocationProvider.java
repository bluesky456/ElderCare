package com.ovvi.remotelocation.model.provider;

import java.util.HashMap;

import com.ovvi.remotelocation.model.provider.DataBaseConstants.DataBaseUtil;
import com.ovvi.remotelocation.model.provider.DataBaseConstants.NoticesColumn;
import com.ovvi.remotelocation.model.provider.DataBaseHelper.Tables;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class RemoteLocationProvider extends ContentProvider {

	private DataBaseHelper dbHelper;
	private static final UriMatcher sUriMather;
	private static HashMap<String, String> noticesProjectionMap;

	private static final int MATCH_TYPE_NOTICES = 1;
	private static final int MATCH_TYPE_NOTICES_ID = 2;

	static {
		sUriMather = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMather.addURI(DataBaseConstants.AUTHORITY, DataBaseConstants.KeyValue.NOTICES, MATCH_TYPE_NOTICES);
		sUriMather.addURI(DataBaseConstants.AUTHORITY, DataBaseConstants.KeyValue.NOTICES + "/#",
				MATCH_TYPE_NOTICES_ID);

		noticesProjectionMap = new HashMap<String, String>();
		noticesProjectionMap.put(NoticesColumn._ID, NoticesColumn._ID);
		noticesProjectionMap.put(NoticesColumn.ID, NoticesColumn.ID);
		noticesProjectionMap.put(NoticesColumn.FROM_ID, NoticesColumn.FROM_ID);
		noticesProjectionMap.put(NoticesColumn.TO_ID, NoticesColumn.TO_ID);
		noticesProjectionMap.put(NoticesColumn.MSG, NoticesColumn.MSG);
		noticesProjectionMap.put(NoticesColumn.TYPE, NoticesColumn.TYPE);
		noticesProjectionMap.put(NoticesColumn.STATE, NoticesColumn.STATE);
		noticesProjectionMap.put(NoticesColumn.CREATE_TIME, NoticesColumn.CREATE_TIME);
		noticesProjectionMap.put(NoticesColumn.OPTION, NoticesColumn.OPTION);
	}

	private String getKeyValue(Uri uri) {
		if (null == uri) {
			return null;
		}

		String key = null;
		key = uri.getPathSegments().get(0);
		return key;
	}

	private String getId(Uri uri) {
		if (null == uri) {
			return null;
		}

		String id = null;
		id = uri.getPathSegments().get(1);
		return id;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int count = 0;
		StringBuilder sqls = new StringBuilder();
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		switch (sUriMather.match(uri)) {
		case MATCH_TYPE_NOTICES:
			count = db.delete(Tables.TABLE_NOTICES, selection, selectionArgs);
			break;

		case MATCH_TYPE_NOTICES_ID:
			String Id = getId(uri);
			if (TextUtils.isEmpty(Id)) {
				throw new IllegalArgumentException("Unknown URI :" + uri);
			}

			sqls.append(NoticesColumn._ID).append(DataBaseUtil.SQL_SYMBOL_EQLALS).append(Id);

			if (!(TextUtils.isEmpty(selection))) {
				sqls.append(DataBaseUtil.SQL_SYMBOL_SPACE).append(DataBaseUtil.SQL_SYMBOL_AND)
						.append(DataBaseUtil.SQL_SYMBOL_LEFT_BRACKET).append(selection)
						.append(DataBaseUtil.SQL_SYMBOL_RIGHT_BRACKET);
			} else {
				sqls.append("");
			}
			count = db.delete(Tables.TABLE_NOTICES, sqls.toString(), selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI :" + uri);
		}

		if (count > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}

		return count;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		ContentValues values;
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		switch (sUriMather.match(uri)) {
		case MATCH_TYPE_NOTICES:
			values = ensureContentValues(initialValues);

			long rowId = db.insert(Tables.TABLE_NOTICES, null, values);
			if (rowId > 0L) {
				Uri retUri = ContentUris.withAppendedId(uri, rowId);

				getContext().getContentResolver().notifyChange(retUri, null);
				return retUri;
			}
			break;

		default:
			throw new IllegalArgumentException("Unknown URI :" + uri);
		}

		return null;
	}

	@Override
	public boolean onCreate() {
		dbHelper = new DataBaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		String key;
		String sourceId;
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		switch (sUriMather.match(uri)) {
		case MATCH_TYPE_NOTICES:
			key = getKeyValue(uri);
			if (TextUtils.isEmpty(key)) {
				throw new IllegalArgumentException("Unknown URI :" + uri);
			}

			qb.setTables(Tables.TABLE_NOTICES);
			qb.setProjectionMap(noticesProjectionMap);
			break;

		case MATCH_TYPE_NOTICES_ID:
			sourceId = getId(uri);
			if (TextUtils.isEmpty(sourceId)) {
				throw new IllegalArgumentException("Unknown URI :" + uri);
			}

			qb.setTables(Tables.TABLE_NOTICES);
			qb.setProjectionMap(noticesProjectionMap);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI :" + uri);
		}

		SQLiteDatabase db = dbHelper.getReadableDatabase();

		Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues initialValues, String selection, String[] selectionArgs) {
		int count = 0;
		StringBuilder sqls = new StringBuilder(DataBaseUtil.SQL_TRUE);
		ContentValues values = ensureContentValues(initialValues);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (!(TextUtils.isEmpty(selection))) {
            sqls.append(DataBaseUtil.SQL_SYMBOL_SPACE).append(DataBaseUtil.SQL_SYMBOL_AND)
                    .append(DataBaseUtil.SQL_SYMBOL_LEFT_BRACKET).append(selection)
                    .append(DataBaseUtil.SQL_SYMBOL_RIGHT_BRACKET);
        }
        
		switch (sUriMather.match(uri)) {
		case MATCH_TYPE_NOTICES:
			count = db.update(Tables.TABLE_NOTICES, values, sqls.toString(), selectionArgs);
			break;

		case MATCH_TYPE_NOTICES_ID:
			String sourceId = getId(uri);
			if (TextUtils.isEmpty(sourceId)) {
				throw new IllegalArgumentException("Unknown URI :" + uri);
			}

			count = db.update(Tables.TABLE_NOTICES, values, sqls.toString(), selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI :" + uri);
		}

		if (count > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return count;
	}

	private ContentValues ensureContentValues(ContentValues initialValues) {
		ContentValues result;

		if (initialValues != null) {
			result = new ContentValues(initialValues);
		} else {
			result = new ContentValues();
		}

		return result;
	}
}
