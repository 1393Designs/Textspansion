package com.designatum_1393.textspansion.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.designatum_1393.textspansion.Preferences;
import com.designatum_1393.textspansion.R;

public class PreferencesFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Preference deletePref = (Preference) findPreference("deleteAll");
        deletePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences prefs = getActivity().getSharedPreferences("textspansionPrefs", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("deleteAll", "deletePref has been clicked");
                editor.commit();
                // deleteDialog();
                return true;
            }
        });

        Preference tutPref = (Preference) findPreference("tutorial");
        tutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                // startActivity(new Intent(getApplicationContext(), tutorial.class));
                return true;
            }
        });

        Preference aboutPref = (Preference) findPreference("aboutizzle");
        aboutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences prefs = getActivity().getSharedPreferences("textspansionPrefs", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("about", "About has been clicked");
                editor.commit();
                // aboutDialog();
                return true;
            }
        });
    }

}
