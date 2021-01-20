package com.notifii.notifiiapp.mvp.models;

import com.squareup.moshi.Json;

public class EditRecipientProfileRequest {

    @Json(name = "session_id")
    private String sessionId;
    @Json(name = "authentication_token")
    private String authenticationToken;
    @Json(name = "account_id")
    private String accountId;
    @Json(name = "recipient_id")
    private String recipientId;
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
    @Json(name = "send_connect_nonmarketing_email")
    private String sendConnectNonmarketingEmail;
    @Json(name = "send_connect_marketing_email")
    private String sendConnectMarketingEmail;
    @Json(name = "send_track_nonmarketing_text")
    private String sendTrackNonmarketingText;
    @Json(name = "send_connect_nonmarketing_text")
    private String sendConnectNonmarketingText;
    @Json(name = "send_connect_marketing_text")
    private String sendConnectMarketingText;
    @Json(name = "send_track_nonmarketing_push")
    private String sendTrackNonmarketingPush;
    @Json(name = "send_connect_nonmarketing_push")
    private String sendConnectNonmarketingPush;
    @Json(name = "send_connect_marketing_push")
    private String sendConnectMarketingPush;
    @Json(name = "stop_track_nonmarketing_email")
    private String stopTrackNonmarketingEmail;
    @Json(name = "stop_connect_nonmarketing_email")
    private String stopConnectNonmarketingEmail;
    @Json(name = "stop_connect_marketing_email")
    private String stopConnectMarketingEmail;
    @Json(name = "stop_track_nonmarketing_text")
    private String stopTrackNonmarketingText;
    @Json(name = "stop_connect_nonmarketing_text")
    private String stopConnectNonmarketingText;
    @Json(name = "stop_connect_marketing_text")
    private String stopConnectMarketingText;
    @Json(name = "stop_track_nonmarketing_push")
    private String stopTrackNonmarketingPush;
    @Json(name = "stop_connect_nonmarketing_push")
    private String stopConnectNonmarketingPush;
    @Json(name = "stop_connect_marketing_push")
    private String stopConnectMarketingPush;
    @Json(name = "recipient_status")
    private String recipientStatus;
    @Json(name = "recipient_type")
    private String recipientType;
    @Json(name = "special_track_instructions_flag")
    private String specialTrackInstructionsFlag;
    @Json(name = "special_track_instructions")
    private String specialTrackInstructions;
    @Json(name = "vacation_status")
    private String vacationStatus;
    @Json(name = "vacation_start_date")
    private String vacationStartDate;
    @Json(name = "vacation_end_date")
    private String vacationEndDate;
    @Json(name = "move_in_date")
    private String moveInDate;
    @Json(name = "move_out_date")
    private String moveOutDate;
    @Json(name = "lease_start_date")
    private String leaseStartDate;
    @Json(name = "lease_end_date")
    private String leaseEndDate;
    @Json(name = "birth_date")
    private String birthDate;
    @Json(name = "send_pkg_login_notification")
    private String sendPkgLoginNotification;
    @Json(name = "send_pkg_logout_notification")
    private String sendPkgLogoutNotification;
    @Json(name = "user_id")
    private String userId;
    @Json(name = "recipient_title")
    private String recipientTitle;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRecipientTitle() {
        return recipientTitle;
    }

