/////////////////////////////////////////////////////////////////
// SortBySenderAscending.java
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
 *  This class deals with Ascending Sorting for Sender
 */
public class SortBySenderAscending implements Comparator<Package> {
    @Override
    public int compare(com.notifii.notifiiapp.models.Package p1, Package p2)
    {
        return p1.getSender().compareToIgnoreCase(p2.getSender());
        /*int res = String.CASE_INSENSITIVE_ORDER.compare(p1.getSender(), p2.getSender());
        if (res == 0) {
            res = p1.getSender().compareTo(p2.getSender());
        }
        return res;*/
    }
}
