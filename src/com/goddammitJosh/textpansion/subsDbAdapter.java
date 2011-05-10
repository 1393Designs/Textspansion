package com.goddammitJosh.textpansion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Simple substitutions database access helper class. Defines the basic CRUD
 * operations for textspansion's substituions, and gives the ability to list
 * all subs as well as retrieve or modify a specific substitution.
 * 
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 */
public class subsDbAdapter 
{

    public static final String KEY_ABBR = "abbr"; // short name of the sub
    public static final String KEY_FULL = "full"; // full sub
    public static final String KEY_ROWID = "_id"; // row's id

    private static final String TAG = "Textspansion: subsDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
        "create table subs (_id integer primary key autoincrement, "
        + "abbr text not null, full text not null);";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "subs";
    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;

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
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS subs");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public subsDbAdapter(Context ctx)
    {
        this.mCtx = ctx;
    }

    /**
     * Open the subs database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public subsDbAdapter open() throws SQLException
    {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close()
    {
        mDbHelper.close();
    }


    /**
     * Create a new substitution using the abbreviated and full names provided.
     * If the substitution is successfully created return the new rowId for
     * that sub, otherwise return a -1 to indicate failure.
     * 
     * @param abbr the abbreviated version of the substitution
     * @param full the full version of the substitution
     * @return rowId or -1 if failed
     */
    public long createSub(String abbr, String full)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ABBR, abbr);
        initialValues.put(KEY_FULL, full);
        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the sub with the given rowId
     * 
     * @param rowId id of sub to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteSub(long rowId)
    {
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all substitutions in the database
     * 
     * @return Cursor over all subs
     */
    public Cursor fetchAllSubs()
    {
        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_ABBR,
                KEY_FULL}, null, null, null, null, KEY_ABBR);
    }

    /**
     * Return a Cursor positioned at the substitution that matches the given rowId
     * 
     * @param rowId id of sub to retrieve
     * @return Cursor positioned to matching sub, if found
     * @throws SQLException if sub could not be found/retrieved
     */
    public Cursor fetchSub(long rowId) throws SQLException
    {

        Cursor mCursor =

            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                    KEY_ABBR, KEY_FULL}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null)
        {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Update the sub using the details provided. The sub to be updated is
     * specified using the rowId, and it is altered to use the abbreviated and
     * full values passed in
     * 
     * @param rowId id of sub to update
     * @param abbr value to which the sub's abbreviation will be set
     * @param full value to which the sub's full text will be set
     * @return true if the sub was successfully updated, false otherwise
     */
    public boolean updateSub(long rowId, String abbr, String full)
    {
        ContentValues args = new ContentValues();
		//deleteSub(rowId);
        args.put(KEY_ABBR, abbr);
        args.put(KEY_FULL, full);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
	
}
