package com.notifii.notifiiapp.mvp.presenters;

import com.achercasky.initialclasses.mvp.presenter.BasePresenter;
import com.notifii.notifiiapp.mvp.models.GetPackageDetailsRequest;
import com.notifii.notifiiapp.mvp.views.GetPkgDetailsView;
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

public class GetPkgDetailsPresenter extends BasePresenter<GetPkgDetailsView> {

    public void gettingAllPkgDetails(String header, GetPackageDetailsRequest request) {

        Disposable disposable = NTFTrackService.getInstance(header).doGetPackageDetails(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CustomDisposableObserver<ResponseBody>() {
                    @Override
                    public void onConnectionLost() {
                        if (getMvpView() != null) {
                            getMvpView().onNoInternetConnection();
                        }
                    }

                    @Override
                    public void onNext(ResponseBody response) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response.string());
                            String apiStatus = jsonObject.optString("api_status");
                            String message = jsonObject.optString("api_message");
                            if (apiStatus.toLowerCase().equals(NTF_Constants.ResponseKeys.SESSION_EXPIRED)) {
                              getMvpView().onSessionExpired(message);
                            } else if (jsonObject.optString("api_status").toLowerCase().equalsIgnoreCase(NTF_Utils.ResponseKeys.FOUND)) {
                                getMvpView().onPackageFound(jsonObject);
                            } else if (jsonObject.optString("api_status").toLowerCase().equalsIgnoreCase(NTF_Utils.ResponseKeys.NOT_FOUND)) {
                                getMvpView().onPackageNotFound(message);
                            } else if (jsonObject.optString("api_status").toLowerCase().equalsIgnoreCase(NTF_Utils.ResponseKeys.ERROR)) {
                                getMvpView().onErrorCode(message);
                            } else {
                                getMvpView().onErrorCode(message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
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



