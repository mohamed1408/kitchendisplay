package com.bizone.kitchendisplay;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.io.IOException;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        base_url = "https://biz1pos.azurewebsites.net/";

        login_btn = findViewById(R.id.login_btn);

        unameTxt = findViewById(R.id.username);
        passTxt = findViewById(R.id.password);

        loader = findViewById(R.id.loading);
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
                loader.setVisibility(View.GONE);
                Log.i("LOGIN_ERROR", String.valueOf(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                loader.setVisibility(View.GONE);
                if(response.isSuccessful()) {
                    Log.i("LOGIN_SUCCESS", response.body().string());
                }
            }
        });

    }
}