package com.notifii.notifiiapp.mvp.views;

import com.notifii.notifiiapp.base.BaseMvpView;
import com.notifii.notifiiapp.mvp.models.RecipientPendingPackagesResponse;

public interface RecipientPendingPackagesView extends BaseMvpView {
    void onRequestSuccess(RecipientPendingPackagesResponse response);
}
