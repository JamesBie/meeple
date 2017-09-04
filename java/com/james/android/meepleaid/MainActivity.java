package com.james.android.meepleaid;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;


public class MainActivity extends AppCompatActivity {

    public static InterstitialAd mInterstitialAd;
public final static String SAMPLE_BGG_COLLECTION = "<items totalitems=\"4\" termsofuse=\"http://boardgamegeek.com/xmlapi/termsofuse\" pubdate=\"Mon, 31 Jul 2017 23:34:16 +0000\"><item objecttype=\"thing\" objectid=\"150376\" subtype=\"boardgame\" collid=\"44008471\"><name sortindex=\"1\">Dead of Winter: A Crossroads Game</name><yearpublished>2014</yearpublished><image>https://cf.geekdo-images.com/images/pic3016500.jpg</image><thumbnail>https://cf.geekdo-images.com/images/pic3016500_t.jpg</thumbnail><status own=\"1\" prevowned=\"0\" fortrade=\"1\" want=\"0\" wanttoplay=\"0\" wanttobuy=\"0\" wishlist=\"0\" preordered=\"0\" lastmodified=\"2017-07-23 23:27:01\"/><numplays>0</numplays><comment>Played Once</comment><conditiontext>Used</conditiontext></item><item objecttype=\"thing\" objectid=\"174430\" subtype=\"boardgame\" collid=\"40764218\"><name sortindex=\"1\">Gloomhaven</name><yearpublished>2017</yearpublished><image>https://cf.geekdo-images.com/images/pic2437871.jpg</image><thumbnail>https://cf.geekdo-images.com/images/pic2437871_t.jpg</thumbnail><status own=\"1\" prevowned=\"0\" fortrade=\"0\" want=\"0\" wanttoplay=\"0\" wanttobuy=\"0\" wishlist=\"0\" preordered=\"0\" lastmodified=\"2017-02-24 00:47:57\"/><numplays>0</numplays></item><item objecttype=\"thing\" objectid=\"122515\" subtype=\"boardgame\" collid=\"44008464\"><name sortindex=\"1\">Keyflower</name><yearpublished>2012</yearpublished><image>https://cf.geekdo-images.com/images/pic2278942.jpg</image><thumbnail>https://cf.geekdo-images.com/images/pic2278942_t.jpg</thumbnail><status own=\"1\" prevowned=\"0\" fortrade=\"1\" want=\"0\" wanttoplay=\"0\" wanttobuy=\"0\" wishlist=\"0\" preordered=\"0\" lastmodified=\"2017-07-23 23:26:32\"/><numplays>0</numplays><comment>Played Once</comment><conditiontext>Used</conditiontext></item><item objecttype=\"thing\" objectid=\"182028\" subtype=\"boardgame\" collid=\"37352260\"><name sortindex=\"1\">Through the Ages: A New Story of Civilization</name><yearpublished>2015</yearpublished><image>https://cf.geekdo-images.com/images/pic2663291.jpg</image><thumbnail>https://cf.geekdo-images.com/images/pic2663291_t.jpg</thumbnail><status own=\"1\" prevowned=\"0\" fortrade=\"1\" want=\"0\" wanttoplay=\"0\" wanttobuy=\"0\" wishlist=\"0\" preordered=\"0\" lastmodified=\"2016-10-08 15:03:57\"/><numplays>0</numplays><conditiontext>Like New, 2 plays</conditiontext></item></items>";
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize ads
        MobileAds.initialize(this, "ca-app-pub-2679874130351383~6653555145");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-2679874130351383/5216172786"); //real ad
        //mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); //test ad
        mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("F2B4DE8687A75F51C498DA9D54CB3628").build());
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("F2B4DE8687A75F51C498DA9D54CB3628").build();
        mAdView.loadAd(adRequest);


       final PCDBHelper mPCDbHelper = new PCDBHelper(this);
        TextView setUsername = (TextView) findViewById(R.id.set_username_textview);
        setUsername.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {

                Intent setUsernameIntent = new Intent(MainActivity.this, UsernameActivity.class);
                startActivity(setUsernameIntent);
            }
        });

        TextView setBgCollectionDetails = (TextView) findViewById(R.id.collection_detail_textview);
        setBgCollectionDetails.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {

                Intent setBgCollectionDetailsIntent = new Intent(MainActivity.this, BgCollectionActivity.class);
                Log.v("Mainactivity", "Initializing intent for bg");
                startActivity(setBgCollectionDetailsIntent);
            }
        });

        TextView startGameCounter = (TextView) findViewById(R.id.point_tracker);
        startGameCounter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                SQLiteDatabase pcDatabase = mPCDbHelper.getReadableDatabase();
                String count = "SELECT count (*) FROM " + PCContract.PCEntry.TABLE_NAME;
                Cursor mCursor;

                boolean tableexist = true;
                try {
                    mCursor = pcDatabase.rawQuery(count, null);

                }// try to query table
                catch (Exception e) {tableexist = false; mCursor=null;}//if you cant query table b/c it doesnt exit create a new table

                if (tableexist&&mCursor!= null) {
                    Intent startGameCounterIntent = new Intent(MainActivity.this, ResumeGameActivity.class);
                    startActivity(startGameCounterIntent);

                } else {
                    Intent startGameCounterIntent = new Intent(MainActivity.this, PlayerCountOverview.class);
                    startActivity(startGameCounterIntent);
                }



            }
        });
    }
}
