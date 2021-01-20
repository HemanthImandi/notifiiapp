package com.notifii.notifiiapp.mvp.presenters;

import android.text.TextUtils;

import com.achercasky.initialclasses.mvp.presenter.BasePresenter;
import com.notifii.notifiiapp.mvp.models.ContactUsRequest;
import com.notifii.notifiiapp.mvp.models.ContactUsResponse;
import com.notifii.notifiiapp.mvp.models.SsoEmailRequest;
import com.notifii.notifiiapp.mvp.models.SsoEmailResponse;
import com.notifii.notifiiapp.mvp.views.ContactUsView;
import com.notifii.notifiiapp.mvp.views.SsoEmailView;
import com.notifii.notifiiapp.network.CustomDisposableObserver;
import com.notifii.notifiiapp.network.NTFTrackService;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ContactUsPresenter extends BasePresenter<ContactUsView> {

    public void doCheckCredentials(String name, String email, String phone, String message) {

        if (TextUtils.isEmpty(name.trim())) {
            getMvpView().onEmptyName();
            return;
        } else if (TextUtils.isEmpty(email.trim())) {
            getMvpView().onEmptyEmail();
            return;
        } else if (!NTF_Utils.isValidEmail(email.trim())) {
            getMvpView().onInvalidEmail();
            return;
        } else if (TextUtils.isEmpty(message.trim())) {
            getMvpView().onEmptyMessage();
            return;
        }

        getMvpView().onCredentialsValidated();
    }

    public void doContactDetailsSubmit(String header, ContactUsRequest request) {
        Disposable disposable = NTFTrackService.getInstance(header).doContactDetailsSubmit(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CustomDisposableObserver<ContactUsResponse>() {
                    @Override
                    public void onNext(ContactUsResponse contactResponse) {
                        if (getMvpView() != null) {
                            if (contactResponse.getApiStatus().toLowerCase().equals(NTF_Constants.ResponseKeys.SESSION_EXPIRED)) {
                                getMvpView().onSessionExpired(contactResponse.getApiMessage());
                            } else if (contactResponse.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.SUCCESS)) {
                                getMvpView().onContactSuccess(contactResponse.getApiMessage());
                            } else if (contactResponse.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.ERROR)) {
                                getMvpView().onErrorCode(contactResponse.getApiMessage());
                            } else {
                                getMvpView().onErrorCode(contactResponse.getApiMessage());
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
