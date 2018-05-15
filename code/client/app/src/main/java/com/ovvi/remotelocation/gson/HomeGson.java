package com.ovvi.remotelocation.gson;

import com.ovvi.remotelocation.bean.HomeResult;

public class HomeGson {

    /** 响应码 */
    private int code;
    /** 响应的内容 */
    private String msg;
    /** 结果 */
    private HomeResult result;

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

    public HomeResult getResult() {
        return result;
    }

    public void setResult(HomeResult result) {
        this.result = result;
    }

}
