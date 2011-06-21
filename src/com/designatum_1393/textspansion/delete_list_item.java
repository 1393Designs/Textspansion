package com.designatum_1393.textspansion;

import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.content.Context;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.util.AttributeSet;
import android.text.method.PasswordTransformationMethod;
import android.widget.TextView;
import android.database.Cursor;
import android.util.Log;

class delete_list_item extends LinearLayout implements Checkable
{
	private String _short;
	private String _long;

	private CheckBox _checkbox;
	private String[] arr;
	private int count = 1;
	private subsDbAdapter helper = new subsDbAdapter(getContext());
	private TextView longView;

	public delete_list_item(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate()
	{
		super.onFinishInflate();
		Log.i("delete_list_item", "finished inflating!");
		count++;
		final LinearLayout ll = (LinearLayout)getChildAt(1); // get the inner linearLayout	
		longView = (TextView) findViewById(R.id.LongText);
		//Cursor c = helper.fetchAllSubs(false);
		/*Cursor c = helper.fetchSub((long)count);
		if ( c.getString(c.getColumnIndexOrThrow(subsDbAdapter.KEY_PRIVATE)).equals("1") )*/
		longView.setTransformationMethod(new PasswordTransformationMethod());


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
