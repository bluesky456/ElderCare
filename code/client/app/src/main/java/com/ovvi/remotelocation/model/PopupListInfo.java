package com.ovvi.remotelocation.model;

public class PopupListInfo {

    private int id;
    private String name;
    private String phone;
    /** 成员用户头像 */
    private String portrait;

    public PopupListInfo(String portrait, String name) {
        this.portrait = portrait;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

}
