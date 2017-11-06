package com.tommybear.batcitycrossfit;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

import static android.R.attr.author;

/**
 * Created by dot on 8/11/2017.
 */

public  class PunchCardsFB {
    public String username;
    public String punchesleft;
    public String last_updated;


    public PunchCardsFB() {
    }

    public PunchCardsFB(String username, String punchesleft, String last_updated) {
        this.username = username;
        this.punchesleft = punchesleft;
        this.last_updated = last_updated;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("punchesleft", punchesleft);
        result.put("username_datebought", last_updated);
        return result;
    }

}