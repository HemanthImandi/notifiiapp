/////////////////////////////////////////////////////////////////
// SortByTagNumberAscending.java
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
 *  This class deals with Ascending Sorting for TagNumber
 */
public class SortByTagNumberAscending implements Comparator<Package>
{
    @Override
    public int compare(Package p1, Package p2)
    {
        //boolean comapre =  Integer.parseInt(p1.getTagNumber()) > (Integer.parseInt(p2.getTagNumber()));
        if (!p1.getTagNumber().isEmpty() && !p1.getTagNumber().equals("null") && !p2.getTagNumber().isEmpty()&& !p2.getTagNumber().equals("null")) {
            return Integer.parseInt(p1.getTagNumber()) < Integer.parseInt(p2.getTagNumber()) ? -1 : Integer.parseInt(p1.getTagNumber()) == Integer.parseInt(p2.getTagNumber()) ? 0 : 1;
        }else {
             return p2.getTagNumber().compareToIgnoreCase(p1.getTagNumber());
        }
    }
}
