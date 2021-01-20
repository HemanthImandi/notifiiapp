package com.notifii.notifiiapp.mvp.views;

import com.notifii.notifiiapp.base.BaseMvpView;
import org.json.JSONObject;

public interface PendingPackagesView extends BaseMvpView {

    void onPendingPackagesSuccess(JSONObject jsonArray);

    void onPendingPackagesFail(String var1);

}
