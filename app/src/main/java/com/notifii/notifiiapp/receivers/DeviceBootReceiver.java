package com.notifii.notifiiapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.notifii.notifiiapp.helpers.NTF_PrefsManager;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;


public class DeviceBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"DeviceBootReceiverTriggered",Toast.LENGTH_LONG).show();
        Log.d("switched_on", "deviceBoot" );
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) || intent.getAction().equals("android.intent.action.QUICKBOOT_POWERON")){
            Log.d("switched_on", "deviceBootReceiver triggered" );
            NTF_PrefsManager spManager = new NTF_PrefsManager(context);
            //set the alarm if it was set before mobile switched off.
            if(spManager.hasKey(NTF_Constants.Prefs_Keys.IS_ALARM_RUNNING)) {
                NTF_Utils.cancelAlarm(context);
                NTF_Utils.startAlarmIfRequired(context);
            } else {
                //this is not required... but let it be.
                NTF_Utils.cancelAlarm(context);
            }
        }
    }
}