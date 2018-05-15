package com.ovvi.remotelocation.gson;

import com.ovvi.remotelocation.bean.UserPhoto;


public class PhotoGson {

    private int code;

    private String msg;

    private UserPhoto result;

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

    public UserPhoto getResult() {
        return result;
    }

    public void setResult(UserPhoto portrait) {
        this.result = portrait;
    }

}
