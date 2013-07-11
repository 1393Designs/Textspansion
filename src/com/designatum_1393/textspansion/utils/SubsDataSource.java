package com.designatum_1393.textspansion.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.designatum_1393.textspansion.Sub;

import java.util.ArrayList;
import java.util.List;

public class SubsDataSource {

    // Database fields
    private SQLiteDatabase database;
    private DbHelper dbHelper;
    private String[] allColumns = { DbHelper.KEY_ID,
        DbHelper.KEY_FULL,
        DbHelper.KEY_ABBR,
        DbHelper.KEY_PRIVATE
    };

    public SubsDataSource(Context context) {
        dbHelper = new DbHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Sub createSub(String abbrText, String fullText, String privacy) {
        ContentValues values = new ContentValues();
        values.put(DbHelper.KEY_ABBR, abbrText);
        values.put(DbHelper.KEY_FULL, fullText);
        values.put(DbHelper.KEY_PRIVATE, privacy);
        long insertId = database.insert(DbHelper.SUBS_TABLE, null,
                values);
        Cursor cursor = database.query(DbHelper.SUBS_TABLE,
                allColumns, DbHelper.KEY_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Sub newComment = cursorToComment(cursor);
        cursor.close();
        return newComment;
    }

    public void deleteSub(Sub sub) {
        long id = sub.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(DbHelper.SUBS_TABLE, DbHelper.KEY_ID
                + " = " + id, null);
    }

    public List<Sub> getAllSubs() {
        List<Sub> subs = new ArrayList<Sub>();

        Cursor cursor = database.query(DbHelper.SUBS_TABLE,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Sub comment = cursorToComment(cursor);
            subs.add(comment);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return subs;
    }

    private Sub cursorToComment(Cursor cursor) {
        Sub comment = new Sub();
        comment.setId(cursor.getLong(0));
        comment.setAbbrText(cursor.getString(1));
        comment.setFullText(cursor.getString(2));
        comment.setPrivacy(cursor.getString(3));
        return comment;
    }
}