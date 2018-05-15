/**
 * *********************************************************************************
 * @Copyright: Copyright(C) 2017 www.ovvitech.com Inc. All rights reserved. 
 * @Project: AdvertApiTest 
 * @File: OkHttpUtil.java
 * @Author: liuyunlong 
 * @Date: 2017年12月22日 下午4:03:18
 * @version: V1.0
 * TODO
 * 
 * Modification  History:
 * Date         Author        Version        Discription
 * -----------------------------------------------------------------------------------

 * 2017年12月22日       yunlong.liu          1.0             1.0

 * Why & What is modified: <修改原因描述>
 *
 *************************************************************************************/
package com.ovvi.api.utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.ovvi.api.jsonbean.ResponseBean;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @description 
 * @author liuyunlong 
 * @date 2017年12月22日下午4:03:18
 * 
 */
public class OkHttpUtil {

	private static final int timeout = 120000;
	private static final String VT_KEY = "RSDdwp9840##DSG2d";
	private static Gson gson = new Gson();
	private static final String HOST = "http://older.legalaxy.cn";
	private static final String TEST_HOST = "http://192.168.9.164:9001";
	private static final String LOCAL_HOST = "http://192.168.8.228:8080";

	public static void main(String[] args) throws Exception {
		// 父母
		// 87-lXPKx7lEerCOGirP6pSctuEa37IE5E81mmjLiFCozVcOvUcVFW1y9OjwxaPedM1T2BSXamUjq7V8qD1oxjyelg==
		// 88-rX2geVgzsb2pZj77pfCdV9buHhPagK8Cut4Y2tba6+YjGtbVKpJ3HGldjV+74Rw0V4nTIlCWbX3vdsH3M9yBEg==
		// 子女
		// 90-fWUMNjq8yBzhqf+PJ2O4jcxl+VQnkMR1lrU2sHPt/07/oEQW4/jv5yA14Aze5aViPMjf3Pco6Kq8VOanCMzr6A==
		// 91-vZl/QywO+KNQ0gKTZPFPScOspuHoGh93LBJ1PbAYPr37Ye0+zcX+xpno7okQp5as/gI3BpovrjInfUY9b0E5Mw==

		// String url = "http://192.168.9.164:9001/api/user/login";
		// String url = HOST + "/api/account/register";
		String url = HOST + "/api/account/login";
		// String url = LOCAL_HOST + "/api/account/question";
		// String url = LOCAL_HOST + "/api/account/pwd";

		// String url = "http://192.168.8.228:8080/api/location/report";
		// String url = LOCAL_HOST + "/api/location/remote/ask";
		// String url = "http://192.168.8.228:8080/api/location/remote/receive";
		// String url = "http://192.168.8.228:8080/api/location/fence";
		// String url = LOCAL_HOST + "/api/location/history";

		// String url = LOCAL_HOST + "/api/user/family/add";
		// String url = "http://192.168.8.228:8080/api/user/family/del";
		// String url = TEST_HOST + "/api/user/family/list";
		// String url = TEST_HOST + "/api/user/home";
		// String url = "http://192.168.8.228:8080/api/user/setting";
		// String url = "http://192.168.9.164:9001/api/user/einfo";
		// String url = "http://192.168.8.228:8080/api/user/elabel";
		// String url = TEST_HOST + "/api/user/notice/ask";
		// String url = LOCAL_HOST + "/api/user/notice/report";
		// String url = TEST_HOST + "/api/upload/portrait";
		// String url = "http://192.168.8.228:7777/index";

		// register and login
		Map<String, String> headerMap = new HashMap<String, String>();
		headerMap.put("vt", EncryptorUtil.encrypt(VT_KEY + "|" + System.currentTimeMillis()));
		headerMap.put("tk", "7UbKRpKCmrWJ0jhmSD8NrHYJEy/azwQoUoCP/lZ76s58ls+KgsRCk52i57x0yW2mXY8x3MJJtrTp3hnUkLmlHw==");

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userName", "18826890117");
		paramMap.put("nickname", "彩虹");
		paramMap.put("password", "1q2w3e4r5t");
		paramMap.put("brand", "ovvi");
		paramMap.put("product", "ovvi O6100");
		paramMap.put("imei", "8658425474554");
		paramMap.put("type", 2); // 1-老人；2-子女
		paramMap.put("qcode", 2); // 1.生日；2-大学校名；3-父亲姓名
		paramMap.put("answer", "中理工");

		// /api/account/question
		// paramMap.put("phoneNum", "18565245840");

		// /api/account/pwd
		// paramMap.put("phoneNum", "18565245840");
		// paramMap.put("answer", "19901023");
		// paramMap.put("password", "abcd1234");

		// location/report
		// paramMap.put("longitude", "6.2154475714");
		// paramMap.put("latitude", "3.215474554");

		// remote/ask
		// paramMap.put("toId", 87);

		// remote/receive
		// paramMap.put("uid", 18);

		// remote/fence
		// paramMap.put("uid", 18);

		// history
		// paramMap.put("uid", 38);
		paramMap.put("createTime", "2018-2-1");

		// family/add
		// paramMap.put("phoneNum", "18565245870");
		// paramMap.put("label", "芭比");

		// family/del
		// paramMap.put("uid", 17);

		// einfo
		// paramMap.put("nickname", "测试");
		// paramMap.put("portrait",
		// "http://192.168.9.164/res/portrait/1be23358e2c79932cb59e45d1860cac3.jpg");

		// elabel
		// paramMap.put("id", 18);
		// paramMap.put("label", "婆婆");

		// notice/report
		// NoticeReport n1 = new OkHttpUtil().new NoticeReport("58", (byte) 4,
		// (byte) 2, (byte) 1, "", "");
		// NoticeReport n2 = new OkHttpUtil().new NoticeReport("61", (byte) 4,
		// (byte) 1, (byte) 0, "542513.3245", "656844.3251");
		// List<NoticeReport> arr = new ArrayList<>();
		// arr.add(n1);
		// arr.add(n2);
		// paramMap.put("notices", gson.toJson(arr));

		// api/upload/portrait
		// paramMap.put("file", new File("F://ovvi//负责项目//老年关怀//beauty.jpg"));

		execOnce(url, paramMap, headerMap);

		// upLoadFile(url, paramMap, headerMap);
	}

