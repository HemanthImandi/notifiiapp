package com.notifii.notifiiapp.sorting;

import java.util.Comparator;

public class SortUserName implements Comparator<String>
{
    @Override
    public int compare(String p1, String p2)
    {
        return p1.compareToIgnoreCase(p2);

    }
}
