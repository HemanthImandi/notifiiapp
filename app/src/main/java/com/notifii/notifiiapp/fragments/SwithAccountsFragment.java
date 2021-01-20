package com.notifii.notifiiapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.activities.MFAActivity;
import com.notifii.notifiiapp.activities.MainActivity;
import com.notifii.notifiiapp.adapters.MultisiteAdapter;
import com.notifii.notifiiapp.base.NTF_BaseFragment;
import com.notifii.notifiiapp.helpers.SingleTon;
import com.notifii.notifiiapp.models.CustomTypefaceSpan;
import com.notifii.notifiiapp.mvp.models.GetGlobalConstantsRequest;
import com.notifii.notifiiapp.mvp.models.LinkedAccount;
import com.notifii.notifiiapp.mvp.models.LoginResponse;
import com.notifii.notifiiapp.mvp.models.SwitchAccountRequest;
import com.notifii.notifiiapp.mvp.presenters.GlobalConstantsPresenter;
import com.notifii.notifiiapp.mvp.presenters.RecipientsListPresenter;
import com.notifii.notifiiapp.mvp.presenters.SwitchAccountsPresenter;
import com.notifii.notifiiapp.mvp.views.GlobalConstanctsView;
import com.notifii.notifiiapp.mvp.views.MultisiteLoginView;
import com.notifii.notifiiapp.mvp.views.RecipientsListView;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SwithAccountsFragment extends NTF_BaseFragment implements MultisiteLoginView, GlobalConstanctsView, RecipientsListView {

    @BindView(R.id.account_name_TV)
    public TextView account_name_TV;
    @BindView(R.id.listview)
    public ListView listview;

    String userid, accountid;
    ArrayList<LinkedAccount> accounts = new ArrayList<>();
    SwitchAccountsPresenter switchAccountsPresenter;
    private String loginApiStatus = "";
    GlobalConstantsPresenter globalConstantsPresenter;
    RecipientsListPresenter recipientsListPresenter;
    String sessionTimeOut = "";
    String suucessApiMessage = "";


    public SwithAccountsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_swith_accounts, container, false);
        ButterKnife.bind(this, mainView);
        return mainView;
    }

    private void setAccountName(){
        try {
            String text1 = "You are currently logged in at ";
            String text2 = ntf_Preferences.getString(Prefs_Keys.ACCOUNT_NAME, getActivity());
            Spannable spannable = new SpannableString(text1 + text2);
            Typeface face1 = Typeface.createFromAsset(getActivity().getAssets(), "font/avenir_roman.ttf");
            Typeface face2 = Typeface.createFromAsset(getActivity().getAssets(), "font/avenir_85_heavy.ttf");
            spannable.setSpan(new CustomTypefaceSpan("sans-serif", face1), 0, text1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new CustomTypefaceSpan("sans-serif", face2), text1.length(), text1.length() + text2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            account_name_TV.setText(spannable);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setToolbarTitle("Switch Accounts");
        setToolbarActionButtons(true, false, 0);
        sessionTimeOut = ntf_Preferences.get(Prefs_Keys.SESSION_TIMEOUT);
        switchAccountsPresenter = new SwitchAccountsPresenter();
        switchAccountsPresenter.attachMvpView(this);
        globalConstantsPresenter = new GlobalConstantsPresenter();
        globalConstantsPresenter.attachMvpView(this);
        recipientsListPresenter = new RecipientsListPresenter();
        recipientsListPresenter.attachMvpView(this);
        setAccountName();

        try {
            JSONObject jsonObject = SingleTon.getInstance().getGlobalJson(getActivity());
            JSONArray accountsArray = jsonObject.getJSONArray("linked_accounts");
            userid=ntf_Preferences.get(Prefs_Keys.USER_ID);
            accountid=ntf_Preferences.get(Prefs_Keys.ACCOUNT_ID);
            for (int i=0; i<accountsArray.length(); i++){
                if (!accountsArray.getJSONObject(i).getString("user_id").equalsIgnoreCase(userid)
                && !accountsArray.getJSONObject(i).getString("account_id").equalsIgnoreCase(accountid)) {
                    LinkedAccount account = new LinkedAccount();
                    account.setUserId(accountsArray.getJSONObject(i).getString("user_id"));
                    account.setAccountId(accountsArray.getJSONObject(i).getString("account_id"));
                    account.setAccountName(accountsArray.getJSONObject(i).getString("account_name"));
                    accounts.add(account);
                }
            }
            accounts.get(0).setSelected(true);
            MultisiteAdapter adapter = new MultisiteAdapter(accounts,getActivity());
            listview.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.switch_btn)
    void switchAccount(){
        try {
            for (LinkedAccount account : accounts) {
                if (account.isSelected()) {
                    NTF_Utils.showProgressDialog(getActivity());
                    String versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
                    String header = NTF_Utils.getHeader(ntf_Preferences, getActivity());
                    SwitchAccountRequest request = new SwitchAccountRequest();
                    request.setAccountId(accountid);
                    request.setSessionId(ntf_Preferences.get(Prefs_Keys.SESSION_ID));
                    request.setAuthenticationToken(ntf_Preferences.get(Prefs_Keys.AUTHENTICATION_TOKEN));
                    request.setUserId(userid);
                    request.setSwitchedAccountId(account.getAccountId());
                    request.setSwitchedUserId(account.getUserId());
                    request.setSessionTimedout(sessionTimeOut);
                    request.setDeviceUniqueId(NTF_Utils.getUUID(getActivity()));
                    request.setAppVersion(versionName);
                    switchAccountsPresenter.doSwitchAccounts(header,request);
                    return;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @OnClick(R.id.backImage)
    public void backClick() {
        NTF_Utils.hideKeyboard(getActivity());
        getActivity().onBackPressed();
    }


    @Override
    public void onMFAConfigured(LoginResponse loginResponse) {
        NTF_Utils.hideProgressDialog();
         Intent intent = new Intent(getActivity(), MFAActivity.class);
        intent.putExtra(MFAActivity.user_id,loginResponse.getUserId());
        intent.putExtra(MFAActivity.acc_id,loginResponse.getAccountId());
        intent.putExtra(MFAActivity.session_timeout,loginResponse.getSessionTimedout());
        intent.putExtra(MFAActivity.user_name,account_name_TV.getText().toString());
        startActivity(intent);
    }

    @Override
    public void onMultisiteLoginSuccess(LoginResponse loginResponse) {
        ntf_Preferences.clearAllPrefs();
        ntf_Preferences.save(Prefs_Keys.SESSION_TIMEOUT,sessionTimeOut);
        NTF_Utils.doSaveUserDetails(loginResponse, ntf_Preferences);
        SingleTon.clearInstance();
        NTF_Utils.deleteFile(getContext(), RECIPIENT_DATA_FILE_NAME);
        NTF_Utils.deleteFile(getContext(), GLOBAL_DATA_FILE_NAME);
        loginApiStatus = loginResponse.getApiStatus();
        getGlobalConstancts();
        suucessApiMessage = loginResponse.getApiMessage();
    }

    private void getGlobalConstancts() {
        if (NTF_Utils.isOnline(getActivity())) {
            GetGlobalConstantsRequest request = NTF_Utils.getGlobalConstantsRequestObject("switch_action", ntf_Preferences);
            globalConstantsPresenter.getGlobalConstancts(null, request);
            Log.d("getGlobalConstancts","session:"+request.getSessionId());
        } else {
            NTF_Utils.hideProgressDialog();
            NTF_Utils.showNoNetworkAlert(getActivity());
            ntf_Preferences.save(Prefs_Keys.IS_LOGGED_IN, false);
        }
    }

    @Override
    public void getGlobalConstanctsFail(String message) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(getActivity(), "", message, null);
    }

    @Override
    public void getGlobalConstanctsSuccess(JSONObject response) {
        getRecipientsList();
        NTF_Utils.saveGlobalConstants(getActivity(), response, ntf_Preferences);
    }

    @Override
    public void onRecipientsListSuccess(JSONObject jsonObject) {
        NTF_Utils.hideProgressDialog();
        if (jsonObject != null) {
            NTF_Utils.saveRecipientsData(getActivity(), jsonObject);
        }
        SingleTon.getInstance().setPendingPackagesAPIMode("switch_action");
        NTF_Utils.showAlert(getActivity(), ALERT_SUCCESS_TITLE, suucessApiMessage, new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                if (loginApiStatus.equalsIgnoreCase(ResponseKeys.RESET_PASSWORD) || loginApiStatus.equalsIgnoreCase(ResponseKeys.RESET_USERNAME_PASSWORD)) {
                    intent.putExtra(NTF_Constants.Extras_Keys.LOGIN_API_STATUS, loginApiStatus);
                }
                ntf_Preferences.save(Prefs_Keys.IS_LOGGED_IN, true);
                NTF_Utils.startAlarmIfRequired(getActivity());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    @Override
    public void onRecipientListError(String message) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(getActivity(), "", message, null);
    }

    private void getRecipientsList() {
        if (NTF_Utils.isOnline(getActivity())) {
            recipientsListPresenter.getRecipientList(null, NTF_Utils.getRecipientListRequestObject("switch_action", ntf_Preferences));
        } else {
            ntf_Preferences.save(Prefs_Keys.IS_LOGGED_IN, false);
            NTF_Utils.hideProgressDialog();
            NTF_Utils.showNoNetworkAlert(getActivity());
        }
    }

    @Override
    public void onSessionExpired(String message) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showSessionExpireAlert(message, getActivity(), ntf_Preferences);
    }

    @Override
    public void onServerError() {
        NTF_Utils.hideProgressDialog();
    }

    @Override
    public Context getMvpContext() {
        return getActivity();
    }

    @Override
    public void onError(Throwable throwable) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(getActivity(), ALERT_ERROR_TITLE_OOPS, NTF_Utils.getErrorMessage(throwable), null);
    }

    @Override
    public void onNoInternetConnection() {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showNoNetworkAlert(getActivity());
    }

    @Override
    public void onErrorCode(String s) {
        NTF_Utils.showAlert(getActivity(), ALERT_ERROR_TITLE_OOPS, s, null);
        NTF_Utils.hideProgressDialog();
    }
}