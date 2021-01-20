package com.notifii.notifiiapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.notifii.notifiiapp.helpers.SingleTon;
import com.notifii.notifiiapp.models.SpinnerData;
import com.notifii.notifiiapp.mvp.models.KioskPackage;
import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.adapters.SelectPackagesAdapter;
import com.notifii.notifiiapp.base.NTF_BaseActivity;
import com.notifii.notifiiapp.mvp.models.RecipientPendingPackagesRequest;
import com.notifii.notifiiapp.mvp.models.RecipientPendingPackagesResponse;
import com.notifii.notifiiapp.mvp.presenters.RecipientPendingPackagesPresenter;
import com.notifii.notifiiapp.mvp.views.RecipientPendingPackagesView;
import com.notifii.notifiiapp.utils.NTF_Utils;

import java.util.ArrayList;
import java.util.Arrays;

public class SelectYourPackageActivity extends NTF_BaseActivity implements RecipientPendingPackagesView {

    @BindView(R.id.textViewActionTitle)
    TextView mTextViewActionTitle;
    @BindView(R.id.headinglineTV)
    TextView headinglineTV;
    @BindView(R.id.noPackagesFoundTV)
    TextView noPackagesFoundTV;
    @BindView(R.id.submitBTN)
    Button submitBTN;
    @BindView(R.id.iv_app_icon)
    ImageView logo;
    @BindView(R.id.selectpackages_LV)
    ListView listView;
    @BindView(R.id.errormessageFL)
    FrameLayout errormessageFL;
    @BindView(R.id.buttonsLL)
    LinearLayout buttonsLL;

    private SelectPackagesAdapter adapter;
    private ArrayList<KioskPackage> list;
    private String resident_id = "";
    public static final String SELECTED_PACKAGES = "selected-packages";
    private Handler handler=null;
    Runnable runnable=null;
    RecipientPendingPackagesPresenter presenter;
    private String selectedMailrooms = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_your_package);
        ButterKnife.bind(this);
        setToolbar();
        presenter=new RecipientPendingPackagesPresenter();
        presenter.attachMvpView(this);
        resident_id = getIntent().getStringExtra(IdentifyUserActivity.RESIDENT_ID);
        selectedMailrooms = ntf_Preferences.get(Prefs_Keys.SELECTED_MAILROOM);
        list = new ArrayList<>();
        errormessageFL.setVisibility(View.INVISIBLE);
        adapter = new SelectPackagesAdapter(list, this,ntf_Preferences);
        adapter.setMultipleMailrooms(selectedMailrooms.contains(","));
        listView.setAdapter(adapter);
        getPackages(resident_id);
    }

    private void setToolbar() {
        mTextViewActionTitle.setText("");
        mTextViewActionTitle.setBackgroundResource(R.drawable.ic_notifii_track_white_logo);
        logo.setVisibility(View.GONE);
    }

    @OnClick(R.id.startoverBTN)
    void onStartOverClicked(){
        if (handler!=null && runnable!=null){
            handler.removeCallbacks(runnable);
        }
        Intent loginActivityIntent = new Intent(this, IdentifyUserActivity.class);
        startActivity(loginActivityIntent);
        finishAffinity();
    }

    @OnClick(R.id.submitBTN)
    void onSubmitClicked(){
        if (getSelectecPackages().length() > 1) {
            Intent intent = new Intent(this, SignHereActivity.class);
            intent.putExtra(SELECTED_PACKAGES,getSelectecPackages());
            intent.putExtra(IdentifyUserActivity.RESIDENT_ID,resident_id);
            startActivity(intent);
        } else {
            errormessageFL.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.closeerrorTV)
    void onCloseErrorTV(){
        errormessageFL.setVisibility(View.INVISIBLE);
    }

    private String getSelectecPackages() {
        String selected_packages="";
        for (KioskPackage model : list) {
            if (model.isSelected()) {
                if (selected_packages.length() > 1) {
                    selected_packages = selected_packages + "," + model.getPackageId();
                } else {
                    selected_packages = selected_packages + model.getPackageId();
                }
            }
        }
        return selected_packages;
    }

    private void startHandler(){
        handler = new Handler();
        runnable=new Runnable() {
            @Override
            public void run() {
                Intent loginActivityIntent = new Intent(SelectYourPackageActivity.this, IdentifyUserActivity.class);
                startActivity(loginActivityIntent);
                finishAffinity();
            }
        };
        handler.postDelayed(runnable,5000);
    }

    public void getPackages(String resident_id) {
        try {
            NTF_Utils.showProgressDialog(this);
            RecipientPendingPackagesRequest request=new RecipientPendingPackagesRequest();
            request.setSessionId( ntf_Preferences.get(Prefs_Keys.SESSION_ID));
            request.setAuthenticationToken(ntf_Preferences.get(Prefs_Keys.AUTHENTICATION_TOKEN));
            request.setAccountId(ntf_Preferences.get(Prefs_Keys.ACCOUNT_ID));
            request.setUserId(ntf_Preferences.get(Prefs_Keys.USER_ID));
            request.setRecipientId(resident_id);
            presenter.getRecipientPackages(null,request);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        //it disables the back button functionality
    }

    private BroadcastReceiver finishBackgroundCall = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            adapter.resetSettings(ntf_Preferences);
            NTF_Utils.hideProgressDialog();
            NTF_Utils.handleBackgroundCallResponse(intent,SelectYourPackageActivity.this);
        }
    };

    private BroadcastReceiver startBackgroundCall = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NTF_Utils.showProgressDialog(SelectYourPackageActivity.this);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(startBackgroundCall,
                new IntentFilter(Extras_Keys.LOCAL_BROADCAST_START_ACTION));
        LocalBroadcastManager.getInstance(this).registerReceiver(finishBackgroundCall,
                new IntentFilter(Extras_Keys.LOCAL_BROADCAST_END_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(finishBackgroundCall);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(startBackgroundCall);
    }

    private void filterMailrooms(){
        String filteredString = "";
        ArrayList<SpinnerData> mailrooms = SingleTon.getInstance().getmMailRoomList(this);
        for (SpinnerData mailroom : mailrooms) {
            if (selectedMailrooms.contains(mailroom.getValue())){
                filteredString = filteredString + "," + mailroom.getValue();
            }
        }
        if (filteredString.startsWith(",")){
            filteredString = filteredString.substring(1);
        }
        selectedMailrooms = filteredString;
    }

    @Override
    public void onRequestSuccess(RecipientPendingPackagesResponse response) {
        NTF_Utils.hideProgressDialog();
        buttonsLL.setVisibility(View.VISIBLE);
        headinglineTV.setVisibility(View.VISIBLE);
        list.clear();
        list.addAll(response.getPackages());
        filterMailrooms();
        String[] mailrooms = selectedMailrooms.split(",");
        ArrayList<String> mrs = new ArrayList<String>(Arrays.asList(mailrooms));
        for (int i = list.size() - 1; i >= 0; i--) {
            if (!mrs.contains(list.get(i).getMailroomId())) {
                list.remove(list.get(i));
            }
        }
        if (list.size() == 0) {
            listView.setVisibility(View.GONE);
            headinglineTV.setText("You don't have any packages.");
            headinglineTV.setTextColor(getResources().getColor(R.color.red));
            submitBTN.setVisibility(View.GONE);
            startHandler();
        } else {
            adapter.notifyDataSetChanged();
        }
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
    public void onErrorCode(String message) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(this, "",message, null);
    }

}
