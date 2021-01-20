/////////////////////////////////////////////////////////////////
// SortByShelfDescending.java
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
 *  This class deals with Descending Sorting for Shelf
 */
public class SortByShelfDescending implements Comparator<Package>
{
    @Override
    public int compare(Package p1, Package p2)
    {
        return p2.getShelf().compareToIgnoreCase(p1.getShelf());
       /* int res = String.CASE_INSENSITIVE_ORDER.compare(p1.getShelf(), p2.getShelf());
        if (res == 0) {
            res = p2.getShelf().compareTo(p1.getShelf());
        }
        return res;*/
    }
}
