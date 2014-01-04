package com.designs_1393.textspansion.fragments;

import android.app.ListFragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.designs_1393.textspansion.R;
import com.designs_1393.textspansion.Sub;
import com.designs_1393.textspansion.utils.SubsArrayAdapter;
import com.designs_1393.textspansion.utils.SubsDataSource;
import com.designs_1393.textspansion.utils.Tokens;

import java.util.ArrayList;

public class QuickViewFragment  extends ListFragment {

    private SubsArrayAdapter subsArrayAdapter;
    private SubsDataSource subsDataSource;
    private ClipboardManager clipboardManager;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if(!sharedPreferences.contains("sortie"))
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("sortie", "subTitle");
            editor.commit();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.setHasOptionsMenu(true);

        clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        subsDataSource = new SubsDataSource(getActivity());
        subsDataSource.open();
        fillList();

        getListView().setDivider(getResources().getDrawable(R.color.list_divider));
        getListView().setDividerHeight(2);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!subsDataSource.isOpen()) {
            subsDataSource.open();
        }
        fillList();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (subsDataSource.isOpen()) {
            subsDataSource.close();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (subsDataSource.isOpen()) {
            subsDataSource.close();
        }
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        // If not in CAB mode
        Sub clickedSub = subsArrayAdapter.getItem(position);
        Toast.makeText(getActivity(), clickedSub.getSubTitle() + " has been copied.", Toast.LENGTH_SHORT).show();

        clipboardManager.setPrimaryClip(ClipData.newPlainText("Textspansion Snippet",
                Tokens.replace(clickedSub.getPasteText(), getActivity()))
        );

        getActivity().finish();
    }

    public void fillList() {
        if (sharedPreferences.getBoolean("hidePasteText", false)) {
            subsArrayAdapter = new SubsArrayAdapter(getActivity(),
                    R.layout.clip_row_hide_paste,
                    (ArrayList) subsDataSource.getAllSubs()
            );
        } else {
            subsArrayAdapter = new SubsArrayAdapter(getActivity(),
                    R.layout.clip_row,
                    (ArrayList) subsDataSource.getAllSubs()
            );
        }
        setListAdapter(subsArrayAdapter);
    }
}
