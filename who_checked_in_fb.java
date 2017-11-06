package com.tommybear.batcitycrossfit;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dot on 8/11/2017.
 */

public  class who_checked_in_fb {
    public String date_class_hour;
    public String username;


    public who_checked_in_fb() {
    }

    public who_checked_in_fb(String date_class_hour, String username) {
        this.date_class_hour = date_class_hour;
        this.username = username;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("date_class_hour", date_class_hour);
        result.put("username", username);
        return result;
    }

}