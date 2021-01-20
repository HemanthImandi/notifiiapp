package com.notifii.notifiiapp.mvp.models;

import com.squareup.moshi.Json;

public class SwitchAccountRequest {
    @Json(name = "session_id")
    private String sessionId;
    @Json(name = "authentication_token")
    private String authenticationToken;
    @Json(name = "account_id")
    private String accountId;
    @Json(name = "user_id")
    private String userId;
    @Json(name = "session_timedout")
    private String sessionTimedout;
    @Json(name = "switched_account_id")
    private String switchedAccountId;
    @Json(name = "switched_user_id")
    private String switchedUserId;
    @Json(name = "device_unique_id")
    private String deviceUniqueId;
    @Json(name = "app_version")
    private String appVersion;

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

    public String getSessionTimedout() {
        return sessionTimedout;
    }

    public void setSessionTimedout(String sessionTimedout) {
        this.sessionTimedout = sessionTimedout;
    }

    public String getSwitchedAccountId() {
        return switchedAccountId;
    }

    public void setSwitchedAccountId(String switchedAccountId) {
        this.switchedAccountId = switchedAccountId;
    }

    public String getSwitchedUserId() {
        return switchedUserId;
    }

    public void setSwitchedUserId(String switchedUserId) {
        this.switchedUserId = switchedUserId;
    }

    public String getDeviceUniqueId() {
        return deviceUniqueId;
    }

    public void setDeviceUniqueId(String deviceUniqueId) {
        this.deviceUniqueId = deviceUniqueId;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

}
