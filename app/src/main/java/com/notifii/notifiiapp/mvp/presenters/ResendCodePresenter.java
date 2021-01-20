package com.notifii.notifiiapp.mvp.presenters;

import com.achercasky.initialclasses.mvp.presenter.BasePresenter;
import com.notifii.notifiiapp.mvp.models.ResendMFACodeRequest;
import com.notifii.notifiiapp.mvp.models.ResendMFACodeResponse;
import com.notifii.notifiiapp.mvp.views.ResendCodeView;
import com.notifii.notifiiapp.network.CustomDisposableObserver;
import com.notifii.notifiiapp.network.NTFTrackService;
import com.notifii.notifiiapp.utils.NTF_Constants;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ResendCodePresenter extends BasePresenter<ResendCodeView> {

    public void resendVerificationCode(String header, ResendMFACodeRequest request) {
        Disposable disposable = NTFTrackService.getInstance(header).resendVerificationCode(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CustomDisposableObserver<ResendMFACodeResponse>() {
                    @Override
                    public void onNext(ResendMFACodeResponse response) {
                        if (getMvpView() != null) {
                            if (response.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.SUCCESS)) {
                                getMvpView().onResendSuccess(response.getApiMessage());
                            } else if (response.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.ERROR)) {
                                getMvpView().onResndFail(response.getApiMessage());
                            } else if (response.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.DISABLE_RESEND)) {
                                getMvpView().onDisableResend(response.getApiMessage());
                            } else {
                                getMvpView().onErrorCode(response.getApiMessage());
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