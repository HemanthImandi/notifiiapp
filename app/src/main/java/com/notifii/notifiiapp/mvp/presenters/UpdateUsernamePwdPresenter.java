package com.notifii.notifiiapp.mvp.presenters;

import com.achercasky.initialclasses.mvp.presenter.BasePresenter;
import com.notifii.notifiiapp.mvp.models.UpdateUsernamePwdRequest;
import com.notifii.notifiiapp.mvp.models.UpdateUsernamePwdResponse;
import com.notifii.notifiiapp.mvp.models.UsernameCheckAvailabilityRequest;
import com.notifii.notifiiapp.mvp.models.UsernameCheckAvailabilityResponse;
import com.notifii.notifiiapp.mvp.views.UpdateUsernamePwdView;
import com.notifii.notifiiapp.network.CustomDisposableObserver;
import com.notifii.notifiiapp.network.NTFTrackService;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;

import java.util.regex.Pattern;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class UpdateUsernamePwdPresenter extends BasePresenter<UpdateUsernamePwdView> {

    public void checkPasswordValidated(String newPassword, String confirmPassword) {

        if (newPassword.isEmpty()) {
            getMvpView().fieldsNotValid("Please enter new password.");
            return;
        }
        if (confirmPassword.isEmpty()) {
            getMvpView().fieldsNotValid("Please enter confirm password.");
            return;
        }
        if (!confirmPassword.equals(newPassword)) {
            getMvpView().fieldsNotValid("Password and confirm password do not match.");
            return;
        }
        String invalidmsg = NTF_Utils.getPasswordInvalidMessage(newPassword);
        if (invalidmsg != null) {
            getMvpView().fieldsNotValid(invalidmsg);
            return;
        }
        getMvpView().onFieldsValidated("");
    }

    public void checkUsernamePasswordValidated(String username, String newPassword, String confirmPassword) {
        Pattern specialCharactersPattern = Pattern.compile("[&'><\\\"]", Pattern.CASE_INSENSITIVE);
        if (username.isEmpty()) {
            getMvpView().fieldsNotValid("Please enter your username.");
            return;
        }
        if (username.length()<6 || username.contains(" ")) {
            getMvpView().fieldsNotValid("Username must be at least 6 characters long & no spaces.");
            return;
        }
        if (specialCharactersPattern.matcher(username).find()) {
            getMvpView().fieldsNotValid("Username does not allow this characters & ' > < \" ");
            return;
        }
        if (newPassword.isEmpty()) {
            getMvpView().fieldsNotValid("Please enter new password.");
            return;
        }
        if (confirmPassword.isEmpty()) {
            getMvpView().fieldsNotValid("Please enter confirm password.");
            return;
        }
        if (!confirmPassword.equals(newPassword)) {
            getMvpView().fieldsNotValid("Password and confirm password do not match.");
            return;
        }
        String invalidmsg = NTF_Utils.getPasswordInvalidMessage(newPassword);
        if (invalidmsg != null) {
            getMvpView().fieldsNotValid(invalidmsg);
            return;
        }
        getMvpView().onFieldsValidated(username);
    }

    public void doCheckUsernameAvailabilty(String username) {
        Pattern specialCharactersPattern = Pattern.compile("[&'><\\\"]", Pattern.CASE_INSENSITIVE);
        if (username.isEmpty()) {
            getMvpView().fieldsNotValid("Please enter your username.");
            return;
        }
        if (username.length()<6 || username.contains(" ")) {
            getMvpView().fieldsNotValid("Username must be at least 6 characters long & no spaces.");
            return;
        }
        if (specialCharactersPattern.matcher(username).find()) {
            getMvpView().fieldsNotValid("Username does not allow this characters & ' > < \" ");
            return;
        }
        getMvpView().onUserNameValidated();
    }

    public void updatingUsernamePwd(String header, UpdateUsernamePwdRequest request) {
        Disposable disposable = NTFTrackService.getInstance(header).doUpdate(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CustomDisposableObserver<UpdateUsernamePwdResponse>() {
                    @Override
                    public void onNext(UpdateUsernamePwdResponse response) {
                        if (getMvpView() != null) {
                            if (response.getApiStatus().equals(NTF_Utils.ResponseKeys.SESSION_EXPIRED)) {
                                getMvpView().onSessionExpired(response.getApiMessage());
                            } else if (response.getApiStatus().toLowerCase().equalsIgnoreCase(NTF_Utils.ResponseKeys.SUCCESS)) {
                                getMvpView().onUpdatingSuccess(response);
                            } else {
                                getMvpView().onUpdatingFailure(response.getApiMessage());
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
                    public void onError(Throwable e) {
                        if (getMvpView() != null) {
                            getMvpView().onError(e);
                        }
                    }
                });
        compositeSubscription.add(disposable);


    }

    public void checkingUsernameAvailability(String header, UsernameCheckAvailabilityRequest request) {
        Disposable disposable = NTFTrackService.getInstance(header).doCheckAvailability(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CustomDisposableObserver<UsernameCheckAvailabilityResponse>() {
                    @Override
                    public void onNext(UsernameCheckAvailabilityResponse response) {
                        if (getMvpView() != null) {
                            if (response.getApiStatus().equals(NTF_Constants.ResponseKeys.SESSION_EXPIRED)) {
                                getMvpView().onSessionExpired(response.getApiMessage());
                            } else if (response.getApiStatus().toLowerCase().equalsIgnoreCase(NTF_Utils.ResponseKeys.AVAILABLE)) {
                                getMvpView().onCheckingSuccess(response);
                            } else if(response.getApiStatus().toLowerCase().equalsIgnoreCase(NTF_Utils.ResponseKeys.TAKEN)){
                                getMvpView().userNameTaken();
                            } else {
                                getMvpView().onCheckingFailure(response.getApiMessage());
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
                    public void onError(Throwable e) {
                        if (getMvpView() != null) {
                            getMvpView().onError(e);
                        }
                    }
                });
        compositeSubscription.add(disposable);
    }
}
