package com.notifii.notifiiapp.sorting;

import com.notifii.notifiiapp.models.Package;

import java.util.Comparator;

public class UnitNumberAscending  implements Comparator<Package> {
    @Override
    public int compare(Package p1, Package p2) {
        String unitnumber1=p1.getRecipientAddress1()!=null?p1.getRecipientAddress1():"";
        String unitnumber2=p2.getRecipientAddress1()!=null?p2.getRecipientAddress1():"";
        if (unitnumber1.length()==unitnumber2.length())
            return unitnumber1.compareToIgnoreCase(unitnumber2);
        else if (unitnumber1.length()>unitnumber2.length())
            return 1;
        else
            return -1;
    }
}
