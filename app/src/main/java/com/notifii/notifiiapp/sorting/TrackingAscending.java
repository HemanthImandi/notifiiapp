package com.notifii.notifiiapp.sorting;

import com.notifii.notifiiapp.models.Package;

import java.util.Comparator;

public class TrackingAscending  implements Comparator<Package> {

    @Override
    public int compare(Package p1, Package p2) {
        return p1.getTrackingNumber().compareToIgnoreCase(p2.getTrackingNumber());
    }
}