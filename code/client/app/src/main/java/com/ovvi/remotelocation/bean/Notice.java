package com.ovvi.remotelocation.bean;

/**
 * 通知
 * 
 * @author chensong
 * 
 */
public class Notice {

	/** 通知id */
	private int id;

	private int fromId;

	private int toId;
	/** 通知内容 */
	private String msg;
	/** 通知类型：1-远程定位；2-添加家庭成员 */
	private int type;
	private int state;
	private String createTime;

	public int getFromId() {
		return fromId;
	}

	public void setFromId(int fromId) {
		this.fromId = fromId;
	}

	public int getToId() {
		return toId;
	}

	public void setToId(int toId) {
		this.toId = toId;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Notice)) {
			return false;
		}
		final Notice bean = (Notice) obj;
		if (this.id != bean.id) {
			return false;
		}
		if (this.fromId != bean.fromId) {
			return false;
		}
		if (this.toId != bean.toId) {
			return false;
		}
		if (this.type != bean.type) {
			return false;
		}
		if (this.state != bean.state) {
			return false;
		}
		if (!this.msg.equals(bean.msg)) {
			return false;
		}
		if (!this.createTime.equals(bean.createTime)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + fromId;
		result = prime * result + toId;
		result = prime * result + ((null == msg) ? 0 : msg.hashCode());
		result = prime * result + type;
		result = prime * result + state;
		result = prime * result + ((null == createTime) ? 0 : createTime.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "Notices(id=" + this.id + " fromId=" + this.fromId + " toId=" + this.toId + " msg=" + this.msg + " type="
				+ this.type + " state=" + this.state + " createTime=" + this.createTime + ")";
	}
}
