package com.notifii.notifiiapp.mvp.models;

import com.squareup.moshi.Json;

import java.util.List;

public class RecipientPendingPackagesResponse {
    @Json(name = "api_status")
    private String apiStatus;
    @Json(name = "api_message")
    private String apiMessage;
    @Json(name = "number_packages")
    private Integer numberPackages;
    @Json(name = "packages")
    private List<KioskPackage> packages = null;

    public String getApiMessage() {
        return apiMessage;
    }

    public void setApiMessage(String apiMessage) {
        this.apiMessage = apiMessage;
    }

    public String getApiStatus() {
        return apiStatus;
    }

    public void setApiStatus(String apiStatus) {
        this.apiStatus = apiStatus;
    }

    public Integer getNumberPackages() {
        return numberPackages;
    }

    public void setNumberPackages(Integer numberPackages) {
        this.numberPackages = numberPackages;
    }

    public List<KioskPackage> getPackages() {
        return packages;
    }

    public void setPackages(List<KioskPackage> packages) {
        this.packages = packages;
    }

}
