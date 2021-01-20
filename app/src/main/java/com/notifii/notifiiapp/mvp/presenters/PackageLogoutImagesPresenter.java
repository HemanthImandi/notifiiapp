package com.notifii.notifiiapp.mvp.presenters;

import com.achercasky.initialclasses.mvp.presenter.BasePresenter;
import com.notifii.notifiiapp.mvp.models.PackageLogoutImagesRequest;
import com.notifii.notifiiapp.mvp.models.PackageLogoutImagesResponse;
import com.notifii.notifiiapp.mvp.views.PackageLogoutImagesView;
import com.notifii.notifiiapp.network.CustomDisposableObserver;
import com.notifii.notifiiapp.network.NTFTrackService;
import com.notifii.notifiiapp.utils.NTF_Constants;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PackageLogoutImagesPresenter extends BasePresenter<PackageLogoutImagesView> {

    public void logoutPkgImages(String header, PackageLogoutImagesRequest request) {
        Disposable disposable = NTFTrackService.getInstance(header).logoutPkgImages(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CustomDisposableObserver<PackageLogoutImagesResponse>() {
                    @Override
                    public void onNext(PackageLogoutImagesResponse response) {
                        if (getMvpView() != null) {
                            if (response.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.SESSION_EXPIRED)) {
                                getMvpView().onSessionExpired(response.getApiMessage());
                            } else if (response.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.SUCCESS)) {
                                getMvpView().onImagesSuccess();
                            } else if (response.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.WARNING)) {
                                getMvpView().onOtherResults(response.getApiMessage());
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
