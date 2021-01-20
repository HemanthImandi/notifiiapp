
package com.notifii.notifiiapp.mvp.models;

import java.util.List;
import com.squareup.moshi.Json;

public class PackageImages {

    @Json(name = "login_images")
    private List<String> loginImages = null;

    public List<String> getLoginImages() {
        return loginImages;
    }

    public void setLoginImages(List<String> loginImages) {
        this.loginImages = loginImages;
    }

}
