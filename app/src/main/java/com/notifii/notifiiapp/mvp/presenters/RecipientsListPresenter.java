package com.notifii.notifiiapp.mvp.presenters;

import com.achercasky.initialclasses.mvp.presenter.BasePresenter;
import com.notifii.notifiiapp.mvp.models.RecipientListRequest;
import com.notifii.notifiiapp.mvp.views.RecipientsListView;
import com.notifii.notifiiapp.network.CustomDisposableObserver;
import com.notifii.notifiiapp.network.NTFTrackService;
import com.notifii.notifiiapp.utils.NTF_Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class RecipientsListPresenter extends BasePresenter<RecipientsListView> {

    public void getRecipientList(String header, RecipientListRequest request) {
        Disposable disposable = NTFTrackService.getInstance(header).getRecipientList(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CustomDisposableObserver<ResponseBody>() {
                    @Override
                    public void onNext(ResponseBody response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.string());
                            String apiStatus = jsonObject.optString("api_status");
                            String message = jsonObject.optString("api_message");
                            if (apiStatus.toLowerCase().equals(NTF_Constants.ResponseKeys.SESSION_EXPIRED)) {
                                getMvpView().onSessionExpired(message);
                            } else if(apiStatus.toLowerCase().equals(NTF_Constants.ResponseKeys.SUCCESS)) {
                                getMvpView().onRecipientsListSuccess(jsonObject);
                            } else if(apiStatus.toLowerCase().equals(NTF_Constants.ResponseKeys.ERROR)) {
                                getMvpView().onRecipientListError(message);
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
