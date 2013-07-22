package com.designatum_1393.textspansion.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.designatum_1393.textspansion.R;
import com.designatum_1393.textspansion.Sub;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class ImportExport {
    private static final String extStoDir = Environment.getExternalStorageDirectory().toString() + "/Textspansion";

    public static void exportSubs(SubsDataSource subsDataSource, Context context) {
        File root = new File(extStoDir);
        if(!root.exists())
            root.mkdirs();

        if(root.canWrite()){
            File fileToWrite = new File(extStoDir, "subs.json");
            ArrayList<Sub> subs = (ArrayList) subsDataSource.getAllSubs();
            JSONArray subsJsonArray = new JSONArray();
            FileWriter fileWriter;
            try {
                fileWriter = new FileWriter(fileToWrite);

                for (Sub sub : subs) {
                    JSONObject newSubToAdd = new JSONObject();
                    newSubToAdd.put("SubTitle", sub.getSubTitle());
                    newSubToAdd.put("PasteText", sub.getPasteText());
                    newSubToAdd.put("Privacy", sub.isPrivate());
                    subsJsonArray.put(newSubToAdd);
                }
                fileWriter.write(subsJsonArray.toString());
                fileWriter.flush();
                fileWriter.close();
                Toast.makeText(context, context.getResources().getString(R.string.subs_saved), Toast.LENGTH_SHORT).show();
            } catch (Exception e) { e.printStackTrace(); }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.subs_cant_save), Toast.LENGTH_SHORT).show();
        }
    }
}
