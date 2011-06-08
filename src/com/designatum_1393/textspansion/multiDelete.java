package com.designatum_1393.textspansion;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.database.Cursor;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.app.ListActivity;
import android.os.Bundle;
import java.lang.Object;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.util.SparseBooleanArray;
import java.lang.Boolean;
import android.widget.LinearLayout;
import android.widget.CheckBox;	
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;

import java.util.ArrayList;

// inner class
import android.content.Context;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.util.AttributeSet;

import android.util.Log;

public class multiDelete extends ListActivity
{
	static class deleteListItem extends LinearLayout implements Checkable
	{
		private String _short;
		private String _long;
 
		private CheckBox _checkbox;
		private String[] arr;
	
		public deleteListItem(Context context, AttributeSet attrs)
		{
			super(context, attrs);
		}
	
		@Override
		protected void onFinishInflate()
		{
			super.onFinishInflate();
			Log.i("multidelete", "Inflate finished");

			final LinearLayout ll = (LinearLayout)getChildAt(1); // get the inner linearLayout
			_checkbox = (CheckBox) findViewById(R.id.listCheckBox);
		}
	
		@Override
		public boolean isChecked()
		{
			return _checkbox != null ? _checkbox.isChecked() : false;
		}
	
		@Override
		public void setChecked(boolean checked)
		{
			_checkbox.setChecked(checked);
		}
	
		@Override
		public void toggle()
		{
			if (_checkbox != null) // necessary?
				_checkbox.toggle();
		}
	} // deleteListItem

	private subsDbAdapter mDbHelper = new subsDbAdapter(this);
	private SharedPreferences prefs;
	private SharedPreferences sharedPrefs;
	private boolean sortByShort;
	private Cursor mSubsCursor;
	private SimpleCursorAdapter subsAdapter; 
	private String full, abbr;
	private ArrayList<String> aSelected = new ArrayList<String>(0);
	private ArrayList<String> fSelected = new ArrayList<String>(0);

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
		Cursor c = mSubsCursor;
		c.moveToPosition(position);
		abbr = c.getString(c.getColumnIndexOrThrow(subsDbAdapter.KEY_ABBR));
		full = c.getString(c.getColumnIndexOrThrow(subsDbAdapter.KEY_FULL));

		if ( aSelected.contains(abbr) && fSelected.contains(full) )
		{
			aSelected.remove(abbr);
			fSelected.remove(full);
		}
		else
		{
			aSelected.add(abbr);
			fSelected.add(full);
		}
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.delete_list);
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

		if(sharedPrefs.getString("sortie", "HERPADERP").equals("short"))
			sortByShort = true;
		else if(sharedPrefs.getString("sortie", "HERPADERP").equals("long"))
			sortByShort = false;
		
		mDbHelper.open();
		mSubsCursor = mDbHelper.fetchAllSubs(sortByShort);
		startManagingCursor(mSubsCursor);
		
		String[] from = new String[]{subsDbAdapter.KEY_ABBR, subsDbAdapter.KEY_FULL};
		int[] to = new int[]{R.id.ShortText, R.id.LongText};
		
		// Now create an array adapter and set it to display using the stock android row
		subsAdapter = new SimpleCursorAdapter(getApplicationContext(),
			R.layout.delete_list_item, mSubsCursor, from, to);

		setListAdapter(subsAdapter);
		
		ListView list=getListView();
		list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		list.setItemsCanFocus(false);
		
		final Button confirm = (Button) findViewById(R.id.multi_confirm);
		confirm.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				deleteDialog();
			}
		});
		
		final Button cancel = (Button) findViewById(R.id.multi_cancel);
		cancel.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				finish();
			}
		});

		mDbHelper.close();
	}
	
	private void fillData()
	{
		mSubsCursor = mDbHelper.fetchAllSubs(sortByShort);
		startManagingCursor(mSubsCursor);
		
		String[] from = new String[]{subsDbAdapter.KEY_ABBR, subsDbAdapter.KEY_FULL};
		int[] to = new int[]{R.id.ShortText, R.id.LongText};
		
		subsAdapter = new SimpleCursorAdapter(getApplicationContext(),
		R.layout.delete_list_item, mSubsCursor, from, to);

		setListAdapter(subsAdapter);
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		mSubsCursor.close();
		mDbHelper.close();
	}

	public void deleteSelected()
	{
		boolean val = true;
		mDbHelper.open();
		for(int i = 0; i < aSelected.size(); i++ )
		{
			val = mDbHelper.deleteSub(fSelected.get(i), aSelected.get(i));
		}
		fillData();
	}
	
	public void deleteDialog(){
		AlertDialog.Builder dd = new AlertDialog.Builder(this);
		dd.setIcon(R.drawable.icon);
		dd.setTitle("Are you sure?");
		dd.setView(LayoutInflater.from(this).inflate(R.layout.delete_selected_dialog,null));

		dd.setPositiveButton("Yes, delete selected", 
		new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
			deleteSelected();
			}
		});
		
		dd.setNegativeButton("NO", 
		new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
			}
		});
		
		dd.show();
	}
}
