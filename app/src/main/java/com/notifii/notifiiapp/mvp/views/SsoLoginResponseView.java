package com.notifii.notifiiapp.mvp.views;

import com.notifii.notifiiapp.base.BaseMvpView;
import com.notifii.notifiiapp.mvp.models.LoginResponse;

public interface SsoLoginResponseView extends BaseMvpView {
    void onLoginSuccess(LoginResponse loginResponse);
    void onLoginFail(LoginResponse loginResponse);
}
