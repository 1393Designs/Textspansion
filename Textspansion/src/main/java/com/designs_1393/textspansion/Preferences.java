package com.designs_1393.textspansion;

import android.app.ActionBar;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

public class Preferences extends PreferenceActivity {
    public static final String PREF_LOAD_ON_BOOT = "notification";
    public static final String PREF_HIDE_PASTE_TEXT = "hidePasteText";
    private NotificationManager mNotificationManager;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if (key.equals(PREF_LOAD_ON_BOOT)) {
                    if (prefs.getBoolean(key, false)) {
                        Intent notificationIntent = new Intent(getApplicationContext(), Textspansion.class);
                        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

                        Notification.Builder builder = new Notification.Builder(getApplicationContext());
                        builder.setContentTitle(getApplicationContext().getString(R.string.app_name));
                        builder.setContentText(getApplicationContext().getString(R.string.notification_click));
                        builder.setTicker(getApplicationContext().getString(R.string.notification_running));
                        builder.setSmallIcon(R.drawable.textspansion_notification);
                        builder.setOngoing(true);
                        builder.setContentIntent(contentIntent);
                        Notification notification = builder.getNotification();

                        mNotificationManager.notify(1, notification);
                    } else {
                        mNotificationManager.cancelAll();
                    }
                }
            }
        };
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, Textspansion.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
