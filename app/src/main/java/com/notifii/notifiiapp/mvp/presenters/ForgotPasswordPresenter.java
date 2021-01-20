package com.notifii.notifiiapp.mvp.presenters;

import com.achercasky.initialclasses.mvp.presenter.BasePresenter;
import com.notifii.notifiiapp.mvp.models.ForgotPasswordRequest;
import com.notifii.notifiiapp.mvp.models.ForgotPasswordResponse;
import com.notifii.notifiiapp.mvp.models.LoginRequest;
import com.notifii.notifiiapp.mvp.models.LoginResponse;
import com.notifii.notifiiapp.mvp.views.ForgotPasswordView;
import com.notifii.notifiiapp.network.CustomDisposableObserver;
import com.notifii.notifiiapp.network.NTFTrackService;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ForgotPasswordPresenter extends BasePresenter<ForgotPasswordView> {

    public void doCheckCredentials(String email) {

        if (email.trim().isEmpty()) {
            getMvpView().onEmptyEmail();
            return;
        }

        if (!NTF_Utils.isValidEmail(email)) {
            getMvpView().onInvalidEmail();
            return;
        }

        getMvpView().onCredentialsValidated(email);
    }

    public void forgotpassword(String header, ForgotPasswordRequest request) {
        Disposable disposable = NTFTrackService.getInstance(header).forgotpassword(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CustomDisposableObserver<ForgotPasswordResponse>() {
                    @Override
                    public void onNext(ForgotPasswordResponse loginResponse) {
                        if (getMvpView() != null) {
                            if (loginResponse.getApiStatus().equalsIgnoreCase(NTF_Utils.ResponseKeys.SUCCESS)) {
                                getMvpView().onSuccess(loginResponse.getApiMessage());
                            } else if (loginResponse.getApiStatus().equalsIgnoreCase(NTF_Utils.ResponseKeys.ERROR)) {
                                getMvpView().onErrorCode(loginResponse.getApiMessage());
                            } else {
                                getMvpView().onErrorCode(loginResponse.getApiMessage());
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
