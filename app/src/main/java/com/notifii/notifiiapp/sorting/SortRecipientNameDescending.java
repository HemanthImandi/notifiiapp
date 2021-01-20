/////////////////////////////////////////////////////////////////
// SortRecipientNameDescending.java
//
// Created by Annapoorna
// Notifii Project
//
//Copyright (c) 2016 Notifii, LLC. All rights reserved
/////////////////////////////////////////////////////////////////
package com.notifii.notifiiapp.sorting;

import com.notifii.notifiiapp.models.Recipient;

import java.util.Comparator;

/**
 *  This class deals with Descending Sorting for RecipientName
 */
public class SortRecipientNameDescending implements Comparator<Recipient>
{
    @Override
    public int compare(Recipient p1, Recipient p2)
    {
        return p2.getFullName().compareToIgnoreCase(p1.getFullName());
        /*int res = String.CASE_INSENSITIVE_ORDER.compare(p1.getFullName(), p2.getFullName());
        if (res == 0) {
            res = p2.getFullName().compareTo(p1.getFullName());
        }
        return res;*/
    }
}
