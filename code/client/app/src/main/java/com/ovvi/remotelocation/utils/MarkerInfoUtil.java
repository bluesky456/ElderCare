package com.ovvi.remotelocation.utils;

import java.io.Serializable;

/**
 * 覆盖物信息
 * 
 * @author chensong
 * 
 */
public class MarkerInfoUtil implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 纬度 */
    private double latitude;
    /** 经度 */
    private double longitude;
    /** 街道描述 */
    private String descStreet;
    /** 位置描述 */
    private String descLocation;
    /** 创建时间 */
    private String createTime;

    public MarkerInfoUtil() {
    }

    public MarkerInfoUtil(double latitude, double longitude, String descStreet,
            String descLocation, String createTime) {

        this.latitude = latitude;
        this.longitude = longitude;
        this.descStreet = descStreet;
        this.descLocation = descLocation;
        this.createTime = createTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}
