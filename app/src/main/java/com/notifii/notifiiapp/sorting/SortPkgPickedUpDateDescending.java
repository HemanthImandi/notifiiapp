/////////////////////////////////////////////////////////////////
// SortPkgPickedUpDateDescending.java
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
 *  This class deals with Descending Sorting for PickupDate
 */
public class SortPkgPickedUpDateDescending implements Comparator<Package>
{
    @Override
    public int compare(Package p1, Package p2)
    {
        if (p1.getDatePickedup() == null && p2.getDatePickedup() == null) {
            return 0;
        }
        if (p1.getDatePickedup() == null) {
            return -1;
        }
        if (p2.getDatePickedup() == null) {
            return 1;
        }
        return p1.getDatePickedup().compareTo(p2.getDatePickedup());
    }
}