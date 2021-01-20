package com.notifii.notifiiapp.sorting;

import com.notifii.notifiiapp.models.Package;

import java.util.Comparator;

public class SortByAddressDesc implements Comparator<Package>
{
    @Override
    public int compare(Package p1, Package p2)
    {
        return p2.getRecipientAddress1().compareToIgnoreCase(p1.getRecipientAddress1());
    }
}
