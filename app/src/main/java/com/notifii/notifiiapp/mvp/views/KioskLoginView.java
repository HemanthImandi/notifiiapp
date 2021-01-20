package com.notifii.notifiiapp.mvp.views;

import com.notifii.notifiiapp.base.BaseMvpView;
import com.notifii.notifiiapp.mvp.models.KioskLoginResponse;

public interface KioskLoginView extends BaseMvpView {

    void onKioskLoginSuccess(KioskLoginResponse loginResponse);
}
