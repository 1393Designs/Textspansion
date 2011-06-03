package com.goddammitJosh.textpansion;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckedTextView;


import android.app.Activity;
import android.app.Dialog;
import java.util.Arrays;
import java.util.ArrayList;
import java.lang.String;

import java.util.Comparator;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnClickListener;

import android.util.Log;
import android.text.ClipboardManager;
import android.view.ViewManager;

//menu
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;

//Export 
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import org.xmlpull.v1.XmlSerializer;
import android.util.Xml;
import android.os.Environment;

//Import
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import android.net.Uri;
import java.net.URI;

//context menu
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;

//sorted map
import android.widget.SimpleCursorAdapter;
import android.database.Cursor;

//Preference stuff
import android.content.Intent;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

public class subsList extends ListActivity implements OnSharedPreferenceChangeListener
{
	public static final int INSERT_ID = Menu.FIRST;
	public static final int EXPORT_ID = Menu.NONE;

	private int mSubNumber = 1;

	private subsDbAdapter mDbHelper;
	private Cursor mSubsCursor;

	private ClipboardManager cb;
	
	private String extStoDir = Environment.getExternalStorageDirectory().toString() + "/Textspansion";
	private static final String TAG = "Textspansion: SubsList";

	private File dbFile = new File("/data/data/com.goddammitJosh.textpansion/databases/", "data");
	private boolean addTut = false;
	
	private SharedPreferences prefs;
	private SharedPreferences sharedPrefs;
	private boolean sortByShort = true;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subs_list); // TODO: change to a real list
		
		//Setup preferences
		prefs = this.getSharedPreferences("textspansionPrefs", Activity.MODE_PRIVATE);
		prefs.registerOnSharedPreferenceChangeListener(this);
		
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

		mDbHelper = new subsDbAdapter(this);
        if(!dbFile.exists()) 
        { 
             SharedPreferences.Editor editor = sharedPrefs.edit(); 
             editor.putString("sortie", "short"); 
             editor.commit(); 
             addTut = true; 
        } 

		if(sharedPrefs.getString("sortie", "HERPADERP").equals("short"))
			sortByShort = true;
		else if(sharedPrefs.getString("sortie", "HERPADERP").equals("long"))
			sortByShort = false;

		mDbHelper.open();
		mSubsCursor = mDbHelper.fetchAllSubs(sortByShort);
		if(addTut)
			mDbHelper.addTutorial();
		fillData();
		registerForContextMenu(getListView()); 
		cb = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
	}
	
	public void onSharedPreferenceChanged(SharedPreferences prefs, String key){
		//if(key.equals("tutorial"))
			Log.i("Shared Prefs", key);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
		Cursor c = mSubsCursor;
		c.moveToPosition(position);
		Log.i("textspansion", "Clicked");
		Toast.makeText(getApplicationContext(), c.getString(c.getColumnIndexOrThrow(subsDbAdapter.KEY_ABBR)) + " has been copied."
			, Toast.LENGTH_SHORT).show();
			
		cb.setText(c.getString(c.getColumnIndexOrThrow(subsDbAdapter.KEY_FULL)));
		
		//Intent psIntent = new Intent(getApplicationContext(), paste_service.class);
		//getApplicationContext().startService(psIntent);
		
		if(sharedPrefs.getBoolean("endOnCopy", true))
			finish();
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		mDbHelper.close();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		mDbHelper.open();
		
		if(sharedPrefs.getString("sortie", "HERPADERP").equals("short"))
			sortByShort = true;
		else if(sharedPrefs.getString("sortie", "HERPADERP").equals("long"))
			sortByShort = false;
		else
			Log.i("SORTING BY", "OOP");
		
		if(prefs.contains("tutorial"))
		{
			mDbHelper.addTutorial();
			SharedPreferences.Editor editor = prefs.edit();
			editor.remove("tutorial");
			editor.commit();
		}
		fillData();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.sub_list_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.add_item:
				addItem();
				return true;
			case R.id.menu_export:
				exportSubs();
				return true;
			case R.id.multi_delete:
				startActivity(new Intent(this, multiDelete.class));
								fillData();
				return true;
			case R.id.menu_settings:
				startActivity(new Intent(this, settings.class));
				return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.sub_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

		switch (item.getItemId()) // can get replaced by android:onClick="method name" in sub_context_menu.xml?
		{
			case R.id.edit_item:
				editItem(info.position);
				return true;
			case R.id.delete_item:
				deleteItem(info.position);
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

// -------------------- Database Manipulation --------------------
	private void fillData()
	{
		mSubsCursor = mDbHelper.fetchAllSubs(sortByShort);
		startManagingCursor(mSubsCursor);
		
		String[] from = new String[]{subsDbAdapter.KEY_ABBR, subsDbAdapter.KEY_FULL};
		int[] to = new int[]{R.id.ShortTextMain, R.id.LongTextMain};
		
		// Now create an array adapter and set it to display using the stock android row
		SimpleCursorAdapter subsAdapter = new SimpleCursorAdapter(getApplicationContext(),
			R.layout.subs_row, mSubsCursor, from, to);
		setListAdapter(subsAdapter);
	}

	public void addItem()
	{
		final Dialog dialog = new Dialog(subsList.this);
		dialog.setContentView(R.menu.maindialog);
		dialog.setTitle("Adding a thingy");
		dialog.setCancelable(true);
		
		TextView short_text = (TextView) dialog.findViewById(R.id.short_label);
		final EditText short_input = (EditText) dialog.findViewById(R.id.short_entry);
		TextView long_text = (TextView) dialog.findViewById(R.id.long_label);
		final EditText long_input = (EditText) dialog.findViewById(R.id.long_entry);
		
		Button cancel_button = (Button) dialog.findViewById(R.id.cancelButton);
		cancel_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		Button okay_button = (Button) dialog.findViewById(R.id.okayButton);
		okay_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String short_name = short_input.getText().toString();
				String long_name = long_input.getText().toString();
				if(short_name.compareTo("") == 0)
					short_name = long_name;
				if (mDbHelper.createSub(short_name, long_name) == -1)
					Toast.makeText(getApplicationContext(),
					"That item already exists.", Toast.LENGTH_SHORT).show();
				fillData();
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	
	public void editItem(int item)
	{
		final int theItem = item+1; // SQL starts counting from 1
		final Dialog dialog = new Dialog(subsList.this);
		dialog.setContentView(R.menu.maindialog);
		dialog.setTitle("Editing a thingy");
		dialog.setCancelable(true);
		
		TextView short_text = (TextView) dialog.findViewById(R.id.short_label);
		final EditText short_input = (EditText) dialog.findViewById(R.id.short_entry);
		TextView long_text = (TextView) dialog.findViewById(R.id.long_label);
		final EditText long_input = (EditText) dialog.findViewById(R.id.long_entry);
				
			// set previous values as defaults
			Cursor c = mSubsCursor;
			c.moveToPosition(item);
			final String old_short = c.getString(c.getColumnIndexOrThrow(subsDbAdapter.KEY_ABBR));
			final String old_full  = c.getString(c.getColumnIndexOrThrow(subsDbAdapter.KEY_FULL));
			short_input.setText(c.getString(c.getColumnIndexOrThrow(subsDbAdapter.KEY_ABBR)));
			long_input.setText(c.getString(c.getColumnIndexOrThrow(subsDbAdapter.KEY_FULL)));
		
		
		Button cancel_button = (Button) dialog.findViewById(R.id.cancelButton);
		cancel_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		Button okay_button = (Button) dialog.findViewById(R.id.okayButton);
		okay_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String short_name = short_input.getText().toString();
				String long_name = long_input.getText().toString();
				if ( short_name.equals(old_short) && long_name.equals(old_full) )
				{
					dialog.dismiss();
				}
				else
				{
					if(short_name.compareTo("") == 0)
						short_name = long_name;
					if( !mDbHelper.updateSub(old_full, old_short, short_name, long_name))
						Toast.makeText(getApplicationContext(),
						"That item already exists.", Toast.LENGTH_SHORT).show();
					fillData();
					dialog.dismiss();
				}
			}
		});
		dialog.show();
	}
	
	public void deleteItem(int item)
	{
		Cursor c = mSubsCursor;
		c.moveToPosition(item);
		final String old_short = c.getString(c.getColumnIndexOrThrow(subsDbAdapter.KEY_ABBR));
		final String old_full  = c.getString(c.getColumnIndexOrThrow(subsDbAdapter.KEY_FULL));
		mDbHelper.deleteSub(old_full, old_short);
		fillData();
	}

