package com.notifii.notifiiapp.mvp.views;

import com.notifii.notifiiapp.base.BaseMvpView;
import com.notifii.notifiiapp.mvp.models.LoginResponse;

public interface KioskSsoLoginResponseView extends BaseMvpView {
    void onKioskLoginSuccess(LoginResponse loginResponse);
    void onKioskOtherResults(LoginResponse loginResponse);
    void onAccountClosedOrSuspended(LoginResponse loginResponse);
}
