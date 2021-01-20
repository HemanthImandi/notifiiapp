package com.notifii.notifiiapp.mvp.views;

import com.notifii.notifiiapp.base.BaseMvpView;
import com.notifii.notifiiapp.mvp.models.LogPkgInResponse;

public interface LogPkgInView extends BaseMvpView {

    void onPkgLoginSuccess(LogPkgInResponse response);

    void onPkgLoginWarning(LogPkgInResponse response);

    void onPkgLoginError(LogPkgInResponse response);

}
