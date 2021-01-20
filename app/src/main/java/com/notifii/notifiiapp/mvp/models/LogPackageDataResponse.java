package com.notifii.notifiiapp.mvp.models;

import com.squareup.moshi.Json;

public class LogPackageDataResponse {
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

}
