package com.notifii.notifiiapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.adapters.MultipleAccountsAdapter;
import com.notifii.notifiiapp.adapters.SpinnerHintAdapter;
import com.notifii.notifiiapp.adapters.SpinnerMarkAdapter;
import com.notifii.notifiiapp.adapters.UsernameAdapter;
import com.notifii.notifiiapp.customui.CustomAutoCompleteTextView;
import com.notifii.notifiiapp.models.ShelfData;
import com.notifii.notifiiapp.models.SpinnerData;
import com.notifii.notifiiapp.base.NTF_BaseActivity;
import com.notifii.notifiiapp.fragments.KioskExitDialogFragment;
import com.notifii.notifiiapp.mvp.models.AllLinkedAccount;
import com.notifii.notifiiapp.mvp.models.SsoEmailRequest;
import com.notifii.notifiiapp.mvp.models.SsoEmailResponse;
import com.notifii.notifiiapp.mvp.presenters.KioskSsoEmailPresenter;
import com.notifii.notifiiapp.mvp.presenters.SsoEmailPresenter;
import com.notifii.notifiiapp.mvp.views.SsoEmailView;
import com.notifii.notifiiapp.receivers.ConnectionChangeReceiver;
import com.notifii.notifiiapp.refresh.EventHandler;
import com.notifii.notifiiapp.sorting.SortUserName;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;

public class SsoEmailActivity extends NTF_BaseActivity implements EventHandler, SsoEmailView {

    @BindView(R.id.editText_email)
    CustomAutoCompleteTextView emailET;
    @BindView(R.id.listview)
    ListView listView;
    @BindView(R.id.logouttimeSpinner)
    Spinner logouttimeSpinner;
    @BindView(R.id.login_btn)
    Button login_btn;
    @BindView(R.id.multipleAccountsLL)
    LinearLayout multipleAccountsLL;
    @BindView(R.id.back)
    TextView backTV;
    @BindView(R.id.spinnerText)
    TextView spinnerText;


