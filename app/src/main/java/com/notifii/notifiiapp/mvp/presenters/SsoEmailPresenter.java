package com.notifii.notifiiapp.mvp.presenters;

import android.text.TextUtils;

import com.achercasky.initialclasses.mvp.presenter.BasePresenter;
import com.notifii.notifiiapp.mvp.models.SsoEmailRequest;
import com.notifii.notifiiapp.mvp.models.SsoEmailResponse;
import com.notifii.notifiiapp.mvp.views.SsoEmailView;
import com.notifii.notifiiapp.network.CustomDisposableObserver;
import com.notifii.notifiiapp.network.NTFTrackService;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SsoEmailPresenter extends BasePresenter<SsoEmailView> {

    public void doCheckCredentials(String email) {

        if (TextUtils.isEmpty(email.trim())) {
            getMvpView().onEmptyEmail();
            return;
        } else if (!NTF_Utils.isValidEmail(email)) {
            getMvpView().onInvalidEmail();
            return;
        }

        getMvpView().onCredentialsValidated();
    }

    public void doSsoEmailLogin(String header, SsoEmailRequest request) {
        Disposable disposable = NTFTrackService.getInstance(header).doSsoEmailLogin(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CustomDisposableObserver<SsoEmailResponse>() {
                    @Override
                    public void onNext(SsoEmailResponse ssoEmailResponse) {
                        if (getMvpView() != null) {
                            if (ssoEmailResponse.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.SUCCESS)) {
                                getMvpView().onSsoEmailSuccess(ssoEmailResponse);
                            } else if (ssoEmailResponse.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.SELECT_ACCOUNT)) {
                                getMvpView().onMultipleAccountsFound(ssoEmailResponse);
                            } else if (ssoEmailResponse.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.ERROR)) {
                                getMvpView().onError(ssoEmailResponse.getApiMessage());
                            } else {
                                getMvpView().onError(ssoEmailResponse.getApiMessage());
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
