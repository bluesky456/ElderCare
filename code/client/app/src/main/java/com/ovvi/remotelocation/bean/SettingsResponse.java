package com.ovvi.remotelocation.bean;


public class SettingsResponse {

    /** 响应码 */
    private int code;
    /** 响应的内容 */
    private String msg;
    /** 结果 */
    private UserInfo result;

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

    public UserInfo getResult() {
        return result;
    }

    public void setResult(UserInfo result) {
        this.result = result;
    }

}
