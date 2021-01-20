package com.notifii.notifiiapp.mvp.models;

import com.squareup.moshi.Json;

public class PackagePicture {
    @Json(name = "picture_data")
    private String pictureData;

    public String getPictureData() {
        return pictureData;
    }

    public void setPictureData(String pictureData) {
        this.pictureData = pictureData;
    }
}
