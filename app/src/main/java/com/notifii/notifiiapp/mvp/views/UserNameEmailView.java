package com.notifii.notifiiapp.mvp.views;

import com.notifii.notifiiapp.base.BaseMvpView;
import com.notifii.notifiiapp.mvp.models.UpdateNamEmailRequest;

public interface UserNameEmailView extends BaseMvpView {

    void onEmptyEmail();

    void onEmptyName();

    void onInvalidEmail();

    void onCredentialsValidated();

    void onSuccess(String message);

}
