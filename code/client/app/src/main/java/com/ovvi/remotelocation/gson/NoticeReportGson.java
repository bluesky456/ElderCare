package com.ovvi.remotelocation.gson;

public class NoticeReportGson {
    public int nid;
    public int state;
    public int type;
    public int result;
    public String longitude;
    public String latitude;
    public String descStreet;
    public String descLocation;

    public NoticeReportGson(int nid, int state, int type, int result, String longitude,
            String latitude, String descStreet, String descLocation) {
        this.nid = nid;
        this.state = state;
        this.type = type;
        this.result = result;
        this.longitude = longitude;
        this.latitude = latitude;
        this.descStreet = descStreet;
        this.descLocation = descLocation;
    }
}
