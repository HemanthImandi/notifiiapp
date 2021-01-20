package com.notifii.notifiiapp.mvp.models;

import com.squareup.moshi.Json;

public class PackageLookUpResponse {
    @Json(name = "api_status")
    private String apiStatus;
    @Json(name = "package_id")
    private String packageId;

    public String getApiMessage() {
        return apiMessage;
    }

    public void setApiMessage(String apiMessage) {
        this.apiMessage = apiMessage;
    }

    @Json(name = "api_message")
    private String apiMessage;
    @Json(name = "tracking_number")
    private String trackingNumber;
    @Json(name = "recipient_name")
    private String recipientName;
    @Json(name = "recipient_address1")
    private String recipientAddress1;

    public String getApiStatus() {
        return apiStatus;
    }

    public void setApiStatus(String apiStatus) {
        this.apiStatus = apiStatus;
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

    public String getRecipientName() {
        return recipientName;
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
}

