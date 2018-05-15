package com.ovvi.remotelocation.adapter;

import com.ovvi.remotelocation.R;
import com.ovvi.remotelocation.model.logic.RemoteLocationLogicManager;
import com.ovvi.remotelocation.model.logic.RemoteLocationLogicPolicy;
import com.ovvi.remotelocation.model.provider.DataBaseConstants.NoticesColumn;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NoticeListAdapter extends CursorAdapter {
	private Context mContext;

	public NoticeListAdapter(Context context, Cursor cursor) {
		super(context, cursor, FLAG_REGISTER_CONTENT_OBSERVER);
		mContext = context;
	}

	private void hideView(View view) {
		if (null != view) {
			view.setVisibility(View.GONE);
		}
	}
	
	/**
	 * notice 协议中定义的state状态，其中前4种为server端定义，后两种为客户端定义（为减少字段，统一定义状态），
	 * 因此在report时若state为后两种，需要强制修改为STATE_READ
	 * @author lantian
	 */
	public enum NOTICE_STATE {
		STATE_DEFAULT,               //默认值
		STATE_UNREAD,                //未读
		STATE_ALREADY_SENT,          //已下发
		STATE_DISPLAYED,             //已展示
		STATE_READ,                  //已读
		STATE_AGREED,                //同意
		STATE_REFUSED                //拒绝
	}
	
	public static enum OPTION_TYPE {
		TYPE_DEFAULT,
		TYPE_PENDING,           //通知待处理
		TYPE_FIXED              //通知已处理
	}
	
	private void modifyStateById(int _id, NOTICE_STATE state) {
		RemoteLocationLogicPolicy policy = RemoteLocationLogicManager.getInstance(mContext).getRemoteLocationLogicPolicy();
		policy.updateStateById(_id, state.ordinal());
	}
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final NoticeListViewItem viewHolder = new NoticeListViewItem();
		LayoutInflater factory = LayoutInflater.from(context);
		View itemView = factory.inflate(R.layout.notice_list_item, parent, false);
		viewHolder.list_item_iv = (ImageView) itemView.findViewById(R.id.notice_listitem_iv);
		viewHolder.list_item_title_tv = (TextView) itemView.findViewById(R.id.notice_listitem_title_tv);
		viewHolder.list_item_agree_btn = (Button) itemView.findViewById(R.id.notice_listitem_agree_btn);
		viewHolder.list_item_refuse_btn = (Button) itemView.findViewById(R.id.notice_listitem_refuse_btn);
		itemView.setTag(viewHolder);
		return itemView;
	}
	
	@Override
	public void bindView(View view, Context context, final Cursor cursor) {
		final NoticeListViewItem viewHolder = (NoticeListViewItem) view.getTag();
		final int _id = cursor.getInt(cursor.getColumnIndex(NoticesColumn._ID));
		String title = "";
		title = cursor.getString(cursor.getColumnIndex(NoticesColumn.MSG));
		viewHolder.list_item_title_tv.setText(title);
		
		if (null != viewHolder.list_item_agree_btn) {
			viewHolder.list_item_agree_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					hideView(viewHolder.list_item_refuse_btn);
					modifyStateById(_id, NOTICE_STATE.STATE_AGREED);
				}
			});			
		}

		if (null != viewHolder.list_item_refuse_btn) {
			viewHolder.list_item_refuse_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					hideView(viewHolder.list_item_agree_btn);
					modifyStateById(_id, NOTICE_STATE.STATE_REFUSED);
				}
			});			
		}
	}

	public class NoticeListViewItem {
		ImageView list_item_iv;
		TextView list_item_title_tv;
		Button list_item_agree_btn;
		Button list_item_refuse_btn;
	}
}
