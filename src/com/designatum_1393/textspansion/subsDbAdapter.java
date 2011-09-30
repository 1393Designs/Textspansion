package com.designatum_1393.textspansion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class subsDbAdapter 
{

	public static final String KEY_ABBR = "abbr"; // short name of the sub
	public static final String KEY_FULL = "full"; // full sub
	public static final String KEY_ROWID = "_id"; // row's id
	public static final String KEY_PRIVATE = "_pvt"; // private status of the sub
	public static final String KEY_CLIP = "clip";
	public static final String KEY_DATE = "date";
		// uses binary logic (active high) in an int for this

	private static final String TAG = "Textspansion: subsDbAdapter";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	private static final String DATABASE_CREATE =
		"create table subs (_id integer primary key autoincrement, "
		+ "abbr text not null, full text not null, _pvt text not null);";

	private static final String DATABASE_NAME = "data";
	private static final String SUBS_TABLE = "subs";
	private static final String CLIPS_TABLE = "clips";
	private static final int DATABASE_VERSION = 4;
	//Version 1.0 was Database_version 2
	//Version 1.1 is Database_version 3
	//Version 1.2 is Database_version 4
	private final Context mCtx;

	/**
	 * Class constructor.  Retains calling application's context, so that it
	 * can be used in additional functions.
	 * 
	 * @param ctx	Calling application's context
	 */
	public subsDbAdapter(Context ctx)
	{
		this.mCtx = ctx;

	}

	/**
	 * Inner class providing a database upgrade process.
	 */	
	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		DatabaseHelper(Context context) 
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
			db.execSQL(DATABASE_CREATE);
			db.execSQL("CREATE TABLE clips (_id integer primary key autoincrement, "
				+ "date text not null, clip text not null);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			if (oldVersion == 2)
			{
				db.execSQL("ALTER TABLE subs ADD COLUMN _pvt text null");
				ContentValues args = new ContentValues();
				args.put(KEY_PRIVATE, "0");
				db.update(SUBS_TABLE, args, null, null);
			}
			db.execSQL("CREATE TABLE clips (_id integer primary key autoincrement, "
				+ "date text not null, clip text not null);");
		}
	}

	/**
	 * Injects the tutorial substitions into the list of items.  This is called
	 * on first launch, or when the user clicks "Tutorial" from the settings
	 * menu.
	 */
	public void addTutorial()
	{
		ContentValues steps = new ContentValues();
		String shortName, longName;

		shortName = "1) Welcome!";
		longName = "- Welcome to Textspansion, the first rapid text-insertion app for Android!";

		steps.put(KEY_ABBR, shortName);
		steps.put(KEY_FULL, longName);
		steps.put(KEY_PRIVATE, 0);

		if (mDb.query(SUBS_TABLE, new String[] {KEY_FULL}, KEY_ABBR +"=? and " +KEY_FULL +"=?", new String[] {shortName, longName}, null, null, KEY_ABBR).getCount() == 0)			  
			mDb.insert(SUBS_TABLE, null, steps);

		shortName = "2) How To Use";
		longName = "- You can access the app by long-pressing the device's Search button\n\n" + 
			"- Simply click on any of these entries - the text in the bottom half of the box will be copied to your clipboard.\n\n" +
			"- Simply paste it where ever you want it!";

		steps.put(KEY_ABBR, shortName);
		steps.put(KEY_FULL, longName);
		steps.put(KEY_PRIVATE, 0);

		if (mDb.query(SUBS_TABLE, new String[] {KEY_FULL}, KEY_ABBR +"=? and " +KEY_FULL +"=?", new String[] {shortName, longName}, null, null, KEY_ABBR).getCount() == 0)			  
			mDb.insert(SUBS_TABLE, null, steps);

		shortName = "3) Adding";
		longName = "- Click your device's menu key, then \"Add!\"\n" + 
			"- Long-pressing on an item to edit or delete it.\n\n" + 
			"- You can delete multiple items at the same time by clicking the device's menu button and select multi-delete.";

		steps.put(KEY_ABBR, shortName);
		steps.put(KEY_FULL, longName);
		steps.put(KEY_PRIVATE, 0);

		if (mDb.query(SUBS_TABLE, new String[] {KEY_FULL}, KEY_ABBR +"=? and " +KEY_FULL +"=?", new String[] {shortName, longName}, null, null, KEY_ABBR).getCount() == 0)			  
			mDb.insert(SUBS_TABLE, null, steps);

		shortName = "4) Data Management";
		longName = "- You can export all of your substitutions by selecting \"Export\" in the menu. The exported file will be located in '/sdcard/Textspansion/' \n\n" +
			"- To import that data you can either email that file to yourself and open the file that way or locate the file from your phone and open it directly.";

		steps.put(KEY_ABBR, shortName);
		steps.put(KEY_FULL, longName);
		steps.put(KEY_PRIVATE, 0);

		if (mDb.query(SUBS_TABLE, new String[] {KEY_FULL}, KEY_ABBR +"=? and " +KEY_FULL +"=?", new String[] {shortName, longName}, null, null, KEY_ABBR).getCount() == 0)			  
			mDb.insert(SUBS_TABLE, null, steps);

		shortName = "5) Get Started!";
		longName = "- The tutorial is accessible via the settings panel. Happy Textspanding!";

		steps.put(KEY_ABBR, shortName);
		steps.put(KEY_FULL, longName);
		steps.put(KEY_PRIVATE, 0);

		if (mDb.query(SUBS_TABLE, new String[] {KEY_FULL}, KEY_ABBR +"=? and " +KEY_FULL +"=?", new String[] {shortName, longName}, null, null, KEY_ABBR).getCount() == 0)			  
			mDb.insert(SUBS_TABLE, null, steps);

	}

	/**
	 * Opens the database helper for writing and returns the database adapter.
	 *
	 * @return	Database adapter associated with the database.
	 */
	public subsDbAdapter open() throws SQLException
	{
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	/**
	 * Closes the database helper.
	 */
	public void close()
	{
		mDbHelper.close();
	}

	/**
	 * Creates a new entry in the database.
	 *
	 * @param abbr	Short name of the substitution
	 * @param full	Long name of the substitution
	 * @param pvt	String representation of the substitution's private state.
	 * 				"1" represents true; "0" represents false.
	 * @return		The row ID of the newly inserted row, or -1 if an error occurred
	 */
	public long createSub(String abbr, String full, boolean pvt)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_ABBR, abbr);
		initialValues.put(KEY_FULL, full);
		String pvtS;

		if (pvt)
		{
			pvtS = "1";
			initialValues.put(KEY_PRIVATE, "1");
		}
		else
		{
			pvtS = "0";
			initialValues.put(KEY_PRIVATE, "0");
		}

		if (mDb.query(SUBS_TABLE, new String[] {KEY_FULL}, KEY_ABBR +"=? and " +KEY_FULL +"=? and "+KEY_PRIVATE+"=?", new String[] {abbr, full, pvtS}, null, null, KEY_ABBR).getCount() != 0)	
		{
			return -1;
		}
		else
			return mDb.insert(SUBS_TABLE, null, initialValues);
	}

	/**
	 * Deletes a specific element in the database.
	 *
	 * @param oldFull	Long name of the substitution being deleted
	 * @param oldAbbr	Short name of the substitution being deleted
	 * @param oldPvt	String representation of the private state for the
	 * 					substitution being deleted
	 * @return			True if any items are deleted, false otherwise
	 */
	public boolean deleteSub(String oldFull, String oldAbbr, String oldPvt)
	{
		String whereClause = KEY_FULL +"='" +oldFull.replace("'", "''") +"'" +" AND "
					+KEY_ABBR +"='" +oldAbbr.replace("'", "''") +"'" + " AND " + KEY_PRIVATE + "='" + oldPvt.replace("'", "''") +"'";

		return mDb.delete(SUBS_TABLE, whereClause, null) > 0;
	}

	/**
	 * Deletes all elements in the database, but does not delete the database itself.
	 *
	 * @return	True if any items are deleted, false otherwise.
	 */	
	public boolean abandonShip()
	{
		return mDb.delete(SUBS_TABLE, null, null) > 0;
	}

	/**
	 * Returns a cursor containing every element of the database.  
	 *
	 * @param sortByShort	whether to sort by sort or not
	 *
	 * @return				Cursor containing the row ID, short name, long
	 *						name, and private state for each substitution in
	 *						the database.
	 */
	public Cursor fetchAllSubs(boolean sortByShort)
	{
		if(sortByShort)
			return mDb.query(SUBS_TABLE, new String[] {KEY_ROWID, KEY_ABBR,
				KEY_FULL, KEY_PRIVATE}, null, null, null, null, KEY_ABBR);
		else
			return mDb.query(SUBS_TABLE, new String[] {KEY_ROWID, KEY_ABBR,
				KEY_FULL, KEY_PRIVATE}, null, null, null, null, KEY_FULL);
	}

	/**
	 * Gets a cursor contaning the element at a certain row ID.
	 *
	 * @param rowId		Row in the database containing the desired element.
	 */
	public Cursor fetchSub(long rowId) throws SQLException
	{

		Cursor mCursor =

			mDb.query(true, SUBS_TABLE, new String[] {KEY_ROWID,
					KEY_ABBR, KEY_FULL, KEY_PRIVATE}, KEY_ROWID + "=" + rowId, null,
					null, null, null, null);
		if (mCursor != null)
		{
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	/**
	 * Updates updates an alement in the database.
	 *
	 * @param oldFull	Previous long name
	 * @param oldAbbr	Previous short name
	 * @param oldPvt	Previous private state (boolean representation)
	 * @param abbr		New short name
	 * @param full		New long name
	 * @param pvt		New private state (boolean representation)
	 *
	 * @return 			True if at least one element was modified,
	 * 					false if none were changed, and false no element
	 * 					containing oldFull, oldAbbr, and oldPvt exists in the
	 * 					database.
	 */
	public boolean updateSub(String oldFull, String oldAbbr, boolean oldPvt, String abbr, String full, boolean pvt)
	{
		String whereClause = KEY_FULL +"='" +oldFull.replace("'", "''") +"'" +" AND "
					+KEY_ABBR +"='" +oldAbbr.replace("'", "''") +"'";

		ContentValues args = new ContentValues();
		args.put(KEY_ABBR, abbr);
		args.put(KEY_FULL, full);
		if ( pvt )
			args.put(KEY_PRIVATE, "1");
		else
			args.put(KEY_PRIVATE, "0");
		if (mDb.query(SUBS_TABLE, new String[] {KEY_FULL}, KEY_ABBR +"=? and " +KEY_FULL +"=? and " +KEY_PRIVATE +"=?", new String[] {abbr, full, pvt?"1":"0"}, null, null, KEY_ABBR).getCount() != 0)
		{
			return false;
		}
		else
			return mDb.update(SUBS_TABLE, args, whereClause, null) > 0;
	}
	
	/* ###############################################################################################
	 * ####################################### clipboard stuff #######################################
	 * ###############################################################################################
	 */

	/**
	 * Returns a cursor containing every element of the database.
	 *
	 * @param sortByDate	whether to sort by date or text
	 *
	 * @return				Cursor containing the row ID, date, and clipped text
	 *						for each item in the clipboard history's table.
	 */
	public Cursor fetchAllClips(boolean sortByDate)
	{
		// NOTE: statically defined to sort by date for now.
		return mDb.query(CLIPS_TABLE, new String[] {KEY_ROWID, KEY_DATE,
			KEY_CLIP}, null, null, null, null, KEY_DATE);
/*		if(sortByDate)
			return mDb.query(CLIPS_TABLE, new String[] {KEY_DATE, KEY_CLIP},
				null, null, null, null, KEY_DATE);
		else
			return mDb.query(SUBS_TABLE, new String[] {KEY_DATE, KEY_CLIP},
				null, null, null, null, KEY_CLIP);*/
	}


	/**
	 * Creates a new clipboard entry in the appropriate table.
	 *
	 * @param date	Time to associate with this clip, in the form
	 * 				TODO: Determine format
	 * @param clip	Text taken from the clipboard
	 * @return		The row ID of the newly inserted row, or -1 if an error occurred
	 */
	public long createClip(String date, String clip)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_DATE, date);
		initialValues.put(KEY_CLIP, clip);

		if (mDb.query(SUBS_TABLE, new String[] {KEY_FULL}, KEY_FULL +"=?", new String[] {clip}, null, null, KEY_FULL).getCount() == 0)
		{
			if (mDb.query(CLIPS_TABLE, new String[] {KEY_CLIP}, KEY_CLIP +"=?", new String[] {clip}, null, null, KEY_DATE).getCount() == 0)
			{
				return mDb.insert(CLIPS_TABLE, null, initialValues);
			}
		}
		return -1;
	}


	/**
	 * Deletes a specific element from the clipboard table.
	 *
	 * @param oldClip	Clipped text of the item being deleted
	 * @return			True if any items are deleted, false otherwise
	 */
	public boolean deleteClip(String oldClip)
	{
		String whereClause = KEY_CLIP +"='" +oldClip.replace("'", "''") +"'";

		return mDb.delete(CLIPS_TABLE, whereClause, null) > 0;
	}
}