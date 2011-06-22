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

public class privitized_adapter extends CursorAdapter
{
	private static final String KEY_ABBR = "abbr";
	private static final String KEY_FULL = "full";
	private static final String KEY_ROWID = "_id";
	private static final String KEY_PRIVATE = "_pvt";
	private int shortID, longID, layout;

	public privitized_adapter(Context context, Cursor c, String use)
	{
		super(context, c);
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

	@Override
	public View newView(Context context, Cursor c, ViewGroup viewGroup)
	{
		return LayoutInflater.from(context).inflate(layout, viewGroup, false);
	}

	@Override
	public void bindView(View v, Context context, Cursor c)
	{
		//LinearLayout ll = (LinearLayout) v.findViewById(R.id.innerLayout);
		TextView shortView = (TextView) v.findViewById(shortID);
		TextView longView = (TextView) v.findViewById(longID);

		shortView.setText(c.getString(c.getColumnIndexOrThrow(KEY_ABBR)));
		longView.setText(c.getString(c.getColumnIndexOrThrow(KEY_FULL)));
		
		if ( c.getString(c.getColumnIndexOrThrow(subsDbAdapter.KEY_PRIVATE)).equals("1") )
			longView.setTransformationMethod(new PasswordTransformationMethod());
		else
			longView.setTransformationMethod(null);
	}
}
