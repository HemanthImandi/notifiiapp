package com.notifii.notifiiapp.mvp.presenters;

import com.achercasky.initialclasses.mvp.presenter.BasePresenter;
import com.notifii.notifiiapp.mvp.models.LoginRequest;
import com.notifii.notifiiapp.mvp.models.LoginResponse;
import com.notifii.notifiiapp.mvp.views.LoginView;
import com.notifii.notifiiapp.network.CustomDisposableObserver;
import com.notifii.notifiiapp.network.NTFTrackService;
import com.notifii.notifiiapp.utils.NTF_Constants;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LoginPresenter extends BasePresenter<LoginView> {

    public void doCheckCredentials(String username, String password) {

        if (username.trim().isEmpty()) {
            getMvpView().onEmptyUserName();
            return;
        }

        if (password.trim().isEmpty()) {
            getMvpView().onEmptyPassword();
            return;
        }

        getMvpView().onCredentialsValidated(username, password);
    }

    public void doLogin(String header, LoginRequest loginRequest) {
        Disposable disposable = NTFTrackService.getInstance(header).login(loginRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CustomDisposableObserver<LoginResponse>() {
                    @Override
                    public void onNext(LoginResponse loginResponse) {
                        if (getMvpView() != null) {
                            if (loginResponse.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.SUCCESS)
                                    || loginResponse.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.RESET_PASSWORD)
                                    || loginResponse.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.RESET_USERNAME_PASSWORD)) {
                                getMvpView().onLoginSuccess(loginResponse);
                            } else if (loginResponse.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.ACCOUNT_CLOSED)
                                       || loginResponse.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.ERROR)) {
                                getMvpView().onAccountClosedOrSuspended(loginResponse);
                            } else if (loginResponse.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.MFA_API_STATUS)) {
                                getMvpView().onMFAEnabled(loginResponse);
                            } else if (loginResponse.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.MULTISITE_USER)) {
                                getMvpView().onMultisiteEnabled(loginResponse);
                            } else {
                                getMvpView().onLoginFail(loginResponse);
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
