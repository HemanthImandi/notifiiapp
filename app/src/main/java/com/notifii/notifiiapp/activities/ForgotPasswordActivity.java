
/////////////////////////////////////////////////////////////////
// ForgotPasswordActivity.java
//
// Created by Annapoorna
// Notifii Project
//
//Copyright (c) 2016 Notifii, LLC. All rights reserved
/////////////////////////////////////////////////////////////////

package com.notifii.notifiiapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.base.NTF_BaseActivity;
import com.notifii.notifiiapp.mvp.models.ForgotPasswordRequest;
import com.notifii.notifiiapp.mvp.presenters.ForgotPasswordPresenter;
import com.notifii.notifiiapp.mvp.views.ForgotPasswordView;
import com.notifii.notifiiapp.receivers.ConnectionChangeReceiver;
import com.notifii.notifiiapp.utils.NTF_Utils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

/*
 * This class deals with the ForgotPasswordActivity functionality in Mobile Device.
 */
public class ForgotPasswordActivity extends NTF_BaseActivity implements ForgotPasswordView {

    @BindView(R.id.editText_email)
    EditText mEditTextEmail;
    @BindView(R.id.button_reset_my_account)
    Button mButtonResetMyAccount;
    @BindView(R.id.logo_image)
    ImageView logoImage;

    private final BroadcastReceiver mybroadcast = new ConnectionChangeReceiver();
    ForgotPasswordPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acttivity_forgot_pwd);
        ButterKnife.bind(this);
        Picasso.with(this).load(R.drawable.ic_notifii_track_logo).into(logoImage);
        presenter = new ForgotPasswordPresenter();
        presenter.attachMvpView(this);
    }

    @OnEditorAction(R.id.editText_email)
    boolean onEditorAction(TextView v, int actionId, KeyEvent key) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            mButtonResetMyAccount.performClick();
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        NTF_Utils.hideKeyboard(this);
    }

    @OnClick(R.id.textView_never_mind)
    void onNeverMindClicked() {
        NTF_Utils.hideKeyboard(this);
        finish();
    }

    @OnClick(R.id.button_reset_my_account)
    void resetPassword() {
        NTF_Utils.hideKeyboard(ForgotPasswordActivity.this);
        String email = mEditTextEmail.getText().toString().trim();
        if (NTF_Utils.isOnline(ForgotPasswordActivity.this)) {
            presenter.doCheckCredentials(email);
        } else {
            NTF_Utils.showNoNetworkAlert(ForgotPasswordActivity.this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(mybroadcast);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEmptyEmail() {//C8870591
        NTF_Utils.showAlert(ForgotPasswordActivity.this, ALERT_ERROR_TITLE_OOPS, getString(R.string.email_error_field_required), null);
    }

    @Override
    public void onInvalidEmail() {
        NTF_Utils.showAlert(ForgotPasswordActivity.this, ALERT_ERROR_TITLE_OOPS, getString(R.string.error_invalid_email), null);
    }

    @Override
    public void onCredentialsValidated(String email) {
        NTF_Utils.showProgressDialog(ForgotPasswordActivity.this);
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setUsername("");
        request.setEmail(email);
        presenter.forgotpassword(null, request);
    }

    @Override
    public void onSuccess(String message) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlertForFinish(this, ALERT_SUCCESS_TITLE, message);
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
        NTF_Utils.showAlert(this, "", NTF_Utils.getErrorMessage(throwable), null);
    }

    @Override
    public void onNoInternetConnection() {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showNoNetworkAlert(this);
    }

    @Override
    public void onErrorCode(String s) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(ForgotPasswordActivity.this, ALERT_ERROR_TITLE_OOPS, s, null);
    }

}
