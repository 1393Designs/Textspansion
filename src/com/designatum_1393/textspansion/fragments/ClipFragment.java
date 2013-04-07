package com.designatum_1393.textspansion;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ClipFragment extends ListFragment {

	View mContentView = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.clips_list,
			container, false);

		return mContentView;
	}
} 