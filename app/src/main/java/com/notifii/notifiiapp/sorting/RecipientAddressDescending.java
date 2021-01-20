package com.notifii.notifiiapp.sorting;

import com.notifii.notifiiapp.models.Recipient;

import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;

public class RecipientAddressDescending implements Comparator<Recipient> {
    @Override
    public int compare(Recipient p1, Recipient p2) {
        try {
            String unitnumber1 = p1.getAddress1() != null ? p1.getAddress1() : "";
            String unitnumber2 = p2.getAddress1() != null ? p2.getAddress1() : "";
            if (unitnumber1.isEmpty()) {
                return 1;
            } else if (unitnumber2.isEmpty()) {
                return -1;
            } else if (StringUtils.isNumeric(String.valueOf(unitnumber1.charAt(0)))&&StringUtils.isNumeric(String.valueOf(unitnumber2.charAt(0)))) {
                return unitnumber2.compareToIgnoreCase(unitnumber1);
            } else if (StringUtils.isNumeric(String.valueOf(unitnumber1.charAt(0)))) {
                return -1;
            } else if (StringUtils.isNumeric(String.valueOf(unitnumber2.charAt(0)))) {
                return 1;
            } else {
                return unitnumber2.compareToIgnoreCase(unitnumber1);
            }
        } catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }
}