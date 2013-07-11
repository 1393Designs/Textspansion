package com.designatum_1393.textspansion.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {

    public static final String SUBS_TABLE = "subs";
    public static final String KEY_ID = "_id";
    public static final String KEY_FULL = "full";
    public static final String KEY_ABBR = "abbr";
    public static final String KEY_PRIVATE = "_pvt";

    private static final String DATABASE_NAME = "subs.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + SUBS_TABLE + "(" + KEY_ID
            + " integer primary key autoincrement, "
            + KEY_ABBR + " text not null, + "
            + KEY_FULL + " text not null, + "
            + KEY_PRIVATE + " text not null);";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DbHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + SUBS_TABLE);
        onCreate(db);
    }

}