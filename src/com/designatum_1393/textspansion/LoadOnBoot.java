package com.designatum_1393.textspansion;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;

//Preference stuff
import android.preference.PreferenceManager;
import android.content.SharedPreferences;

//Notifications
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.util.Log;


public class LoadOnBoot extends BroadcastReceiver {
    private static final int HELLO_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Log.i("vincetran", "Wat is this");
        if(sharedPreferences.getBoolean(Preferences.PREF_LOAD_ON_BOOT, false)) {
            Log.i("vincetran", "Found the pref: let's load up this notification");
            Intent notificationIntent = new Intent(context, Textspansion.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

            Notification notification = new Notification.Builder(context)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(context.getString(R.string.notification_click))
                    .setTicker(context.getString(R.string.notification_running))
                    .setSmallIcon(R.drawable.textspansion_notification)
                    //.setLargeIcon(R.drawable.notification_icon_large)
                    .setOngoing(true)
                    .setContentIntent(contentIntent)
                    .getNotification();


            mNotificationManager.notify(HELLO_ID, notification);
        } else {
            mNotificationManager.cancelAll();
        }
    }
}