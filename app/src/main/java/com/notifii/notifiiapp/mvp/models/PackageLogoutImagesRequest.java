package com.notifii.notifiiapp.mvp.models;

import com.squareup.moshi.Json;

import java.util.List;

public class PackageLogoutImagesRequest {
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
    @Json(name = "esignature_base64")
    private String esignatureBase64;
    @Json(name = "logout_code_id")
    private String logoutCodeId;
    @Json(name = "recipent_id")
    private String recipentId;
    @Json(name = "package_pictures")
    private List<PackagePicture> packagePictures =null;

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

    public String getEsignatureBase64() {
        return esignatureBase64;
    }

    public void setEsignatureBase64(String esignatureBase64) {
        this.esignatureBase64 = esignatureBase64;
    }

    public String getLogoutCodeId() {
        return logoutCodeId;
    }

    public void setLogoutCodeId(String logoutCodeId) {
        this.logoutCodeId = logoutCodeId;
    }

    public String getRecipentId() {
        return recipentId;
    }

    public void setRecipentId(String recipentId) {
        this.recipentId = recipentId;
    }

    public List<PackagePicture> getPackagePictures() {
        return packagePictures;
    }

    public void setPackagePictures(List<PackagePicture> packagePictures) {
        this.packagePictures = packagePictures;
    }
}
