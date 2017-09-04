package com.james.android.meepleaid;

import android.util.Log;

/**
 * Created by 100599223 on 8/18/2017.
 */

public class Players {
    private String mName;
    private String mUsername;
    private int mScore;
    private double mTime;

    public Players (String name, String username, int score, double time){
        mName = name;
        mUsername=username;
        mScore = score;
        mTime = time;

        Log.i("Players", "Name " + mName+ " Score: "+ mScore + " Time: "+ mTime );



    }
    public String getPlayerName() {return mName;}
    public String getUserName() {return mUsername;}
    public int getScore() {return mScore;}
    public double getTime() {return mTime;}
}
