package com.notifii.notifiiapp.mvp.presenters;

import com.achercasky.initialclasses.mvp.presenter.BasePresenter;
import com.notifii.notifiiapp.mvp.models.PackageLogoutDataRequest;
import com.notifii.notifiiapp.mvp.models.PackageLogoutDataResponse;
import com.notifii.notifiiapp.mvp.views.PackageLogoutDataView;
import com.notifii.notifiiapp.network.CustomDisposableObserver;
import com.notifii.notifiiapp.network.NTFTrackService;
import com.notifii.notifiiapp.utils.NTF_Constants;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PackageLogoutDataPresenter extends BasePresenter<PackageLogoutDataView> {

    public void doCheckCredentials(String signature) {

        if (signature.trim().isEmpty()) {
            getMvpView().onEmptySignature();
            return;
        }

        getMvpView().onCredentialsValidated(signature);
    }

    public void logoutPkgData(String header, PackageLogoutDataRequest request) {
        Disposable disposable = NTFTrackService.getInstance(header).logoutPkgData(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CustomDisposableObserver<PackageLogoutDataResponse>() {
                    @Override
                    public void onNext(PackageLogoutDataResponse response) {
                        if (getMvpView() != null) {
                            if (response.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.SESSION_EXPIRED)) {
                                getMvpView().onSessionExpired(response.getApiMessage());
                            } else if (response.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.SUCCESS)) {
                                getMvpView().onDataSuccess();
                            } else if (response.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.WARNING)) {
                                getMvpView().onDataWarning(response.getApiMessage());
                            } else if (response.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.ERROR)) {
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
