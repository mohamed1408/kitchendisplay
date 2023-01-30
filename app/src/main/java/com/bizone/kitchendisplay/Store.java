package com.bizone.kitchendisplay;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class Store {
    String name;
    Integer id, companyid;

    public Store(@NonNull JSONObject obj) throws JSONException {
        this.name = obj.getString("Name");
        this.id = obj.getInt("Id");
        this.companyid = obj.getInt("CompanyId");
    }
}
