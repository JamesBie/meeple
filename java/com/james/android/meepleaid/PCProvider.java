package com.james.android.meepleaid;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by 100599223 on 8/14/2017.
 */

public class PCProvider extends ContentProvider {
    public static final String LOG_TAG = PCProvider.class.getSimpleName();

    /**
     * initialize the provider and the database helper object
     */

    //URI matcher code for the content URI for the pets table
    private static final int PC = 102;
    private static final int PC_ID = 103;
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
        sUriMatcher.addURI(PCContract.CONTENT_AUTHORITY, PCContract.PATH_PC, PC);
        sUriMatcher.addURI(PCContract.CONTENT_AUTHORITY,PCContract.PATH_PC + "/#", PC_ID);

    }

    /** database helper object
     *
     */

    private PCDBHelper mDbHelper;

    @Override
    public boolean onCreate(){
        mDbHelper = new PCDBHelper(getContext());
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
            case PC:
                //for the bg code, query the bg table directly with the given
                //projection, selection, selection arguments and sort order
                //the cursor could contain multiple rows of the bg table
                cursor = database.query(PCContract.PCEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PC_ID:
                selection = PCContract.PCEntry._ID+"=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query((PCContract.PCEntry.TABLE_NAME), projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("cannot query unknown "+ uri);
        }
        return cursor;
    }
    /*public Uri insertBGfromXML (Uri uri, ArrayList<BoardGame> boardgames){
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
    }*/

    public Uri insert(Uri uri, ContentValues contentValues){
        final int match = sUriMatcher.match(uri);
        switch (match){
            case PC:
                Log.i("Insert", "Case PC");
                return insertPC(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for "+ uri);

        }

    }

    private Uri insertPC (Uri uri , ContentValues values){
        //todo
        //
        //
        // sanity check

        String name = values.getAsString(PCContract.PCEntry.COLUMN_PC_NAME);
        Log.i("insertPC", "value of name is " + name);
        if( name == null) {
            throw new IllegalArgumentException("PC requires a name");
        }
        String user = values.getAsString(PCContract.PCEntry.Column_PC_USER);
        Log.i("insertPC", "value of user is " + user);

        Integer score = values.getAsInteger(PCContract.PCEntry.COLUMN_PC_SCORE);
        Log.i("insertPC", "value of score is " + score);
        Float totaltime = values.getAsFloat(PCContract.PCEntry.COLUMN_PC_TOTALTIME);
        Log.i("insertPC", "value of totaltime is " + totaltime);
        if ((totaltime!= null && totaltime <0)){
            throw new IllegalArgumentException("PC time cant be less than 0");

        }

        //get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //insert the new BG with given values
        long id = database.insert(PCContract.PCEntry.TABLE_NAME,null,values);
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

            case PC:
                Log.i("Update", "Case PC!");
                return updatePC(uri, contentValues, selection, selectionArgs);
            case PC_ID:
                // for the PC_id extract out the id from the uri
                //so we know which row to update. selection will be "_id=?" and
                //slection arguments will be a string array containing hte actual id
                Log.i("Update", "Case PC_ID!");
                selection = PCContract.PCEntry._ID+"=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updatePC(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);

        }
    }

    /**
     * update pets in the database with given content values. apply the changes to the rows
     * specified in the selection and selection arguments( which could be 0 or 1 or more bg's
     * return the number of rows that were successfully updated
     */

    private int updatePC (Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        //if the {@link Entry#COLOMUN_PET_NAME} key is present,
        //check tat the name value is not null.

        if(values.containsKey(PCContract.PCEntry.COLUMN_PC_NAME)){
            String name = values.getAsString(PCContract.PCEntry.COLUMN_PC_NAME);
            if (name == null){
                throw new IllegalArgumentException("PC requires a name");
            }
        }
        if(values.containsKey(PCContract.PCEntry.Column_PC_USER)){
            String user = values.getAsString(PCContract.PCEntry.Column_PC_USER);
            if (user ==null){
                throw new IllegalArgumentException("PC requires a Username");
            }

        }

        if (values.containsKey(PCContract.PCEntry.COLUMN_PC_SCORE)){

            Integer score = values.getAsInteger(PCContract.PCEntry.COLUMN_PC_SCORE);

        }

        if (values.containsKey(PCContract.PCEntry.COLUMN_PC_TOTALTIME)){
            //check # is greater than 0
            Float totaltime = values.getAsFloat(PCContract.PCEntry.COLUMN_PC_TOTALTIME);
            if (totaltime != null && totaltime < 0){
                throw new IllegalArgumentException("PC require valid totaltime number");
            }
        }

        //if theres no values to update then we dont try to update
        if (values.size() ==0){
            return 0;
        }

        //otherwise, get writable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //return the number of database rows affected by the update statement
        int rowsUpdated =  database.update(PCContract.PCEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated !=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsUpdated;
    }
    @Override
    public int delete(Uri uri, String selection , String[] selectionArgs){
        //get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);

        switch(match) {
            case PC:
                //Delete all rows that match selection and selection args
                return database.delete(PCContract.PCEntry.TABLE_NAME, selection, selectionArgs);
            case PC_ID:
                //selecte a single row given by the id in the URI
                selection = PCContract.PCEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return database.delete(PCContract.PCEntry.TABLE_NAME, selection, selectionArgs);
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
            case PC:
                return PCContract.PCEntry.CONTENT_LIST_TYPE;
            case PC_ID:
                return PCContract.PCEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
