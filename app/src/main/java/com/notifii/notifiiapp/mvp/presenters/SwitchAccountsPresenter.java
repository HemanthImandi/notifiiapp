package com.notifii.notifiiapp.mvp.presenters;

import com.achercasky.initialclasses.mvp.presenter.BasePresenter;
import com.notifii.notifiiapp.mvp.models.LoginResponse;
import com.notifii.notifiiapp.mvp.models.MultisiteLoginRequest;
import com.notifii.notifiiapp.mvp.models.SwitchAccountRequest;
import com.notifii.notifiiapp.mvp.views.MultisiteLoginView;
import com.notifii.notifiiapp.network.CustomDisposableObserver;
import com.notifii.notifiiapp.network.NTFTrackService;
import com.notifii.notifiiapp.utils.NTF_Constants;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SwitchAccountsPresenter extends BasePresenter<MultisiteLoginView> {

    public void doSwitchAccounts(String header, SwitchAccountRequest request) {
        Disposable disposable = NTFTrackService.getInstance(header).doSwitchAccounts(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CustomDisposableObserver<LoginResponse>() {
                    @Override
                    public void onNext(LoginResponse response) {
                        if (getMvpView() != null) {
                            if (response.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.MFA_API_STATUS)) {
                                getMvpView().onMFAConfigured(response);
                            } else if (response.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.SUCCESS)
                                    || response.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.RESET_PASSWORD)
                                    || response.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.RESET_USERNAME_PASSWORD)) {
                                getMvpView().onMultisiteLoginSuccess(response);
                            } else if (response.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.ACCOUNT_CLOSED)
                                    || response.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.ERROR)) {
                                getMvpView().onErrorCode(response.getApiMessage());
                            } else if (response.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.SESSION_EXPIRED)) {
                                getMvpView().onSessionExpired(response.getApiMessage());
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
