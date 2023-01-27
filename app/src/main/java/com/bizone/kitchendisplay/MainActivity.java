package com.bizone.kitchendisplay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//import com.microsoft.signalr;
public class MainActivity extends AppCompatActivity {

    Button retry_btn;
    Button future_btn;
    SwipeRefreshLayout swipeRefreshLayout;
    HubConnection hubConnection;
    ArrayList<Kot> KOTs = new ArrayList<>();
    OkHttpClient client = new OkHttpClient();
    KOTsViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        adapter = new KOTsViewAdapter(this, KOTs);
//        String[] mobileArray = {"Android","IPhone","WindowsMobile","Blackberry","WebOS","Ubuntu","Windows7","Max OS X","Android","IPhone","WindowsMobile","Blackberry","WebOS","Ubuntu","Windows7","Max OS X","Android","IPhone","WindowsMobile","Blackberry","WebOS","Ubuntu","Windows7","Max OS X","Android","IPhone","WindowsMobile","Blackberry","WebOS","Ubuntu","Windows7","Max OS X","Android","IPhone","WindowsMobile","Blackberry","WebOS","Ubuntu","Windows7","Max OS X"};

        hubConnection = HubConnectionBuilder.create("https://biz1pos.azurewebsites.net/uphub").build();

        retry_btn = (Button)findViewById(R.id.now_btn);
        future_btn = (Button)findViewById(R.id.future_btn);

        String url = "https://biz1pos.azurewebsites.net/api/KOT/GetStoreKots?storeId=22&orderid=0&kotGroupId=15";
        ListView listView = (ListView) findViewById(R.id.kot_list);
        listView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                // User defined method to shuffle the array list items
                FetchItems();
            }
        });
        retry_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED){
                    Log.i("SIGNAL_R_DELORDUPDATE", "Starting SIGNAL_R");
                    hubConnection.start();
                } else if(hubConnection.getConnectionState() == HubConnectionState.CONNECTED){
                    hubConnection.send("joinroom", "androidroom");
                    Log.i("SIGNAL_R_DELORDUPDATE", hubConnection.getConnectionId());
                }
            }
        });
//        future_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Request req = new Request.Builder()
//                        .url(url)
//                        .build();
//                client.newCall(req).enqueue(new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        Log.i("OkHttp_log_error", String.valueOf(e));
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        if(response.isSuccessful()) {
////                            String str_body = response.body().string();
////                            Log.i("OkHttp_log_success", str_body);
////                            Log.i("OkHttp_log_success", response.body().toString());
//                            try {
//                                JSONArray array = new JSONArray(response.body().string());
//                                JSONArray kots = new JSONArray();
////                                JSONObject jsObject = new JSONObject(response.body().toString());
////                                JSONArray arr = new JSONArray(response.body());
//                                for (int i=0; i<array.length(); i++) {
//                                    JSONObject object = array.getJSONObject(i);
//
//                                    String DeliveryDateTime = object.getString("DeliveryDateTime");
//                                    String Note = object.getString("Note");
//                                    String json = object.getString("json");
//                                    Integer Id = object.getInt("Id");
//
//                                    JSONObject jsObject = new JSONObject(json);
//
//                                    if(!jsObject.has("kotTimeStamp")){
//                                        Long stamp = Long.valueOf(0);
//                                        jsObject.put("kotTimeStamp",stamp);
//                                    }
//
//                                    jsObject.put("DeliveryDateTime",DeliveryDateTime);
//                                    jsObject.put("Id",Id);
//                                    jsObject.put("Note",Note);
//                                    Kot kot = new Kot();
//                                    kot.populate(jsObject);
//                                    KOTs.add(kot);
//                                    adapter.notifyDataSetChanged();
//                                }
//                                for (int i=0; i<KOTs.size(); i++) {
//                                    Log.i("OkHttp_log_success", KOTs.get(i).invoiceno);
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                });
//            }
//        });
        hubConnection.on("DeliveryOrderUpdate", (a, b, c, d, e) -> {
            Log.i("SIGNAL_R_DELORDUPDATE", c);
        },Integer.class, Integer.class, String.class, String.class, Integer.class);
        hubConnection.on("JoinMessage", (a) -> {
            Log.i("SIGNAL_R_JOINMESSAGE", a);
        },String.class);

//        ArrayAdapter adapter = new ArrayAdapter<String>(this,
//                R.layout.activity_listview, mobileArray);

    }
    //METHOD WHICH WILL HANDLE DYNAMIC INSERTION
    public void FetchItems() {

        String url = "https://biz1pos.azurewebsites.net/api/KOT/GetStoreKots?storeId=22&orderid=0&kotGroupId=15";
        Request req = new Request.Builder()
                .url(url)
                .build();
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                swipeRefreshLayout.setRefreshing(false);
                Log.i("OkHttp_log_error", String.valueOf(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                swipeRefreshLayout.setRefreshing(false);
                if(response.isSuccessful()) {
                    try {
                        JSONArray array = new JSONArray(response.body().string());
                        JSONArray kots = new JSONArray();
                        for (int i=0; i<array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);

                            String DeliveryDateTime = object.getString("DeliveryDateTime");
                            String Note = object.getString("Note");
                            String json = object.getString("json");
                            Integer Id = object.getInt("Id");

                            JSONObject jsObject = new JSONObject(json);

                            JSONArray addeditems = jsObject.getJSONArray("added");
                            JSONArray removeditems = jsObject.getJSONArray("removed");

                            if(!jsObject.has("kotTimeStamp")){
                                Long stamp = Long.valueOf(0);
                                jsObject.put("kotTimeStamp",stamp);
                            }

                            jsObject.put("DeliveryDateTime",DeliveryDateTime);
                            jsObject.put("Id",Id);
                            jsObject.put("Note",Note);
                            jsObject.put("added", addeditems);
                            jsObject.put("removed", removeditems);
                            Kot kot = new Kot();
                            kot.populate(jsObject);
                            KOTs.add(kot);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                        for (int i=0; i<KOTs.size(); i++) {
                            Log.i("OkHttp_log_success", KOTs.get(i).invoiceno);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}