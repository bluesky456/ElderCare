/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: OvviAdvertApi 
 * @File: Constants.java
 * @Author: liuyunlong 
 * @Date: 2017年3月9日 上午8:52:11
 * @version: V1.0
 * TODO
 * 
 * Modification  History:
 * Date         Author        Version        Discription
 * -----------------------------------------------------------------------------------

 * 2017年3月9日       yunlong.liu          1.0             1.0

 * Why & What is modified: <修改原因描述>
 *
 *************************************************************************************/
package com.ovvi.api.utils;

import java.text.SimpleDateFormat;

/**
 * @description 
 * @author liuyunlong 
 * @date 2017年3月9日上午8:52:11
 * 
 */
public interface Const {

	public static final SimpleDateFormat SDFCOMMON = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat SDFCOMMON_DATE = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat SDFCOMMON_HH = new SimpleDateFormat("HH");

	public static final int INT_B_1 = -1;
	public static final int INT_0 = 0;
	public static final int INT_1 = 1;
	public static final int INT_2 = 2;
	public static final int INT_3 = 3;
	public static final int INT_4 = 4;
	public static final int INT_5 = 5;
	public static final int INT_7 = 7;
	public static final int INT_10 = 10;
	public static final int INT_20 = 20;
	public static final int INT_100 = 100;
	public static final int INT_1000 = 1000;

	public static final int ENABLE = 1;
	public static final int DISABLE = 0;

	/**一天的毫秒数*/
	public static final Integer DAY_TIMESTAMP = 1000 * 60 * 60 * 24;

	/** 禁止重复发送验证码 时间限制*/
	public static final long DEFAULT_SEND_SMCODE_REPEATE_LIMIT = 60;
	/** 手机验证码有效期5分钟 */
	public static final long DEFAULT_SMCODE_VALIDITY_TIME = 5 * 60;
	/** url有效时间 */
	public static final long DEFAULT_URL_VALIDITY_TIME = 2 * 60 * 1000;

	/** token失效时间15天 */
	public static final long DEFAULT_TOKEN_VALIDITY_TIME = 15 * 24 * 60 * 60 * 1000;

	/** 验证URL时效私钥 */
	public static final String DEFAULT_VT_KEY = "RSDdwp9840##DSG2d";
	/** token验证私钥*/
	public static final String DEFAULT_TK_KEY = "SPdm73=#@HYQHhhepl=";

	/**响应结构体*/
	public enum RESPONSE_CONSTRUCTOR {

		CODE("code", "状态码"),

		MSG("msg", "返回描述"),

		DATA("data", "返回数据json字符串");

		public String code;

		public String desc;

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		private RESPONSE_CONSTRUCTOR(String code, String desc) {
			this.code = code;
			this.desc = desc;
		}
	}

	/**响应类型*/
	public enum RES_TYPE {

		SUCCESS(1, "success"),

		VALIDATE_HEAD_VT_FAIL(1001, "fail to validate header vt"),

		VALIDATE_HEAD_TK_FAIL(1002, "fail to validate header tk"),

		RECORD_ALREADY_EXIST(1003, "record already exist"),

		RECORD_NOT_EXIST_OR_WRONG_PWD(1004, "record not exist or password is wrong"),

		PASSWORD_ERROR(1005, "must be combined with numbers and letters, and length must between 8 and 12"),

		PARAM_EMPTY(1006, "can not be empty"),

		ILLEGAL_FORMAT(1007, "is not in the right format"),

		LENGTH_TOO_LONG(1008, "length too long"),

		MUST_BETWEN_1_2(1009, "must between 1 and 2"),

		UNKNOW_USER_TYPE(1010, "unknow user type"),

		USER_NOT_EXIST(1011, "user not exist"),

		PHONE_NOT_REGISTER(1012, "phoneNum not register"),

		UNKNOW_NOTICE_TYPE(1013, "unknow notice type"),

		UNKNOW_REPORT_RESULT(1014, "unknow report result"),

		MUST_BETWEN_3_4(1015, "must between 3 and 4"),

		RECORD_MEMBER_LINK_NOT_EXIST(1016, "member link not exist"),

		RECORD_NOTICE_NOT_EXIST(1017, "notice not exist"),

