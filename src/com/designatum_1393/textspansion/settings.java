package com.designatum_1393.textspansion;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.DialogPreference;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.text.util.Linkify;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.TextView;
import android.content.Intent;


public class settings extends PreferenceActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);

		Preference deletePref = (Preference) findPreference("deleteAll");
		deletePref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				SharedPreferences prefs = getSharedPreferences("textspansionPrefs", Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString("deleteAll", "deletePref has been clicked");
				editor.commit();
				deleteDialog();
				return true;
			}
		});

		Preference tutPref = (Preference) findPreference("tutorial");
		tutPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				SharedPreferences prefs = getSharedPreferences("textspansionPrefs", Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString("tutorial", "tutPref has been clicked");
				editor.commit();
				startActivity(new Intent(getApplicationContext(), tutorial.class));
				//finish();
				return true;
			}
		});

		Preference aboutPref = (Preference) findPreference("aboutizzle");
		aboutPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				SharedPreferences prefs = getSharedPreferences("textspansionPrefs", Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString("about", "About has been clicked");
				editor.commit();
				aboutDialog();
				return true;
			}
		});
	}

	public void deleteDialog(){
		AlertDialog.Builder dd = new AlertDialog.Builder(this);
		dd.setIcon(R.drawable.icon);
		dd.setTitle("Are you sure?");
		dd.setView(LayoutInflater.from(this).inflate(R.layout.delete_dialog,null));

		dd.setPositiveButton("Delete EVERYTHING", 
		new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
			goAndDeleteEverything();
			}
		});

		dd.setNegativeButton("NO NO NO", 
		new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
			}
		});

		dd.show();
	}

	public void goAndDeleteEverything()
	{
		subsDbAdapter mDbHelper = new subsDbAdapter(this);
		mDbHelper.open();
		mDbHelper.abandonShip();
		mDbHelper.close();

	}


	public void aboutDialog(){
		final TextView message = new TextView(this);

		try{
			PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), PackageManager.GET_META_DATA);
			String versionInfo = pInfo.versionName;

			String versionString = String.format("Version: %s", versionInfo);
			String authors = "Authors: Sean Barag and Vincent Tran";
			String testers = "QA Tester: Nee Taylor";
			String website = "Visit our website: http://1393Designs.com";
			String license = "This application is linked with the ActionBar library, licensed under the Apache License, Version 2.0. \nActionBar: Copyright \u00A92010 Johan Nilsson" +
				"\n\nA copy of the Apache license can be found at: http://www.apache.org/licenses/LICENSE-2.0.html";
			String cp = "\u00A92011 1393 Designs, All Rights Reserved.";

			message.setPadding(10, 10, 10, 10);
			message.setText(versionString + "\n\n" + authors + "\n\n" + testers + "\n\n" + website + "\n\n" + license + "\n\n" + cp);
			Linkify.addLinks(message, Linkify.EMAIL_ADDRESSES);
			Linkify.addLinks(message, Linkify.WEB_URLS);

			AlertDialog.Builder ad = new AlertDialog.Builder(this);
			ad.setIcon(R.drawable.icon);
			ad.setTitle("About");
			ad.setView(message);

			ad.setPositiveButton("That's nice", 
			new android.content.DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int arg1) {
				}
			});
			ad.show();

		}catch(Exception e){}

	}
}
