package com.ovvi.remotelocation.model;

public class AddressListInfo {

    private String addressDesc;
    private double longitude;
    private double latidude;

    public AddressListInfo(String addressDesc, double latidude, double longitude) {

        this.addressDesc = addressDesc;
        this.latidude = latidude;
        this.longitude = longitude;
    }

    public String getAddressDesc() {
        return addressDesc;
    }

    public void setAddressDesc(String addressDesc) {
        this.addressDesc = addressDesc;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatidude() {
        return latidude;
    }

    public void setLatidude(double latidude) {
        this.latidude = latidude;
    }

}
