package com.designatum_1393.textspansion.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.LayoutInflater;

import com.designatum_1393.textspansion.Preferences;
import com.designatum_1393.textspansion.R;
import com.designatum_1393.textspansion.utils.SubsDataSource;

public class PreferencesFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Preference deletePref = findPreference("deleteAll");
        if (deletePref != null) {
            deletePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    SharedPreferences prefs = getActivity().getSharedPreferences("textspansionPrefs", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("deleteAll", "deletePref has been clicked");
                    editor.commit();
                    deleteDialog();
                    return true;
                }
            });
        }

        Preference tutPref = findPreference("tutorial");
        if (tutPref != null) {
            tutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    // startActivity(new Intent(getApplicationContext(), tutorial.class));
                    return true;
                }
            });
        }

        Preference aboutPref = findPreference("aboutizzle");
        if (aboutPref != null) {
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

    public void deleteDialog(){
        AlertDialog.Builder dd = new AlertDialog.Builder(getActivity());
        dd.setIcon(R.drawable.icon);
        dd.setTitle("Are you sure?");
        dd.setView(LayoutInflater.from(getActivity()).inflate(R.layout.delete_dialog, null));

        dd.setPositiveButton("Delete EVERYTHING",
            new android.content.DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    SubsDataSource subsDataSource;
                    subsDataSource = new SubsDataSource(getActivity());
                    subsDataSource.open();
                    subsDataSource.abandonShip();
                    subsDataSource.close();
                }
            }
        );

        dd.setNegativeButton("NO NO NO",
            new android.content.DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                }
            }
        );
        dd.show();
    }
}
