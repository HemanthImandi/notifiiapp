package com.notifii.notifiiapp.mvp.models;

import com.squareup.moshi.Json;

import java.util.List;

public class SsoEmailResponse {
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
    @Json(name = "process_type")
    private String processType;
    @Json(name = "session_timedout")
    private String sessionTimedout;
    @Json(name = "api_status")
    private String apiStatus;
    @Json(name = "api_message")
    private String apiMessage;
    @Json(name = "sso_url")
    private String ssoUrl;
    @Json(name = "all_linked_accounts")
    private List<AllLinkedAccount> allLinkedAccounts = null;

    public String getApiMessage() {
        return apiMessage;
    }

    public void setApiMessage(String apiMessage) {
        this.apiMessage = apiMessage;
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

    public String getProcessType() {
        return processType;
    }

    public void setProcessType(String processType) {
        this.processType = processType;
    }

    public String getSessionTimedout() {
        return sessionTimedout;
    }

    public void setSessionTimedout(String sessionTimedout) {
        this.sessionTimedout = sessionTimedout;
    }

    public String getApiStatus() {
        return apiStatus;
    }

    public void setApiStatus(String apiStatus) {
        this.apiStatus = apiStatus;
    }

    public String getSsoUrl() {
        return ssoUrl;
    }

    public void setSsoUrl(String ssoUrl) {
        this.ssoUrl = ssoUrl;
    }

    public List<AllLinkedAccount> getAllLinkedAccounts() {
        return allLinkedAccounts;
    }

    public void setAllLinkedAccounts(List<AllLinkedAccount> allLinkedAccounts) {
        this.allLinkedAccounts = allLinkedAccounts;
    }

}
