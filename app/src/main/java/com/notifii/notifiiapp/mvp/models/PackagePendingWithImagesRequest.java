
package com.notifii.notifiiapp.mvp.models;

import com.squareup.moshi.Json;

public class PackagePendingWithImagesRequest {

    @Json(name = "session_id")
    private String sessionId;
    @Json(name = "authentication_token")
    private String authenticationToken;
    @Json(name = "account_id")
    private String accountId;
    @Json(name = "account_type")
    private String accountType;
    @Json(name = "mailroom_id")
    private String mailroomId;
    @Json(name = "limit_cycle")
    private String limitCycle;
    @Json(name = "sort_by")
    private String sortBy;
    @Json(name = "sort_type")
    private String sortType;
    @Json(name = "search_by")
    private String searchBy;
    @Json(name = "device_type")
    private String deviceType;
    @Json(name = "version")
    private String version;
    @Json(name = "api_mode")
    private String apiMode;

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

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getMailroomId() {
        return mailroomId;
    }

    public void setMailroomId(String mailroomId) {
        this.mailroomId = mailroomId;
    }

    public String getLimitCycle() {
        return limitCycle;
    }

    public void setLimitCycle(String limitCycle) {
        this.limitCycle = limitCycle;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortType() {
        return sortType;
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
    }

    public String getSearchBy() {
        return searchBy;
    }

    public void setSearchBy(String searchBy) {
        this.searchBy = searchBy;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getApiMode() {
        return apiMode;
    }

    public void setApiMode(String apiMode) {
        this.apiMode = apiMode;
    }

}
