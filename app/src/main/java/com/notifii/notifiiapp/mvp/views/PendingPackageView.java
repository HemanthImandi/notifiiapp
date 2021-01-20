package com.notifii.notifiiapp.mvp.views;

import com.notifii.notifiiapp.base.BaseMvpView;
import com.notifii.notifiiapp.mvp.models.AddRecipientProfileResponse;
import com.notifii.notifiiapp.mvp.models.EditRecipientProfileResponse;
import com.notifii.notifiiapp.mvp.models.PackagePendingWithImagesResponse;

public interface PendingPackageView extends BaseMvpView {

    public void onResuestSuccess(PackagePendingWithImagesResponse pendingWithImagesResponse);
}
