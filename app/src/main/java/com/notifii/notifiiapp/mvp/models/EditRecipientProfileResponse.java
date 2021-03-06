package com.notifii.notifiiapp.mvp.models;

import com.squareup.moshi.Json;

public class EditRecipientProfileResponse {
    @Json(name = "api_status")
    private String apiStatus;
    @Json(name = "api_message")
    private String apiMessage;
    public String getApiStatus() {
        return apiStatus;
    }
    public String getApiMessage() {
        return apiMessage;
    }

    public void setApiMessage(String apiMessage) {
        this.apiMessage = apiMessage;
    }

    public void setApiStatus(String apiStatus) {
        this.apiStatus = apiStatus;
    }

}
