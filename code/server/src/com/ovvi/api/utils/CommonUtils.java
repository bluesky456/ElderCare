/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: OvviAdvertApi 
 * @File: CommonUtils.java
 * @Author: liuyunlong 
 * @Date: 2017年5月5日 上午10:34:55
 * @version: V1.0
 * TODO
 * 
 * Modification  History:
 * Date         Author        Version        Discription
 * -----------------------------------------------------------------------------------

 * 2017年5月5日       yunlong.liu          1.0             1.0

 * Why & What is modified: <修改原因描述>
 *
 *************************************************************************************/
package com.ovvi.api.utils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @description 
 * @author liuyunlong 
 * @date 2017年5月5日上午10:34:55
 * 
 */
public class CommonUtils {

	public static boolean isIntegerOk(Integer i) {
		return null != i && i > 0 ? true : false;
	}

	public static long dateToStamp(String dateStr) {
		try {
			Date date = Const.SDFCOMMON.parse(dateStr);
			long ts = date.getTime() / 1000;
			return ts;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 根据范围获取随机数，格式如：10~20
	 * @param dailyActiveRate
	 * @return
	 * @author liuyunlong
	 * @version 2017年10月13日下午3:00:51
	 */
	public static Integer getRandomByCondition(String dailyActiveRate) {
		if (!StringUtils.isEmpty(dailyActiveRate)) {
			String[] range = dailyActiveRate.split("\\~");
			if (null != range && range.length == 2) {
				Random rand = new Random();
				int nextInt = Integer.parseInt(range[1]) - Integer.parseInt(range[0]) + 1;
				int randNum = rand.nextInt(nextInt) + Integer.parseInt(range[0]);
				return randNum;
			}
		}
		return 0;
	}

	/** 
	 * @param list
	 * @return
	 * @author liuyunlong
	 * @version 2017年10月13日下午4:40:08
	 * @param <T>
	 */
	public static <T> Map<Integer, List<T>> list2Map(List<T> list) {
		if (!CollectionUtils.isEmpty(list)) {
		}
		return null;
	}

	/** 
	 * 获取指定天数的未来日期
	 * @param days
	 * @return
	 * @author liuyunlong
	 * @version 2017年10月13日下午5:43:24
	 */
	public static String getFeatureDateByDays(Integer days) {
		if (null != days && days > 0) {
			Calendar calendar = Calendar.getInstance();// 日历对象
			calendar.setTime(new Date());// 设置当前日期
			calendar.add(Calendar.DATE, +days);
			return Const.SDFCOMMON_DATE.format(calendar.getTime());
		}
		return "";
	}

	/** 
	 * @param createTime
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月26日下午4:55:55
	 */
	public static String[] getTimeStampByDate(String createTime) {
		String[] result = new String[2];
		try {
			if (!StringUtils.isEmpty(createTime) && Const.INT_10 == createTime.length()) {
				String start = createTime + " 00:00:00";
				String end = createTime + " 23:59:59";
				result[0] = Const.SDFCOMMON.parse(start).getTime() + "";
				result[1] = Const.SDFCOMMON.parse(end).getTime() + "";
			} else {
				String format = Const.SDFCOMMON_DATE.format(new Date());
				String start = format + " 00:00:00";
				String end = format + " 23:59:59";
				result[0] = Const.SDFCOMMON.parse(start).getTime() + "";
				result[1] = Const.SDFCOMMON.parse(end).getTime() + "";
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return result;
	}

	/**
	 * 获取数字随机数
	 * @param count：随机数的位数
	 * @return
	 * @author liuyunlong
	 * @version 2018年1月5日下午3:31:04
	 */
	public static String getRandomNum(int count) {
		String str = "0123456789";
		StringBuilder sb = new StringBuilder(count);
		for (int i = 0; i < 4; i++) {
			char ch = str.charAt(new Random().nextInt(str.length()));
			sb.append(ch);
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		System.out.println(String.format("start=%s;end=%s", getTimeStampByDate("2018-02-01")[0], getTimeStampByDate("2018-02-01")[1]));
		System.out.println(getRandomNum(4));
	}
}
