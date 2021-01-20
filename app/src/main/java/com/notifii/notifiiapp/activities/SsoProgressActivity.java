package com.notifii.notifiiapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.models.SpinnerData;
import com.notifii.notifiiapp.base.NTF_BaseActivity;
import com.notifii.notifiiapp.helpers.SingleTon;
import com.notifii.notifiiapp.mvp.models.LoginResponse;
import com.notifii.notifiiapp.mvp.models.SsoLoginResponseRequest;
import com.notifii.notifiiapp.mvp.presenters.GlobalConstantsPresenter;
import com.notifii.notifiiapp.mvp.presenters.KioskSsoLoginResponsePresenter;
import com.notifii.notifiiapp.mvp.presenters.RecipientsListPresenter;
import com.notifii.notifiiapp.mvp.presenters.SsoLoginResponsePresenter;
import com.notifii.notifiiapp.mvp.views.GlobalConstanctsView;
import com.notifii.notifiiapp.mvp.views.KioskSsoLoginResponseView;
import com.notifii.notifiiapp.mvp.views.RecipientsListView;
import com.notifii.notifiiapp.mvp.views.SsoLoginResponseView;
import com.notifii.notifiiapp.refresh.EventHandler;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;

import org.json.JSONObject;

import java.util.ArrayList;

public class SsoProgressActivity extends NTF_BaseActivity implements SsoLoginResponseView, GlobalConstanctsView, RecipientsListView, KioskSsoLoginResponseView {

