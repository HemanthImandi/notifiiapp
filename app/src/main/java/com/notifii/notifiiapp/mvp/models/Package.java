
package com.notifii.notifiiapp.mvp.models;

import com.squareup.moshi.Json;

public class Package {

    @Json(name = "package_id")
    private String packageId;
    @Json(name = "tracking_number")
    private String trackingNumber;
    @Json(name = "date_received")
    private String dateReceived;
    @Json(name = "recipient_id")
    private String recipientId;
    @Json(name = "recipient_name")
    private String recipientName;
    @Json(name = "first_name")
    private String firstName;
    @Json(name = "last_name")
    private String lastName;
    @Json(name = "recipient_address1")
    private String recipientAddress1;
    @Json(name = "mailroom_id")
    private String mailroomId;
    @Json(name = "shelf")
    private String shelf;
    @Json(name = "sender")
    private String sender;
    @Json(name = "tag_number")
    private Object tagNumber;
    @Json(name = "package_type")
    private String packageType;
    @Json(name = "custom_message")
    private String customMessage;
    @Json(name = "staff_note")
    private String staffNote;
    @Json(name = "change_history")
    private Object changeHistory;
    @Json(name = "shipping_carrier")
    private String shippingCarrier;
    @Json(name = "special_track_instructions")
    private String specialTrackInstructions;
    @Json(name = "vacation_message")
    private String vacationMessage;
    @Json(name = "package_images")
    private PackageImages packageImages;
    @Json(name = "date_pickedup")
    private String datePickedup;

    private boolean isChecked;


    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(String dateReceived) {
        this.dateReceived = dateReceived;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
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

    public String getRecipientAddress1() {
        return recipientAddress1;
    }

    public void setRecipientAddress1(String recipientAddress1) {
        this.recipientAddress1 = recipientAddress1;
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

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Object getTagNumber() {
        return tagNumber;
    }

    public void setTagNumber(Object tagNumber) {
        this.tagNumber = tagNumber;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
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

    public Object getChangeHistory() {
        return changeHistory;
    }

    public void setChangeHistory(Object changeHistory) {
        this.changeHistory = changeHistory;
    }

    public String getShippingCarrier() {
        return shippingCarrier;
    }

    public void setShippingCarrier(String shippingCarrier) {
        this.shippingCarrier = shippingCarrier;
    }

    public String getSpecialTrackInstructions() {
        return specialTrackInstructions;
    }

    public void setSpecialTrackInstructions(String specialTrackInstructions) {
        this.specialTrackInstructions = specialTrackInstructions;
    }

    public String getVacationMessage() {
        return vacationMessage;
    }

    public void setVacationMessage(String vacationMessage) {
        this.vacationMessage = vacationMessage;
    }

    public PackageImages getPackageImages() {
        return packageImages;
    }

    public void setPackageImages(PackageImages packageImages) {
        this.packageImages = packageImages;
    }
    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public boolean isChecked() {
        return isChecked;
    }
    public boolean isItemExpanded() {
        return isItemExpanded;
    }

    public void setItemExpanded(boolean itemExpanded) {
        isItemExpanded = itemExpanded;
    }

    private boolean isItemExpanded;

    public String getDatePickedup() {
        return datePickedup;
    }

    public void setDatePickedup(String datePickedup) {
        this.datePickedup = datePickedup;
    }
}
