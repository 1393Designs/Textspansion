package com.designatum_1393.textspansion.utils;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.designatum_1393.textspansion.R;
import com.designatum_1393.textspansion.Sub;

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
        TextView pasteText = (TextView)row.findViewById(R.id.PasteTextMain);

        subTitle.setText(subs.get(position).getSubTitle());
        pasteText.setText(subs.get(position).getPasteText());

        return row;
    }
}