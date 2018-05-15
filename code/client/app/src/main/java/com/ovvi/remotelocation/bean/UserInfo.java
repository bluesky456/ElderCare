package com.ovvi.remotelocation.bean;

/**
 * 用户信息
 * @author chensong
 *
 */
public class UserInfo {

    /** 用户名(手机号) */
    private String userName;
    /** 昵称 */
    private String nickname;
    /** 头像 */
    private String portrait;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

}
