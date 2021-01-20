package com.notifii.notifiiapp.mvp.presenters;

import android.text.TextUtils;

import com.achercasky.initialclasses.mvp.presenter.BasePresenter;
import com.notifii.notifiiapp.mvp.models.ContactUsRequest;
import com.notifii.notifiiapp.mvp.models.ContactUsResponse;
import com.notifii.notifiiapp.mvp.models.UpdateNamEmailRequest;
import com.notifii.notifiiapp.mvp.models.UpdateNamEmailResponse;
import com.notifii.notifiiapp.mvp.views.ContactUsView;
import com.notifii.notifiiapp.mvp.views.UserNameEmailView;
import com.notifii.notifiiapp.network.CustomDisposableObserver;
import com.notifii.notifiiapp.network.NTFTrackService;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class UpdateNameEmailPresenter extends BasePresenter<UserNameEmailView> {

    public void doCheckCredentials(String name, String email, boolean isName) {
        if (isName) {
            if (name.trim().isEmpty()) {
                getMvpView().onEmptyName();
                return;
            }
        } else {
            if (email.trim().isEmpty()) {
                getMvpView().onEmptyEmail();
                return;
            } else if (!NTF_Utils.isValidEmail(email.trim())) {
                getMvpView().onInvalidEmail();
                return;
            }

        }
        getMvpView().onCredentialsValidated();
    }

    public void doUserNameEmailSubmit(String header, UpdateNamEmailRequest request) {
        Disposable disposable = NTFTrackService.getInstance(header).doUserNameEmailSubmit(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CustomDisposableObserver<UpdateNamEmailResponse>() {
                    @Override
                    public void onNext(UpdateNamEmailResponse updateNamEmailResponse) {
                        if (getMvpView() != null) {
                            if (updateNamEmailResponse.getApiStatus().toLowerCase().equals(NTF_Utils.ResponseKeys.SESSION_EXPIRED)) {
                                getMvpView().onSessionExpired(updateNamEmailResponse.getApiMessage());
                            } else if (updateNamEmailResponse.getApiStatus().equalsIgnoreCase(NTF_Utils.ResponseKeys.SUCCESS)) {
                                getMvpView().onSuccess(updateNamEmailResponse.getApiMessage());
                            } else if (updateNamEmailResponse.getApiStatus().equalsIgnoreCase(NTF_Utils.ResponseKeys.ERROR)) {
                                getMvpView().onErrorCode(updateNamEmailResponse.getApiMessage());
                            } else {
                                getMvpView().onErrorCode(updateNamEmailResponse.getApiMessage());
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
