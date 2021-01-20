package com.notifii.notifiiapp.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.notifii.notifiiapp.helpers.SingleTon;
import com.notifii.notifiiapp.models.Recipient;
import com.notifii.notifiiapp.utils.NTF_Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DataFetching extends AsyncTask<Void, Void, Void> {

    private ArrayList<Recipient> mRecipientArrayList;
    Context context;

    public DataFetching(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            JSONObject recipientJsonObj = NTF_Utils.getRecipientsData(context);
            if (recipientJsonObj != null) {
                mRecipientArrayList = Recipient.getRecipientsList(recipientJsonObj.getJSONArray("recipients"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        if (mRecipientArrayList != null && !mRecipientArrayList.isEmpty()) {
            SingleTon.getInstance().setmRecipientListForOcr(mRecipientArrayList);
        }
    }
}
