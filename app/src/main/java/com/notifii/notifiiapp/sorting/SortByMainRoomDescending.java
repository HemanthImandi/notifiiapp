/////////////////////////////////////////////////////////////////
// SortByMainRoomDescending.java
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
 *  This class deals with Descending Sorting for MailRoom
 */
public class SortByMainRoomDescending implements Comparator<Package>
{
    @Override
    public int compare(Package p1, Package p2)
    {
        return p2.getMailroomId().compareToIgnoreCase(p1.getMailroomId());
       /* int res = String.CASE_INSENSITIVE_ORDER.compare(p1.getMailroomId(), p2.getMailroomId());
        if (res == 0) {
            res = p2.getMailroomId().compareTo(p1.getMailroomId());
        }
        return res;*/
    }
}
