package com.notifii.notifiiapp.mvp.models;

import com.squareup.moshi.Json;

public class LoginRequest {
    @Json(name = "username")
    private String username;
    @Json(name = "password")
    private String password;
    @Json(name = "app_version")
    private String appVersion;
    @Json(name = "session_timedout")
    private String sessionTimedout;
    @Json(name = "device_unique_id")
    private String deviceUniqueId;

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

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getSessionTimedout() {
        return sessionTimedout;
    }

    public void setSessionTimedout(String sessionTimedout) {
        this.sessionTimedout = sessionTimedout;
    }

    public String getDeviceUniqueId() {
        return deviceUniqueId;
    }

    public void setDeviceUniqueId(String deviceUniqueId) {
        this.deviceUniqueId = deviceUniqueId;
    }
}
