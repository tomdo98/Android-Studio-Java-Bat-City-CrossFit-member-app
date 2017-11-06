package com.tommybear.batcitycrossfit;

/**
 * Created by dot on 8/11/2017.
 */

public  class CheckIns {
    private String CheckInDate;
    private String name;
    private String latitide;
    private String longitude;
    private String time;


    public CheckIns() {
    }

    public CheckIns(String CheckInDate, String latitide, String longitude, String name, String time) {
        this.CheckInDate = CheckInDate;
        this.latitide = latitide;
        this.longitude = longitude;
        this.name = name;
        this.time = time;
    }

    public String getCheckInDate() {
        return CheckInDate;
    }

    public void setCheckInDate(String text) { this.CheckInDate = CheckInDate;}

    public String getlatitide() {
        return latitide;
    }

    public void setlatitide(String latitide) {
        this.name = latitide;
    }

    public String getlongitude() {
        return longitude;
    }

    public void setlongitude(String longitude) {
        this.name = longitude;
    }

    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }

    public String gettime() {
        return time;
    }

    public void settime(String time) {
        this.name = time;
    }
}
