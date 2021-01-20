package com.notifii.notifiiapp.mvp.presenters;

import android.text.TextUtils;

import com.achercasky.initialclasses.mvp.presenter.BasePresenter;
import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.mvp.models.UpdateNamEmailRequest;
import com.notifii.notifiiapp.mvp.models.UpdateNamEmailResponse;
import com.notifii.notifiiapp.mvp.models.UpdateUserNameRequest;
import com.notifii.notifiiapp.mvp.models.UpdateUserNameResponse;
import com.notifii.notifiiapp.mvp.views.UserNameEmailView;
import com.notifii.notifiiapp.mvp.views.UserNameView;
import com.notifii.notifiiapp.network.CustomDisposableObserver;
import com.notifii.notifiiapp.network.NTFTrackService;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class UpdateUserNamePresenter extends BasePresenter<UserNameView> {

    public void doCheckCredentials(String name) {
        if (name.isEmpty()) {
            getMvpView().onInvalidName("Please enter your username.");
            return;
        } else if (name.length() < 6 || name.contains(" ")) {
            getMvpView().onInvalidName("Username must be at least 6 characters long & no spaces.");
            return;
        }
        String invalidmsg = NTF_Utils.getUsernameInvalidMessage(name);
        if (invalidmsg != null) {
            getMvpView().onInvalidName(invalidmsg);
            return;
        }

        getMvpView().onCredentialsValidated();
    }

    public void doUserNameSubmit(String header, UpdateUserNameRequest request) {
        Disposable disposable = NTFTrackService.getInstance(header).doUserNameSubmit(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CustomDisposableObserver<UpdateUserNameResponse>() {
                    @Override
                    public void onNext(UpdateUserNameResponse updateUserNameResponse) {
                        if (getMvpView() != null) {
                            if (updateUserNameResponse.getApiStatus().toLowerCase().equals(NTF_Constants.ResponseKeys.SESSION_EXPIRED)) {
                                getMvpView().onSessionExpired(updateUserNameResponse.getApiMessage());
                            } else if (updateUserNameResponse.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.SUCCESS)) {
                                getMvpView().onSuccessUserName(updateUserNameResponse.getApiMessage());
                            } else if (updateUserNameResponse.getApiStatus().toLowerCase().equalsIgnoreCase(NTF_Constants.ResponseKeys.TAKEN)) {
                                getMvpView().onUserNotAvailable(updateUserNameResponse.getApiMessage());
                            } else if (updateUserNameResponse.getApiStatus().toLowerCase().equalsIgnoreCase(NTF_Constants.ResponseKeys.ERROR)) {
                                getMvpView().onErrorCode(updateUserNameResponse.getApiMessage());
                            } else {
                                getMvpView().onErrorCode(updateUserNameResponse.getApiMessage());
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
