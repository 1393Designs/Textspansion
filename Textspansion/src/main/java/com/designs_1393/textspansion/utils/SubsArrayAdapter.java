package com.designs_1393.textspansion.utils;


import android.content.Context;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.designs_1393.textspansion.R;
import com.designs_1393.textspansion.Sub;

import java.util.ArrayList;

public class SubsArrayAdapter extends ArrayAdapter<Sub> {
    Context context;
    int layoutResourceId;
    ArrayList<Sub> subs = null;

    public SubsArrayAdapter(Context context, int layoutResourceId, ArrayList<Sub> subs) {
        super(context, layoutResourceId, subs);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.subs = subs;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = LayoutInflater.from(context).inflate(layoutResourceId, parent, false);

        TextView subTitle = (TextView)row.findViewById(R.id.SubTitleMain);
        subTitle.setText(subs.get(position).getSubTitle());

        if (layoutResourceId == R.layout.clip_row) {
            TextView pasteText = (TextView)row.findViewById(R.id.PasteTextMain);
            pasteText.setText(subs.get(position).getPasteText());

            if (subs.get(position).isPrivate()) {
                pasteText.setTransformationMethod(new PasswordTransformationMethod());
            } else {
                pasteText.setTransformationMethod(null);
            }
        }

        return row;
    }
}