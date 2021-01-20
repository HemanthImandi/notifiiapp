package com.notifii.notifiiapp.mvp.models;

import com.squareup.moshi.Json;

public class LogPkgInResponse {
    @Json(name = "api_status")
    private String apiStatus;
    @Json(name = "api_message")
    private String apiMessage;
    @Json(name = "special_track_instructions")
    private String specialTrackInstructions;
    @Json(name = "vacation_message")
    private String vacationMessage;

    public String getApiStatus() {
        return apiStatus;
    }

    public void setApiStatus(String apiStatus) {
        this.apiStatus = apiStatus;
    }

    public String getApiMessage() {
        return apiMessage;
    }

    public void setApiMessage(String apiMessage) {
        this.apiMessage = apiMessage;
    }

    public String getSpecialTrackInstructions() {
        return specialTrackInstructions;
    }

    public void setSpecialTrackInstructions(String specialTrackInstructions) {
        this.specialTrackInstructions = specialTrackInstructions;
    }

    public String getVacationMessage() {
        return vacationMessage;
    }

    public void setVacationMessage(String vacationMessage) {
        this.vacationMessage = vacationMessage;
    }

}