		DATE_IS_EMPTY(1018, "data is empty"),

		NEVER_REPORT_LOCATION(1019, "never report location"),

		ALREADY_FAMILY_MEMBER(1020, "already family member"),

		SMCODE_SEND_LIMIT(1021, "same phone limit one smcode per min"),

		SMCODE_OVERDUE(1022, "smcode overdue"),

		CREATE_FRESHER_FAIL(1023, "fail create fresher"),

		UPDATE_RECORD_FAIL(1024, "fail to update record"),

		SEND_MSG_FAIL(1025, "fail to send sms"),

		PWD_UNKNOW_BACK_TYPE(1026, "unknow pwd back type"),

		PWD_ANSWER_UNMATCH(1027, "answer unmatch"),

		SMCODE_UNMATCH(1028, "smcode unmatch"),

		UNKONW_ERROR(-1, "unknow error");

		public Integer code;

		public String msg;

		public Integer getCode() {
			return code;
		}

		public void setCode(Integer code) {
			this.code = code;
		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}

		private RES_TYPE(Integer code, String msg) {
			this.code = code;
			this.msg = msg;
		}

		public static Integer msg2Code(String msg) {
			for (RES_TYPE status : values()) {
				if (status.msg.equals(msg)) {
					return status.code;
				}
			}
			return null;
		}
	}

	/**
	 * 接口地址
	 * @description 
	 * @author liuyunlong 
	 * @date 2017年12月22日上午10:07:44
	 *
	 */
	public enum REQUEST_URL {

		SMCODE("/api/account/smcode", "获取状态码接口"),

		REGISTER("/api/account/register", "注册接口"),

		LOGIN("/api/account/login", "登录接口"),

		PWD_QUESTION("/api/account/question", "找回问题"),

		PWD_BACK("/api/account/pwd", "找回密码"),

		LOCATION_REPORT("/api/location/report", "上报位置信息"),

		LOCATION_ASK("/api/location/remote/ask", "发起远程定位请求"),

		LOCATION_RECEIVE("/api/location/remote/receive", "刷新远程定位请求"),

		LOCATION_HISTORY("/api/location/history", "历史轨迹"),

		LOCATION_FENCE("/api/location/fence", "电子栅栏"),

		FAMILY_ADD("/api/user/family/add", "添加家庭成员"),

		FAMILY_LIST("/api/user/family/list", "获取家庭成员"),

		FAMILY_DEL("/api/user/family/del", "添加家庭成员"),

		HOME("/api/user/home", "主界面"),

		SETTING("/api/user/setting", "获取设置页面数据"),

		EINFO("/api/user/einfo", "更新个人信息"),

		ELABEL("/api/user/label", "修改成员备注名"),

		NOTICE_ASK("/api/user/notice/ask", "获取通知消息"),

		NOTICE_REPORT("/api/user/notice/report", "通知消息上报结果");

		public String url;

		public String desc;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		private REQUEST_URL(String url, String desc) {
			this.url = url;
			this.desc = desc;
		}
	}

	/**
	 * 用户类型
	 * @description 
	 * @author liuyunlong 
	 * @date 2017年12月25日下午7:56:39
	 *
	 */
	public enum USER_TYPE {

		OLDER((byte) 1, "老人"),

		CHILDREN((byte) 2, "子女");

		public byte type;

		public String desc;

		public byte getType() {
			return type;
		}

		public void setType(byte type) {
			this.type = type;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		private USER_TYPE(byte type, String desc) {
			this.type = type;
			this.desc = desc;
		}
	}

	/**
	 * 发送短信业务类型
	 * @description 
	 * @author liuyunlong 
	 * @date 2018年1月5日上午11:51:06
	 *
	 */
	public enum SMSCODE_TYE {

		REGISTER((byte) 1, "注册"),

		PWD((byte) 2, "找回密码"),

		MEMBER_ADD((byte) 3, "添加家庭成员");

		public byte type;

		public String desc;

		public byte getType() {
			return type;
		}

		public void setType(byte type) {
			this.type = type;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		private SMSCODE_TYE(byte type, String desc) {
			this.type = type;
			this.desc = desc;
		}
	}

