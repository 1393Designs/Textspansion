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

    private int mSubNumber = 1;

    private subsDbAdapter mDbHelper;
    private Cursor mSubsCursor;

    private ClipboardManager cb;

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
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_add_item);
        return result;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item)
    {
        switch (item.getItemId())
        {
            case INSERT_ID:
                addItem();
                return true;
            // add delete here ?
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
        int[] to = new int[]{R.id.line1, R.id.line2};

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
		
		Button add_button = (Button) dialog.findViewById(R.id.addButton);
		add_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String short_name = short_input.getText().toString();
				String long_name = long_input.getText().toString();
                                
                                mDbHelper.createSub(short_name, long_name);
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
		dialog.setTitle("Adding a thingy");
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
		
		Button add_button = (Button) dialog.findViewById(R.id.addButton);
		add_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String short_name = short_input.getText().toString();
				String long_name = long_input.getText().toString();
                                if ( short_name.equals(old_short) && long_name.equals(old_full) )
                                {
                                    dialog.dismiss();
                                }
                                else
                                {
                                    mDbHelper.updateSub(theItem, short_name, long_name);
                                    fillData();
				    dialog.dismiss();
                                }
			}
		});
		dialog.show();
    }
    
    public void deleteItem(int item)
    {
        //mDbHelper.deleteSub(item+1);
        //fillData();
        Toast.makeText(getApplicationContext(), "Delete is still buggy.  Sowwy :("
            , Toast.LENGTH_SHORT).show();
    }

}
