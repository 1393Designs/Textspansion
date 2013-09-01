package com.designs_1393.textspansion.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.os.Environment;
import android.widget.Toast;

import com.designs_1393.textspansion.R;
import com.designs_1393.textspansion.Sub;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ImportExport {
    private static final String TAG = "Textspansion";
    private static final String extStoDir = Environment.getExternalStorageDirectory().toString() + "/Textspansion";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void exportSubs(SubsDataSource subsDataSource, Context context) {
        File root = new File(extStoDir);
        if(!root.exists())
            root.mkdirs();

        if(root.canWrite()){
            File fileToWrite = new File(extStoDir, "subs.json");
            ArrayList<Sub> subs = (ArrayList) subsDataSource.getAllSubs();
            FileWriter fileWriter;
            try {
                fileWriter = new FileWriter(fileToWrite);

                String allSubsAsJson = mapper.writeValueAsString(subs);
                Log.i(TAG, allSubsAsJson);

                fileWriter.write(allSubsAsJson);
                fileWriter.flush();
                fileWriter.close();
                Toast.makeText(context, context.getResources().getString(R.string.subs_saved), Toast.LENGTH_SHORT).show();
            } catch (Exception e) { e.printStackTrace(); }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.subs_cant_save), Toast.LENGTH_SHORT).show();
        }
    }

    public static void importSubs(Uri jsonUri) {
        ObjectMapper mapper = new ObjectMapper();

    }
}