// ---------------------------------- FILE I/O -----------------------------------------	
	
	public void exportSubs()
	{
		try{
			File root = new File(extStoDir);
			Cursor c = mSubsCursor;
			if(!root.exists())
				root.mkdirs();
			if(c.getCount() < 1)
				Toast.makeText(getApplicationContext(), "You have nothing to write out.",Toast.LENGTH_SHORT).show();
			else if(root.canWrite()){
				String subs_short = null, subs_full  = null;
				int i = 0;
				c.moveToPosition(i);
				
				File outTXT = new File(extStoDir, "subs.xml");
				XmlSerializer serializer = Xml.newSerializer();
				FileOutputStream fio = new FileOutputStream(outTXT);
				try{
					serializer.setOutput(fio, null);
					serializer.startDocument("UTF-8", true);
					serializer.startTag("", "Textspansion");
					
					do
					{
						subs_short = c.getString(c.getColumnIndexOrThrow(subsDbAdapter.KEY_ABBR));
						subs_full = c.getString(c.getColumnIndexOrThrow(subsDbAdapter.KEY_FULL));
						
						serializer.startTag("", "Subs");
						serializer.startTag("", "Short");
						serializer.text(subs_short);
						serializer.endTag("", "Short");
						serializer.startTag("", "Long");
						serializer.text(subs_full);
						serializer.endTag("", "Long");
						serializer.endTag("", "Subs");
						
						c.move(1);
					}while(!c.isAfterLast());
					
					serializer.endTag("", "Textspansion");
					serializer.endDocument();
				} catch (Exception e) {
					throw new RuntimeException(e);
				} 
				
				Toast.makeText(getApplicationContext(), "Substitutions saved to SD!",Toast.LENGTH_SHORT).show();
			}
			else
				Toast.makeText(getApplicationContext(), "App isn't allowed to write to SD! :(",Toast.LENGTH_SHORT).show();
		}catch(IOException e){
			Log.e(TAG, "Could not write file :" + e.getMessage());
		}
	}
}
