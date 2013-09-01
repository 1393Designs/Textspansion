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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ImportExport {
    private static final String TAG = "Textspansion";
    private static final String extStoDir = Environment.getExternalStorageDirectory().toString() + "/Textspansion";
    private static final ObjectMapper mapper = new ObjectMapper();
    static {
        // use the JSON Pretty Printer
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

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

                mapper.writeValue(fileWriter, subs);

                fileWriter.flush();
                fileWriter.close();
                Toast.makeText(context, context.getResources().getString(R.string.subs_saved), Toast.LENGTH_SHORT).show();
            } catch (Exception e) { e.printStackTrace(); }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.subs_cant_save), Toast.LENGTH_SHORT).show();
        }
    }

    public static void importSubs(Uri jsonUri, Context ctx) {
        SubsDataSource subsDataSource = new SubsDataSource(ctx);
        subsDataSource.open();
        List<Sub> importedSubs = new ArrayList();
        InputStream jsonInputStream = null;

        try {
            jsonInputStream = ctx.getContentResolver().openInputStream(jsonUri);
            // Note: TypeReference is used to tell Jackson what the input should be
            importedSubs = mapper.readValue(jsonInputStream, new TypeReference<List<Sub>>() {});
            if(jsonInputStream != null) {
                jsonInputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        subsDataSource.addSubs(importedSubs);

        subsDataSource.close();
    }
}
