package com.notifii.notifiiapp.mvp.views;

import com.notifii.notifiiapp.base.BaseMvpView;

import org.json.JSONObject;

public interface GlobalConstanctsView extends BaseMvpView {

    void getGlobalConstanctsFail(String message);

    void getGlobalConstanctsSuccess(JSONObject response);

}
