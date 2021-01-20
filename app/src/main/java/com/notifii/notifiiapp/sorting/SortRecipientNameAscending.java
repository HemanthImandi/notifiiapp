package com.notifii.notifiiapp.sorting;


import com.notifii.notifiiapp.models.Recipient;

import java.util.Comparator;

public class SortRecipientNameAscending  implements Comparator<Recipient>
{
    @Override
    public int compare(Recipient p1, Recipient p2)
    {
        return p1.getFullName().compareToIgnoreCase(p2.getFullName());

    }
}