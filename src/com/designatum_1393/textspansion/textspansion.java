package com.designatum_1393.textspansion;

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
import android.widget.CheckBox;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.content.DialogInterface;
import java.io.IOException;
import java.lang.String;

import android.app.Activity;
import android.app.Dialog;
import java.util.Arrays;
import java.util.ArrayList;
import java.lang.String;

import java.util.Comparator;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnClickListener;

import android.text.ClipboardManager;
import android.view.ViewManager;

//private subs
import android.text.method.PasswordTransformationMethod;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

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

import android.content.Context;

import android.util.Log;


public class textspansion extends ListActivity
{
	public static final int INSERT_ID = Menu.FIRST;
	public static final int EXPORT_ID = Menu.NONE;

	private int mSubNumber = 1;

	private subsDbAdapter mDbHelper;
	private Cursor mSubsCursor;

	private ClipboardManager cb;
	
	private String extStoDir = Environment.getExternalStorageDirectory().toString() + "/Textspansion";
	private static final String TAG = "Textspansion: SubsList";

	private File dbFile = new File("/data/data/com.designatum_1393.textspansion/databases/", "data");
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
		
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		if(!sharedPrefs.getBoolean("EULA", false))
			presentEULA();
			
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
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
		Cursor c = mSubsCursor;
		c.moveToPosition(position);
		Toast.makeText(getApplicationContext(), c.getString(c.getColumnIndexOrThrow(subsDbAdapter.KEY_ABBR)) + " has been copied."
			, Toast.LENGTH_SHORT).show();
			
		cb.setText(c.getString(c.getColumnIndexOrThrow(subsDbAdapter.KEY_FULL)));
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
				if(mSubsCursor.getCount() < 1)
					Toast.makeText(getApplicationContext(), "You have nothing to write out.",Toast.LENGTH_SHORT).show();
				else
					chooseExport();
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
	
/*-------------------------------------------------------------
--------------------------- Dialogs ---------------------------
-------------------------------------------------------------*/
	/** 
	 * Presents the End User License Agreement (EULA) dialog.  This does not
	 * check if Textspansion has been launched previously -- that occurs in
	 * {@link OnCreate}.
	 */
	public void presentEULA()
	{
		AlertDialog.Builder ed = new AlertDialog.Builder(this);
		ed.setIcon(R.drawable.icon);
		ed.setTitle("End-User License Agreement");
		ed.setView(LayoutInflater.from(this).inflate(R.layout.eula_dialog,null));

		ed.setPositiveButton("Agree", 
		new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
			SharedPreferences.Editor editor = sharedPrefs.edit(); 
			editor.putBoolean("EULA", true); 
			editor.commit(); 
			}
		});
		
		ed.setNegativeButton("Disagree (You will be kicked out)", 
		new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
			SharedPreferences.Editor editor = sharedPrefs.edit(); 
			editor.putBoolean("EULA", false);
			editor.commit(); 
			finish();
			}
		});
		ed.show();
	}

	/**
	 * Presents a dialog with export methods.  Currently only supporting email
	 * and SD card exporting.  This method is called when the user selects
	 * "Export" from the menu.
	 */
	public void chooseExport()
	{
		final CharSequence[] choices = {"Email", "SD card"};
		
		AlertDialog.Builder exporter = new AlertDialog.Builder(this);
		exporter.setIcon(R.drawable.icon);
		exporter.setTitle("Choose where to export to:");
		exporter.setItems(choices, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				switch(item){
					case 0:
						exportSubs();
						sendXml();
						break;
					case 1:
						exportSubs();
						break;
				}
			}
		});

		exporter.show();
	}

