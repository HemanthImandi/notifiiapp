package com.notifii.notifiiapp.mvp.views;

import com.notifii.notifiiapp.base.BaseMvpView;
import com.notifii.notifiiapp.mvp.models.SsoEmailResponse;

public interface ContactUsView extends BaseMvpView {

    void onEmptyEmail();

    void onEmptyName();

    void onEmptyMessage();

    void onInvalidEmail();

    void onCredentialsValidated();

    void onContactSuccess(String apiMessage);

}
