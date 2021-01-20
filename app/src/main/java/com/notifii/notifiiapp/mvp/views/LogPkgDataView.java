package com.notifii.notifiiapp.mvp.views;

import com.notifii.notifiiapp.base.BaseMvpView;

public interface LogPkgDataView extends BaseMvpView {
    void onPkgDataLogoutSuccess(String message);

    void onPkgImagesLogoutSuccess();

    void onGettingWarning(String response);
}

