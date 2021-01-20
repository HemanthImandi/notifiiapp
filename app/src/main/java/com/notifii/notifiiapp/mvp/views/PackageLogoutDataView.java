package com.notifii.notifiiapp.mvp.views;

import com.notifii.notifiiapp.base.BaseMvpView;

public interface PackageLogoutDataView extends BaseMvpView {

    void onCredentialsValidated(String signature);

    void onEmptySignature();

    void onDataSuccess();

    void onDataWarning(String message);

}
