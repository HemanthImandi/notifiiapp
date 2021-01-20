package com.notifii.notifiiapp.mvp.models;

import com.squareup.moshi.Json;

import java.util.List;

public class LoginResponse {

    @Json(name = "api_status")
    private String apiStatus;
    @Json(name = "api_message")
    private String apiMessage;
    @Json(name = "session_id")
    private String sessionId;
    @Json(name = "authentication_token")
    private String authenticationToken;
    @Json(name = "expiration")
    private String expiration;
    @Json(name = "user_id")
    private String userId;
    @Json(name = "full_name")
    private String fullName;
    @Json(name = "account_id")
    private String accountId;
    @Json(name = "account_name")
    private String accountName;
    @Json(name = "account_type")
    private String accountType;
    @Json(name = "user_type_id")
    private String userTypeId;
    @Json(name = "user_type")
    private String userType;
    @Json(name = "default_mailroom _id")
    private String defaultMailroomId;
    @Json(name = "timezone")
    private String timezone;
    @Json(name = "pkg_login_carrier")
    private String pkgLoginCarrier;
    @Json(name = "pkg_login_sender")
    private String pkgLoginSender;
    @Json(name = "pkg_login_servicetype")
    private String pkgLoginServicetype;
    @Json(name = "pkg_login_packagetype")
    private String pkgLoginPackagetype;
    @Json(name = "pkg_login_condition")
    private String pkgLoginCondition;
    @Json(name = "pkg_login_weight")
    private String pkgLoginWeight;
    @Json(name = "pkg_login_dimensions")
    private String pkgLoginDimensions;
    @Json(name = "pkg_login_shelf")
    private String pkgLoginShelf;
    @Json(name = "pkg_login_ponumber")
    private String pkgLoginPonumber;
    @Json(name = "pkg_logout_address1")
    private String pkgLogoutAddress1;
    @Json(name = "pkg_logout_shelf")
    private String pkgLogoutShelf;
    @Json(name = "pkg_logout_packagetype")
    private String pkgLogoutPackagetype;
    @Json(name = "pkg_logout_tagnumber")
    private String pkgLogoutTagnumber;
    @Json(name = "pkg_logout_mailroom")
    private String pkgLogoutMailroom;
    @Json(name = "pkg_logout_sender")
    private String pkgLogoutSender;
    @Json(name = "require_explicit_sms_opt_in")
    private String requireExplicitSmsOptIn;
    @Json(name = "send_opt_in_sms")
    private String sendOptInSms;
    @Json(name = "timezoneid_shortcode")
    private String timezoneidShortcode;
    @Json(name = "session_timedout")
    private String sessionTimedout;
    @Json(name = "number_linked_accounts")
    private Integer numberLinkedAccounts;
    @Json(name = "linked_accounts")
    private List<LinkedAccount> linkedAccounts = null;
    @Json(name = "require_front_camera")
    private String requireFrontCamera;


    public String getTimezoneidShortcode() {
        return timezoneidShortcode;
    }

    public void setTimezoneidShortcode(String timezoneidShortcode) {
        this.timezoneidShortcode = timezoneidShortcode;
    }

    public String getApiStatus() {
        return apiStatus;
    }

    public void setApiStatus(String apiStatus) {
        this.apiStatus = apiStatus;
    }

    public String getApiMessage() {
        return apiMessage;
    }

