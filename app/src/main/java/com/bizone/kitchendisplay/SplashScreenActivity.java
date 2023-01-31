package com.bizone.kitchendisplay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class SplashScreenActivity extends AppCompatActivity {

    Intent loginI, mainI, lockScrnI;
    Integer storeid, companyid, kotgroupid;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        companyid = preferences.getInt("companyid", 0);
        storeid = preferences.getInt("storeid", 0);
        kotgroupid = preferences.getInt("kotgroupid", 0);

        loginI = new Intent(SplashScreenActivity.this, LoginActivity.class);
        mainI = new Intent(SplashScreenActivity.this, MainActivity.class);
        lockScrnI = new Intent(SplashScreenActivity.this, LockScreenActivity.class);

        if(companyid == 0 || storeid == 0) {
            startActivity(loginI);
        } else {
            startActivity(lockScrnI);
        }
    }
}