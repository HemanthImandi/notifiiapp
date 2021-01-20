package com.notifii.notifiiapp.mvp.views;

import com.notifii.notifiiapp.base.BaseMvpView;

import org.json.JSONObject;

public interface RecipientsListView extends BaseMvpView {

    void onRecipientsListSuccess(JSONObject jsonObject);
    void onRecipientListError(String error);
}
