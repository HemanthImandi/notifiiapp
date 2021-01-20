package com.notifii.notifiiapp.mvp.views;



import com.notifii.notifiiapp.base.BaseMvpView;


import org.json.JSONObject;

public interface GetPkgDetailsView extends BaseMvpView {
    void onPackageFound(JSONObject jsonObject);
    void onPackageNotFound(String response);
}
