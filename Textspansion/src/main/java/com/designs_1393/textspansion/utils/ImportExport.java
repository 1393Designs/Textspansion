package com.designs_1393.textspansion.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.designs_1393.textspansion.R;
import com.designs_1393.textspansion.Sub;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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

    public static void importLegacySubs(Uri xmlUri, Context ctx) {
        SubsDataSource subsDataSource = new SubsDataSource(ctx);
        subsDataSource.open();
        List<Sub> importedSubs = new ArrayList();
        InputStream xmlInputStream = null;
        String subTitleStr, pasteTextStr, privacyStr, encryptionKey = "textspansion";
        Element textspansion;
        NodeList subTitle = null, pasteText = null, privacy = null, textie = null, encryptCheck = null;

        try {
            xmlInputStream = ctx.getContentResolver().openInputStream(xmlUri);
            DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            Document doc = builder.parse(xmlInputStream);
            xmlInputStream.close();
            textspansion = doc.getDocumentElement();
            subTitle = textspansion.getElementsByTagName("Short");
            pasteText = textspansion.getElementsByTagName("Long");
            privacy = textspansion.getElementsByTagName("Private");
            encryptCheck = textspansion.getElementsByTagName("Encrypt");

            if(xmlInputStream != null) {
                xmlInputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(encryptCheck != null && encryptCheck.getLength() == 1){
            Toast.makeText(ctx,
                    "Encrypted import file detected - Decryption is, unfortunately, not supported",
                    Toast.LENGTH_LONG
            ).show();

            subsDataSource.close();
            return;
        }

        int subTitleCount = subTitle.getLength();
        int pasteTextCount = pasteText.getLength();

        if ((subTitleCount <= 0 || pasteTextCount <= 0) || (subTitleCount != pasteTextCount)) {
            Toast.makeText(ctx, "The xml is malformed and can't be imported.", Toast.LENGTH_LONG).show();
        } else{
            for (int i = 0; i < subTitleCount; i++)
            {
                subTitleStr = subTitle.item(i).getFirstChild().getNodeValue();
                pasteTextStr = pasteText.item(i).getFirstChild().getNodeValue();
                privacyStr = privacy.item(i).getFirstChild().getNodeValue();

                if(subTitleStr.compareTo("") == 0)
                    subTitleStr = pasteTextStr;

                importedSubs.add(new Sub(subTitleStr, pasteTextStr, (privacyStr.compareTo("1") == 0)));
            }
        }

        subsDataSource.addSubs(importedSubs);
        subsDataSource.close();
    }
}
