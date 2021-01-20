package com.notifii.notifiiapp.mvp.presenters;

import android.text.TextUtils;

import com.achercasky.initialclasses.mvp.presenter.BasePresenter;
import com.notifii.notifiiapp.mvp.models.ContactUsRequest;
import com.notifii.notifiiapp.mvp.models.ContactUsResponse;
import com.notifii.notifiiapp.mvp.models.ProfileRequest;
import com.notifii.notifiiapp.mvp.models.ProfileResponse;
import com.notifii.notifiiapp.mvp.views.ContactUsView;
import com.notifii.notifiiapp.mvp.views.ProfileView;
import com.notifii.notifiiapp.network.CustomDisposableObserver;
import com.notifii.notifiiapp.network.NTFTrackService;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ProfilePresenter extends BasePresenter<ProfileView> {


    public void doProfileDetails(String header, ProfileRequest request) {
        Disposable disposable = NTFTrackService.getInstance(header).doProfileDetails(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CustomDisposableObserver<ProfileResponse>() {
                    @Override
                    public void onNext(ProfileResponse profileResponse) {
                        if (getMvpView() != null) {
                            if (profileResponse.getApiStatus().toLowerCase().equals(NTF_Utils.ResponseKeys.SESSION_EXPIRED) ) {
                                getMvpView().onSessionExpired(profileResponse.getApiMessage());
                            } else if (profileResponse.getApiStatus().equalsIgnoreCase(NTF_Utils.ResponseKeys.SUCCESS)) {
                                getMvpView().onProfileSucess(profileResponse);
                            } else if (profileResponse.getApiStatus().equalsIgnoreCase(NTF_Utils.ResponseKeys.ERROR)) {
                                getMvpView().onErrorCode(profileResponse.getApiMessage());
                            } else {
                                getMvpView().onErrorCode(profileResponse.getApiMessage());
                            }
                        }
                    }

                    @Override
                    public void onConnectionLost() {
                        if (getMvpView() != null) {
                            getMvpView().onNoInternetConnection();
                        }
                    }

                    @Override
                    public void onServerError() {
                        if (getMvpView() != null) {
                            getMvpView().onServerError();
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        if (getMvpView() != null) {
                            getMvpView().onError(t);
                        }
                    }
                });
        compositeSubscription.add(disposable);
    }
}
