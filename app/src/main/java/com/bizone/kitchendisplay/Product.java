package com.bizone.kitchendisplay;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class Product {
    String name;
    Double quantity;

    public Product(@NonNull JSONObject obj) throws JSONException {
        this.name = obj.getString("showname");
        this.quantity = obj.getDouble("Quantity");
    }

}
