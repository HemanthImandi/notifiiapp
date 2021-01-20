package com.notifii.notifiiapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.notifii.notifiiapp.activities.LoginActivity;
import com.notifii.notifiiapp.helpers.NTF_PrefsManager;
import com.notifii.notifiiapp.helpers.SingleTon;
import com.notifii.notifiiapp.mvp.models.RecipientListRequest;
import com.notifii.notifiiapp.mvp.presenters.GlobalConstantsPresenter;
import com.notifii.notifiiapp.mvp.presenters.RecipientsListPresenter;
import com.notifii.notifiiapp.mvp.views.GlobalConstanctsView;
import com.notifii.notifiiapp.mvp.views.RecipientsListView;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;

import org.json.JSONObject;

public class NetworkChangeReceiver extends BroadcastReceiver implements GlobalConstanctsView, RecipientsListView {
    GlobalConstantsPresenter globalConstantsPresenter;
    RecipientsListPresenter recipientsListPresenter;
    private NTF_PrefsManager ntf_Preferences;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (this.context == null) {
            this.context = context;
        }
        if (ntf_Preferences == null) {
            ntf_Preferences = new NTF_PrefsManager(context);
        }

        if (NTF_Utils.isOnline(context) && ntf_Preferences.hasKey(NTF_Constants.Prefs_Keys.SYNC_FAILED_DUE_TO_CONNECTIVITY)) {
            ntf_Preferences.removeKey(NTF_Constants.Prefs_Keys.SYNC_FAILED_DUE_TO_CONNECTIVITY);
            globalConstantsPresenter = new GlobalConstantsPresenter();
            globalConstantsPresenter.attachMvpView(this);
            recipientsListPresenter = new RecipientsListPresenter();
            recipientsListPresenter.attachMvpView(this);
            getGlobalConstants();
        }

    }

    //Preparing Service request for getting global constants
    private void getGlobalConstants() {
        if (NTF_Utils.isOnline(context)) {
            globalConstantsPresenter.getGlobalConstancts(null, NTF_Utils.getGlobalConstantsRequestObject("login_action", ntf_Preferences));
        }
    }


    @Override
    public void getGlobalConstanctsFail(String message) {
        NTF_Utils.backgroundCallCompleted(context,message,"");
    }

    @Override
    public void getGlobalConstanctsSuccess(JSONObject response) {
        ntf_Preferences.save(NTF_Constants.Prefs_Keys.IS_FIELDS_REFRESHED, NTF_Constants.Extras_Keys.FIELDS_REFRESHED);
        NTF_Utils.saveGlobalConstants(context, response, ntf_Preferences);
        gettingRecipientList();
    }

    // And Preparing service request for Recipient List...
    public void gettingRecipientList() {
        RecipientListRequest recipientListRequest = new RecipientListRequest();
        recipientListRequest.setApiMode("refresh_action");
        recipientListRequest.setSessionId(ntf_Preferences.get(NTF_Constants.Prefs_Keys.SESSION_ID));
        recipientListRequest.setAuthenticationToken(ntf_Preferences.get(NTF_Constants.Prefs_Keys.AUTHENTICATION_TOKEN));
        recipientListRequest.setAccountId(ntf_Preferences.get(NTF_Constants.Prefs_Keys.ACCOUNT_ID));
        recipientListRequest.setUserId(ntf_Preferences.get(NTF_Constants.Prefs_Keys.USER_ID));
        recipientsListPresenter.getRecipientList(null, recipientListRequest);
    }

    @Override
    public void onRecipientsListSuccess(JSONObject jsonObject) {
        if (jsonObject != null) {
            NTF_Utils.saveRecipientsData(context, jsonObject);
        }
        NTF_Utils.backgroundCallCompleted(context,"","");
        SingleTon.getInstance().setLogPkgInNeedRefesh(true);

    }

    @Override
    public void onRecipientListError(String error) {

    }

    @Override
    public void onSessionExpired(String message) {
        NTF_Utils.backgroundCallCompleted(context,message,NTF_Constants.ResponseKeys.SESSION_EXPIRED );

    }

    @Override
    public void onServerError() {

    }

    @Override
    public Context getMvpContext() {
        return context;
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onNoInternetConnection() {

    }

    @Override
    public void onErrorCode(String s) {

    }
}
