
package com.notifii.notifiiapp.mvp.models;

import java.util.List;
import com.squareup.moshi.Json;


public class PackagePendingWithImagesResponse {

    @Json(name = "api_status")
    private String apiStatus;
    @Json(name = "api_message")
    private String apiMessage;
    @Json(name = "number_packages")
    private String numberPackages;
    @Json(name = "pending_packages_limit")
    private Integer pendingPackagesLimit;
    @Json(name = "total_pending_packages")
    private String totalPendingPackages;
    @Json(name = "available_pending_packages")
    private Integer availablePendingPackages;
    @Json(name = "packages")
    private List<Package> packages = null;

    public String getApiStatus() {
        return apiStatus;
    }

    public void setApiStatus(String apiStatus) {
        this.apiStatus = apiStatus;
    }

    public String getApiMessage() {
        return apiMessage;
    }

    public void setApiMessage(String apiMessage) {
        this.apiMessage = apiMessage;
    }

    public String getNumberPackages() {
        return numberPackages;
    }

    public void setNumberPackages(String numberPackages) {
        this.numberPackages = numberPackages;
    }

    public Integer getPendingPackagesLimit() {
        return pendingPackagesLimit;
    }

    public void setPendingPackagesLimit(Integer pendingPackagesLimit) {
        this.pendingPackagesLimit = pendingPackagesLimit;
    }

    public String getTotalPendingPackages() {
        return totalPendingPackages;
    }

    public void setTotalPendingPackages(String totalPendingPackages) {
        this.totalPendingPackages = totalPendingPackages;
    }

    public Integer getAvailablePendingPackages() {
        return availablePendingPackages;
    }

    public void setAvailablePendingPackages(Integer availablePendingPackages) {
        this.availablePendingPackages = availablePendingPackages;
    }

    public List<Package> getPackages() {
        return packages;
    }

    public void setPackages(List<Package> packages) {
        this.packages = packages;
    }

}
