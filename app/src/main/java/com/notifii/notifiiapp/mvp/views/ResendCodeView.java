package com.notifii.notifiiapp.mvp.views;

import com.notifii.notifiiapp.base.BaseMvpView;

public interface ResendCodeView extends BaseMvpView {

    void onResendSuccess(String message);

    void onResndFail(String message);

    void onDisableResend(String message);
}
