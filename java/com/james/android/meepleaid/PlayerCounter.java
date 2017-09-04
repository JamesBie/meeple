package com.james.android.meepleaid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by 100599223 on 8/13/2017.
 */

public class PlayerCounter extends FragmentActivity {
int mPlayerCount;



    /**
     * the pager widget which handles animation and allows
     * swipping horizontally
     * to access previous and next pages
     * */

    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);
        int Num= getIntent().getExtras().getInt("playerToLoad",0);


        SharedPreferences pref = getSharedPreferences("UserData", MODE_PRIVATE);
        String mTotalPlayers = pref.getString("totalplayers","");
        if (!mTotalPlayers.isEmpty()){
            mPlayerCount = Integer.valueOf(mTotalPlayers);
        }else {
            mPlayerCount=0;
        }


        //bundle to pass number of totla fragments made
        //Instantiate a ViewPager and PagerAdapter

        mPager = (ViewPager) findViewById(R.id.container);
        mPagerAdapter = new PlayerCounterPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(Num);


    }
@Override
    public void onBackPressed(){

            super.onBackPressed();


    }

    /**
     * a simple pager adapter that represnts number of screenslidespagefragment objects
     * in sequence
     */

    private class PlayerCounterPagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter {
        public PlayerCounterPagerAdapter(android.support.v4.app.FragmentManager fm){
            super(fm);
        }


        @Override
        public android.support.v4.app.Fragment getItem(int position ) {
            return PlayerCounterFragment.create(position, mPlayerCount);
        }

        public int getCount(){
            return mPlayerCount;
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.player_counter_menu, menu);

        menu.findItem(R.id.action_previous).setEnabled(mPager.getCurrentItem() > 0);

        // Add either a "next" or "finish" button to the action bar, depending on which page
        // is currently selected.
        MenuItem item = menu.add(Menu.NONE, R.id.action_next, Menu.NONE,
                (mPager.getCurrentItem() == mPagerAdapter.getCount() - 1)
                        ? "Finished"
                        : "Next");
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Navigate "up" the demo structure to the launchpad activity.
                // See http://developer.android.com/design/patterns/navigation.html for more.
                NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
                return true;

            case R.id.action_previous:
                // Go to the previous step in the wizard. If there is no previous step,
                // setCurrentItem will do nothing.
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
                return true;

            case R.id.action_next:
                // Advance to the next step in the wizard. If there is no next step, setCurrentItem
                // will do nothing.
                mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
