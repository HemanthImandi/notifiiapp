package com.notifii.notifiiapp.base;

public interface BaseMvpView extends com.achercasky.initialclasses.mvp.view.BaseMvpView {

    void onSessionExpired(String message);

    void onServerError();
}
