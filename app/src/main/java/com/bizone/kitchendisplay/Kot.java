package com.bizone.kitchendisplay;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;

public class Kot {
    String invoiceno, deliverydatetime, instructions, ordername, note;
    Long createdtimestamp;
    Integer statusid, kotno, ordertypid, kotid;
    ArrayList<Product> added, removed;
    boolean isloading;

    public void populate(JSONObject obj) throws JSONException {
        this.invoiceno = obj.getString("invoiceno");
        this.deliverydatetime = obj.getString("DeliveryDateTime");
        this.instructions = obj.getString("Instruction");
        this.ordername = "Delivery";
        this.note = obj.getString("Note");

        this.createdtimestamp = obj.getLong("kotTimeStamp");

        this.statusid = obj.getInt("KOTStatusId");
        this.kotno = obj.getInt("KOTNo");
        this.ordertypid = obj.getInt("ordertypeid");
        this.kotid = obj.getInt("Id");

        this.isloading = false;

        this.added = new ArrayList<Product>();
        this.removed = new ArrayList<Product>();

        JSONArray addeditems = obj.getJSONArray("added");
        JSONArray removeditems = obj.getJSONArray("removed");

        for (int i=0; i<addeditems.length(); i++){
            added.add(new Product(addeditems.getJSONObject(i)));
        }
        for (int i=0; i<removeditems.length(); i++){
            removed.add(new Product(removeditems.getJSONObject(i)));
        }
    }
}
