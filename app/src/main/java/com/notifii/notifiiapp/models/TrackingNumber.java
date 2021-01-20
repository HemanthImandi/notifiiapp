package com.notifii.notifiiapp.models;

import java.io.Serializable;

/**
 * Created by Administrator on 8/22/2017.
 */

public class TrackingNumber implements Serializable {

    String trackingNumber = "";
    boolean firstScanConpleted = true;

    public boolean isFirstScanConpleted() {
        return firstScanConpleted;
    }

    public void setFirstScanConpleted(boolean firstScanConpleted) {
        this.firstScanConpleted = firstScanConpleted;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

}