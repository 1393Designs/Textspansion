package com.designatum_1393.textspansion.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListFragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import com.designatum_1393.textspansion.Preferences;
import com.designatum_1393.textspansion.R;
import com.designatum_1393.textspansion.Sub;
import com.designatum_1393.textspansion.utils.ImportExport;
import com.designatum_1393.textspansion.utils.SubsArrayAdapter;
import com.designatum_1393.textspansion.utils.SubsDataSource;

import java.util.ArrayList;

public class ClipFragment extends ListFragment {

    private SubsArrayAdapter subsArrayAdapter;
    private SubsDataSource subsDataSource;
    private ClipboardManager clipboardManager;
    private SharedPreferences sharedPreferences;
    public int selectedItem = -1;
    protected Object mActionMode;
    private View selectedView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.setHasOptionsMenu(true);

        clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        subsDataSource = new SubsDataSource(getActivity());
        subsDataSource.open();
        fillList();

        final AdapterView.OnItemLongClickListener listener = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (mActionMode != null)
                    return false;

                selectedItem = position;
                selectedView = view;

                // WHY DON'T THIS WORK?
                //view.setSelected(true);

                // Until above gets fixed, WORKAROUNDS!
                selectedView.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
                mActionMode = getActivity().startActionMode(mActionModeCallback);
                return true;
            }
        };

        getListView().setOnItemLongClickListener(listener);
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

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater menuInflater = mode.getMenuInflater();
            menuInflater.inflate(R.menu.main_context, menu);
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.edit_sub:
                    modifySub("edit");
                    mode.finish();
                    return true;
                case R.id.delete_sub:
                    deleteSub();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            selectedItem = -1;

            // Please replace this when we find the correct way to do this
            selectedView.setBackgroundColor(getResources().getColor(android.R.color.background_dark));
            selectedView = null;
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.actions, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_sub:
                modifySub("add");
                return true;
            case R.id.settings_menu:
                startActivity(new Intent(getActivity(), Preferences.class));
                return true;
            case R.id.export_menu:
                chooseExport();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        Sub clickedSub = subsArrayAdapter.getItem(position);
        Toast.makeText(getActivity(), clickedSub.getSubTitle() + " has been copied.", Toast.LENGTH_SHORT).show();

        clipboardManager.setPrimaryClip(ClipData.newPlainText("Textspansion Snippet", clickedSub.getPasteText()));

        getActivity().finish();
    }

    public void fillList() {
        if (sharedPreferences.getBoolean("hidePasteText", false)) {
            subsArrayAdapter = new SubsArrayAdapter(getActivity(), R.layout.clip_row_hide_paste, (ArrayList) subsDataSource.getAllSubs());
        } else {
            subsArrayAdapter = new SubsArrayAdapter(getActivity(), R.layout.clip_row, (ArrayList) subsDataSource.getAllSubs());
        }
        setListAdapter(subsArrayAdapter);
    }

    private void modifySub(final String modifyType) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.add_dialog);
        if (modifyType.equals("add"))
            dialog.setTitle(R.string.add_title);
        else if (modifyType.equals("edit"))
            dialog.setTitle(R.string.edit_title);
        dialog.setCancelable(true);

        final EditText subTitleInput = (EditText) dialog.findViewById(R.id.subTitleEntry);
        final EditText pasteTextInput = (EditText) dialog.findViewById(R.id.pasteTextEntry);
        final CheckBox pvtBox = (CheckBox) dialog.findViewById(R.id.pvt_box);
        final Sub subToEdit = subsDataSource.getSub(selectedItem);

        if (modifyType.equals("edit")) {
            subTitleInput.setText(subToEdit.getSubTitle());
            pasteTextInput.setText(subToEdit.getPasteText());
            if (subToEdit.isPrivate()) {
                pvtBox.setChecked(true);
                pasteTextInput.setTransformationMethod(new PasswordTransformationMethod());
            }
        }

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
            if (modifyType.equals("add")) {
                if (subsDataSource.addSub(newSub) == -1) {
                    Toast.makeText(
                        getActivity().getApplicationContext(),
                        "That item already exists.",
                        Toast.LENGTH_SHORT
                    ).show();
                }
            } else if (modifyType.equals("edit")) {
                subsDataSource.editSub(subToEdit, newSub);
                fillList();
            }
            if (modifyType.equals("add")) {
                subsArrayAdapter.add(newSub);
            }
            dialog.dismiss();
            }
        });
        dialog.show();
        subsArrayAdapter.notifyDataSetChanged();
    }

    private void deleteSub() {
        Sub subToDelete = subsDataSource.getSub(selectedItem);
        subsDataSource.deleteSub(subToDelete);
        fillList();
        subsArrayAdapter.notifyDataSetChanged();
    }

    private void chooseExport() {
        //Sets up an AlertDialog to allow user to choose where they wish to export the database to
        final CharSequence[] choices = {"Email", "SD card"};

        AlertDialog.Builder exporter = new AlertDialog.Builder(getActivity());
        exporter.setIcon(R.drawable.icon);
        exporter.setTitle(getResources().getString(R.string.export_title));
        exporter.setItems(choices, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        ImportExport.exportSubs(subsDataSource, getActivity().getApplicationContext());
                        emailJson();
                        break;
                    case 1:
                        ImportExport.exportSubs(subsDataSource, getActivity().getApplicationContext());
                        break;
                }
            }
        });
        exporter.show();
    }

    public void emailJson() {
        Intent send = new Intent(Intent.ACTION_SEND);
        send.setType("text/xml");
        send.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/Textspansion/subs.json"));
        send.putExtra(Intent.EXTRA_SUBJECT, "[Textspansion] Database Export");
        startActivity(Intent.createChooser(send, "Send email using..."));
    }
}