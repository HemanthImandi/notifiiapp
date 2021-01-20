/////////////////////////////////////////////////////////////////
// NTF_BaseActivity.java
//
// Created by Annapoorna
// Notifii Project
//
//Copyright (c) 2016 Notifii, LLC. All rights reserved
/////////////////////////////////////////////////////////////////
package com.notifii.notifiiapp.base;

import android.os.Bundle;
import android.widget.Toast;

import com.notifii.notifiiapp.helpers.NTF_PrefsManager;
import com.notifii.notifiiapp.utils.NTF_Constants;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This is the Base activity for all Activities..
 */
public class NTF_BaseActivity extends AppCompatActivity implements NTF_Constants {

    public NTF_PrefsManager ntf_Preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ntf_Preferences = new NTF_PrefsManager(this);
    }

    /*Common Method for displaying Toast Message in all Activities*/
    public void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(NTF_BaseActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
