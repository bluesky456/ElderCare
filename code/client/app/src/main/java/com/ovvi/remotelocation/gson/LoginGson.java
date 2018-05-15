package com.ovvi.remotelocation.gson;

import com.ovvi.remotelocation.bean.TokenResult;

public class LoginGson {

    private int code;
    private String msg;
    private TokenResult result;

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

    public TokenResult getResult() {
        return result;
    }

    public void setResult(TokenResult result) {
        this.result = result;
    }

}
