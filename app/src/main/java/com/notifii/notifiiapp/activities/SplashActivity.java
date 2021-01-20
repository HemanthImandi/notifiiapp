package com.notifii.notifiiapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.asynctasks.DataFetching;
import com.notifii.notifiiapp.base.NTF_BaseActivity;
import com.notifii.notifiiapp.helpers.SingleTon;
import com.notifii.notifiiapp.utils.NTF_Utils;
import org.json.JSONArray;

public class SplashActivity extends NTF_BaseActivity {

    private final int _splashTime = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SingleTon.getInstance().setOcrArray(new JSONArray());

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("folder_type", "" + getResources().getDimension(R.dimen.folder_type));
        new DataFetching(getApplicationContext()).execute();
        startSplashing();
    }

    // Setting time for SplashScreen
    private void startSplashing() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ntf_Preferences.contains(Prefs_Keys.IS_KIOSK_MODE) && ntf_Preferences.getBoolean(Prefs_Keys.IS_KIOSK_MODE)) {
                    NTF_Utils.startAlarmIfRequired(SplashActivity.this);
                    startActivity(new Intent(SplashActivity.this, IdentifyUserActivity.class));
                } else if (ntf_Preferences.getBoolean(Prefs_Keys.IS_LOGGED_IN)) {
                    if (!ntf_Preferences.hasKey(Prefs_Keys.HAS_LOGIN)) {
                        ntf_Preferences.save(Prefs_Keys.HAS_LOGIN, "YES");
                    }
                    NTF_Utils.startAlarmIfRequired(SplashActivity.this);
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                } else {
                    Intent loginActivityIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(loginActivityIntent);
                }
                finish();
            }
        }, _splashTime);
    }


}

