package com.designatum_1393.textspansion;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class Textspansion extends Activity
{
    private SharedPreferences sharedPreferences;
    private NotificationManager mNotificationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}

    @Override
    protected void onResume() {
        super.onResume();
        if(sharedPreferences.getBoolean("notification", false)) {
            Intent notificationIntent = new Intent(this, Textspansion.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Notification.Builder builder = new Notification.Builder(this);
            builder.setContentTitle(getString(R.string.app_name));
            builder.setContentText(getString(R.string.notification_click));
            builder.setTicker(getString(R.string.notification_running));
            builder.setSmallIcon(R.drawable.textspansion_notification);
            builder.setOngoing(true);
            builder.setContentIntent(contentIntent);
            Notification notification = builder.getNotification();

            mNotificationManager.notify(1, notification);
        }
        else {
            mNotificationManager.cancelAll();
        }
    }
}