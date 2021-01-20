package com.notifii.notifiiapp.events;



import com.notifii.notifiiapp.models.Recipient;

import java.util.ArrayList;

/**
 * Created by Administrator on 8/2/2016.
 */
public class RefreshRecipientList {
    public ArrayList<Recipient> dataset;
    public RefreshRecipientList(ArrayList<Recipient> dataset) {
        this.dataset = dataset;
    }
}
