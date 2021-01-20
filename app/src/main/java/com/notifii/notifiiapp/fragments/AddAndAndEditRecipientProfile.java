package com.notifii.notifiiapp.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.activities.MainActivity;
import com.notifii.notifiiapp.adapters.CalendarViewAdapter;
import com.notifii.notifiiapp.adapters.SpinnerHintAdapter;
import com.notifii.notifiiapp.adapters.SpinnerMarkAdapter;
import com.notifii.notifiiapp.asynctasks.RecipientJsonToModel;
import com.notifii.notifiiapp.base.NTF_BaseFragment;
import com.notifii.notifiiapp.models.Recipient;
import com.notifii.notifiiapp.models.SpinnerData;
import com.notifii.notifiiapp.mvp.models.AddRecipientProfileRequest;
import com.notifii.notifiiapp.mvp.models.AddRecipientProfileResponse;
import com.notifii.notifiiapp.mvp.models.EditRecipientProfileRequest;
import com.notifii.notifiiapp.mvp.models.EditRecipientProfileResponse;
import com.notifii.notifiiapp.mvp.presenters.AddandEditRecipientProfilePresenter;
import com.notifii.notifiiapp.mvp.views.AddAndEditRecipientView;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnItemClick;
import butterknife.OnItemSelected;

/*
 * This class deals with the Adding Recipient and Edit the Recipient Profile Details...
 *  mRecipient == null means add not null edit
 */
public class AddAndAndEditRecipientProfile extends NTF_BaseFragment implements AddAndEditRecipientView {

    @BindView(R.id.tv_spinner_type)
    TextView mTvSpinnerType;
    @BindView(R.id.tv_spinner_status)
    TextView mTvSpinnerStatus;
    @BindView(R.id.typeButtonDownArrow)
    ImageView mIvTypeArrow;
    @BindView(R.id.statusButtonDownArrow)
    ImageView mIvStatusArrow;
    @BindView(R.id.type_linear)
    LinearLayout mTypeLinear;
    @BindView(R.id.vacation_start_date_tv)
    TextView vacationStartDateTV;
    @BindView(R.id.vacation_end_date_tv)
    TextView vacationEndDateTV;
    @BindView(R.id.special_track_ins_etext)
    EditText specialTrackInstructionsET;
    @BindView(R.id.fl_spinner_send_pkg_login_notifii)
    FrameLayout sendPkgLoginNotifiiFrameL;
    @BindView(R.id.spinner_send_pkg_login_notifii)
    Spinner sendPkgLoginNotifiiSpinner;
    @BindView(R.id.tv_spinner_send_pkg_login_notifii)
    TextView sendPkgLoginNotifiiText;
    @BindView(R.id.sendPkgLoginNotifiiButtonDownArrow)
    ImageView sendPkgLoginNotifiiDropdown;
    @BindView(R.id.fl_spinner_send_pkg_logout_notifii)
    FrameLayout sendPkgLogoutNotifiiFrameL;
    @BindView(R.id.spinner_send_pkg_logout_notifii)
    Spinner sendPkgLogoutNotifiiSpinner;
    @BindView(R.id.tv_spinner_send_pkg_logout_notifii)
    TextView sendPkgLogoutNotifiiText;
    @BindView(R.id.sendPkgLogoutNotifiiButtonDownArrow)
    ImageView sendPkgLogoutNotifiiDropdown;
    @BindView(R.id.fl_specialtrackInsFlag)
    FrameLayout specialTrackInsFlagFrameL;
    @BindView(R.id.spinner_specialtrackInsFlag)
    Spinner specialTrackInsFlagSpinner;
    @BindView(R.id.tv_spinner_specialtrackInsFlag)
    TextView specialTrackInsFlagText;
    @BindView(R.id.specialtrackInsFlagDownArrow)
    ImageView specialTrackInsFlagDropdown;
    @BindView(R.id.showAdvOptionsLayout)
    LinearLayout showAdvOptionsLayout;
    @BindView(R.id.close_start_vacation)
    TextView vacationStartCloseImage;
    @BindView(R.id.close_end_vacation)
    TextView vacationEndCloseImage;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    @BindView(R.id.special_track_ins_layout)
    LinearLayout specialTrackInsLayout;
    @BindView(R.id.end_calendar_layout)
    LinearLayout vacationEndDateLayout;
    @BindView(R.id.start_calendar_layout)
    LinearLayout vacationStartDateLayout;
    @BindView(R.id.status_linear)
    LinearLayout statusLinear;
    @BindView(R.id.show_adv_options_text)
    TextView showAdvoptionsText;
    @BindView(R.id.edittext_firs_tname)
    EditText mEdittextFirsTname;
    @BindView(R.id.edittext_last_name)
    EditText mEdittextLastName;
    @BindView(R.id.textview_addr1_label)
    TextView mTextviewAddr1Label;
    @BindView(R.id.edittext_addr1)
    EditText mEdittextAddr1;
    @BindView(R.id.spinner_type)
    Spinner mSpinnerType;
    @BindView(R.id.spinner_status)
    Spinner mSpinnerStatus;
    @BindView(R.id.edittext_mobile_phone)
    EditText mEdittextMobilePhone;
    @BindView(R.id.edittext_email)
    EditText mEdittextEmail;
    @BindView(R.id.checkBoxNotifyEmail)
    ImageView mTrackNonMarkCheckBoxNotifyEmail;
    @BindView(R.id.checkBoxNotifyText)
    ImageView mTrackNonMarkCheckBoxNotifyText;
    @BindView(R.id.buttonSubmit)
    Button mButtonSubmit;
    @BindView(R.id.imageStarAddr1)
    View mViewAddr1Star;
    @BindView(R.id.address_layout)
    LinearLayout addressLayout;

