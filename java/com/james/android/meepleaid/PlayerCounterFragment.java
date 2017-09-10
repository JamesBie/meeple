package com.james.android.meepleaid;


import android.app.Activity;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by 100599223 on 8/14/2017.
 */

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerCounterFragment extends android.support.v4.app.Fragment {
    //argument keyfor the page number this fragment represents
    public static final String ARG_PAGE = "page";
    private int mPlayerCount = 0; //value thats added or subtracted from players current score tracker
    private int mPageNumber; //current page fragment beign viewed
    private String mUsername;
    private int mTotalFragments;
    private String baseScore = "0";
    private String playerScoreText;
    private static boolean tableexist;
    private static boolean tabletest = false;
    private static SQLiteDatabase database;
    private static PCDBHelper mDbHelper;
    private static Cursor mCursor;
    private static String count;
    private static boolean timer_running = false;
    private boolean isPaused = false;
    private boolean isCanceled = false;
    private long timeRemaining = 0;
    private double playerTime;
    static CountDownTimer timer;


 



    //occurs before create so we can ensure we get the context for the database
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mDbHelper = new PCDBHelper(activity);
        database = mDbHelper.getReadableDatabase();
        count = "SELECT count (*) FROM " + PCContract.PCEntry.TABLE_NAME;


    }


    // fragments page number which is set to the argument value for argpage
    public static PlayerCounterFragment create(int pageNumber, int totalpages) {
        PlayerCounterFragment fragment = new PlayerCounterFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        args.putInt("totalpage", totalpages);
        fragment.setArguments(args);


        return fragment;
    }

    public PlayerCounterFragment() {
        //empty public constructor
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mPageNumber = getArguments().getInt(ARG_PAGE);
        mTotalFragments = getArguments().getInt("totalpage");

        try {
            mCursor = database.rawQuery(count, null);
            mCursor.moveToFirst();
            int icount = mCursor.getInt(0);
            if (icount > 0) {
                tableexist = true;
            }
            mCursor.close();
        }// try to query table
        catch (Exception e) {
            tableexist = false;
        }//if you cant query table b/c it doesnt exit create a new table

        if (!tableexist) {
            database.execSQL("CREATE TABLE " + PCContract.PCEntry.TABLE_NAME + " ( " + PCContract.PCEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + PCContract.PCEntry.COLUMN_PC_NAME + " TEXT NOT NULL,"
                    + PCContract.PCEntry.COLUMN_PC_USER + " TEXT NOT NULL,"
                    + PCContract.PCEntry.COLUMN_PC_SCORE + " INTEGER NOT NULL, "
                    + PCContract.PCEntry.COLUMN_PC_TOTALTIME + " REAL);");

            for (int i = 0; i < mTotalFragments; i++) {
                ContentValues cv = new ContentValues();
                String mUserName = "Player " + String.valueOf(i + 1);
                Log.i("creating database", "username is! " + mUserName);
                cv.put(PCContract.PCEntry.COLUMN_PC_NAME, (mUserName));
                cv.put(PCContract.PCEntry.COLUMN_PC_USER,"a");
                cv.put(PCContract.PCEntry.COLUMN_PC_SCORE, 0);
                cv.put(PCContract.PCEntry.COLUMN_PC_TOTALTIME, 0);

                Log.i("end ofcreatingdatabase", "username in cv is " + cv.get(PCContract.PCEntry.COLUMN_PC_NAME));
                Uri uri = getActivity().getContentResolver().insert(PCContract.PCEntry.CONTENT_URI, cv);
            }
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.player_counter, container, false);


// figure out why i need this before things update
        ContentValues cv = new ContentValues();
        cv.put(PCContract.PCEntry.COLUMN_PC_NAME, "Player " + String.valueOf(mPageNumber + 1));
        Uri currentPlayerUri = PCContract.PCEntry.CONTENT_URI;
        currentPlayerUri = Uri.withAppendedPath(PCContract.PCEntry.CONTENT_URI, (String.valueOf(mPageNumber + 1)));
        int rowsAffected = getActivity().getContentResolver().update(currentPlayerUri, cv, null, null);


        ImageView incrementone = (ImageView) rootView.findViewById(R.id.increment_one_button);
        ImageView decrementone = (ImageView) rootView.findViewById(R.id.decrement_one_button);
        ImageView incrementfive = (ImageView) rootView.findViewById(R.id.increment_five_button);
        ImageView decrementfive = (ImageView) rootView.findViewById(R.id.decrement_five_button);
        final TextView playerScore = (TextView) rootView.findViewById(R.id.current_points);
        final TextInputLayout inputLayout = (TextInputLayout) rootView.findViewById(R.id.inputLayout);
        final TextView playerName = (TextView) rootView.findViewById(R.id.current_player);// Title at tope for who player is
        final String currentPlayer = "Player " + String.valueOf(mPageNumber + 1); // finds player from fragment

        //playerName.setText(currentPlayer);

        Log.i("beforetestprojection", "testing projection");
        String[] testprojection = {
                PCContract.PCEntry.COLUMN_PC_NAME,
                PCContract.PCEntry.COLUMN_PC_USER,
                PCContract.PCEntry.COLUMN_PC_SCORE,
                PCContract.PCEntry.COLUMN_PC_TOTALTIME
        };

        Log.i("beforecursor", "querying entire database");

        Cursor testcursor = getActivity().getContentResolver().query(
                PCContract.PCEntry.CONTENT_URI,  //content uri of the words table
                testprojection,             //columns to return for each row
                null,                   //selection criteria
                null,                   //selection criteria
                null);
        try {
            Log.i("after cursor", "there are " + testcursor.getCount() + " rows");
            //int idColumnIndex = testcursor.getColumnIndex(PCContract.PCEntry._ID);
            //Log.i("after idcolumnindex", "idColumn index is" + idColumnIndex);
            int nameColumnIndex = testcursor.getColumnIndex(PCContract.PCEntry.COLUMN_PC_NAME);
            Log.i("after namecolumnindex", "nameColumn index is " + nameColumnIndex);

            int userColumnIndex = testcursor.getColumnIndex(PCContract.PCEntry.COLUMN_PC_USER);
            Log.i("after namecolumnindex", "nameColumn index is " + nameColumnIndex);
            int scoreColumnIndex = testcursor.getColumnIndex(PCContract.PCEntry.COLUMN_PC_SCORE);
            Log.i("after scorecolumnindex", "scoreColumn index is " + scoreColumnIndex);
            int timeColumnIndex = testcursor.getColumnIndex(PCContract.PCEntry.COLUMN_PC_TOTALTIME);
            Log.i("after timecolumnindex", "timeColumn index is " + timeColumnIndex);


            while (testcursor.moveToNext()) {
                //int currentID = testcursor.getInt(idColumnIndex);
                String currentName = testcursor.getString(nameColumnIndex);
                String currentUser = testcursor.getString(userColumnIndex);
                int currentScore = testcursor.getInt(scoreColumnIndex);
                double currentTime = testcursor.getDouble(timeColumnIndex);


                Log.i("values of database are", /*"current id"+ currentID+*/ "current name " + currentName +"current user "+currentUser+
                         " current Score " + currentScore + " currentTime " + currentTime);


            }

        } finally {
            testcursor.close();
        }




        // Retrieve the values from database
        String[] projection = {
                PCContract.PCEntry.COLUMN_PC_NAME,
                PCContract.PCEntry.COLUMN_PC_USER,
                PCContract.PCEntry.COLUMN_PC_SCORE,
                PCContract.PCEntry.COLUMN_PC_TOTALTIME
        };

        String[] selectArg = {"Player " + String.valueOf(mPageNumber + 1)};


        Cursor cursor = getActivity().getContentResolver().query(PCContract.PCEntry.CONTENT_URI, projection, PCContract.PCEntry.COLUMN_PC_NAME + "=?", selectArg, null);


        if (cursor != null && cursor.moveToFirst()) { //check if cursor is null
            int userColumnIndex = cursor.getColumnIndex(PCContract.PCEntry.COLUMN_PC_USER);
            int scoreColumnIndex = cursor.getColumnIndex(PCContract.PCEntry.COLUMN_PC_SCORE);
            cursor.moveToFirst();
            int timeColumnIndex = cursor.getColumnIndex(PCContract.PCEntry.COLUMN_PC_TOTALTIME);
            Log.i("timecolumnindex", String.valueOf(timeColumnIndex));
            int columntext = cursor.getCount();
            cursor.moveToFirst();
            String currentUser = cursor.getString(userColumnIndex);
            int currentScore = cursor.getInt(scoreColumnIndex);
            cursor.moveToFirst();
            playerTime = cursor.getDouble(timeColumnIndex);

            playerScore.setText(String.valueOf(currentScore));
            playerName.setText(currentUser);
            baseScore = playerScore.getText().toString().split(" ")[0];



            cursor.close();
        }
        TextView confirmButton = (TextView) rootView.findViewById(R.id.confirmation_score_button);
        TextView cancelButton = (TextView) rootView.findViewById(R.id.cancel_score_button);





        playerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new UserChangePopup();
                newFragment.show(getFragmentManager(), "username " + String.valueOf(mPageNumber) );
            }

        });

        //Clicking confirm will bring the score into the database and set the textview.
        //increase scorecounter by one
        incrementone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayerCount += 1;
                baseScore = playerScore.getText().toString().split(" ")[0];
                if (mPlayerCount > 0) {
                    playerScoreText = baseScore + " (+" + String.valueOf(mPlayerCount) + ")";
                } else {

                    playerScoreText = baseScore + " (" + String.valueOf(mPlayerCount) + ")";
                }
                playerScore.setText(String.valueOf(playerScoreText));
            }
        });

        //decrease scorecounter by one
        decrementone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPlayerCount -= 1;
                baseScore = playerScore.getText().toString().split(" ")[0];
                if (mPlayerCount > 0) {
                    playerScoreText = baseScore + " (+" + String.valueOf(mPlayerCount) + ")";
                } else {

                    playerScoreText = baseScore + " (" + String.valueOf(mPlayerCount) + ")";
                }
                playerScore.setText(String.valueOf(playerScoreText));
            }
        });
