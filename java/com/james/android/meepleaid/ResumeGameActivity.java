package com.james.android.meepleaid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by 100599223 on 8/14/2017.
 */

public class ResumeGameActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resume_game);
        Button clearButton = (Button) findViewById(R.id.clear_game_button);
        Button resumeButton = (Button) findViewById(R.id.resume_game_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PCDBHelper mDbHelper = new PCDBHelper(v.getContext());
                SQLiteDatabase database;
                database = mDbHelper.getWritableDatabase();
                database.execSQL("DROP TABLE playergame");
                //delete the number of players saved from previous game
                SharedPreferences pref = getSharedPreferences("UserData", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.remove("totalplayers");
                editor.commit();


                Intent intent = new Intent(ResumeGameActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        resumeButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResumeGameActivity.this, PlayerCountOverview.class);
                startActivity(intent);
            }
        });
    }

}
