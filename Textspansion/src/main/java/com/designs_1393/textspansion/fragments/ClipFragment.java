package com.designs_1393.textspansion.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListFragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.Toast;

import com.designs_1393.textspansion.Preferences;
import com.designs_1393.textspansion.R;
import com.designs_1393.textspansion.Sub;
import com.designs_1393.textspansion.utils.ImportExport;
import com.designs_1393.textspansion.utils.SubsArrayAdapter;
import com.designs_1393.textspansion.utils.SubsDataSource;
import com.designs_1393.textspansion.utils.Tokens;

import java.io.File;
import java.util.ArrayList;

public class ClipFragment extends ListFragment {

    private SubsArrayAdapter subsArrayAdapter;
    private SubsDataSource subsDataSource;
    private ClipboardManager clipboardManager;
    private SharedPreferences sharedPreferences;
    private boolean selected = false;
    private ArrayList<Integer> selectedItems = new ArrayList<Integer>();
    protected ActionMode mActionMode;
    private ArrayList<View> selectedViews = new ArrayList<View>();
    private File dbFile = new File("/data/data/com.designs_1393.textspansion/shared_prefs/", "data");


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

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mActionMode != null)
                    return false;

                view.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
                selectedViews.add(view);
                selectedItems.add(i);
                selected = true;
                mActionMode = getActivity().startActionMode(mActionModeCallback);
                return true;
            }
        });

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

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater menuInflater = mode.getMenuInflater();
            menuInflater.inflate(R.menu.main_context, menu);
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            if (selectedItems.size() > 1)
                menu.findItem(R.id.edit_sub).setVisible(false);
            else
                menu.findItem(R.id.edit_sub).setVisible(true);
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
            selectedItems.clear();

            // Please replace this when we find the correct way to do this
            selected = false;
            for (View selectedView : selectedViews) {
                selectedView.setBackgroundColor(getResources().getColor(R.color.sub_row_background));
            }
            selectedViews.clear();
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
        if (!selected) {
            // If not in CAB mode
            Sub clickedSub = subsArrayAdapter.getItem(position);
            Toast.makeText(getActivity(), clickedSub.getSubTitle() + " has been copied.", Toast.LENGTH_SHORT).show();

            clipboardManager.setPrimaryClip(
                    ClipData.newPlainText("Textspansion Snippet",
                            Tokens.replace(clickedSub.getPasteText(), getActivity())
                    )
            );

            getActivity().finish();
        } else if (selectedItems.contains(position)) {
            // If in CAB mode and the clicked item has already been clicked on
            // unselect item by removing from selectedItems and selectedViews

            View selectedView = selectedViews.get(selectedItems.indexOf(position));
            selectedView.setBackgroundColor(getResources().getColor(R.color.sub_row_background));
            selectedViews.remove(selectedItems.indexOf(position));
            selectedItems.remove(selectedItems.indexOf(position));
            mActionMode.invalidate();
            if (selectedItems.size() == 0)
                mActionMode.finish();
        } else {
            // Otherwise, select the item and add to lists
            view.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
            selectedItems.add(position);
            selectedViews.add(view);
            mActionMode.invalidate();
        }
    }

    public void fillList() {
        if (sharedPreferences.getBoolean("hidePasteText", false)) {
            subsArrayAdapter = new SubsArrayAdapter(
                    getActivity(),
                    R.layout.clip_row_hide_paste,
                    (ArrayList) subsDataSource.getAllSubs()
            );
        } else {
            subsArrayAdapter = new SubsArrayAdapter(
                    getActivity(),
                    R.layout.clip_row,
                    (ArrayList) subsDataSource.getAllSubs()
            );
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

        int editPosition = -1;
        if (selectedItems.size() == 1)
            editPosition = selectedItems.get(0);

        final Sub subToEdit = subsDataSource.getSub(editPosition);

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
                if (subsDataSource.addSub(newSub)) {
                    subsArrayAdapter.add(newSub);
                    fillList();
                } else {
                    Toast.makeText(
                            getActivity().getApplicationContext(),
                            "That item already exists.",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            } else if (modifyType.equals("edit")) {
                // TODO: Check to see if Sub Title already exists in list
                // if yes, show Toast saying item already exists and ignore
                // if no, continue with edit
                if (!subsDataSource.editSub(subToEdit, newSub)) {
                    Toast.makeText(
                            getActivity().getApplicationContext(),
                            "An item like that already exists.",
                            Toast.LENGTH_SHORT
                    ).show();
                }
                fillList();
            }

            dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void deleteSub() {
        for (int selectedPosition : selectedItems) {
            Sub subToDelete = subsDataSource.getSub(selectedPosition);
            subsDataSource.deleteSub(subToDelete);
        }
        Toast.makeText(
                getActivity().getApplicationContext(),
                "Snippets deleted!",
                Toast.LENGTH_SHORT
        ).show();
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
                        Log.i("TEXTSPANSION", "PATH: " + Environment.getExternalStorageDirectory().toString() + "/Textspansion");
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