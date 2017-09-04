package com.james.android.meepleaid;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;

import static com.james.android.meepleaid.MainActivity.mInterstitialAd;

/**
 * Created by 100599223 on 8/25/2017.
 */

public class FinishGame extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finish_game);

        final Intent returnToMain = new Intent(FinishGame.this, MainActivity.class);
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
            mInterstitialAd.setAdListener(new AdListener(){
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("F2B4DE8687A75F51C498DA9D54CB3628").build());
                    startActivity(returnToMain);
                }

                @Override
                public void onAdLeftApplication() {
                    super.onAdLeftApplication();
                    mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("F2B4DE8687A75F51C498DA9D54CB3628").build());
                    startActivity(returnToMain);
                }
            });
        }
        else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");

            startActivity(returnToMain);
        }






    }
}