//increase scorecounter by one
        incrementfive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayerCount += 5;
                baseScore = playerScore.getText().toString().split(" ")[0];
                if (mPlayerCount > 0) {
                    playerScoreText = baseScore + " (+" + String.valueOf(mPlayerCount) + ")";
                } else {

                    playerScoreText = baseScore + " (" + String.valueOf(mPlayerCount) + ")";
                }
                playerScore.setText(String.valueOf(playerScoreText));
            }
        });

        //decrease scorecounter by one
        decrementfive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPlayerCount -= 5;
                baseScore = playerScore.getText().toString().split(" ")[0];
                if (mPlayerCount > 0) {
                    playerScoreText = baseScore + " (+" + String.valueOf(mPlayerCount) + ")";
                } else {

                    playerScoreText = baseScore + " (" + String.valueOf(mPlayerCount) + ")";
                }
                playerScore.setText(String.valueOf(playerScoreText));
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDbHelper = new PCDBHelper(v.getContext());
                database = mDbHelper.getWritableDatabase();
                baseScore = playerScore.getText().toString().split(" ")[0];

                playerScoreText = String.valueOf(Integer.parseInt(baseScore) + mPlayerCount);
                baseScore = playerScoreText;

                playerScore.setText(playerScoreText);
                mPlayerCount = 0;

                //update database wiht new values for player
                ContentValues cv = new ContentValues();
                cv.put(PCContract.PCEntry.COLUMN_PC_NAME, "Player " + String.valueOf(mPageNumber + 1));// insert player number into column based on current page
                cv.put(PCContract.PCEntry.COLUMN_PC_SCORE, Integer.valueOf(playerScoreText));
                cv.put(PCContract.PCEntry.COLUMN_PC_TOTALTIME, playerTime);

                Log.i("Confirmbutonclicked", "Current Name file is " + cv.get(PCContract.PCEntry.COLUMN_PC_NAME));
                Log.i("confirmbuttonclicked", "about to update database");
                Uri currentPlayerUri = PCContract.PCEntry.CONTENT_URI;
                currentPlayerUri = Uri.withAppendedPath(PCContract.PCEntry.CONTENT_URI, (String.valueOf(mPageNumber + 1)));
                Log.i("confirm button", "Currentplayeruri " + currentPlayerUri.toString());
                int rowsAffected = getActivity().getContentResolver().update(currentPlayerUri, cv, null, null);
                if (rowsAffected == 0) {
                    Toast.makeText(v.getContext(), "no row affected update failed", Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(v.getContext(), "update successful", Toast.LENGTH_SHORT).show();
                }


            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mPlayerCount = 0;
                playerScore.setText(baseScore);

            }
        });


        final EditText timerText = (EditText) rootView.findViewById(R.id.timer_edittext);
        final Button timerStartButton = (Button) rootView.findViewById(R.id.time_start_button);
        final Button timerStopButton = (Button) rootView.findViewById(R.id.time_stop_button);


        final SharedPreferences prefTime = getActivity().getSharedPreferences("defaulttimer", MODE_PRIVATE); //gets timer that user previously inputted and save as default
        final SharedPreferences.Editor editorTime = prefTime.edit();
        final String defaulttime = prefTime.getString("defaulttimer", "");
        timerText.setText(defaulttime);


        timerStopButton.setEnabled(false);

        timerStopButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mDbHelper = new PCDBHelper(v.getContext());
                database = mDbHelper.getWritableDatabase();


                if (isPaused) {
                    isCanceled = true;
                    isPaused=false;
                } else {
                isPaused = true;
                }

                timerStartButton.setEnabled(true);

                if (!isCanceled) {
                    timerStopButton.setEnabled(true);
                    timerStartButton.setText("Resume");
                    timerStopButton.setText("Clear");
                    timerText.setText(String.valueOf(Math.round(timeRemaining / 1000)));
                } else {



                    timerStopButton.setEnabled(false);
                    timerStartButton.setText("Start");
                    timerStopButton.setText("Pause");
                    timerText.setText(defaulttime);

                }
                //update time in database if timer is fully cancelled.
                String[] projection = {
                        PCContract.PCEntry.COLUMN_PC_NAME,
                        PCContract.PCEntry.COLUMN_PC_USER,
                        PCContract.PCEntry.COLUMN_PC_SCORE,
                        PCContract.PCEntry.COLUMN_PC_TOTALTIME
                };

                String[] selectArg = {"Player " + String.valueOf(mPageNumber + 1)};


                Cursor cursor = getActivity().getContentResolver().query(PCContract.PCEntry.CONTENT_URI, projection, PCContract.PCEntry.COLUMN_PC_NAME + "=?", selectArg, null);


                if (cursor != null && cursor.moveToFirst()) { //check if cursor is null
                    int userColumnIndex = cursor.getColumnIndex(PCContract.PCEntry.COLUMN_PC_USER);
                    int scoreColumnIndex = cursor.getColumnIndex(PCContract.PCEntry.COLUMN_PC_SCORE);
                    cursor.moveToFirst();
                    int timeColumnIndex = cursor.getColumnIndex(PCContract.PCEntry.COLUMN_PC_TOTALTIME);
                    Log.i("timecolumnindex", String.valueOf(timeColumnIndex));
                    int columntext = cursor.getCount();
                    cursor.moveToFirst();
                    String currentUser = cursor.getString(userColumnIndex);
                    int currentScore = cursor.getInt(scoreColumnIndex);
                    cursor.moveToFirst();
                    playerTime = cursor.getDouble(timeColumnIndex);
                    cursor.close();
                }





                timer_running = false;


            }
        });


        timerStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int timer_time;
                //Enable pause button
                try {

                    if (!timerText.getText().toString().isEmpty()) {

                        timer_time = Integer.valueOf(timerText.getText().toString());
                    } else {
                        timer_time = 0;
                    }

                    inputLayout.setError(null);
                    timerStopButton.setEnabled(true);
                    timerStartButton.setEnabled(false);
                    timerStartButton.setText("Start");
                    timerStopButton.setText("Pause");


                    if (!isPaused && !isCanceled) {
                        editorTime.putString("defaulttimer", String.valueOf(timer_time));
                        editorTime.apply();
                    }

                    isPaused = false;
                    isCanceled = false;

                    if (!timer_running) {
                        timer = new CountDownTimer(timer_time * 1000, 100) {

                            public void onTick(long millisUntilFinished) {

                                String[] projection = {
                                        PCContract.PCEntry.COLUMN_PC_NAME,
                                        PCContract.PCEntry.COLUMN_PC_SCORE,
                                        PCContract.PCEntry.COLUMN_PC_TOTALTIME
                                };

                                String[] selectArg = {"Player " + String.valueOf(mPageNumber + 1)};


                                Cursor cursor = getActivity().getContentResolver().query(PCContract.PCEntry.CONTENT_URI, projection, PCContract.PCEntry.COLUMN_PC_NAME + "=?", selectArg, null);


                                if (cursor != null && cursor.moveToFirst()) { //check if cursor is null
                                    int scoreColumnIndex = cursor.getColumnIndex(PCContract.PCEntry.COLUMN_PC_SCORE);
                                    cursor.moveToFirst();
                                    int timeColumnIndex = cursor.getColumnIndex(PCContract.PCEntry.COLUMN_PC_TOTALTIME);
                                    Log.i("timecolumnindex", String.valueOf(timeColumnIndex));
                                    int columntext = cursor.getCount();
                                    cursor.moveToFirst();
                                    int currentScore = cursor.getInt(scoreColumnIndex);
                                    cursor.moveToFirst();
                                    double playerTime = cursor.getDouble(timeColumnIndex);
                                    cursor.close();

                                }
                                if (isPaused || isCanceled) {
                                    //cancel current instance if paused of cancelled
                                    timer_running = false;
                                    cancel();

                                } else {
                                    timer_running = true;

                                    String timer_text = "" + millisUntilFinished / 1000;

                                    timerText.setText(timer_text);
                                    timeRemaining = millisUntilFinished;

                                    ContentValues cv = new ContentValues();

                                    cv.put(PCContract.PCEntry.COLUMN_PC_NAME, "Player " + String.valueOf(mPageNumber + 1)); // insert into player column depending on name
                                    cv.put(PCContract.PCEntry.COLUMN_PC_TOTALTIME, (playerTime + (timer_time - timeRemaining / 1000)));
                                    Log.i("stopbutton time value", "player time is " + playerTime);
                                    Uri currentPlayerUri = Uri.withAppendedPath(PCContract.PCEntry.CONTENT_URI, (String.valueOf(mPageNumber + 1)));

                                    int rowsAffected = getActivity().getContentResolver().update(currentPlayerUri, cv, null, null); //update and save into a variable to get back information
                                    Log.i("afterrowaffected", "PlayerTime: " + playerTime + "\n" + "timer_time: " + timer_time + "\n" + "timerRemaining: " + timeRemaining + "\nTimer_time: " + timer_time);


                                   /* if (rowsAffected==0){
                                    Toast.makeText(v.getContext(), "no row affected update failed", Toast.LENGTH_SHORT).show();
                                }else {

                                    Toast.makeText(v.getContext(), "update time successful", Toast.LENGTH_SHORT).show();
                                }*/


                                }
                            }


                            public void onFinish() {
                                timerText.setText("0");
                                timer_running = false;
                                Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                                long[] pattern = {0, 1000, 100, 1000, 100, 1000};
                                v.vibrate(pattern, -1);
                                timerStartButton.setEnabled(true);
                                timerStopButton.setEnabled(false);
                            }
                        }.start();
                    }


                } catch(NumberFormatException e) {
                    inputLayout.setError("Enter a valid seconds time");
                }
            }
        });


        timerText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });


        return rootView;


    }




    public int getPageNumber() {
        return mPageNumber;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        database.close();
    }



    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onStop() {

        super.onStop();
        if (timer_running == true){
            timer.cancel();
            timer_running=false;
        }


    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
          // if(timer_running==true){
                Toast.makeText(getActivity(),"Changed to Landscape", Toast.LENGTH_LONG);
           // }
            Log.i("config change","changed landscape");


        }
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
           // if(timer_running==true){
                Toast.makeText(getActivity(),"Changed to Portrait", Toast.LENGTH_LONG);
           // }
            Log.i("config change","changed portrait");

        }
        Log.i("config change","changed");
    }
}

