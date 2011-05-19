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

//menu
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;

//Export 
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import android.os.Environment;

//Import
import java.io.BufferedReader;
import java.io.FileReader;

//context menu
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;

//sorted map

import android.widget.SimpleCursorAdapter;
import android.database.Cursor;

public class subsList extends ListActivity //implements OnGlobalFocusChangeListener
{
    public static final int INSERT_ID = Menu.FIRST;
	public static final int EXPORT_ID = Menu.NONE;

    private int mSubNumber = 1;

    private subsDbAdapter mDbHelper;
    private Cursor mSubsCursor;

    private ClipboardManager cb;
	
	private String extStoDir = Environment.getExternalStorageDirectory().toString() + "/Textspansion";
	private static final String TAG = "Textspansion: SubsList";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subs_list); // TODO: change to a real list
        mDbHelper = new subsDbAdapter(this);
        mDbHelper.open();
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
        Log.i("textspansion", "Clicked");
        Toast.makeText(getApplicationContext(), c.getString(c.getColumnIndexOrThrow(subsDbAdapter.KEY_ABBR))
            , Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(), c.getString(c.getColumnIndexOrThrow(subsDbAdapter.KEY_FULL))
            , Toast.LENGTH_SHORT).show();

        cb.setText(c.getString(c.getColumnIndexOrThrow(subsDbAdapter.KEY_FULL)));
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
			case R.id.menu_import:
				importSubs();
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
        mSubsCursor = mDbHelper.fetchAllSubs();
        startManagingCursor(mSubsCursor);
		
        String[] from = new String[]{subsDbAdapter.KEY_ABBR, subsDbAdapter.KEY_FULL};
        int[] to = new int[]{android.R.id.text1, android.R.id.text2};
		
        // Now create an array adapter and set it to display using the stock android row
        SimpleCursorAdapter subsAdapter = new SimpleCursorAdapter(getApplicationContext(),
            android.R.layout.two_line_list_item, mSubsCursor, from, to);
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
				Toast.makeText(getApplicationContext(), "Editing "+theItem+".", Toast.LENGTH_SHORT).show();
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

	public void exportSubs()
	{
		try{
			File root = new File(extStoDir);
			if(!root.exists())
				root.mkdirs();
			if(root.canWrite()){
				Cursor c = mSubsCursor;
				String subs_short = null, subs_full  = null;
				int i = 0;
				c.moveToPosition(i);
				File outTXT = new File(extStoDir, "subs.txt");
				FileWriter outTXTWriter = new FileWriter(outTXT);
				BufferedWriter out = new BufferedWriter(outTXTWriter);
				do
				{
					subs_short = c.getString(c.getColumnIndexOrThrow(subsDbAdapter.KEY_ABBR));
					subs_full = c.getString(c.getColumnIndexOrThrow(subsDbAdapter.KEY_FULL));
					out.write(subs_short + "\t" + subs_full + "\n");
					c.move(1);
				}while(!c.isAfterLast());
				
				out.close();
				Toast.makeText(getApplicationContext(), "Substitutions saved to SD!",Toast.LENGTH_SHORT).show();
			}
			else
				Toast.makeText(getApplicationContext(), "App isn't allowed to write to SD! :(",Toast.LENGTH_SHORT).show();
		}catch(IOException e){
			Log.e(TAG, "Could not write file :" + e.getMessage());
		}
	}
	
	public void importSubs()
	{
		try{
			File root = new File(extStoDir);
			File inTXT = new File(extStoDir, "subs.txt");
			if(inTXT.exists())
			{
				BufferedReader buf = new BufferedReader(new FileReader(inTXT));
				String temp = null;
				String[] splits = null;
				while((temp=buf.readLine()) != null)
				{
					splits = temp.split("\t");
					mDbHelper.createSub(splits[0], splits[1]);
					fillData();
				}
			}
		}catch(IOException e){
			Log.e(TAG, "Could not read file :" + e.getMessage());
		}
	}
}