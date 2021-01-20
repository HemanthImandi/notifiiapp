package com.notifii.notifiiapp.mvp.models;

import com.squareup.moshi.Json;

public class KioskLogoutRequest {
    @Json(name = "session_id")
    private String sessionId;
    @Json(name = "authentication_token")
    private String authenticationToken;
    @Json(name = "account_id")
    private String accountId;
    @Json(name = "username")
    private String username;
    @Json(name = "password")
    private String password;
    @Json(name = "session_timedout")
    private String sessionTimedout;
    @Json(name = "device_unique_id")
    private String deviceUniqueId;
    @Json(name = "app_version")
    private String appVersion;
    @Json(name = "user_id")
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getDeviceUniqueId() {
        return deviceUniqueId;
    }

    public void setDeviceUniqueId(String deviceUniqueId) {
        this.deviceUniqueId = deviceUniqueId;
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

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSessionTimedout() {
        return sessionTimedout;
    }

    public void setSessionTimedout(String sessionTimedout) {
        this.sessionTimedout = sessionTimedout;
    }

}
