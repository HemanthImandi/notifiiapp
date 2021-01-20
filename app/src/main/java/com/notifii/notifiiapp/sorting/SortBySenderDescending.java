/////////////////////////////////////////////////////////////////
// SortBySenderDescending.java
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
 *  This class deals with Descending Sorting for Sender
 */
public class SortBySenderDescending implements Comparator<Package>
{
    @Override
    public int compare(Package p1, Package p2)
    {
        return p2.getSender().compareToIgnoreCase(p1.getSender());
       /* int res = String.CASE_INSENSITIVE_ORDER.compare(p1.getSender(), p2.getSender());
        if (res == 0) {
            res = p2.getSender().compareTo(p1.getSender());
        }
        return res;*/
    }
}
