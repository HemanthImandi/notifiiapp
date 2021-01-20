package com.notifii.notifiiapp.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.notifii.notifiiapp.utils.NTF_Constants;

import java.util.Set;

public class NTF_PrefsManager implements NTF_Constants {

    private Context context;
    private SharedPreferences prefsManager;

    public NTF_PrefsManager(Context applicationContext) {
        this.context = applicationContext;
        this.prefsManager = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /*For saving the String value*/
    public void save(String key, String value) {
        if (key.equals(Prefs_Keys.DEFAULT_MAILROOM_ID)){
            int i=0;
        }
        try {
            SharedPreferences.Editor editor = prefsManager.edit();
            editor.putString(key, value);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean contains(String key) {
        return prefsManager.contains(key);
    }

    public void save(String key, Set<String> value) {
        try {
            SharedPreferences.Editor editor = prefsManager.edit();
            editor.putStringSet(key, value);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*For saving the Integer value*/
    public void save(String key, int value) {
        try {
            SharedPreferences.Editor editor = prefsManager.edit();
            editor.putInt(key, value);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*For saving the Long value*/
    public void save(String key, long value) {
        try {
            SharedPreferences.Editor editor = prefsManager.edit();
            editor.putLong(key, value);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*For saving the boolean value*/
    public void save(String key, boolean value) {
        try {
            SharedPreferences.Editor editor = prefsManager.edit();
            editor.putBoolean(key, value);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*This method deals with getting the values saved in this SharedPreferences*/
    public String get(String... key) {
        if (key.length == 1) {
            return prefsManager.getString(key[0], "");
        } else {
            return prefsManager.getString(key[0], key[1]);
        }
    }

    public void clearAllPrefs() {
        prefsManager.edit().clear().commit();
    }

    public boolean hasKey(String key) {
        return prefsManager.contains(key);
    }

    public void removeKey(String key) {
        SharedPreferences.Editor editor = prefsManager.edit();
        editor.remove(key);
        editor.commit();
    }

    public boolean getBoolean(String key) {
        return prefsManager.getBoolean(key, false);
    }

    public static String getString(String name, Context context) {
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());
        return settings.getString(name.toString(), "");
    }

}

