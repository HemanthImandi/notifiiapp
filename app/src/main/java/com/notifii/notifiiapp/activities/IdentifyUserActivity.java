package com.notifii.notifiiapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.adapters.KioskAutoCompleteCustomAdapter;
import com.notifii.notifiiapp.adapters.KioskAutoCompleteUnitAdapter;
import com.notifii.notifiiapp.adapters.MailroomAdapter;
import com.notifii.notifiiapp.base.NTF_BaseActivity;
import com.notifii.notifiiapp.customui.CustomAutoCompleteTextView;
import com.notifii.notifiiapp.fragments.KioskExitDialogFragment;
import com.notifii.notifiiapp.helpers.SingleTon;
import com.notifii.notifiiapp.models.Recipient;
import com.notifii.notifiiapp.models.SpinnerData;
import com.notifii.notifiiapp.receivers.ConnectionChangeReceiver;
import com.notifii.notifiiapp.refresh.RecipientItemClickNoticeListener;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;

import static com.notifii.notifiiapp.utils.NTF_Constants.ResponseKeys.ERROR;
import static com.notifii.notifiiapp.utils.NTF_Constants.ResponseKeys.SESSION_EXPIRED;

public class IdentifyUserActivity extends NTF_BaseActivity {

    @BindView(R.id.textViewActionTitle)
    TextView mTextViewActionTitle;
    @BindView(R.id.buttonsLL)
    LinearLayout buttonsLL;
    @BindView(R.id.backImage)
    ImageView backImage;
    @BindView(R.id.iv_app_icon)
    ImageView logo;
    @BindView(R.id.editTextRecipientName)
    CustomAutoCompleteTextView autoCompleteTextView;
    @BindView(R.id.textViewAutoCompleteRecipient)
    TextView mTextViewAutoCompleteRecipient;
    @BindView(R.id.ll_recipientName)
    LinearLayout mLlRecipientName;
    @BindView(R.id.errormessageFL)
    FrameLayout errormessageFL;
    @BindView(R.id.recipientNameClear)
    ImageView recipientNameClear;
    @BindView(R.id.editTextRecipientNameFL)
    FrameLayout editTextRecipientNameFL;
    @BindView(R.id.edittextHintTV)
    TextView edittextHintTV;


