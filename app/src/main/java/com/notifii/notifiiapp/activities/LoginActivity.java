package com.notifii.notifiiapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.adapters.SpinnerHintAdapter;
import com.notifii.notifiiapp.adapters.SpinnerMarkAdapter;
import com.notifii.notifiiapp.adapters.UsernameAdapter;
import com.notifii.notifiiapp.customui.CustomAutoCompleteTextView;
import com.notifii.notifiiapp.helpers.SingleTon;
import com.notifii.notifiiapp.models.ShelfData;
import com.notifii.notifiiapp.models.SpinnerData;
import com.notifii.notifiiapp.base.NTF_BaseActivity;
import com.notifii.notifiiapp.mvp.models.LoginRequest;
import com.notifii.notifiiapp.mvp.models.LoginResponse;
import com.notifii.notifiiapp.mvp.presenters.GlobalConstantsPresenter;
import com.notifii.notifiiapp.mvp.presenters.LoginPresenter;
import com.notifii.notifiiapp.mvp.presenters.RecipientsListPresenter;
import com.notifii.notifiiapp.mvp.views.GlobalConstanctsView;
import com.notifii.notifiiapp.mvp.views.LoginView;
import com.notifii.notifiiapp.mvp.views.RecipientsListView;
import com.notifii.notifiiapp.receivers.ConnectionChangeReceiver;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnItemSelected;


public class LoginActivity extends NTF_BaseActivity implements LoginView, GlobalConstanctsView, RecipientsListView {

    @BindView(R.id.userNameACTV)
    CustomAutoCompleteTextView mEditTextUserName;
    @BindView(R.id.passwordET)
    EditText mEditTextPassword;
    @BindView(R.id.logouttimeSpinner)
    Spinner logouttimeSpinner;
    @BindView(R.id.textViewVersionNumber)
    TextView mTextViewVersionNumber;
    @BindView(R.id.loginButton)
    Button mButtonLogin;
    @BindView(R.id.logo_image)
    ImageView logoImage;
    @BindView(R.id.spinnerText)
    TextView spinnerText;

    private final BroadcastReceiver mybroadcast = new ConnectionChangeReceiver();
    private String selectedLogoutValue = "12 Hours";
    ArrayList<SpinnerData> stayLoggedInList;
    LoginPresenter loginPresenter;
    GlobalConstantsPresenter globalConstantsPresenter;
    RecipientsListPresenter recipientsListPresenter;
    private String loginApiStatus = "";
    private boolean isPageChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        NTF_Utils.cancelAlarm(this);
        NTF_Utils.setTypefaceForACTV(mEditTextUserName,this);
        findViews();
        setLoggedData();
        clearInternalData();
        setSpinnerData();
        loginPresenter = new LoginPresenter();
        loginPresenter.attachMvpView(this);
        globalConstantsPresenter = new GlobalConstantsPresenter();
        globalConstantsPresenter.attachMvpView(this);
        recipientsListPresenter = new RecipientsListPresenter();
        recipientsListPresenter.attachMvpView(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPageChanged) {
            isPageChanged = false;
            mEditTextUserName.setText("");
            mEditTextPassword.setText("");
            selectedLogoutValue = "12 Hours";
            SpinnerData.resetData(stayLoggedInList);
            stayLoggedInList.get(0).setSelected(true);
            logouttimeSpinner.setSelection(1);
            spinnerText.setText(stayLoggedInList.get(0).getName());
        }
        NTF_Utils.hideKeyboard(this);
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

