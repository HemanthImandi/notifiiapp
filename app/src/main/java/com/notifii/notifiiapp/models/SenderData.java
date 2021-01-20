/////////////////////////////////////////////////////////////////
// SenderData.java
//
// Created by Annapoorna
// Notifii Project
//
//Copyright (c) 2016 Notifii, LLC. All rights reserved
/////////////////////////////////////////////////////////////////
package com.notifii.notifiiapp.models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 *  This is the model class for Sender Details.
 */
public class SenderData {

    private String sender;

    public String getSender() {
        return sender;
    }

    public void setSender(String name) {
        this.sender = name;
    }

    @Override
    public String toString() {
        return sender.toString();
    }

    // This method deals with the Response parsing of SenderList.
    public static ArrayList<SenderData> getSenderList(JSONArray jsonArray) {

        ArrayList<SenderData> list = new ArrayList<>();

        try {
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0, len = jsonArray.length(); i < len; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    SenderData senderData = new SenderData();
                    senderData.setSender(jsonObject.optString("sender"));
                    list.add(senderData);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
