package com.goddammitJosh.textpansion;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.database.Cursor;
import android.util.Log;
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

import java.util.ArrayList;

// inner class
import android.content.Context;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.util.AttributeSet;

public class multiDelete extends ListActivity
{
        private static ArrayList<String[]> selected = new ArrayList<String[]>(0);
    static class deleteListItem extends LinearLayout implements Checkable
    {

        private String _short;
        private String _long;
 
        private CheckBox _checkbox;
    
        public deleteListItem(Context context, AttributeSet attrs)
        {
            super(context, attrs);
        

        }
    
        @Override
        protected void onFinishInflate()
        {
            super.onFinishInflate();


            final LinearLayout ll = (LinearLayout)getChildAt(1); // get the inner linearLayout
            _checkbox = (CheckBox) findViewById(R.id.listCheckBox);
            _checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    if (isChecked)
                    {
                        _short = ((TextView) ll.getChildAt(0)).getText().toString();
                        _long  = ((TextView) ll.getChildAt(1)).getText().toString();
                        selected.add(new String[]{_short, _long});
                    }
                }
            });
    //        _short = ((TextView) ll.getChildAt(0)).getText().toString();
            //Log.i("deleteListItem", ((TextView) ll.findViewById(R.id.ShortText)).getText().toString() );
            Log.i("TESTING", "");
    //        _long = ((TextView) ).getText().toString();
            // find checked text view;
            int childCount = getChildCount();

        }
    
        @Override
        public boolean isChecked()
        {
            Log.i("deleteListItem", "SEEIN IF CHECKEDED");
            return _checkbox != null ? _checkbox.isChecked() : false;
        }
    
        @Override
        public void setChecked(boolean checked)
        {
            Log.i("deleteListItem", "SETTIN' CHECKEDDDD");
            if (_checkbox != null)
                _checkbox.setChecked(checked);
        }
    
        @Override
        public void toggle()
        {
            Log.i("deleteListItem", "THEYRE TOGGLIN MAH BUTTINZ");
            if (_checkbox != null)
                _checkbox.toggle();
        }
    }

	private subsDbAdapter mDbHelper = new subsDbAdapter(this);
	private SharedPreferences prefs;
	private SharedPreferences sharedPrefs;
	private boolean sortByShort;
	private Cursor mSubsCursor;
        private SimpleCursorAdapter subsAdapter; 

	
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_list);
		//R.layout.subs_list 		 android.R.layout.simple_list_item_multiple_choice
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

		if(sharedPrefs.getString("sortie", "HERPADERP").equals("short"))
			sortByShort = true;
		else if(sharedPrefs.getString("sortie", "HERPADERP").equals("long"))
			sortByShort = false;
		else
			Log.i("SORTING BY", "OOP");
		
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
				deleteSelected();
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

        @Override
        protected void onStop()
        {
            super.onStop();
            mSubsCursor.close();
	    mDbHelper.close();
        }
	
	public void deleteSelected()
        {
            Log.i("Delete", "Opening");
	    mDbHelper.open();
	    Log.i("Delete", "Opened");
            for(int i = 0; i < selected.size(); i++ )
            {
                mDbHelper.deleteSub(selected.get(i)[0], selected.get(i)[1]);
            }
	}

}
