package com.notifii.notifiiapp.fragments;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.activities.MainActivity;
import com.notifii.notifiiapp.base.NTF_BaseFragment;
import com.notifii.notifiiapp.mvp.models.ProfileRequest;
import com.notifii.notifiiapp.mvp.models.ProfileResponse;
import com.notifii.notifiiapp.mvp.presenters.ContactUsPresenter;
import com.notifii.notifiiapp.mvp.presenters.ProfilePresenter;
import com.notifii.notifiiapp.mvp.views.ProfileView;
import com.notifii.notifiiapp.utils.NTF_Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MyProfileFragment extends NTF_BaseFragment implements ProfileView {

    @BindView(R.id.userFullNameTV)
    public TextView mTextViewFullName;
    @BindView(R.id.accountTypeTV)
    public TextView mTextViewAccountType;
    @BindView(R.id.emailTV)
    public TextView mTextViewEmailId;
    @BindView(R.id.userNameTV)
    public TextView mTextViewUsername;
    @BindView(R.id.nameLayout)
    public LinearLayout mLayoutName;
    @BindView(R.id.userNameLayout)
    public LinearLayout mLayoutUserName;
    @BindView(R.id.passwordLayout)
    public LinearLayout mLayoutPassword;
    @BindView(R.id.nameArrow)
    public ImageView nameArrowImageView;
    @BindView(R.id.userNameArrow)
    public ImageView userNameArrowImageView;
    @BindView(R.id.passwordArrow)
    public ImageView passwordArrowImageView;
    @BindView(R.id.emailLayout)
    public LinearLayout emailLayout;
    @BindView(R.id.emailArrow)
    public ImageView emailArrow;
    @BindView(R.id.uset_type_layout)
    public LinearLayout usetTypeLayout;

    private String userId;
    private Fragment fragment;
    private String fullName;
    private String email;
    ProfilePresenter profilePresenter;
    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_my_profile, container, false);
        ButterKnife.bind(this, mainView);
        return mainView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews();
        doPopulateDetails();
        setSetingsBasedUI();
    }

    @Override
    public void onStackChanged() {
        super.onStackChanged();
        if (NTF_Utils.getCurrentFragment(this) != null && NTF_Utils.getCurrentFragment(this) instanceof MyProfileFragment) {
            onFragmentPopup(true);
        } else {
            onFragmentPopup(false);
        }
    }


    private void bindViews() {
        setToolbarTitle("My Profile");
        setToolbarActionButtons(true, false, 0);
        userId = ntf_Preferences.get(Prefs_Keys.USER_ID);
        profilePresenter = new ProfilePresenter();
        profilePresenter.attachMvpView(this);
        doLoadUserDetails(userId);
    }

    // Preparing the Service request for getting the details..
    private void doLoadUserDetails(String userId) {
        if (getActivity() == null) {
            return;
        }
        if (NTF_Utils.isOnline(getActivity())) {
            NTF_Utils.showProgressDialog(getActivity());
            ProfileRequest profileRequest = new ProfileRequest();
            profileRequest.setSessionId(ntf_Preferences.get(Prefs_Keys.SESSION_ID));
            profileRequest.setAuthenticationToken(ntf_Preferences.get(Prefs_Keys.AUTHENTICATION_TOKEN));
            profileRequest.setAccountId(ntf_Preferences.get(Prefs_Keys.ACCOUNT_ID));
            profileRequest.setUserId(userId);
            // Service request for getting the details...
            profilePresenter.doProfileDetails(null, profileRequest);
        } else {
            NTF_Utils.showNoNetworkAlert(getActivity());
            ((MainActivity) getActivity()).registerReceiver();
        }
    }

    public void doPopulateDetails() {
        if (ntf_Preferences.hasKey(Prefs_Keys.FULL_NAME)) {
            mTextViewFullName.setText(ntf_Preferences.get(Prefs_Keys.FULL_NAME));
            fullName = mTextViewFullName.getText().toString();
        }
        if (ntf_Preferences.hasKey(Prefs_Keys.EMAIL)) {
            mTextViewEmailId.setText(ntf_Preferences.get(Prefs_Keys.EMAIL));
            email = mTextViewEmailId.getText().toString();
        }
        if (ntf_Preferences.hasKey(Prefs_Keys.USER_NAME)) {
            mTextViewUsername.setText(ntf_Preferences.get(Prefs_Keys.USER_NAME));
        }
    }

    @Override
    public void onProfileSucess(ProfileResponse profileResponse) {
        NTF_Utils.hideProgressDialog();
        mTextViewUsername.setText(profileResponse.getUsername());
        mTextViewAccountType.setText(profileResponse.getUserType());
        usetTypeLayout.setContentDescription(context.getString(R.string.account_user_type) + " " + profileResponse.getUserType());
        fullName = profileResponse.getFullName();
        mTextViewFullName.setText(fullName);
        email = profileResponse.getEmail();
        mTextViewEmailId.setText(email);
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
        return null;
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
    public void onErrorCode(String message) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(getActivity(), "", message, null);
    }

    @OnClick({R.id.backImage, R.id.mobile_left_linear})
    public void onBackClick() {
        getActivity().onBackPressed();
    }

    @OnClick({R.id.nameLayout, R.id.nameArrow})
    public void onNameClick() {
        fragment = new UpdateNameEmailFragment(fullName, email, true);
        if (!(NTF_Utils.getCurrentFragment(this) instanceof UpdateNameEmailFragment)) {
            changeDetailsFragment(fragment);
        }
    }

    @OnClick({R.id.emailLayout, R.id.emailArrow})
    public void onEmailClick() {
        if (ntf_Preferences.get(Prefs_Keys.EMAIL_OPTION_ENABLED,"").equalsIgnoreCase("0")){
            return;
        }
        fragment = new UpdateNameEmailFragment(fullName, email, false);
        if (!(NTF_Utils.getCurrentFragment(this) instanceof UpdateNameEmailFragment)) {
            changeDetailsFragment(fragment);
        }
    }

    @OnClick({R.id.userNameLayout, R.id.userNameArrow})
    public void onUserNameNameClick() {
        fragment = new UpdateUsernameFragment();
        if (!(NTF_Utils.getCurrentFragment(this) instanceof UpdateUsernameFragment)) {
            changeDetailsFragment(fragment);
        }
    }

    @OnClick({R.id.passwordLayout, R.id.passwordArrow})
    public void onPasswordClick() {
        fragment = new UpdatePasswordFragment();
        if (!(NTF_Utils.getCurrentFragment(this) instanceof UpdatePasswordFragment)) {
            changeDetailsFragment(fragment);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(finishBackgroundCall,
                    new IntentFilter(Extras_Keys.LOCAL_BROADCAST_END_ACTION));
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private BroadcastReceiver finishBackgroundCall = new BroadcastReceiver() {
        @Override
        public void onReceive(Context contextt, Intent intent) {
            setSetingsBasedUI();
        }
    };

    private void setSetingsBasedUI(){
        if (ntf_Preferences.get(Prefs_Keys.EMAIL_OPTION_ENABLED).equalsIgnoreCase("0")){
            emailArrow.setVisibility(View.GONE);
        } else {
            emailArrow.setVisibility(View.VISIBLE);
        }

        if (ntf_Preferences.get(Prefs_Keys.SSO_LOGIN_OPTION).equalsIgnoreCase("2")
                && ntf_Preferences.get(Prefs_Keys.SSO_STATUS).equalsIgnoreCase("1")){
            mLayoutUserName.setVisibility(View.GONE);
            mLayoutPassword.setVisibility(View.GONE);
        } else {
            mLayoutUserName.setVisibility(View.VISIBLE);
            mLayoutPassword.setVisibility(View.VISIBLE);
        }
    }
}
