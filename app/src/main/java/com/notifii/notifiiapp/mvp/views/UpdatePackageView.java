package com.notifii.notifiiapp.mvp.views;

import com.notifii.notifiiapp.base.BaseMvpView;
import com.notifii.notifiiapp.mvp.models.UpdatePackageRequest;

import org.json.JSONObject;

public interface UpdatePackageView extends BaseMvpView {

    void onEmptyAllParameters();

    void multipleEditRequestValidated(JSONObject request);
    
    void onUpdatingSinglePkgSuccess(JSONObject jsonObject);

    void onUpdatingMultiplePkgsSuccess();


}
