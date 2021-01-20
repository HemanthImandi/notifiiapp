package com.notifii.notifiiapp.mvp.models;

import com.squareup.moshi.Json;

public class AddRecipientProfileRequest {

    @Json(name = "session_id")
    private String sessionId;
    @Json(name = "authentication_token")
    private String authenticationToken;
    @Json(name = "account_id")
    private String accountId;
    @Json(name = "user_id")
    private String userId;
    @Json(name = "first_name")
    private String firstName;
    @Json(name = "last_name")
    private String lastName;
    @Json(name = "address1")
    private String address1;
    @Json(name = "email")
    private String email;
    @Json(name = "cellphone")
    private String cellphone;
    @Json(name = "send_track_nonmarketing_email")
    private String sendTrackNonmarketingEmail;
    @Json(name = "send_track_nonmarketing_text")
    private String sendTrackNonmarketingText;
    @Json(name = "recipient_status")
    private String recipientStatus;
    @Json(name = "recipient_type")
    private String recipientType;
    @Json(name = "special_track_instructions")
    private String specialTrackInstructions;
    @Json(name = "special_track_instructions_flag")
    private String specialTrackInstructionsFlag;
    @Json(name = "vacation_start_date")
    private String vacationStartDate;
    @Json(name = "vacation_end_date")
    private String vacationEndDate;
    @Json(name = "send_pkg_login_notification")
    private String sendPkgLoginNotification;
    @Json(name = "send_pkg_logout_notification")
    private String sendPkgLogoutNotification;

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getSendTrackNonmarketingEmail() {
        return sendTrackNonmarketingEmail;
    }

    public void setSendTrackNonmarketingEmail(String sendTrackNonmarketingEmail) {
        this.sendTrackNonmarketingEmail = sendTrackNonmarketingEmail;
    }

    public String getSendTrackNonmarketingText() {
        return sendTrackNonmarketingText;
    }

    public void setSendTrackNonmarketingText(String sendTrackNonmarketingText) {
        this.sendTrackNonmarketingText = sendTrackNonmarketingText;
    }

    public String getRecipientStatus() {
        return recipientStatus;
    }

    public void setRecipientStatus(String recipientStatus) {
        this.recipientStatus = recipientStatus;
    }

    public String getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(String recipientType) {
        this.recipientType = recipientType;
    }

    public String getSpecialTrackInstructions() {
        return specialTrackInstructions;
    }

    public void setSpecialTrackInstructions(String specialTrackInstructions) {
        this.specialTrackInstructions = specialTrackInstructions;
    }

    public String getSpecialTrackInstructionsFlag() {
        return specialTrackInstructionsFlag;
    }

    public void setSpecialTrackInstructionsFlag(String specialTrackInstructionsFlag) {
        this.specialTrackInstructionsFlag = specialTrackInstructionsFlag;
    }

    public String getVacationStartDate() {
        return vacationStartDate;
    }

    public void setVacationStartDate(String vacationStartDate) {
        this.vacationStartDate = vacationStartDate;
    }

    public String getVacationEndDate() {
        return vacationEndDate;
    }

    public void setVacationEndDate(String vacationEndDate) {
        this.vacationEndDate = vacationEndDate;
    }

    public String getSendPkgLoginNotification() {
        return sendPkgLoginNotification;
    }

    public void setSendPkgLoginNotification(String sendPkgLoginNotification) {
        this.sendPkgLoginNotification = sendPkgLoginNotification;
    }

    public String getSendPkgLogoutNotification() {
        return sendPkgLogoutNotification;
    }

    public void setSendPkgLogoutNotification(String sendPkgLogoutNotification) {
        this.sendPkgLogoutNotification = sendPkgLogoutNotification;
    }

}