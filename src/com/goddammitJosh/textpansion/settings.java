package com.goddammitJosh.textpansion;
//Herp Derp Making a comment
import android.os.Bundle;
import android.widget.TextView;
import android.app.Activity;

public class settings extends Activity
{
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        TextView textview = new TextView(this);
        textview.setText(R.string.tab_title_settings);
        setContentView(textview);
    }
}
