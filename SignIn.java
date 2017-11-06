package com.tommybear.batcitycrossfit;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

import static android.R.attr.author;

/**
 * Created by dot on 8/11/2017.
 */

public  class SignIn {
    public String username;
    public String date_checkin;
    public String location;
    public String type;
    public String balance_snap;
    public String date_hour;


    public SignIn() {
    }

    public SignIn(String username, String date_checkin, String location ,String type, String balance_snap, String date_hour) {
        this.username = username;
        this.date_checkin = date_checkin;
        this.location = location;
        this.type = type;
        this.balance_snap = balance_snap;
        this.date_hour = date_hour;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("date_checkin", date_checkin);
        result.put("location", location);
        result.put("type", type);
        result.put("balance_snap", balance_snap);
        result.put("date_hour", date_hour);
        return result;
    }

}