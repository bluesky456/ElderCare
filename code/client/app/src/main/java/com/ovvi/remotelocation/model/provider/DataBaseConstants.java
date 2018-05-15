package com.ovvi.remotelocation.model.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public final class DataBaseConstants {
	public static final String AUTHORITY = "com.ovvi.remotelocation".intern();
	
	/**
	 * @author lantian
	 * Provider uri定义
	 */
	public static class ContentUri {
		/** days table uri */
		public static final Uri REMOTE_LOCATION_NOTICES = Uri.parse("content://" + AUTHORITY + "/" + KeyValue.NOTICES);
	}
	
	/**
	 * @author lantian
	 *  数据库notices表列名定义
	 */
	public static class NoticesColumn implements BaseColumns {
		
		// notices表字段
		public static final String ID = "id";
		public static final String FROM_ID = "fromId";
		public static final String TO_ID = "toId";
		public static final String MSG = "msg";
		public static final String TYPE = "type";
		public static final String STATE = "state";
		public static final String CREATE_TIME = "createTime";
		public static final String OPTION = "option";
	}
	
	/**
	 * @author lantian
	 * 数据库子键定义
	 */
	public static class KeyValue {
		/** notices子健 */
		public static final String NOTICES = "notices";
	}
	
	public static class DataBaseUtil {
		/** true forever */
		public static final String SQL_TRUE = " 1 = 1 ";
		
		/** false forever */
		public static final String SQL_FALSE = " 1 <> 1 ";
		
		/** and */
		public static final String SQL_SYMBOL_AND = " and ";

		/** = */
		public static final String SQL_SYMBOL_EQLALS = " = ";
		
		/** <> */
		public static final String SQL_SYMBOL_NOT_EQLALS = " <> ";
		
		/** > */
		public static final String SQL_SYMBOL_GREATER = " > ";
		
		/** >= */
		public static final String SQL_SYMBOL_GREATER_OR_EQLALS = " >= ";
		
		/** < */
		public static final String SQL_SYMBOL_LESS = " < ";
		
		/** <= */
		public static final String SQL_SYMBOL_LESS_OR_EQLALS = " <= ";

		/** ( */
		public static final String SQL_SYMBOL_LEFT_BRACKET = " (";

		/** ) */
		public static final String SQL_SYMBOL_RIGHT_BRACKET = ") ";

		/** 空格 */
		public static final String SQL_SYMBOL_SPACE = " ";
		
		/** 单引号 */
		public static final String SQL_SYMBOL_QUOTE = "'";
	}
}
