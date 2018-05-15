package com.ovvi.remotelocation.model.logic;
import java.util.ArrayList;
import java.util.List;

import com.ovvi.remotelocation.bean.Notice;
import com.ovvi.remotelocation.model.dao.RemoteLocationDbDao;

import android.content.Context;

public class RemoteLocationLogicPolicy implements RemoteLocationLogicInterface {
	private Context mContext;

	public RemoteLocationLogicPolicy(Context context) {
		this.mContext = context;
	}

	@Override
	public boolean addNotice(Notice bean) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addNotices(List<Notice> beans) {
		RemoteLocationDbDao dao = new RemoteLocationDbDao(mContext);
		int ret = dao.insertNotices(beans);
		return ret > 0;
	}

	@Override
	public boolean updateNotice(Notice bean) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateNotices(List<Notice> beans) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateStateById(int id, int state) {
		RemoteLocationDbDao dao = new RemoteLocationDbDao(mContext);
		int ret = dao.updateOptionById(id, state);
		return ret > 0;
	}

	@Override
	public List<Notice> getPendingNotices() {
		RemoteLocationDbDao dao = new RemoteLocationDbDao(mContext);
		return dao.getPendingNotices();
	}
	
	@Override
	public int getUnReadNoticesCount() {
		RemoteLocationDbDao dao = new RemoteLocationDbDao(mContext);
		return dao.getUnReadNoticesCount();
	}
	
	@Override
	public int updatePendingToFix() {
		RemoteLocationDbDao dao = new RemoteLocationDbDao(mContext);
		return dao.updatePendingToFix();
	}

	@Override
	public int clearAllNotice() {
		RemoteLocationDbDao dao = new RemoteLocationDbDao(mContext);
		return dao.clearAllNotice();
	}
}
