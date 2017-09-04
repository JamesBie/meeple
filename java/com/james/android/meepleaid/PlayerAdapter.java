package com.james.android.meepleaid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by 100599223 on 8/18/2017.
 */

public class PlayerAdapter extends ArrayAdapter<Players> {

    private ArrayList<Players> mPlayerList;



    public PlayerAdapter (Context context, ArrayList<Players> playerlist){
        super(context, 0, playerlist);
        mPlayerList = playerlist;
    }

    public int getCount() {

        return mPlayerList.size();
    }

    public long GetItemPosition(int position){
        return position;
    }


    @Override
    public View getView (int position, View convertView, ViewGroup parent){
        // check if existing view is being used. if not inflate

        View listItemView = convertView;
        if (listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_playercount, parent, false);
        }
        if (!mPlayerList.isEmpty()) {
            TextView playerName = (TextView) listItemView.findViewById(R.id.player_overview_name);
            playerName.setText(mPlayerList.get(position).getPlayerName());

            TextView playerScore = (TextView) listItemView.findViewById(R.id.player_overview_score);
            playerScore.setText(String.valueOf(mPlayerList.get(position).getScore()));

            TextView playerTime = (TextView) listItemView.findViewById(R.id.player_overview_time);
            playerTime.setText(String.valueOf(mPlayerList.get(position).getTime()));
        }

        return listItemView;

    }

}
