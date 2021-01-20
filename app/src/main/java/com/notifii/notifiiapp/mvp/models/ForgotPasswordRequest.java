package com.notifii.notifiiapp.mvp.models;

import com.squareup.moshi.Json;

public class ForgotPasswordRequest {
    @Json(name = "username")
    private String username;
    @Json(name = "email")
    private String email;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
