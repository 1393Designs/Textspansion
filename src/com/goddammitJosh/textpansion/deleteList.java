package com.goddammitJosh.textpansion;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.util.Log;
import android.widget.TextView;
import java.util.ArrayList;



public class deleteList extends LinearLayout
{
	public deleteList(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate()
	{
		super.onFinishInflate();
		Log.i("deleteList", "INNNNNNFFLLLLLAAAATTTTEEEEDDDD");
	}
}
