package com.notifii.notifiiapp.mvp.presenters;

import com.achercasky.initialclasses.mvp.presenter.BasePresenter;
import com.notifii.notifiiapp.mvp.models.LoginResponse;
import com.notifii.notifiiapp.mvp.models.SsoLoginResponseRequest;
import com.notifii.notifiiapp.mvp.views.KioskSsoLoginResponseView;
import com.notifii.notifiiapp.network.CustomDisposableObserver;
import com.notifii.notifiiapp.network.NTFTrackService;
import com.notifii.notifiiapp.utils.NTF_Constants;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class KioskSsoLoginResponsePresenter extends BasePresenter<KioskSsoLoginResponseView> {

    public void doSsoLogin(String header, SsoLoginResponseRequest loginRequest) {
        Disposable disposable = NTFTrackService.getInstance(header).doSsoLogin(loginRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CustomDisposableObserver<LoginResponse>() {
                    @Override
                    public void onNext(LoginResponse loginResponse) {
                        if (getMvpView() != null) {
                            if (loginResponse.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.SESSION_EXPIRED)) {
                                getMvpView().onSessionExpired(loginResponse.getApiMessage());
                            } else if (loginResponse.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.SUCCESS)
                                    || loginResponse.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.RESET_PASSWORD)
                                    || loginResponse.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.RESET_USERNAME_PASSWORD)) {
                                getMvpView().onKioskLoginSuccess(loginResponse);
                            } else if (loginResponse.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.ACCOUNT_CLOSED)
                                    || loginResponse.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.ERROR)) {
                                getMvpView().onAccountClosedOrSuspended(loginResponse);
                            } else {
                                getMvpView().onKioskOtherResults(loginResponse);
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
