package com.james.android.meepleaid;

import android.util.Log;

/**
 * Created by 100599223 on 8/7/2017.
 */

public class BoardGame {

    private String mTitle;
    private int mMinPlayer;
    private int mMaxPlayer;

    public BoardGame(String title, int minPlayer, int maxPlayer){
        mTitle = title;
        mMinPlayer = minPlayer;
        mMaxPlayer = maxPlayer;

        Log.i("Boardgame", "boardgame " +mTitle+" set");

    }

    public String getTitle(){return mTitle;}
    public int getMinPlayer() {return mMinPlayer;}
    public int getmMaxPlayer() {return mMaxPlayer;}
}
