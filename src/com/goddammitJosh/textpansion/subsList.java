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
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class subsList extends ListActivity //implements OnGlobalFocusChangeListener
{

    ArrayAdapter<String> aa; 
    SortedMap subsMap;
    List<String> subs;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		
        String act = null;
        try
        {
            act = ((Activity) this).getCallingPackage();
        }
        catch (NullPointerException ex)
        {
            Log.i("sublist", "WTF");
        }
        finally
        {
            if (act==null)
                Log.i("sublist", "act is null");
            else
                Log.i("subsList", act);
        }
    
        final ClipboardManager cb = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);

        String[] substitutions = getResources().getStringArray(R.array.subs_array);
		
		//Sets up SortedMap of strings to keep everything organized
        subsMap = (SortedMap) getLastNonConfigurationInstance();
        if (subsMap == null)
        {
            subsMap = new TreeMap();
            for(String e : substitutions)
            {
                subsMap.put(e, e);
            }
        }
        
		subs = new ArrayList(subsMap.values());
		
		//Initialize arrayadapter
        aa = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, subs);
        setListAdapter(aa);

        ListView lv = getListView();
        lv.setTextFilterEnabled(true);

        // allows each item to have a context menu
        registerForContextMenu(lv);

        lv.setOnItemClickListener(new OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view,
                int position, long id)
            {
                // when clicked, copy to clipboard!
                //ClipboardManager cb =  new ClipboardManager();
                cb.setText(( (TextView)view).getText() );

                /* this is all for API level 11 (Honeycomb, i believe)
                 * ClipData.Item item = ClipData.Item(view.getText());
                 * ClipData text = ClipData( "Expansion", MIMETYPE_TEXT_PLAIN, item);
                 * cb.setPrimaryClip(text); */
                
                // notify that copy was successful!
                String toastString = ((TextView) view).getText().toString().concat(" ");
                toastString = toastString.concat(getResources().getString(R.string.toast_copied));
                
                Toast.makeText(getApplicationContext(), toastString,
                    Toast.LENGTH_SHORT).show();

                // return to the app you were in.
                finish();
            }
        });
    } // onCreate

    @Override
    public Object onRetainNonConfigurationInstance()
    {
        SortedMap savedMap = new TreeMap();
        savedMap.putAll(subsMap);
        return savedMap;
    }   

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sub_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        //Handle item selection
        switch (item.getItemId())
        {
            case R.id.add_item:
                addItem();
                return true;
            case R.id.delete:
                deleteItems();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                editItem(info.id);
                return true;
            case R.id.delete_item:
                deleteItem(subsMap, info.id);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
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
				subsMap.put(short_name, long_name);
				//Log.d("textspansion", subsMap.firstKey().toString());
				subs.clear();
				subs.addAll(subsMap.values());
				aa.notifyDataSetChanged();
				dialog.dismiss();
			}
		});
		dialog.show();

    }
	

    public void deleteItems()
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
    }

}
