/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: BrushApi 
 * @File: TestQtzImpl.java
 * @Author: liuyunlong 
 * @Date: 2017年11月10日 上午9:46:20
 * @version: V1.0
 * TODO
 * 
 * Modification  History:
 * Date         Author        Version        Discription
 * -----------------------------------------------------------------------------------

 * 2017年11月10日       yunlong.liu          1.0             1.0

 * Why & What is modified: <修改原因描述>
 *
 *************************************************************************************/
package com.ovvi.api.schedule.impl;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.ovvi.api.schedule.TestQtz;
import com.ovvi.api.utils.MD5Util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @description 
 * @author liuyunlong 
 * @date 2017年11月10日上午9:46:20
 * 
 */
@Component
public class TestQtzImpl implements TestQtz {

	private static final String url = "http://api.data.baidu.com/channel/api/checkimei";
	// private static final String url =
	// "http://api.data.baidu.com/channel/api/testcheck";
	private static final String channleName = "ddsr001";
	private static final String secretKey = "hello_channel";
	private static final Integer batchValue = 1000;
	private static final Integer isSixDayOkey = 7;;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/** 
	 * 
	 * @author liuyunlong
	 * @version 2017年11月10日上午9:46:20
	 */
	@Override
	@Scheduled(cron = "30 37 14 * * ? ") // 每天00:00
	public void excute() {
		String imei = "861708031960400";
		// String imei = null;
		List<String> allImeis = getImeis(imei);
		if (!CollectionUtils.isEmpty(allImeis)) {
			if (allImeis.size() > batchValue) {
				int times = allImeis.size() / batchValue;
				System.out.println(String.format("用户总数=%s; 每次执行=%s; 分批执行次数=%s", allImeis.size(), batchValue, times));
				List<String> all = new ArrayList<>();
				Map<String, String> imeiMap = new HashMap<>();
				for (int i = 1; i <= times; i++) {
					try {
						int start = (i - 1) * batchValue + 1;
						int end = i * batchValue;
						String imeis = "";
						for (int j = start; j <= end; j++) {
							imeis += MD5Util.getMD5String(allImeis.get(j)) + ",";
							imeiMap.put(MD5Util.getMD5String(allImeis.get(j)), allImeis.get(j));
						}
						imeis = imeis.substring(0, imeis.length() - 1);

						String param = getParam(imeis);
						String result = sendHttpPost(url, param);
						@SuppressWarnings("unchecked")
						Map<String, Object> fromJson = new Gson().fromJson(result, Map.class);
						if (null != fromJson) {
							@SuppressWarnings("unchecked")
							List<String> list = (List<String>) fromJson.get("data");
							if (!CollectionUtils.isEmpty(list)) {
								System.out.println(String.format("第%s批;匹配数量=%s", i, list.size()));
								for (int j = 0; j < list.size(); j++) {
									if (imeiMap.containsKey(list.get(j))) {
										System.out.println(String.format("imei匹配成功=%s", imeiMap.get(list.get(j))));
									}
								}
								all.addAll(list);
							}
						}
					} catch (Exception e) {
						System.out.println(String.format("异常批次=%s;异常=%s", i, e.toString()));
					}
				}
				System.out.println(String.format("手百沈默总数量=%s", all.size()));
			} else if (allImeis.size() == 1) {
				String imeis = MD5Util.getMD5String(allImeis.get(0));
				String param = getParam(imeis);
				String result = sendHttpPost(url, param);
				@SuppressWarnings("unchecked")
				Map<String, Object> fromJson = new Gson().fromJson(result, Map.class);
				if (null != fromJson) {
					@SuppressWarnings("unchecked")
					List<String> list = (List<String>) fromJson.get("data");
					if (!CollectionUtils.isEmpty(list)) {
						System.out.println(String.format("imei号=%s匹配成功", list.get(0)));
					} else {
						System.out.println(String.format("imei号匹配失败"));
					}
				}
			}
		}
	}

	/** 
	 * @return
	 * @author liuyunlong
	 * @version 2017年11月14日上午10:35:15
	 */
	private List<String> getImeis(String imei) {
		List<String> allImeis = new ArrayList<>();
		if (StringUtils.isEmpty(imei)) {
			String sql = "SELECT imei FROM hk_advert_register_user_sb WHERE type = ?";
			allImeis = jdbcTemplate.queryForList(sql, String.class, 2);
		} else {
			allImeis.add(imei);
		}
		return allImeis;
	}

	/** 
	 * @return
	 * @author liuyunlong
	 * @version 2017年11月10日上午10:02:16
	 * @param imeis 
	 */
	private String getParam(String imeis) {
		String millis = System.currentTimeMillis() + "";
		String sign = "channel_name=" + channleName + "imeis=" + imeis + "is_six_day_okey=" + isSixDayOkey + "request_time=" + millis + secretKey;
		sign = MD5Util.getMD5String(sign);
		return "channel_name=" + channleName + "&is_six_day_okey=" + isSixDayOkey + "&request_time=" + millis + "&sign=" + sign + "&imeis=" + imeis;
	}

	private String sendHttpPost(String href, String data) {
		try {
			URL url = new URL(href);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			OutputStream output = conn.getOutputStream();
			output.write(data.getBytes("utf-8"));
			output.flush();

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[4 * 1024];
			int count = -1;
			InputStream input = conn.getInputStream();
			while ((count = input.read(buffer)) != -1) { // 读取数据
				baos.write(buffer, 0, count);
			}
			String result = new String(baos.toByteArray(), "utf-8");
			// System.out.println(String.format("返回结果：%s", result));
			return result;
		} catch (Exception e) {
			System.out.println(String.format("发送请求异常；e=%s", e.toString()));
		}
		return "";
	}

	@Data
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	class RequestParam {
		private String imeis;
		private String channel_name;
		private String request_time;
		private String sign;
		private int is_six_day_okey;
	}

	@Data
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	class SingParams {
		private String imeis;
		private String channel_name;
		private String request_time;
		private int is_six_day_okey;
	}
}
