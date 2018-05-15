package com.ovvi.remotelocation.gson;

import java.util.List;

import com.ovvi.remotelocation.bean.Notice;

public class NoticesGson {

    private int code;

    private String msg;

    private List<Notice> result;

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

    public List<Notice> getResult() {
        return result;
    }

    public void setResult(List<Notice> result) {
        this.result = result;
    }

}
