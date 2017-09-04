package com.james.android.meepleaid;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by 100599223 on 8/10/2017.
 */

public class BGDBHelper extends SQLiteOpenHelper {
/*
name of the database file
 */

    private static final String DATABASE_NAME = "kallax.db";
    /*
    database version. If you change the database schema you must incremend the version

     */
    private static final int DATABASE_VERSION = 1;

    public BGDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_BG_TABLE = "CREATE TABLE " + BGContract.BGEntry.TABLE_NAME + " ( " + BGContract.BGEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BGContract.BGEntry.COLUMN_BG_NAME + " TEXT NOT NULL,"
                + BGContract.BGEntry.COLUMN_BG_MINPLAYER + " TEXT NOT NULL, "
                + BGContract.BGEntry.COLUMN_BG_MAXPLAYER + " TEXT NOT NULL);";
        Log.i("database name", SQL_CREATE_BG_TABLE);

        //Execute the SQL statement
        db.execSQL(SQL_CREATE_BG_TABLE);
    }
            /*
                this is called when the database needs to be upgraded
            */
            public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion){

                //database is still at version 1 so nothing to be done here
                // TODO: 8/10/2017
            }
}
