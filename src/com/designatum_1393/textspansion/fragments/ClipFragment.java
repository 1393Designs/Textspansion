package com.designatum_1393.textspansion.fragments;

import android.app.Dialog;
import android.app.ListFragment;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.designatum_1393.textspansion.R;
import com.designatum_1393.textspansion.Sub;
import com.designatum_1393.textspansion.Textspansion;
import com.designatum_1393.textspansion.utils.SubsArrayAdapter;
import com.designatum_1393.textspansion.utils.SubsDataSource;

import java.util.ArrayList;
import java.util.List;

public class ClipFragment extends ListFragment {

    private SubsDataSource subsDataSource;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.setHasOptionsMenu(true);

        subsDataSource = new SubsDataSource(getActivity());
        subsDataSource.open();
        List<Sub> values = subsDataSource.getAllSubs();
        SubsArrayAdapter adapter = new SubsArrayAdapter(getActivity(), R.layout.clip_row, (ArrayList)values);
        setListAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.actions, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add_sub:
                addSub();
                return true;
            case R.id.settings_menu:
                return true;
            case R.id.export_menu:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addSub() {
        final Dialog dialog = new Dialog(getActivity());
        final SubsArrayAdapter subsArrayAdapter = (SubsArrayAdapter)getListAdapter();
        dialog.setContentView(R.layout.add_dialog);
        dialog.setTitle(R.string.add_title);
        dialog.setCancelable(true);

        final EditText subTitleInput = (EditText) dialog.findViewById(R.id.subTitleEntry);
        final EditText pasteTextInput = (EditText) dialog.findViewById(R.id.pasteTextEntry);

        final CheckBox pvtBox = (CheckBox) dialog.findViewById(R.id.pvt_box);
        pvtBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                    pasteTextInput.setTransformationMethod(new PasswordTransformationMethod());
                else
                    pasteTextInput.setTransformationMethod(null);
            }
        });

        Button cancel_button = (Button) dialog.findViewById(R.id.cancelButton);
        cancel_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button okay_button = (Button) dialog.findViewById(R.id.okayButton);
        okay_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String subTitle = subTitleInput.getText().toString();
                String pasteText = pasteTextInput.getText().toString();
                if(pvtBox.isChecked() && subTitle.compareTo("") == 0)
                    subTitle = "Private Entry";
                else if(subTitle.compareTo("") == 0 && (pasteText.length() > 51))
                    subTitle = pasteText.substring(0,49);
                else if (subTitle.compareTo("") == 0)
                    subTitle = pasteText;

                Sub newSub = new Sub(subTitle, pasteText, pvtBox.isChecked());
                if (subsDataSource.addSub(newSub) == -1)
                    Toast.makeText(getActivity().getApplicationContext(),
                            "That item already exists.", Toast.LENGTH_SHORT).show();

                subsArrayAdapter.add(newSub);
                dialog.dismiss();
            }
        });
        dialog.show();
        subsArrayAdapter.notifyDataSetChanged();
    }
}