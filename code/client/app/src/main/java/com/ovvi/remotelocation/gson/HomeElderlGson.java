package com.ovvi.remotelocation.gson;

import com.ovvi.remotelocation.bean.HomeElderlResult;

/**
 * 老人端主界面请求响应
 * @author chensong
 *
 */
public class HomeElderlGson {

    /** 响应码 */
    private int code;
    /** 响应的内容 */
    private String msg;
    /** 结果 */
    private HomeElderlResult result;

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

    public HomeElderlResult getResult() {
        return result;
    }

    public void setResult(HomeElderlResult result) {
        this.result = result;
    }

}
