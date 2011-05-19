package com.goddammitJosh.textpansion;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class settings extends PreferenceActivity
{
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
    }
}
