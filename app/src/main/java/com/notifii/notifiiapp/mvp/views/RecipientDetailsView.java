package com.notifii.notifiiapp.mvp.views;

import com.notifii.notifiiapp.base.BaseMvpView;

import org.json.JSONObject;

public interface RecipientDetailsView extends BaseMvpView {

    void onRecipienttSuccess(JSONObject jsonObject);
}
