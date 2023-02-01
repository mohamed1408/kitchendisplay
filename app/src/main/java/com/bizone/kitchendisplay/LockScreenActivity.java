package com.bizone.kitchendisplay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LockScreenActivity extends AppCompatActivity {
    Intent mainAct;
    Integer storeid, companyid;
    String base_url;
    TextView pin_view;
    Button unlock_btn;
    ProgressBar progressBar;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);

        base_url = "https://biz1pos.azurewebsites.net/";
        pin_view = (TextView) findViewById(R.id.pin_view);
        unlock_btn = (Button) findViewById(R.id.unlock_btn);
        progressBar = (ProgressBar) findViewById(R.id.unlock_loading);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        companyid = preferences.getInt("companyid", 0);
        storeid = preferences.getInt("storeid", 0);
        client = new OkHttpClient();
        mainAct = new Intent(LockScreenActivity.this, MainActivity.class);
    }

    public void numPadEvent(View v) {
        Button btn = (Button) v;
        String btn_text = btn.getText().toString();
        String pin = pin_view.getText().toString();
        Log.i("numPadEvent", btn_text);
        switch (btn_text) {
            case "delete":
                if (pin.length() > 0)
                    pin_view.setText(chop(pin));
                break;
            case "unlock":
                btn.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                unlock(pin);
                break;
            default:
                pin_view.setText(pin + btn_text);
        }
    }

    @NonNull
    private String chop(String s) {
        return s.substring(0, s.length() - 1);
    }

    private void unlock(String pin) {
        String url = base_url + "api/LogIn/pinlogin2?companyid=" + companyid + "&pin=" + pin + "&storeid=" + storeid;

        Request req = new Request.Builder()
                .url(url)
                .build();
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        unlock_btn.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                });
                Log.i("LOGIN_ERROR", String.valueOf(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        unlock_btn.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                });
                if(response.isSuccessful()) {
                    String body_str = response.body().string();
                    Log.i("UNLOCK_RESPONSE", body_str);
                    try {
                        JSONObject resp = new JSONObject(body_str);
                        Integer status = resp.getInt("status");
                        if(status == 200) {
                            Integer userid = resp.getInt("userid");
                            editor.putInt("userid", userid);
                            editor.apply();
                            startActivity(mainAct);
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getBaseContext(), "Invalid Pin", Toast.LENGTH_LONG).show();
                                    pin_view.setText("");
                                }
                            });
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }
}