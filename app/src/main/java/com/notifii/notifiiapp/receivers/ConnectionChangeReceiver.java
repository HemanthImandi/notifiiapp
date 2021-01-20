package com.notifii.notifiiapp.receivers;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.utils.NTF_Utils;

/**
 * This class deals with the Network Changes. i.e., it is in online or offline
 */
public class ConnectionChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (NTF_Utils.isOnline(context)) {
//            NTF_Utils.showAlert(context,"","You are not connected to the Internet.",null);
        }
    }
}

