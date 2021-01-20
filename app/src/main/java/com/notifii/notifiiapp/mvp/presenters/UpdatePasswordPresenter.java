package com.notifii.notifiiapp.mvp.presenters;

import android.text.TextUtils;

import com.achercasky.initialclasses.mvp.presenter.BasePresenter;
import com.notifii.notifiiapp.mvp.models.UpdateNamEmailRequest;
import com.notifii.notifiiapp.mvp.models.UpdateNamEmailResponse;
import com.notifii.notifiiapp.mvp.models.UpdatePasswordRequest;
import com.notifii.notifiiapp.mvp.models.UpdatePasswordResponse;
import com.notifii.notifiiapp.mvp.views.UserNameEmailView;
import com.notifii.notifiiapp.mvp.views.UserPasswordView;
import com.notifii.notifiiapp.network.CustomDisposableObserver;
import com.notifii.notifiiapp.network.NTFTrackService;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class UpdatePasswordPresenter extends BasePresenter<UserPasswordView> {

    public void doCheckCredentials(String updatePassword, String confirmPassword) {
        if (updatePassword.isEmpty()) {
            getMvpView().onInvalidPassword("Please enter new password.");
            return;
        } else if (confirmPassword.isEmpty()) {
            getMvpView().onInvalidPassword("Please enter confirm password.");
            return;
        } else if (!updatePassword.equals(confirmPassword)) {
            getMvpView().onInvalidPassword("Password and confirm password do not match.");
            return;
        }

        String invalidmsg = NTF_Utils.getPasswordInvalidMessage(updatePassword);
        if (invalidmsg != null) {
          getMvpView().onInvalidPassword(invalidmsg);
            return;
        }

        getMvpView().onCredentialsValidated();
    }




    public void doUserPasswordSubmit(String header, UpdatePasswordRequest request) {
        Disposable disposable = NTFTrackService.getInstance(header).doUserPasswordSubmit(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CustomDisposableObserver<UpdatePasswordResponse>() {
                    @Override
                    public void onNext(UpdatePasswordResponse updatePasswordResponse) {
                        if (getMvpView() != null) {
                            if (updatePasswordResponse.getApiStatus().toLowerCase().equals(NTF_Constants.ResponseKeys.SESSION_EXPIRED) ) {
                                getMvpView().onSessionExpired(updatePasswordResponse.getApiMessage());
                            } else if (updatePasswordResponse.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.SUCCESS)) {
                                getMvpView().onPasswordSucess(updatePasswordResponse.getApiMessage());
                            } else if (updatePasswordResponse.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.ERROR)) {
                                getMvpView().onErrorCode(updatePasswordResponse.getApiMessage());
                            } else {
                                getMvpView().onErrorCode(updatePasswordResponse.getApiMessage());
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
