package com.tommybear.batcitycrossfit;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dot on 8/11/2017.
 */

public  class card_stat_fb {
    public String username;
    public String stat_type;
    public String last_updated;


    public card_stat_fb() {
    }

    public card_stat_fb(String username, String stat_type, String last_updated) {
        this.username = username;
        this.stat_type = stat_type;
        this.last_updated = last_updated;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("punchesleft", stat_type);
        result.put("last_updated", last_updated);
        return result;
    }

}