package com.notifii.notifiiapp.fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.activities.IdentifyUserActivity;
import com.notifii.notifiiapp.activities.SsoEmailActivity;
import com.notifii.notifiiapp.adapters.SpinnerHintAdapter;
import com.notifii.notifiiapp.adapters.SpinnerMarkAdapter;
import com.notifii.notifiiapp.adapters.UsernameAdapter;
import com.notifii.notifiiapp.customui.CustomAutoCompleteTextView;
import com.notifii.notifiiapp.models.ShelfData;
import com.notifii.notifiiapp.models.SpinnerData;
import com.notifii.notifiiapp.helpers.NTF_PrefsManager;
import com.notifii.notifiiapp.mvp.models.KioskLogoutRequest;
import com.notifii.notifiiapp.mvp.models.LoginResponse;
import com.notifii.notifiiapp.mvp.presenters.GlobalConstantsPresenter;
import com.notifii.notifiiapp.mvp.presenters.KioskLogoutPresenter;
import com.notifii.notifiiapp.mvp.presenters.RecipientsListPresenter;
import com.notifii.notifiiapp.mvp.views.GlobalConstanctsView;
import com.notifii.notifiiapp.mvp.views.LoginView;
import com.notifii.notifiiapp.mvp.views.RecipientsListView;
import com.notifii.notifiiapp.sorting.SortUserName;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static com.notifii.notifiiapp.utils.NTF_Constants.ALERT_ERROR_TITLE_OOPS;
import static com.notifii.notifiiapp.utils.NTF_Constants.ALERT_WARNING_TITLE;

/**
 * A simple {@link Fragment} subclass.
 */
public class KioskExitDialogFragment extends DialogFragment implements LoginView, GlobalConstanctsView, RecipientsListView {

    @BindView(R.id.logouttimeSpinner)
    Spinner logouttimeSpinner;
    @BindView(R.id.usernameET)
    CustomAutoCompleteTextView usernameET;
    @BindView(R.id.passwordET)
    EditText passwordET;
    @BindView(R.id.spinnerText)
    TextView spinnerText;

    private Dialog dialog;
    private String selectedLogoutValue = "12 Hours";
    ArrayList<SpinnerData> stayLoggedInList;
    IdentifyUserActivity identifyUserActivity;
    NTF_PrefsManager ntf_Preferences;
    IdentifyUserActivity activity;
    public static final String IS_FROM_KIOSK = "is_from_kiosk";
    private String loginApiStatus = "";
    KioskLogoutPresenter kioskLogoutPresenter;
    GlobalConstantsPresenter globalConstantsPresenter;
    RecipientsListPresenter recipientsListPresenter;

    public void setActivity(IdentifyUserActivity activity) {
        this.activity = activity;
    }

    public KioskExitDialogFragment() {
    }

    public void setData(IdentifyUserActivity identifyUserActivity, NTF_PrefsManager ntf_Preferences) {
        this.identifyUserActivity = identifyUserActivity;
        this.ntf_Preferences = ntf_Preferences != null ? ntf_Preferences : new NTF_PrefsManager(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kiosk_exit_dialog, container, false);
        ButterKnife.bind(this, view);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dialog = getDialog();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        setSpinnerData();
        setLoggedData();
        NTF_Utils.setTypefaceForACTV(usernameET,getActivity());
        kioskLogoutPresenter = new KioskLogoutPresenter();
        kioskLogoutPresenter.attachMvpView(this);
        globalConstantsPresenter = new GlobalConstantsPresenter();
        globalConstantsPresenter.attachMvpView(this);
        recipientsListPresenter = new RecipientsListPresenter();
        recipientsListPresenter.attachMvpView(this);
    }

