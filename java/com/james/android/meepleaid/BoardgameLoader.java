package com.james.android.meepleaid;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.james.android.meepleaid.BGContract.BGEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 100599223 on 8/9/2017.
 */

public class BoardgameLoader extends AsyncTaskLoader<List<BoardGame>> {
    private String mUrl;
    private boolean mFromDatabase;
    Context mContext;

    public BoardgameLoader(Context context, String url, Boolean fromDatabase){
        super(context);
        mUrl = url;
        mFromDatabase = fromDatabase;
        mContext=context;
    }

    @Override
    protected void onStartLoading() {forceLoad();}

    /*
    this is a background thread
     */
    @Override
public List<BoardGame> loadInBackground(){
        List<BoardGame> boardgames= new ArrayList<>();

    if (mUrl == null){

        return null;
    }
    if(mFromDatabase){
        Log.i("enter mFromdatabase", String.valueOf(mFromDatabase));
        BGDBHelper mDbHelper = new BGDBHelper(mContext);
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        //Cursor cursor = database.query(BGEntry.TABLE_NAME,projection,selection, selectionArgs,null,null,sortOrder);
        Cursor cursor = database.rawQuery("SELECT * FROM " + BGEntry.TABLE_NAME,null);
        int idColumnIndex = cursor.getColumnIndex(BGEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(BGEntry.COLUMN_BG_NAME);
        int minColumnIndex = cursor.getColumnIndex(BGEntry.COLUMN_BG_MINPLAYER);
        int maxColumnIndex = cursor.getColumnIndex(BGEntry.COLUMN_BG_MAXPLAYER);
        //iterate through all the returned rows in the cursor. will return
        //true as long as -1( no next line) isnt reached
       try {
           while (cursor.moveToNext()) {
               int currentID = cursor.getInt(idColumnIndex);
               String currentname = cursor.getString(nameColumnIndex);
               int currentmin = cursor.getInt(minColumnIndex);
               int currentmax = cursor.getInt(maxColumnIndex);

              boardgames.add(new BoardGame(currentname,currentmin,currentmax));
           }
       }finally { cursor.close();
//       Log.i("printedfromdatabase",boardgames.toString());
       }



    }else{
        boardgames = DownloadXmlTask.fetchXML(mUrl);
       // Log.i("printedfromxml", boardgames.toString());
    }


            return boardgames;
}

}
