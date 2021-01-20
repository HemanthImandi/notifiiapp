package com.notifii.notifiiapp.mvp.models;

import com.squareup.moshi.Json;

public class UpdatePackageResponse {

    @Json(name = "change_history")
    private String changeHistory;
    @Json(name = "api_status")
    private String apiStatus;
    @Json(name = "api_message")
    private String apiMessage;

    public String getApiMessage() {
        return apiMessage;
    }

    public void setApiMessage(String apiMessage) {
        this.apiMessage = apiMessage;
    }

    public String getChangeHistory() {
        return changeHistory;
    }

    public void setChangeHistory(String changeHistory) {
        this.changeHistory = changeHistory;
    }

    public String getApiStatus() {
        return apiStatus;
    }

    public void setApiStatus(String apiStatus) {
        this.apiStatus = apiStatus;
    }

}
