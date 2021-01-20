package com.notifii.notifiiapp.mvp.views;

import com.notifii.notifiiapp.base.BaseMvpView;
import com.notifii.notifiiapp.mvp.models.LoginResponse;

public interface LoginView extends BaseMvpView {

    void onEmptyUserName();

    void onEmptyPassword();

    void onCredentialsValidated(String username, String password);

    void onLoginFail(LoginResponse loginResponse);

    void onLoginSuccess(LoginResponse loginResponse);

    void onWarning(String message);

    void onAccountClosedOrSuspended(LoginResponse loginResponse);

    void onMFAEnabled(LoginResponse loginResponse);

    void onMultisiteEnabled(LoginResponse response);

}
