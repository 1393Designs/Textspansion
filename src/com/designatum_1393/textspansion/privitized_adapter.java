package com.designatum_1393.textspansion;

import android.database.Cursor;
import android.widget.TextView;
import android.content.Context;
import android.view.ViewGroup;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class privitized_adapter extends CursorAdapter
{
	private static final String KEY_ABBR = "abbr";
	private static final String KEY_FULL = "full";
	private static final String KEY_ROWID = "_id";
	private static final String KEY_PRIVATE = "_pvt";
	private static final String KEY_CLIP = "clip";
	private static final String KEY_DATE = "date";
	private boolean dontShowText = false;
	private int shortID, longID, layout;
	private String use;
	
	public privitized_adapter(Context context, Cursor c, String use)
	{
		super(context, c);
		this.use = use;
		if ( use.equals("multidelete") )
		{
			shortID = R.id.ShortText;
			longID = R.id.LongText;
			layout = R.layout.delete_list_item;
		}
		else
		{
			shortID = R.id.ShortTextMain;
			longID = R.id.LongTextMain;
			layout = R.layout.subs_row;
		}
	}
	
	public privitized_adapter(Context context, Cursor c, String use, boolean titleOnly)
	{
		super(context, c);
		this.dontShowText = titleOnly;
		this.use = use;
		if ( use.equals("multidelete") )
		{
			shortID = R.id.ShortText;
			longID = R.id.LongText;
			layout = R.layout.delete_list_item;
		}
		else
		{
			if(!dontShowText){
				shortID = R.id.ShortTextMain;
				longID = R.id.LongTextMain;
				layout = R.layout.subs_row;
			}
			else{
				shortID = R.id.ShortTextMain;
				layout = R.layout.subs_row_notitle;
			}
		}
	}

	@Override
	public View newView(Context context, Cursor c, ViewGroup viewGroup)
	{
		return LayoutInflater.from(context).inflate(layout, viewGroup, false);
	}

	@Override
	public void bindView(View v, Context context, Cursor c)
	{
		TextView shortView = (TextView) v.findViewById(shortID);
		TextView longView = null;
		if(use.equals("clips") || !dontShowText){
			longView = (TextView) v.findViewById(longID);
		}

		if(use.equals("clips")){
			shortView.setText(c.getString(c.getColumnIndexOrThrow(KEY_CLIP)));
			longView.setText(c.getString(c.getColumnIndexOrThrow(KEY_DATE)));
		}
		else{
			shortView.setText(c.getString(c.getColumnIndexOrThrow(KEY_ABBR)));
			if(!dontShowText){
				longView.setText(c.getString(c.getColumnIndexOrThrow(KEY_FULL)));

				if ( c.getString(c.getColumnIndexOrThrow(subsDbAdapter.KEY_PRIVATE)).equals("1") )
					longView.setTransformationMethod(new PasswordTransformationMethod());
				else
					longView.setTransformationMethod(null);
			}
		}
	}
}