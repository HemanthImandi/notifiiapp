/////////////////////////////////////////////////////////////////
// Package.java
//
// Created by Annapoorna
// Notifii Project
//
//Copyright (c) 2016 Notifii, LLC. All rights reserved
/////////////////////////////////////////////////////////////////
package com.notifii.notifiiapp.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;
import android.util.Log;

import com.notifii.notifiiapp.utils.NTF_Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * This is the model class for All details of Package.
 */
public class Package implements Parcelable {

    private static int count = 0;
    private static int imagesTotalCount = 0;
    private String packageId;
    private String trackingNumber;
    private Date dateReceived;

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    private String nextPageToken;

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    private String carrier;

    public String getCustomMessage() {
        return customMessage;
    }

    public void setCustomMessage(String customMessage) {
        this.customMessage = customMessage;
    }

    public String getStaffNotes() {
        return staffNotes;
    }

    public void setStaffNotes(String staffNotes) {
        this.staffNotes = staffNotes;
    }

    public String getChangeHistory() {
        return changeHistory;
    }

    public String getSpecialTrackInstructions() {
        return specialTrackInstructions;
    }

    public void setSpecialTrackInstructions(String specialTrackInstructions) {
        this.specialTrackInstructions = specialTrackInstructions;
    }

    public String getVacation_message() {
        return vacation_message;
    }

    public void setVacation_message(String vacation_message) {
        this.vacation_message = vacation_message;
    }

    public void setChangeHistory(String changeHistory) {

        this.changeHistory = changeHistory;
    }

    private String customMessage;
    private String staffNotes;
    private String changeHistory;
    private String specialTrackInstructions;
    private String vacation_message;

    public Date getDatePickedup() {
        return datePickedUp;
    }