    public void setRecipientTitle(String recipientTitle) {
        this.recipientTitle = recipientTitle;
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

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
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

    public String getSendConnectNonmarketingEmail() {
        return sendConnectNonmarketingEmail;
    }

    public void setSendConnectNonmarketingEmail(String sendConnectNonmarketingEmail) {
        this.sendConnectNonmarketingEmail = sendConnectNonmarketingEmail;
    }

    public String getSendConnectMarketingEmail() {
        return sendConnectMarketingEmail;
    }

    public void setSendConnectMarketingEmail(String sendConnectMarketingEmail) {
        this.sendConnectMarketingEmail = sendConnectMarketingEmail;
    }

    public String getSendTrackNonmarketingText() {
        return sendTrackNonmarketingText;
    }

    public void setSendTrackNonmarketingText(String sendTrackNonmarketingText) {
        this.sendTrackNonmarketingText = sendTrackNonmarketingText;
    }

    public String getSendConnectNonmarketingText() {
        return sendConnectNonmarketingText;
    }

    public void setSendConnectNonmarketingText(String sendConnectNonmarketingText) {
        this.sendConnectNonmarketingText = sendConnectNonmarketingText;
    }

    public String getSendConnectMarketingText() {
        return sendConnectMarketingText;
    }

    public void setSendConnectMarketingText(String sendConnectMarketingText) {
        this.sendConnectMarketingText = sendConnectMarketingText;
    }

    public String getSendTrackNonmarketingPush() {
        return sendTrackNonmarketingPush;
    }

    public void setSendTrackNonmarketingPush(String sendTrackNonmarketingPush) {
        this.sendTrackNonmarketingPush = sendTrackNonmarketingPush;
    }

    public String getSendConnectNonmarketingPush() {
        return sendConnectNonmarketingPush;
    }

    public void setSendConnectNonmarketingPush(String sendConnectNonmarketingPush) {
        this.sendConnectNonmarketingPush = sendConnectNonmarketingPush;
    }

    public String getSendConnectMarketingPush() {
        return sendConnectMarketingPush;
    }

    public void setSendConnectMarketingPush(String sendConnectMarketingPush) {
        this.sendConnectMarketingPush = sendConnectMarketingPush;
    }

    public String getStopTrackNonmarketingEmail() {
        return stopTrackNonmarketingEmail;
    }

    public void setStopTrackNonmarketingEmail(String stopTrackNonmarketingEmail) {
        this.stopTrackNonmarketingEmail = stopTrackNonmarketingEmail;
    }

    public String getStopConnectNonmarketingEmail() {
        return stopConnectNonmarketingEmail;
    }

    public void setStopConnectNonmarketingEmail(String stopConnectNonmarketingEmail) {
        this.stopConnectNonmarketingEmail = stopConnectNonmarketingEmail;
    }

    public String getStopConnectMarketingEmail() {
        return stopConnectMarketingEmail;
    }

    public void setStopConnectMarketingEmail(String stopConnectMarketingEmail) {
        this.stopConnectMarketingEmail = stopConnectMarketingEmail;
    }

    public String getStopTrackNonmarketingText() {
        return stopTrackNonmarketingText;
    }

    public void setStopTrackNonmarketingText(String stopTrackNonmarketingText) {
        this.stopTrackNonmarketingText = stopTrackNonmarketingText;
    }

    public String getStopConnectNonmarketingText() {
        return stopConnectNonmarketingText;
    }

    public void setStopConnectNonmarketingText(String stopConnectNonmarketingText) {
        this.stopConnectNonmarketingText = stopConnectNonmarketingText;
    }

    public String getStopConnectMarketingText() {
        return stopConnectMarketingText;
    }

    public void setStopConnectMarketingText(String stopConnectMarketingText) {
        this.stopConnectMarketingText = stopConnectMarketingText;
    }

    public String getStopTrackNonmarketingPush() {
        return stopTrackNonmarketingPush;
    }

    public void setStopTrackNonmarketingPush(String stopTrackNonmarketingPush) {
        this.stopTrackNonmarketingPush = stopTrackNonmarketingPush;
    }

    public String getStopConnectNonmarketingPush() {
        return stopConnectNonmarketingPush;
    }

    public void setStopConnectNonmarketingPush(String stopConnectNonmarketingPush) {
        this.stopConnectNonmarketingPush = stopConnectNonmarketingPush;
    }

    public String getStopConnectMarketingPush() {
        return stopConnectMarketingPush;
    }

    public void setStopConnectMarketingPush(String stopConnectMarketingPush) {
        this.stopConnectMarketingPush = stopConnectMarketingPush;
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

    public String getSpecialTrackInstructionsFlag() {
        return specialTrackInstructionsFlag;
    }

    public void setSpecialTrackInstructionsFlag(String specialTrackInstructionsFlag) {
        this.specialTrackInstructionsFlag = specialTrackInstructionsFlag;
    }

    public String getSpecialTrackInstructions() {
        return specialTrackInstructions;
    }

    public void setSpecialTrackInstructions(String specialTrackInstructions) {
        this.specialTrackInstructions = specialTrackInstructions;
    }

    public String getVacationStatus() {
        return vacationStatus;
    }

    public void setVacationStatus(String vacationStatus) {
        this.vacationStatus = vacationStatus;
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

    public String getMoveInDate() {
        return moveInDate;
    }

    public void setMoveInDate(String moveInDate) {
        this.moveInDate = moveInDate;
    }

    public String getMoveOutDate() {
        return moveOutDate;
    }

    public void setMoveOutDate(String moveOutDate) {
        this.moveOutDate = moveOutDate;
    }

    public String getLeaseStartDate() {
        return leaseStartDate;
    }

    public void setLeaseStartDate(String leaseStartDate) {
        this.leaseStartDate = leaseStartDate;
    }

    public String getLeaseEndDate() {
        return leaseEndDate;
    }

    public void setLeaseEndDate(String leaseEndDate) {
        this.leaseEndDate = leaseEndDate;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
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

