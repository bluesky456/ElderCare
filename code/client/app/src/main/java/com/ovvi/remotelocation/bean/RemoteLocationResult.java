package com.ovvi.remotelocation.bean;

public class RemoteLocationResult {

    private int id;

    private String longitude;

    private String latitude;

    /** 街道描述 */
    private String descStreet;
    /** 具体位置描述 */
    private String descLocation;

    private String createTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getDescStreet() {
        return descStreet;
    }

    public void setDescStreet(String descStreet) {
        this.descStreet = descStreet;
    }

    public String getDescLocation() {
        return descLocation;
    }

    public void setDescLocation(String descLocation) {
        this.descLocation = descLocation;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

}
