package com.james.android.meepleaid;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 100599223 on 8/7/2017.
 */

public class BoardGameAdapter extends ArrayAdapter<BoardGame> implements Filterable {
    private ArrayList<BoardGame> originalData=null;
    private ArrayList<BoardGame> filteredData=null;
    private ArrayList<BoardGame> filteredNumber = null;
    private LayoutInflater mInflater;
    private ItemFilter mFilter = new ItemFilter();
    private PlayerFilter mPlayerFilter = new PlayerFilter();
    private boolean mFromNameEditText;


    public long getItemId(int position){
        return position;
    }

    public int getCount(){
        return filteredData==null?0:filteredData.size();
    }


    public BoardGame getItem(int position) {
        if(filteredData==null){
            Log.v("filter Log", "null filtered data");
            return null;
        }else{
        return filteredData.get(position);}}


    public BoardGameAdapter(Context context, ArrayList<BoardGame> boardGames){
        super(context, 0 , boardGames);
        this.filteredData = boardGames;
        this.originalData = boardGames;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent){

        //checks if the exisitng view is being reused. if not inflate it
        View listItemView = convertView;
        if (listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,false);
        }


        //find the TextView in the list_item.xml layout with the id mboardgame text view
        TextView boardgameTitle = (TextView) listItemView.findViewById(R.id.bgTitleTextView);
        boardgameTitle.setText(filteredData.get(position).getTitle());

        TextView boardgameMin = (TextView) listItemView.findViewById(R.id.minPlayerTextView);
        boardgameMin.setText(String.valueOf(filteredData.get(position).getMinPlayer()));

        TextView boardgameMax = (TextView) listItemView.findViewById(R.id.maxPlayerTextView);
        boardgameMax.setText(String.valueOf(filteredData.get(position).getmMaxPlayer()));


        //return the whole list item layout containing the 3 textview so it can be shown in the list view
        return listItemView;

    }


    public Filter getFilter() {

        return mFilter;
    }

    public Filter getFilter(boolean player){

       return mPlayerFilter ;

    }

    private class ItemFilter extends Filter {


        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            final List<BoardGame> list = originalData;
            int count = list.size();
            final ArrayList<BoardGame> nlist = new ArrayList<BoardGame>(count);

            String filterableString;

            for (int i = 0; i<count; i++){
                filterableString = list.get(i).getTitle();
                if(filterableString.toLowerCase().contains(filterString)){
                    nlist.add(list.get(i));
                }
            }

            results.values=nlist;
            results.count=nlist.size();

            return results;



        }
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results){
            filteredData = (ArrayList<BoardGame>) results.values;
            notifyDataSetChanged();

        }
    }

    private class PlayerFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString();
            FilterResults results = new FilterResults();
            final List<BoardGame> list = originalData;
            int count = list.size();
            final ArrayList<BoardGame> nlist = new ArrayList<BoardGame>(count);

            int filterableMin;
            int filterableMax;

            if(filterString.isEmpty()){
                results.values=list;
                results.count=list.size();

                return results;
            }

            for (int i = 0; i<count; i++){
                filterableMin = list.get(i).getMinPlayer();
                filterableMax = list.get(i).getmMaxPlayer();
                if((filterableMin<= Integer.parseInt(filterString)) && (Integer.parseInt(filterString) <= filterableMax)){
                    nlist.add(list.get(i));
                }

            }

            results.values=nlist;
            results.count=nlist.size();

            return results;



        }
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results){
            filteredData = (ArrayList<BoardGame>) results.values;
            notifyDataSetChanged();

        }
    }

}
