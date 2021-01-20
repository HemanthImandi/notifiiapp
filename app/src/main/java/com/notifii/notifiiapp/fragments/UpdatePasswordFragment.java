package com.notifii.notifiiapp.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.activities.MainActivity;
import com.notifii.notifiiapp.base.NTF_BaseFragment;
import com.notifii.notifiiapp.mvp.models.UpdateNamEmailRequest;
import com.notifii.notifiiapp.mvp.models.UpdatePasswordRequest;
import com.notifii.notifiiapp.mvp.models.UpdateUserNameRequest;
import com.notifii.notifiiapp.mvp.presenters.ContactUsPresenter;
import com.notifii.notifiiapp.mvp.presenters.UpdatePasswordPresenter;
import com.notifii.notifiiapp.mvp.views.UserPasswordView;
import com.notifii.notifiiapp.utils.NTF_Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/** This class deals with the Updating the password..
 */
public class UpdatePasswordFragment extends NTF_BaseFragment implements UserPasswordView {

    @BindView(R.id.editText_update_password)
    public EditText mEditTextUpdatePassword;
    @BindView(R.id.editText_confirm_password)
    public EditText mEditTextConfirmPassword;
    @BindView(R.id.button_update_password_submit)
    public Button mButtonUpdatePasswordSubmit;
    @BindView(R.id.spec_charsTV)
    public TextView spec_charsTV;
    @BindView(R.id.upper_caseTV)
    public TextView upper_caseTV;
    @BindView(R.id.lowercaseTV)
    public TextView lowercaseTV;
    @BindView(R.id.eightcharsTV)
    public TextView eightcharsTV;
    @BindView(R.id.oneNumTV)
    public TextView oneNumTV;
    @BindView(R.id.blank_spacesTV)
    public TextView blank_spacesTV;

    private String mUpdatePassword, mConfirmPassword;
    private String userId;
    UpdatePasswordPresenter updatePasswordPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_update_password, container, false);
        ButterKnife.bind(this, mainView);
        return mainView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(mainView);
        updatePasswordPresenter = new UpdatePasswordPresenter();
        updatePasswordPresenter.attachMvpView(this);
        spec_charsTV.setText("\u2022 " + spec_charsTV.getText().toString());
        upper_caseTV.setText("\u2022 " + upper_caseTV.getText().toString());
        lowercaseTV.setText("\u2022 " + lowercaseTV.getText().toString());
        oneNumTV.setText("\u2022 " + oneNumTV.getText().toString());
        blank_spacesTV.setText("\u2022 " + blank_spacesTV.getText().toString());
        eightcharsTV.setText("\u2022 " + eightcharsTV.getText().toString());
    }

    @Override
    public void onStackChanged() {
        super.onStackChanged();
        if (NTF_Utils.getCurrentFragment(this) != null && NTF_Utils.getCurrentFragment(this) instanceof UpdatePasswordFragment) {
            onFragmentPopup(true);
        } else {
            onFragmentPopup(false);
        }
    }

    private void bindViews(View v) {
        setToolbarTitle(getResources().getString(R.string.action_update_password));
        setToolbarActionButtons(true, false, 0);
        userId = ntf_Preferences.get(Prefs_Keys.USER_ID);
        mEditTextUpdatePassword.setAccessibilityDelegate(new View.AccessibilityDelegate() {
            public void onInitializeAccessibilityNodeInfo(View host,
                                                          AccessibilityNodeInfo info) {
                // Let the default implementation populate the info.
                super.onInitializeAccessibilityNodeInfo(host, info);
                // Set some other information.
                info.setEnabled(host.isEnabled());
            }
        });
    }

    @Override
    public void onInvalidPassword(String invalidMsg) {
        NTF_Utils.showAlert(getActivity(), "", invalidMsg, null);
    }

    // Preparing the Service request for update password..
    @Override
    public void onCredentialsValidated() {
        NTF_Utils.showProgressDialog(getActivity());
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setSessionId(ntf_Preferences.get(Prefs_Keys.SESSION_ID));
        updatePasswordRequest.setAuthenticationToken(ntf_Preferences.get(Prefs_Keys.AUTHENTICATION_TOKEN));
        updatePasswordRequest.setAccountId(ntf_Preferences.get(Prefs_Keys.ACCOUNT_ID));
        updatePasswordRequest.setUserId(userId);
        updatePasswordRequest.setPassword(mEditTextConfirmPassword.getText().toString());
        // Service request for update password..
        updatePasswordPresenter.doUserPasswordSubmit(null, updatePasswordRequest);
    }

    @Override
    public void onPasswordSucess(String message) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlertForFinish(getActivity(), ALERT_SUCCESS_TITLE, message);
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

    @OnClick(R.id.button_update_password_submit)
    public void onUpdatePasswordClick() {
        if(!NTF_Utils.isOnline(getActivity())) {
            NTF_Utils.showNoNetworkAlert(getActivity());
            return;
        }
        mUpdatePassword = mEditTextUpdatePassword.getText().toString();
        mConfirmPassword = mEditTextConfirmPassword.getText().toString();
        // This method deals with the input fields validation..
        updatePasswordPresenter.doCheckCredentials(mUpdatePassword, mConfirmPassword);
    }

}
