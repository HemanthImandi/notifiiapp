package com.notifii.notifiiapp.sorting;

import com.notifii.notifiiapp.models.Package;

import java.util.Comparator;

public class SortByAddressAsc implements Comparator<Package> {
    @Override
    public int compare(Package p1, Package p2) {

        return p1.getRecipientAddress1().compareToIgnoreCase(p2.getRecipientAddress1());

    }
}
