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

//For encrypt/decrypt
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.app.Dialog;
import android.view.View.OnClickListener;
import android.view.View;
//import android.content.DialogInterface;

public class importExt extends Activity
{

	private File dbFile = new File("/data/data/com.designatum_1393.textspansion/databases/", "data");
	private subsDbAdapter mDbHelper;
	private Cursor mSubsCursor;
	private Element textspansion;
	private NodeList shortName, longName, pvts, textie, encryptCheck;
	
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
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
			.newDocumentBuilder();
			Document doc = builder.parse(attachment);
			attachment.close();
			textspansion = doc.getDocumentElement();
			shortName = textspansion.getElementsByTagName("Short");
			longName = textspansion.getElementsByTagName("Long");
			pvts = textspansion.getElementsByTagName("Private");
			textie = textspansion.getElementsByTagName("Textspansion");
			encryptCheck = textspansion.getElementsByTagName("Encrypt");
		}catch(Exception e){}
		
		//Will detect if the xml file is encrypted. If so, prompts user for the key.
		if(encryptCheck.getLength() == 1){
			final Dialog dialog = new Dialog(importExt.this);
			dialog.setContentView(R.menu.decryptdialog);
			dialog.setTitle("Decrypting...");
			
			TextView key_text = (TextView) dialog.findViewById(R.id.key_label);
			final EditText key_input = (EditText) dialog.findViewById(R.id.key_entry);
			
			Button okay_button = (Button) dialog.findViewById(R.id.okayButton);
			okay_button.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					String key_name = key_input.getText().toString();
					if(key_name.compareTo("") == 0)
						key_name = "textspansion";
					
					importSubs(intent, key_name);
					mDbHelper.close();
					startActivity(new Intent(getApplicationContext(), textspansion.class));
					finish();
					dialog.dismiss();
					
				}
			});
			dialog.show();
		}
		else
			importSubs(intent, "textspansion");
	}
	
	public void importSubs(Intent intent, String key) 
	{
		String userKey = key;			
		//First checks to see if the xml file is malformed
		if ((shortName.getLength() <= 0 || longName.getLength() <= 0) || (shortName.getLength() != longName.getLength())) {
			Toast.makeText(this, "The xml is malformed and can't be imported.", Toast.LENGTH_LONG).show();
		}
		else{
			boolean decryptText = false;
			
			if(encryptCheck.getLength() == 1)
				decryptText = true;
				
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
					
					if(decryptText)
					{
						try{
							shortNameStr = decrypt(userKey, shortNameStr);
							longNameStr = decrypt(userKey, longNameStr);
						}
						catch(Exception e)
						{}
					}
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
						
					if(decryptText)
					{
						try{
							shortNameStr = decrypt("textspansion", shortNameStr);
							longNameStr = decrypt("textspansion", longNameStr);
						}
						catch(Exception e)
						{}
					}
					
					if (mDbHelper.createSub(shortNameStr, longNameStr, false) == -1)
						Toast.makeText(getApplicationContext(), "There was at least one repeat that was not added", Toast.LENGTH_SHORT).show(); 
				}
			}
				
		}
		mDbHelper.close();
		startActivity(new Intent(this, textspansion.class));
		finish();
	}
	
	// Following code taken from: http://www.androidsnippets.com/encryptdecrypt-strings	
	public static String decrypt(String seed, String encrypted) throws Exception 
	{
		byte[] rawKey = getRawKey(seed.getBytes());
		byte[] enc = toByte(encrypted);
		byte[] result = decrypt(rawKey, enc);
		return new String(result);
	}
	
	private static byte[] getRawKey(byte[] seed) throws Exception 
	{
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		sr.setSeed(seed);
		kgen.init(128, sr); 
		SecretKey skey = kgen.generateKey();
		byte[] raw = skey.getEncoded();
		return raw;
	}
	
	private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		byte[] decrypted = cipher.doFinal(encrypted);
		return decrypted;
	}
	
	public static String toHex(String txt) 
	{
		return toHex(txt.getBytes());
	}
	
	public static String fromHex(String hex) {
		return new String(toByte(hex));
	}
	
	public static String toHex(byte[] buf) 
	{
		if (buf == null)
			return "";
		StringBuffer result = new StringBuffer(2*buf.length);
		for (int i = 0; i < buf.length; i++) {
				appendHex(result, buf[i]);
		}
		return result.toString();
	}
	
	public static byte[] toByte(String hexString) {
		int len = hexString.length()/2;
		byte[] result = new byte[len];
		for (int i = 0; i < len; i++)
			result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
		return result;
	}
	
	private final static String HEX = "0123456789ABCDEF";
	
	private static void appendHex(StringBuffer sb, byte b) 
	{
		sb.append(HEX.charAt((b>>4)&0x0f)).append(HEX.charAt(b&0x0f));
	}
	
	//End code snippet
}