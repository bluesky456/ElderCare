package com.ovvi.remotelocation.utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ovvi.remotelocation.base.Common;
import com.ovvi.remotelocation.bean.Notice;

public class HttpUtil {
    private static final String TAG = "HttpUtil";

    private static GsonBuilder builder = new GsonBuilder();
    private static Gson gson = builder.create();
    private static String entryString;

    static OkHttpClient client;
    private onResponseCode responseCode;

    public HttpUtil(Context context) {
        client = new OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS)
                .build(); // 设置各种超时时间

    }

    /**
     * 消息批量处理
     * 
     * @param notices
     * @return
     */
    private String entityStringtoJson(List<Notice> notices) {
        Type type = new TypeToken<List<Notice>>() {
        }.getType();
        String jsonString = gson.toJson(notices, type);
        return jsonString;
    }

    public void postRequests(int taskId, String url, HashMap<String, Object> paramMap,
            HashMap<String, String> headMap) {
        if (null == headMap || headMap.isEmpty()) {
            return;
        }

        if (null == paramMap || paramMap.isEmpty()) {
            return;
        }

        Builder requestBuilder = new Request.Builder().url(url);
        // 将请求头以键值对形式添加，可添加多个请求头
        RequestBody requestBodyPost = null;
        requestBuilder.addHeader(
                "vt",
                EncryptorUtil.encrypt("RSDdwp9840##DSG2d" + "|"
                        + System.currentTimeMillis()));
        if (taskId != Common.task.register && taskId != Common.task.login
                && taskId != Common.task.question && taskId != Common.task.seek_pwd) {
            requestBuilder.addHeader("tk", headMap.get("token"));
        }

        try {
            requestBuilder.post(RequestBody.create(
                    MediaType.parse("application/x-www-form-urlencoded; charset=utf-8"),
                    getRequestData(paramMap).toString().getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            LogUtils.d(TAG, "post error:" + e);
        }

        Request request = requestBuilder.build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtils.d(TAG, "e=" + e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String responseData = response.body().string();
                LogUtils.d(TAG, " responseData=" + responseData);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        responseCode.setPostResponse(responseData);
                    }
                }).start();
            }
        });
    }

    private static StringBuffer getRequestData(Map<String, Object> params) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            for (Entry<String, Object> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey()).append("=")
                        .append(URLEncoder.encode(entry.getValue().toString()))
                        .append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }

    /**
     * 
     * @param url
     * @param map
     */
    public void postRequest(final int taskId, final String url,
            final HashMap<String, String> map) {
        entryString = "RSDdwp9840##DSG2d" + "|" + System.currentTimeMillis();
        final Request.Builder builder = new Request.Builder().url(url);

        RequestBody requestBodyPost = null;

        switch (taskId) {
        case Common.task.register: {
            if (CommonUtil.isOldMode()) {
                requestBodyPost = new FormBody.Builder()
                        .add("userName", map.get("userName"))
                        .add("password", map.get("password"))
                        .add("nickname", map.get("nickname"))
                        .add("brand", map.get("brand"))
                        .add("product", map.get("product")).add("imei", map.get("imei"))
                        .add("type", map.get("type")).build();
            } else {
                requestBodyPost = new FormBody.Builder()
                        .add("userName", map.get("userName"))
                        .add("password", map.get("password"))
                        .add("nickname", map.get("nickname"))
                        .add("brand", map.get("brand"))
                        .add("product", map.get("product")).add("imei", map.get("imei"))
                        .add("type", map.get("type")).add("qcode", map.get("qcode"))
                        .add("answer", map.get("answer")).build();
            }
            break;
        }
        case Common.task.login: {
            requestBodyPost = new FormBody.Builder().add("userName", map.get("userName"))
                    .add("password", map.get("password")).add("type", map.get("type"))
                    .build();
            break;
        }
        case Common.task.location_report: {
            requestBodyPost = new FormBody.Builder()
                    .add("longitude", map.get("longitude"))
                    .add("latitude", map.get("latitude"))
                    .add("descStreet", map.get("descStreet"))
                    .add("descLocation", map.get("descLocation")).build();
            break;
        }
        case Common.task.settings: {
            requestBodyPost = new FormBody.Builder().build();
            break;
        }
        case Common.task.home: {
            requestBodyPost = new FormBody.Builder().build();
            break;
        }
        case Common.task.family_add: {
            requestBodyPost = new FormBody.Builder().add("phoneNum", map.get("phoneNum"))
                    .add("label", map.get("label")).build();
            break;
        }
        case Common.task.family_list:
            requestBodyPost = new FormBody.Builder().build();
            break;

        case Common.task.family_del: {
            requestBodyPost = new FormBody.Builder().add("uid", map.get("uid")).build();
            break;
        }
        case Common.task.elabel: {
            requestBodyPost = new FormBody.Builder().add("id", map.get("id"))
                    .add("label", map.get("label")).build();
            break;
        }
        case Common.task.portrait: {
            requestBodyPost = new FormBody.Builder().add("file", map.get("file")).build();
            break;
        }
        case Common.task.notice_ask: {
            requestBodyPost = new FormBody.Builder().build();
            break;
        }
        case Common.task.notice_report: {
            requestBodyPost = new FormBody.Builder().add("nid", map.get("nid"))
                    .add("state", map.get("state")).add("type", map.get("type"))
                    .add("result", map.get("result")).build();
            break;
        }
        case Common.task.remote_ask: {
            requestBodyPost = new FormBody.Builder().add("toId", map.get("toId")).build();
            break;
        }
        case Common.task.remote_receive: {
            requestBodyPost = new FormBody.Builder().add("uid", map.get("uid")).build();
            break;
        }
        case Common.task.history: {
            if (CommonUtil.isOldMode()) {
                requestBodyPost = new FormBody.Builder().add("createTime",
                        map.get("createTime")).build();
            } else {
                requestBodyPost = new FormBody.Builder().add("uid", map.get("uid"))
                        .add("createTime", map.get("createTime")).build();
            }
            break;
        }
        case Common.task.fence: {
            requestBodyPost = new FormBody.Builder().add("uid", map.get("uid")).build();
            break;
        }
        case Common.task.userinfo: {
            requestBodyPost = new FormBody.Builder().add("nickname", map.get("nickname"))
                    .add("portrait", map.get("portrait")).build();
            break;
        }
        case Common.task.question: {
            requestBodyPost = new FormBody.Builder().add("phoneNum", map.get("phoneNum"))
                    .build();
            break;
        }
        case Common.task.seek_pwd: {
            requestBodyPost = new FormBody.Builder().add("phoneNum", map.get("phoneNum"))
                    .add("password", map.get("password"))
                    .add("answer", map.get("answer")).build();
            break;
        }
        default:
            break;
        }
        // 将请求头以键值对形式添加，可添加多个请求头
        builder.addHeader("vt", EncryptorUtil.encrypt(entryString));
        if (taskId != Common.task.register && taskId != Common.task.login
                && taskId != Common.task.question && taskId != Common.task.seek_pwd) {
            builder.addHeader("tk", map.get("token"));
        }
        builder.post(requestBodyPost);
        Request requestPost = builder.build();

        LogUtils.d(TAG, "requestPost=" + requestPost);
        client.newCall(requestPost).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtils.d(TAG, "onFailure e=" + e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String responseData = response.body().string();
                LogUtils.d(TAG, " responseData=" + responseData);

                responseCode.setPostResponse(responseData);
            }
        });

    }

    public void setResponse(onResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public interface onResponseCode {

        public void setPostResponse(final String responseData);

    }

    public void upLoadFile(String actionUrl, HashMap<String, Object> map) {
        try {
            // 补全请求地址
            String requestUrl = actionUrl;
            MultipartBody.Builder builder = new MultipartBody.Builder();
            // 设置类型
            builder.setType(MultipartBody.FORM);
            // 追加参数
            for (String key : map.keySet()) {
                Object object = map.get(key);
                if (!(object instanceof File)) {
                    builder.addFormDataPart(key, object.toString());
                } else {
                    File file = (File) object;
                    builder.addFormDataPart(key, file.getName(),
                            RequestBody.create(null, file));
                }
            }
            // 创建RequestBody
            RequestBody body = builder.build();
            // 设置header头
            Builder requestBuilder = new Request.Builder();
            entryString = "RSDdwp9840##DSG2d" + "|" + System.currentTimeMillis();
            requestBuilder.addHeader("vt", EncryptorUtil.encrypt(entryString));
            requestBuilder.addHeader("tk", (String) map.get("token"));
            // 创建Request
            final Request request = requestBuilder.url(requestUrl).post(body).build();
            // 单独设置参数 比如读取超时时间
            final Call call = new OkHttpClient.Builder()
                    .writeTimeout(50, TimeUnit.SECONDS).build().newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LogUtils.d(TAG, "exception=" + e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData = response.body().string();
                    responseCode.setPostResponse(responseData);
                }
            });
        } catch (Exception e) {
            LogUtils.d(TAG, String.format("exception，e=%s", e.toString()));
        }
    }

}
