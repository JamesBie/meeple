package com.james.android.meepleaid;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

import static android.R.attr.id;

/**
 * Created by 100599223 on 8/10/2017.
 */

public class BGProvider extends ContentProvider {
public static final String LOG_TAG = BGProvider.class.getSimpleName();

    /**
     * initialize the provider and the database helper object
     */

    //URI matcher code for the content URI for the pets table
    private static final int BG = 100;
    private static final int BG_ID = 101;
   /*
    uriMatcher object to match a content URI to a corresponding code.
    The input passed into the constructor represents the code
    to return for the root URI.
    it's common to use NO_MATCH as the input for this case
     */

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //static initializer, this is run the first time anythign is called form this class
    static {
        //the calls to addURI() go here, for al of the content URI patterns that the provider should recognize
        //all paths added to the UriMatcher have a corresponding code to return if a match is found.

        //the content URI of the form "content://com.james.android.boardgames/boardgames" will map to the interger cod (@link#bg).
        //this uri is used to provide access to multiple rows of the boardgame table
        sUriMatcher.addURI(BGContract.CONTENT_AUTHORITY, BGContract.PATH_BG, BG);
    }

    /** database helper object
     *
     */

        private BGDBHelper mDbHelper;

    @Override
    public boolean onCreate(){
        mDbHelper = new BGDBHelper(getContext());
        return true;
    }

    /**
     * perform the query for the given URI. use the given projection, selection
     * selection arguments and sort order
     */

    @Override
    public Cursor query (Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch(match) {
            case BG:
                //for the bg code, query the bg table directly with the given
                //projection, selection, selection arguments and sort order
                //the cursor could contain multiple rows of the bg table
                cursor = database.query(BGContract.BGEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BG_ID:
                selection = BGContract.BGEntry._ID+"=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query((BGContract.BGEntry.TABLE_NAME), projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("cannot query unknown "+ uri);
        }
        return cursor;
    }
    public Uri insertBGfromXML (Uri uri, ArrayList<BoardGame> boardgames){
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int size = boardgames.size();
        try{
            for (int i = 0; i <size; ++i){
                ContentValues cv = new ContentValues();
                cv.put(BGContract.BGEntry.COLUMN_BG_NAME,boardgames.get(i).getTitle());
                cv.put(BGContract.BGEntry.COLUMN_BG_MINPLAYER,boardgames.get(i).getMinPlayer());
                cv.put(BGContract.BGEntry.COLUMN_BG_MAXPLAYER, boardgames.get(i).getmMaxPlayer());
                database.insertOrThrow(BGContract.BGEntry.TABLE_NAME,null,cv);
                Log.i("insert", cv.keySet() + cv.valueSet().toString());
            }

            database.close();
        } catch (Exception e) {
            Log.e("insertbgfromxml"," exception", e);
        }
        return ContentUris.withAppendedId(uri,id);
    }

    public Uri insert(Uri uri, ContentValues contentValues){
        final int match = sUriMatcher.match(uri);
        switch (match){
            case BG:

                return insertBg(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for "+ uri);

        }

    }

    private Uri insertBg (Uri uri , ContentValues values){
        //todo
        //
        //
        // sanity check

        String name = values.getAsString(BGContract.BGEntry.COLUMN_BG_NAME);

        if( name == null) {
            throw new IllegalArgumentException("BG requires a name");
        }

        Integer min = values.getAsInteger(BGContract.BGEntry.COLUMN_BG_MINPLAYER);
        Integer max = values.getAsInteger(BGContract.BGEntry.COLUMN_BG_MAXPLAYER);
        if ((min!= null && min <0)||(max!=null && max <0)){
            throw new IllegalArgumentException("BG player count cant be less than 0");

        }

        //get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //insert the new BG with given values
        long id = database.insert(BGContract.BGEntry.TABLE_NAME,null,values);
        // if the id is -1 then the insertion failed and we should log an error and return null

        if (id ==-1){
            Log.e(LOG_TAG,"Failed to insert row for " +uri);
            return null;
        }
        //return the new uri with the id( of the newly insreted row ) append
        return ContentUris.withAppendedId(uri,id);
    }

    /**
     * update the data at the given selection and selection arguments, with the new contentvalue
     */

    public int update (Uri uri, ContentValues contentValues, String selection, String[] selectionArgs){

        final int match = sUriMatcher.match(uri);
        switch (match) {

            case BG:
                return updateBG(uri, contentValues, selection, selectionArgs);
            case BG_ID:
                // for the bg_id extract out the id from the uri
                //so we know which row to update. selection will be "_id=?" and
                //slection arguments will be a string array containing hte actual id

                selection = BGContract.BGEntry._ID+"=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateBG(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);

        }
    }

    /**
     * update pets in the database with given content values. apply the changes to the rows
     * specified in the selection and selection arguments( which could be 0 or 1 or more bg's
     * return the number of rows that were successfully updated
     */

    private int updateBG (Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        //if the {@link Entry#COLOMUN_PET_NAME} key is present,
        //check tat the name value is not null.

        if(values.containsKey(BGContract.BGEntry.COLUMN_BG_NAME)){
            String name = values.getAsString(BGContract.BGEntry.COLUMN_BG_NAME);
            if (name == null){
                throw new IllegalArgumentException("BG requires a name");
            }
        }

        if (values.containsKey(BGContract.BGEntry.COLUMN_BG_MINPLAYER)){
            //check # is greater than 0
            Integer min = values.getAsInteger(BGContract.BGEntry.COLUMN_BG_MINPLAYER);
            if (min != null && min < 0){
                throw new IllegalArgumentException("BG require valid mine player number");
            }
        }

        if (values.containsKey(BGContract.BGEntry.COLUMN_BG_MAXPLAYER)){
            //check # is greater than 0
            Integer max = values.getAsInteger(BGContract.BGEntry.COLUMN_BG_MAXPLAYER);
            if (max != null && max < 0){
                throw new IllegalArgumentException("BG require valid max player number");
            }
        }

        //if theres no values to update then we dont try to update
        if (values.size() ==0){
            return 0;
        }

        //otherwise, get writable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //return the number of database rows affected by the update statement
        return database.update(BGContract.BGEntry.TABLE_NAME, values, selection, selectionArgs);

    }
    @Override
    public int delete(Uri uri, String selection , String[] selectionArgs){
        //get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);

        switch(match) {
            case BG:
                //Delete all rows that match selection and selection args
                return database.delete(BGContract.BGEntry.TABLE_NAME, selection, selectionArgs);
            case BG_ID:
                //selecte a single row given by the id in the URI
                selection = BGContract.BGEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return database.delete(BGContract.BGEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("deletion not supported for " + uri);

        }
    }

    /**
     * return the MIME type of data for the content URI
     */
    @Override
public String getType(Uri uri){
    final int match = sUriMatcher.match(uri);
        switch (match) {
            case BG:
                return BGContract.BGEntry.CONTENT_LIST_TYPE;
            case BG_ID:
                return BGContract.BGEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
    }
}



}
