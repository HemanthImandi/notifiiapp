/////////////////////////////////////////////////////////////////
// NotifiiApplication.java
//
// Created by Annapoorna
// Notifii Project
//
//Copyright (c) 2016 Notifii, LLC. All rights reserved
/////////////////////////////////////////////////////////////////
package com.notifii.notifiiapp.base;

import android.app.Application;
import android.graphics.Bitmap;
import android.os.StrictMode;

import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Rfc3339DateJsonAdapter;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;


/*
 *  This was the Application class for Notifii.
 */
public class NotifiiApplication extends Application implements NTF_Constants {

    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }


    public static Moshi getMoshi() {
        return new Moshi.Builder().add(Date.class, new Rfc3339DateJsonAdapter()).build();
    }
}