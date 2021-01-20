/////////////////////////////////////////////////////////////////
// ShelfData.java
//
// Created by Annapoorna
// Notifii Project
//
//Copyright (c) 2016 Notifii, LLC. All rights reserved
/////////////////////////////////////////////////////////////////
package com.notifii.notifiiapp.models;

import com.notifii.notifiiapp.sorting.SortUserName;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 *  This is the model class for Shelf Details.
 */

public class ShelfData {

    private String shelfDetails;

    public String getShelfDetails() {
        return shelfDetails;
    }

    public void setShelfDetails(String name) {
        this.shelfDetails = name;
    }

    @Override
    public String toString() {
        return shelfDetails.toString();
    }

    // This method deals with Response Parsing of Shelf Details.
    public static ArrayList<ShelfData> getShelfDetailsList(JSONArray jsonArray) {

        ArrayList<ShelfData> list = null;

        try {
            if (jsonArray != null && jsonArray.length() > 0) {

                list = new ArrayList<>();

                for (int i = 0, len = jsonArray.length(); i < len; i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    ShelfData shelfData = new ShelfData();
                    shelfData.setShelfDetails(jsonObject.optString("shelf"));
                    list.add(shelfData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static ArrayList<ShelfData> getUserNames(String[] loginNameArr){
        ArrayList<String> names=new ArrayList<>(Arrays.asList(loginNameArr));
        Collections.sort(names,new SortUserName());
        ArrayList<ShelfData> usernames=new ArrayList<>();
        for (String un:names){
            ShelfData sd=new ShelfData();
            sd.setShelfDetails(un);
            usernames.add(sd);
        }
        return usernames;
    }
}
