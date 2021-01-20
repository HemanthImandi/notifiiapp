package com.notifii.notifiiapp.mvp.views;

import com.notifii.notifiiapp.base.BaseMvpView;
import com.notifii.notifiiapp.mvp.models.UpdateUsernamePwdResponse;
import com.notifii.notifiiapp.mvp.models.UsernameCheckAvailabilityResponse;

public interface UpdateUsernamePwdView extends BaseMvpView {

    void fieldsNotValid(String message);

    void onFieldsValidated(String username);

    void onUserNameValidated();

    void onUpdatingSuccess(UpdateUsernamePwdResponse response);

    void onUpdatingFailure(String message);

    void onCheckingSuccess(UsernameCheckAvailabilityResponse response);

    void userNameTaken();

    void onCheckingFailure(String mesage);
}
