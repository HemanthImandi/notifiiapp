package com.notifii.notifiiapp.mvp.views;

import com.notifii.notifiiapp.base.BaseMvpView;

public interface ForgotPasswordView  extends BaseMvpView{

    void onEmptyEmail();

    void onInvalidEmail();

    void onCredentialsValidated(String email);

    void onSuccess(String message);

}
