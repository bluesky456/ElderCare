package com.ovvi.remotelocation.gson;

import com.ovvi.remotelocation.bean.SeekAccountResult;

public class SeekAccountGson {

    private int code;

    private String msg;

    private SeekAccountResult result;

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

    public SeekAccountResult getResult() {
        return result;
    }

    public void setResult(SeekAccountResult result) {
        this.result = result;
    }

}
