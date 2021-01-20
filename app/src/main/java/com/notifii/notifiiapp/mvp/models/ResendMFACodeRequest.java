package com.notifii.notifiiapp.mvp.models;

import com.squareup.moshi.Json;

public class ResendMFACodeRequest {
    @Json(name = "account_id")
    private String accountId;
    @Json(name = "user_id")
    private String userId;

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

}
