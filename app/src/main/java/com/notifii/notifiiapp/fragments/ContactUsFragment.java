package com.notifii.notifiiapp.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.base.NTF_BaseFragment;
import com.notifii.notifiiapp.mvp.models.ContactUsRequest;
import com.notifii.notifiiapp.mvp.presenters.ContactUsPresenter;
import com.notifii.notifiiapp.mvp.views.ContactUsView;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This class deals with the Contact Us details....
 */
public class ContactUsFragment extends NTF_BaseFragment implements ContactUsView {

    @BindView(R.id.editText_name)
    public EditText mEditTextName;
    @BindView(R.id.editText_email)
    public EditText mEditTextEmail;
    @BindView(R.id.editText_phone)
    public EditText mEditTextPhone;
    @BindView(R.id.editText_message)
    public EditText mEditTextMessage;
    @BindView(R.id.button_contactUs_submit)
    public Button mButtonContactUsSubmit;

    private String mName, mEmail, mPhone, mMessage;
    private String userId;
    public View mainView;
    ContactUsPresenter contactUsPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_contact_us, container, false);
        ButterKnife.bind(this, mainView);
        return mainView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews();
        contactUsPresenter = new ContactUsPresenter();
        contactUsPresenter.attachMvpView(this);
    }

    @Override
    public void onStackChanged() {
        super.onStackChanged();
        if (NTF_Utils.getCurrentFragment(this) != null && NTF_Utils.getCurrentFragment(this) instanceof ContactUsFragment) {
            onFragmentPopup(true);
        } else {
            onFragmentPopup(false);
        }
    }

    private void bindViews() {
        setToolbarTitle("Contact Us");
        setToolbarActionButtons(true, false, 0);
        userId = ntf_Preferences.get(NTF_Constants.Prefs_Keys.USER_ID);
    }

    private void clearFields() {
        mEditTextMessage.setText("");
        mEditTextName.setText("");
        mEditTextEmail.setText("");
        mEditTextPhone.setText("");
    }

    @Override
    public void onEmptyEmail() {
        NTF_Utils.showAlert(getActivity(), "", getString(R.string.email_error_field_required), null);
    }

    @Override
    public void onEmptyName() {
        NTF_Utils.showAlert(getActivity(), "", getString(R.string.name_error_field_required), null);
    }

    @Override
    public void onEmptyMessage() {
        NTF_Utils.showAlert(getActivity(), "", getString(R.string.msg_error_field_required), null);
    }

    @Override
    public void onInvalidEmail() {
        NTF_Utils.showAlert(getActivity(), "", getString(R.string.error_invalid_email), null);
    }

    // Preparing the Service request for Sending the details..
    @Override
    public void onCredentialsValidated() {
        NTF_Utils.showProgressDialog(getActivity());
        ContactUsRequest contactUsRequest = new ContactUsRequest();
        contactUsRequest.setSenderEmail(mEmail);
        contactUsRequest.setSenderName(mName);
        contactUsRequest.setSenderPhone(mPhone);
        contactUsRequest.setSenderMessage(mMessage);
        contactUsRequest.setAccountId(ntf_Preferences.get(Prefs_Keys.ACCOUNT_ID));
        contactUsRequest.setAuthenticationToken(ntf_Preferences.get(Prefs_Keys.AUTHENTICATION_TOKEN));
        contactUsRequest.setSessionId(ntf_Preferences.get(Prefs_Keys.SESSION_ID));
        contactUsRequest.setUserId(userId);
        // Service request for sending the details...
        contactUsPresenter.doContactDetailsSubmit(null, contactUsRequest);
    }

    @Override
    public void onContactSuccess(String message) {
        NTF_Utils.hideProgressDialog();
        clearFields();
        NTF_Utils.showAlert(getActivity(), ALERT_SUCCESS_TITLE, message, null);
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

    @OnClick(R.id.button_contactUs_submit)
    public void onContactSubmit(View v) {

        if (!NTF_Utils.isOnline(getActivity())) {
            NTF_Utils.showNoNetworkAlert(getActivity());
            return;
        }

        mName = mEditTextName.getText().toString();
        mEmail = mEditTextEmail.getText().toString();
        mPhone = mEditTextPhone.getText().toString();
        mMessage = mEditTextMessage.getText().toString();
        contactUsPresenter.doCheckCredentials(mName, mEmail, mPhone, mMessage);
    }

    @OnClick(R.id.backImage)
    public void backClick() {
        NTF_Utils.hideKeyboard(getActivity());
        getActivity().onBackPressed();
    }
}
