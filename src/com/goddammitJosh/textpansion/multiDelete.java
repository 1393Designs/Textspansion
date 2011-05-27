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
		
		mDbHelper.close();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.multi_delete_menu, menu);
        return true;
    }
	
	@Override
    public boolean onMenuItemSelected(int featureId, MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.multi_confirm:
                return true;
			case R.id.multi_cancel:
				finish();
				return true;
			
        }
        return super.onMenuItemSelected(featureId, item);
    }
}
