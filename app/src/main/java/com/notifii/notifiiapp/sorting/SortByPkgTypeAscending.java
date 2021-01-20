/////////////////////////////////////////////////////////////////
// SortByPkgTypeAscending.java
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
 *  This class deals with Ascending Sorting for PackageType
 */
public class SortByPkgTypeAscending implements Comparator<Package>
{
    @Override
    public int compare(Package p1, Package p2)
    {
        return p1.getPackageType().compareToIgnoreCase(p2.getPackageType());
        /*int res = String.CASE_INSENSITIVE_ORDER.compare(p1.getPackageType(), p2.getPackageType());
        if (res == 0) {
            res = p1.getPackageType().compareTo(p2.getPackageType());
        }
        return res;*/
    }
}