    public void setDatePickedUp(Date datePickedUp) {
        this.datePickedUp = datePickedUp;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    private Date datePickedUp;
    private String recipientName;
    private String recipientAddress1;
    private String mailroomId;

    public String getPendingPackagesLimit() {
        return pendingPackagesLimit;
    }

    public void setPendingPackagesLimit(String pendingPackagesLimit) {
        this.pendingPackagesLimit = pendingPackagesLimit;
    }

    private String shelf;
    private String poNumber;
    private String pendingPackagesLimit;

    public String getLoginPictures() {
        return loginPictures;
    }

    public void setLoginPictures(String loginPictures) {
        this.loginPictures = loginPictures;
    }

    private String sender;
    private String tagNumber;
    private String packageType;
    private ArrayList<String> loginPicturesList = new ArrayList<>();

    public ArrayList<String> getLoginPicturesList() {
        return loginPicturesList;
    }

    public void setLoginPicturesList(ArrayList<String> loginPicturesList) {
        this.loginPicturesList = loginPicturesList;
    }

    private String loginPictures;

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    private boolean isChecked;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public Date getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(Date dateReceived) {
        this.dateReceived = dateReceived;
    }

    public String getRecipientName() {
        return Html.fromHtml(recipientName).toString();
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientAddress1() {
        return recipientAddress1;
    }

    public void setRecipientAddress1(String recipientAddress1) {
        this.recipientAddress1 = recipientAddress1;
    }

    public String getMailroomId() {
        return mailroomId == null ? "" : mailroomId;
    }

    public void setMailroomId(String mailroomId) {
        this.mailroomId = mailroomId;
    }

    public String getShelf() {
        return shelf;
    }

    public void setShelf(String shelf) {
        this.shelf = shelf;
    }

    public String getTagNumber() {
        return tagNumber;
    }

    public void setTagNumber(String tagNumber) {
        this.tagNumber = tagNumber;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String package_type) {
        this.packageType = package_type;
    }

    public static ArrayList<Package> getPackagesPending(JSONArray jsonArray) {
        count = 0;
        imagesTotalCount = 0;
        ArrayList<Package> mPakgList=new ArrayList<>();
        try {
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    Calendar calendar = Calendar.getInstance();
                    Log.v("Notifii Time  :", "Out Adapter Time : " + calendar.getTime() + " Pending Packages List : " + jsonArray.length());
                    Package objPackage = new Package();
                    JSONObject pakageJsonObj = jsonArray.getJSONObject(i);
                    objPackage.setPackageId(pakageJsonObj.optString("package_id"));
                    objPackage.setTrackingNumber(pakageJsonObj.optString("tracking_number"));
                    objPackage.setDateReceived(NTF_Utils.getDate(pakageJsonObj.optString("date_received")));
                    objPackage.setDatePickedUp(NTF_Utils.getDate(pakageJsonObj.optString("date_pickedup")));
                    objPackage.setRecipientName(pakageJsonObj.optString("recipient_name"));
                    objPackage.setRecipientAddress1(pakageJsonObj.optString("recipient_address1"));
                    objPackage.setIsChecked(false);
                    objPackage.setTagNumber(pakageJsonObj.optString("tag_number"));
                    objPackage.setShelf(pakageJsonObj.optString("shelf"));
                    objPackage.setMailroomId(pakageJsonObj.optString("mailroom_id"));
                    objPackage.setPackageType(pakageJsonObj.optString("package_type"));
                    objPackage.setSender(pakageJsonObj.optString("sender"));
                    objPackage.setCustomMessage(pakageJsonObj.optString("custom_message"));
                    objPackage.setStaffNotes(pakageJsonObj.optString("staff_note"));
                    objPackage.setChangeHistory(pakageJsonObj.optString("change_history"));
                    objPackage.setSpecialTrackInstructions(pakageJsonObj.optString("special_track_instructions"));
                    objPackage.setVacation_message(pakageJsonObj.optString("vacation_message"));
                    try {
                        JSONArray loginImagesJsonArray = pakageJsonObj.optJSONObject("package_images").optJSONArray("login_images");
                        if (loginImagesJsonArray.length() > 0) {
                            count++;
                        }
                        if (loginImagesJsonArray != null && loginImagesJsonArray.length() > 0) {
                            ArrayList<String> images = new ArrayList<>();
                            for (int j = 0, noofimages = loginImagesJsonArray.length(); j < noofimages; j++) {
                                imagesTotalCount++;
                                images.add(String.valueOf(loginImagesJsonArray.get(j)));
                            }
                            objPackage.setLoginPicturesList(images);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mPakgList.add(objPackage);

                }
            }
            return mPakgList;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return mPakgList;
        }
    }

    // This method deals with response parsing of PackagesPending Service
    public static ArrayList<Package> getPackagesPending(JSONArray jsonArray, boolean showImages, ArrayList<Package> mPakgList) {
        count = 0;
        imagesTotalCount = 0;

        try {
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    Calendar calendar = Calendar.getInstance();
                    Log.v("Notifii Time  :", "Out Adapter Time : " + calendar.getTime() + " Pending Packages List : " + jsonArray.length());
                    Package objPackage = new Package();
                    JSONObject pakageJsonObj = jsonArray.getJSONObject(i);
                    objPackage.setPackageId(pakageJsonObj.optString("package_id"));
                    objPackage.setTrackingNumber(pakageJsonObj.optString("tracking_number"));
                    //objPackage.setDateReceived(NTF_Utils.getDate(pakageJsonObj.optString("date_received")));
                    //objPackage.setDatePickedUp(NTF_Utils.getDate(pakageJsonObj.optString("date_pickedup")));
                    objPackage.setDateReceived(NTF_Utils.getDate(pakageJsonObj.optString("date_received")));
                    objPackage.setDatePickedUp(NTF_Utils.getDate(pakageJsonObj.optString("date_pickedup")));
                    objPackage.setRecipientName(pakageJsonObj.optString("recipient_name"));
                    objPackage.setRecipientAddress1(pakageJsonObj.optString("recipient_address1"));
                    objPackage.setIsChecked(false);
                    objPackage.setTagNumber(pakageJsonObj.optString("tag_number"));
                    objPackage.setShelf(pakageJsonObj.optString("shelf"));
                    objPackage.setMailroomId(pakageJsonObj.optString("mailroom_id"));
                    objPackage.setPackageType(pakageJsonObj.optString("package_type"));
                    objPackage.setSender(pakageJsonObj.optString("sender"));
                    objPackage.setCustomMessage(pakageJsonObj.optString("custom_message"));
                    objPackage.setStaffNotes(pakageJsonObj.optString("staff_note"));
                    objPackage.setChangeHistory(pakageJsonObj.optString("change_history"));
                    objPackage.setSpecialTrackInstructions(pakageJsonObj.optString("special_track_instructions"));
                    objPackage.setVacation_message(pakageJsonObj.optString("vacation_message"));

                    try {
                        if (showImages) {
                            JSONObject packagesImagesJsonObject = pakageJsonObj.optJSONObject("package_images");
                            JSONArray loginImagesJsonArray = packagesImagesJsonObject.optJSONArray("login_images");
                            if (loginImagesJsonArray.length() > 0) {
                                count++;
                            }
                            if (loginImagesJsonArray != null && loginImagesJsonArray.length() > 0) {
                                ArrayList<String> images = new ArrayList<>();
                                for (int j = 0, noofimages = loginImagesJsonArray.length(); j < noofimages; j++) {
                                    imagesTotalCount++;
                                    images.add(String.valueOf(loginImagesJsonArray.get(j)));
                                }
                                objPackage.setLoginPicturesList(images);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mPakgList.add(objPackage);

                }
            }
            Calendar calendar = Calendar.getInstance();
            // Log.v("Notifii Time  :", "Out Adapter Time : "+calendar.getTime() +" Packages with Images  : "+ count);
            // Log.v("Notifii Time  :", "Out Adapter Time : "+calendar.getTime() +" all Packages total Images  : "+ imagesTotalCount);
            return mPakgList;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return mPakgList;
        }
    }

    // This method deals with response parsing of PackagesPending Service
    public static ArrayList<Package> getPackageHistory(JSONArray jsonArray) {
        ArrayList<Package> packageArrayList = new ArrayList<>();
        try {
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0, len = jsonArray.length(); i < len; i++) {
                    Package objPackage = new Package();
                    JSONObject pakageJsonObj = jsonArray.getJSONObject(i);
                    objPackage.setPackageId(pakageJsonObj.optString("package_id"));
                    objPackage.setTrackingNumber(pakageJsonObj.optString("tracking_number"));
                    objPackage.setDateReceived(NTF_Utils.getDate(pakageJsonObj.optString("date_received")));
                    objPackage.setDatePickedUp(NTF_Utils.getDate(pakageJsonObj.optString("date_pickedup")));
                    objPackage.setRecipientName(pakageJsonObj.optString("recipient_name"));
                    objPackage.setRecipientAddress1(pakageJsonObj.optString("recipient_address1"));
                    objPackage.setSender(pakageJsonObj.optString("sender"));
                    objPackage.setShelf(pakageJsonObj.optString("shelf"));
                    objPackage.setMailroomId(pakageJsonObj.optString("mailroom_id"));
                    objPackage.setCustomMessage(pakageJsonObj.optString("custom_message"));
                    objPackage.setStaffNotes(pakageJsonObj.optString("staff_note"));
                    objPackage.setTagNumber(pakageJsonObj.optString("tag_number"));
                    objPackage.setPoNumber(pakageJsonObj.optString("po_number"));
                    packageArrayList.add(objPackage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return packageArrayList;
        }
    }


    /*Default Constructor*/
    public Package() {

    }

    protected Package(Parcel in) {
        packageId = in.readString();
        trackingNumber = in.readString();
        long tmpDateReceived = in.readLong();
        dateReceived = tmpDateReceived != -1 ? new Date(tmpDateReceived) : null;
        recipientName = in.readString();
        recipientAddress1 = in.readString();
        mailroomId = in.readString();
        shelf = in.readString();
        sender = in.readString();
        tagNumber = in.readString();
        packageType = in.readString();
        isChecked = in.readByte() != 0x00;
        isItemExpanded = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(packageId);
        dest.writeString(trackingNumber);
        dest.writeLong(dateReceived != null ? dateReceived.getTime() : -1L);
        dest.writeString(recipientName);
        dest.writeString(recipientAddress1);
        dest.writeString(mailroomId);
        dest.writeString(shelf);
        dest.writeString(sender);
        dest.writeString(tagNumber);
        dest.writeString(packageType);
        dest.writeByte((byte) (isChecked ? 0x01 : 0x00));
        dest.writeByte((byte) (isItemExpanded ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Creator<Package> CREATOR = new Creator<Package>() {
        @Override
        public Package createFromParcel(Parcel in) {
            return new Package(in);
        }

        @Override
        public Package[] newArray(int size) {
            return new Package[size];
        }
    };


    public boolean isItemExpanded() {
        return isItemExpanded;
    }

    public void setItemExpanded(boolean itemExpanded) {
        isItemExpanded = itemExpanded;
    }

    private boolean isItemExpanded;
}