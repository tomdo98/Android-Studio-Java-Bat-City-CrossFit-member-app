package com.tommybear.batcitycrossfit;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

import static android.R.attr.author;

/**
 * Created by dot on 8/11/2017.
 */

public  class batcitymessage {
    public String username;
    public String datecomment;
    public String comment;


    public batcitymessage() {
    }

    public batcitymessage(String username, String datecomment, String comment) {
        this.username = username;
        this.datecomment = datecomment;
        this.comment = comment;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("datecomment", datecomment);
        result.put("comment", comment);
        return result;
    }

}