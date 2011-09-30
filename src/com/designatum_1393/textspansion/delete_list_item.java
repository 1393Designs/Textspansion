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

class delete_list_item extends LinearLayout implements Checkable
{
	private String _short;
	private String _long;

	private CheckBox _checkbox;
	private String[] arr;
	private int count = 1;

	public delete_list_item(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate()
	{
		super.onFinishInflate();
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
		if (_checkbox != null)
			_checkbox.toggle();
	}
} 
