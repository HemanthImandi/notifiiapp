/////////////////////////////////////////////////////////////////
// SortByDateAscending.java
//
// Created by Annapoorna
// Notifii Project
//
//Copyright (c) 2016 Notifii, LLC. All rights reserved
/////////////////////////////////////////////////////////////////
package com.notifii.notifiiapp.sorting;

import com.notifii.notifiiapp.models.Package;

import java.util.Comparator;

/**
 *  This class deals with Ascending Sorting for Date
 */
public class SortByDateAscending implements Comparator<Package>
{
    @Override
    public int compare(Package p1, Package p2)
    {
        if (p1.getDateReceived() == null && p2.getDateReceived() == null) {
            return 0;
        }
        if (p1.getDateReceived() == null) {
            return 1;
        }
        if (p2.getDateReceived() == null) {
            return -1;
        }
        return p2.getDateReceived().compareTo(p1.getDateReceived());
    }
}
