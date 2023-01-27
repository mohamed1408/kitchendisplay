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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//import com.microsoft.signalr;
public class MainActivity extends AppCompatActivity {

    Button now_btn;
    Button future_btn;
    SwipeRefreshLayout swipeRefreshLayout;
    HubConnection hubConnection;
    ArrayList<Kot> KOTs = new ArrayList<>();
    ArrayList<Kot> nKOTs = new ArrayList<>();
    ArrayList<Kot> fKOTs = new ArrayList<>();
    OkHttpClient client = new OkHttpClient();
    KOTsViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        adapter = new KOTsViewAdapter(this, KOTs);

        hubConnection = HubConnectionBuilder.create("https://biz1pos.azurewebsites.net/uphub").build();

        now_btn = (Button)findViewById(R.id.now_btn);
        future_btn = (Button)findViewById(R.id.future_btn);

        String url = "https://biz1pos.azurewebsites.net/api/KOT/GetStoreKots?storeId=22&orderid=0&kotGroupId=15";
        ListView listView = (ListView) findViewById(R.id.kot_list);
        listView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FetchItems();
            }
        });
        // retry_btn.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View view) {
        //         if(hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED){
        //             Log.i("SIGNAL_R_DELORDUPDATE", "Starting SIGNAL_R");
        //             hubConnection.start();
        //         } else if(hubConnection.getConnectionState() == HubConnectionState.CONNECTED){
        //             hubConnection.send("joinroom", "androidroom");
        //             Log.i("SIGNAL_R_DELORDUPDATE", hubConnection.getConnectionId());
        //         }
        //     }
        // });
        // hubConnection.on("DeliveryOrderUpdate", (a, b, c, d, e) -> {
        //     Log.i("SIGNAL_R_DELORDUPDATE", c);
        // },Integer.class, Integer.class, String.class, String.class, Integer.class);
        // hubConnection.on("JoinMessage", (a) -> {
        //     Log.i("SIGNAL_R_JOINMESSAGE", a);
        // },String.class);
        FetchItems();
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
                        nKOTs.clear();
                        fKOTs.clear();
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

                            LocalDateTime delDate = LocalDateTime.parse(kot.deliverydatetime);
                            LocalDate localDate = LocalDate.now();
                            LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX);
//                            Log.i("KOTS_DATE_CHECK", endOfDay.toString() + " " + delDate);
                            if(endOfDay.isAfter(delDate)){
                                Log.i("KOTS_DATE_CHECK", "AFter True " + kot.deliverydatetime + " " + kot.invoiceno);
                                nKOTs.add(kot);
                            } else {
                                Log.i("KOTS_DATE_CHECK", "AFter Fals " + kot.deliverydatetime + " " + kot.invoiceno);
                                fKOTs.add(kot);
                            }
                            // KOTs.add(kot);
                        }
                        nKOTs.sort((a,b) -> a.deliverydatetime.compareTo(b.deliverydatetime));
                        fKOTs.sort((a,b) -> a.deliverydatetime.compareTo(b.deliverydatetime));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.clear();
                                adapter.addAll(nKOTs);
                                adapter.notifyDataSetChanged();
                                now_btn.setEnabled(false);
                                future_btn.setEnabled(true);
                            }
                        });
                    } catch (JSONException e) {
                        Log.i("KOTS_DATE_CHECK", "Error");
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void viewNowKots(View v) {
        Log.i("KOTS_DATE_CHECK", "Viewing Now KOTs");
        adapter.clear();
        adapter.addAll(nKOTs);
        adapter.notifyDataSetChanged();
        now_btn.setEnabled(false);
        future_btn.setEnabled(true);
    }

    public void viewFutureKots(View v) {
        Log.i("KOTS_DATE_CHECK", "Viewing Future KOTs");
        adapter.clear();
        adapter.addAll(fKOTs);
        adapter.notifyDataSetChanged();
        now_btn.setEnabled(true);
        future_btn.setEnabled(false);
    }

    public void updateList(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
}