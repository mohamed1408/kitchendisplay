package com.bizone.kitchendisplay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
    ArrayList<Kot> allKOTs = new ArrayList<>();
    ArrayList<Kot> nKOTs = new ArrayList<>();
    ArrayList<Kot> fKOTs = new ArrayList<>();
    OkHttpClient client = new OkHttpClient();
    KOTsViewAdapter adapter;
    Integer storeid, companyid, kotgroupid;
    String base_url, url;
    ToggleButton takeawaytg, deltg, pckuptg, change_filter, change_filter1, newtg, pendtg, compltg;
    LinearLayout status_filter, typ_filter;
    ListView listView;
    List<Integer> selected_types = new ArrayList<Integer>();
    List<Integer> selected_statuses = new ArrayList<Integer>();
    Intent loginI;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginI = new Intent(MainActivity.this, LoginActivity.class);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        companyid = preferences.getInt("companyid", 0);
        storeid = preferences.getInt("storeid", 0);
        kotgroupid = preferences.getInt("kotgroupid", 0);

        Log.i("GETTING_PREFS", "CompanyId: " + companyid + " StoreId: " + storeid + " KOTGroupId: " + kotgroupid);

        if(companyid == 0 || storeid == 0) {
            startActivity(loginI);
            return;
        }

        setContentView(R.layout.activity_main);

        base_url = "https://biz1pos.azurewebsites.net/";

        selected_types.addAll(Arrays.asList(2,3,4));
        selected_statuses.addAll(Arrays.asList(0,1,2,3,4));

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        listView = (ListView) findViewById(R.id.kot_list);

        now_btn = (Button)findViewById(R.id.now_btn);
        future_btn = (Button)findViewById(R.id.future_btn);

        deltg = (ToggleButton)findViewById(R.id.deltg);
        pckuptg = (ToggleButton)findViewById(R.id.pckuptg);
        takeawaytg = (ToggleButton)findViewById(R.id.takeawaytg);

        change_filter = (ToggleButton)findViewById(R.id.change_filter);
        change_filter1 = (ToggleButton)findViewById(R.id.change_filter1);

        newtg = (ToggleButton)findViewById(R.id.newtg);
        pendtg = (ToggleButton)findViewById(R.id.pendtg);
        compltg = (ToggleButton)findViewById(R.id.compltg);


        typ_filter = (LinearLayout) findViewById(R.id.typ_filter);
        status_filter = (LinearLayout) findViewById(R.id.status_filter);


        adapter = new KOTsViewAdapter(this, new ArrayList<Kot>());

        hubConnection = HubConnectionBuilder.create(base_url + "uphub").build();
        hubConnection.start();

        signalRconfig();

        url = base_url + "api/KOT/GetStoreKots?storeId=" + storeid + "&orderid=0&kotGroupId=" + kotgroupid;
        listView.setAdapter(adapter);

        newtg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    selected_statuses.add(0);
                } else {
                    selected_statuses.removeIf(x -> x == 0);
                }
                applyFilter();
            }
        });
        pendtg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    selected_statuses.addAll(Arrays.asList(1,2,3));
                } else {
                    selected_statuses.removeIf(x -> x >= 1 && x <= 3);
                }
                applyFilter();
            }
        });
        compltg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    selected_statuses.add(4);
                } else {
                    selected_statuses.removeIf(x -> x == 4);
                }
                applyFilter();
            }
        });

        change_filter1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                typ_filter.setVisibility(View.GONE);
                status_filter.setVisibility(View.VISIBLE);
            }
        });
        change_filter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                typ_filter.setVisibility(View.VISIBLE);
                status_filter.setVisibility(View.GONE);
            }
        });
        takeawaytg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    selected_types.add(2);
                } else {
                    selected_types.removeIf(x -> x == 2);
                }
                applyFilter();
            }
        });
        deltg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    selected_types.add(3);
                } else {
                    selected_types.removeIf(x -> x == 3);
                }
                applyFilter();
            }
        });
        pckuptg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    selected_types.add(4);
                } else {
                    selected_types.removeIf(x -> x == 4);
                }
                applyFilter();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("SIGNAL_R_STATE", hubConnection.getConnectionState().toString());
                if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED) {
                    hubConnection.start();
                }
                FetchItems();
            }
        });
        FetchItems();
    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            // do something here
            // Log.i("LOGOUT_EVENT", "id");
            editor.clear();
            editor.apply();
            startActivity(loginI);
        }
        return super.onOptionsItemSelected(item);
    }

    //METHOD WHICH WILL HANDLE DYNAMIC INSERTION
    public void FetchItems() {
        Log.i("RUNONUITHREAD", "APPLYING FILTER");
        Request req = new Request.Builder()
                .url(url)
                .build();
        swipeRefreshLayout.setRefreshing(true);
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
                        allKOTs.clear();
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

                            allKOTs.add(kot);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("RUNONUITHREAD", "APPLYING FILTER");
                                now_btn.setEnabled(false);
                                future_btn.setEnabled(true);
                                applyFilter();
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

    public void applyFilter() {
        LocalDate localDate = LocalDate.now();
        LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX);
        Kot kot;
        nKOTs.clear();
        fKOTs.clear();
        for (int i=0; i<allKOTs.size(); i++) {
            kot = allKOTs.get(i);
            if(selected_types.contains(kot.ordertypid) && selected_statuses.contains(kot.statusid)) {
                LocalDateTime delDate = LocalDateTime.parse(kot.deliverydatetime);
                if(endOfDay.isAfter(delDate)) {
                    nKOTs.add(kot);
                } else {
                    fKOTs.add(kot);
                }
            }
        }
        nKOTs.sort((a,b) -> a.deliverydatetime.compareTo(b.deliverydatetime));
        fKOTs.sort((a,b) -> a.deliverydatetime.compareTo(b.deliverydatetime));
        Log.i("KOT FILTER", "AllKots: " + allKOTs.size() + " nKots: " + nKOTs.size() + " fKOTs: " + fKOTs.size());
        Log.i("KOT FILTER", "selected_types: " + selected_types.size() + " selected_statuses: " + selected_statuses.size());
        if(!now_btn.isEnabled()) viewNowKots(now_btn);
        else if(!future_btn.isEnabled()) viewFutureKots(future_btn);
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

    public void updateList() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.cancelAllTimers();
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void signalRconfig() {
         hubConnection.on("DeliveryOrderUpdate", (o_storeid, del_storeid, invoiceno, event, orderid) -> {
             Log.i("SIGNAL_R_DELORDUPDATE", o_storeid + " " + del_storeid + " " + invoiceno + " " + event + " " + orderid);
             if(del_storeid == storeid) {
                 Log.i("SIGNAL_R_DELORDUPDATE", "Fetching KOTs.....");
                 runOnUiThread(new Runnable() {
                     @Override
                     public void run() {
                        FetchItems();
                     }
                 });
             }
         },Integer.class, Integer.class, String.class, String.class, Integer.class);

         hubConnection.onClosed((a) -> {
             Log.i("SIGNAL_R_DELORDUPDATE", "Error");
         });
    }

    public void login(View v) {
        setContentView(R.layout.activity_main);
    }
}