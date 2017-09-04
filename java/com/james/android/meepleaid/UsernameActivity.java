package com.james.android.meepleaid;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by 100599223 on 8/2/2017.
 */

public class UsernameActivity extends AppCompatActivity {

String mUserName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_username);
        final EditText usernameEditText = (EditText) findViewById(R.id.username_edittext);
        final SharedPreferences pref = getSharedPreferences("UserData", MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();

        if (!pref.getString("username","").equals("")){

            mUserName = usernameEditText.getText().toString(); //set username once button is clicked
            usernameEditText.setText(pref.getString("username",""));
        }


        Button usernameButton = (Button) findViewById(R.id.username_button);

        usernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BGDBHelper mDbHelper = new BGDBHelper(v.getContext());
                SQLiteDatabase database = mDbHelper.getWritableDatabase();
                database.execSQL("DROP TABLE boardgamecollection");
                database.execSQL("CREATE TABLE " + BGContract.BGEntry.TABLE_NAME + " ( " + BGContract.BGEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + BGContract.BGEntry.COLUMN_BG_NAME + " TEXT NOT NULL,"
                        + BGContract.BGEntry.COLUMN_BG_MINPLAYER + " TEXT NOT NULL, "
                        + BGContract.BGEntry.COLUMN_BG_MAXPLAYER + " TEXT NOT NULL);");
                database.close();

                mUserName = usernameEditText.getText().toString(); //set username once button is clicked
                editor.putString("username", mUserName);
                editor.apply();
                Toast.makeText(UsernameActivity.this, "Username set to \n " + mUserName, Toast.LENGTH_SHORT).show();
            }
        });

        Button usernameClear = (Button) findViewById(R.id.username_clearbutton);
        usernameClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.remove("username");
                editor.apply();
                usernameEditText.setText(pref.getString("username",""));

                BGDBHelper mDbHelper = new BGDBHelper(v.getContext());
                SQLiteDatabase database = mDbHelper.getWritableDatabase();
                database.execSQL("DROP TABLE boardgamecollection");
                database.execSQL("CREATE TABLE " + BGContract.BGEntry.TABLE_NAME + " ( " + BGContract.BGEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + BGContract.BGEntry.COLUMN_BG_NAME + " TEXT NOT NULL,"
                        + BGContract.BGEntry.COLUMN_BG_MINPLAYER + " TEXT NOT NULL, "
                        + BGContract.BGEntry.COLUMN_BG_MAXPLAYER + " TEXT NOT NULL);");
                database.close();
            }
        });

    }

}