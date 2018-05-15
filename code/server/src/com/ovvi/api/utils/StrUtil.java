/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: OvviAdvertApi 
 * @File: StringUtil.java
 * @Author: liuyunlong 
 * @Date: 2017年3月15日 上午10:30:22
 * @version: V1.0
 * TODO
 * 
 * Modification  History:
 * Date         Author        Version        Discription
 * -----------------------------------------------------------------------------------

 * 2017年3月15日       yunlong.liu          1.0             1.0

 * Why & What is modified: <修改原因描述>
 *
 *************************************************************************************/
package com.ovvi.api.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @description 
 * @author liuyunlong 
 * @date 2017年3月15日上午10:30:22
 * 
 */
public class StrUtil extends StringUtils {

	/**字符串大于等于 0 相等 1 大于 -1 小于*/
	public static boolean bigThanAnd(String beginStr, String endStr) {
		if (null == beginStr || null == endStr) {
			return false;
		}
		return (beginStr.trim().compareTo(endStr.trim()) >= 1 || beginStr.trim().compareTo(endStr.trim()) == 0) ? true : false;
	}

	/**字符串小于等于*/
	public static boolean smallThanAnd(String beginStr, String endStr) {
		if (null == beginStr || null == endStr) {
			return false;
		}
		return (beginStr.trim().compareTo(endStr.trim()) <= -1 || beginStr.trim().compareTo(endStr.trim()) == 0) ? true : false;
	}

	/**字符串介于之间*/
	public static boolean betweenAnd(String str, String beginStr, String endStr) {
		if (null == beginStr || null == endStr || null == str) {
			return false;
		}
		return (str.trim().compareTo(beginStr.trim()) >= 1 || str.trim().compareTo(beginStr.trim()) == 0)
				&& (str.trim().compareTo(endStr.trim()) <= -1 || str.trim().compareTo(endStr.trim()) == 0) ? true : false;
	}

	public static boolean isEmpty(String str) {
		boolean result = false;
		if (StringUtils.isEmpty(str) || "null".equalsIgnoreCase(str)) {
			result = true;
		}
		return result;
	}

	public static void main(String[] args) {
		System.out.println(bigThanAnd("2019", "2016"));
		System.out.println(smallThanAnd("2011", "2016"));
		System.out.println(betweenAnd("2014", "2013", "2019"));
	}
}
