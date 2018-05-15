package com.ovvi.remotelocation.bean;

public class Members {

    /** 成员用户ID */
    private int id;
    /** 成员用户名(手机号) */
    private String userName;
    /** 成员用户备注名 */
    private String label;
    /** 成员用户头像 */
    private String portrait;
    /** 成员关系状态：0-待确认；1-同意；2-拒绝 */
    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

}
