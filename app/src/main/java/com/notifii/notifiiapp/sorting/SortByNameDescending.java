/////////////////////////////////////////////////////////////////
// SortByNameDescending.java
//
// Created by Annapoorna
// Notifii Project
//
//Copyright (c) 2016 Notifii, LLC. All rights reserved
/////////////////////////////////////////////////////////////////
package com.notifii.notifiiapp.sorting;


import com.notifii.notifiiapp.models.Package;

import java.util.Comparator;

/*
 *  This class deals with Descending Sorting for Name*/
public class SortByNameDescending implements Comparator<Package>
{
    @Override
    public int compare(Package p1, Package p2)
    {
        return p2.getRecipientName().compareToIgnoreCase(p1.getRecipientName());
    }
}
