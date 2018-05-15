package com.ovvi.remotelocation.gson;

import java.util.List;

import com.ovvi.remotelocation.bean.HistoryTrackResult;

/**
 * 历史轨迹请求响应
 * @author chensong
 *
 */
public class HistoryTrackGson {

    private int code;

    private String msg;

    private List<HistoryTrackResult> result;

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

    public List<HistoryTrackResult> getResult() {
        return result;
    }

    public void setResult(List<HistoryTrackResult> result) {
        this.result = result;
    }

}
