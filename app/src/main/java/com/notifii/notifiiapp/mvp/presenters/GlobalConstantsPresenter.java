package com.notifii.notifiiapp.mvp.presenters;

import com.achercasky.initialclasses.mvp.presenter.BasePresenter;
import com.notifii.notifiiapp.mvp.models.GetGlobalConstantsRequest;
import com.notifii.notifiiapp.mvp.views.GlobalConstanctsView;
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

public class GlobalConstantsPresenter extends BasePresenter<GlobalConstanctsView> {

    public void getGlobalConstancts(String header, GetGlobalConstantsRequest request) {
        Disposable disposable = NTFTrackService.getInstance(header).getGlobalConstancts(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CustomDisposableObserver<ResponseBody>() {
                    @Override
                    public void onNext(ResponseBody response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.string());
                            if (getMvpView() != null) {
                                if (jsonObject.optString("api_status").equalsIgnoreCase(NTF_Utils.ResponseKeys.SESSION_EXPIRED)) {
                                    getMvpView().onSessionExpired(jsonObject.optString("api_message"));
                                } else if (jsonObject.optString("api_status").equalsIgnoreCase(NTF_Utils.ResponseKeys.SUCCESS)){
                                    getMvpView().getGlobalConstanctsSuccess(jsonObject);
                                } else if (jsonObject.optString("api_status").equalsIgnoreCase(NTF_Utils.ResponseKeys.ERROR)){
                                    getMvpView().getGlobalConstanctsFail(jsonObject.optString("api_message"));
                                } else {
                                    getMvpView().getGlobalConstanctsFail(jsonObject.optString("api_message"));
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
                            getMvpView().onError(t);
                        }
                    }
                });
        compositeSubscription.add(disposable);
    }

}
