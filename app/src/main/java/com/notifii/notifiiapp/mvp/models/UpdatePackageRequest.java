package com.notifii.notifiiapp.mvp.models;

import com.squareup.moshi.Json;

import java.util.List;

public class UpdatePackageRequest {
    @Json(name = "session_id")
    private String sessionId;
    @Json(name = "authentication_token")
    private String authenticationToken;
    @Json(name = "account_id")
    private String accountId;
    @Json(name = "user_id")
    private String userId;
    @Json(name = "mailroom_id")
    private String mailroomId;
    @Json(name = "shelf")
    private String shelf;
    @Json(name = "shipping_carrier")
    private String shippingCarrier;
    @Json(name = "sender")
    private String sender;
    @Json(name = "service_type")
    private String serviceType;
    @Json(name = "package_type")
    private String packageType;
    @Json(name = "package_condition")
    private String packageCondition;
    @Json(name = "po_number")
    private String poNumber;
    @Json(name = "weight")
    private String weight;
    @Json(name = "dimensions")
    private String dimensions;
    @Json(name = "staff_note_replace")
    private String staffNoteReplace;
    @Json(name = "logout_code_id")
    private String logoutCodeId;
    @Json(name = "package_id")
    private String packageId;
    @Json(name = "special_handlings")
    private String specialHandlings;
    @Json(name = "package_pictures")
    private List<PackagePicture> packagePictures = null;

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSpecialHandlings() {
        return specialHandlings;
    }

    public void setSpecialHandlings(String specialHandlings) {
        this.specialHandlings = specialHandlings;
    }

    public String getMailroomId() {
        return mailroomId;
    }

    public void setMailroomId(String mailroomId) {
        this.mailroomId = mailroomId;
    }

    public String getShelf() {
        return shelf;
    }

    public void setShelf(String shelf) {
        this.shelf = shelf;
    }

    public String getShippingCarrier() {
        return shippingCarrier;
    }

    public void setShippingCarrier(String shippingCarrier) {
        this.shippingCarrier = shippingCarrier;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public String getPackageCondition() {
        return packageCondition;
    }

    public void setPackageCondition(String packageCondition) {
        this.packageCondition = packageCondition;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public String getStaffNoteReplace() {
        return staffNoteReplace;
    }

    public void setStaffNoteReplace(String staffNoteReplace) {
        this.staffNoteReplace = staffNoteReplace;
    }

    public String getLogoutCodeId() {
        return logoutCodeId;
    }

    public void setLogoutCodeId(String logoutCodeId) {
        this.logoutCodeId = logoutCodeId;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public List<PackagePicture> getPackagePictures() {
        return packagePictures;
    }

    public void setPackagePictures(List<PackagePicture> packagePictures) {
        this.packagePictures = packagePictures;
    }


}