    private void setLoggedData() {
        usernameET.setDropDownBackgroundResource(R.drawable.spinner_list);
        String loginNames = NTF_Utils.getLoggedUserNames(getActivity(), NTF_Constants.Prefs_Keys.LOGIN_API_STATUS);
        if (!loginNames.equals("")) {
            String[] loginNameArr = loginNames.split(",");
            if (loginNameArr.length > 0) {
                try {
                    UsernameAdapter adapter = new UsernameAdapter(getActivity(), R.layout.spinner_item, ShelfData.getUserNames(loginNameArr));
                    usernameET.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @OnClick(R.id.dialogcloseTV)
    void dismissDialog() {
        dialog.dismiss();
    }

    @OnClick(R.id.spinnerFL)
    void spinnerClicked() {
        logouttimeSpinner.performClick();
    }

    @OnClick(R.id.loginButton)
    void loginClicked() {
        if (NTF_Utils.isOnline(getActivity())) {
            kioskLogoutPresenter.doCheckCredentials(usernameET.getText().toString(), passwordET.getText().toString());
        } else{
            NTF_Utils.showNoNetworkAlert(getActivity());
        }
    }

    @OnClick(R.id.ssoCredentials)
    void onSsoEmailClicked() {
        clearFields();
        Intent intent = new Intent(getActivity(), SsoEmailActivity.class);
        intent.putExtra(IS_FROM_KIOSK, true);
        startActivity(intent);
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

    private void setSpinnerData() {
        stayLoggedInList = SpinnerData.getStayLoggedInSpinnerData();
        SpinnerMarkAdapter adapter = new SpinnerMarkAdapter(getActivity(), stayLoggedInList);
        logouttimeSpinner.setAdapter(new SpinnerHintAdapter(adapter, R.layout.spinner_hint, getActivity()));
        logouttimeSpinner.setSelection(1);
    }

    @Override
    public void onStart() {
        super.onStart();
        int width = getResources().getDisplayMetrics().widthPixels;
        if (dialog == null || dialog.getWindow() == null) {
            return;
        }
        int dialogWidth = (int) (width * 0.90f);
        if (getResources().getBoolean(R.bool.isDeviceTablet)) {
            dialogWidth = (int) (width * 0.6f);
        }
        int dialogHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setLayout(dialogWidth, dialogHeight);
    }

    private void saveLoginId() {
        NTF_Utils.saveLoggedUserNames(NTF_Constants.Prefs_Keys.LOGIN_API_STATUS, usernameET.getText().toString().trim(), getActivity());
    }

    @Override
    public void onEmptyUserName() {
        NTF_Utils.showAlert(getActivity(), ALERT_ERROR_TITLE_OOPS, "Please enter your username.", null);
    }

    @Override
    public void onEmptyPassword() {
        NTF_Utils.showAlert(getActivity(), ALERT_ERROR_TITLE_OOPS, "Please enter your password.", null);
    }

    @Override
    public void onCredentialsValidated(String username, String password) {
        NTF_Utils.showProgressDialog(getActivity());
        ntf_Preferences.save(NTF_Constants.Prefs_Keys.IS_KIOSK_MODE, false);
        KioskLogoutRequest request = new KioskLogoutRequest();
        request.setSessionId(ntf_Preferences.get(NTF_Constants.Prefs_Keys.SESSION_ID));
        request.setSessionTimedout(selectedLogoutValue);
        request.setAccountId(ntf_Preferences.get(NTF_Constants.Prefs_Keys.ACCOUNT_ID));
        request.setAuthenticationToken(ntf_Preferences.get(NTF_Constants.Prefs_Keys.AUTHENTICATION_TOKEN));
        request.setPassword(passwordET.getText().toString());
        request.setUsername(usernameET.getText().toString());
        request.setDeviceUniqueId(NTF_Utils.getUUID(getActivity()));
        request.setAppVersion(NTF_Utils.getVersion(getActivity()!=null?getActivity():activity));
        request.setUserId(ntf_Preferences.get(NTF_Constants.Prefs_Keys.USER_ID));
        String header = NTF_Utils.getHeader(ntf_Preferences, getActivity());
        kioskLogoutPresenter.doKioskLogout(header, request);
    }

    @Override
    public void onLoginFail(LoginResponse loginResponse) {
        NTF_Utils.hideProgressDialog();
        ntf_Preferences.save(NTF_Constants.Prefs_Keys.IS_KIOSK_MODE, true);
        NTF_Utils.showAlert(activity, "", loginResponse.getApiMessage(), null);
    }

    @Override
    public void onWarning(String message) {
        NTF_Utils.hideProgressDialog();
        ntf_Preferences.save(NTF_Constants.Prefs_Keys.IS_KIOSK_MODE, true);
        NTF_Utils.showAlert(activity, ALERT_WARNING_TITLE, message, null);
    }

    @Override
    public void onLoginSuccess(LoginResponse loginResponse) {
        NTF_Utils.doSaveKioskLogoutSettings(activity, loginResponse);
        getGlobalConstants();
        ntf_Preferences.save(NTF_Constants.Prefs_Keys.IS_LOGGED_IN, true);
        ntf_Preferences.save(NTF_Constants.Prefs_Keys.SESSION_TIMEOUT,selectedLogoutValue);
        loginApiStatus = loginResponse.getApiStatus();
        saveLoginId();
    }

    private void getGlobalConstants() {
        if (NTF_Utils.isOnline(getActivity())) {
            globalConstantsPresenter.getGlobalConstancts(null, NTF_Utils.getGlobalConstantsRequestObject("kiosk_logout_action", ntf_Preferences));
        } else {
            NTF_Utils.hideProgressDialog();
            NTF_Utils.showNoNetworkAlert(getActivity());
            ntf_Preferences.save(NTF_Constants.Prefs_Keys.IS_LOGGED_IN, false);
        }
    }

    @Override
    public void onAccountClosedOrSuspended(LoginResponse loginResponse) {
        NTF_Utils.hideProgressDialog();
        ntf_Preferences.save(NTF_Constants.Prefs_Keys.IS_KIOSK_MODE, true);
        NTF_Utils.showAlert(getActivity(), "", loginResponse.getApiMessage(), null);
    }

    @Override
    public void onMFAEnabled(LoginResponse loginResponse) {
        NTF_Utils.hideProgressDialog();
        ntf_Preferences.save(NTF_Constants.Prefs_Keys.IS_KIOSK_MODE, true);
        NTF_Utils.showAlert(getActivity(), "", loginResponse.getApiMessage(), null);
    }

    @Override
    public void onMultisiteEnabled(LoginResponse response) {

    }

    @Override
    public void onSessionExpired(String message) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showSessionExpireAlert(message, getActivity(), ntf_Preferences);
    }

    @Override
    public void onServerError() {
        ntf_Preferences.save(NTF_Constants.Prefs_Keys.IS_KIOSK_MODE, true);
        NTF_Utils.hideProgressDialog();
    }

    @Override
    public Context getMvpContext() {
        return getActivity();
    }

    @Override
    public void onError(Throwable throwable) {
        ntf_Preferences.save(NTF_Constants.Prefs_Keys.IS_KIOSK_MODE, true);
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(getActivity(), "", NTF_Utils.getErrorMessage(throwable), null);
    }

    @Override
    public void onNoInternetConnection() {
        ntf_Preferences.save(NTF_Constants.Prefs_Keys.IS_KIOSK_MODE, true);
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showNoNetworkAlert(getActivity());
    }

    private void clearFields(){
        usernameET.setText("");
        passwordET.setText("");
    }

    @Override
    public void onErrorCode(String s) {
        NTF_Utils.hideProgressDialog();
    }

    @Override
    public void getGlobalConstanctsFail(String message) {
        getRecipientsList();
        NTF_Utils.showAlert(getActivity(), "", message, null);
    }

    @Override
    public void getGlobalConstanctsSuccess(JSONObject response) {
        getRecipientsList();
        NTF_Utils.saveGlobalConstants(getActivity(), response, ntf_Preferences);
    }

    private void getRecipientsList() {
        if (NTF_Utils.isOnline(getActivity())) {
            recipientsListPresenter.getRecipientList(null, NTF_Utils.getRecipientListRequestObject("kiosk_logout_action", ntf_Preferences));
        } else {
            ntf_Preferences.save(NTF_Constants.Prefs_Keys.IS_LOGGED_IN, false);
            NTF_Utils.hideProgressDialog();
            NTF_Utils.showNoNetworkAlert(getActivity());
        }
    }
    @Override
    public void onRecipientListError(String message) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(getActivity(), "", message, null);
    }
    @Override
    public void onRecipientsListSuccess(JSONObject jsonObject) {
        NTF_Utils.hideProgressDialog();
        if (jsonObject != null) {
            NTF_Utils.saveRecipientsData(getActivity(), jsonObject);
        }
        ntf_Preferences.save(NTF_Constants.Prefs_Keys.LOGIN_API_STATUS, loginApiStatus);
        identifyUserActivity.doKioskLogout(true);
    }
}
