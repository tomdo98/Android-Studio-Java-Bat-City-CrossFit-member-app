package com.tommybear.batcitycrossfit;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dot on 8/11/2017.
 */

public  class Class_rt_fb {
    public String date_class_hour;
    public String count;
    public String classmates;

    public Class_rt_fb() {
    }

    public Class_rt_fb(String date_class_hour, String count, String classmates) {
        this.date_class_hour = date_class_hour;
        this.count = count;
        this.classmates = classmates;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("date_class_hour", date_class_hour);
        result.put("count", count);
        result.put("classmates", classmates);
        return result;
    }

}