package com.notifii.notifiiapp.mvp.presenters;

import com.achercasky.initialclasses.mvp.presenter.BasePresenter;
import com.notifii.notifiiapp.mvp.models.LogPackageDataRequest;
import com.notifii.notifiiapp.mvp.models.LogPackageDataResponse;
import com.notifii.notifiiapp.mvp.models.LogPkgImagesRequest;
import com.notifii.notifiiapp.mvp.models.LogPkgImagesResponse;
import com.notifii.notifiiapp.mvp.views.LogPkgDataView;
import com.notifii.notifiiapp.network.CustomDisposableObserver;
import com.notifii.notifiiapp.network.NTFTrackService;
import com.notifii.notifiiapp.utils.NTF_Constants;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LogOutPackageDetailsPresenter extends BasePresenter<LogPkgDataView> {

    public void doPackageDataLogout(String header, LogPackageDataRequest request) {

        Disposable disposable = NTFTrackService.getInstance(header).doPkgLogout(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CustomDisposableObserver<LogPackageDataResponse>() {
                    @Override
                    public void onNext(LogPackageDataResponse response) {
                        if (getMvpView() != null) {
                            if (response.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.SESSION_EXPIRED)){
                                getMvpView().onSessionExpired(response.getApiMessage());
                            } else if (response.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.WARNING)) {
                                getMvpView().onGettingWarning(response.getApiMessage());
                            } else if (response.getApiStatus().toLowerCase().equals(NTF_Constants.ResponseKeys.SUCCESS)){
                                getMvpView().onPkgDataLogoutSuccess(response.getApiMessage());
                            }  else if (response.getApiStatus().toLowerCase().equals(NTF_Constants.ResponseKeys.ERROR)){
                                getMvpView().onErrorCode(response.getApiMessage());
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


    public void doPackageImagesLogout(String header, LogPkgImagesRequest request) {
        Disposable disposable = NTFTrackService.getInstance(header).doPkgImagesLogout(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CustomDisposableObserver<LogPkgImagesResponse>() {
                    @Override
                    public void onNext(LogPkgImagesResponse response) {
                        if (getMvpView() != null) {
                            if (response.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.SESSION_EXPIRED)) {
                                getMvpView().onSessionExpired(response.getApiMessage());
                            } else if (response.getApiStatus().equals(NTF_Constants.ResponseKeys.SUCCESS)) {
                                getMvpView().onPkgImagesLogoutSuccess();
                            } else if (response.getApiStatus().equals(NTF_Constants.ResponseKeys.ERROR)) {
                                getMvpView().onErrorCode(response.getApiMessage());
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
