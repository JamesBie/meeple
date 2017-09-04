package com.james.android.meepleaid;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by 100599223 on 8/18/2017.
 */

public class PlayerCountOverview extends AppCompatActivity {

    private PlayerAdapter pcAdapter;
    ArrayList<Players> playerlist = new ArrayList<Players>();
    PCDBHelper mDbHelper;
    SQLiteDatabase database;
    private static String count;
    private boolean tableexist;
    Cursor mCursor;

    @Override
    protected void onResume() {
        super.onResume();
        count = "SELECT count (*) FROM " + PCContract.PCEntry.TABLE_NAME;
        playerlist.clear();
        pcAdapter.clear();
        //populate from database
        mCursor = database.rawQuery(count,null);
        Cursor cursor = database.rawQuery("SELECT * FROM " + PCContract.PCEntry.TABLE_NAME,null);
        int idColumnIndex = cursor.getColumnIndex(PCContract.PCEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(PCContract.PCEntry.COLUMN_PC_NAME);
        int userColumnIndex = cursor.getColumnIndex(PCContract.PCEntry.Column_PC_USER);
        int scoreColumnIndex = cursor.getColumnIndex(PCContract.PCEntry.COLUMN_PC_SCORE);
        int timeColumnIndex = cursor.getColumnIndex(PCContract.PCEntry.COLUMN_PC_TOTALTIME);

        //iterate through and retrieve playerlist data from cursor
        mCursor.moveToFirst();
        try {
            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);
                String currentname = cursor.getString(nameColumnIndex);
                String currentuser = cursor.getString(userColumnIndex);
                int currentscore = cursor.getInt(scoreColumnIndex);
                int currenttime = cursor.getInt(timeColumnIndex);

                playerlist.add(new Players(currentname,currentuser,currentscore,currenttime));
            }
        }finally { cursor.close();}


    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_overview);
        mDbHelper = new PCDBHelper(this);
        database = mDbHelper.getReadableDatabase();
        count = "SELECT count (*) FROM " + PCContract.PCEntry.TABLE_NAME;


        try {mCursor = database.rawQuery(count,null);
            mCursor.moveToFirst();
            int icount = mCursor.getInt(0);
            if(icount>=0){
                tableexist=true;
            }
            mCursor.close();
        }// try to query table
        catch( Exception e){tableexist = false;}//if you cant query table b/c it doesnt exit create a new table

        if (!tableexist){
            database.execSQL("CREATE TABLE " + PCContract.PCEntry.TABLE_NAME + " ( " + PCContract.PCEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + PCContract.PCEntry.COLUMN_PC_NAME + " TEXT NOT NULL,"
                    + PCContract.PCEntry.Column_PC_USER + " TEXT NOT NULL,"
                    + PCContract.PCEntry.COLUMN_PC_SCORE + " INTEGER NOT NULL, "
                    + PCContract.PCEntry.COLUMN_PC_TOTALTIME + " REAL);");

        }


        pcAdapter = new PlayerAdapter(this,playerlist);


        ListView listView = (ListView) findViewById(R.id.listview_of_players);
        listView.setAdapter(pcAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                Players currentPlayer = pcAdapter.getItem(position);
                                                Intent playerProfile = new Intent(PlayerCountOverview.this, PlayerCounter.class);
                                                playerProfile.putExtra("playerToLoad",pcAdapter.getPosition(currentPlayer));


                                                SharedPreferences pref = getSharedPreferences("UserData", MODE_PRIVATE);
                                                SharedPreferences.Editor editor = pref.edit();
                                                editor.putString("totalplayers", String.valueOf(pcAdapter.getCount()));
                                                editor.apply();

                                                startActivity(playerProfile);



                                            }
                                        }


        );




        FloatingActionButton addPlayer = (FloatingActionButton) findViewById(R.id.add_new_player);
        addPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues cv = new ContentValues();
                database = mDbHelper.getWritableDatabase();
                cv.put(PCContract.PCEntry.COLUMN_PC_NAME,"player "+ String.valueOf(pcAdapter.getCount()+1));
                cv.put(PCContract.PCEntry.Column_PC_USER, "a");
                cv.put(PCContract.PCEntry.COLUMN_PC_SCORE,0);
                cv.put(PCContract.PCEntry.COLUMN_PC_TOTALTIME, 0);
                Uri uri = getContentResolver().insert(PCContract.PCEntry.CONTENT_URI, cv);
                Log.i("new player added", cv.keySet() + cv.valueSet().toString());
                pcAdapter.notifyDataSetChanged();
                pcAdapter.add(new Players(cv.getAsString("name"),cv.getAsString("username"),cv.getAsInteger("score"),cv.getAsDouble("totaltime")));
            }
        });


        FloatingActionButton endFab = (FloatingActionButton) this.findViewById(R.id.myFAB);
        endFab.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent finishGame = new Intent(PlayerCountOverview.this, FinishGame.class);
                startActivity(finishGame);
            }
        });


    }
    @Override
    public void onBackPressed(){

        super.onBackPressed();
        Intent backToMain = new Intent(PlayerCountOverview.this, MainActivity.class);
        startActivity(backToMain);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //inflate the menu
        getMenuInflater().inflate(R.menu.player_overview_menu,menu);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case (R.id.select_random_player ):
                Random randompicker = new Random();
                if (pcAdapter.getCount() > 0) {
                    int randomNum = (int) (randompicker.nextDouble() * (pcAdapter.getCount() ));
                    Toast.makeText(this, "Player randomly chosen was " + pcAdapter.getItem(randomNum).getPlayerName(), Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(this, "No players found", Toast.LENGTH_SHORT).show();
                }
                return true;

            case (R.id.action_email_player_overview):
                Intent email_intent = new Intent();
                String player_board = "";
                Date now = new Date();
                Date alsoNow = Calendar.getInstance().getTime();
                String nowAsString = new SimpleDateFormat("yyyy-MM-dd").format(now);


                for (int i=0; i<pcAdapter.getCount(); i++){
                    player_board = player_board + pcAdapter.getItem(i).getPlayerName() + " - "+ pcAdapter.getItem(i).getScore()+" - " + pcAdapter.getItem(i).getTime()+"\n";

                }
                email_intent.setAction(Intent.ACTION_SENDTO);
                email_intent.setData(Uri.parse("mailto:"));   //should only get emailing apps
                email_intent.putExtra(Intent.EXTRA_TEXT,"Player - Score - Time\n");
                email_intent.putExtra(Intent.EXTRA_TEXT, player_board);
                email_intent.putExtra(Intent.EXTRA_SUBJECT, "Board Game of " + nowAsString );
                if (email_intent.resolveActivity(getPackageManager() )!= null){
                    startActivity(email_intent);
                }


        }

        return super.onOptionsItemSelected(item);
    }
}