	public static String execOnce(String url, Map<String, Object> params, Map<String, String> headers) {
		if (null == url || url.trim().isEmpty()) {
			return null;
		}
		Builder requestBuilder = new Request.Builder().url(url);
		if (null != headers && !headers.isEmpty()) {
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				requestBuilder.addHeader(entry.getKey(), getValueEncoded(entry.getValue()));
			}
		}
		if (null != params) {
			try {
				requestBuilder.post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded; charset=utf-8"), getRequestData(params).toString().getBytes("UTF-8")));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		Request request = requestBuilder.build();
		try {
			OkHttpClient.Builder builder = new OkHttpClient.Builder();
			builder.connectTimeout(timeout, TimeUnit.SECONDS);
			builder.readTimeout(timeout, TimeUnit.SECONDS);
			builder.retryOnConnectionFailure(true);
			Response response = builder.build().newCall(request).execute();
			if (response.isSuccessful()) {
				String result = response.body().string();
				response.body();
				ResponseBean<?> resultBean = gson.fromJson(result, ResponseBean.class);
				System.out.println(String.format("resultBean = %s", resultBean.toString()));
				System.out.println(String.format("请求地址=%s；响应状态=%s；响应长度=%s；响应内容=%s", request.url(), response.code(), result.length(), result));
				return result;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String getValueEncoded(String value) {
		if (value == null)
			return "null";
		String newValue = value.replace("\n", "");
		for (int i = 0, length = newValue.length(); i < length; i++) {
			char c = newValue.charAt(i);
			if (c <= '\u001f' || c >= '\u007f') {
				try {
					return URLEncoder.encode(newValue, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		return newValue;
	}

	@SuppressWarnings("deprecation")
	private static StringBuffer getRequestData(Map<String, Object> params) {
		StringBuffer stringBuffer = new StringBuffer();
		try {
			for (Entry<String, Object> entry : params.entrySet()) {
				stringBuffer.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue().toString())).append("&");
			}
			stringBuffer.deleteCharAt(stringBuffer.length() - 1); // 删除最后的一个"&"
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}

	/**
	 *上传文件
	 * @param actionUrl 接口地址
	 * @param paramsMap 参数
	 * @param callBack 回调
	 * @param <T>
	 */
	public static <T> void upLoadFile(String actionUrl, Map<String, Object> paramsMap, Map<String, String> headersMap) {
		try {
			// 补全请求地址
			String requestUrl = actionUrl;
			MultipartBody.Builder builder = new MultipartBody.Builder();
			// 设置类型
			builder.setType(MultipartBody.FORM);
			// 追加参数
			for (String key : paramsMap.keySet()) {
				Object object = paramsMap.get(key);
				if (!(object instanceof File)) {
					builder.addFormDataPart(key, object.toString());
				} else {
					File file = (File) object;
					builder.addFormDataPart(key, file.getName(), RequestBody.create(null, file));
				}
			}
			// 创建RequestBody
			RequestBody body = builder.build();
			// 设置header头
			Builder requestBuilder = new Request.Builder();
			for (String key : headersMap.keySet()) {
				requestBuilder.addHeader(key, headersMap.get(key).toString());
			}
			// 创建Request
			final Request request = requestBuilder.url(requestUrl).post(body).build();
			// 单独设置参数 比如读取超时时间
			final Call call = new OkHttpClient.Builder().writeTimeout(50, TimeUnit.SECONDS).build().newCall(request);
			call.enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					System.out.println("请求异常=" + e.toString());
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					if (response.isSuccessful()) {
						String string = response.body().string();
						System.out.println("响应结果=" + string);
					} else {
					}
				}
			});
		} catch (Exception e) {
			System.out.println(String.format("请求异常，e=%s", e.toString()));
		}
	}

	class NoticeReport {
		private String nid;
		private byte state;
		private byte type;
		private byte result;
		private String longitude;
		private String latitude;

		public String getNid() {
			return nid;
		}

		public void setNid(String nid) {
			this.nid = nid;
		}

		public byte getState() {
			return state;
		}

		public void setState(byte state) {
			this.state = state;
		}

		public byte getType() {
			return type;
		}

		public void setType(byte type) {
			this.type = type;
		}

		public byte getResult() {
			return result;
		}

		public void setResult(byte result) {
			this.result = result;
		}

		public String getLongitude() {
			return longitude;
		}

		public void setLongitude(String longitude) {
			this.longitude = longitude;
		}

		public String getLatitude() {
			return latitude;
		}

		public void setLatitude(String latitude) {
			this.latitude = latitude;
		}

		public NoticeReport(String nid, byte state, byte type, byte result, String longitude, String latitude) {
			super();
			this.nid = nid;
			this.state = state;
			this.type = type;
			this.result = result;
			this.longitude = longitude;
			this.latitude = latitude;
		}
	}
}
