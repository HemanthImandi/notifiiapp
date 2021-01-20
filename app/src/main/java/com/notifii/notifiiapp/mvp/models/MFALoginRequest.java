package com.notifii.notifiiapp.mvp.models;

import com.squareup.moshi.Json;

public class MFALoginRequest {

    @Json(name = "mfa_code")
    private String mfaCode;
    @Json(name = "account_id")
    private String accountId;
    @Json(name = "user_id")
    private String userId;
    @Json(name = "session_timedout")
    private String sessionTimedout;
    @Json(name = "device_unique_id")
    private String deviceUdid;

    public String getMfaCode() {
        return mfaCode;
    }

    public void setMfaCode(String mfaCode) {
        this.mfaCode = mfaCode;
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

    public String getSessionTimedout() {
        return sessionTimedout;
    }

    public void setSessionTimedout(String sessionTimedout) {
        this.sessionTimedout = sessionTimedout;
    }

    public String getDeviceUdid() {
        return deviceUdid;
    }

    public void setDeviceUdid(String deviceUdid) {
        this.deviceUdid = deviceUdid;
    }

}
