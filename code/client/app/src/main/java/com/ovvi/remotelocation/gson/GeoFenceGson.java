package com.ovvi.remotelocation.gson;

import com.ovvi.remotelocation.bean.GeoFenceResult;

/**
 * 地理围栏的gson响应数据
 * 
 * @author chensong
 * 
 */
public class GeoFenceGson {

    private int code;

    private String msg;

    private GeoFenceResult result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public GeoFenceResult getResult() {
        return result;
    }

    public void setResult(GeoFenceResult result) {
        this.result = result;
    }

}