    private void clearInternalData() {
        try {
            ntf_Preferences.clearAllPrefs();
            ntf_Preferences.save(Prefs_Keys.IS_LOGGED_IN, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnEditorAction(R.id.passwordET)
    boolean onEditorAction(TextView v, int actionId, KeyEvent key) {
        if (actionId == EditorInfo.IME_ACTION_GO) {
            mButtonLogin.performClick();
            return true;
        }
        return false;
    }

    private void findViews() {
        Log.v("Notifii Track :", "In Find views");
        Picasso.with(this).load(R.drawable.ic_notifii_track_logo).into(logoImage);
        setVersion();
        try {
            mEditTextUserName.setDropDownBackgroundResource(R.drawable.spinner_list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSpinnerData() {
        stayLoggedInList = SpinnerData.getStayLoggedInSpinnerData();
        SpinnerMarkAdapter adapter = new SpinnerMarkAdapter(this, stayLoggedInList);
        logouttimeSpinner.setAdapter(new SpinnerHintAdapter(adapter, R.layout.spinner_hint, this));
        logouttimeSpinner.setSelection(1);
    }

    @OnItemSelected(R.id.logouttimeSpinner)
    public void spinnerItemSelected(Spinner spinner, int position) {
        if (spinner.getSelectedItem() != null) {
            selectedLogoutValue = ((SpinnerData) logouttimeSpinner.getSelectedItem()).getValue();
            SpinnerData.resetData(stayLoggedInList);
            ((SpinnerData) logouttimeSpinner.getSelectedItem()).setSelected(true);
            spinnerText.setText(((SpinnerData) logouttimeSpinner.getSelectedItem()).getName());
        }
    }

    @OnClick(R.id.loginButton)
    void doLogin() {
        if (!NTF_Utils.isOnline(this)) {
            NTF_Utils.showNoNetworkAlert(this);
            return;
        }
        loginApiStatus = "";
        String userName = mEditTextUserName.getText().toString();
        String password = mEditTextPassword.getText().toString().trim();
        loginPresenter.doCheckCredentials(userName, password);
    }

    @OnClick(R.id.forgotMyPasswordTV)
    void onForgotPassword() {
        if (!NTF_Utils.isOnline(this)) {
            NTF_Utils.showNoNetworkAlert(this);
            return;
        }
        isPageChanged = true;
        startActivity(new Intent(this, ForgotPasswordActivity.class));
    }

    @OnClick(R.id.logged_in_frame)
    void onSpinnerClick() {
        logouttimeSpinner.performClick();
    }

    @OnClick(R.id.ssoLoginBTN)
    void doSsoLogin() {
        if (!NTF_Utils.isOnline(this)) {
            NTF_Utils.showNoNetworkAlert(this);
            return;
        }
        isPageChanged = true;
        startActivity(new Intent(this, SsoEmailActivity.class));
    }

    private void saveLoginId() {
        NTF_Utils.saveLoggedUserNames(Prefs_Keys.LOGIN_API_STATUS, mEditTextUserName.getText().toString().trim(), LoginActivity.this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void setVersion() {
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            mTextViewVersionNumber.setText("Version " + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    private void setLoggedData() {
        String loginNames = NTF_Utils.getLoggedUserNames(LoginActivity.this, Prefs_Keys.LOGIN_API_STATUS);
        if (!loginNames.equals("")) {
            String[] loginNameArr = loginNames.split(",");
            if (loginNameArr.length > 0) {
                try {
                    UsernameAdapter adapter = new UsernameAdapter(this, R.layout.spinner_item, ShelfData.getUserNames(loginNameArr));
                    mEditTextUserName.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void onEmptyUserName() {
        NTF_Utils.showAlert(LoginActivity.this, ALERT_ERROR_TITLE_OOPS, "Please enter your username.", null);
    }

    @Override
    public void onEmptyPassword() {
        NTF_Utils.showAlert(LoginActivity.this, ALERT_ERROR_TITLE_OOPS, "Please enter your password.", null);
    }

    @Override
    public void onCredentialsValidated(String username, String password) {
        if (!NTF_Utils.isOnline(this)) {
            NTF_Utils.showNoNetworkAlert(this);
            registerReceiver(mybroadcast, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        } else {
            LoginRequest request = new LoginRequest();
            request.setUsername(username);
            request.setPassword(password);
            request.setAppVersion(NTF_Utils.getVersion(this));
//            request.setUserIp(NTF_Utils.getIPAddress(true));
            request.setSessionTimedout(selectedLogoutValue);
            request.setDeviceUniqueId(NTF_Utils.getUUID(this));

            NTF_Utils.showProgressDialog(this);
            String header = NTF_Utils.getHeader(ntf_Preferences, this);
            loginPresenter.doLogin(header, request);
        }
    }

    private void getGlobalConstancts() {
        if (NTF_Utils.isOnline(LoginActivity.this)) {
            globalConstantsPresenter.getGlobalConstancts(null, NTF_Utils.getGlobalConstantsRequestObject("login_action", ntf_Preferences));
        } else {
            NTF_Utils.hideProgressDialog();
            NTF_Utils.showNoNetworkAlert(LoginActivity.this);
            ntf_Preferences.save(Prefs_Keys.IS_LOGGED_IN, false);
        }
    }

    @Override
    public void onLoginSuccess(LoginResponse loginResponse) {
        NTF_Utils.doSaveUserDetails(loginResponse, ntf_Preferences);
        getGlobalConstancts();
        loginApiStatus = loginResponse.getApiStatus();
        if (loginResponse.getApiStatus().equalsIgnoreCase(ResponseKeys.SUCCESS)) {
            saveLoginId();
        }
    }

    @Override
    public void onAccountClosedOrSuspended(LoginResponse loginResponse) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(LoginActivity.this, "", loginResponse.getApiMessage(), null);
        loginApiStatus = loginResponse.getApiStatus();
    }

    @Override
    public void onMFAEnabled(LoginResponse loginResponse) {
        NTF_Utils.hideProgressDialog();
        Intent intent = new Intent(this,MFAActivity.class);
        intent.putExtra(MFAActivity.user_id,loginResponse.getUserId());
        intent.putExtra(MFAActivity.acc_id,loginResponse.getAccountId());
        intent.putExtra(MFAActivity.session_timeout,loginResponse.getSessionTimedout());
        intent.putExtra(MFAActivity.user_name,mEditTextUserName.getText().toString());
        startActivity(intent);
    }

    @Override
    public void onMultisiteEnabled(LoginResponse loginResponse) {
        NTF_Utils.hideProgressDialog();
        MultiSiteActivity.lResponse = loginResponse;
        isPageChanged = true;
        Intent intent = new Intent(this,MultiSiteActivity.class);
        intent.putExtra(MultiSiteActivity.USERNAME,mEditTextUserName.getText().toString().trim());
        intent.putExtra(MultiSiteActivity.PASSWORD,mEditTextPassword.getText().toString().trim());
        startActivity(intent);
    }

    @Override
    public void onLoginFail(LoginResponse loginResponse) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(LoginActivity.this, "", loginResponse.getApiMessage(), null);
        loginApiStatus = loginResponse.getApiStatus();
    }

    @Override
    public void onSessionExpired(String message) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(LoginActivity.this, "", message, null);
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
        NTF_Utils.showAlert(LoginActivity.this, "", message, null);
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
        if (NTF_Utils.isOnline(LoginActivity.this)) {
            recipientsListPresenter.getRecipientList(null, NTF_Utils.getRecipientListRequestObject("login_action", ntf_Preferences));
        } else {
            ntf_Preferences.save(Prefs_Keys.IS_LOGGED_IN, false);
            NTF_Utils.hideProgressDialog();
            NTF_Utils.showNoNetworkAlert(LoginActivity.this);
        }
    }
    @Override
    public void onRecipientListError(String message) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(LoginActivity.this, "", message, null);
    }

    @Override
    public void onWarning(String message) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(LoginActivity.this, "", message, null);
    }

    @Override
    public void onRecipientsListSuccess(JSONObject jsonObject) {
        NTF_Utils.hideProgressDialog();
        if (jsonObject != null) {
            NTF_Utils.saveRecipientsData(LoginActivity.this, jsonObject);
        }
        SingleTon.getInstance().setPendingPackagesAPIMode("login_action");
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        if (loginApiStatus.equalsIgnoreCase(ResponseKeys.RESET_PASSWORD) || loginApiStatus.equalsIgnoreCase(ResponseKeys.RESET_USERNAME_PASSWORD)) {
            intent.putExtra(NTF_Constants.Extras_Keys.LOGIN_API_STATUS, loginApiStatus);
        }
        ntf_Preferences.save(Prefs_Keys.IS_LOGGED_IN, true);
        ntf_Preferences.save(Prefs_Keys.SESSION_TIMEOUT,selectedLogoutValue);
        NTF_Utils.startAlarmIfRequired(LoginActivity.this);
        startActivity(intent);
        finish();
    }
}