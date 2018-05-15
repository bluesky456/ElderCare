package com.ovvi.remotelocation.model;



public class Location {

    public int id;
    public String number;
    public String locationAddress;
    /**经度*/
    public double locationLongitude;
    /**纬度*/
    public double locationLatitude;
    
    public String locationDateTime;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public double getLocationLongitude() {
        return locationLongitude;
    }

    public void setLocationLongitude(double locationLongitude) {
        this.locationLongitude = locationLongitude;
    }

    public double getLocationLatitude() {
        return locationLatitude;
    }

    public void setLocationLatitude(double locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    public String getLocationDateTime() {
        return locationDateTime;
    }

    public void setLocationDateTime(String locationDateTime) {
        this.locationDateTime = locationDateTime;
    }

    
}
