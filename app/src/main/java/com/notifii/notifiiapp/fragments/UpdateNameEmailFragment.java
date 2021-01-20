package com.notifii.notifiiapp.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.base.NTF_BaseFragment;
import com.notifii.notifiiapp.mvp.models.UpdateNamEmailRequest;
import com.notifii.notifiiapp.mvp.presenters.ContactUsPresenter;
import com.notifii.notifiiapp.mvp.presenters.UpdateNameEmailPresenter;
import com.notifii.notifiiapp.mvp.views.UserNameEmailView;
import com.notifii.notifiiapp.utils.NTF_Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UpdateNameEmailFragment extends NTF_BaseFragment implements UserNameEmailView {

    @BindView(R.id.editText_update_name)
    public EditText mEditTextName;
    @BindView(R.id.editText_update_email)
    public EditText mEditTextEmail;
    @BindView(R.id.button_update_name_email_submit)
    public Button mButtonSubmit;

    public String fullNameValue;
    public String emailValue;
    private String userId;
    private String name;
    private String email;
    private boolean isName;
    UpdateNameEmailPresenter updateNameEmailPresenter;


    public UpdateNameEmailFragment(String name, String email, boolean isName) {
        fullNameValue = name;
        emailValue = email;
        this.isName = isName;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_update_name_email, container, false);
        ButterKnife.bind(this, mainView);
        return mainView;
    }

    @Override
    public void onStackChanged() {
        super.onStackChanged();
        if (NTF_Utils.getCurrentFragment(this) != null && NTF_Utils.getCurrentFragment(this) instanceof UpdateNameEmailFragment) {
            onFragmentPopup(true);
        } else {
            onFragmentPopup(false);
        }
    }

    private void bindViews() {
        if (isName) {
            setToolbarTitle("Update Name");
            mEditTextEmail.setVisibility(View.GONE);
        } else {
            setToolbarTitle("Update Email");
            mEditTextName.setVisibility(View.GONE);
        }
        setToolbarActionButtons(true, false, 0);
        mEditTextName.setText(fullNameValue);
        mEditTextEmail.setText(emailValue);
        userId = ntf_Preferences.get(Prefs_Keys.USER_ID);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews();
        updateNameEmailPresenter = new UpdateNameEmailPresenter();
        updateNameEmailPresenter.attachMvpView(this);
        NTF_Utils.handleDoneButton(mEditTextName, getActivity());
        NTF_Utils.handleDoneButton(mEditTextEmail, getActivity());
    }

    @Override
    public void onEmptyEmail() {
        NTF_Utils.showAlert(getActivity(), "", getString(R.string.email_error_field_required), null);
    }

    @Override
    public void onEmptyName() {
        NTF_Utils.showAlert(getActivity(), "", "Please enter your name.", null);
    }

    @Override
    public void onInvalidEmail() {
        NTF_Utils.showAlert(getActivity(), "", getString(R.string.error_invalid_email), null);
    }

    @Override
    public void onCredentialsValidated() {
        NTF_Utils.showProgressDialog(getActivity());
        UpdateNamEmailRequest updateNamEmailRequest = new UpdateNamEmailRequest();
        updateNamEmailRequest.setSessionId(ntf_Preferences.get(Prefs_Keys.SESSION_ID));
        updateNamEmailRequest.setAuthenticationToken(ntf_Preferences.get(Prefs_Keys.AUTHENTICATION_TOKEN));
        updateNamEmailRequest.setAccountId(ntf_Preferences.get(Prefs_Keys.ACCOUNT_ID));
        updateNamEmailRequest.setUserId(userId);
        if (isName) {
            updateNamEmailRequest.setFullName(name);
            updateNamEmailRequest.setEmail("");
        } else {
            updateNamEmailRequest.setFullName("");
            updateNamEmailRequest.setEmail(email);
        }
        updateNameEmailPresenter.doUserNameEmailSubmit(null, updateNamEmailRequest);
    }

    @Override
    public void onSuccess(String message) {
        NTF_Utils.hideProgressDialog();
        ntf_Preferences.save(Prefs_Keys.FULL_NAME, name);
        ntf_Preferences.save(Prefs_Keys.EMAIL, email);
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

    @OnClick(R.id.button_update_name_email_submit)
    public void onUpdateNameEmailSubmit() {
        if (!NTF_Utils.isOnline(getActivity())) {
            NTF_Utils.showNoNetworkAlert(getActivity());
            return;
        }
        name = mEditTextName.getText().toString().trim();
        email = mEditTextEmail.getText().toString().trim();
        updateNameEmailPresenter.doCheckCredentials(name, email, isName);

    }

    @OnClick({R.id.backImage, R.id.mobile_left_linear})
    public void onBackClick() {
        NTF_Utils.hideKeyboard(getActivity());
        getActivity().onBackPressed();
    }


}
