package com.ovvi.remotelocation.model.logic;

import java.util.List;

import com.ovvi.remotelocation.bean.Notice;

public interface RemoteLocationLogicInterface {

	/**
	 * 添加通知消息
	 * @param context 上下文
	 * @param bean 消息
	 * @return 添加单条通知消息
	 */
	public boolean addNotice(Notice bean);

	/**
	 * 批量添加通知消息
	 * @param context 上下文
	 * @param beans 消息列表
	 * @return true:成功, false:失败
	 */
	public boolean addNotices(List<Notice> beans);

	/**
	 * 更新单条通知消息
	 * @param bean 消息
	 * @return true:成功, false:失败
	 */
	public boolean updateNotice(Notice bean);

	/**
	 * 批量更新通知消息
	 * @param beans 消息列表
	 * @return true:成功, false:失败
	 */
	public boolean updateNotices(List<Notice> beans);
	
	/**
	 * @param id 主键Id
	 * @param state notice状态，1:未读，2:已下发，3:已展示，4:已读，5:同意，6:拒绝。前面四种为server端状态，后两种为客户端状态
	 * @return 更新消息通知的状态
	 */
	public boolean updateStateById(int id, int state);
	
	/**
	 * @return 返回待处理的notice，条件为1 == option, 或者理解为本地已处理完，待上报服务器的消息
	 */
	public List<Notice> getPendingNotices();
	
	/**
	 * @return 返回未读notice数量，条件为0 == option
	 */
	public int getUnReadNoticesCount();
	
	/**
	 * 更新已处理的notice状态，
	 */
	public int updatePendingToFix();
	
	/**
	 * 删除所有notice数据
	 */
	public int clearAllNotice();
}
