
package com.notifii.notifiiapp.mvp.models;

import com.squareup.moshi.Json;

public class SearchPackageRequest {

    @Json(name = "session_id")
    private String sessionId;
    @Json(name = "authentication_token")
    private String authenticationToken;
    @Json(name = "user_id")
    private String userId;
    @Json(name = "account_id")
    private String accountId;
    @Json(name = "tracking_number")
    private String trackingNumber;
    @Json(name = "recipient_name")
    private String recipientName;
    @Json(name = "package_date_range")
    private String packageDateRange;
    @Json(name = "recipient_address1")
    private String recipientAddress1;
    @Json(name = "sender")
    private String sender;
    @Json(name = "po_number")
    private String poNumber;
    @Json(name = "tag_number")
    private String tagNumber;
    @Json(name = "shelf")
    private String shelf;
    @Json(name = "custom_message")
    private String customMessage;
    @Json(name = "staff_note")
    private String staffNote;
    @Json(name = "search_criteria")
    private String searchCriteria;

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

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientAddress1() {
        return recipientAddress1;
    }

    public void setRecipientAddress1(String recipientAddress1) {
        this.recipientAddress1 = recipientAddress1;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getTagNumber() {
        return tagNumber;
    }

    public void setTagNumber(String tagNumber) {
        this.tagNumber = tagNumber;
    }

    public String getShelf() {
        return shelf;
    }

    public void setShelf(String shelf) {
        this.shelf = shelf;
    }

    public String getCustomMessage() {
        return customMessage;
    }

    public void setCustomMessage(String customMessage) {
        this.customMessage = customMessage;
    }

    public String getStaffNote() {
        return staffNote;
    }

    public void setStaffNote(String staffNote) {
        this.staffNote = staffNote;
    }

    public String getSearchCriteria() {
        return searchCriteria;
    }

    public void setSearchCriteria(String searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    public String getPackageDateRange() {
        return packageDateRange;
    }

    public void setPackageDateRange(String packageDateRange) {
        this.packageDateRange = packageDateRange;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
