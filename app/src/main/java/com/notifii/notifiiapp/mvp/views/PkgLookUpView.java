package com.notifii.notifiiapp.mvp.views;

import com.notifii.notifiiapp.base.BaseMvpView;
import com.notifii.notifiiapp.mvp.models.PackageLookUpResponse;

public interface PkgLookUpView  extends BaseMvpView {
    void onPackageFound(PackageLookUpResponse response);
    void onPackageNotFound(String message);
}
