package com.notifii.notifiiapp.mvp.presenters;

import com.achercasky.initialclasses.mvp.presenter.BasePresenter;
import com.notifii.notifiiapp.mvp.models.RecipientPendingPackagesRequest;
import com.notifii.notifiiapp.mvp.models.RecipientPendingPackagesResponse;
import com.notifii.notifiiapp.mvp.views.RecipientPendingPackagesView;
import com.notifii.notifiiapp.network.CustomDisposableObserver;
import com.notifii.notifiiapp.network.NTFTrackService;
import com.notifii.notifiiapp.utils.NTF_Constants;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RecipientPendingPackagesPresenter extends BasePresenter<RecipientPendingPackagesView> {
    public void getRecipientPackages(String header, RecipientPendingPackagesRequest loginRequest) {
        Disposable disposable = NTFTrackService.getInstance(header).getRecipientPackages(loginRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CustomDisposableObserver<RecipientPendingPackagesResponse>() {
                    @Override
                    public void onNext(RecipientPendingPackagesResponse loginResponse) {
                        if (getMvpView() != null) {
                            if (loginResponse.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.SESSION_EXPIRED)) {
                                getMvpView().onSessionExpired(loginResponse.getApiMessage());
                            } else if (loginResponse.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.SUCCESS)){
                                getMvpView().onRequestSuccess(loginResponse);
                            }  else if (loginResponse.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.ERROR)){
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
