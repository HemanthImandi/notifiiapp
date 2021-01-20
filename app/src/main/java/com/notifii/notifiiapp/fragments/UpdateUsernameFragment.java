package com.notifii.notifiiapp.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.base.NTF_BaseFragment;
import com.notifii.notifiiapp.mvp.models.UpdateUserNameRequest;
import com.notifii.notifiiapp.mvp.models.UpdateUserNameResponse;
import com.notifii.notifiiapp.mvp.presenters.UpdateNameEmailPresenter;
import com.notifii.notifiiapp.mvp.presenters.UpdateUserNamePresenter;
import com.notifii.notifiiapp.mvp.views.UserNameView;
import com.notifii.notifiiapp.utils.NTF_Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * This class deals with the Updating the UserName...
 */

public class UpdateUsernameFragment extends NTF_BaseFragment implements UserNameView {

    @BindView(R.id.editText_update_user_name)
    public EditText mEditTextUpdateUsername;

    @BindView(R.id.button_update_username_submit)
    public Button mButtonUpdateUsernameSubmit;

    private String mUpdateUsername;
    private String userId;
    public View mainView;
    UpdateUserNamePresenter updateUserNamePresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_update_username, container, false);
        ButterKnife.bind(this, mainView);
        return mainView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews();
        updateUserNamePresenter = new UpdateUserNamePresenter();
        updateUserNamePresenter.attachMvpView(this);
    }

    @Override
    public void onStackChanged() {
        super.onStackChanged();
        if (NTF_Utils.getCurrentFragment(this) != null && NTF_Utils.getCurrentFragment(this) instanceof UpdateUsernameFragment) {
            onFragmentPopup(true);
        } else {
            onFragmentPopup(false);
        }
    }

    private void bindViews() {
        setToolbarTitle("Update Username");
        setToolbarActionButtons(true, false, 0);
        userId = ntf_Preferences.get(Prefs_Keys.USER_ID);
    }


    @Override
    public void onInvalidName(String message) {
        NTF_Utils.showAlert(getActivity(), "", message, null);
    }

    // Preparing service request for Updating the UserName..
    @Override
    public void onCredentialsValidated() {
        NTF_Utils.showProgressDialog(getActivity());
        UpdateUserNameRequest updateUserNameRequest = new UpdateUserNameRequest();
        updateUserNameRequest.setSessionId(ntf_Preferences.get(Prefs_Keys.SESSION_ID));
        updateUserNameRequest.setAuthenticationToken(ntf_Preferences.get(Prefs_Keys.AUTHENTICATION_TOKEN));
        updateUserNameRequest.setAccountId(ntf_Preferences.get(Prefs_Keys.ACCOUNT_ID));
        updateUserNameRequest.setUserId(userId);
        updateUserNameRequest.setUsername(mUpdateUsername);
        // Service request for Updating the UserName..
        updateUserNamePresenter.doUserNameSubmit(null, updateUserNameRequest);
    }

    @Override
    public void onSuccessUserName(String message) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlertForFinish(getActivity(), ALERT_SUCCESS_TITLE, message);
        ntf_Preferences.save(Prefs_Keys.USER_NAME, mEditTextUpdateUsername.getText().toString());
    }

    // when username was not available"
    @Override
    public void onUserNotAvailable(String message) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(getActivity(), ALERT_ERROR_TITLE_OOPS, message,null);
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
        NTF_Utils.hideKeyboard(getActivity());
        getActivity().onBackPressed();
    }

    // This method deals with the input fields validation...
    @OnClick(R.id.button_update_username_submit)
    public void onupdateUserNameSubmit() {
        if (!NTF_Utils.isOnline(getActivity())) {
            NTF_Utils.showNoNetworkAlert(getActivity());
            return;
        }
        mUpdateUsername = mEditTextUpdateUsername.getText().toString();
        updateUserNamePresenter.doCheckCredentials(mUpdateUsername);
    }
}
