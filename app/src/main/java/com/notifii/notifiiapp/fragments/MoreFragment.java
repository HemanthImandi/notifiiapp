package com.notifii.notifiiapp.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.activities.LoginActivity;
import com.notifii.notifiiapp.activities.MainActivity;
import com.notifii.notifiiapp.adapters.MultisiteAdapter;
import com.notifii.notifiiapp.base.NTF_BaseFragment;
import com.notifii.notifiiapp.helpers.SingleTon;
import com.notifii.notifiiapp.mvp.models.LinkedAccount;
import com.notifii.notifiiapp.mvp.models.LogoutRequest;
import com.notifii.notifiiapp.mvp.models.RecipientListRequest;
import com.notifii.notifiiapp.mvp.presenters.GlobalConstantsPresenter;
import com.notifii.notifiiapp.mvp.presenters.LogoutPresenter;
import com.notifii.notifiiapp.mvp.presenters.RecipientsListPresenter;
import com.notifii.notifiiapp.mvp.views.GlobalConstanctsView;
import com.notifii.notifiiapp.mvp.views.LogoutView;
import com.notifii.notifiiapp.mvp.views.RecipientsListView;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MoreFragment extends NTF_BaseFragment implements LogoutView, GlobalConstanctsView, RecipientsListView {

    @BindView(R.id.textView_version)
    TextView textViewVersion;
    @BindView(R.id.button_resident_list)
    TextView buttonResidentList;
    @BindView(R.id.button_search_resident)
    TextView buttonSearchResident;
    @BindView(R.id.button_new_resident)
    TextView buttonNewResident;
    @BindView(R.id.textView_account)
    TextView textViewAccount;
    @BindView(R.id.textView_user)
    TextView textViewUser;
    @BindView(R.id.layout_resident_list)
    LinearLayout layoutResidentList;
    @BindView(R.id.version_layout)
    LinearLayout versionLayout;
    @BindView(R.id.switch_accounts)
    LinearLayout switch_accounts;
    @BindView(R.id.layout_refresh)
    LinearLayout layout_refresh;

    private Activity activity;
    public static boolean isFromMoreFragment;
    private int id;
    LogoutPresenter logoutPresenter;
    GlobalConstantsPresenter globalConstantsPresenter;
    RecipientsListPresenter recipientsListPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_more, container, false);
        ButterKnife.bind(this, mainView);
        return mainView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BindViews();
        ((MainActivity) (getActivity() != null ? getActivity() : activity)).disableTransparentLayer();
        updateSwitchAccountLayout();
    }

    void updateSwitchAccountLayout(){
        JSONObject jsonObject = SingleTon.getInstance().getGlobalJson(getActivity());
        if (jsonObject != null && jsonObject.has("linked_accounts")) {
            switch_accounts.setVisibility(View.VISIBLE);
            layout_refresh.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_more_button_white));
        } else {
            switch_accounts.setVisibility(View.GONE);
            layout_refresh.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_more_buttons_grey));
        }
    }

    @Override
    public void onAttach(Context context) {
        activity = getActivity();
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        logoutPresenter = new LogoutPresenter();
        logoutPresenter.attachMvpView(this);
        globalConstantsPresenter = new GlobalConstantsPresenter();
        globalConstantsPresenter.attachMvpView(this);
        recipientsListPresenter = new RecipientsListPresenter();
        recipientsListPresenter.attachMvpView(this);
        super.onCreate(savedInstanceState);
    }

    private void BindViews() {
        setToolbarTitle("");
        setToolbarActionButtons(false, false, 0);
        setLogo(R.drawable.ic_notifii_track_white_logo);
        setAppIcon(false);
        setVersion();
        String recipientTypeLable = NTF_Utils.getRecipientTypeLabel(ntf_Preferences.get(Prefs_Keys.ACCOUNT_TYPE));
        buttonNewResident.setText("Add New " + recipientTypeLable);
        buttonResidentList.setText(recipientTypeLable + " List");
        layoutResidentList.setContentDescription(recipientTypeLable + " List Button");
        buttonSearchResident.setText("Search " + recipientTypeLable);
    }

    @Override
    public void onStackChanged() {
        super.onStackChanged();
        if (NTF_Utils.getCurrentFragment(this) != null && NTF_Utils.getCurrentFragment(this) instanceof MoreFragment) {
            onFragmentPopup(true);
        } else {
            onFragmentPopup(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isFromMoreFragment = true;
        textViewAccount.setText(ntf_Preferences.getString(Prefs_Keys.ACCOUNT_NAME, getActivity()));
        textViewAccount.setContentDescription(ntf_Preferences.getString(Prefs_Keys.ACCOUNT_NAME, getActivity()));
        textViewUser.setText(ntf_Preferences.getString(Prefs_Keys.FULL_NAME, getActivity()));
        textViewUser.setContentDescription(ntf_Preferences.getString(Prefs_Keys.FULL_NAME, getActivity()));
        try {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(finishBackgroundCall,
                    new IntentFilter(Extras_Keys.LOCAL_BROADCAST_END_ACTION));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Setting the version name to the TextView
    private void setVersion() {
        try {
            String versionName = getActivity().getPackageManager()
                    .getPackageInfo(getActivity().getPackageName(), 0).versionName;
            textViewVersion.setText(versionName);
            versionLayout.setContentDescription(getString(R.string.text_version) + " " + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.switch_accounts)
    void onSwitchAccountsClicked(){
        SwithAccountsFragment fragment = new SwithAccountsFragment();
        if (!(NTF_Utils.getCurrentFragment(this) instanceof SwithAccountsFragment)) {
            changeDetailsFragment(fragment);
        }
    }

    @OnClick(R.id.layout_resident_list)
    void onResidentListClicked() {
        if (ntf_Preferences.get(Prefs_Keys.PRIVILEGE_CORE_RECIPIENTS) != null) {
            if (!ntf_Preferences.get(Prefs_Keys.PRIVILEGE_CORE_RECIPIENTS).equalsIgnoreCase("n")) {
                id = 1;
                displayView(id);
            } else {
                NTF_Utils.showInSufficientPrivelegeAlert(getActivity() != null ? getActivity() : activity);
            }
        } else {
            id = 1;
            displayView(id);
        }
    }

    @OnClick(R.id.layout_new_resident)
    void onNewResidentClicked() {
        if (ntf_Preferences.get(Prefs_Keys.PRIVILEGE_CORE_RECIPIENTS) != null) {
            if (ntf_Preferences.get(Prefs_Keys.PRIVILEGE_CORE_RECIPIENTS).equalsIgnoreCase("f")) {
                id = 2;
                displayView(id);
            } else {
                NTF_Utils.showInSufficientPrivelegeAlert(getActivity() != null ? getActivity() : activity);
            }
        } else {
            id = 2;
            displayView(id);
        }
    }

    @OnClick(R.id.layout_pkg_history)
    void onPkgHistoryClicked() {
        if ((ntf_Preferences.get(Prefs_Keys.PRIVILEGE_TRACK_PACKAGES)) != null) {
            if (ntf_Preferences.get(Prefs_Keys.PRIVILEGE_TRACK_PACKAGES).equalsIgnoreCase("n")) {
                NTF_Utils.showInSufficientPrivelegeAlert(getActivity() != null ? getActivity() : activity);
            } else {
                id = 3;
                displayView(id);
            }
        } else {
            id = 3;
            displayView(id);
        }
    }

    @OnClick(R.id.layout_search_resident)
    void onSearchResidentClicked() {
        if ((ntf_Preferences.get(Prefs_Keys.PRIVILEGE_CORE_RECIPIENTS)) != null) {
            if (ntf_Preferences.get(Prefs_Keys.PRIVILEGE_CORE_RECIPIENTS).equalsIgnoreCase("n")) {
                NTF_Utils.showInSufficientPrivelegeAlert(getActivity() != null ? getActivity() : activity);
            } else {
                id = 9;
                displayView(id);
            }
        } else {
            id = 9;
            displayView(id);
        }
    }


    @OnClick(R.id.layout_contact_us)
    void onContactUsClicked() {
        id = 4;
        displayView(id);
    }

    @OnClick(R.id.layout_profile)
    void onProfileClicked() {
        id = 7;
        displayView(id);
    }

    @OnClick(R.id.layout_refresh)
    void onRefreshClicked() {
        id = 8;
        displayView(id);
    }

    @OnClick(R.id.button_account_tools_logout)
    void onlogoutClicked() {
        if (NTF_Utils.isOnline(getActivity())) {
            LogoutRequest request = new LogoutRequest();
            request.setAuthenticationToken(ntf_Preferences.get(Prefs_Keys.AUTHENTICATION_TOKEN));
            request.setSessionId(ntf_Preferences.get(Prefs_Keys.SESSION_ID));
            request.setAccountId(ntf_Preferences.get(Prefs_Keys.ACCOUNT_ID));
            request.setUserId(ntf_Preferences.get(Prefs_Keys.USER_ID));
            NTF_Utils.showProgressDialog(getActivity());
            logoutPresenter.doLogout(null, request);
        } else {
            NTF_Utils.showNoNetworkAlert(getActivity());
            ((MainActivity) getActivity()).registerReceiver();
        }
    }

    // Setting the View for corresponding Clicking button
    private void displayView(int position) {
        Fragment fragment = null;

        switch (position) {

            case 1:
                fragment = new CompleteRecipientListFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean(Extras_Keys.IS_FROM_SEARCH_RESIDENT, false);
                fragment.setArguments(bundle);
                if (!(NTF_Utils.getCurrentFragment(this) instanceof CompleteRecipientListFragment)) {
                    changeDetailsFragment(fragment);
                }
                break;

            case 2:
                fragment = new AddAndAndEditRecipientProfile();
                if (!(NTF_Utils.getCurrentFragment(this) instanceof AddAndAndEditRecipientProfile)) {
                    changeDetailsFragment(fragment);
                }
                break;

            case 3:
                fragment = new PackageHistoryFragment();
                Bundle bundle1 = new Bundle();
                bundle1.putBoolean(Extras_Keys.IS_FROM_SEARCH_PACKAGE, false);
                fragment.setArguments(bundle1);
                if (!(NTF_Utils.getCurrentFragment(this) instanceof PackageHistoryFragment)) {
                    changeDetailsFragment(fragment);
                }
                break;

            case 4:
                fragment = new ContactUsFragment();
                if (!(NTF_Utils.getCurrentFragment(this) instanceof ContactUsFragment)) {
                    changeDetailsFragment(fragment);
                }
                break;

            case 5:
                fragment = new UpdateUsernameFragment();
                if (!(NTF_Utils.getCurrentFragment(this) instanceof UpdateUsernameFragment)) {
//                    changeDetailsFragment(fragment);
                    NTF_Utils.showAlert(getActivity(), ALERT_ERROR_TITLE_OOPS, "Working in progress", null);
                }
                break;
            case 6:
                fragment = new UpdatePasswordFragment();
                if (!(NTF_Utils.getCurrentFragment(this) instanceof UpdatePasswordFragment)) {
                    changeDetailsFragment(fragment);
                }
                break;

            case 7:
                fragment = new MyProfileFragment();
                if (!(NTF_Utils.getCurrentFragment(this) instanceof MyProfileFragment)) {
                    changeDetailsFragment(fragment);
                }
                break;

            case 8:
                getGlobalConstancts();
                break;

            case 9:
                fragment = new SearchRecipientFragment();
                if (!(NTF_Utils.getCurrentFragment(this) instanceof SearchRecipientFragment)) {
                    changeDetailsFragment(fragment);
                }

                break;
        }
    }

    private void getGlobalConstancts() {
        if (NTF_Utils.isOnline(getActivity())) {
            NTF_Utils.showProgressDialog(getActivity());
            globalConstantsPresenter.getGlobalConstancts(null, NTF_Utils.getGlobalConstantsRequestObject("refresh_action", ntf_Preferences));
        } else {
            NTF_Utils.showNoNetworkAlert(getActivity());
            ((MainActivity) getActivity()).registerReceiver();
        }
    }

    @Override
    public void onLogoutSuccess(String message) {
        ntf_Preferences.clearAllPrefs();
        SingleTon.clearInstance();
        NTF_Utils.deleteFile(getContext(), RECIPIENT_DATA_FILE_NAME);
        NTF_Utils.deleteFile(getContext(), GLOBAL_DATA_FILE_NAME);

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(finishBackgroundCall);
        } catch (Exception e) {
            e.printStackTrace();
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
        NTF_Utils.showAlert(getActivity(), "", NTF_Utils.getErrorMessage(throwable), null);
    }

    @Override
    public void onNoInternetConnection() {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showNoNetworkAlert(getActivity());
    }


    @Override
    public void onErrorCode(String s) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(getActivity(), "", s, null);
    }

    @Override
    public void getGlobalConstanctsFail(String message) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(getActivity(), "", message, null);
    }

    @Override
    public void getGlobalConstanctsSuccess(JSONObject response) {
        RecipientListRequest recipientListRequest = new RecipientListRequest();
        recipientListRequest.setApiMode("refresh_action");
        recipientListRequest.setSessionId(ntf_Preferences.get(Prefs_Keys.SESSION_ID));
        recipientListRequest.setAuthenticationToken(ntf_Preferences.get(Prefs_Keys.AUTHENTICATION_TOKEN));
        recipientListRequest.setAccountId(ntf_Preferences.get(Prefs_Keys.ACCOUNT_ID));
        recipientListRequest.setUserId(ntf_Preferences.get(NTF_Constants.Prefs_Keys.USER_ID));
        recipientsListPresenter.getRecipientList(null, recipientListRequest);
        NTF_Utils.saveGlobalConstants(getActivity(), response, ntf_Preferences);
        SingleTon.getInstance().setmMailRoomList(null);
        SingleTon.getInstance().setmGobalJsonData(null);
        updateSwitchAccountLayout();
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
            NTF_Utils.saveRecipientsData(getContext(), jsonObject);

        }
        NTF_Utils.showAlert(getActivity(), ALERT_SUCCESS_TITLE, "All account settings have been refreshed.", null);
        SingleTon.getInstance().setLogPkgInNeedRefesh(true);
    }

    private BroadcastReceiver finishBackgroundCall = new BroadcastReceiver() {
        @Override
        public void onReceive(Context contextt, Intent intent) {
            updateSwitchAccountLayout();
        }
    };
}