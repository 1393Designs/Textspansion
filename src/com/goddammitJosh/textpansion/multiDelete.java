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


public class multiDelete extends ListActivity
{
	private subsDbAdapter mDbHelper = new subsDbAdapter(this);
	private SharedPreferences prefs;
	private SharedPreferences sharedPrefs;
	private boolean sortByShort;
	private Cursor mSubsCursor;

	
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.subs_main);
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
        SimpleCursorAdapter subsAdapter = new SimpleCursorAdapter(getApplicationContext(),
            R.layout.subs_delete, mSubsCursor, from, to);

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
	
	public void deleteSelected(){
		Log.i("Delete", "YOU CALLLLLLLLLLED????????");
		ListView lv = getListView();
		String where, abbr, full = null;
		LinearLayout itemLayout;
		CheckBox cb;
		
		SparseBooleanArray SBA = getListView().getCheckedItemPositions();
		
		long[] derp = getListView().getCheckedItemIds();
		
		if (derp.length==0)
			Log.i("Delete", "SUMPIN WRONG");
		
		for(int i = 0; i < derp.length; i++)
		{
			Log.i("Delete derp", "Checked ID: " + derp[i]);
		}
		
		// for( int i = 0; i < lv.getChildCount(); i++)
		// {
			// itemLayout = (LinearLayout)lv.getChildAt(i);
			// cb = (CheckBox)itemLayout.findViewById(R.id.listCheckBox);
			

			
			// if (cb.isChecked())
			// {
				// abbr = ((TextView)itemLayout.findViewById(R.id.ShortText)).getText().toString();
				// full = ((TextView)itemLayout.findViewById(R.id.LongText)).getText().toString();
				// //where.concat(KEY_ABBR +"=" +{read short} +" AND " +{read long})
				// //if(i != (lv.getChildCount()-1))
				// //	where.concat(" OR ");
				
				// Log.i("DELETE", abbr);
			// }
		// }
		// SparseBooleanArray SBA = getListView().getCheckedItemPositions();
		
		// long[] derp = getListView().getCheckedItemIds();
		
		// for(int i = 0; i < derp.length; i++)
		// {
		
			// Log.i("Delete derp", "Checked ID: " + derp[i]);
		
		// }
		// for(int i = 0; i < SBA.size(); i++)
		// {
			// Log.i("Delete", Boolean.toString(SBA.valueAt(i)));
		
		
		
		// }
	
	
	
	
	
	
	
	}

}
