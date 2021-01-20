package com.notifii.notifiiapp.mvp.views;

import com.notifii.notifiiapp.base.BaseMvpView;
import com.notifii.notifiiapp.mvp.models.LoginResponse;

public interface MFALoginView extends BaseMvpView {

    void onLoginSuccess(LoginResponse response);

    void onLoginFail(String mesasge);

}
