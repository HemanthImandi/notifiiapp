package com.notifii.notifiiapp.mvp.models;

import com.squareup.moshi.Json;

public class PackageLogoutDataRequest {
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
    @Json(name = "staff_note")
    private String staffNote;

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

    public String getStaffNote() {
        return staffNote;
    }

    public void setStaffNote(String staffNote) {
        this.staffNote = staffNote;
    }

}
