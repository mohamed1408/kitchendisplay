package com.bizone.kitchendisplay;

import androidx.appcompat.app.AppCompatActivity;

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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//import com.microsoft.signalr;
public class MainActivity extends AppCompatActivity {

    Button retry_btn;
    Button future_btn;

    HubConnection hubConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] mobileArray = {"Android","IPhone","WindowsMobile","Blackberry","WebOS","Ubuntu","Windows7","Max OS X","Android","IPhone","WindowsMobile","Blackberry","WebOS","Ubuntu","Windows7","Max OS X","Android","IPhone","WindowsMobile","Blackberry","WebOS","Ubuntu","Windows7","Max OS X","Android","IPhone","WindowsMobile","Blackberry","WebOS","Ubuntu","Windows7","Max OS X","Android","IPhone","WindowsMobile","Blackberry","WebOS","Ubuntu","Windows7","Max OS X"};

        hubConnection = HubConnectionBuilder.create("https://biz1pos.azurewebsites.net/uphub").build();

        retry_btn = (Button)findViewById(R.id.now_btn);
        future_btn = (Button)findViewById(R.id.future_btn);

        OkHttpClient client = new OkHttpClient();
        String url = "https://biz1pos.azurewebsites.net/api/KOT/GetStoreKots?storeId=22&orderid=0&kotGroupId=15";

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
        future_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Request req = new Request.Builder()
                        .url(url)
                        .build();
                client.newCall(req).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.i("OkHttp_log_error", String.valueOf(e));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if(response.isSuccessful()) {
                            String str_body = response.body().string();
                            Log.i("OkHttp_log_success", str_body);
                            try {
//                                JSONObject jsObject = new JSONObject(response.body().toString());
                                JSONArray arr = new JSONArray(response.body());
                                for (int i=0; i<arr.length(); i++) {
                                    JSONObject object = arr.getJSONObject(i);
                                    String DeliveryDateTime = object.getString("DeliveryDateTime");
                                    Log.i("OkHttp_log_success", DeliveryDateTime);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
        hubConnection.on("DeliveryOrderUpdate", (a, b, c, d, e) -> {
            Log.i("SIGNAL_R_DELORDUPDATE", c);
        },Integer.class, Integer.class, String.class, String.class, Integer.class);
        hubConnection.on("JoinMessage", (a) -> {
            Log.i("SIGNAL_R_JOINMESSAGE", a);
        },String.class);

        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.activity_listview, mobileArray);

        ListView listView = (ListView) findViewById(R.id.kot_list);
        listView.setAdapter(adapter);
    }
}