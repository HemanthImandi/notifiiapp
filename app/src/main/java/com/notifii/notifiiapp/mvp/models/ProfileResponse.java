
package com.notifii.notifiiapp.mvp.models;

import com.squareup.moshi.Json;

public class ProfileResponse {

    @Json(name = "username")
    private String username;
    @Json(name = "user_type_id")
    private String userTypeId;
    @Json(name = "full_name")
    private String fullName;
    @Json(name = "email")
    private String email;
    @Json(name = "last_login")
    private String lastLogin;
    @Json(name = "mailroom_id")
    private String mailroomId;
    @Json(name = "user_type")
    private String userType;
    @Json(name = "account_type")
    private String accountType;
    @Json(name = "api_status")
    private String apiStatus;
    @Json(name = "api_message")
    private String apiMessage;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserTypeId() {
        return userTypeId;
    }

    public void setUserTypeId(String userTypeId) {
        this.userTypeId = userTypeId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getMailroomId() {
        return mailroomId;
    }

    public void setMailroomId(String mailroomId) {
        this.mailroomId = mailroomId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getApiStatus() {
        return apiStatus;
    }

    public void setApiStatus(String apiStatus) {
        this.apiStatus = apiStatus;
    }

    public void setApiMessage(String apiMessage) {
        this.apiMessage = apiMessage;
    }

    public String getApiMessage() {
        return apiMessage;
    }
}
