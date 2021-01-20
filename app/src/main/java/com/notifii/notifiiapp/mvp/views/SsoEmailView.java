package com.notifii.notifiiapp.mvp.views;

import com.notifii.notifiiapp.base.BaseMvpView;
import com.notifii.notifiiapp.mvp.models.SsoEmailResponse;

public interface SsoEmailView extends BaseMvpView {

    void onEmptyEmail();

    void onInvalidEmail();

    void onCredentialsValidated();
    void onWarning(String message);

    void onSsoEmailSuccess(SsoEmailResponse ssoEmailResponse);

    void onMultipleAccountsFound(SsoEmailResponse ssoEmailResponse);

    void onError(String apiMessage);
}
