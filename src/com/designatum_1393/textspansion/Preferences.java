package com.designatum_1393.textspansion;

import android.app.Activity;
import android.os.Bundle;

public class Preferences extends Activity{
    public static final String PREF_LOAD_ON_BOOT = "notification";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);
    }
}
