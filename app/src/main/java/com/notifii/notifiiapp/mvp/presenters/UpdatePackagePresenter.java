package com.notifii.notifiiapp.mvp.presenters;

import com.achercasky.initialclasses.mvp.presenter.BasePresenter;

import com.notifii.notifiiapp.mvp.models.UpdatePackageResponse;
import com.notifii.notifiiapp.mvp.views.UpdatePackageView;
import com.notifii.notifiiapp.network.CustomDisposableObserver;
import com.notifii.notifiiapp.network.NTFTrackService;
import com.notifii.notifiiapp.utils.NTF_Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class UpdatePackagePresenter extends BasePresenter<UpdatePackageView> {

    public void checkMultipleEditRequest(JSONObject request, boolean isMultipleMailroom) {
        try {
            if ((request.optString("shelf") == null || request.optString("shelf").isEmpty()) &&
                    (request.optString("shipping_carrier") == null || request.optString("shipping_carrier").isEmpty()) &&
                    (request.optString("sender") == null || request.optString("sender").isEmpty()) &&
                    (request.optString("service_type") == null || request.optString("service_type").isEmpty()) &&
                    (request.optString("package_type") == null || request.optString("package_type").isEmpty()) &&
                    (request.optString("package_condition") == null || request.optString("package_condition").isEmpty()) &&
                    (request.optString("staff_note_replace") == null || request.optString("staff_note_replace").isEmpty()) &&
                    (request.optJSONArray("package_pictures") == null || request.optJSONArray("package_pictures").length()==0) &&
                    (request.optString("special_handlings") == null || request.optString("special_handlings").isEmpty()) &&
                    (request.optString("po_number") == null || request.optString("po_number").isEmpty()) &&
                    (request.optString("weight") == null || request.optString("weight").isEmpty()) &&
                    (request.optString("dimensions") == null || request.optString("dimensions").isEmpty())) {
                if (isMultipleMailroom) {
                    if (request.optString("mailroom_id") == null || request.optString("mailroom_id").isEmpty()) {
                        getMvpView().onEmptyAllParameters();
                    } else {
                        getMvpView().multipleEditRequestValidated(request);
                    }
                } else {
                    getMvpView().onEmptyAllParameters();
                }
            } else {
                getMvpView().multipleEditRequestValidated(request);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updatingMultiplePackages(String header, RequestBody request) {
        Disposable disposable = NTFTrackService.getInstance(header).doUpdateMultiplePackages(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CustomDisposableObserver<UpdatePackageResponse>() {
                    @Override
                    public void onConnectionLost() {
                        if (getMvpView() != null) {
                            getMvpView().onNoInternetConnection();
                        }
                    }

                    @Override
                    public void onNext(UpdatePackageResponse response) {
                        if (getMvpView() != null) {
                            if (response.getApiStatus().equals(NTF_Constants.ResponseKeys.SESSION_EXPIRED)) {
                                getMvpView().onSessionExpired(response.getApiMessage());
                            } else if (response.getApiStatus().toLowerCase().equalsIgnoreCase(NTF_Constants.ResponseKeys.SUCCESS)) {
                                getMvpView().onUpdatingMultiplePkgsSuccess();
                            } else if (response.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.ERROR)) {
                                getMvpView().onErrorCode(response.getApiMessage());
                            } else {
                                getMvpView().onErrorCode(response.getApiMessage());
                            }
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

    public void updatingSinglePackage(String header, RequestBody requestBody) {
        Disposable disposable = NTFTrackService.getInstance(header).doUpdateSinglePkg(requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CustomDisposableObserver<ResponseBody>() {


                    @Override
                    public void onNext(ResponseBody response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.string());
                            if (getMvpView() != null) {
                                if (jsonObject.optString("api_status").equals(NTF_Constants.ResponseKeys.SESSION_EXPIRED)) {
                                    getMvpView().onSessionExpired(jsonObject.optString("api_message"));
                                } else if (jsonObject.optString("api_status").equals(NTF_Constants.ResponseKeys.SUCCESS)) {
                                    getMvpView().onUpdatingSinglePkgSuccess(jsonObject);
                                } else if (jsonObject.optString("api_status").equals(NTF_Constants.ResponseKeys.ERROR)) {
                                    getMvpView().onErrorCode(jsonObject.optString("api_message"));
                                } else {
                                    getMvpView().onErrorCode(jsonObject.optString("api_message"));
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

