package com.notifii.notifiiapp.mvp.models;

import com.squareup.moshi.Json;

public class SsoLoginResponseRequest {
    @Json(name = "session_id")
    private String sessionId;
    @Json(name = "authentication_token")
    private String authenticationToken;
    @Json(name = "sso_setting_type")
    private String ssoSettingType;
    @Json(name = "sso_account_id")
    private String ssoAccountId;
    @Json(name = "account_id")
    private String accountId;
    @Json(name = "user_id")
    private String userId;
    @Json(name = "user_email")
    private String userEmail;
    @Json(name = "SAMLResponse")
    private String sAMLResponse;
    @Json(name = "session_timedout")
    private String sessionTimedout;
    @Json(name = "app_mode")
    private String appMode;
    @Json(name = "app_version")
    private String appVersion;
    @Json(name = "device_unique_id")
    private String deviceUniqueId;

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

    public String getSsoSettingType() {
        return ssoSettingType;
    }

    public void setSsoSettingType(String ssoSettingType) {
        this.ssoSettingType = ssoSettingType;
    }

    public String getSsoAccountId() {
        return ssoAccountId;
    }

    public void setSsoAccountId(String ssoAccountId) {
        this.ssoAccountId = ssoAccountId;
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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getSAMLResponse() {
        return sAMLResponse;
    }

    public void setSAMLResponse(String sAMLResponse) {
        this.sAMLResponse = sAMLResponse;
    }

    public String getSessionTimedout() {
        return sessionTimedout;
    }

    public void setSessionTimedout(String sessionTimedout) {
        this.sessionTimedout = sessionTimedout;
    }

    public String getAppMode() {
        return appMode;
    }

    public void setAppMode(String appMode) {
        this.appMode = appMode;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    /*public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }*/

}
