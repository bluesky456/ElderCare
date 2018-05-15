package com.ovvi.remotelocation.model.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.ovvi.remotelocation.adapter.NoticeListAdapter;
import com.ovvi.remotelocation.bean.Notice;
import com.ovvi.remotelocation.model.provider.DataBaseConstants.ContentUri;
import com.ovvi.remotelocation.model.provider.DataBaseConstants.DataBaseUtil;
import com.ovvi.remotelocation.model.provider.DataBaseConstants.NoticesColumn;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

public class RemoteLocationDbDao {

	Context context;

	public RemoteLocationDbDao(Context context) {
		this.context = context;
	}

	private ContentValues ensureContentValues(Notice bean) {
		ContentValues result = new ContentValues();

		if (bean != null) {
			if (bean.getId() > 0)
				result.put(NoticesColumn.ID, bean.getId());
			if (bean.getFromId() > 0)
				result.put(NoticesColumn.FROM_ID, bean.getFromId());
			if (bean.getToId() > 0)
				result.put(NoticesColumn.TO_ID, bean.getToId());
			if (!TextUtils.isEmpty(bean.getMsg()))
				result.put(NoticesColumn.MSG, bean.getMsg());
			if (0 < bean.getType() && 2 >= bean.getType())
				result.put(NoticesColumn.TYPE, bean.getType());
			if (0 < bean.getState() && 6 >= bean.getState())
				result.put(NoticesColumn.STATE, bean.getState());
			if (!TextUtils.isEmpty(bean.getCreateTime()))
				result.put(NoticesColumn.CREATE_TIME, bean.getCreateTime());
		}

		return result;
	}

	private Set<Integer> getNoticeIds() {
		Set<Integer> ids = new HashSet<Integer>();
		ContentResolver resolver = context.getContentResolver();
		Cursor c = null;

		try {
			c = resolver.query(ContentUri.REMOTE_LOCATION_NOTICES, null, null, null,
					NoticesColumn.ID + " DESC");
			if (null == c || !c.moveToFirst()) {
				return ids;
			}
			do {
				int indexId = c.getColumnIndex(NoticesColumn.ID);
				int id = c.getInt(indexId);
				if (!ids.contains(id)) {
					ids.add(id);
				}
			} while (c.moveToNext());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != c) {
				c.close();
			}
		}

