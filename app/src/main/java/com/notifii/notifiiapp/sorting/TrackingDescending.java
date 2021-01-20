package com.notifii.notifiiapp.sorting;

import com.notifii.notifiiapp.models.Package;

import java.util.Comparator;

public class TrackingDescending implements Comparator<Package> {
    @Override
    public int compare(Package p1, Package p2) {
        return p2.getTrackingNumber().compareToIgnoreCase(p1.getTrackingNumber());
    }
}