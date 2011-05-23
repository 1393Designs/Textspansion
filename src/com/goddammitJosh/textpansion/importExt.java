package com.goddammitJosh.textpansion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.app.Activity;
import android.util.Log;
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


public class importExt extends Activity
{

	private File dbFile = new File("/data/data/com.goddammitJosh.textpansion/databases/", "data");
	private subsDbAdapter mDbHelper;
    private Cursor mSubsCursor;
	
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.import_view);
		
		Intent intent = getIntent();
		Uri dataUri = intent.getData();
		String uriTemp = dataUri.toString();
		URI dataURI = null;
		try{
			dataURI = new URI(uriTemp);
		}catch(Exception e){
			Log.i("ImportExt", "Wuh oh! " + e.getMessage());
		}
		
		mDbHelper = new subsDbAdapter(this);
		mDbHelper.open();
		
		importSubs(dataURI);
		
		mDbHelper.close();
		
		startActivity(new Intent(this, subsList.class));
		
		finish();
	}
	
	public void importSubs(URI dataURI) 
	{		
		try{
			File inTXT = new File(dataURI);
			if(inTXT.exists())
			{
				FileInputStream fio = new FileInputStream(inTXT);
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				factory.setNamespaceAware(true);
				XmlPullParser xpp = factory.newPullParser();
				
				String shortName, longName;
				
				xpp.setInput(fio, null);
				int eventType = xpp.getEventType();
				eventType = xpp.next();
				//eventType = xpp.next();
				
				if(xpp.getName().equals("Textspansion")) 
				{
					eventType = xpp.next();
					while (eventType != XmlPullParser.END_DOCUMENT) {
						if(eventType == XmlPullParser.START_TAG && xpp.getName().equals("Short")) 
						{
							Log.i("IMPORT", "Start tag "+xpp.getName());
							//Parses to value of short Tag
							eventType = xpp.next();
							Log.i("IMPORT", "Short Name Text: "+xpp.getText());
							shortName = xpp.getText();
							//Parses to END_TAG
							eventType = xpp.next();
							//Parses to START_TAG
							eventType = xpp.next();
							Log.i("IMPORT", "Start Tag: "+xpp.getName());
							//Parses to value of long Tag
							eventType = xpp.next();
							Log.i("IMPORT", "Long Name Text: "+xpp.getText());
							longName = xpp.getText();
							//Parses to END_TAG
							eventType = xpp.next();
							
							if(shortName.compareTo("") == 0)
								shortName = longName;
							if (mDbHelper.createSub(shortName, longName) == -1)
								Toast.makeText(getApplicationContext(), "There was at least one repeat that was not added", Toast.LENGTH_SHORT).show(); 
						}
						eventType = xpp.next();
					}
				}
				else
					Toast.makeText(getApplicationContext(), "File is not compatible with Textspansion", Toast.LENGTH_SHORT).show(); 
			}
		}catch(Exception e){
			Log.i("IMPORTEXT", "Could not parse file :" + e.getMessage());
		}
	}
	
}
		 