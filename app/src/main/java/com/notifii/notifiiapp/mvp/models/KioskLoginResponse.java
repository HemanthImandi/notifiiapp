package com.notifii.notifiiapp.mvp.models;

import com.squareup.moshi.Json;

public class KioskLoginResponse {

    @Json(name = "api_status")
    private String apiStatus;
    @Json(name = "api_message")
    private String apiMessage;
    @Json(name = "session_id")
    private String sessionId;
    @Json(name = "authentication_token")
    private String authenticationToken;
    @Json(name = "expiration")
    private String expiration;
    @Json(name = "account_id")
    private String accountId;
    @Json(name = "user_id")
    private String userId;
    @Json(name = "timezone")
    private String timezone;
    @Json(name = "kiosk_mode_disclaimer")
    private String kioskModeDisclaimer;
    @Json(name = "kiosk_display_recipient_format")
    private String kioskDisplayRecipientFormat;
    @Json(name = "timezoneid_shortcode")
    private String timezoneidShortcode;
    @Json(name = "account_type")
    private String accountType;
    @Json(name = "kiosk_use_front_camera")
    private String useFrontCamera;

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getTimezoneidShortcode() {
        return timezoneidShortcode;
    }

    public void setTimezoneidShortcode(String timezoneidShortcode) {
        this.timezoneidShortcode = timezoneidShortcode;
    }


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

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getAuthenticationToken() {
        return authenticationToken;
    }

    public void setAuthenticationToken(String authenticationToken) {
        this.authenticationToken = authenticationToken;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getKioskModeDisclaimer() {
        return kioskModeDisclaimer;
    }

    public void setKioskModeDisclaimer(String kioskModeDisclaimer) {
        this.kioskModeDisclaimer = kioskModeDisclaimer;
    }

    public String getKioskDisplayRecipientFormat() {
        return kioskDisplayRecipientFormat;
    }

    public void setKioskDisplayRecipientFormat(String kioskDisplayRecipientFormat) {
        this.kioskDisplayRecipientFormat = kioskDisplayRecipientFormat;
    }


    public String getUseFrontCamera() {
        return useFrontCamera;
    }

    public void setUseFrontCamera(String useFrontCamera) {
        this.useFrontCamera = useFrontCamera;
    }
}
