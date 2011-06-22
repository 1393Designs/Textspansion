package com.designatum_1393.textspansion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.app.Activity;
import java.io.BufferedReader;
import java.io.FileReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.FileInputStream;
import java.io.File;
import android.database.Cursor;
import android.content.Intent;
import android.net.Uri;
import java.net.URI;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class importExt extends Activity
{

	private File dbFile = new File("/data/data/com.designatum_1393.textspansion/databases/", "data");
	private subsDbAdapter mDbHelper;
	private Cursor mSubsCursor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.import_view);
		
		Intent intent = getIntent();
		mDbHelper = new subsDbAdapter(this);
		mDbHelper.open();
		importSubs(intent);
		mDbHelper.close();
		startActivity(new Intent(this, textspansion.class));
		finish();
	}
	
	public void importSubs(Intent intent) 
	{		
		try{
			InputStream attachment = getContentResolver().openInputStream(intent.getData());
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
			.newDocumentBuilder();
			Document doc = builder.parse(attachment);
			attachment.close();
			
			Element textspansion = doc.getDocumentElement();
			NodeList shortName = textspansion.getElementsByTagName("Short");
			NodeList longName = textspansion.getElementsByTagName("Long");
			NodeList pvts = textspansion.getElementsByTagName("Private");
			NodeList textie = textspansion.getElementsByTagName("Textspansion");
			
			if (textie.getLength() == 0 || (shortName.getLength() <= 0 || longName.getLength() <= 0) || (shortName.getLength() != longName.getLength())) {
				Toast.makeText(this, "The xml is malformed and can't be imported.", Toast.LENGTH_LONG).show();
			}
			else{
				String shortNameStr, longNameStr, pvt;
				boolean pvtPassIn;
				if(pvts.getLength() > 0)
				{
					for (int i =0; i<shortName.getLength(); i++)
					{
						shortNameStr = shortName.item(i).getFirstChild().getNodeValue();
						longNameStr = longName.item(i).getFirstChild().getNodeValue();
						pvt = pvts.item(i).getFirstChild().getNodeValue();
						
						if(pvt.compareTo("1") == 0)
							pvtPassIn = true;
						else
							pvtPassIn = false;
						
						if(shortNameStr.compareTo("") == 0)
							shortNameStr = longNameStr;
						if (mDbHelper.createSub(shortNameStr, longNameStr, pvtPassIn) == -1)
							Toast.makeText(getApplicationContext(), "There was at least one repeat that was not added", Toast.LENGTH_SHORT).show(); 
					}
				}
				else
				{
					for (int i =0; i<shortName.getLength(); i++)
					{
						shortNameStr = shortName.item(i).getFirstChild().getNodeValue();
						longNameStr = longName.item(i).getFirstChild().getNodeValue();
						
						if(shortNameStr.compareTo("") == 0)
							shortNameStr = longNameStr;
						if (mDbHelper.createSub(shortNameStr, longNameStr, false) == -1)
							Toast.makeText(getApplicationContext(), "There was at least one repeat that was not added", Toast.LENGTH_SHORT).show(); 
					}
				}
					
			}
		}catch(Exception e){
			Toast.makeText(this, "Unable to import this file.", Toast.LENGTH_LONG).show();
		}
	}
	
}
		 
