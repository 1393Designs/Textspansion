package com.designatum_1393.textspansion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.app.Activity;
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

import java.util.Scanner;
import java.io.InputStream;




public class importTxt extends Activity
{

	private File dbFile = new File("/data/data/com.designatum_1393.textspansion/databases/", "data");
	private subsDbAdapter mDbHelper;
	private Cursor mSubsCursor;
	private String importTxt="", shortName, longName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.import_view);
		
		final Intent intent = getIntent();
		mDbHelper = new subsDbAdapter(this);
		mDbHelper.open();
		
		
		//Gets data from the xml file
		try{
			InputStream attachment = getContentResolver().openInputStream(intent.getData());
			Scanner scan = new Scanner(attachment);
			
			while(scan.hasNextLine())
			{
				importTxt += scan.nextLine();
				if(scan.hasNextLine())
					importTxt += "\n";
			}
			int counter=0;
			boolean sN = false, lN = true;
			while(counter < importTxt.length())
			{
				if(sN)
				{
					sN = false;
					lN = true;
					longName = "";
				}
				else if(lN)
				{
					lN = false;
					sN = true;
					shortName = "";
				}
				
				if(importTxt.charAt(counter) == '[')
				{
					counter++;
					while(importTxt.charAt(counter) != ']')
					{
						if(sN)
						{
							if(importTxt.charAt(counter) == '\\' && importTxt.charAt(counter+1) == 'n')
							{
								shortName += "\n";
								counter += 2;
							}
							else
								shortName += importTxt.charAt(counter);
						}
						else
						{
							if(importTxt.charAt(counter) == '\\' && importTxt.charAt(counter+1) == 'n')
							{
								longName += "\n";
								counter += 2;
							}
							else
								longName += importTxt.charAt(counter);
						}
							
						counter++;
					}
				}
				if(lN)
				{
					if (mDbHelper.createSub(shortName, longName, false) == -1)
						Toast.makeText(getApplicationContext(), "There was at least one repeat that was not added", Toast.LENGTH_SHORT).show(); 
				}
				counter++;
			}
		}catch(Exception e){}
		
		startActivity(new Intent(this, textspansion.class));
		finish();
	}
}