package com.bizone.kitchendisplay;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    Button login_btn;

    EditText unameTxt;
    EditText passTxt;
    ProgressBar loader;
    OkHttpClient client = new OkHttpClient();
    String base_url;
    Integer companyid, storeid, kotgroupid;
    StoreVIewAdapter storeAadapter;
    KOTGroupAdapter kotGroupAdapter;
    ArrayList<Store> stores;
    ArrayList<KOTGroup> kotGroups;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Intent mainAct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mainAct = new Intent(LoginActivity.this, MainActivity.class);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        base_url = "https://biz1pos.azurewebsites.net/";

        login_btn = findViewById(R.id.login_btn);

        unameTxt = findViewById(R.id.username);
        passTxt = findViewById(R.id.password);

        loader = findViewById(R.id.loading);

        stores = new ArrayList<Store>();
        kotGroups = new ArrayList<KOTGroup>();

        storeAadapter = new StoreVIewAdapter(this,stores);
        kotGroupAdapter = new KOTGroupAdapter(this,kotGroups);
    }

    public void Login(View v) {
        loader.setVisibility(View.VISIBLE);

        String uname = unameTxt.getText().toString();
        String pass  = passTxt.getText().toString();

        String url = base_url + "api/Login/LogIn";

        RequestBody loginform = new FormBody.Builder()
                .add("EmailId", uname)
                .add("Password", pass)
                .build();

        Request req = new Request.Builder()
                .url(url)
                .post(loginform)
                .build();

        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loader.setVisibility(View.GONE);
                    }
                });
                Log.i("LOGIN_ERROR", String.valueOf(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loader.setVisibility(View.GONE);
                    }
                });
                if(response.isSuccessful()) {
                    stores.clear();
//                    Log.i("LOGIN_SUCCESS", response.body().string());
                    JSONObject responseObject;
                    JSONArray stores_arr = new JSONArray();
                    String resp_body_str = response.body().string();
                    try {
                        responseObject = new JSONObject(resp_body_str);
                        stores_arr = responseObject.getJSONArray("data");
                        for(int i=0; i<stores_arr.length(); i++) {
                            JSONObject store_ob = stores_arr.getJSONObject(i);
                            Store store = new Store(store_ob);
                            stores.add(store);
                        }
                        stores.sort((a,b) -> a.name.compareTo(b.name));
                    } catch (JSONException e) {
                        Log.i("JOSN_PARSE_ERROR", "UNAME: " + uname + " PASS: " + pass);
                        Log.i("JOSN_PARSE_ERROR", resp_body_str);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            openStoresBottomSheet();
                        }
                    });
                }
            }
        });
    }

    public void getKOTGroups(Integer companyid) {
        loader.setVisibility(View.VISIBLE);

        String uname = unameTxt.getText().toString();
        String pass  = passTxt.getText().toString();

        String url = base_url + "api/KOTGroup/GetIndex?CompanyId=" + companyid;

        Request req = new Request.Builder()
                .url(url)
                .build();

        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loader.setVisibility(View.GONE);
                    }
                });
                Log.i("LOGIN_ERROR", String.valueOf(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loader.setVisibility(View.GONE);
                    }
                });
                if(response.isSuccessful()) {
                    kotGroups.clear();
                    JSONObject responseObject;
                    JSONArray kotgrps_arr = new JSONArray();
                    String resp_body_str = response.body().string();
                    Log.i("LOGIN_SUCCESS", resp_body_str);
                    try {
//                        responseObject = new JSONObject(resp_body_str);
                        kotgrps_arr = new JSONArray(resp_body_str);
                        for(int i=0; i<kotgrps_arr.length(); i++) {
                            JSONObject kg_ob = kotgrps_arr.getJSONObject(i);
                            KOTGroup kotGroup = new KOTGroup(kg_ob);
                            kotGroups.add(kotGroup);
                        }
                        kotGroups.sort((a,b) -> a.description.compareTo(b.description));
                    } catch (JSONException e) {
                        Log.i("JOSN_PARSE_ERROR", "UNAME: " + uname + " PASS: " + pass);
                        Log.i("JOSN_PARSE_ERROR", resp_body_str);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            openKGBottomSheet();
                        }
                    });
                }
            }
        });
    }
    private void openStoresBottomSheet() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet);

        bottomSheetDialog.setCancelable(false);

        ListView store_lv = bottomSheetDialog.findViewById(R.id.store_list);
        Button close_btn = bottomSheetDialog.findViewById(R.id.close_btn);

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.cancel();
            }
        });

        storeAadapter = new StoreVIewAdapter(bottomSheetDialog.getContext(),stores);
        store_lv.setAdapter(storeAadapter);
        store_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Store store = stores.get(i);
                Log.i("STORE_SELECT", stores.get(i).id + " " + stores.get(i).name + " " + stores.get(i).companyid);
                companyid = store.companyid;
                storeid = store.id;
                bottomSheetDialog.cancel();
                getKOTGroups(companyid);
            }
        });

        storeAadapter.notifyDataSetChanged();
        // LinearLayout copy = bottomSheetDialog.findViewById(R.id.copyLinearLayout);
        // LinearLayout share = bottomSheetDialog.findViewById(R.id.shareLinearLayout);
        // LinearLayout upload = bottomSheetDialog.findViewById(R.id.uploadLinearLaySout);
        // LinearLayout download = bottomSheetDialog.findViewById(R.id.download);
        // LinearLayout delete = bottomSheetDialog.findViewById(R.id.delete);

        bottomSheetDialog.show();
    }
    private void openKGBottomSheet() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.kotgroup_bottom_sheet);

        bottomSheetDialog.setCancelable(false);

        ListView kg_lv = bottomSheetDialog.findViewById(R.id.kg_list);
        Button close_btn = bottomSheetDialog.findViewById(R.id.close_btn);

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.cancel();
            }
        });

        kotGroupAdapter = new KOTGroupAdapter(bottomSheetDialog.getContext(),kotGroups);
        kg_lv.setAdapter(kotGroupAdapter);
        kg_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                KOTGroup kotGroup = kotGroups.get(i);
                Log.i("KG_SELECT", kotGroup.id + " " + kotGroup.description);
                kotgroupid = kotGroup.id;
                editor.putInt("companyid", companyid);
                editor.putInt("storeid", storeid);
                editor.putInt("kotgroupid", kotgroupid);
                editor.apply();
                bottomSheetDialog.cancel();
                startActivity(mainAct);
            }
        });
        kotGroupAdapter.notifyDataSetChanged();
        // LinearLayout copy = bottomSheetDialog.findViewById(R.id.copyLinearLayout);
        // LinearLayout share = bottomSheetDialog.findViewById(R.id.shareLinearLayout);
        // LinearLayout upload = bottomSheetDialog.findViewById(R.id.uploadLinearLaySout);
        // LinearLayout download = bottomSheetDialog.findViewById(R.id.download);
        // LinearLayout delete = bottomSheetDialog.findViewById(R.id.delete);

        bottomSheetDialog.show();
    }
}