    MultipleAccountsAdapter adapter;
    ArrayList<AllLinkedAccount> accounts;
    String selected_account = "";
    ArrayList<SpinnerData> stayLoggedInList;
    private String selectedLogoutValue = "12 Hours";
    public static final String STAY_LOGIN_KEY = "stay_login_key";
    private boolean isFromKiosk = false;
    SsoEmailPresenter ssoEmailPresenter;
    KioskSsoEmailPresenter kioskSsoEmailPresenter;
    private final BroadcastReceiver mybroadcast = new ConnectionChangeReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sso_email);
        ButterKnife.bind(this);
        accounts = new ArrayList<>();
        setSpinnerData();
        setDataToEmailField();
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey(KioskExitDialogFragment.IS_FROM_KIOSK)
                && getIntent().getExtras().getBoolean(KioskExitDialogFragment.IS_FROM_KIOSK, false)) {
            isFromKiosk = true;
            backTV.setText("[Provide username/password credentials instead]");
            login_btn.setText("Exit Kiosk Mode");
        }
        ssoEmailPresenter = new SsoEmailPresenter();
        ssoEmailPresenter.attachMvpView(this);
        kioskSsoEmailPresenter = new KioskSsoEmailPresenter();
        kioskSsoEmailPresenter.attachMvpView(this);
        NTF_Utils.setTypefaceForACTV(emailET,this);
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
            stayLoggedInList.get(position - 1).setSelected(true);
            spinnerText.setText(((SpinnerData) logouttimeSpinner.getSelectedItem()).getName());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 555) {
            accounts = new ArrayList<>();
            selected_account = "";
            multipleAccountsLL.setVisibility(View.GONE);
        }
    }


    @OnEditorAction(R.id.editText_email)
    boolean onEditorAction(TextView v, int actionId, KeyEvent key) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
            login_btn.performClick();
            return true;
        }
        return false;
    }

    @OnTextChanged(R.id.editText_email)
    protected void onTextChanged(CharSequence text) {
        if (text.length() == 0 && accounts.size() > 0) {
            accounts = new ArrayList<>();
            multipleAccountsLL.setVisibility(View.GONE);
        }
    }

    private void setDataToEmailField() {
        emailET.setDropDownBackgroundResource(R.drawable.spinner_list);
        String loginNames = NTF_Utils.getLoggedUserNames(this, Prefs_Keys.LOGIN_SSO_EMAIL);
        if (loginNames != null && !loginNames.equals("")) {
            String[] loginNameArr = loginNames.split(",");
            if (loginNameArr.length > 0) {
                try {
                    UsernameAdapter adapter = new UsernameAdapter(this, R.layout.spinner_item, ShelfData.getUserNames(loginNameArr));
                    emailET.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @OnClick(R.id.back)
    void goBack() {
        NTF_Utils.hideKeyboard(this);
        finish();
    }

    @OnClick(R.id.login_btn)
    void doLogin() {
        if (NTF_Utils.isOnline(SsoEmailActivity.this)) {
            ssoEmailPresenter.doCheckCredentials(emailET.getText().toString());
        } else {
            NTF_Utils.showNoNetworkAlert(SsoEmailActivity.this);
        }
    }

    @Override
    public void onRefresh() {
        accounts = new ArrayList<>();
        selected_account = "";
        multipleAccountsLL.setVisibility(View.GONE);
    }

    @Override
    public void onEmptyEmail() {
        NTF_Utils.showAlert(this, ALERT_ERROR_TITLE_OOPS, getString(R.string.email_error_field_required), null);
    }

    @Override
    public void onInvalidEmail() {
        NTF_Utils.showAlert(this, ALERT_ERROR_TITLE_OOPS, getString(R.string.error_invalid_email), null);
    }

    @Override
    public void onCredentialsValidated() {
        NTF_Utils.showProgressDialog(this);
        selected_account = "";
        for (AllLinkedAccount account : accounts) {
            if (account.isSelected()) {
                selected_account = account.getAccountId();
            }
        }
        if (accounts.size() > 0 && selected_account.equals("")) {
            NTF_Utils.showAlert(this, ALERT_ERROR_TITLE_OOPS, "Please select at least one account.", null);
        } else {
            try {
                SsoEmailRequest request = new SsoEmailRequest();
                request.setEmail(emailET.getText().toString());
                request.setSessionTimedout(selectedLogoutValue);
                if (!isFromKiosk) {
                    if (accounts.size() > 0) {
                        request.setSelectedAccount(selected_account);
                    }
                    ssoEmailPresenter.doSsoEmailLogin(null, request);
                } else {
                    request.setEmail(emailET.getText().toString());
                    if (accounts.size() > 0) {
                        request.setSelectedAccount(selected_account);
                    }
                    request.setAccountId(ntf_Preferences.get(NTF_Constants.Prefs_Keys.ACCOUNT_ID));
                    request.setUserId(ntf_Preferences.get(Prefs_Keys.USER_ID));
                    request.setAuthenticationToken(ntf_Preferences.get(Prefs_Keys.AUTHENTICATION_TOKEN));
                    request.setSessionId(ntf_Preferences.get(Prefs_Keys.SESSION_ID));
                    kioskSsoEmailPresenter.doSsoEmailLogin(null, request);
                }
            } catch (Exception e) {
                NTF_Utils.hideKeyboard(this);
                e.printStackTrace();
            }
        }
    }

    @OnClick(R.id.logged_in_frame)
    void onSpinnerClick() {
        logouttimeSpinner.performClick();
    }

    @Override
    public void onSsoEmailSuccess(SsoEmailResponse ssoEmailResponse) {
        NTF_Utils.hideProgressDialog();
        Intent intent = new Intent(this, SsoLoginActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("sso_setting_type", ssoEmailResponse.getSsoSettingType());
        bundle.putString("sso_account_id", ssoEmailResponse.getSsoAccountId());
        bundle.putString("account_id", ssoEmailResponse.getAccountId());
        bundle.putString("user_id", ssoEmailResponse.getUserId());
        bundle.putString("user_email", ssoEmailResponse.getUserEmail());
        bundle.putString("sso_url", ssoEmailResponse.getSsoUrl());
        bundle.putString(STAY_LOGIN_KEY, selectedLogoutValue);
        bundle.putBoolean("isfromkiosk", isFromKiosk);
        intent.putExtras(bundle);
        SsoLoginActivity.setEventHandler(this);
        SsoProgressActivity.setEventHandler(this);
        NTF_Utils.saveLoggedUserNames(Prefs_Keys.LOGIN_SSO_EMAIL, emailET.getText().toString().trim(), this);
        startActivity(intent);
    }

    @Override
    public void onMultipleAccountsFound(SsoEmailResponse ssoEmailResponse) {
        NTF_Utils.hideProgressDialog();
        accounts.addAll(ssoEmailResponse.getAllLinkedAccounts());
        accounts.get(0).setSelected(true);
        adapter = new MultipleAccountsAdapter(accounts, this);
        listView.setAdapter(adapter);
        multipleAccountsLL.setVisibility(View.VISIBLE);
    }

    @Override
    public void onError(String message) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(this, ALERT_ERROR_TITLE_OOPS, message, runnable);
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
    public void onWarning(String message) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(this, ALERT_WARNING_TITLE, message, null);
    }

    @Override
    public void onErrorCode(String s) {
        NTF_Utils.hideProgressDialog();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (accounts.size() > 0) {
                accounts = new ArrayList<>();
                multipleAccountsLL.setVisibility(View.GONE);
            }
        }
    };

}
