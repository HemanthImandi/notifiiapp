package com.notifii.notifiiapp.mvp.views;

import com.notifii.notifiiapp.base.BaseMvpView;

import org.json.JSONObject;

public interface PackagHistoryView extends BaseMvpView {

    void onPackageHistoryforAccountSuccess(JSONObject jsonObject);
}
