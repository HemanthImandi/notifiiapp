package com.notifii.notifiiapp.mvp.views;

import com.notifii.notifiiapp.base.BaseMvpView;
import com.notifii.notifiiapp.mvp.models.UpdateNamEmailRequest;
import com.notifii.notifiiapp.mvp.models.UpdateUserNameResponse;

public interface UserNameView extends BaseMvpView {

    void onInvalidName(String message);

    void onUserNotAvailable(String message);

    void onCredentialsValidated();

    void onSuccessUserName(String message);

}