    private boolean isfromkiosk = false;
    private static EventHandler eventHandler;
    SsoLoginResponsePresenter ssoLoginResponsePresenter;
    GlobalConstantsPresenter globalConstantsPresenter;
    RecipientsListPresenter recipientsListPresenter;
    KioskSsoLoginResponsePresenter kioskSsoLoginResponsePresenter;
    private String apiStatus = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sso_progress);
        ssoLoginResponsePresenter = new SsoLoginResponsePresenter();
        ssoLoginResponsePresenter.attachMvpView(this);
        globalConstantsPresenter = new GlobalConstantsPresenter();
        globalConstantsPresenter.attachMvpView(this);
        recipientsListPresenter = new RecipientsListPresenter();
        recipientsListPresenter.attachMvpView(this);
        kioskSsoLoginResponsePresenter = new KioskSsoLoginResponsePresenter();
        kioskSsoLoginResponsePresenter.attachMvpView(this);
        if (getIntent() != null && getIntent().getExtras() != null) {
            isfromkiosk = getIntent().getExtras().getBoolean("isfromkiosk", false);
            if (!isfromkiosk) {
                sendLoginRequest();
            } else {
                sendKioskRequest();
            }
        }

    }

    public static void setEventHandler(EventHandler handler) {
        eventHandler = handler;
    }

    private void sendKioskRequest() {
        try {
            NTF_Utils.showProgressDialog(this);
            ntf_Preferences.save(NTF_Constants.Prefs_Keys.IS_KIOSK_MODE, false);
            SsoLoginResponseRequest request = new SsoLoginResponseRequest();
            request.setAuthenticationToken(ntf_Preferences.get(NTF_Constants.Prefs_Keys.AUTHENTICATION_TOKEN));
            request.setSessionId(ntf_Preferences.get(NTF_Constants.Prefs_Keys.SESSION_ID));
            request.setSsoSettingType(getIntent().getStringExtra("sso_setting_type"));
            request.setSsoAccountId(getIntent().getStringExtra("sso_account_id"));
            request.setAccountId(getIntent().getStringExtra("account_id"));
            request.setUserId(getIntent().getStringExtra("user_id"));
            request.setUserEmail(getIntent().getStringExtra("user_email"));
            request.setSAMLResponse(getIntent().getStringExtra("saml_response"));
            request.setSessionTimedout(getIntent().getStringExtra(SsoEmailActivity.STAY_LOGIN_KEY));
//            request.setUserIp(NTF_Utils.getIPAddress(true));
            request.setAppVersion(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
            request.setAppMode("kiosk");
            request.setDeviceUniqueId(NTF_Utils.getUUID(this));
            String header = NTF_Utils.getHeader(ntf_Preferences, this);
            kioskSsoLoginResponsePresenter.doSsoLogin(header, request);
        } catch (Exception e) {
            ntf_Preferences.save(NTF_Constants.Prefs_Keys.IS_KIOSK_MODE, true);
            e.printStackTrace();
        }
    }

    private void sendLoginRequest() {
        try {
            NTF_Utils.showProgressDialog(this);
            SsoLoginResponseRequest request = new SsoLoginResponseRequest();
            request.setSsoSettingType(getIntent().getStringExtra("sso_setting_type"));
            request.setSsoAccountId(getIntent().getStringExtra("sso_account_id"));
            request.setAccountId(getIntent().getStringExtra("account_id"));
            request.setUserId(getIntent().getStringExtra("user_id"));
            request.setUserEmail(getIntent().getStringExtra("user_email"));
            request.setSAMLResponse(getIntent().getStringExtra("saml_response"));
            request.setSessionTimedout(getIntent().getStringExtra(SsoEmailActivity.STAY_LOGIN_KEY));
//            request.setUserIp(NTF_Utils.getIPAddress(true));
            request.setAppVersion(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
            request.setAppMode("normal");
            request.setDeviceUniqueId(NTF_Utils.getUUID(this));
            String header = NTF_Utils.getHeader(ntf_Preferences, this);
            ssoLoginResponsePresenter.doSsoLogin(header, request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getGlobalConstancts() {
        if (NTF_Utils.isOnline(this)) {
            globalConstantsPresenter.getGlobalConstancts(null, NTF_Utils.getGlobalConstantsRequestObject(isfromkiosk ? "kiosk_logout_action" : "login_action", ntf_Preferences));
        } else {
            NTF_Utils.hideProgressDialog();
            NTF_Utils.showNoNetworkAlert(this);
            ntf_Preferences.save(Prefs_Keys.IS_LOGGED_IN, false);
        }
    }

    private void getRecipientsList() {
        if (NTF_Utils.isOnline(this)) {
            recipientsListPresenter.getRecipientList(null, NTF_Utils.getRecipientListRequestObject(isfromkiosk ? "kiosk_logout_action" : "login_action", ntf_Preferences));
        } else {
            ntf_Preferences.save(Prefs_Keys.IS_LOGGED_IN, false);
            NTF_Utils.hideProgressDialog();
            NTF_Utils.showNoNetworkAlert(this);
        }
    }

    @Override
    public void onRecipientListError(String message) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(this, "", message, null);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            finish();
            Intent intent = new Intent();
            intent.putExtra("value", true);
            setResult(RESULT_OK, intent);
            eventHandler.onRefresh();
        }
    };

    @Override
    public void onLoginSuccess(LoginResponse loginResponse) {
        apiStatus = loginResponse.getApiStatus();
        NTF_Utils.doSaveUserDetails(loginResponse, ntf_Preferences);
        ntf_Preferences.save(Prefs_Keys.SESSION_TIMEOUT,getIntent().getStringExtra(SsoEmailActivity.STAY_LOGIN_KEY));
        ntf_Preferences.save(NTF_Constants.Prefs_Keys.IS_LOGGED_IN, true);
        getGlobalConstancts();
    }

    @Override
    public void onLoginFail(LoginResponse loginResponse) {
        apiStatus = loginResponse.getApiStatus();
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlertForFinish(this, "", loginResponse.getApiMessage());

    }
    @Override
    public void onSessionExpired(String message) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showSessionExpireAlert(message, this, ntf_Preferences);
    }

    @Override
    public void onServerError() {
        NTF_Utils.hideProgressDialog();
    }

    @Override
    public Context getMvpContext() {
        return this;
    }

    @Override
    public void onError(Throwable throwable) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlertForFinish(this, "", NTF_Utils.getErrorMessage(throwable));
    }

    @Override
    public void onNoInternetConnection() {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showNoNetworkAlert(this);
    }

    @Override
    public void onErrorCode(String message) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlertForFinish(this, ALERT_ERROR_TITLE_OOPS, message);
        ntf_Preferences.save(NTF_Constants.Prefs_Keys.IS_KIOSK_MODE, true);
    }

    @Override
    public void getGlobalConstanctsFail(String message) {
        getRecipientsList();
        NTF_Utils.showAlert(this, "", message, null);
    }

    @Override
    public void getGlobalConstanctsSuccess(JSONObject response) {
        try {
            getRecipientsList();
            NTF_Utils.saveGlobalConstants(this, response, ntf_Preferences);
            ArrayList<SpinnerData> mMailRoomList = SpinnerData.getList(response.getJSONArray("mailrooms"), null).getList();
            boolean isMailroomSet = false;
            if (mMailRoomList.size() > 1) {
                for (int i = 0; i < mMailRoomList.size(); i++) {
                    if (mMailRoomList.get(i).getValue().equalsIgnoreCase(ntf_Preferences.get(NTF_Constants.Prefs_Keys.DEFAULT_MAILROOM_ID))) {
                        isMailroomSet = true;
                        ntf_Preferences.save(NTF_Constants.Prefs_Keys.DEFAULT_MAILROOM_ID, mMailRoomList.get(i).getValue());
                    }
                }
            }
            if (!isMailroomSet) {
                ntf_Preferences.save(NTF_Constants.Prefs_Keys.DEFAULT_MAILROOM_ID, "all");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRecipientsListSuccess(JSONObject jsonObject) {
        NTF_Utils.hideProgressDialog();
        if (jsonObject != null) {
            NTF_Utils.saveRecipientsData(this, jsonObject);
        }
        Intent intent = new Intent(this, MainActivity.class);
        if (apiStatus.equalsIgnoreCase(ResponseKeys.RESET_PASSWORD) || apiStatus.equalsIgnoreCase(ResponseKeys.RESET_USERNAME_PASSWORD)) {
            intent.putExtra(NTF_Constants.Extras_Keys.LOGIN_API_STATUS, apiStatus);
        }
        NTF_Utils.startAlarmIfRequired(this);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }

    @Override
    public void onKioskLoginSuccess(LoginResponse loginResponse) {
        NTF_Utils.doSaveKioskLogoutSettings(this, loginResponse);
        getGlobalConstancts();
        ntf_Preferences.save(Prefs_Keys.SESSION_TIMEOUT,getIntent().getStringExtra(SsoEmailActivity.STAY_LOGIN_KEY));
        ntf_Preferences.save(NTF_Constants.Prefs_Keys.IS_LOGGED_IN, true);
    }

    @Override
    public void onKioskOtherResults(LoginResponse loginResponse) {
        NTF_Utils.hideProgressDialog();
        ntf_Preferences.save(NTF_Constants.Prefs_Keys.IS_KIOSK_MODE, true);
        NTF_Utils.showAlert(this, "", loginResponse.getApiMessage(), runnable);
    }

    @Override
    public void onAccountClosedOrSuspended(LoginResponse loginResponse) {
        ntf_Preferences.save(NTF_Constants.Prefs_Keys.IS_KIOSK_MODE, true);
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlertForFinish(this, ALERT_ERROR_TITLE_OOPS, loginResponse.getApiMessage());
    }
}
