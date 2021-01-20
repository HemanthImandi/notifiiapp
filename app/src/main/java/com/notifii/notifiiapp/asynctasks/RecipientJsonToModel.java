package com.notifii.notifiiapp.asynctasks;

import android.os.AsyncTask;
import com.notifii.notifiiapp.models.Recipient;

public class RecipientJsonToModel extends AsyncTask<Void, Recipient, Void> {

    Runnable bgRun, UIRun;

    public RecipientJsonToModel(Runnable bgRun, Runnable UIRun) {
        this.bgRun = bgRun;
        this.UIRun = UIRun;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... jsonObjects) {
        if (bgRun != null)
            bgRun.run();
        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        if (UIRun != null)
            UIRun.run();
    }
}
