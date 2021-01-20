package com.notifii.notifiiapp.mvp.views;

import com.notifii.notifiiapp.base.BaseMvpView;
import com.notifii.notifiiapp.mvp.models.ProfileResponse;

public interface ProfileView extends BaseMvpView {


    void onProfileSucess(ProfileResponse profileResponse);

}
