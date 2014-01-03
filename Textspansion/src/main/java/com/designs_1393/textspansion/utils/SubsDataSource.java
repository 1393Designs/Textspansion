package com.designs_1393.textspansion.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

import com.designs_1393.textspansion.Sub;

import java.util.ArrayList;
import java.util.List;

public class SubsDataSource {

    private SharedPreferences sharedPreferences;

    // Database fields
    private SQLiteDatabase database;
    private DbHelper dbHelper;
    private String[] allColumns = {
        DbHelper.KEY_ID,
        DbHelper.KEY_TITLE,
        DbHelper.KEY_PASTE,
        DbHelper.KEY_PRIVATE
    };

    public SubsDataSource(Context context) {
        dbHelper = new DbHelper(context);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public boolean isOpen() {
        return database.isOpen();
    }

    public long addSub(Sub newSub) {
        ContentValues values = new ContentValues();
        values.put(DbHelper.KEY_TITLE, newSub.getSubTitle());
        values.put(DbHelper.KEY_PASTE, newSub.getPasteText());
        values.put(DbHelper.KEY_PRIVATE, newSub.getPrivacy());
        long insertId = database.insert(DbHelper.SUBS_TABLE, null,
                values);

        return insertId;
    }

    public void addSubs(List<Sub> subsToImport) {
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for(Sub newSub : subsToImport) {
                values.clear();
                values.put(DbHelper.KEY_TITLE, newSub.getSubTitle());
                values.put(DbHelper.KEY_PASTE, newSub.getPasteText());
                values.put(DbHelper.KEY_PRIVATE, newSub.getPrivacy());
                database.insert(DbHelper.SUBS_TABLE, null,
                        values);
            }

            database.setTransactionSuccessful();
        } catch(Exception e) {
            Log.w("Textspansion", e);
        } finally {
            database.endTransaction();
        }
    }

    public boolean editSub(Sub oldSub, Sub newSub) {
        String oldTitle = oldSub.getSubTitle();
        String oldPaste = oldSub.getPasteText();
        String whereClause = DbHelper.KEY_PASTE + "='" + oldPaste.replace("'", "''") + "'" + " AND "
                + DbHelper.KEY_TITLE + "='" + oldTitle.replace("'", "''") + "'";

        ContentValues args = new ContentValues();
        args.put(DbHelper.KEY_TITLE, newSub.getSubTitle());
        args.put(DbHelper.KEY_PASTE, newSub.getPasteText());
        args.put(DbHelper.KEY_PRIVATE, newSub.getPrivacy());

        int count = database.query(
                DbHelper.SUBS_TABLE,
                new String[] {DbHelper.KEY_PASTE},
                DbHelper.KEY_TITLE +"=? and " +DbHelper.KEY_PASTE +"=? and " +DbHelper.KEY_PRIVATE +"=?",
                new String[] {newSub.getSubTitle(), newSub.getPasteText(), newSub.getPrivacy()},
                null,
                null,
                DbHelper.KEY_TITLE
        ).getCount();

        if (count != 0)
            return false;
        else
            return database.update(DbHelper.SUBS_TABLE, args, whereClause, null) > 0;

    }

    public void deleteSub(Sub sub) {
        long id = sub.getId();
        database.delete(DbHelper.SUBS_TABLE, DbHelper.KEY_ID
                + " = " + id, null);
    }

    public boolean abandonShip() {
        return database.delete(DbHelper.SUBS_TABLE, null, null) > 0;
    }

    public List<Sub> getAllSubs() {
        List<Sub> subs = new ArrayList<Sub>();
        String sortBy = "";
        if (sharedPreferences.getString("sortie", "DEFAULT").equals("subTitle")) {
            sortBy = DbHelper.KEY_TITLE;
        } else if (sharedPreferences.getString("sortie", "DEFAULT").equals("pasteText")) {
            sortBy = DbHelper.KEY_PASTE;
        }

        Cursor cursor = database.query(DbHelper.SUBS_TABLE,
                allColumns, null, null, null, null, sortBy);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Sub comment = cursorToComment(cursor);
            subs.add(comment);
            cursor.moveToNext();
        }
        cursor.close();
        return subs;
    }

    public Sub getSub(int position) {
        List<Sub> subs = getAllSubs();
        try {
            return subs.get(position);
        } catch (Exception e) {
            return new Sub();
        }
    }

    private Sub cursorToComment(Cursor cursor) {
        Sub comment = new Sub();
        comment.setId(cursor.getLong(0));
        comment.setSubTitle(cursor.getString(1));
        comment.setPasteText(cursor.getString(2));
        comment.setPrivacy(cursor.getString(3));
        return comment;
    }
}