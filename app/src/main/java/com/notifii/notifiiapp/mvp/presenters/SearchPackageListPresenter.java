package com.notifii.notifiiapp.mvp.presenters;

import com.achercasky.initialclasses.mvp.presenter.BasePresenter;
import com.notifii.notifiiapp.mvp.models.SearchPackageRequest;
import com.notifii.notifiiapp.mvp.views.PackagHistoryView;
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

public class SearchPackageListPresenter extends BasePresenter<PackagHistoryView> {

    public void getSearchPackageList(String header, SearchPackageRequest request) {
        Disposable disposable = NTFTrackService.getInstance(header).getSearchPackageList(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CustomDisposableObserver<ResponseBody>() {
                    @Override
                    public void onNext(ResponseBody response) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response.string());
                            String apiStatus = jsonObject.optString("api_status");
                            String message = jsonObject.optString("api_message");
                            if (apiStatus.toLowerCase().equals(NTF_Constants.ResponseKeys.SESSION_EXPIRED)) {
                                getMvpView().onSessionExpired(message);
                            } else if(apiStatus.toLowerCase().equals(NTF_Constants.ResponseKeys.SUCCESS)) {
                                getMvpView().onPackageHistoryforAccountSuccess(jsonObject);
                            } else if(apiStatus.toLowerCase().equals(NTF_Constants.ResponseKeys.ERROR)) {
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
