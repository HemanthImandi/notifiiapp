package com.notifii.notifiiapp.mvp.models;

import com.squareup.moshi.Json;

public class UpdateUsernamePwdRequest {

    @Json(name = "new_username")
    private String newUsername;
    @Json(name = "new_password")
    private String newPassword;
    @Json(name = "authentication_token")
    private String authenticationToken;
    @Json(name = "session_id")
    private String sessionId;
    @Json(name = "user_id")
    private String userId;
    @Json(name = "account_id")
    private String accountId;

    public String getNewUsername() {
        return newUsername;
    }

    public void setNewUsername(String newUsername) {
        this.newUsername = newUsername;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getAuthenticationToken() {
        return authenticationToken;
    }

    public void setAuthenticationToken(String authenticationToken) {
        this.authenticationToken = authenticationToken;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

}
