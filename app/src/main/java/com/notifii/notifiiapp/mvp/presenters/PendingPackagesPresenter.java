package com.notifii.notifiiapp.mvp.presenters;

import com.achercasky.initialclasses.mvp.presenter.BasePresenter;
import com.notifii.notifiiapp.helpers.SingleTon;
import com.notifii.notifiiapp.mvp.models.PendingPackagesRequest;
import com.notifii.notifiiapp.mvp.views.PendingPackagesView;
import com.notifii.notifiiapp.network.CustomDisposableObserver;
import com.notifii.notifiiapp.network.NTFTrackService;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class PendingPackagesPresenter extends BasePresenter<PendingPackagesView> {

    public void getPendingPackages(String header, PendingPackagesRequest request) {
        Disposable disposable = NTFTrackService.getInstance(header).getPendingPackages(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CustomDisposableObserver<ResponseBody>() {
                    @Override
                    public void onNext(ResponseBody response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.string());
                            if (getMvpView() != null) {
                                if (jsonObject.optString("api_status").equalsIgnoreCase(NTF_Constants.ResponseKeys.SUCCESS)) {
                                    getMvpView().onPendingPackagesSuccess(jsonObject);
                                } else if (jsonObject.optString("api_status").equalsIgnoreCase(NTF_Constants.ResponseKeys.SESSION_EXPIRED)) {
                                    getMvpView().onSessionExpired(jsonObject.optString("api_message"));
                                } else if (jsonObject.optString("api_status").equalsIgnoreCase(NTF_Constants.ResponseKeys.ERROR)) {
                                    getMvpView().onPendingPackagesFail(jsonObject.optString("api_message"));
                                } else {
                                    getMvpView().onPendingPackagesFail(jsonObject.optString("api_message"));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
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
                            getMvpView().onPendingPackagesFail(NTF_Utils.getErrorMessage(t));
                        }
                    }
                });
        compositeSubscription.add(disposable);
    }

    public void cancelRequests() {
        compositeSubscription.clear();
    }
}
