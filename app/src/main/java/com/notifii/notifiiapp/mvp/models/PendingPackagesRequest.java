package com.notifii.notifiiapp.mvp.models;

import com.squareup.moshi.Json;

public class PendingPackagesRequest {
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
    @Json(name = "search_by")
    private String searchBy;
    @Json(name = "mailroom_specific_sorting")
    private String mailroomSpecificSorting;
    @Json(name = "api_mode")
    private String apiMode;
    @Json(name = "user_id")
    private String userId;
    @Json(name = "app_version")
    private String appVersion;
    @Json(name = "pending_packages_primary_sort_column")
    private String pendingPackagesPrimarySortColumn;
    @Json(name = "pending_packages_primary_sort_order")
    private String pendingPackagesPrimarySortOrder;
    @Json(name = "pending_packages_secondary_sort_column")
    private String pendingPackagesSecondarySortColumn;
    @Json(name = "pending_packages_secondary_sort_order")
    private String pendingPackagesSecondarySortOrder;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getSearchBy() {
        return searchBy;
    }

    public void setSearchBy(String searchBy) {
        this.searchBy = searchBy;
    }

    public String getMailroomSpecificSorting() {
        return mailroomSpecificSorting;
    }

    public void setMailroomSpecificSorting(String mailroomSpecificSorting) {
        this.mailroomSpecificSorting = mailroomSpecificSorting;
    }

    public String getApiMode() {
        return apiMode;
    }

    public void setApiMode(String apiMode) {
        this.apiMode = apiMode;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getPendingPackagesPrimarySortColumn() {
        return pendingPackagesPrimarySortColumn;
    }

    public void setPendingPackagesPrimarySortColumn(String pendingPackagesPrimarySortColumn) {
        this.pendingPackagesPrimarySortColumn = pendingPackagesPrimarySortColumn;
    }

    public String getPendingPackagesPrimarySortOrder() {
        return pendingPackagesPrimarySortOrder;
    }

    public void setPendingPackagesPrimarySortOrder(String pendingPackagesPrimarySortOrder) {
        this.pendingPackagesPrimarySortOrder = pendingPackagesPrimarySortOrder;
    }

    public String getPendingPackagesSecondarySortColumn() {
        return pendingPackagesSecondarySortColumn;
    }

    public void setPendingPackagesSecondarySortColumn(String pendingPackagesSecondarySortColumn) {
        this.pendingPackagesSecondarySortColumn = pendingPackagesSecondarySortColumn;
    }

    public String getPendingPackagesSecondarySortOrder() {
        return pendingPackagesSecondarySortOrder;
    }

    public void setPendingPackagesSecondarySortOrder(String pendingPackagesSecondarySortOrder) {
        this.pendingPackagesSecondarySortOrder = pendingPackagesSecondarySortOrder;
    }
}