    public void setApiMessage(String apiMessage) {
        this.apiMessage = apiMessage;
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

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getUserTypeId() {
        return userTypeId;
    }

    public void setUserTypeId(String userTypeId) {
        this.userTypeId = userTypeId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getDefaultMailroomId() {
        return defaultMailroomId;
    }

    public void setDefaultMailroomId(String defaultMailroomId) {
        this.defaultMailroomId = defaultMailroomId;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getPkgLoginCarrier() {
        return pkgLoginCarrier;
    }

    public void setPkgLoginCarrier(String pkgLoginCarrier) {
        this.pkgLoginCarrier = pkgLoginCarrier;
    }

    public String getPkgLoginSender() {
        return pkgLoginSender;
    }

    public void setPkgLoginSender(String pkgLoginSender) {
        this.pkgLoginSender = pkgLoginSender;
    }

    public String getPkgLoginServicetype() {
        return pkgLoginServicetype;
    }

    public void setPkgLoginServicetype(String pkgLoginServicetype) {
        this.pkgLoginServicetype = pkgLoginServicetype;
    }

    public String getPkgLoginPackagetype() {
        return pkgLoginPackagetype;
    }

    public void setPkgLoginPackagetype(String pkgLoginPackagetype) {
        this.pkgLoginPackagetype = pkgLoginPackagetype;
    }

    public String getPkgLoginCondition() {
        return pkgLoginCondition;
    }

    public void setPkgLoginCondition(String pkgLoginCondition) {
        this.pkgLoginCondition = pkgLoginCondition;
    }

    public String getPkgLoginWeight() {
        return pkgLoginWeight;
    }

    public void setPkgLoginWeight(String pkgLoginWeight) {
        this.pkgLoginWeight = pkgLoginWeight;
    }

    public String getPkgLoginDimensions() {
        return pkgLoginDimensions;
    }

    public void setPkgLoginDimensions(String pkgLoginDimensions) {
        this.pkgLoginDimensions = pkgLoginDimensions;
    }

    public String getPkgLoginShelf() {
        return pkgLoginShelf;
    }

    public void setPkgLoginShelf(String pkgLoginShelf) {
        this.pkgLoginShelf = pkgLoginShelf;
    }

    public String getPkgLoginPonumber() {
        return pkgLoginPonumber;
    }

    public void setPkgLoginPonumber(String pkgLoginPonumber) {
        this.pkgLoginPonumber = pkgLoginPonumber;
    }

    public String getPkgLogoutAddress1() {
        return pkgLogoutAddress1;
    }

    public void setPkgLogoutAddress1(String pkgLogoutAddress1) {
        this.pkgLogoutAddress1 = pkgLogoutAddress1;
    }

    public String getPkgLogoutShelf() {
        return pkgLogoutShelf;
    }

    public void setPkgLogoutShelf(String pkgLogoutShelf) {
        this.pkgLogoutShelf = pkgLogoutShelf;
    }

    public String getPkgLogoutPackagetype() {
        return pkgLogoutPackagetype;
    }

    public void setPkgLogoutPackagetype(String pkgLogoutPackagetype) {
        this.pkgLogoutPackagetype = pkgLogoutPackagetype;
    }

    public String getPkgLogoutTagnumber() {
        return pkgLogoutTagnumber;
    }

    public void setPkgLogoutTagnumber(String pkgLogoutTagnumber) {
        this.pkgLogoutTagnumber = pkgLogoutTagnumber;
    }

    public String getPkgLogoutMailroom() {
        return pkgLogoutMailroom;
    }

    public void setPkgLogoutMailroom(String pkgLogoutMailroom) {
        this.pkgLogoutMailroom = pkgLogoutMailroom;
    }

    public String getPkgLogoutSender() {
        return pkgLogoutSender;
    }

    public void setPkgLogoutSender(String pkgLogoutSender) {
        this.pkgLogoutSender = pkgLogoutSender;
    }

    public String getRequireExplicitSmsOptIn() {
        return requireExplicitSmsOptIn;
    }

    public void setRequireExplicitSmsOptIn(String requireExplicitSmsOptIn) {
        this.requireExplicitSmsOptIn = requireExplicitSmsOptIn;
    }

    public String getSendOptInSms() {
        return sendOptInSms;
    }

    public void setSendOptInSms(String sendOptInSms) {
        this.sendOptInSms = sendOptInSms;
    }

    public String getSessionTimedout() {
        return sessionTimedout;
    }

    public void setSessionTimedout(String sessionTimedout) {
        this.sessionTimedout = sessionTimedout;
    }

    public Integer getNumberLinkedAccounts() {
        return numberLinkedAccounts;
    }

    public void setNumberLinkedAccounts(Integer numberLinkedAccounts) {
        this.numberLinkedAccounts = numberLinkedAccounts;
    }

    public List<LinkedAccount> getLinkedAccounts() {
        return linkedAccounts;
    }

    public void setLinkedAccounts(List<LinkedAccount> linkedAccounts) {
        this.linkedAccounts = linkedAccounts;
    }

    public String getRequireFrontCamera() {
        return requireFrontCamera;
    }

    public void setRequireFrontCamera(String requireFrontCamera) {
        this.requireFrontCamera = requireFrontCamera;
    }
}