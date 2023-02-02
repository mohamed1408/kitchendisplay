package com.bizone.kitchendisplay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotifyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("NOTIFICATION_EVENT", "Receiver " + intent.getStringExtra("myAction"));
        if(intent.getStringExtra("myAction") != null && intent.getStringExtra("myAction").equals("mDoNotify")) {
            Log.i("NOTIFICATION_EVENT", "Receiver1");

//            Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
//                    R.drawable.ic_action_logout);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "CHANNEL_ID")
                    .setSmallIcon(R.drawable.ic_action_logout)
//                    .setLargeIcon(icon)
                    .setContentTitle("My notification")
                    .setContentText("Much longer text that cannot fit one line...")
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("Much longer text that cannot fit one line..."))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Log.i("NOTIFICATION_PERMISSION_CHECK","NOT_GRANTED");
                return;
            }
            Log.i("NOTIFICATION_PERMISSION_CHECK","GRANTED");
            managerCompat.notify(1, builder.build());
        }
    }
}