		return ids;
	}
	
	public Uri insertNotice(Notice bean) {
		if (null == bean || 0 >= bean.getId()) {
			return null;
		}
		
		//清除掉已存在数据库的记录
		Set<Integer> ids = getNoticeIds();
		int id = bean.getId();
		if (id <= 0 || ids.contains(id)) {
			return null;
		}
		
		ContentValues values = ensureContentValues(bean);
		ContentResolver resolver = context.getContentResolver();
		return resolver.insert(ContentUri.REMOTE_LOCATION_NOTICES, values);
	}

	public int insertNotices(List<Notice> beans) {

		if (null == beans || beans.isEmpty()) {
			return 0;
		}
		
		List<Notice> targets = new ArrayList<Notice>();
		//清除掉已存在数据库的记录
		Set<Integer> ids = getNoticeIds();
		for (Notice bean : beans) {
			int id = bean.getId();
			if (id > 0 && !ids.contains(id)) {
				targets.add(bean);
			}
		}
		
		if (targets.isEmpty()) {
			return 0;
		}
		
		ContentValues[] values = new ContentValues[targets.size()];
		for (int index = 0; index < targets.size(); index++) {
			values[index] = ensureContentValues(targets.get(index));
		}
		ContentResolver resolver = context.getContentResolver();
		return resolver.bulkInsert(ContentUri.REMOTE_LOCATION_NOTICES, values);
	}

	public int updateNotice(Context context, int id, Notice bean) {
		ContentValues values = ensureContentValues(bean);
		ContentResolver resolver = context.getContentResolver();
		String selection = String.format(Locale.ENGLISH, "%s IN (%s)", NoticesColumn._ID, "" + id);
		return resolver.update(ContentUri.REMOTE_LOCATION_NOTICES, values, selection, null);
	}

	public int updateOptionById(int id, int state) {
		if (1 > state || 6 < state)
			return 0;
			
		ContentValues values = new ContentValues();
		values.put(NoticesColumn.STATE, state);
		if (5 == state || 6 == state) {
			// 定义state为5代表同意、state为6代表拒绝，此时一般要更新数据库，做为下次response to server的数据标记
			values.put(NoticesColumn.OPTION, 1);			
		}
		ContentResolver resolver = context.getContentResolver();
		String selection = String.format(Locale.ENGLISH, "%s IN (%s)", NoticesColumn._ID, "" + id);
		return resolver.update(ContentUri.REMOTE_LOCATION_NOTICES, values, selection, null);
	}

	public List<Notice> getPendingNotices() {
		List<Notice> ret = new ArrayList<Notice>();
		ContentResolver resolver = context.getContentResolver();
		String selection = DataBaseUtil.SQL_TRUE
				+ DataBaseUtil.SQL_SYMBOL_AND + NoticesColumn.OPTION + DataBaseUtil.SQL_SYMBOL_EQLALS + NoticeListAdapter.OPTION_TYPE.TYPE_PENDING.ordinal();
		Cursor c = null;

		try {
			c = resolver.query(ContentUri.REMOTE_LOCATION_NOTICES, null, selection, null,
					NoticesColumn.ID + " DESC");
			if (null != c && c.moveToFirst()) {
				do {
					int indexId = c.getColumnIndex(NoticesColumn.ID);
					int indexFromId = c.getColumnIndex(NoticesColumn.FROM_ID);
					int indexToId = c.getColumnIndex(NoticesColumn.TO_ID);
					int indexMsg = c.getColumnIndex(NoticesColumn.MSG);
					int indexType = c.getColumnIndex(NoticesColumn.TYPE);
					int indexState = c.getColumnIndex(NoticesColumn.STATE);
					int indexCreateTime = c.getColumnIndex(NoticesColumn.CREATE_TIME);
					
					Notice bean = new Notice();
					bean.setId(c.getInt(indexId));
					bean.setFromId(c.getInt(indexFromId));
					bean.setToId(c.getInt(indexToId));
					bean.setMsg(c.getString(indexMsg));
					bean.setType(c.getInt(indexType));
					bean.setState(c.getInt(indexState));
					bean.setCreateTime(c.getString(indexCreateTime));
					
					if (!ret.contains(bean)) {
						ret.add(bean);
					}
				} while (c.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != c) {
				c.close();
			}
		}

		return ret;
	}
	
	public int getUnReadNoticesCount() {
		List<Notice> ret = new ArrayList<Notice>();
		ContentResolver resolver = context.getContentResolver();
		String selection = DataBaseUtil.SQL_TRUE
				+ DataBaseUtil.SQL_SYMBOL_AND + NoticesColumn.OPTION + DataBaseUtil.SQL_SYMBOL_EQLALS + NoticeListAdapter.OPTION_TYPE.TYPE_DEFAULT.ordinal();
		Cursor c = null;

		try {
			c = resolver.query(ContentUri.REMOTE_LOCATION_NOTICES, null, selection, null,
					NoticesColumn.ID + " DESC");
			if (null != c && c.moveToFirst()) {
				do {
					int indexId = c.getColumnIndex(NoticesColumn.ID);
					int indexFromId = c.getColumnIndex(NoticesColumn.FROM_ID);
					int indexToId = c.getColumnIndex(NoticesColumn.TO_ID);
					int indexMsg = c.getColumnIndex(NoticesColumn.MSG);
					int indexType = c.getColumnIndex(NoticesColumn.TYPE);
					int indexState = c.getColumnIndex(NoticesColumn.STATE);
					int indexCreateTime = c.getColumnIndex(NoticesColumn.CREATE_TIME);
					
					Notice bean = new Notice();
					bean.setId(c.getInt(indexId));
					bean.setFromId(c.getInt(indexFromId));
					bean.setToId(c.getInt(indexToId));
					bean.setMsg(c.getString(indexMsg));
					bean.setType(c.getInt(indexType));
					bean.setState(c.getInt(indexState));
					bean.setCreateTime(c.getString(indexCreateTime));
					
					if (!ret.contains(bean)) {
						ret.add(bean);
					}
				} while (c.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != c) {
				c.close();
			}
		}

		return ret.size();
	}
	
	public int updatePendingToFix() {
		ContentValues values = new ContentValues(1);
		values.put(NoticesColumn.OPTION, 2);
		ContentResolver resolver = context.getContentResolver();
		String selection = DataBaseUtil.SQL_TRUE
				+ DataBaseUtil.SQL_SYMBOL_AND + NoticesColumn.OPTION + DataBaseUtil.SQL_SYMBOL_EQLALS + NoticeListAdapter.OPTION_TYPE.TYPE_PENDING.ordinal();
		return resolver.update(ContentUri.REMOTE_LOCATION_NOTICES, values, selection, null);
	}

	public int clearAllNotice() {
		ContentResolver resolver = context.getContentResolver();
		return resolver.delete(ContentUri.REMOTE_LOCATION_NOTICES, DataBaseUtil.SQL_TRUE, null);
	}
}