/*-------------------------------------------------------------
-------------------- Database Manipulation --------------------
-------------------------------------------------------------*/
	/**
	 * Updates the contents of the ListView.  Technically, requerys the
	 * database, uses that cursor in a new {@link privitized_adapter}, and then
	 * sets the List Adapter to said adapter.
	 */
	private void fillData()
	{
		mSubsCursor = mDbHelper.fetchAllSubs(sortByShort);
		startManagingCursor(mSubsCursor);
		
		privitized_adapter subsAdapter = new privitized_adapter(getApplicationContext(), mSubsCursor, "main");	
		setListAdapter(subsAdapter);
	}

	/**
	 * Adds an item to the database.  Shows a dialog that allows the user to
	 * enter a short name, long name, and to choose whether the new item's long
	 * name should be private.
	 * 
	 * This method is called when the user chooses "Add" from the menu.
	 */  
	public void addItem()
	{
		final Dialog dialog = new Dialog(textspansion.this);
		dialog.setContentView(R.menu.maindialog);
		dialog.setTitle("Adding an Entry");
		dialog.setCancelable(true);
		
		TextView short_text = (TextView) dialog.findViewById(R.id.short_label);
		final EditText short_input = (EditText) dialog.findViewById(R.id.short_entry);
		TextView long_text = (TextView) dialog.findViewById(R.id.long_label);
		final EditText long_input = (EditText) dialog.findViewById(R.id.long_entry);
	
		final CheckBox pvt_box = (CheckBox) dialog.findViewById(R.id.pvt_box);
		pvt_box.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				if ( isChecked )
					long_input.setTransformationMethod(new PasswordTransformationMethod());
				else
					long_input.setTransformationMethod(null);
			}
		});

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
				if(pvt_box.isChecked() && short_name.compareTo("") == 0)
						short_name = "Private Entry";
				else if(short_name.compareTo("") == 0 && (long_name.length() > 51))
					short_name = long_name.substring(0,49);
				else if (short_name.compareTo("") == 0)
					short_name = long_name;
				if( pvt_box.isChecked() )
					Toast.makeText(getApplicationContext(),
					"This will be private!", Toast.LENGTH_SHORT).show();
				if (mDbHelper.createSub(short_name, long_name, pvt_box.isChecked()) == -1)
					Toast.makeText(getApplicationContext(),
					"That item already exists.", Toast.LENGTH_SHORT).show();
				fillData();
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	
	/**
	 * Edits an item in the database.  Shows a dialog that allows the user to
	 * edit the selected item's short name, long name, and private state.
	 * 
	 * This method is called when the user chooses "Edit" from the context menu.
	 */  
	public void editItem(int item)
	{
		final int theItem = item+1; // SQL starts counting from 1
		final Dialog dialog = new Dialog(textspansion.this);
		dialog.setContentView(R.menu.maindialog);
		dialog.setTitle("Editing an Entry");
		dialog.setCancelable(true);
		
		TextView short_text = (TextView) dialog.findViewById(R.id.short_label);
		final EditText short_input = (EditText) dialog.findViewById(R.id.short_entry);
		TextView long_text = (TextView) dialog.findViewById(R.id.long_label);
		final EditText long_input = (EditText) dialog.findViewById(R.id.long_entry);
		
		final CheckBox pvt_box = (CheckBox) dialog.findViewById(R.id.pvt_box);
		pvt_box.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				if ( isChecked )
					long_input.setTransformationMethod(new PasswordTransformationMethod());
				else
					long_input.setTransformationMethod(null);
			}
		});
		
		Cursor c = mSubsCursor;
		c.moveToPosition(item);
		final String old_short  = c.getString(c.getColumnIndexOrThrow(subsDbAdapter.KEY_ABBR));
		final String old_full   = c.getString(c.getColumnIndexOrThrow(subsDbAdapter.KEY_FULL));
		final boolean old_pvt = ( c.getString(c.getColumnIndexOrThrow(subsDbAdapter.KEY_PRIVATE)).equals("1") ); // active high
		
		short_input.setText(old_short);
		long_input.setText(old_full);
		pvt_box.setChecked(old_pvt);
		
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
				if ( short_name.equals(old_short) && long_name.equals(old_full) && pvt_box.isChecked() == old_pvt )
				{
					dialog.dismiss();
				}
				else
				{
					if(pvt_box.isChecked() && short_name.compareTo("") == 0)
						short_name = "Private Entry";
					else if(short_name.compareTo("") == 0 && (long_name.length() > 51))
					{
						short_name = long_name.substring(0,49);
					}
					else if (short_name.compareTo("") == 0)
						short_name = long_name;
					
					if( !mDbHelper.updateSub(old_full, old_short, old_pvt, short_name, long_name, pvt_box.isChecked()))
						Toast.makeText(getApplicationContext(),
						"That item already exists.", Toast.LENGTH_SHORT).show();
					fillData();
					dialog.dismiss();
				}
			}
		});
		dialog.show();
	}

	/**
	 * Deletes the selected item from the database.  Note that this does not display a
	 * confirmation dialog.
	 *
	 * This method is called when a user selects "Delete" from the context menu.
	 */
	public void deleteItem(int item)
	{
		Cursor c = mSubsCursor;
		c.moveToPosition(item);
		final String old_short = c.getString(c.getColumnIndexOrThrow(subsDbAdapter.KEY_ABBR));
		final String old_full  = c.getString(c.getColumnIndexOrThrow(subsDbAdapter.KEY_FULL));
		final String old_pvt  = c.getString(c.getColumnIndexOrThrow(subsDbAdapter.KEY_PRIVATE));
		mDbHelper.deleteSub(old_full, old_short, old_pvt);
		fillData();
	}

/*-------------------------------------------------------------
----------------------- File I/O ------------------------------
-------------------------------------------------------------*/	

	/**
	 * Sends the exported XML representation of the database via email.  Reads
	 * the previously exported XML file at /sdcard/Textspansion/subs.xml and
	 * sends it with the email application of the user's choice (or default).
	 */	
	public void sendXml()
	{
		Intent send = new Intent(Intent.ACTION_SEND);
		send.setType("text/xml");
		send.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/Textspansion/subs.xml"));
		send.putExtra(Intent.EXTRA_SUBJECT, "[Textspansion] Database Export");
		startActivity(Intent.createChooser(send, "Send email using..."));
	}

	/**
	 * Exports the database as an XML file to the SD card.  The .xml file is
	 * created at /sdcard/Textspansion/subs.xml.  This method is called when
	 * the user chooses "Export" from the menu.  The output of this method is
	 * used in the {@link sendXml()} method.
	 */
	public void exportSubs()
	{
		try{
			File root = new File(extStoDir);
			Cursor c = mSubsCursor;
			if(!root.exists())
				root.mkdirs();

			if(root.canWrite()){
				String subs_short = null, subs_full = null, subs_pvt = null;
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
						subs_pvt = c.getString(c.getColumnIndexOrThrow(subsDbAdapter.KEY_PRIVATE));
						
						serializer.startTag("", "Subs");
						serializer.startTag("", "Short");
						serializer.text(subs_short);
						serializer.endTag("", "Short");
						serializer.startTag("", "Long");
						serializer.text(subs_full);
						serializer.endTag("", "Long");
						serializer.startTag("", "Private");
						serializer.text(subs_pvt);
						serializer.endTag("", "Private");
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
		}catch(IOException e){}
	}
}
