package com.notifii.notifiiapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.adapters.MailroomAdapter;
import com.notifii.notifiiapp.base.NTF_BaseActivity;
import com.notifii.notifiiapp.helpers.SingleTon;
import com.notifii.notifiiapp.models.SpinnerData;
import com.notifii.notifiiapp.mvp.models.KioskLoginRequest;
import com.notifii.notifiiapp.mvp.models.KioskLoginResponse;
import com.notifii.notifiiapp.mvp.presenters.KioskLoginPresenter;
import com.notifii.notifiiapp.mvp.views.KioskLoginView;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectMailroomActivity extends NTF_BaseActivity implements KioskLoginView {

    @BindView(R.id.recycler_view)
    ListView listview;
    @BindView(R.id.errormessageFL)
    View errormessageFL;

    private ArrayList<SpinnerData> mailrooms;
    private KioskLoginPresenter kioskLoginPresenter;
    private String selectedMailRoom= "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mailroom);
        ButterKnife.bind(this);
        mailrooms = new ArrayList<>();
        mailrooms.addAll(SingleTon.getInstance().getmMailRoomList(this));
        SpinnerData.resetData(mailrooms);
        MailroomAdapter adapter = new MailroomAdapter(mailrooms, this);
        listview.setAdapter(adapter);
        kioskLoginPresenter = new KioskLoginPresenter();
        kioskLoginPresenter.attachMvpView(this);
    }

    @OnClick(R.id.submit_btn)
    void onSubmit(){
        selectedMailRoom = "";
        for (SpinnerData mailroom : mailrooms) {
            if (mailroom.isSelected()) {
                selectedMailRoom = selectedMailRoom + "," + mailroom.getValue();
            }
        }
        if (selectedMailRoom.startsWith(",")) {
            selectedMailRoom = selectedMailRoom.substring(1);
        }
        if (selectedMailRoom.isEmpty()){
            NTF_Utils.showAlert(this,ALERT_ERROR_TITLE_OOPS,"You must choose a location.",null);
        } else {
            try {
                if (!NTF_Utils.isOnline(this)) {
                    NTF_Utils.showNoNetworkAlert(this);
                    return;
                }
                NTF_Utils.showProgressDialog(this);
                ntf_Preferences.save(NTF_Constants.Prefs_Keys.IS_KIOSK_MODE, true);
                KioskLoginRequest request = new KioskLoginRequest();
                request.setSessionId(ntf_Preferences.get(NTF_Constants.Prefs_Keys.SESSION_ID));
                request.setAuthenticationToken(ntf_Preferences.get(NTF_Constants.Prefs_Keys.AUTHENTICATION_TOKEN));
                request.setAccountId(ntf_Preferences.get(NTF_Constants.Prefs_Keys.ACCOUNT_ID));
                request.setUserId(ntf_Preferences.get(NTF_Constants.Prefs_Keys.USER_ID));
                request.setDeviceUniqueId(NTF_Utils.getUUID(this));
                String header = NTF_Utils.getHeader(ntf_Preferences, this);
                kioskLoginPresenter.doKioskLogin(header, request,ntf_Preferences);
            } catch (Exception e) {
                ntf_Preferences.save(NTF_Constants.Prefs_Keys.IS_KIOSK_MODE, false);
                e.printStackTrace();
            }
        }
    }

    @OnClick(R.id.cancleLL)
    void onCancel(){
        finish();
    }

    @OnClick(R.id.closeerrorTV)
    void onCloseError(){
        errormessageFL.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onKioskLoginSuccess(KioskLoginResponse loginResponse) {
        try {
            ntf_Preferences.save(Prefs_Keys.KIOSK_DISPLAY_RECIPIENT_FORMAT, loginResponse.getKioskDisplayRecipientFormat());
            ntf_Preferences.save(Prefs_Keys.SESSION_ID, loginResponse.getSessionId());
            ntf_Preferences.save(Prefs_Keys.AUTHENTICATION_TOKEN, loginResponse.getAuthenticationToken());
            ntf_Preferences.save(Prefs_Keys.EXPIRATION, loginResponse.getExpiration());
            ntf_Preferences.save(Prefs_Keys.ACCOUNT_ID, loginResponse.getAccountId());
            ntf_Preferences.save(Prefs_Keys.USER_ID, loginResponse.getUserId());
            ntf_Preferences.save(Prefs_Keys.TIMEZONE, loginResponse.getTimezone());
            ntf_Preferences.save(Prefs_Keys.KIOSK_MODE_DISCLAIMER, loginResponse.getKioskModeDisclaimer());
            ntf_Preferences.save(Prefs_Keys.TIMEZONE_SHORTCODE, loginResponse.getTimezoneidShortcode());
            ntf_Preferences.save(Prefs_Keys.SELECTED_MAILROOM, selectedMailRoom);
            ntf_Preferences.save(Prefs_Keys.ACCOUNT_TYPE, loginResponse.getAccountType());
            ntf_Preferences.save(Prefs_Keys.USE_FRONT_CAMERA, loginResponse.getUseFrontCamera());
            Intent intent = new Intent(this, IdentifyUserActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finishAffinity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSessionExpired(String message) {
        try {
            NTF_Utils.hideProgressDialog();
            NTF_Utils.showSessionExpireAlert(message, this, ntf_Preferences);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //to handle server error
    @Override
    public void onServerError() {
        NTF_Utils.hideProgressDialog();
    }

    @Override
    public Context getMvpContext() {
        return this;
    }

    // to handle error from server
    @Override
    public void onError(Throwable throwable) {
        try {
            NTF_Utils.hideProgressDialog();
            NTF_Utils.showAlert(this, "", NTF_Utils.getErrorMessage(throwable), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //to handle no internet case
    @Override
    public void onNoInternetConnection() {
        try {
            NTF_Utils.hideProgressDialog();
            NTF_Utils.showNoNetworkAlert(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // to handle error case in api response in this screen
    @Override
    public void onErrorCode(String s) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(this, "", s, null);
    }
}
