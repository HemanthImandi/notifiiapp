package com.notifii.notifiiapp.mvp.models;

import com.squareup.moshi.Json;

public class  SsoEmailRequest {

    @Json(name = "email")
    private String email;
    @Json(name = "account_id")
    private String accountId;
    @Json(name = "authentication_token")
    private String authenticationToken;
    @Json(name = "session_id")
    private String sessionId;
    @Json(name = "session_timedout")
    private String sessionTimedout;
    @Json(name = "selected_account")
    private String selectedAccount;
    @Json(name = "user_id")
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
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

    public String getSessionTimedout() {
        return sessionTimedout;
    }

    public void setSessionTimedout(String sessionTimedout) {
        this.sessionTimedout = sessionTimedout;
    }

    public String getSelectedAccount() {
        return selectedAccount;
    }

    public void setSelectedAccount(String selectedAccount) {
        this.selectedAccount = selectedAccount;
    }

}