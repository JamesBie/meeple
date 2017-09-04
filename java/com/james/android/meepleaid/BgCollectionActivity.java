package com.james.android.meepleaid;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.james.android.meepleaid.BGContract.BGEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by 100599223 on 8/7/2017. Main activity for displaying the board game collection from
 * board game geek. implements loaderManager so things don't interrupt the async task.
 */

public class BgCollectionActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<BoardGame>> {

    private static String mUsername;

    private static String INPUT_URL = "https://www.boardgamegeek.com/xmlapi2/collection?username="+mUsername+"&stats=1"+"&excludesubtype=boardgameexpansion"+"&own=1";

    private BoardGameAdapter bAdapter;
    private BoardGameAdapter filteredbAdapter;
    private static final int EARTHQUAKE_LOADER_ID = 1;
    private static BgCollectionActivity parent;
    private EditText editTextName;
    private EditText editTextPlayerNumber;
    private String editTextNameMemory="";
    private String editTextPlayerMemory="";

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate the menu option from the res/menu and
        //add this menu item to the otp of the app bar

        getMenuInflater().inflate(R.menu.boardgame_menu, menu);
        return true;
    }

    @Override
    public Loader<List<BoardGame>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences pref = getSharedPreferences("UserData", MODE_PRIVATE); //gets username saved from useractivity

        mUsername = pref.getString("username","");
        INPUT_URL = "https://www.boardgamegeek.com/xmlapi2/collection?username="+mUsername+"&stats=1"+"&excludesubtype=boardgameexpansion"+"&own=1";
        Log.w("create loader", mUsername);
        Log.w("create loader", INPUT_URL);
        BGDBHelper mDbHelper = new BGDBHelper(this);
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Log.i("oncreate", "database size is " + database.getMaximumSize());
        String count = "SELECT count (*) FROM " + BGEntry.TABLE_NAME;
        Cursor mCursor;

        boolean tableexist = true;
