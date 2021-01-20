package com.notifii.notifiiapp.mvp.models;

import com.squareup.moshi.Json;

public class AllLinkedAccount {
    @Json(name = "account_name")
    private String accountName;
    @Json(name = "account_id")
    private String accountId;
    boolean isSelected=false;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

}
