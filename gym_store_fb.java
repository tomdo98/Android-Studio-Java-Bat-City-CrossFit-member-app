package com.tommybear.batcitycrossfit;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

import static android.R.attr.author;

/**
 * Created by dot on 8/11/2017.
 */

public  class gym_store_fb {
    public String username;
    public String items_purchased;
    public String datepurchased;
    public String cost;


    public gym_store_fb() {
    }

    public gym_store_fb(String username, String items_purchased, String datepurchased, String cost) {
        this.username = username;
        this.items_purchased = items_purchased;
        this.datepurchased = datepurchased;
        this.cost = cost;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("items_purchased", items_purchased);
        result.put("datepurchased", datepurchased);
        result.put("cost", cost);
        return result;
    }

}