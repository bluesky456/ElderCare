package com.ovvi.remotelocation.activity;

import com.ovvi.remotelocation.R;
import com.ovvi.remotelocation.adapter.NoticeListAdapter;
import com.ovvi.remotelocation.constants.Constant;
import com.ovvi.remotelocation.model.provider.DataBaseConstants.ContentUri;
import com.ovvi.remotelocation.model.provider.DataBaseConstants.DataBaseUtil;
import com.ovvi.remotelocation.model.provider.DataBaseConstants.NoticesColumn;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

public class NoticeListActivity extends TitleActivity implements LoaderManager.LoaderCallbacks<Cursor> {
	private static final int LOADER_ID = 1;
	private Context mContext;
	private NoticeListAdapter mAdapter;
	private TextView mTitleTips;
	private ListView mNoticeListView = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		mContext = this;
		setTitle(R.string.add_family_notice_title);
		setContentView(R.layout.notice_add_family);
        hideForwardView();
		
        mAdapter = new NoticeListAdapter(mContext, null);
        mNoticeListView = (ListView)findViewById(R.id.notice_lv);
        if (null != mNoticeListView) {
        	mNoticeListView.setAdapter(mAdapter);	
		}
		getLoaderManager().initLoader(LOADER_ID, null, this);
	}
	
    public enum NOTICE_TYPE {
    	TYPE_ALL,
    	TYPE_LOCATION,         //定位通知
    	TYPE_ADD_FAMILY        //添加家人通知
    }
    
	public NOTICE_TYPE getNoticeType() {
		return NOTICE_TYPE.TYPE_ADD_FAMILY;
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] PROJECTION = {
				NoticesColumn._ID, NoticesColumn.ID, NoticesColumn.FROM_ID, NoticesColumn.TO_ID, NoticesColumn.MSG, NoticesColumn.TYPE, NoticesColumn.STATE, NoticesColumn.CREATE_TIME};
		String selection = DataBaseUtil.SQL_TRUE 
				+ DataBaseUtil.SQL_SYMBOL_AND + NoticesColumn.TYPE + DataBaseUtil.SQL_SYMBOL_EQLALS + getNoticeType().ordinal()
				+ DataBaseUtil.SQL_SYMBOL_AND + NoticesColumn.OPTION + DataBaseUtil.SQL_SYMBOL_NOT_EQLALS + NoticeListAdapter.OPTION_TYPE.TYPE_PENDING.ordinal()
				+ DataBaseUtil.SQL_SYMBOL_AND + NoticesColumn.OPTION + DataBaseUtil.SQL_SYMBOL_NOT_EQLALS + NoticeListAdapter.OPTION_TYPE.TYPE_FIXED.ordinal();
		return new CursorLoader(mContext, ContentUri.REMOTE_LOCATION_NOTICES, PROJECTION, selection, null, "_id asc");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mAdapter.changeCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.changeCursor(null);
	}
	
	private void sendReportPending() {
		Intent intent = new Intent(Constant.ACTION_REPORT_NOTICE);
		mContext.sendBroadcast(intent);
	}
	
	@Override
	public void onBackPressed() {
		sendReportPending();
		super.onBackPressed();
	}
	
	@Override
	protected void onBackward(View backwardView) {
		sendReportPending();
		super.onBackward(backwardView);
	}
}
