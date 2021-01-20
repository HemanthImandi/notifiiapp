package com.notifii.notifiiapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.base.NTF_BaseActivity;
import com.notifii.notifiiapp.helpers.SingleTon;
import com.notifii.notifiiapp.mvp.models.LoginResponse;
import com.notifii.notifiiapp.mvp.models.MFALoginRequest;
import com.notifii.notifiiapp.mvp.models.ResendMFACodeRequest;
import com.notifii.notifiiapp.mvp.presenters.GlobalConstantsPresenter;
import com.notifii.notifiiapp.mvp.presenters.MFALoginPresenter;
import com.notifii.notifiiapp.mvp.presenters.RecipientsListPresenter;
import com.notifii.notifiiapp.mvp.presenters.ResendCodePresenter;
import com.notifii.notifiiapp.mvp.views.GlobalConstanctsView;
import com.notifii.notifiiapp.mvp.views.MFALoginView;
import com.notifii.notifiiapp.mvp.views.RecipientsListView;
import com.notifii.notifiiapp.mvp.views.ResendCodeView;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MFAActivity extends NTF_BaseActivity implements MFALoginView , GlobalConstanctsView, RecipientsListView, ResendCodeView {

    @BindView(R.id.editText_code)
    EditText editTextCode;
    @BindView(R.id.textView_resend)
    TextView textViewResend;

    MFALoginPresenter loginPresenter;
    GlobalConstantsPresenter globalConstantsPresenter;
    RecipientsListPresenter recipientsListPresenter;
    ResendCodePresenter resendCodePresenter;
    String userID,sessionTimeOut,accountID,userName;
    private String loginApiStatus = "";
    public static String user_id="user_id", session_timeout="session_timeout", acc_id="account_id",user_name="user_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_f_a);
        ButterKnife.bind(this);
        loginPresenter = new MFALoginPresenter();
        loginPresenter.attachMvpView(this);
        globalConstantsPresenter = new GlobalConstantsPresenter();
        globalConstantsPresenter.attachMvpView(this);
        recipientsListPresenter = new RecipientsListPresenter();
        recipientsListPresenter.attachMvpView(this);
        resendCodePresenter = new ResendCodePresenter();
        resendCodePresenter.attachMvpView(this);
        if (getIntent().getExtras()!=null){
            userID = getIntent().getStringExtra(user_id);
            sessionTimeOut = getIntent().getStringExtra(session_timeout);
            accountID = getIntent().getStringExtra(acc_id);
            userName = getIntent().getStringExtra(user_name);
        }
    }

    @OnClick(R.id.do_mfa_login)
    void doMFALogin(){
        if (!NTF_Utils.isOnline(this)) {
            NTF_Utils.showNoNetworkAlert(this);
            return;
        } else if (editTextCode.getText().toString().trim().isEmpty()){
            NTF_Utils.showAlert(MFAActivity.this, ALERT_ERROR_TITLE_OOPS, "Please enter verification code.", null);
            return;
        }
        NTF_Utils.showProgressDialog(this);
        MFALoginRequest request=new MFALoginRequest();
        request.setAccountId(accountID);
        request.setUserId(userID);
        request.setSessionTimedout(sessionTimeOut);
        request.setDeviceUdid(NTF_Utils.getUUID(this));
        request.setMfaCode(editTextCode.getText().toString().trim());
        String header = NTF_Utils.getHeader(ntf_Preferences, this);
        loginPresenter.doMFAlogin(header,request);
    }


    @OnClick({R.id.backLL})
    void goBack(){
        finish();
    }

    @OnClick(R.id.textView_resend)
    void resendCode(){
        if (!NTF_Utils.isOnline(this)) {
            NTF_Utils.showNoNetworkAlert(this);
            return;
        }
        NTF_Utils.showProgressDialog(this);
        ResendMFACodeRequest request=new ResendMFACodeRequest();
        request.setAccountId(accountID);
        request.setUserId(userID);
        resendCodePresenter.resendVerificationCode(null,request);
    }

    @Override
    public void onLoginSuccess(LoginResponse response) {
        NTF_Utils.doSaveUserDetails(response, ntf_Preferences);
        getGlobalConstancts();
        loginApiStatus = response.getApiStatus();
        if (response.getApiStatus().equalsIgnoreCase(NTF_Constants.ResponseKeys.SUCCESS)) {
            saveLoginId();
        }
    }

    @Override
    public void onLoginFail(String mesasge) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(MFAActivity.this, "", mesasge, null);
    }

    private void saveLoginId() {
        NTF_Utils.saveLoggedUserNames(Prefs_Keys.LOGIN_API_STATUS, userName, MFAActivity.this);
    }

    private void getGlobalConstancts() {
        if (NTF_Utils.isOnline(MFAActivity.this)) {
            globalConstantsPresenter.getGlobalConstancts(null, NTF_Utils.getGlobalConstantsRequestObject("login_action", ntf_Preferences));
        } else {
            NTF_Utils.hideProgressDialog();
            NTF_Utils.showNoNetworkAlert(MFAActivity.this);
            ntf_Preferences.save(Prefs_Keys.IS_LOGGED_IN, false);
        }
    }

    @Override
    public void onSessionExpired(String message) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(MFAActivity.this, "", message, null);
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
        NTF_Utils.showAlert(this, "", NTF_Utils.getErrorMessage(throwable), null);
    }

    @Override
    public void onNoInternetConnection() {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showNoNetworkAlert(this);
    }

    @Override
    public void onErrorCode(String message) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(MFAActivity.this, "", message, null);
    }

    @Override
    public void getGlobalConstanctsFail(String message) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(this, "", message, null);
    }

    @Override
    public void getGlobalConstanctsSuccess(JSONObject response) {
        getRecipientsList();
        NTF_Utils.saveGlobalConstants(this, response, ntf_Preferences);
    }

    private void getRecipientsList() {
        if (NTF_Utils.isOnline(MFAActivity.this)) {
            recipientsListPresenter.getRecipientList(null, NTF_Utils.getRecipientListRequestObject("login_action", ntf_Preferences));
        } else {
            ntf_Preferences.save(Prefs_Keys.IS_LOGGED_IN, false);
            NTF_Utils.hideProgressDialog();
            NTF_Utils.showNoNetworkAlert(MFAActivity.this);
        }
    }

    @Override
    public void onRecipientsListSuccess(JSONObject jsonObject) {
        NTF_Utils.hideProgressDialog();
        if (jsonObject != null) {
            NTF_Utils.saveRecipientsData(MFAActivity.this, jsonObject);
        }
        SingleTon.getInstance().setPendingPackagesAPIMode("login_action");
        ntf_Preferences.save(Prefs_Keys.SESSION_TIMEOUT,sessionTimeOut);
        Intent intent = new Intent(MFAActivity.this, MainActivity.class);
        if (loginApiStatus.equalsIgnoreCase(ResponseKeys.RESET_PASSWORD) || loginApiStatus.equalsIgnoreCase(ResponseKeys.RESET_USERNAME_PASSWORD)) {
            intent.putExtra(NTF_Constants.Extras_Keys.LOGIN_API_STATUS, loginApiStatus);
        }
        ntf_Preferences.save(Prefs_Keys.IS_LOGGED_IN, true);
        NTF_Utils.startAlarmIfRequired(MFAActivity.this);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRecipientListError(String message) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(MFAActivity.this, "", message, null);
    }

    @Override
    public void onResendSuccess(String message) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(MFAActivity.this, ALERT_SUCCESS_TITLE, message, null);
    }

    @Override
    public void onResndFail(String message) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(MFAActivity.this, "", message, null);
    }

    @Override
    public void onDisableResend(String message) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(MFAActivity.this, ALERT_SUCCESS_TITLE, message,null);
        textViewResend.setClickable(false);
        textViewResend.setVisibility(View.GONE);
    }
}
