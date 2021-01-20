package com.notifii.notifiiapp.mvp.models;

import com.squareup.moshi.Json;

import java.util.List;

public class LogPkgImagesRequest {

    @Json(name = "session_id")
    private String sessionId;
    @Json(name = "authentication_token")
    private String authenticationToken;
    @Json(name = "account_id")
    private String accountId;
    @Json(name = "user_id")
    private String userId;
    @Json(name = "package_id")
    private String packageId;
    @Json(name = "logout_code_id")
    private String logoutCodeId;
    @Json(name = "esignature_base64")
    private String esignatureBase64;
    @Json(name = "package_pictures")
    private List<PackagePicture> packagePictures =null;
    @Json(name = "advanced_send_notification")
    private String advancedSendNotification;

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

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getLogoutCodeId() {
        return logoutCodeId;
    }

    public void setLogoutCodeId(String logoutCodeId) {
        this.logoutCodeId = logoutCodeId;
    }

    public String getEsignatureBase64() {
        return esignatureBase64;
    }

    public void setEsignatureBase64(String esignatureBase64) {
        this.esignatureBase64 = esignatureBase64;
    }

    public List<PackagePicture> getPackagePictures() {
        return packagePictures;
    }

    public void setPackagePictures(List<PackagePicture> packagePictures) {

        this.packagePictures = packagePictures;
    }

    public String getAdvancedSendNotification() {
        return advancedSendNotification;
    }

    public void setAdvancedSendNotification(String advancedSendNotification) {
        this.advancedSendNotification = advancedSendNotification;
    }
}
