package com.tommybear.batcitycrossfit;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

import static android.R.attr.author;

/**
 * Created by dot on 8/11/2017.
 */

public  class CheckIns2 {
    public String checkInDate;
    public String name;
    public String latitide;
    public String longitude;
    public String time;


    public CheckIns2() {
    }

    public CheckIns2(String CheckInDate, String latitide, String longitude, String name, String time) {
        this.checkInDate = checkInDate;
        this.latitide = latitide;
        this.longitude = longitude;
        this.name = name;
        this.time = time;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("checkInDate", checkInDate);
        result.put("latitude", latitide);
        result.put("longitude", longitude);
        result.put("name", name);
        result.put("time", time);

        return result;
    }
}
