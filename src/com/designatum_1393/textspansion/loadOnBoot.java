package com.designatum_1393.textspansion;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.widget.Toast;

//Preference stuff
import android.content.Intent;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

//Notifications
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.app.Activity;

public class loadOnBoot extends BroadcastReceiver 
{
	private SharedPreferences sharedPrefs;
	private static final int HELLO_ID = 1;
	
    @Override
    public void onReceive(Context context, Intent intent) 
	{		
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		if(sharedPrefs.getBoolean("notification", false))
		{
			int icon = R.drawable.notification_icon;
			CharSequence tickerText = "Textspansion is running";
			long when = System.currentTimeMillis();
			Notification notification = new Notification(icon, tickerText, when);
			CharSequence contentTitle = "Textspansion";
			CharSequence contentText = "Click to access your snippets";
			Intent notificationIntent = new Intent(context, textspansion.class);
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

			notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
			notification.flags = Notification.FLAG_ONGOING_EVENT;

			mNotificationManager.notify(HELLO_ID, notification);
		}
		else
		{
			mNotificationManager.cancelAll();
		}
    }
}