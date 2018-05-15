package com.ovvi.remotelocation.gson;

import java.util.List;

import com.ovvi.remotelocation.bean.Members;

public class MemberListGson {

    private int code;

    private String msg;

    private List<Members> result;

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

    public List<Members> getResult() {
        return result;
    }

    public void setResult(List<Members> result) {
        this.result = result;
    }

}
