package com.notifii.notifiiapp.mvp.views;

import com.notifii.notifiiapp.base.BaseMvpView;

public interface PackageLogoutImagesView extends BaseMvpView {

    void onImagesSuccess();

    void onOtherResults(String message);
}
