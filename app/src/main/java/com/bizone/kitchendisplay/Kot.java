package com.bizone.kitchendisplay;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;

public class Kot {
    String invoiceno, deliverydatetime, instructions, ordername, note, customername;
    Long createdtimestamp;
    Integer statusid, kotno, ordertypid, kotid;
    ArrayList<Product> added, removed;
    boolean isloading, isitemrendered;

    public void populate(JSONObject obj, JSONObject customerDetails) throws JSONException {
        Log.i("KOT_CONTRUCTOR", String.valueOf(obj.toString().contains("CustomerDetails")));
//        JSONObject customerDetails = obj.getJSONObject("CustomerDetails");

        this.customername = customerDetails.getString("Name");
        this.invoiceno = obj.getString("invoiceno");
        this.deliverydatetime = obj.getString("DeliveryDateTime");
        this.instructions = obj.getString("Message");
        this.note = obj.getString("Note");

        this.createdtimestamp = obj.getLong("kotTimeStamp");

        this.statusid = obj.getInt("KOTStatusId");
        this.kotno = obj.getInt("KOTNo");
        this.ordertypid = obj.getInt("ordertypeid");
        this.kotid = obj.getInt("Id");

        this.isloading = false;

        this.added = new ArrayList<Product>();
        this.removed = new ArrayList<Product>();

        switch (ordertypid) {
            case 1:
                this.ordername = "DINE IN";
                break;
            case 2:
                this.ordername = "TAKE AWAY";
                break;
            case 3:
                this.ordername = "DELIVERY";
                break;
            case 4:
                this.ordername = "PICK UP";
                break;
            case 5:
                this.ordername = "COUNTER";
                break;
            case 6:
                this.ordername = "ONLINE ORDER";
                break;
            case 7:
                this.ordername = "FB ONLINE ORDER";
        }

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
