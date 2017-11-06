package com.tommybear.batcitycrossfit;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

import static android.R.attr.author;

/**
 * Created by dot on 8/11/2017.
 */

public  class SignInRunningBalance {
    public String username;
    public String last_updated;
    public String balance;
    public String xaweek;
    public String active;
    public String messagetoclient;
    public String weekofyear;
    public String timesthisweek;

    public SignInRunningBalance() {
    }

    public SignInRunningBalance(String username, String last_updated, String balance, String xaweek, String active, String messagetoclient, String weekofyear, String timesthisweek) {
        this.username = username;
        this.last_updated = last_updated;
        this.balance = balance;
        this.xaweek = xaweek;
        this.active = active;
        this.messagetoclient = messagetoclient;
        this.weekofyear = weekofyear;
        this.timesthisweek = timesthisweek;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("last_updated", last_updated);
        result.put("balance", balance);
        result.put("xaweek", xaweek);
        result.put("active", active);
        result.put("messagetoclient", messagetoclient);
        result.put("weekofyear", weekofyear);
        result.put("timesthisweek", timesthisweek);
        return result;
    }

}