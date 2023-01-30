package com.bizone.kitchendisplay;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class KOTGroup {
    Integer id;
    String description;

    public KOTGroup(@NonNull JSONObject obj) throws JSONException {
        this.description = obj.getString("Description");
        this.id = obj.getInt("Id");
    }
}