    String mRecipientTypeValue = "";
    private String accTypeName;
    private String sendPkgLoginNotifiiSelected;
    private String sendPkgLogoutNotifiiSelected;
    private String specialtrackInsFlagSelected;
    private boolean advLayoutVisible;
    private Calendar mCurrentCalendar;
    private String mDay;
    private ArrayList<SpinnerData> sendPkgLoginNotifiiList = new ArrayList<>();
    private ArrayList<SpinnerData> sendPkgLogoutNotifiiList = new ArrayList<>();
    private ArrayList<SpinnerData> specialTrackInsFlagList = new ArrayList<>();
    private boolean startDateIsSelected;
    private boolean isFromEditScreen;
    private String selectedStatusValue;
    private Dialog mCalendarDialog;
    private int mButtonId;
    private ArrayList<String> yearList = new ArrayList<>();
    private String[] mYearList;
    private CalendarViewAdapter mCalendarAdapter;
    private Integer mMonth;
    private Integer mYear;
    private Calendar mPreviousCalendar;
    private Calendar mNextCalendar;
    private String mStartDate;
    private SimpleDateFormat date;
    private String mEndDate;
    private Calendar temp;
    private Handler handler;
    private com.notifii.notifiiapp.models.Recipient mRecipient;
    private ArrayList<SpinnerData> mStatusList = null;
    private ArrayList<SpinnerData> mTypeList = null;
    private boolean isAddr1Required = false;
    private JSONObject recipientJsonObject;
    private String title;
    private boolean isRecipientUpdated;
    AddandEditRecipientProfilePresenter addRecipientProfilePresenter;
    private boolean isEdit = false;
    private boolean isEmail, isFirstTime = true;
    private String phone_bounced, phone_bounce_alert, phone_bounce_reason, phone_type_error, phone_type_reason;
    private String[] mMonthList;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            // git test
            mRecipient = bundle.getParcelable(Extras_Keys.KEY_RECIPIENT);
        }
    }

    public static AddAndAndEditRecipientProfile getInstance(Recipient recipient) {
        AddAndAndEditRecipientProfile frgament = new AddAndAndEditRecipientProfile();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Extras_Keys.KEY_RECIPIENT, recipient);
        frgament.setArguments(bundle);
        return frgament;
    }

    public static AddAndAndEditRecipientProfile getInstance() {
        AddAndAndEditRecipientProfile frgament = new AddAndAndEditRecipientProfile();
        return frgament;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_add_and_edit_recipient_profile, container, false);
        ButterKnife.bind(this, mainView);
        return mainView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews();
        addRecipientProfilePresenter = new AddandEditRecipientProfilePresenter();
        addRecipientProfilePresenter.attachMvpView(this);
    }


    @Override
    public void onStackChanged() {
        super.onStackChanged();
        if (NTF_Utils.getCurrentFragment(this) != null && NTF_Utils.getCurrentFragment(this) instanceof AddAndAndEditRecipientProfile) {
            onFragmentPopup(true);
        } else {
            onFragmentPopup(false);
        }
    }

    @OnClick(R.id.checkBoxNotifyEmail)
    void onEmailCheckBoxClicked() {
        mTrackNonMarkCheckBoxNotifyEmail.setTag(mTrackNonMarkCheckBoxNotifyEmail.getTag().equals("0") ? "1" : "0");
        mTrackNonMarkCheckBoxNotifyEmail.setBackgroundResource(mTrackNonMarkCheckBoxNotifyEmail.getTag().equals("1") ? (R.drawable.ic_check_selected) : (R.drawable.ic_check_default));
    }

    @OnClick(R.id.checkBoxNotifyText)
    void onTextCheckBoxClicked() {
        mTrackNonMarkCheckBoxNotifyText.setTag(mTrackNonMarkCheckBoxNotifyText.getTag().equals("0") ? "1" : "0");
        mTrackNonMarkCheckBoxNotifyText.setBackgroundResource(mTrackNonMarkCheckBoxNotifyText.getTag().equals("1") ? (R.drawable.ic_check_selected) : (R.drawable.ic_check_default));
    }

    private void bindViews() {
        setToolbarActionButtons(true, false, 0);
        accTypeName = NTF_Utils.getRecipientTypeLabel(ntf_Preferences.get(Prefs_Keys.ACCOUNT_TYPE));
        if (mRecipient != null) {
            isFromEditScreen = true;
        }
        date = new SimpleDateFormat("yyyy-MM-dd");
        setToolbarTitle(mRecipient == null ? "Add New " + accTypeName : "Edit " + accTypeName + " Profile");
        title = mRecipient == null ? "Add New " + accTypeName : "Edit " + accTypeName + " Profile";
        mCurrentCalendar = Calendar.getInstance();
        mPreviousCalendar = Calendar.getInstance();
        mNextCalendar = Calendar.getInstance();
        showAdvOptionsLayout.setVisibility(View.GONE);
        sendPkgLoginNotifiiList = SpinnerData.getSendPkgLoginNotifiiSpinnerData(false);
        sendPkgLogoutNotifiiList = SpinnerData.getSendPkgLoginNotifiiSpinnerData(false);
        specialTrackInsFlagList = SpinnerData.getSpecialTrackInsFlagSpinnerData(false);

        if (vacationStartDateTV.getText().toString().isEmpty()) {
            vacationStartCloseImage.setVisibility(View.INVISIBLE);
        } else {
            vacationStartCloseImage.setVisibility(View.VISIBLE);
        }
        if (vacationEndDateTV.getText().toString().isEmpty()) {
            vacationEndCloseImage.setVisibility(View.INVISIBLE);
        } else {
            vacationEndCloseImage.setVisibility(View.VISIBLE);
        }
        if (title.equalsIgnoreCase("Add New " + accTypeName)) {
            mButtonSubmit.setText("Add " + accTypeName);
            mIvStatusArrow.setVisibility(View.GONE);
            statusLinear.setVisibility(View.GONE);
        } else {
            mButtonSubmit.setText(getString(R.string.save));
            mIvStatusArrow.setVisibility(View.VISIBLE);
            statusLinear.setVisibility(View.VISIBLE);
        }
        setREcipientStatuesSipinnerData();
        setRecipientTypeSpinnerData();
        bindToViews();
        String accType = ntf_Preferences.get(Prefs_Keys.ACCOUNT_TYPE);
        mTextviewAddr1Label.setText(NTF_Utils.getRecipientAddress1Label(accType));

        if (mEdittextAddr1 != null) {
            mEdittextAddr1.setContentDescription(NTF_Utils.getRecipientAddress1Label(accType));
        }
        if (accType.toLowerCase().equalsIgnoreCase("apt") || accType.toLowerCase().equalsIgnoreCase("mpc")) {
            isAddr1Required = true;
            mViewAddr1Star.setVisibility(View.VISIBLE);
            addressLayout.setContentDescription(NTF_Utils.getRecipientAddress1Label(accType) + " star");
        } else {
            isAddr1Required = false;
            mViewAddr1Star.setVisibility(View.GONE);
            addressLayout.setContentDescription(NTF_Utils.getRecipientAddress1Label(accType));
        }
        if (accType.toLowerCase().equalsIgnoreCase("edu")) {
            mTypeLinear.setVisibility(View.VISIBLE);
        } else {
            mTypeLinear.setVisibility(View.GONE);
        }
        String send_opt_in_sms = ntf_Preferences.get(Prefs_Keys.REQUIRE_EXPLICIT_SMS_OPT_IN);
        if (send_opt_in_sms != null && send_opt_in_sms.equals("1")) {
            mTrackNonMarkCheckBoxNotifyText.setEnabled(false);
            mTrackNonMarkCheckBoxNotifyText.setAlpha(0.7f);
        }
    }

    // Binding the values to the Views
    private void bindToViews() {
        if (mRecipient != null) {
            mEdittextFirsTname.setText((mRecipient.getFirstName() != null && !mRecipient.getFirstName().equalsIgnoreCase("null") ) ? mRecipient.getFirstName() : "");
            mEdittextLastName.setText((mRecipient.getLastName() != null && !mRecipient.getLastName().equalsIgnoreCase("null") ) ? mRecipient.getLastName() : "");
            mEdittextAddr1.setText((mRecipient.getAddress1() != null && !mRecipient.getAddress1().equalsIgnoreCase("null") ) ? mRecipient.getAddress1() : "");
            mEdittextEmail.setText((mRecipient.getEmail() != null && !mRecipient.getEmail().equalsIgnoreCase("null") )? mRecipient.getEmail() : "");
            mEdittextMobilePhone.setText((mRecipient.getCellphone() != null && !mRecipient.getCellphone().equalsIgnoreCase("null") )? mRecipient.getCellphone() : "");
            if (getEmailCheck()) {
                onEmailCheckBoxClicked();
            }
            if (getTextCheck()) {
                onTextCheckBoxClicked();
            }
            phone_bounced = mRecipient.getPhone_bounced();
            phone_bounce_alert = mRecipient.getPhone_bounce_alert();
            phone_bounce_reason = mRecipient.getPhone_bounce_reason();
            phone_type_error = mRecipient.getPhone_type_error();
            phone_type_reason = mRecipient.getPhone_type_reason();

            if (mRecipient.getSpecial_track_instructions() != null && !mRecipient.getSpecial_track_instructions().isEmpty() && !mRecipient.getSpecial_track_instructions().equalsIgnoreCase("null")) {
                specialTrackInstructionsET.setText(mRecipient.getSpecial_track_instructions());
            }

            if (mRecipient.getVacation_start_date() != null && !mRecipient.getVacation_start_date().isEmpty() && !mRecipient.getVacation_start_date().equalsIgnoreCase("null")) {
                vacationStartDateTV.setText(mRecipient.getVacation_start_date());
                if (vacationStartDateTV.getText().toString().isEmpty()) {
                    vacationStartCloseImage.setVisibility(View.INVISIBLE);
                } else {
                    vacationStartCloseImage.setVisibility(View.VISIBLE);
                }
            }
            if (mRecipient.getVacation_end_date() != null && !mRecipient.getVacation_end_date().isEmpty() && !mRecipient.getVacation_end_date().equalsIgnoreCase("null")) {
                vacationEndDateTV.setText(mRecipient.getVacation_end_date());
                if (vacationEndDateTV.getText().toString().isEmpty()) {
                    vacationEndCloseImage.setVisibility(View.INVISIBLE);
                } else {
                    vacationEndCloseImage.setVisibility(View.VISIBLE);
                }
            }

            try {
                // Setting the Recipient type spinner value..
                if (mTypeList != null && !mTypeList.isEmpty()) {
                    if (mTypeList.size() == 1) {
                        mIvTypeArrow.setVisibility(View.GONE);
                    } else {
                        mIvTypeArrow.setVisibility(View.VISIBLE);
                    }
                    String value = mRecipient.getRecipient_type_value();
                    mRecipientTypeValue = mRecipient.getRecipient_type_value();
                    if (value != null && !value.equals("")) {
                        for (int i = 0, len = mTypeList.size(); i < len; i++) {
                            if (value.equalsIgnoreCase(mTypeList.get(i).getValue())) {
                                mSpinnerType.setSelection(i + 1);
                                mTvSpinnerType.setText(mTypeList.get(i).getName());
                                mTypeList.get(i).setSelected(true);
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                // Setting the Recipient status spinner value..
                if (mStatusList != null && !mStatusList.isEmpty()) {
                    String value = mRecipient.getRecipient_status_value();
                    if (value != null && !value.equals("")) {
                        for (int i = 0, len = mStatusList.size(); i < len; i++) {
                            if (value.equalsIgnoreCase(mStatusList.get(i).getValue())) {
                                mSpinnerStatus.setSelection(i);
                                mTvSpinnerStatus.setText(mStatusList.get(i).getName());
                                String selectedStatusName = mStatusList.get(i).getName();
                                selectedStatusValue = mStatusList.get(i).getValue();
                                mStatusList.get(i).setSelected(true);
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sendPackageLoginSpinnerData();
        sendPackageLogoutSpinnerData();
        setSpecialTrackInstructionsFlagSpinnerData();
    }

    private boolean getEmailCheck() {
        if ((mRecipient.getSend_track_nonmarketing_email() != null && mRecipient.getSend_track_nonmarketing_email().equals("1")) && (mRecipient.getStop_connect_nonmarketing_email() != null && mRecipient.getStop_track_nonmarketing_email().equals("0"))) {
            return true;
        }
        return false;
    }

    private boolean getTextCheck() {
        if ((mRecipient.getSend_track_nonmarketing_text() != null && mRecipient.getSend_track_nonmarketing_text().equals("1")) && (mRecipient.getStop_connect_nonmarketing_text() != null && mRecipient.getStop_track_nonmarketing_text().equals("0"))) {
            return true;
        }
        return false;
    }

    private void sendPackageLoginSpinnerData() {
        try {
            if (sendPkgLoginNotifiiList != null && !sendPkgLoginNotifiiList.isEmpty()) {
                if (sendPkgLoginNotifiiList.size() >= 10) {
                    NTF_Utils.setSpinnerHeight(getActivity(), sendPkgLoginNotifiiSpinner);
                }
                // final SpinnerAdapter sendPkgLoginNotifiiAdapter = new SpinnerAdapter(getActivity(), R.layout.spinner_item, sendPkgLoginNotifiiList);
                SpinnerMarkAdapter sendPkgLoginNotifiiAdapter = new SpinnerMarkAdapter(getActivity(), sendPkgLoginNotifiiList);
                sendPkgLoginNotifiiSpinner.setAdapter(sendPkgLoginNotifiiAdapter);
                sendPkgLoginNotifiiSpinner.setSelection(0);
                SpinnerData.resetData(sendPkgLoginNotifiiList);
                sendPkgLoginNotifiiList.get(0).setSelected(true);
                sendPkgLoginNotifiiSpinner.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        sendPkgLoginNotifiiSpinner.performClick();
                        NTF_Utils.hideKeyboard(getActivity());
                        return false;
                    }
                });

                if (mRecipient != null) {
                    String value = mRecipient.getSend_pkg_login_notification();
                    if (value != null && !value.equals("")) {
                        for (int i = 0, len = sendPkgLoginNotifiiList.size(); i < len; i++) {
                            if (value.equalsIgnoreCase(sendPkgLoginNotifiiList.get(i).getValue())) {
                                sendPkgLoginNotifiiSpinner.setSelection(i);
                                sendPkgLoginNotifiiSelected = sendPkgLoginNotifiiList.get(i).getValue();
                                sendPkgLoginNotifiiText.setText(sendPkgLoginNotifiiList.get(i).getName());
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sendPackageLogoutSpinnerData() {
        try {
            if (sendPkgLogoutNotifiiList != null && !sendPkgLogoutNotifiiList.isEmpty()) {
                if (sendPkgLogoutNotifiiList.size() >= 10) {
                    NTF_Utils.setSpinnerHeight(getActivity(), sendPkgLogoutNotifiiSpinner);
                }
                SpinnerMarkAdapter sendPkgLogoutNotifiiAdapter = new SpinnerMarkAdapter(getActivity(), sendPkgLogoutNotifiiList);
                sendPkgLogoutNotifiiSpinner.setAdapter(sendPkgLogoutNotifiiAdapter);
                sendPkgLogoutNotifiiSpinner.setSelection(0);
                SpinnerData.resetData(sendPkgLogoutNotifiiList);
                sendPkgLogoutNotifiiList.get(0).setSelected(true);
                sendPkgLogoutNotifiiSpinner.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        sendPkgLogoutNotifiiSpinner.performClick();
                        NTF_Utils.hideKeyboard(getActivity());
                        return false;
                    }
                });

                if (mRecipient != null) {
                    String value = mRecipient.getSend_pkg_logout_notification();
                    if (value != null && !value.equals("")) {
                        for (int i = 0, len = sendPkgLogoutNotifiiList.size(); i < len; i++) {
                            if (value.equalsIgnoreCase(sendPkgLogoutNotifiiList.get(i).getValue())) {
                                sendPkgLogoutNotifiiSpinner.setSelection(i);
                                sendPkgLogoutNotifiiSelected = sendPkgLogoutNotifiiList.get(i).getValue();
                                sendPkgLogoutNotifiiText.setText(sendPkgLogoutNotifiiList.get(i).getName());
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setSpecialTrackInstructionsFlagSpinnerData() {
        try {
            if (specialTrackInsFlagList != null && !specialTrackInsFlagList.isEmpty()) {
                if (specialTrackInsFlagList.size() >= 10) {
                    NTF_Utils.setSpinnerHeight(getActivity(), specialTrackInsFlagSpinner);
                }
                SpinnerMarkAdapter specialTrackInsFlagAdapter = new SpinnerMarkAdapter(getActivity(), specialTrackInsFlagList);
                specialTrackInsFlagSpinner.setAdapter(specialTrackInsFlagAdapter);
                specialTrackInsFlagSpinner.setSelection(0);
                SpinnerData.resetData(specialTrackInsFlagList);
                specialTrackInsFlagList.get(0).setSelected(true);
                specialTrackInsFlagSpinner.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        specialTrackInsFlagSpinner.performClick();
                        NTF_Utils.hideKeyboard(getActivity());
                        return false;
                    }
                });

                if (mRecipient != null) {
                    String value = mRecipient.getSpecial_track_instructions_flag();
                    if (value != null && !value.equals("")) {
                        for (int i = 0, len = specialTrackInsFlagList.size(); i < len; i++) {
                            if (value.equalsIgnoreCase(specialTrackInsFlagList.get(i).getValue())) {
                                specialTrackInsFlagSpinner.setSelection(i);
                                specialtrackInsFlagSelected = specialTrackInsFlagList.get(i).getValue();
                                specialTrackInsFlagText.setText(specialTrackInsFlagList.get(i).getName());
                                if (specialTrackInsFlagText.getText().toString().equalsIgnoreCase("none")) {
                                    specialTrackInsLayout.setVisibility(View.GONE);
                                } else {
                                    specialTrackInsLayout.setVisibility(View.VISIBLE);
                                }
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Getting the Recipient type List from Response and binding the data to the spinner
    private void setRecipientTypeSpinnerData() {
        try {
            JSONObject jsonObject = NTF_Utils.getGlobalData(getActivity());
            mTypeList = SpinnerData.getList(jsonObject.getJSONArray("recipient_types"), null).getList();
            SpinnerMarkAdapter typeAdapter = new SpinnerMarkAdapter(getActivity(), mTypeList);
            mSpinnerType.setAdapter(new SpinnerHintAdapter(typeAdapter, R.layout.spinner_hint, getActivity()));
            if (mRecipient == null && mTypeList.size() == 1) {
                mSpinnerType.setSelection(1);
                mIvTypeArrow.setVisibility(View.GONE);
                mTvSpinnerType.setText(mTypeList.get(0).getName());
                mRecipientTypeValue = mTypeList.get(0).getValue();
            } else {
                mIvTypeArrow.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Getting the Recipient status List from Response and binding the data to the spinner
    private void setREcipientStatuesSipinnerData() {
        try {
            mStatusList = NTF_Utils.getStatusList();
            if (mStatusList != null && !mStatusList.isEmpty()) {
                mTvSpinnerStatus.setText(mStatusList.get(0).getName());
                selectedStatusValue = mStatusList.get(0).getValue();
            }
            SpinnerMarkAdapter typeAdapter = new SpinnerMarkAdapter(getActivity(), mStatusList);
            mSpinnerStatus.setAdapter(typeAdapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null) {
            if ((getActivity()).isFinishing()) {
                NTF_Utils.hideProgressDialog();
                NTF_Utils.hideKeyboard(getActivity());
            }
        }
    }


    @Override
    public void onEmptyFirstName() {
        NTF_Utils.showAlert(getActivity(), "", "Please enter your first name.", null);
    }

    @Override
    public void onSelectSpinner() {
        NTF_Utils.showAlert(getActivity(), "", "Please select profile type.", null);
    }

    @Override
    public void onEmptySpecialTrackInstructions() {
        NTF_Utils.showAlert(getActivity(), "", "Please enter special track instructions.", null);
    }

    @Override
    public void onEmpyLastName() {
        NTF_Utils.showAlert(getActivity(), "", "Please enter your last name.", null);
    }

    @Override
    public void onEmptyAddress1() {
        NTF_Utils.showAlert(getActivity(), "", "Please enter your " + NTF_Utils.getRecipientAddress1Label(ntf_Preferences.get(NTF_Constants.Prefs_Keys.ACCOUNT_TYPE)).toLowerCase() + ".", null);

    }

    @Override
    public void onCheckEmail() {
        NTF_Utils.showAlert(getActivity(), "", "Your email address is not valid", null);
    }

    @Override
    public void onCheckVacationStartDate() {
        NTF_Utils.showAlert(getActivity(), "", "Please select vacation start date.", null);
    }

    @Override
    public void onCheckVacationEndDate() {
        NTF_Utils.showAlert(getActivity(), "", "Please select vacation end date.", null);
    }

    @Override
    public void onCheckVacationinvalid() {
        NTF_Utils.showAlert(getActivity(), "", "Please select valid vacation end date.", null);
    }

    @Override
    public void onRecipientDetailsGiven() {
        if (mRecipient == null) {
            isEdit = false;
            addRecipient();
        } else {
            isEdit = true;
            updateRecipient();
        }
    }

    // Preparing the Service request of Add recipient
    private void addRecipient() {
        if (NTF_Utils.isOnline(getActivity())) {
            String specialTrackInsText;
            String sendPkgLoginNotifiiText;
            String sendPkgLogoutNotifiiText;

            if (specialtrackInsFlagSelected == null) {
                specialTrackInsText = specialTrackInsFlagList.get(0).getValue();
            } else if (specialtrackInsFlagSelected.equalsIgnoreCase("none") || specialtrackInsFlagSelected.isEmpty()) {
                specialTrackInstructionsET.setText("");
                specialTrackInsText = specialTrackInsFlagList.get(0).getValue();
            } else {
                specialTrackInsText = specialtrackInsFlagSelected;
            }

            if (sendPkgLoginNotifiiSelected == null) {
                sendPkgLoginNotifiiText = sendPkgLoginNotifiiList.get(0).getValue();
            } else if (sendPkgLoginNotifiiSelected.equalsIgnoreCase("none") || sendPkgLoginNotifiiSelected.isEmpty()) {
                sendPkgLoginNotifiiText = sendPkgLoginNotifiiList.get(0).getValue();
            } else {
                sendPkgLoginNotifiiText = sendPkgLoginNotifiiSelected;
            }

            if (sendPkgLogoutNotifiiSelected == null) {
                sendPkgLogoutNotifiiText = sendPkgLogoutNotifiiList.get(0).getValue();
            } else if (sendPkgLogoutNotifiiSelected.equalsIgnoreCase("none") || sendPkgLogoutNotifiiSelected.isEmpty()) {
                sendPkgLogoutNotifiiText = sendPkgLogoutNotifiiList.get(0).getValue();
            } else {
                sendPkgLogoutNotifiiText = sendPkgLogoutNotifiiSelected;
            }
            if (mRecipient != null) {
                mRecipient.setSpecial_track_instructions_flag(specialTrackInsText);
                mRecipient.setSend_pkg_logout_notification(sendPkgLogoutNotifiiText);
                mRecipient.setSend_pkg_login_notification(sendPkgLoginNotifiiText);

            }

            AddRecipientProfileRequest addRecipientProfileRequest = addRecipientProfilePresenter.getAddRecipientRequest(
                    ntf_Preferences.get(Prefs_Keys.SESSION_ID),
                    ntf_Preferences.get(Prefs_Keys.AUTHENTICATION_TOKEN),
                    ntf_Preferences.get(Prefs_Keys.ACCOUNT_ID),
                    mEdittextFirsTname.getText().toString().trim(),
                    mEdittextLastName.getText().toString().trim(),
                    mEdittextAddr1.getText().toString().trim(),
                    mEdittextEmail.getText().toString().trim(),
                    mEdittextMobilePhone.getText().toString().trim(),
                    String.valueOf(mTrackNonMarkCheckBoxNotifyEmail.getTag()),
                    String.valueOf(mTrackNonMarkCheckBoxNotifyText.getTag()),
                    mTvSpinnerStatus.getText().toString(),
                    mRecipientTypeValue,
                    specialTrackInsText,
                    sendPkgLoginNotifiiText,
                    sendPkgLogoutNotifiiText,
                    vacationStartDateTV.getText().toString().trim(),
                    vacationEndDateTV.getText().toString().trim(),
                    specialTrackInstructionsET.getText().toString().trim(),  ntf_Preferences.get(Prefs_Keys.USER_ID));

            NTF_Utils.showProgressDialog(getActivity());
            addRecipientProfilePresenter.addingAllRecipientDetails(null, addRecipientProfileRequest, isEdit);
        } else {
            NTF_Utils.showNoNetworkAlert(getActivity());
            ((MainActivity) getActivity()).registerReceiver();
        }
    }

    @Override
    public void onAddingSuccess(AddRecipientProfileResponse response) {
        try {
            NTF_Utils.hideProgressDialog();
            ntf_Preferences.save(Prefs_Keys.RECIPIENT_ADDED_OR_UPDATED, true);
            new BackGroundTask(response.getRecipientId()).execute();
            NTF_Utils.showAlert(getActivity(), ALERT_SUCCESS_TITLE, response.getApiMessage(), new Runnable() {
                @Override
                public void run() {
                    getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.addToBackStack(null);
                    transaction.add(R.id.realtabcontent, RecipientProfileViewFragment.getInstance(response.getRecipientId()));
                    transaction.commitAllowingStateLoss();
                    getActivity().getSupportFragmentManager().executePendingTransactions();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (getActivity() != null) {
                                getActivity().onBackPressed();
                            }
                        }
                    }, 500);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Preparing the Service request of UpdateRecipient...
    private void updateRecipient() {
        if (NTF_Utils.isOnline(getActivity())) {
            String specialTrackInsFlagText;
            String sendPkgLoginNotifiiText;
            String sendPkgLogoutNotifiiText;
            String specialTrackInsText;

            if (specialtrackInsFlagSelected == null) {
                specialTrackInsFlagText = specialTrackInsFlagList.get(0).getValue();
            } else if (specialtrackInsFlagSelected.equalsIgnoreCase("none") || specialtrackInsFlagSelected.isEmpty()) {
                specialTrackInstructionsET.setText("");
                specialTrackInsFlagText = specialTrackInsFlagList.get(0).getValue();
            } else {
                specialTrackInsFlagText = specialtrackInsFlagSelected;
            }
            mRecipient.setSpecial_track_instructions_flag(specialTrackInsFlagText);

            if (specialTrackInstructionsET != null && specialtrackInsFlagSelected != null && !specialtrackInsFlagSelected.equalsIgnoreCase("null") && !specialtrackInsFlagSelected.equalsIgnoreCase("none")) {
                specialTrackInsText = specialTrackInstructionsET.getText().toString().trim();
            } else {
                specialTrackInsText = mRecipient.getSpecial_track_instructions();
            }


            if (sendPkgLoginNotifiiSelected == null) {
                sendPkgLoginNotifiiText = sendPkgLoginNotifiiList.get(0).getValue();
            } else if (sendPkgLoginNotifiiSelected.equalsIgnoreCase("none") || sendPkgLoginNotifiiSelected.isEmpty()) {
                sendPkgLoginNotifiiText = sendPkgLoginNotifiiList.get(0).getValue();
            } else {
                sendPkgLoginNotifiiText = sendPkgLoginNotifiiSelected;
            }
            mRecipient.setSend_pkg_login_notification(sendPkgLoginNotifiiText);

            if (sendPkgLogoutNotifiiSelected == null) {
                sendPkgLogoutNotifiiText = sendPkgLogoutNotifiiList.get(0).getValue();
            } else if (sendPkgLogoutNotifiiSelected.equalsIgnoreCase("none") || sendPkgLogoutNotifiiSelected.isEmpty()) {
                sendPkgLogoutNotifiiText = sendPkgLogoutNotifiiList.get(0).getValue();
            } else {
                sendPkgLogoutNotifiiText = sendPkgLogoutNotifiiSelected;
            }
            mRecipient.setSend_pkg_logout_notification(sendPkgLogoutNotifiiText);

            if (mRecipient.getLease_start_date() == null || mRecipient.getLease_start_date().equalsIgnoreCase("null")) {
                mRecipient.setLease_start_date("");
            }
            if (mRecipient.getLease_end_date() == null || mRecipient.getLease_end_date().equalsIgnoreCase("null")) {
                mRecipient.setLease_end_date("");
            }
            if (mRecipient.getBirth_date() == null || mRecipient.getBirth_date().equalsIgnoreCase("null")) {
                mRecipient.setBirth_date("");
            }
            if (mRecipient.getMove_in_date() == null || mRecipient.getMove_in_date().equalsIgnoreCase("null")) {
                mRecipient.setMove_in_date("");
            }
            if (mRecipient.getMove_out_date() == null || mRecipient.getMove_out_date().equalsIgnoreCase("null")) {
                mRecipient.setMove_out_date("");
            }
            String vacationStatus;
            if (!vacationStartDateTV.getText().toString().isEmpty() && !vacationEndDateTV.getText().toString().isEmpty()) {
                vacationStatus = "1";
                mRecipient.setVacation_status(vacationStatus);
            } else {
                vacationStatus = "0";
            }

            EditRecipientProfileRequest editRecipientProfileRequest = addRecipientProfilePresenter.getUpdateRecipientRequest(
                    ntf_Preferences.get(Prefs_Keys.SESSION_ID),
                    ntf_Preferences.get(Prefs_Keys.AUTHENTICATION_TOKEN),
                    ntf_Preferences.get(Prefs_Keys.ACCOUNT_ID),
                    mRecipient.getRecipientId(),
                    mEdittextFirsTname.getText().toString().trim(),
                    mEdittextLastName.getText().toString().trim(),
                    mEdittextAddr1.getText().toString().trim(),
                    mEdittextEmail.getText().toString().trim(),
                    mEdittextMobilePhone.getText().toString().trim(),
                    String.valueOf(mTrackNonMarkCheckBoxNotifyEmail.getTag()),
                    String.valueOf(mTrackNonMarkCheckBoxNotifyText.getTag()),
                    mRecipient.getSend_track_nonmarketing_push(),
                    mRecipient.getSend_connect_nonmarketing_email(),
                    mRecipient.getSend_connect_nonmarketing_text(),
                    mRecipient.getSend_connect_nonmarketing_push(),
                    mRecipient.getSend_connect_marketing_email(),
                    mRecipient.getSend_connect_marketing_text(),
                    mRecipient.getSend_connect_marketing_push(),
                    //mTrackNonMarkCheckBoxOptOutEmail.isChecked() ? "1" : "0",
                    //mTrackNonMarkCheckBoxOptOutMessage.isChecked() ? "1" : "0",
                    mRecipient.getStop_track_nonmarketing_email(),
                    mRecipient.getStop_track_nonmarketing_text(),
                    mRecipient.getStop_track_nonmarketing_push(),
                    mRecipient.getStop_connect_nonmarketing_email(),
                    mRecipient.getStop_connect_nonmarketing_text(),
                    mRecipient.getStop_connect_nonmarketing_push(),
                    mRecipient.getStop_connect_marketing_email(),
                    mRecipient.getStop_connect_marketing_text(),
                    mRecipient.getStop_connect_marketing_push(),
                    selectedStatusValue,
                    mRecipientTypeValue,// mSpinnerType.getSelectedItem() != null ? ((SpinnerData) mSpinnerType.getSelectedItem()).getValue() : mRecipientTypeValue,
                    specialTrackInsText,
                    specialTrackInsFlagText,
                    vacationStartDateTV.getText().toString().trim(),
                    vacationEndDateTV.getText().toString().trim(),
                    vacationStatus,
                    mRecipient.getLease_start_date(),
                    mRecipient.getLease_end_date(),
                    mRecipient.getBirth_date(),
                    mRecipient.getMove_in_date(),
                    mRecipient.getMove_out_date(),
                    sendPkgLoginNotifiiText,
                    sendPkgLogoutNotifiiText,
                    ntf_Preferences.get(Prefs_Keys.USER_ID),
                    mRecipient.getRecipientTitle()
            );
            NTF_Utils.showProgressDialog(getActivity());
            //  String header = NTF_Utils.getHeader(ntf_Preferences, getActivity());
            // Service request of UpdateRecipient...
            addRecipientProfilePresenter.editingAllRecipientDetails(null, editRecipientProfileRequest);
        } else {
            NTF_Utils.showNoNetworkAlert(getActivity());
            ((MainActivity) getActivity()).registerReceiver();
        }

    }


    @Override
    public void onEditingSuccess(EditRecipientProfileResponse response) {
        try {
            NTF_Utils.hideProgressDialog();

            isRecipientUpdated = true;
            ntf_Preferences.save(Prefs_Keys.RECIPIENT_ADDED_OR_UPDATED, true);
            new UpdateRecipient(mRecipient.getRecipientId()).execute();
            NTF_Utils.showAlert(getActivity(), ALERT_SUCCESS_TITLE, response.getApiMessage(), new Runnable() {
                @Override
                public void run() {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (getActivity() != null) {
                                getActivity().onBackPressed();
                            }
                        }
                    }, 500);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Saving the Recipient data after successfully adding of recipient
    class BackGroundTask extends AsyncTask<Void, Void, ArrayList<Recipient>> {

        private ArrayList<Recipient> List;
        private String recipientId;

        public BackGroundTask(String recipient_id) {
            recipientId = recipient_id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            NTF_Utils.showProgressDialog(getActivity());
        }

        @Override
        protected ArrayList<Recipient> doInBackground(Void... params) {
            JSONObject jsonObject = NTF_Utils.getRecipientsData(getActivity());
            try {
                JSONArray jsonArray = jsonObject.optJSONArray("recipients");
                JSONObject jsonObject1 = new JSONObject();

                jsonObject1.put("recipient_id", recipientId);
                jsonObject1.put("first_name", mEdittextFirsTname.getText().toString().trim());
                jsonObject1.put("last_name", mEdittextLastName.getText().toString().trim());
                jsonObject1.put("address1", mEdittextAddr1.getText().toString().trim());
                jsonObject1.put("full_name", mEdittextFirsTname.getText().toString().trim() + " " + mEdittextLastName.getText().toString().trim());
                jsonArray.put(jsonObject1);
                jsonObject.put("recipients", jsonArray);
                NTF_Utils.saveRecipientsData(getActivity(), jsonObject);
                return List;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Recipient> recipients) {
            super.onPostExecute(recipients);
            try {
                NTF_Utils.hideProgressDialog();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Saving the Recipient data after successfully updating of recipient
    class UpdateRecipient extends AsyncTask<Void, Void, ArrayList<Recipient>> {

        private ArrayList<Recipient> List;
        private String recipientId;

        public UpdateRecipient(String recipient_id) {
            recipientId = recipient_id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            NTF_Utils.showProgressDialog(getActivity());
        }

        @Override
        protected ArrayList<Recipient> doInBackground(Void... params) {
            JSONObject jsonObject = NTF_Utils.getRecipientsData(getActivity());
            try {
                JSONArray jsonArray = jsonObject.optJSONArray("recipients");
                for (int i=0;i<jsonArray.length();i++){
                    if (jsonArray.getJSONObject(i).optString("recipient_id").equals(recipientId)){
                        jsonArray.getJSONObject(i).put("first_name", mEdittextFirsTname.getText().toString().trim());
                        jsonArray.getJSONObject(i).put("last_name", mEdittextLastName.getText().toString().trim());
                        jsonArray.getJSONObject(i).put("address1", mEdittextAddr1.getText().toString().trim());
                        jsonArray.getJSONObject(i).put("full_name", mEdittextFirsTname.getText().toString().trim() + " " + mEdittextLastName.getText().toString().trim());
                        break;
                    }
                }


                NTF_Utils.saveRecipientsData(getActivity(), jsonObject);
                return List;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Recipient> recipients) {
            super.onPostExecute(recipients);
            try {
                NTF_Utils.hideProgressDialog();

            } catch (Exception e) {
                e.printStackTrace();
            }
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
    public void onErrorCode(String s) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(getActivity(), "", s, null);
    }

    @OnFocusChange({R.id.edittext_mobile_phone, R.id.edittext_email})
    void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {     // gained focus
            scrollView.scrollTo(0, 250);
        }
    }

    @OnClick(R.id.buttonSubmit)
    void onCickSubmitButton(View v) {
        // Log.v("isFieldsValid: ", "" + isFiledsValid());
        if (!NTF_Utils.isOnline(getActivity())) {
            NTF_Utils.showNoNetworkAlert(getActivity());
            return;
        }
        addRecipientProfilePresenter.doCheckRecipientDetails(mEdittextFirsTname, mEdittextLastName, mEdittextAddr1, mEdittextEmail, mTypeLinear, mSpinnerType, specialTrackInstructionsET, vacationStartDateTV, vacationEndDateTV, specialtrackInsFlagSelected, isAddr1Required);

    }


    @OnClick({R.id.tv_spinner_status, R.id.statusButtonDownArrow})
    void onClickView(View view) {
        if (!title.equalsIgnoreCase("Add New " + accTypeName)) {
            mSpinnerStatus.performClick();
        }
    }

    @OnClick(R.id.show_adv_options_text)
    void onClickShowAdvOptions(View view) {
        if (advLayoutVisible) {
            advLayoutVisible = false;
            showAdvoptionsText.setContentDescription("Expand Advanced Options Button");
            showAdvOptionsLayout.setVisibility(View.GONE);
        } else {
            advLayoutVisible = true;
            showAdvoptionsText.setContentDescription("Collapse Advanced Options Button");
            showAdvOptionsLayout.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.tv_spinner_type, R.id.typeButtonDownArrow})
    void onClickedBoth(View view) {
        NTF_Utils.hideKeyboard(getActivity());
        mSpinnerType.performClick();
    }

    @OnClick({R.id.backImage, R.id.mobile_left_linear})
    void onClickedBackImageMobileLeft(View view) {
        NTF_Utils.hideKeyboard(getActivity());
        if (!title.equalsIgnoreCase("Add New " + accTypeName)) {
            MainActivity.isRecipietListToLoad = true;
        }
        getActivity().onBackPressed();
    }

    @OnClick({R.id.tv_spinner_send_pkg_login_notifii, R.id.sendPkgLoginNotifiiButtonDownArrow, R.id.fl_spinner_send_pkg_login_notifii})
    void onClickedMultiple(View view) {
        NTF_Utils.hideKeyboard(getActivity());
        sendPkgLoginNotifiiSpinner.performClick();
    }

    @OnClick({R.id.tv_spinner_send_pkg_logout_notifii, R.id.sendPkgLogoutNotifiiButtonDownArrow, R.id.fl_spinner_send_pkg_logout_notifii})
    void onClickLogout(View view) {
        NTF_Utils.hideKeyboard(getActivity());
        sendPkgLogoutNotifiiSpinner.performClick();
    }

    @OnClick({R.id.fl_specialtrackInsFlag, R.id.tv_spinner_specialtrackInsFlag, R.id.specialtrackInsFlagDownArrow})
    void onClickSpecialTrack(View view) {
        NTF_Utils.hideKeyboard(getActivity());
        specialTrackInsFlagSpinner.performClick();
    }

    @OnClick({R.id.vacation_start_date_tv, R.id.start_calendar_layout})
    void onClickVacationStartDate(View view) {
        mButtonId = 1;
        showCalendarDialog();
    }

    @OnClick({R.id.vacation_end_date_tv, R.id.end_calendar_layout})
    void onClickVacationEndDate(View view) {
        mButtonId = 2;
        showCalendarDialog();
    }

    @OnClick(R.id.close_start_vacation)
    void onClickCloseStartVacation(View view) {
        startDateIsSelected = false;
        vacationStartDateTV.setText("");
        vacationStartCloseImage.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.close_end_vacation)
    void onClickCloseEndVacation(View view) {
        vacationEndDateTV.setText("");
        vacationEndCloseImage.setVisibility(View.INVISIBLE);
    }

    @OnItemSelected(R.id.spinner_send_pkg_login_notifii)
    void onSendPkgLoginSelected(int position) {
        NTF_Utils.hideKeyboard(getActivity());
        if (position == 0) {
            sendPkgLoginNotifiiSelected = sendPkgLoginNotifiiList.get(0).getValue();
            sendPkgLoginNotifiiText.setText(sendPkgLoginNotifiiList.get(0).getName());
        } else {
            sendPkgLoginNotifiiText.setText(sendPkgLoginNotifiiList.get(position).getName());
            sendPkgLoginNotifiiSelected = ((SpinnerData) sendPkgLoginNotifiiSpinner.getSelectedItem()).getValue();
        }
        if (sendPkgLoginNotifiiSpinner.getSelectedItem() != null) {
            SpinnerData.resetData(sendPkgLoginNotifiiList);
            ((SpinnerData) sendPkgLoginNotifiiSpinner.getSelectedItem()).setSelected(true);
        }
    }

    @OnItemSelected(R.id.spinner_send_pkg_logout_notifii)
    void onSendPkgLogoutSelected(int position) {
        NTF_Utils.hideKeyboard(getActivity());
        if (position == 0) {
            sendPkgLogoutNotifiiSelected = sendPkgLogoutNotifiiList.get(0).getValue();
            sendPkgLogoutNotifiiText.setText(sendPkgLogoutNotifiiList.get(0).getName());
        } else {
            sendPkgLogoutNotifiiText.setText(sendPkgLogoutNotifiiList.get(position).getName());
            sendPkgLogoutNotifiiSelected = ((SpinnerData) sendPkgLogoutNotifiiSpinner.getSelectedItem()).getValue();
        }
        if (sendPkgLogoutNotifiiSpinner.getSelectedItem() != null) {
            SpinnerData.resetData(sendPkgLogoutNotifiiList);
            ((SpinnerData) sendPkgLogoutNotifiiSpinner.getSelectedItem()).setSelected(true);
        }
    }

    @OnItemSelected(R.id.spinner_specialtrackInsFlag)
    void onSpecialTrakInsFlagSelected(int position) {
        NTF_Utils.hideKeyboard(getActivity());
        if (position == 0) {
            specialtrackInsFlagSelected = specialTrackInsFlagList.get(0).getValue();
            specialTrackInsFlagText.setText(specialTrackInsFlagList.get(0).getName());
            specialTrackInsLayout.setVisibility(View.GONE);
        } else {
            specialTrackInsFlagText.setText(specialTrackInsFlagList.get(position).getName());
            specialtrackInsFlagSelected = ((SpinnerData) specialTrackInsFlagSpinner.getSelectedItem()).getValue();
            if (specialTrackInsFlagText.getText().toString().equalsIgnoreCase("none")) {
                specialTrackInsLayout.setVisibility(View.GONE);
            } else {
                specialTrackInsLayout.setVisibility(View.VISIBLE);
            }
        }
        if (specialTrackInsFlagSpinner.getSelectedItem() != null) {
            SpinnerData.resetData(specialTrackInsFlagList);
            ((SpinnerData) specialTrackInsFlagSpinner.getSelectedItem()).setSelected(true);
        }
    }


    @OnItemSelected(R.id.spinner_type)
    void onSpinnerTypeSelected(int position) {
        if (mSpinnerType.getSelectedItem() != null) {
            mTvSpinnerType.setText(mTypeList.get(position - 1).getName());
            mRecipientTypeValue = mTypeList.get(position - 1).getValue();
            SpinnerData.resetData(mTypeList);
            ((SpinnerData) mSpinnerType.getSelectedItem()).setSelected(true);
        }
    }

    @OnItemSelected(R.id.spinner_status)
    void onSpinnerStatusSelected(int position) {
        if (isFirstTime) {
            isFirstTime = false;
            return;
        }
        if (mSpinnerStatus.getSelectedItem() != null) {
            mTvSpinnerStatus.setText(mStatusList.get(position).getName());
            selectedStatusValue = ((SpinnerData) mSpinnerStatus.getSelectedItem()).getValue();
            SpinnerData.resetData(mStatusList);
            ((SpinnerData) mSpinnerStatus.getSelectedItem()).setSelected(true);
        }
    }

    class CalendarDialogViewHolder {
        @BindView(R.id.month_spinner)
        Spinner mMonthSpinner;
        @BindView(R.id.year_spinner)
        Spinner mYearSpinner;
        @BindView(R.id.gridview)
        GridView mgridView;
        @BindView(R.id.previous)
        TextView mTextViewPrevious;
        @BindView(R.id.textView_month_spinner)
        TextView textViewMonth;
        @BindView(R.id.startDateFarmeLayout)
        FrameLayout startDateFramelayout;
        @BindView(R.id.textView_year_spinner)
        TextView textViewYear;
        @BindView(R.id.endDate_frame_layout)
        FrameLayout endDateFl;
        @BindView(R.id.next)
        TextView mTextViewNext;

        CalendarDialogViewHolder(Dialog view) {
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.textView_month_spinner)
        void onClickTextMonthSpinner(View v) {
            mMonthSpinner.performClick();
        }

        @OnClick(R.id.startDateFarmeLayout)
        void onClickStartDateFrameLayout(View view) {
            mMonthSpinner.performClick();
        }

        @OnClick(R.id.textView_year_spinner)
        void onClickTextViewYearSpinner(View view) {
            mYearSpinner.performClick();
        }

        @OnClick(R.id.endDate_frame_layout)
        void onClickEndDateFramelayout(View view) {
            mYearSpinner.performClick();
        }

        @OnClick(R.id.previous)
        void onClickPrevious(View view) {
            previousMethod();
        }

        @OnClick(R.id.next)
        void onClickNext(View view) {
            nextMethod();
        }

        @OnItemSelected(R.id.month_spinner)
        public void onItemSelectedMonth(int position) {
            temp.set(Calendar.MONTH, position);
            refreshCalendar();
            textViewMonth.setText(mMonthList[position]);
            mMonth = mMonthSpinner.getSelectedItemPosition();
            setMethod(false);
        }

        @OnItemSelected(R.id.year_spinner)
        public void onItemSelectedYear(int position) {
            temp.set(Calendar.YEAR, Integer.parseInt(mYearList[position]));
            refreshCalendar();
            textViewYear.setText(mYearList[position]);
            mYear = Integer.valueOf(mYearSpinner.getSelectedItem().toString());
            setMethod(false);
        }

        @OnItemClick(R.id.gridview)
        public void onItemClick(View view, int position) {
            TextView textView = (TextView) view.findViewById(R.id.date);
            if (!textView.isEnabled()) {
                return;
            }

            if (textView.getText().toString().isEmpty()) {
                return;
            }

            if (!mgridView.getItemAtPosition(position).toString().isEmpty()) {
                mDay = mgridView.getItemAtPosition(position).toString();
                setMethod(true);
                if (mButtonId == 1) {
                    startDateIsSelected = true;
                    vacationStartCloseImage.setVisibility(View.VISIBLE);
                } else if (mButtonId == 2) {
                    vacationEndCloseImage.setVisibility(View.VISIBLE);
                }
                mCalendarDialog.dismiss();

            }
        }
    }

    Spinner mMonthSpinner = null;
    Spinner mYearSpinner = null;
    GridView mgridView = null;
    TextView mTextViewPrevious = null;
    TextView textViewMonth = null;
    FrameLayout startDateFramelayout, endDateFarmeLayout = null;
    TextView textViewYear = null;
    TextView mTextViewNext = null;

    // Displaying the Calender Dialog...
    private void showCalendarDialog() {
        mCalendarDialog = new Dialog(getActivity()/*,R.style.MyTheme_MySpinnerStyle*/);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mCalendarDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View inflatedView = inflater.inflate(R.layout.calendar, null);
        mCalendarDialog.setContentView(inflatedView);
        mCalendarDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams wmlp = mCalendarDialog.getWindow().getAttributes();
        mCalendarDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        CalendarDialogViewHolder vi = new CalendarDialogViewHolder(mCalendarDialog);
        mMonthSpinner = vi.mMonthSpinner;
        mYearSpinner = vi.mYearSpinner;
        mgridView = vi.mgridView;
        mTextViewPrevious = vi.mTextViewPrevious;
        textViewMonth = vi.textViewMonth;
        startDateFramelayout = vi.startDateFramelayout;
        textViewYear = vi.textViewYear;
        endDateFarmeLayout = vi.endDateFl;
        mTextViewNext = vi.mTextViewNext;
        showCalendarMethod();
        wmlp.gravity = Gravity.TOP | Gravity.LEFT;
        int location1[] = new int[2];
        vacationStartDateTV.getLocationOnScreen(location1);   //x position
        wmlp.x = location1[0];
        wmlp.y = location1[1];   //y position
        //wmlp.width = WindowManager.LayoutParams.WRAP_CONTENT;

        int width = getResources().getDisplayMetrics().widthPixels;
        if (mCalendarDialog == null || mCalendarDialog.getWindow() == null) {
            return;
        }
        int dialogWidth = (int) (width * 0.85f);
        if (getResources().getBoolean(R.bool.isDeviceTablet)) {
            dialogWidth = (int) (width * 0.58f);
        }
        int dialogHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        mCalendarDialog.getWindow().setLayout(dialogWidth, dialogHeight);
        mCalendarDialog.getWindow().setAttributes(wmlp);

        mCalendarDialog.show();
    }

    // This method performs the calendar functionality...
    private void showCalendarMethod() {
        temp = Calendar.getInstance();
        final Calendar currentDateCal = Calendar.getInstance();
        String startDateString = "";
        String endDateString = "";
        mMonthList = getResources().getStringArray(R.array.month);

        String mCurrentMonth = mMonthList[mCurrentCalendar.get(Calendar.MONTH)];

        int mCurrentYear = mCurrentCalendar.get(Calendar.YEAR);

        if (yearList != null) {
            yearList.clear();
        }
        for (int i = mCurrentYear - 15, len = mCurrentYear + 15; i <= len; i++) {
            yearList.add(String.valueOf(i));
        }

        mYearList = new String[yearList.size()];
        for (int i = 0; i < yearList.size(); i++) {
            mYearList[i] = yearList.get(i);
        }

        if (mButtonId == 1) {
            if (vacationStartDateTV.getText().toString().isEmpty()) {
                startDateString = date.format(currentDateCal.getTime());
            } else {
                startDateString = vacationStartDateTV.getText().toString();
            }
            Log.v("DateString: start", "" + startDateString);
            String[] split = startDateString.split("-");
            mDay = split[2];
            mCurrentMonth = String.valueOf(Integer.parseInt(split[1]));
            mCurrentYear = Integer.parseInt(split[0]);

            if (isFromEditScreen) {
                mPreviousCalendar.set(mCurrentYear, Integer.parseInt(mCurrentMonth), Integer.parseInt(mDay));
                mCurrentCalendar.set(mCurrentYear, Integer.parseInt(mCurrentMonth), Integer.parseInt(mDay));
                temp.set(mCurrentYear, Integer.parseInt(mCurrentMonth), Integer.parseInt(mDay));
            }
        } else if (mButtonId == 2) {
            if (vacationEndDateTV.getText().toString().isEmpty()) {
                if (!vacationStartDateTV.getText().toString().isEmpty()) {
                    endDateString = vacationStartDateTV.getText().toString();
                } else {
                    endDateString = date.format(currentDateCal.getTime());
                }
            } else {
                if (!vacationStartDateTV.getText().toString().isEmpty()) {
                    try {
                        Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(vacationEndDateTV.getText().toString());
                        Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse(vacationStartDateTV.getText().toString());
                        if (date1.compareTo(date2) > 0) {
                            endDateString = vacationEndDateTV.getText().toString();
                        } else {
                            endDateString = vacationStartDateTV.getText().toString();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    endDateString = vacationEndDateTV.getText().toString();
                }
            }

            String[] split = endDateString.split("-");
            if (split.length >= 2) {
                mDay = split[2];
                mCurrentMonth = String.valueOf(Integer.parseInt(split[1]));
                mCurrentYear = Integer.parseInt(split[0]);
            }
            if (isFromEditScreen) {
                mNextCalendar.set(mCurrentYear, Integer.parseInt(mCurrentMonth), Integer.parseInt(mDay));
                mCurrentCalendar.set(mCurrentYear, Integer.parseInt(mCurrentMonth), Integer.parseInt(mDay));
                temp.set(mCurrentYear, Integer.parseInt(mCurrentMonth), Integer.parseInt(mDay));
            }
        }

        if (mDay != null && !mDay.isEmpty() && mMonth != null && mYear != null) {
            temp.set(mYear, mMonth, Integer.valueOf(mDay));
        } else if (mDay != null && !mDay.isEmpty() && mCurrentMonth != null && mCurrentYear != 0 && mCurrentYear != -1) {
            temp.set(mCurrentYear, Integer.parseInt(mCurrentMonth), Integer.valueOf(mDay));
        } else {
            mDay = String.valueOf(currentDateCal.get(Calendar.DAY_OF_MONTH));
            mCurrentMonth = String.valueOf(currentDateCal.get(Calendar.MONTH));
            mCurrentYear = currentDateCal.get(Calendar.YEAR);
            temp.set(mCurrentYear, Integer.parseInt(mCurrentMonth), Integer.valueOf(mDay));
        }

        if (!vacationStartDateTV.getText().toString().isEmpty() && String.valueOf(mPreviousCalendar.get(Calendar.MONTH)).equalsIgnoreCase(String.valueOf(0))) {
            mPreviousCalendar.set(Calendar.MONTH, Integer.parseInt(mCurrentMonth));
        }
        if (!vacationEndDateTV.getText().toString().isEmpty() && String.valueOf(mNextCalendar.get(Calendar.MONTH)).equalsIgnoreCase(String.valueOf(0))) {
            mNextCalendar.set(Calendar.MONTH, Integer.parseInt(mCurrentMonth));
        }
        if (mButtonId == 1) {
            mCalendarAdapter = new CalendarViewAdapter(getActivity(), temp, mDay, true, false, true, startDateIsSelected, mButtonId, mMonthSpinner, mYearSpinner, vacationStartDateTV.getText().toString());
        } else {
            mCalendarAdapter = new CalendarViewAdapter(getActivity(), temp, mDay, true, true, true, startDateIsSelected, mButtonId, mMonthSpinner, mYearSpinner, vacationStartDateTV.getText().toString());
        }
        mgridView.setAdapter(mCalendarAdapter);

        NTF_Utils.setCalendarSpinnerHeight(getActivity(), mMonthSpinner);
        NTF_Utils.setCalendarSpinnerHeight(getActivity(), mYearSpinner);

        ArrayAdapter monthAdapter = new ArrayAdapter(getActivity(), R.layout.calendar_spinner_item, mMonthList);
        mMonthSpinner.setAdapter(monthAdapter);

        ArrayAdapter yearAdapter = new ArrayAdapter(getActivity(), R.layout.calendar_spinner_item, mYearList);
        mYearSpinner.setAdapter(yearAdapter);
        mMonthSpinner.setSelection(Integer.parseInt(mCurrentMonth) - 1);

        textViewMonth.setText(mMonthSpinner.getSelectedItem().toString());

        int position = yearAdapter.getPosition(String.valueOf(mCurrentYear));
        mYearSpinner.setSelection(position);
        textViewYear.setText(mYearSpinner.getSelectedItem().toString());

        handler = new Handler();
        handler.post(calendarUpdater);
    }

    private void setMethod(boolean isDateSelected) {
        date = new SimpleDateFormat("yyyy-MM-dd");
        if (mButtonId == 1) {
            mPreviousCalendar = Calendar.getInstance();
            if (mYear != null && mMonth != null) {
                mPreviousCalendar.set(mYear, mMonth, Integer.valueOf(mDay));
            } else {
                mPreviousCalendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(mDay));
            }

            mStartDate = date.format(mPreviousCalendar.getTime());
            if (isDateSelected) {
                vacationStartDateTV.setText(mStartDate);
            }
        } else if (mButtonId == 2) {
            mNextCalendar = Calendar.getInstance();
            Log.v("date", "" + mYear + " " + mMonth + " " + mDay);

            if (mYear != null && mMonth != null) {
                mNextCalendar.set(mYear, mMonth, Integer.valueOf(mDay));
            } else {
                mNextCalendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(mDay));
            }
            mEndDate = date.format(mNextCalendar.getTime());
            if (isDateSelected) {

                vacationEndDateTV.setText(mEndDate);
            }
        }

    }

    // This method performs the refreshing of calendar...
    public void refreshCalendar() {
        mCalendarAdapter.refreshDays();
        mCalendarAdapter.notifyDataSetChanged();
        handler.post(calendarUpdater); // generate some random mCurrentCalendar items
    }

    private ArrayList<String> items = new ArrayList<>();

    public Runnable calendarUpdater = new Runnable() {
        @Override
        public void run() {
            items.clear();
            for (int i = 0; i < 31; i++) {
                Random r = new Random();
                if (r.nextInt(10) > 7) {
                    items.add(Integer.toString(i));
                }
            }
            mCalendarAdapter.setItems(items);
            mCalendarAdapter.notifyDataSetChanged();
        }
    };

    // Setting the data to the Month and year spinners...

    // This method will execute when clicks on previous button in calendar
    private void previousMethod() {

        if (mCurrentCalendar.get(Calendar.MONTH) == mCurrentCalendar.getActualMinimum(Calendar.MONTH)) {
            mCurrentCalendar.set((mCurrentCalendar.get(Calendar.YEAR) - 1), mCurrentCalendar.getActualMaximum(Calendar.MONTH), 1);
        } else {
            mCurrentCalendar.set(Calendar.MONTH, mCurrentCalendar.get(Calendar.MONTH) - 1);
        }
        refreshCalendar();

        int mCurrentMonthItem = mMonthSpinner.getSelectedItemPosition();
        if (mCurrentMonthItem > 0) {
            mMonthSpinner.setSelection(mCurrentMonthItem - 1);
            textViewMonth.setText(mMonthSpinner.getSelectedItem().toString());
        } else {
            int mMonthSpinnerItem = mYearSpinner.getSelectedItemPosition();
            if (mMonthSpinnerItem < yearList.size()) {
                if (mMonthSpinnerItem > 0) {
                    mMonthSpinner.setSelection(11);
                    mYearSpinner.setSelection(mMonthSpinnerItem - 1);

                    textViewMonth.setText(mMonthSpinner.getSelectedItem().toString());
                    textViewYear.setText(mYearSpinner.getSelectedItem().toString());
                } else
                    mTextViewPrevious.setEnabled(false);
                mTextViewNext.setEnabled(true);
            }
        }
    }

    // This method will execute when clicks on next button in calendar
    private void nextMethod() {

        if (mCurrentCalendar.get(Calendar.MONTH) == mCurrentCalendar.getActualMaximum(Calendar.MONTH)) {
            mCurrentCalendar.set((mCurrentCalendar.get(Calendar.YEAR) + 1),
                    mCurrentCalendar.getActualMinimum(Calendar.MONTH), 1);
        } else {
            mCurrentCalendar.set(Calendar.MONTH, mCurrentCalendar.get(Calendar.MONTH) + 1);
        }
        refreshCalendar();

        int mYearSpinnerItem = mYearSpinner.getSelectedItemPosition();
        int mCurrentYearItem = mMonthSpinner.getSelectedItemPosition();
        if (mCurrentYearItem < 11) {
            mMonthSpinner.setSelection(mCurrentYearItem + 1);
            textViewMonth.setText(mMonthSpinner.getSelectedItem().toString());
        } else {
            if (mYearSpinnerItem < yearList.size() - 1) {
                mMonthSpinner.setSelection(0);
                mYearSpinner.setSelection(mYearSpinnerItem + 1);
                textViewMonth.setText(mMonthSpinner.getSelectedItem().toString());
                textViewYear.setText(mYearSpinner.getSelectedItem().toString());
            } else
                mTextViewNext.setEnabled(false);
            mTextViewPrevious.setEnabled(true);
        }
    }

}
