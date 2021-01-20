package com.notifii.notifiiapp.mvp.views;

import com.notifii.notifiiapp.base.BaseMvpView;
import com.notifii.notifiiapp.mvp.models.UpdateNamEmailRequest;

public interface UserPasswordView extends BaseMvpView {

    void onInvalidPassword(String invalidMsg);

    void onCredentialsValidated();

    void onPasswordSucess(String message);

}
