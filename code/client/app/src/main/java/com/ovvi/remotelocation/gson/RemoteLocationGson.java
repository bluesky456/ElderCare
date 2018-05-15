package com.ovvi.remotelocation.gson;

import com.ovvi.remotelocation.bean.RemoteLocationResult;

public class RemoteLocationGson {

    private int code;
    
    private String msg;
    
    private RemoteLocationResult result;

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

    public RemoteLocationResult getResult() {
        return result;
    }

    public void setResult(RemoteLocationResult result) {
        this.result = result;
    }
    
}
