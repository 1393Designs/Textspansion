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
/*import java.util.SortedMap;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;*/

import android.widget.SimpleCursorAdapter;
import android.database.Cursor;

public class subsList extends ListActivity //implements OnGlobalFocusChangeListener
{
    public static final int INSERT_ID = Menu.FIRST;

    private int mSubNumber = 1;

    private subsDbAdapter mDbHelper;
    private Cursor mSubsCursor;

    //private final ClipboardManager cb = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subs_list); // TODO: change to a real list
        mDbHelper = new subsDbAdapter(this);
        mDbHelper.open();
        fillData();
        registerForContextMenu(getListView());
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);
        Cursor c = mSubsCursor;
        Log.i("textspansion", "Clicked");
        Toast.makeText(getApplicationContext(), "Clicked!", Toast.LENGTH_SHORT).show();
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
                createSub();
                return true;
            // add delete here ?
        }
        return super.onMenuItemSelected(featureId, item);
    }

    private void createSub()
    {
        mDbHelper.createSub("Something", "herp");
        fillData();
    }

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

        



		
    

/*    @Override
    public Object onRetainNonConfigurationInstance()
    {
        SortedMap savedMap = new TreeMap();
        savedMap.putAll(subsMap);
        return savedMap;
    }   */

/*    @Override // ours!
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sub_list_menu, menu);
        return true;
    }

    @Override // ours!
    public boolean onOptionsItemSelected(MenuItem item)
    {
        //Handle item selection
        switch (item.getItemId())
        {
            case R.id.add_item:
                createSub();
                
                return true;
            //case R.id.delete:
            //    deleteItems();
            //    return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

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
                //editItem(info.id);
                return true;
            case R.id.delete_item:
                //deleteItem(subsMap, info.id);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

/*    public void addItem()
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
				subsMap.put(short_name, long_name);
				//Log.d("textspansion", subsMap.firstKey().toString());
				subs.clear();
				subs.addAll(subsMap.values());
				aa.notifyDataSetChanged();
				dialog.dismiss();
			}
		});
		dialog.show();

    }*/
	

/*    public void deleteItems()
    {
        Toast.makeText(getApplicationContext(), "Multi-delete coming not so soon.", Toast.LENGTH_SHORT).show();
    }

    public void editItem(long item)
    {
		
        Toast.makeText(getApplicationContext(), "Edit coming not so soon.", Toast.LENGTH_SHORT).show();
    }
    
    public void deleteItem(SortedMap map, long item)
    {
        // when showing by key, use map.remove(subsget((int)item))
        if (map.isEmpty())
            Log.d("textspansion", "IT BE EMPTY");
        map.remove(subs.get((int)item));
        Log.d("textspansion", map.firstKey().toString());
        subs.clear();
        subs.addAll(map.values());
        aa.notifyDataSetChanged();
    }*/

}