	/**
	 * 通知消息状态
	 * @description 
	 * @author liuyunlong 
	 * @date 2017年12月26日下午3:26:25
	 *
	 */
	public enum NOTICE_STATE {

		/** 未读的通知 */
		UNREAD((byte) 1, "未读"),

		PUSHED((byte) 2, "已下发"),

		SHOWED((byte) 3, "已展示"),

		READ((byte) 4, "已读");

		public byte state;

		public String desc;

		public byte getState() {
			return state;
		}

		public void setState(byte state) {
			this.state = state;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		private NOTICE_STATE(byte state, String desc) {
			this.state = state;
			this.desc = desc;
		}
	}

	/**
	 * 通知消息类型
	 * @description 
	 * @author liuyunlong 
	 * @date 2017年12月26日下午3:27:41
	 *
	 */
	public enum NOTICE_TYPE {

		LOCATION_REMOTE((byte) 1, "远程定位"),

		FAMILY_ADD((byte) 2, "添加老人");

		public byte type;

		public String desc;

		public byte getType() {
			return type;
		}

		public void setType(byte type) {
			this.type = type;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		private NOTICE_TYPE(byte type, String desc) {
			this.type = type;
			this.desc = desc;
		}
	}

	/**
	 * 添加家庭成员结果
	 * @description 
	 * @author liuyunlong 
	 * @date 2017年12月27日上午11:47:16
	 *
	 */
	public enum CHECK_RESULT {

		YES((byte) 1, "同意"),

		NO((byte) 2, "拒绝");

		public byte result;

		public String desc;

		public byte getResult() {
			return result;
		}

		public void setResult(byte result) {
			this.result = result;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		private CHECK_RESULT(byte result, String desc) {
			this.result = result;
			this.desc = desc;
		}
	}

	/**
	 * 映射关系状态
	 * @description 
	 * @author liuyunlong 
	 * @date 2017年12月28日上午9:10:12
	 *
	 */
	public enum LINK_STATE {

		WAIT((byte) 0, "未确认"),

		YES((byte) 1, "同意"),

		NO((byte) 2, "拒绝"),

		DEL((byte) 3, "删除");

		public byte state;

		public String desc;

		public byte getState() {
			return state;
		}

		public void setState(byte state) {
			this.state = state;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		private LINK_STATE(byte state, String desc) {
			this.state = state;
			this.desc = desc;
		}
	}

	/**
	 * 用户的状态
	 * @description 
	 * @author liuyunlong 
	 * @date 2018年1月10日上午11:05:27
	 *
	 */
	public enum USER_STATE {

		DEFAULT((byte) 0, "默认状态"),

		INVITE((byte) 1, "被邀请为成员"),

		CODE_SEND((byte) 2, "验证码已下发"),

		REG_SUC((byte) 9, "注册成功");

		public byte state;

		public String desc;

		public byte getState() {
			return state;
		}

		public void setState(byte state) {
			this.state = state;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		private USER_STATE(byte state, String desc) {
			this.state = state;
			this.desc = desc;
		}
	}

	/**
	 * 找回密码问题验证
	 * @description 
	 * @author liuyunlong 
	 * @date 2018年1月11日上午10:24:16
	 *
	 */
	public enum QUESTIONS {

		BIRTHDAY((byte) 1, "您的生日"),

		UNIVERSITY((byte) 2, "您的大学名称"),

		FATHER_NAME((byte) 3, "您父亲的姓名");

		public byte code;

		public String desc;

		public byte getState() {
			return code;
		}

		public void setState(byte state) {
			this.code = state;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		private QUESTIONS(byte state, String desc) {
			this.code = state;
			this.desc = desc;
		}
	}

	/**
	 * 找回密码方式
	 * @description 
	 * @author liuyunlong 
	 * @date 2018年1月11日上午10:50:19
	 *
	 */
	public enum PWD_BACK_TYPE {

		SMCODE((byte) 1, "短信验证码"),

		QUESTION((byte) 2, "问题验证");

		public byte code;

		public String desc;

		public byte getState() {
			return code;
		}

		public void setState(byte state) {
			this.code = state;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		private PWD_BACK_TYPE(byte state, String desc) {
			this.code = state;
			this.desc = desc;
		}
	}
}
