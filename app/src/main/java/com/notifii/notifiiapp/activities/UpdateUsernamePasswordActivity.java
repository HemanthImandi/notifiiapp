package com.notifii.notifiiapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.base.NTF_BaseActivity;
import com.notifii.notifiiapp.mvp.models.UpdateUsernamePwdRequest;
import com.notifii.notifiiapp.mvp.models.UpdateUsernamePwdResponse;
import com.notifii.notifiiapp.mvp.models.UsernameCheckAvailabilityRequest;
import com.notifii.notifiiapp.mvp.models.UsernameCheckAvailabilityResponse;
import com.notifii.notifiiapp.mvp.presenters.UpdateUsernamePwdPresenter;
import com.notifii.notifiiapp.mvp.views.UpdateUsernamePwdView;
import com.notifii.notifiiapp.receivers.ConnectionChangeReceiver;
import com.notifii.notifiiapp.utils.NTF_Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UpdateUsernamePasswordActivity extends NTF_BaseActivity implements UpdateUsernamePwdView {

    @BindView(R.id.update_username_pwdTitle)
    TextView updateUsernamePwdTitle;
    @BindView(R.id.fullnameTv)
    TextView fullName;
    @BindView(R.id.userNameLayout)
    LinearLayout usernameLayout;
    @BindView(R.id.usernameET)
    EditText username;
    @BindView(R.id.newpasswordET)
    EditText newPasswordEt;
    @BindView(R.id.confirmpaswordEt)
    EditText confirmPasswordEt;
    @BindView(R.id.updateMyaccountBtn)
    Button updateMyAccountButton;
    @BindView(R.id.textview_check_availability)
    TextView checkAvailability;
    @BindView(R.id.textviewCancel)
    TextView cancelTv;
    @BindView(R.id.checkavailability4)
    TextView checkAvailabilty4;
    @BindView(R.id.checkavailability3)
    TextView checkAvailabilty3;
    @BindView(R.id.checkavailability2)
    TextView checkAvailabilty2;
    @BindView(R.id.checkavailability1)
    TextView checkAvailabilty1;
    UpdateUsernamePwdPresenter updateUsernamePwdPresenter;

    private String api_status = ResponseKeys.RESET_PASSWORD;
    private final BroadcastReceiver mybroadcast = new ConnectionChangeReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_update_username_password);
        int width = getResources().getDisplayMetrics().widthPixels;
        int dialogWidth = (int) (width * 1f);
        if (getResources().getBoolean(R.bool.isDeviceTablet)) {
            dialogWidth = (int) (width * 0.85f);
        }
        int dialogHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        getWindow().setLayout(dialogWidth, dialogHeight);
        ButterKnife.bind(this);
        updateUsernamePwdPresenter = new UpdateUsernamePwdPresenter();
        updateUsernamePwdPresenter.attachMvpView(this);
        bundleData();
    }


    public void bundleData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            api_status = bundle.getString(Extras_Keys.LOGIN_API_STATUS, ResponseKeys.RESET_PASSWORD);
            if (api_status.equalsIgnoreCase(ResponseKeys.RESET_PASSWORD)) {
                usernameLayout.setVisibility(View.GONE);
                checkAvailabilty1.setVisibility(View.GONE);
                checkAvailabilty2.setVisibility(View.GONE);
                checkAvailabilty3.setVisibility(View.GONE);
                checkAvailabilty4.setVisibility(View.GONE);

                updateUsernamePwdTitle.setText(getString(R.string.change_password));
            } else if (api_status.equalsIgnoreCase(ResponseKeys.RESET_USERNAME_PASSWORD)) {
                usernameLayout.setVisibility(View.VISIBLE);
                updateUsernamePwdTitle.setText(getString(R.string.update_username_pwd_title));
            }
        }
        String fllName = ntf_Preferences.getString(Prefs_Keys.FULL_NAME, UpdateUsernamePasswordActivity.this);
        fullName.setText(fllName);
    }


    @OnClick(R.id.updateMyaccountBtn)
    void onClickButton(View view) {

        updateUserNamePassword();
    }

    @OnClick(R.id.textview_check_availability)
    void onClickavailability(View view) {
        updateUsernamePwdPresenter.doCheckUsernameAvailabilty(username.getText().toString());
    }

    @OnClick(R.id.textviewCancel)
    void onClickCancel(View v) {
        this.finish();
    }


    String userName = null;
    String newPassWord = null;
    String confirmPassword = null;

    // Validating the Input Fields
    public void updateUserNamePassword() {
        userName = username.getText().toString();
        newPassWord = newPasswordEt.getText().toString();
        confirmPassword = confirmPasswordEt.getText().toString();

        if (api_status.equalsIgnoreCase(ResponseKeys.RESET_PASSWORD)) {
            updateUsernamePwdPresenter.checkPasswordValidated(newPassWord, confirmPassword);
        } else {
            updateUsernamePwdPresenter.checkUsernamePasswordValidated(userName, newPassWord, confirmPassword);
        }
    }

    @Override
    public void fieldsNotValid(String message) {
        NTF_Utils.showAlert(UpdateUsernamePasswordActivity.this, "", message, null);
    }

    @Override
    public void onFieldsValidated(String username) {
        if (NTF_Utils.isOnline(UpdateUsernamePasswordActivity.this)) {
            UpdateUsernamePwdRequest updateUsernamePwdRequest = new UpdateUsernamePwdRequest();
            updateUsernamePwdRequest.setNewUsername(username);
            updateUsernamePwdRequest.setNewPassword(newPassWord);
            updateUsernamePwdRequest.setAuthenticationToken(ntf_Preferences.get(Prefs_Keys.AUTHENTICATION_TOKEN));
            updateUsernamePwdRequest.setSessionId(ntf_Preferences.get(Prefs_Keys.SESSION_ID));
            updateUsernamePwdRequest.setUserId(ntf_Preferences.get(Prefs_Keys.USER_ID));
            updateUsernamePwdRequest.setAccountId(ntf_Preferences.get(Prefs_Keys.ACCOUNT_ID));
            NTF_Utils.showProgressDialog(this);
            updateUsernamePwdPresenter.updatingUsernamePwd(null, updateUsernamePwdRequest);
        } else {
            NTF_Utils.showNoNetworkAlert(UpdateUsernamePasswordActivity.this);
            registerReceiver(mybroadcast, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    @Override
    public void onUserNameValidated() {
        if (NTF_Utils.isOnline(UpdateUsernamePasswordActivity.this)) {
            UsernameCheckAvailabilityRequest usernameCheckAvailabilityRequest = new UsernameCheckAvailabilityRequest();
            usernameCheckAvailabilityRequest.setSessionId( ntf_Preferences.get(Prefs_Keys.SESSION_ID));
            usernameCheckAvailabilityRequest.setAuthenticationToken( ntf_Preferences.get(Prefs_Keys.AUTHENTICATION_TOKEN));
            usernameCheckAvailabilityRequest.setUserId( ntf_Preferences.get(Prefs_Keys.USER_ID));
            usernameCheckAvailabilityRequest.setUsername(username.getText().toString());
            usernameCheckAvailabilityRequest.setAccountId(ntf_Preferences.get(Prefs_Keys.ACCOUNT_ID));
            NTF_Utils.showProgressDialog(this);
            updateUsernamePwdPresenter.checkingUsernameAvailability(null, usernameCheckAvailabilityRequest);
        } else {
            NTF_Utils.showNoNetworkAlert(UpdateUsernamePasswordActivity.this);
            registerReceiver(mybroadcast, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    @Override
    public void onUpdatingSuccess(UpdateUsernamePwdResponse response) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlertForFinish(UpdateUsernamePasswordActivity.this, ALERT_SUCCESS_TITLE, response.getApiMessage());
    }

    @Override
    public void onUpdatingFailure(String message) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(UpdateUsernamePasswordActivity.this, "", message, null);
    }

    @Override
    public void onCheckingSuccess(UsernameCheckAvailabilityResponse response) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(UpdateUsernamePasswordActivity.this, ALERT_SUCCESS_TITLE, username.getText().toString() + " is available.", null);

    }

    @Override
    public void userNameTaken() {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(UpdateUsernamePasswordActivity.this, "", username.getText().toString() + " is not available.", null);
    }

    @Override
    public void onCheckingFailure(String mesage) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(UpdateUsernamePasswordActivity.this, "", mesage, null);
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
        return null;
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
    }
}

