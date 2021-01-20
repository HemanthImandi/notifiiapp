/////////////////////////////////////////////////////////////////
// SortByMainRoomAscending.java
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
 *  This class deals with Ascending Sorting for MailRoom
 */
public class SortByMainRoomAscending implements Comparator<Package>
{
    @Override
    public int compare(Package p1, Package p2)
    {
        return p1.getMailroomId().compareToIgnoreCase(p2.getMailroomId());
       /* int res = String.CASE_INSENSITIVE_ORDER.compare(p1.getMailroomId(), p2.getMailroomId());
        if (res == 0) {
            res = p1.getMailroomId().compareTo(p2.getMailroomId());
        }
        return res;*/
    }
}
