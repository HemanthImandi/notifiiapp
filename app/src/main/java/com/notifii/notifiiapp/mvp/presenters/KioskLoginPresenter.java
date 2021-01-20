package com.notifii.notifiiapp.mvp.presenters;

import com.achercasky.initialclasses.mvp.presenter.BasePresenter;
import com.notifii.notifiiapp.helpers.NTF_PrefsManager;
import com.notifii.notifiiapp.mvp.models.KioskLoginRequest;
import com.notifii.notifiiapp.mvp.models.KioskLoginResponse;
import com.notifii.notifiiapp.mvp.views.KioskLoginView;
import com.notifii.notifiiapp.network.CustomDisposableObserver;
import com.notifii.notifiiapp.network.NTFTrackService;
import com.notifii.notifiiapp.utils.NTF_Constants;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class KioskLoginPresenter extends BasePresenter<KioskLoginView> {

    private boolean isKioskInProgress;

    public void doKioskLogin(String header, KioskLoginRequest loginRequest, NTF_PrefsManager prefsManager) {
        if (isKioskInProgress)
            return;
        isKioskInProgress = true;
        Disposable disposable = NTFTrackService.getInstance(header).kioskLogin(loginRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CustomDisposableObserver<KioskLoginResponse>() {
                    @Override
                    public void onNext(KioskLoginResponse loginResponse) {
                        if (getMvpView() != null) {
                            if (loginResponse.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.SUCCESS)) {
                                getMvpView().onKioskLoginSuccess(loginResponse);
                            } else if (loginResponse.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.SESSION_EXPIRED)) {
                                getMvpView().onSessionExpired(loginResponse.getApiMessage());
                            } else if (loginResponse.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.ERROR)) {
                                getMvpView().onErrorCode(loginResponse.getApiMessage());
                                prefsManager.save(NTF_Constants.Prefs_Keys.IS_KIOSK_MODE, false);
                            } else {
                                getMvpView().onErrorCode(loginResponse.getApiMessage());
                                prefsManager.save(NTF_Constants.Prefs_Keys.IS_KIOSK_MODE, false);
                            }
                            isKioskInProgress = false;
                        }
                    }

                    @Override
                    public void onConnectionLost() {
                        isKioskInProgress = false;
                        if (getMvpView() != null) {
                            getMvpView().onNoInternetConnection();
                            prefsManager.save(NTF_Constants.Prefs_Keys.IS_KIOSK_MODE, false);
                        }
                    }

                    @Override
                    public void onServerError() {
                        isKioskInProgress = false;
                        if (getMvpView() != null) {
                            getMvpView().onServerError();
                            prefsManager.save(NTF_Constants.Prefs_Keys.IS_KIOSK_MODE, false);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        isKioskInProgress = false;
                        if (getMvpView() != null) {
                            getMvpView().onError(t);
                            prefsManager.save(NTF_Constants.Prefs_Keys.IS_KIOSK_MODE, false);
                        }
                    }
                });
        compositeSubscription.add(disposable);

    }
}