try {mCursor = database.rawQuery(count,null);}// try to query table
catch( Exception e){tableexist = false;}//if you cant query table b/c it doesnt exist create a new table

        if (!tableexist){
            database.execSQL("CREATE TABLE " + BGContract.BGEntry.TABLE_NAME + " ( " + BGContract.BGEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + BGContract.BGEntry.COLUMN_BG_NAME + " TEXT NOT NULL,"
                    + BGContract.BGEntry.COLUMN_BG_MINPLAYER + " TEXT NOT NULL, "
                    + BGContract.BGEntry.COLUMN_BG_MAXPLAYER + " TEXT NOT NULL);");

        }
        mCursor = database.rawQuery(count,null);
        mCursor.moveToFirst();

        int icount = mCursor.getInt(0);
        if(icount>0){
            Log.i("icount>0", String.valueOf(icount));
            //populate from database
            mCursor.close();
            return new BoardgameLoader(this, INPUT_URL,true);
        }else {
            //populate database from xml
            Log.i ("icount=0", String.valueOf(icount));
            mCursor.close();
            return new BoardgameLoader(this, INPUT_URL,false);
        }



}




    @Override
    public void onLoadFinished(Loader<List<BoardGame>> loader, List<BoardGame> boardgames) {
        //clear adapter of previous boardgame
        bAdapter.clear();

        if (boardgames != null && !boardgames.isEmpty()) {
            bAdapter.addAll(boardgames);
            DownloadXmlTask.BGGdelayed = false;



        }
        //gives a error if boardgamegeek comes back with a 202 message (meaning its still compiling).
        if (DownloadXmlTask.BGGdelayed){
            Toast.makeText(this, "Boardgamegeek currently compiling list\n Please try again in a few minutes", Toast.LENGTH_LONG).show();
            DownloadXmlTask.BGGdelayed = false;
        }
    }

    public void onLoaderReset(Loader<List<BoardGame>> loader) {
        bAdapter.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        BGDBHelper mDbHelper;
        SQLiteDatabase database;
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {

            // Respond to a click on the "update board game" menu option
            case R.id.action_update_boardgame_collection:
                // will update database from internet in future and loader will grab data from database first next
                mDbHelper = new BGDBHelper(this);
                database = mDbHelper.getWritableDatabase();
                database.execSQL("DROP TABLE boardgamecollection");
                database.execSQL( "CREATE TABLE " + BGContract.BGEntry.TABLE_NAME + " ( " + BGContract.BGEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + BGContract.BGEntry.COLUMN_BG_NAME + " TEXT NOT NULL,"
                        + BGContract.BGEntry.COLUMN_BG_MINPLAYER + " TEXT NOT NULL, "
                        + BGContract.BGEntry.COLUMN_BG_MAXPLAYER + " TEXT NOT NULL);");

                int size = bAdapter.getCount();
                Log.i("bAdapterupdatemenu", String.valueOf(bAdapter.getCount()));
                try{
                    ContentValues cv = new ContentValues();
                    for (int i = 0; i <size; ++i){


                        cv.put(BGContract.BGEntry.COLUMN_BG_NAME,bAdapter.getItem(i).getTitle());
                        cv.put(BGContract.BGEntry.COLUMN_BG_MINPLAYER,bAdapter.getItem(i).getMinPlayer());
                        cv.put(BGContract.BGEntry.COLUMN_BG_MAXPLAYER, bAdapter.getItem(i).getmMaxPlayer());
                        Uri uri = getContentResolver().insert(BGEntry.CONTENT_URI, cv);

                        Log.i("insert", cv.keySet() + cv.valueSet().toString());
                        Log.i("databasepagesize", String.valueOf(database.getPageSize()));
                        Log.i("database",database.toString());
                        //long newUri = database.insert(BGEntry.TABLE_NAME,null, cv);
                        //Log.i("newUri", String.valueOf(newUri));
                    }
                    Cursor cursor = database.rawQuery("SELECT * FROM " + BGEntry.TABLE_NAME, null);
                    Toast.makeText(this, "Board Game Collection Saved", Toast.LENGTH_SHORT);
                    Log.i("databasepath", String.valueOf(cursor.getCount()));

                } catch (Exception e) {
                    Log.e("insertbgfromxml"," exception", e);
                }


                database.close();
                return true;
            case R.id.action_randompick_boardgame:
                // TODO: 8/9/2017
                Random randompicker = new Random();

                if (bAdapter.getCount() > 0) {
                    int randomNum = (int) (randompicker.nextDouble() * (bAdapter.getCount() ));
                    Toast.makeText(this, "Board Game picked is " + bAdapter.getItem(randomNum).getTitle(), Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(this, "No Board Games to pick from", Toast.LENGTH_SHORT).show();
                }

                return true;


            // Respond to a click on the "Delete board game" menu option
            case R.id.action_delete_boardgame_collection:
                mDbHelper = new BGDBHelper(this);
                database = mDbHelper.getWritableDatabase();
                database.execSQL("DROP TABLE boardgamecollection");
                database.execSQL("CREATE TABLE " + BGContract.BGEntry.TABLE_NAME + " ( " + BGContract.BGEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + BGContract.BGEntry.COLUMN_BG_NAME + " TEXT NOT NULL,"
                        + BGContract.BGEntry.COLUMN_BG_MINPLAYER + " TEXT NOT NULL, "
                        + BGContract.BGEntry.COLUMN_BG_MAXPLAYER + " TEXT NOT NULL);");
                bAdapter.clear();
                database.close();
                return true;

            case R.id.action_email_boardgame_list:
                Intent email_intent  = new Intent();
                String list_of_games = "Title       Player Count\n";
                for (int i = 0; i < bAdapter.getCount(); i++){
                list_of_games = list_of_games + bAdapter.getItem(i).getTitle()+" "+ String.valueOf(bAdapter.getItem(i).getMinPlayer())+" - "+ String.valueOf(bAdapter.getItem(i).getmMaxPlayer()) + "\n";

            }
                email_intent.setAction(Intent.ACTION_SENDTO);
                email_intent.setData(Uri.parse("mailto:"));   //should only get emailing apps
                email_intent.putExtra(Intent.EXTRA_TEXT, list_of_games);
                email_intent.putExtra(Intent.EXTRA_SUBJECT, "Board Game List of " + mUsername);
            if (email_intent.resolveActivity(getPackageManager() )!= null){
                startActivity(email_intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_list);


// create a list of boardgames

        ArrayList<BoardGame> boardgames = new ArrayList<BoardGame>();

        //create a wordadapter whos data source is the list of BoardGames.
        //the adapter knows how to create list items for each item int he list
        bAdapter = new BoardGameAdapter(this, boardgames);

        //Find the listview object in the view hierarchy of the activity
        //there hsould be a list view with the view id list in the world_list.xml file

        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(bAdapter);

        //Allows editview to leave focus if you click somewhere else
         editTextName = (EditText) findViewById(R.id.editTextFilterName);
         editTextPlayerNumber = (EditText) findViewById(R.id.editTextFilterNumber);

        editTextName.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });



        editTextPlayerNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        editTextName.addTextChangedListener(filterboardgames);

        editTextPlayerNumber.addTextChangedListener(filterboardgames);


        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        //Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        //If there is a network connection, fetch data

       // if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            //Get a reference ot the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            //Initialize the loader. Pass in the in ID constant defined above and pass in null for
            //the bundle. Pass in this activity for the LoaderCallBacks parameter (which is valid
            //because this Activity implements the LoaderCallbacks interface).
            //First Argument: ID of loader to initialize
            //Second Argument: allows us to pass a bundle of additional information (skipped this time)
            //Third Argument: what object shoudl receive the Loadercallbacks (and therefore the data when the load is complete) - will be this activity.
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
        //} else {
            //otherwise, display an error.
            //first we need to hide the loading indicator so the error message can be visible


       // }

    }

    private TextWatcher filterboardgames = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (editTextName.getText().hashCode() == s.hashCode()){
             bAdapter.getFilter().filter(s.toString());

        }

            if (editTextPlayerNumber.getText().hashCode() ==s.hashCode()){ // return based of playercount
                bAdapter.getFilter(true).filter(s.toString());

            }
        }

        @Override
        public void afterTextChanged(Editable s) {


        }
    };

    /**
     * Temporary helper method to display information in the onscreen textview about
     * the boardgame database
     */

    public void transferDatabasetoAdapter() {
    // define a projection that specifies which columns from teh dtabase
        //you will actually use after this query.
        String[] projection = {
                BGEntry._ID,
                BGEntry.COLUMN_BG_NAME,
                BGEntry.COLUMN_BG_MINPLAYER,
                BGEntry.COLUMN_BG_MAXPLAYER
        };

        Cursor cursor = getContentResolver().query(
                BGEntry.CONTENT_URI, //content uri of the table
                projection,         //columns to return for each row
                null,               //selection criteria
                null,               //selection criteria
                null);              //sort order for returned rows

        try {
            // figures out the index of each column
            int idColumnIndex = cursor.getColumnIndex((BGEntry._ID));
            int nameColumnIndex = cursor.getColumnIndex(BGEntry.COLUMN_BG_NAME);
            int minColumnIndex = cursor.getColumnIndex(BGEntry.COLUMN_BG_MINPLAYER);
            int maxColumnIndex = cursor.getColumnIndex(BGEntry.COLUMN_BG_MAXPLAYER);

            while (cursor.moveToNext()) {
            // use that index to instract the string or int value of the word
                //at the current row the cursor is on.

                int currentID=cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                int currentMin = cursor.getInt(minColumnIndex);
                int currentMax = cursor.getInt(maxColumnIndex);
                bAdapter.add(new BoardGame(currentName,currentMin,currentMax));

            }

        }finally {cursor.close();} //Always close the cursor when you're done reading from it. this releases all its

        //resources and makes it invalid.

    }


}



