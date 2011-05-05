package com.goddammitJosh.textpansion;

import android.os.Bundle;
import android.app.TabActivity;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import android.content.res.Resources;
import android.content.Intent;

public class mainWindow extends TabActivity
{
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Resources res = getResources(); // resouce object to get drawables
        TabHost tabHost = getTabHost(); // the activity TabHost
        TabHost.TabSpec spec;           // reusable tabspec for each tab
        Intent intent;                  // reusable intent for each tab

        // create an intent to launch an activity for the tab (to be reused)
        intent = new Intent().setClass(this, subsList.class);

        // --- TODO: make the text for each tab a reference to strings.xml ---

        // initialize a tabspec for each tab and add it to the tabhost
        spec = tabHost.newTabSpec("subs").setIndicator("Substitutions",
            res.getDrawable(R.drawable.ic_tab_settings)).setContent(intent);
        tabHost.addTab(spec);


        // do the same for the others!
        intent = new Intent().setClass(this, settings.class);
        spec = tabHost.newTabSpec("settings").setIndicator("Settings",
            res.getDrawable(R.drawable.ic_tab_settings)).setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);
    }
}
