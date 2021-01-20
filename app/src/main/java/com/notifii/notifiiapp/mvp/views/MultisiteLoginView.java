package com.notifii.notifiiapp.mvp.views;

import com.notifii.notifiiapp.base.BaseMvpView;
import com.notifii.notifiiapp.mvp.models.LoginResponse;

public interface MultisiteLoginView extends BaseMvpView {

    void onMFAConfigured(LoginResponse response);

    void onMultisiteLoginSuccess(LoginResponse response);

}