    private ArrayList<Recipient> mRecipientArrayList;
    private ArrayList<String> displayUnitList;
    private Recipient mRecipient;
    public static final String RESIDENT_ID = "resident-id";
    private String displayformat = "ccc";
    private String selectedUnitNumber = "";
    KioskAutoCompleteCustomAdapter autoCompleteCustomAdapter = null;
    KioskAutoCompleteUnitAdapter autoCompleteUnitAdapter = null;
    private final BroadcastReceiver mybroadcast = new ConnectionChangeReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify_user);
        ButterKnife.bind(this);
        setToolbar();
        displayformat = ntf_Preferences.get(Prefs_Keys.KIOSK_DISPLAY_RECIPIENT_FORMAT, "ccc");
        errormessageFL.setVisibility(View.INVISIBLE);
        setRecipientSpinnerData();
        try {
            edittextHintTV.setText("Type in a few letters of your first name, last name, or " +
                    NTF_Utils.getRecipientAddress1Label(ntf_Preferences.get(Prefs_Keys.ACCOUNT_TYPE)).toLowerCase());
            Typeface face = Typeface.createFromAsset(getAssets(),
                    "font/avenir_85_heavy.ttf");
            autoCompleteTextView.setTypeface(face);
        } catch (Exception e){
            e.printStackTrace();
        }
       /* if(!ntf_Preferences.get(Prefs_Keys.USE_FRONT_CAMERA, "0").equals("0")){
            NTF_Utils.checkCameraPermissionToProgress(this, null);
        }*/
    }

    private BroadcastReceiver finishBackgroundCall = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            displayformat = ntf_Preferences.get(Prefs_Keys.KIOSK_DISPLAY_RECIPIENT_FORMAT, "ccc");
            setRecipientSpinnerData();
            NTF_Utils.hideProgressDialog();
            NTF_Utils.handleBackgroundCallResponse(intent,IdentifyUserActivity.this);
        }
    };

    @OnEditorAction(R.id.editTextRecipientName)
    boolean onEditorAction(TextView v, int actionId, KeyEvent key) {
        if (actionId == EditorInfo.IME_ACTION_GO) {
            NTF_Utils.hideKeyboard(this);
            return true;
        }
        return false;
    }

    @OnTextChanged(R.id.editTextRecipientName)
    void onTextChanged(CharSequence text) {
        try {
            onFocusChanged(true);
            edittextHintTV.setVisibility(text.toString().length() == 0 ? View.VISIBLE : View.GONE);
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    @OnFocusChange(R.id.editTextRecipientName)
    void onFocusChanged(boolean focused) {
        if (focused && autoCompleteTextView.isFocused() && autoCompleteTextView.getText().toString().length() >= 2) {
            recipientNameClear.setVisibility(View.VISIBLE);
        } else {
            recipientNameClear.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.recipientNameClear)
    void clearSender() {
        autoCompleteTextView.setText("");
        recipientNameClear.setVisibility(View.GONE);
    }


    private BroadcastReceiver startBackgroundCall = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NTF_Utils.showProgressDialog(IdentifyUserActivity.this);
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

    private void setToolbar() {
        backImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_kiosk_logout));
        backImage.setVisibility(View.VISIBLE);
        backImage.setBackgroundColor(Color.TRANSPARENT);
        backImage.setContentDescription("Exit kiosk mode");
        mTextViewActionTitle.setText("");
        mTextViewActionTitle.setBackgroundResource(R.drawable.ic_notifii_track_white_logo);
        logo.setVisibility(View.GONE);
    }

    private void doPerformNext() {
        if (displayformat.length() == 0 || (displayformat.length() == 3 && !displayformat.startsWith("hh"))) {
            if (mRecipient == null) {
                errormessageFL.setVisibility(View.VISIBLE);
            } else {
                Intent intent = new Intent(this, SelectYourPackageActivity.class);
                intent.putExtra(RESIDENT_ID, mRecipient.getRecipientId());
                startActivity(intent);
            }
        } else {
            if (selectedUnitNumber == null || selectedUnitNumber.isEmpty()) {
                errormessageFL.setVisibility(View.VISIBLE);
            } else {
                String resID = "";
                for (Recipient recipient : mRecipientArrayList) {
                    if (recipient.getAddress1().equals(selectedUnitNumber)) {
                        resID = resID + "," + recipient.getRecipientId();
                    }
                }
                if (resID.startsWith(",")) {
                    resID = resID.substring(1);
                }
                Intent intent = new Intent(this, SelectYourPackageActivity.class);
                intent.putExtra(RESIDENT_ID, resID);
                startActivity(intent);
            }
        }
    }

    @OnClick({R.id.startoverBTN, R.id.imageButtonClear})
    void onClearRecipient() {
        clearRecipientName();
    }

    @OnClick(R.id.submitBTN)
    void onSubmitClicked() {
        if (NTF_Utils.isOnline(this)) {
            doPerformNext();
        } else {
            NTF_Utils.showNoNetworkAlert(this);
            registerReceiver(mybroadcast, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    @OnClick(R.id.closeerrorTV)
    void closeErrorMsg() {
        errormessageFL.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.backImage)
    void onExitKiosk() {
        NTF_Utils.hideKeyboard(this);
        KioskExitDialogFragment filterDialogFragment = new KioskExitDialogFragment();
        filterDialogFragment.setData(this, ntf_Preferences);
        filterDialogFragment.setActivity(this);
        filterDialogFragment.show(getSupportFragmentManager(), "Kiosk Logout Dialog Fragment");
    }


    public void doKioskLogout(boolean isLogout) {
        try {
            if (isLogout) {
                SingleTon.clearInstance();
                Intent intent = new Intent(this, MainActivity.class);
                String apiStatus = ntf_Preferences.get(NTF_Constants.Prefs_Keys.LOGIN_API_STATUS);
                if (apiStatus.equalsIgnoreCase(ResponseKeys.RESET_PASSWORD) || apiStatus.equalsIgnoreCase(ResponseKeys.RESET_USERNAME_PASSWORD)) {
                    intent.putExtra(NTF_Constants.Extras_Keys.LOGIN_API_STATUS, apiStatus);
                }
                startActivity(intent);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setRecipientSpinnerData() {
        try {
            autoCompleteTextView.setDropDownBackgroundDrawable(getResources().getDrawable(R.drawable.bg_edittext));
            JSONObject recipientJsonObj = NTF_Utils.getRecipientsData(this);
            if (recipientJsonObj != null) {
                mRecipientArrayList = Recipient.getRecipientsList(recipientJsonObj.getJSONArray("recipients"));
                Collections.sort(mRecipientArrayList, new Comparator<Recipient>() {
                    @Override
                    public int compare(Recipient lhs, Recipient rhs) {
                        return lhs.getFullName().compareToIgnoreCase(rhs.getFullName());
                    }
                });
                if (mRecipientArrayList != null && !mRecipientArrayList.isEmpty()) {
                    if (displayformat.length() == 0 || (displayformat.length() == 3 && !displayformat.startsWith("hh"))) {
                        autoCompleteCustomAdapter = new KioskAutoCompleteCustomAdapter(mRecipientArrayList, this, displayformat);
                        autoCompleteTextView.setAdapter(autoCompleteCustomAdapter);
                        autoCompleteCustomAdapter.setOnRecipientItemClickListener(new RecipientItemClickNoticeListener() {
                            @Override
                            public void onItemClicked(Recipient recipient) {
                                if (recipient.getFirstName().equalsIgnoreCase(KioskAutoCompleteCustomAdapter.noMatches)) {
                                    autoCompleteTextView.setText("");
                                } else {
                                    setRecipientName(recipient);
                                    buttonsLL.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    } else if (displayformat.length() == 3 && displayformat.startsWith("hh")) {
                        displayUnitList = new ArrayList<>();
                        for (Recipient recipient : mRecipientArrayList) {
                            if (recipient.getAddress1() != null && !recipient.getAddress1().isEmpty() && !recipient.getAddress1().equalsIgnoreCase("null")) {
                                if (!displayUnitList.contains(recipient.getAddress1())) {
                                    displayUnitList.add(recipient.getAddress1());
                                }
                            }
                        }
                        autoCompleteUnitAdapter = new KioskAutoCompleteUnitAdapter(displayUnitList, this, mRecipientArrayList);
                        autoCompleteTextView.setAdapter(autoCompleteUnitAdapter);
                        autoCompleteUnitAdapter.setOnRecipientItemClickListener(new UnitNumberClickListener() {
                            @Override
                            public void onItemClicked(String unitnumber) {
                                if (unitnumber.equalsIgnoreCase(KioskAutoCompleteUnitAdapter.noMatches)) {
                                    autoCompleteTextView.setText("");
                                } else {
                                    NTF_Utils.hideKeyboard(IdentifyUserActivity.this);
                                    selectedUnitNumber = unitnumber.trim();
                                    autoCompleteTextView.setText("");
                                    editTextRecipientNameFL.setVisibility(View.GONE);
                                    mLlRecipientName.setVisibility(View.VISIBLE);
                                    mTextViewAutoCompleteRecipient.setText(unitnumber);
                                    buttonsLL.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDropDownHeight(int count) {
        NTF_Utils.setDropDownHeight(count, autoCompleteTextView, this);
    }

    private void setRecipientName(Recipient recipient) {
        NTF_Utils.hideKeyboard(this);
        this.mRecipient = recipient;
        autoCompleteTextView.setText("");
        editTextRecipientNameFL.setVisibility(View.GONE);
        mLlRecipientName.setVisibility(View.VISIBLE);
        String name = "";
        //for firstname
        if (displayformat.startsWith("c")) {
            name = recipient.getFirstName();
        } else if (displayformat.startsWith("i") && recipient.getFirstName() != null
                && !recipient.getFirstName().equalsIgnoreCase("null") && recipient.getFirstName().length() > 0) {
            name = String.valueOf(recipient.getFirstName().charAt(0) + ". ");
        }
        //for last name
        if ((String.valueOf(displayformat.charAt(1))).equalsIgnoreCase("c")) {
            name = name + " " + recipient.getLastName();
        } else if ((String.valueOf(displayformat.charAt(1))).equalsIgnoreCase("i")
                && recipient.getLastName() != null && !recipient.getLastName().equalsIgnoreCase("null")
                && recipient.getLastName().length() > 0) {
            name = name + " " + String.valueOf(recipient.getLastName().charAt(0) + ". ");
        }
        //for unit number
        if (displayformat.endsWith("c") && recipient.getAddress1() != null && !recipient.getAddress1().equalsIgnoreCase("null")
                && !recipient.getAddress1().isEmpty()) {
            name = name + " -- " + recipient.getAddress1();
        }

        mTextViewAutoCompleteRecipient.setText(name);
        //mTextViewAutoCompleteRecipient.setText(recipient.getFirstName() + " " + recipient.getLastName() + " -- " + recipient.getAddress1());
    }

    private void clearRecipientName() {
        buttonsLL.setVisibility(View.GONE);
        mRecipient = null;
        mTextViewAutoCompleteRecipient.setText("");
        mLlRecipientName.setVisibility(View.GONE);
        editTextRecipientNameFL.setVisibility(View.VISIBLE);
        autoCompleteTextView.setText("");
        selectedUnitNumber = "";
    }

    public interface UnitNumberClickListener {
        void onItemClicked(String unitnumber);
    }

}
