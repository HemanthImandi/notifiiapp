package com.notifii.notifiiapp.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;
import butterknife.OnTouch;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.activities.MainActivity;
import com.notifii.notifiiapp.activities.ManateeBarcodeScannerActivity;
import com.notifii.notifiiapp.activities.OCRActivity;
import com.notifii.notifiiapp.adapters.RecipientAdapter;
import com.notifii.notifiiapp.adapters.SenderAdapter;
import com.notifii.notifiiapp.adapters.ShelfAdapter;
import com.notifii.notifiiapp.adapters.SpinnerHintAdapter;
import com.notifii.notifiiapp.adapters.SpinnerMarkAdapter;
import com.notifii.notifiiapp.base.NTF_BaseFragment;
import com.notifii.notifiiapp.customui.CustomAutoCompleteTextView;
import com.notifii.notifiiapp.customui.SpecialHandlingLinearLayout;
import com.notifii.notifiiapp.helpers.BitmapHelper;
import com.notifii.notifiiapp.helpers.SingleTon;
import com.notifii.notifiiapp.models.ListModel;
import com.notifii.notifiiapp.models.Recipient;
import com.notifii.notifiiapp.models.SenderData;
import com.notifii.notifiiapp.models.ShelfData;
import com.notifii.notifiiapp.models.SpecialHandling;
import com.notifii.notifiiapp.models.SpinnerData;
import com.notifii.notifiiapp.models.TrackingNumber;
import com.notifii.notifiiapp.mvp.models.LogPkgInResponse;
import com.notifii.notifiiapp.mvp.presenters.PkgLoginPresenter;
import com.notifii.notifiiapp.mvp.views.LogPkgInView;
import com.notifii.notifiiapp.refresh.TextChangeListener;
import com.notifii.notifiiapp.adapters.TrackingRVAdapter;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LogPackageInFragment extends NTF_BaseFragment implements TextChangeListener, LogPkgInView {

    @BindView(R.id.trackingsRV)
    RecyclerView trackingsRV;
    @BindView(R.id.parentLayout)
    LinearLayout parentLayout;

    public JSONObject mGobalJsonData;
    private ArrayList<TrackingNumber> dataSet = new ArrayList<>();
    private View headerView, footerView;
    private TrackingRVAdapter rvAdapter;
    private ArrayList<Recipient> mRecipientArrayList;
    private ArrayList<SpecialHandling> specialHandlings;
    private ArrayList<SpinnerData> mCarrierList;
    private ArrayList<SpinnerData> mServiceTypeList;
    private ArrayList<SpinnerData> mPkgTypeList;
    private ArrayList<SpinnerData> mPkgConditionsList;
    private ArrayList<SpinnerData> mPackageStatusList;
    private ArrayList<SpinnerData> mSendNoificationList;
    private ArrayList<SpinnerData> mMailRoomList;
    private ArrayList<SenderData> mSenderList;
    private ArrayList<ShelfData> mShelfList;
    private String mCarrier = "", mServiceType = "", mPkgType = "", mPkgCondition = "", mPkgStatus = "", mSendNoti = "", mMailRoom = "";
    private Bitmap bitmap1, bitmap2, bitmap3, ocrBitmap;
    private String pkgLoginCarrier, pkgLoginSender, pkgLoginPONumber, pkgLoginSpecialHandling, pkgLoginServiceType, pkgLoginCondition,
            pkgLoginPkgType, pkgLoginShelf, pkgLoginWeight, pkgLoginDimensions, pkgLoginCustomMessage, pkgLoginPictures;
    private PkgOtherViewsViewHolder pkgOtherViewsViewHolder;
    private LoginPicturesViewHolder loginPicturesViewHolder;
    private CustomMessageViewHolder customMessageViewHolder;
    private SpecialHandlingHolder specialHandlingViewHolder;
    private ShelfViewHolder shelfViewHolder;
    private PONumberViewHolder poNumberViewHolder;
    private DimensionsViewHolder dimensionsViewHolder;
    private WeightViewHolder weightViewHolder;
    private PkgConditionViewHolder pkgConditionViewHolder;
    private PackageTypeViewHolder packageTypeViewHolder;
    private ServiceTypeViewHolder serviceTypeViewHolder;
    private SenderViewHolder senderViewHolder;
    private CarrierViewHolder carrierViewHolder;
    private FooterViewHolder footerViewHolder;
    private HeaderViewHolder headerViewHolder;
    private boolean isTablet;
    private Activity activity;
    private Recipient mRecipient;
    private final String package_pictured = "package_pictured#0";
    private boolean isOcrTriggered = false, isFirstImageRemoved = false;
    private String versionName = "";
    private String base64Str1 = null, base64Str2 = null, base64Str3 = null;
    private boolean isCarrierManuallySelected = false;
    private boolean ocrImageIsInProgress = false, pkgLoginStarted = false;
    private String senderData = "", weightData = "", dimensionsData = "", poNumberData = "", shelfData = "", customMessageData = "", staffNotesData = "";
    PkgLoginPresenter presenter;
    private String previousDefaultMailroom = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isTablet = getResources().getBoolean(R.bool.isDeviceTablet);
        try {
            versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Context contextt) {
        super.onAttach(contextt);
        this.activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mGobalJsonData = NTF_Utils.getGlobalData(getActivity());
        mainView = inflater.inflate(R.layout.fragment_log_pkg_in, container, false);
        ButterKnife.bind(this, mainView);
        trackingsRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        headerView = inflater.inflate(R.layout.log_pkg_in_header, trackingsRV, false);
        footerView = inflater.inflate(R.layout.log_pkg_in_footer, trackingsRV, false);
        rvAdapter = new TrackingRVAdapter(dataSet, headerView, footerView);
        rvAdapter.setTextChangeListener(this);
        rvAdapter.setFragment(LogPackageInFragment.this);
        trackingsRV.setAdapter(rvAdapter);
        return mainView;
    }

    private void updateSpecialHandlings() {
        try {
            if (specialHandlingViewHolder != null && specialHandlings != null) {
                specialHandlingViewHolder.shPL.removeAllViews();
                for (SpecialHandling specialHandling : specialHandlings) {
                    try {
                        specialHandling.getHolder().specialHandlingTV.setText(specialHandling.getName());
                        specialHandling.getHolder().checkbox1.setBackgroundResource(specialHandling.isSelected() ? (R.drawable.ic_check_selected) : (R.drawable.ic_check_default));
                        specialHandling.getHolder().checkbox1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                specialHandling.setSelected(!specialHandling.isSelected());
                                specialHandling.getHolder().checkbox1.setBackgroundResource(specialHandling.isSelected() ? (R.drawable.ic_check_selected) : (R.drawable.ic_check_default));
                            }
                        });
                        if (((ViewGroup)specialHandling.getHolder().parentLayout.getParent()) != null){
                            ((ViewGroup)specialHandling.getHolder().parentLayout.getParent()).removeView(specialHandling.getHolder().parentLayout);
                        }
                        specialHandlingViewHolder.shPL.addView(specialHandling.getHolder().parentLayout);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStackChanged() {
        super.onStackChanged();
        if (NTF_Utils.getCurrentFragment(this) != null && NTF_Utils.getCurrentFragment(this) instanceof LogPackageInFragment) {
            onFragmentPopup(true);
        } else {
            onFragmentPopup(false);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            if (dataSet.size() == 0) {
                addTrackingNumberRow();
            }
            footerViewHolder = new FooterViewHolder(footerView);
            headerViewHolder = new HeaderViewHolder(headerView);
            NTF_Utils.setTypefaceForACTV(headerViewHolder.editTextRecipientName, getActivity());
            setToolbarTitle("");
            setToolbarActionButtons(false, false, 0);
            setLogo(R.drawable.ic_notifii_track_white_logo);
            setAppIcon(false);
            if (dataSet == null) {
                dataSet = new ArrayList<>();
            }
            presenter = new PkgLoginPresenter();
            presenter.attachMvpView(this);
            configureViews();
            if (!previousDefaultMailroom.equals(ntf_Preferences.get(Prefs_Keys.DEFAULT_MAILROOM_ID))) {
                previousDefaultMailroom = ntf_Preferences.get(Prefs_Keys.DEFAULT_MAILROOM_ID);
                mMailRoom = "";
            }
            if (!SingleTon.getInstance().isLogPkgInNeedRefesh()) {
                reloadData();      //to load that already entered data
            } else {
                mRecipient = null;
                dataSet = new ArrayList<>();
                rvAdapter = new TrackingRVAdapter(dataSet, headerView, footerView);
                rvAdapter.setTextChangeListener(this);
                rvAdapter.setFragment(LogPackageInFragment.this);
                trackingsRV.setAdapter(rvAdapter);
                addTrackingNumberRow();
                mMailRoom = "";
                mCarrier = "";
                mServiceType = "";
                mPkgCondition = "";
                mPkgType = "";
                mPkgStatus = "";
                mSendNoti = "";
                SingleTon.getInstance().setLogPkgInNeedRefesh(false);
                specialHandlings = SpecialHandling.getSpecialHandling(NTF_Utils.getGlobalData(getActivity()), getActivity()!= null?getActivity():activity);
//                SpecialHandlingsAdapter specialHandlingsAdapter = new SpecialHandlingsAdapter(getActivity(), specialHandlings);
//                specialHandlingViewHolder.specialHandlingRecyclerView.setAdapter(specialHandlingsAdapter);
                updateSpecialHandlings();
                bitmap1 = null;
                bitmap2 = null;
                bitmap3 = null;
            }
            onPrepareFields();
            handlePrivileges();
            setAllSpinnersData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handlePrivileges() {
        if (!ntf_Preferences.get(Prefs_Keys.PRIVILEGE_TRACK_PACKAGES).equalsIgnoreCase("f")) {
            ((MainActivity) (getActivity() != null ? getActivity() : activity)).enableTransparentLayer();
            parentLayout.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
            NTF_Utils.showInSufficientPrivelegeAlert(getActivity() != null ? getActivity() : activity);
        } else {
            ((MainActivity) (getActivity() != null ? getActivity() : activity)).disableTransparentLayer();
        }
    }

    public void addTrackingNumberRow() {
        if (dataSet != null) {
            TrackingNumber newItem = new TrackingNumber();
            dataSet.add(newItem);
            if (rvAdapter != null) {
                rvAdapter.notifyDataSetChanged();
            }
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (!intent.getBooleanExtra(NTF_Constants.IS_KEBOARD_OPEN, false)) {
                    onKeyBoardClose();
                } else {

                }
            } catch (Exception e) {
                Log.e("Exception", e.getMessage());
            }
        }
    };

    private void onKeyBoardClose() {
        try {
            if (dataSet.size() > 1) {
                for (int i = dataSet.size() - 1; i >= 0; i--) {
                    if (dataSet.get(i).getTrackingNumber().isEmpty()) {
                        dataSet.remove(i);
                    } else {
                        dataSet.get(i).setTrackingNumber(dataSet.get(i).getTrackingNumber().trim());
                    }
                }
            }
            if (dataSet.size() == 0) {
                addTrackingNumberRow();
            }
            if (!isCarrierManuallySelected) {
                setCarrier(dataSet.get(0).getTrackingNumber());
            }
            rvAdapter.notifyDataSetChanged();
            if (headerViewHolder.recipientNameFL.getVisibility() == View.VISIBLE && headerViewHolder.editTextRecipientName.isFocused()) {
                headerViewHolder.editTextRecipientName.showDropDown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter(NTF_Constants.KEYBOARD_BROADCAST_RECEIVER_ACTION));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(finishBackgroundCall,
                new IntentFilter(Extras_Keys.LOCAL_BROADCAST_END_ACTION));
    }

    private void onPrepareFields() {
        headerViewHolder.editTextRecipientName.setDropDownBackgroundResource(R.drawable.spinner_list);
        if (SingleTon.getInstance().getmRecipientList() != null && !SingleTon.getInstance().getmRecipientList().isEmpty()) {
            mRecipientArrayList = SingleTon.getInstance().getmRecipientList();
            setRecipientList();
        } else {
            new DataFetching().execute();
        }
        if (dataSet != null && dataSet.size() == 0) {
            dataSet.clear();
            addTrackingNumberRow();
        }
    }

    private void reloadData() {
        if (mRecipient != null) {
            setRecipient(mRecipient);
        } else {
            removeRecipient();
        }
        if (bitmap1 != null) {
            loginPicturesViewHolder.imgView1.setImageBitmap(bitmap1);
            loginPicturesViewHolder.viewPicClose1.setVisibility(View.VISIBLE);
        }
        if (bitmap2 != null) {
            loginPicturesViewHolder.imgView2.setImageBitmap(bitmap2);
            loginPicturesViewHolder.viewPicClose2.setVisibility(View.VISIBLE);
        }
        if (bitmap3 != null) {
            loginPicturesViewHolder.imgView3.setImageBitmap(bitmap3);
            loginPicturesViewHolder.viewPicClose3.setVisibility(View.VISIBLE);
        }
        senderViewHolder.mEditTextSender.setText(senderData);
        weightViewHolder.mEditTextWeight.setText(weightData);
        dimensionsViewHolder.mEditTextDimensions.setText(dimensionsData);
        poNumberViewHolder.mEditTextPONumber.setText(poNumberData);
        shelfViewHolder.mEditShelfLocation.setText(shelfData);
        customMessageViewHolder.mEditTextCustomMessage.setText(customMessageData);
        pkgOtherViewsViewHolder.mStaffNotes.setText(staffNotesData);
    }

    private void setAllSpinnersData() {
        if (ntf_Preferences.get(Prefs_Keys.PRIVILEGE_TRACK_PACKAGES).equalsIgnoreCase("f")) {
            setMailRoomSpinnerData();
            setCarrierSpinnerData();
            setServiceTypeSpinnerData();
            setPkgTypeSpinnerData();
            setPkgConditionSpinnerData();
            setPackageStatusSpinnerData();
            setSendNotificationSpinnerData();
            setShelfData();
            setSenderData();
        }
    }

    private void setServiceTypeSpinnerData() {
        try {
            ListModel listModel = SpinnerData.getList(mGobalJsonData.getJSONArray("service_types"), mServiceType);
            mServiceTypeList = listModel.getList();
            if (mServiceTypeList != null && !mServiceTypeList.isEmpty()) {
                if (mServiceTypeList.size() >= 10) {
                    NTF_Utils.setSpinnerHeight(getActivity(), serviceTypeViewHolder.mSpinnerServiceType);
                }
                SpinnerMarkAdapter adapter = new SpinnerMarkAdapter(getActivity(), mServiceTypeList);
                serviceTypeViewHolder.mSpinnerServiceType.setAdapter(new SpinnerHintAdapter(adapter, R.layout.spinner_hint, getActivity()));
                if (listModel.getPosition() != -1) {
                    serviceTypeViewHolder.mSpinnerServiceType.setSelection(listModel.getPosition() + 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPkgTypeSpinnerData() {
        try {
            ListModel listModel = SpinnerData.getList(mGobalJsonData.getJSONArray("package_types"), mPkgType);
            mPkgTypeList = listModel.getList();
            if (mPkgTypeList != null && !mPkgTypeList.isEmpty()) {
                if (mPkgTypeList.size() >= 10) {
                    NTF_Utils.setSpinnerHeight(getActivity(), packageTypeViewHolder.mSpinnerPackageType);
                }
                SpinnerMarkAdapter adapter = new SpinnerMarkAdapter(getActivity(), mPkgTypeList);
                packageTypeViewHolder.mSpinnerPackageType.setAdapter(new SpinnerHintAdapter(adapter, R.layout.spinner_hint, getActivity()));
                if (listModel.getPosition() != -1) {
                    packageTypeViewHolder.mSpinnerPackageType.setSelection(listModel.getPosition() + 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPkgConditionSpinnerData() {
        try {
            ListModel listModel = SpinnerData.getList(mGobalJsonData.getJSONArray("package_conditions"), mPkgCondition);
            mPkgConditionsList = listModel.getList();
            if (mPkgConditionsList != null && !mPkgConditionsList.isEmpty()) {
                if (mPkgConditionsList.size() >= 10) {
                    NTF_Utils.setSpinnerHeight(getActivity(), pkgConditionViewHolder.mSpinnerPackageCondition);
                }
                SpinnerMarkAdapter adapter = new SpinnerMarkAdapter(getActivity(), mPkgConditionsList);
                pkgConditionViewHolder.mSpinnerPackageCondition.setAdapter(new SpinnerHintAdapter(adapter, R.layout.spinner_hint, getActivity()));
                if (listModel.getPosition() != -1) {
                    pkgConditionViewHolder.mSpinnerPackageCondition.setSelection(listModel.getPosition() + 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPackageStatusSpinnerData() {
        try {
            ListModel listModel = SpinnerData.getList(mGobalJsonData.getJSONArray("logout_code_id"), mPkgStatus);
            mPackageStatusList = NTF_Utils.filterPKGStatus(listModel.getList());
            if (mPackageStatusList != null && !mPackageStatusList.isEmpty()) {
                if (mPackageStatusList.size() >= 10) {
                    NTF_Utils.setSpinnerHeight(getActivity(), pkgOtherViewsViewHolder.mSpinnerPackageStatus);
                }
                SpinnerMarkAdapter adapter = new SpinnerMarkAdapter(getActivity(), mPackageStatusList);
                pkgOtherViewsViewHolder.mSpinnerPackageStatus.setAdapter(new SpinnerHintAdapter(adapter, R.layout.spinner_hint, getActivity()));
                if (listModel.getPosition() != -1) {
                    pkgOtherViewsViewHolder.mSpinnerPackageStatus.setSelection(listModel.getPosition() + 1);
                } else {
                    for (int i = 0, len = mPackageStatusList.size(); i < len; i++) {
                        if (mPackageStatusList.get(i).getValue().equals("10000")) {
                            pkgOtherViewsViewHolder.mSpinnerPackageStatus.setSelection(i + 1);
                            break;
                        } else {
                            pkgOtherViewsViewHolder.mSpinnerPackageStatus.setSelection(0);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSendNotificationSpinnerData() {
        try {
            ListModel listModel = SpinnerData.getList(mGobalJsonData.getJSONArray("advanced_send_notification"), mSendNoti);
            mSendNoificationList = listModel.getList();
            if (mSendNoificationList != null && !mSendNoificationList.isEmpty()) {
                if (mSendNoificationList.size() >= 10) {
                    NTF_Utils.setSpinnerHeight(getActivity(), pkgOtherViewsViewHolder.mSpinnerSendNotification);
                }
                SpinnerMarkAdapter adapter = new SpinnerMarkAdapter(getActivity(), mSendNoificationList);
                pkgOtherViewsViewHolder.mSpinnerSendNotification.setAdapter(new SpinnerHintAdapter(adapter, R.layout.spinner_hint, getActivity()));
                if (listModel.getPosition() != -1) {
                    pkgOtherViewsViewHolder.mSpinnerSendNotification.setSelection(listModel.getPosition() + 1);
                } else {
                    for (int i = 0, len = mSendNoificationList.size(); i < len; i++) {
                        if (mSendNoificationList.get(i).getValue().equals("default")) {
                            pkgOtherViewsViewHolder.mSpinnerSendNotification.setSelection(i + 1);
                            break;
                        } else {
                            pkgOtherViewsViewHolder.mSpinnerSendNotification.setSelection(0);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setMailRoomSpinnerData() {
        try {
            ListModel listModel = SpinnerData.getList(mGobalJsonData.getJSONArray("mailrooms"), mMailRoom);
            if (listModel.getPosition() == -1) {
                mMailRoom = ntf_Preferences.get(Prefs_Keys.DEFAULT_MAILROOM_ID);
                listModel = SpinnerData.getList(mGobalJsonData.getJSONArray("mailrooms"), mMailRoom);
                if (listModel.getPosition() == -1 && listModel.getList().size() > 0) {
                    mMailRoom = listModel.getList().get(0).getValue();
                    listModel = SpinnerData.getList(mGobalJsonData.getJSONArray("mailrooms"), mMailRoom);
                }
            }
            mMailRoomList = listModel.getList();
            NTF_Utils.setSpinnerDropDownHeight(mMailRoomList.size(), footerViewHolder.spinnerMailRoom, getActivity() != null ? getActivity() : activity);
            /*if (mMailRoomList.size() >= 10) {
                NTF_Utils.setSpinnerDropDownHeight(getActivity(), footerViewHolder.spinnerMailRoom);
            }*/
            SpinnerMarkAdapter adapter = new SpinnerMarkAdapter(getActivity(), mMailRoomList);
            footerViewHolder.spinnerMailRoom.setAdapter(new SpinnerHintAdapter(adapter, R.layout.spinner_hint, getActivity()));
            String defaultMailRoomId = ntf_Preferences.get(Prefs_Keys.DEFAULT_MAILROOM_ID);
            if (listModel.getPosition() != -1) {
                footerViewHolder.spinnerMailRoom.setSelection(listModel.getPosition() + 1);
            } else if (defaultMailRoomId.equalsIgnoreCase("all")) {
                mMailRoom = mMailRoomList.get(0).getValue();
                footerViewHolder.spinnerMailRoom.setSelection(1);
            } else {
                for (int i = 0, len = mMailRoomList.size(); i < len; i++) {
                    if (mMailRoomList.get(i).getValue().equalsIgnoreCase(defaultMailRoomId)) {
                        mMailRoom = mMailRoomList.get(i).getValue();
                        footerViewHolder.spinnerMailRoom.setSelection(i + 1);
                        break;
                    }
                }
            }
            if (mMailRoomList.size() > 1) {
                footerViewHolder.ll_mailRoom.setVisibility(View.VISIBLE);
            } else {
                footerViewHolder.ll_mailRoom.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Setting carrier spinner data
    public void setCarrierSpinnerData() {
        try {
            NTF_Utils.hideKeyboard(getActivity());
            ListModel carrierListModel = SpinnerData.getList(mGobalJsonData.getJSONArray("shipping_carriers"), mCarrier);
            mCarrierList = carrierListModel.getList();
            if (mCarrierList != null && !mCarrierList.isEmpty()) {
                if (mCarrierList.size() >= 10) {
                    NTF_Utils.setSpinnerHeight(getActivity(), carrierViewHolder.spinnerCarrier);
                }
                SpinnerMarkAdapter adapter = new SpinnerMarkAdapter(getActivity(), mCarrierList);
                carrierViewHolder.spinnerCarrier.setAdapter(new SpinnerHintAdapter(adapter, R.layout.spinner_hint, getActivity()));
                if (carrierListModel.getPosition() != -1) {
                    carrierViewHolder.spinnerCarrier.setSelection(carrierListModel.getPosition() + 1);
                }
            }
            if (mCarrier != null && !mCarrier.isEmpty() && carrierListModel.getPosition() == -1) {
                carrierViewHolder.mCarrierText.setText(mCarrier);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onFirstTrackingNumberRemoved() {
        try {
            isCarrierManuallySelected = false;
            JSONArray jsonArr = SingleTon.getInstance().getOcrArray();
            if (isOcrTriggered && jsonArr.length() > 0 && !jsonArr.getJSONObject(jsonArr.length() - 1)
                    .getString("tracking_number_match_status").equalsIgnoreCase("0")) {
                jsonArr.getJSONObject(jsonArr.length() - 1).put("tracking_number_match_status", "2");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == getActivity().RESULT_OK && requestCode == OCR_REQUEST_CODE) {
                if (SingleTon.getInstance().getRecipient() != null) {
                    setRecipient(SingleTon.getInstance().getRecipient());
                    headerViewHolder.errorPkgLoginRecipientLL.setVisibility(View.INVISIBLE);
                } else {
                    removeRecipient();
                    headerViewHolder.errorPkgLoginRecipientLL.setVisibility(View.VISIBLE);
                }
                String barcodeValue = data.getExtras().getString(OCRActivity.BARCODEVALUE);
                if (!barcodeValue.isEmpty()) {
                    headerViewHolder.errorTrackingNumberLL.setVisibility(View.INVISIBLE);
                    dataSet.clear();
                    dataSet.add(new TrackingNumber());
                    dataSet.get(0).setTrackingNumber(barcodeValue);
                    rvAdapter.notifyDataSetChanged();
                    setCarrier(barcodeValue);
                } else {
                    dataSet.clear();
                    addTrackingNumberRow();
                    headerViewHolder.errorTrackingNumberLL.setVisibility(View.VISIBLE);
                }
                if (SingleTon.getInstance().getRecipient() != null && !barcodeValue.isEmpty()) {
                    bitmap1 = SingleTon.getInstance().getBitmap1();
                    ocrBitmap = bitmap1.copy(bitmap1.getConfig(), true);
                    loginPicturesViewHolder.imgView1.setImageBitmap(bitmap1);
                    loginPicturesViewHolder.viewPicClose1.setVisibility(View.VISIBLE);
                    isFirstImageRemoved = false;
                } else {
                    isFirstImageRemoved = true;
                    bitmap1 = null;
                    loginPicturesViewHolder.mGap1.setVisibility(View.VISIBLE);
                    loginPicturesViewHolder.imgView1.setImageResource(R.drawable.ic_camera_icon);
                    loginPicturesViewHolder.viewPicClose1.setVisibility(View.GONE);
                }
                saveOcrData(data);
            } else if (resultCode == getActivity().RESULT_OK && requestCode == ManateeBarcodeScannerActivity.BARCODE_REQUEST_CODE) {
                String scanResult = data.getExtras().getString(ManateeBarcodeScannerActivity.KEY_SCAN_RESULT);
                int rvPosition = rvAdapter.getCurrentPosition();
                for (TrackingNumber trackingNumber : dataSet) {
                    if (trackingNumber.getTrackingNumber().trim().equalsIgnoreCase(scanResult) && rvPosition != dataSet.indexOf(trackingNumber)) {
                        NTF_Utils.showAlert(getActivity(), "", "This package was already scanned.", null);
                        return;
                    } else if (trackingNumber.getTrackingNumber().trim().equalsIgnoreCase(scanResult)) {
                        return;
                    }
                }
                if (rvPosition >= dataSet.size()) {
                    rvPosition = dataSet.size() - 1;
                }
                dataSet.get(rvPosition).setTrackingNumber(scanResult);
                rvAdapter.notifyDataSetChanged();
                if (rvPosition == 0) {
                    setCarrier(scanResult);
                }
                try {
                    JSONArray jsonArr = SingleTon.getInstance().getOcrArray();
                    if (isOcrTriggered && jsonArr.length() > 0
                            && (dataSet.size() == 0 || dataSet.size() == 1 || rvPosition == 0)
                            && !jsonArr.getJSONObject(jsonArr.length() - 1).optString("tracking_number_match_status").equalsIgnoreCase("0")) {
                        jsonArr.getJSONObject(jsonArr.length() - 1).put("tracking_number_match_status", "2");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == getActivity().RESULT_OK && requestCode == CLICK_PHOTO_REQUEST_CODE_1) {
                bitmap1 = SingleTon.getInstance().getBitmap1();
                loginPicturesViewHolder.imgView1.setImageBitmap(bitmap1);
                loginPicturesViewHolder.viewPicClose1.setVisibility(View.VISIBLE);
            } else if (resultCode == getActivity().RESULT_OK && requestCode == CLICK_PHOTO_REQUEST_CODE_2) {
                bitmap2 = SingleTon.getInstance().getBitmap2();
                loginPicturesViewHolder.imgView2.setImageBitmap(bitmap2);
                loginPicturesViewHolder.viewPicClose2.setVisibility(View.VISIBLE);
            } else if (resultCode == getActivity().RESULT_OK && requestCode == CLICK_PHOTO_REQUEST_CODE_3) {
                bitmap3 = SingleTon.getInstance().getBitmap3();
                loginPicturesViewHolder.imgView3.setImageBitmap(bitmap3);
                loginPicturesViewHolder.viewPicClose3.setVisibility(View.VISIBLE);
            } else if (SingleTon.getInstance().getCapturedBitmap() != null && SingleTon.getInstance().getRequestCode() == CLICK_PHOTO_REQUEST_CODE_1) {
                bitmap1 = SingleTon.getInstance().getBitmap1();
                Glide.with(getActivity() != null ? getActivity() : activity)
                        .load(BitmapHelper.getByteArrayOfBitmap(bitmap1))
                        .into(loginPicturesViewHolder.imgView1);
                loginPicturesViewHolder.viewPicClose1.setVisibility(View.VISIBLE);
            } else if (SingleTon.getInstance().getCapturedBitmap() != null && SingleTon.getInstance().getRequestCode() == CLICK_PHOTO_REQUEST_CODE_2) {
                bitmap2 = SingleTon.getInstance().getBitmap2();
                Glide.with(getActivity() != null ? getActivity() : activity)
                        .load(BitmapHelper.getByteArrayOfBitmap(bitmap2))
                        .into(loginPicturesViewHolder.imgView2);
                loginPicturesViewHolder.viewPicClose2.setVisibility(View.VISIBLE);
            } else if (SingleTon.getInstance().getCapturedBitmap() != null && SingleTon.getInstance().getRequestCode() == CLICK_PHOTO_REQUEST_CODE_3) {
                bitmap3 = SingleTon.getInstance().getBitmap3();
                Glide.with(getActivity() != null ? getActivity() : activity)
                        .load(BitmapHelper.getByteArrayOfBitmap(bitmap3))
                        .into(loginPicturesViewHolder.imgView3);
                loginPicturesViewHolder.viewPicClose3.setVisibility(View.VISIBLE);
            }
            SingleTon.getInstance().setRequestCode(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearAllFields() {
        dataSet.clear();
        addTrackingNumberRow();
        removeRecipient();
        mCarrier = "";
        mServiceType = "";
        mPkgType = "";
        mPkgCondition = "";
        mPkgStatus = "";
        mSendNoti = "";
        mMailRoom = "";
        ocrBitmap = null;
        senderData = "";
        weightData = "";
        dimensionsData = "";
        poNumberData = "";
        shelfData = "";
        customMessageData = "";
        staffNotesData = "";
        senderViewHolder.mEditTextSender.setText(senderData);
        weightViewHolder.mEditTextWeight.setText(weightData);
        dimensionsViewHolder.mEditTextDimensions.setText(dimensionsData);
        poNumberViewHolder.mEditTextPONumber.setText(poNumberData);
        shelfViewHolder.mEditShelfLocation.setText(shelfData);
        customMessageViewHolder.mEditTextCustomMessage.setText(customMessageData);
        pkgOtherViewsViewHolder.mStaffNotes.setText(staffNotesData);
        loginPicturesViewHolder.onClearImage1();
        loginPicturesViewHolder.onClearImage2();
        loginPicturesViewHolder.onClearImage3();
        specialHandlings = SpecialHandling.getSpecialHandling(NTF_Utils.getGlobalData(getActivity()), getActivity()!= null?getActivity():activity);
//        SpecialHandlingsAdapter specialHandlingsAdapter = new SpecialHandlingsAdapter(getActivity(), specialHandlings);
//        specialHandlingViewHolder.specialHandlingRecyclerView.setAdapter(specialHandlingsAdapter);
        updateSpecialHandlings();
        carrierViewHolder.spinnerCarrier.setSelection(0);
        serviceTypeViewHolder.mSpinnerServiceType.setSelection(0);
        packageTypeViewHolder.mSpinnerPackageType.setSelection(0);
        pkgConditionViewHolder.mSpinnerPackageCondition.setSelection(0);
        carrierViewHolder.mCarrierText.setText("");
        serviceTypeViewHolder.servicetypeText.setText("");
        packageTypeViewHolder.pkgTypeText.setText("");
        pkgConditionViewHolder.pkgConditionText.setText("");
        SpinnerData.resetData(mCarrierList);
        SpinnerData.resetData(mServiceTypeList);
        SpinnerData.resetData(mPkgTypeList);
        SpinnerData.resetData(mPkgConditionsList);
        SpinnerData.resetData(mPackageStatusList);
        SpinnerData.resetData(mSendNoificationList);
        setPackageStatusSpinnerData();
        setSendNotificationSpinnerData();
        isOcrTriggered = false;
        isFirstImageRemoved = false;
        isCarrierManuallySelected = false;
        ocrImageIsInProgress = false;
        pkgLoginStarted = false;
        base64Str1 = null;
        base64Str2 = null;
        base64Str3 = null;
    }

    private void saveOcrData(Intent data) {
        try {
            isOcrTriggered = true;
            String ocrtime = "", algorithm_time = "", ocr_string = "", ocr_matched_tracking_number = "";
            ocrtime = data.getExtras().getString(Extras_Keys.OCR_TIME);
            algorithm_time = data.getExtras().getString(Extras_Keys.ALGORITHM_TIME);
            ocr_string = data.getExtras().getString(Extras_Keys.DETECTED_TEXT);
            ocr_matched_tracking_number = data.getExtras().getString(OCRActivity.BARCODEVALUE);
            JSONArray jsonArray = SingleTon.getInstance().getOcrArray();
            JSONObject jsonobj = new JSONObject();
            jsonobj.put("ocr_time", ocrtime);
            jsonobj.put("algorithm_time", String.valueOf(algorithm_time));
            if (ocr_matched_tracking_number.length() == 0) {
                jsonobj.put("tracking_number_match_status", "0");
                jsonobj.put("ocr_matched_tracking_number", "");
            } else {
                jsonobj.put("tracking_number_match_status", "1");
                jsonobj.put("ocr_matched_tracking_number", ocr_matched_tracking_number);
            }
            if (mRecipient == null) {
                jsonobj.put("recipient_match_status", "0");
                jsonobj.put("ocr_matched_recipient_name", "");
                jsonobj.put("ocr_matched_recipient_id", "");
            } else {
                jsonobj.put("recipient_match_status", "1");
                jsonobj.put("ocr_matched_recipient_name", mRecipient.getFullName());
                jsonobj.put("ocr_matched_recipient_id", mRecipient.getRecipientId());
            }
            jsonobj.put("ocr_string", ocr_string);
            if (mRecipient != null && ocr_matched_tracking_number.length() > 0) {
                jsonobj.put("ocr_image", package_pictured);
                new OCRBase64Task().execute();
            } else {
                jsonobj.put("ocr_image", "");
            }
            jsonobj.put("app_version", getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName);
            jsonobj.put("device_type", "Device=" + Build.MODEL + "; OS=" + Build.VERSION.RELEASE);
            jsonArray.put(jsonobj);
            SingleTon.getInstance().setOcrArray(jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("OCR_METRICS_ERROR", e.getMessage());
        }
    }

    @Override
    public void onPkgLoginSuccess(LogPkgInResponse response) {
        SingleTon.getInstance().setOcrArray(new JSONArray());
        trackingsRV.smoothScrollToPosition(0);
        footerViewHolder.mButtonLogPkgIn.setBackgroundResource(R.drawable.bg_button);
        if (mMailRoom != null && !mMailRoom.isEmpty()) {
            ntf_Preferences.save(Prefs_Keys.DEFAULT_MAILROOM_ID, mMailRoom);
        }
        clearAllFields();
        NTF_Utils.showLogPkgInAlert(getActivity(), ALERT_SUCCESS_TITLE_AWESOME_JOB, response);
        NTF_Utils.hideProgressDialog();
    }

    @Override
    public void onPkgLoginWarning(LogPkgInResponse response) {
        SingleTon.getInstance().setOcrArray(new JSONArray());
        trackingsRV.smoothScrollToPosition(0);
        footerViewHolder.mButtonLogPkgIn.setBackgroundResource(R.drawable.bg_button);
        if (mMailRoom != null && !mMailRoom.isEmpty()) {
            ntf_Preferences.save(Prefs_Keys.DEFAULT_MAILROOM_ID, mMailRoom);
        }
        clearAllFields();
        NTF_Utils.showLogPkgInAlert(getActivity(), ALERT_WARNING_TITLE, response);
        NTF_Utils.hideProgressDialog();
    }

    @Override
    public void onPkgLoginError(LogPkgInResponse response) {
        SingleTon.getInstance().setOcrArray(new JSONArray());
        trackingsRV.smoothScrollToPosition(0);
        footerViewHolder.mButtonLogPkgIn.setBackgroundResource(R.drawable.bg_button);
        if (mMailRoom != null && !mMailRoom.isEmpty()) {
            ntf_Preferences.save(Prefs_Keys.DEFAULT_MAILROOM_ID, mMailRoom);
        }
        clearAllFields();
        NTF_Utils.showAlert(getActivity(), ALERT_ERROR_TITLE_OOPS, response.getApiMessage(), null);
        NTF_Utils.hideProgressDialog();
    }

    @Override
    public void onSessionExpired(String message) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showSessionExpireAlert(message, getActivity(), ntf_Preferences);
    }

    @Override
    public void onServerError() {
        footerViewHolder.mButtonLogPkgIn.setBackgroundResource(R.drawable.bg_button);
        NTF_Utils.hideProgressDialog();
    }

    @Override
    public Context getMvpContext() {
        return getActivity();
    }

    @Override
    public void onError(Throwable throwable) {
        footerViewHolder.mButtonLogPkgIn.setBackgroundResource(R.drawable.bg_button);
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(getActivity(), "", NTF_Utils.getErrorMessage(throwable), null);
    }

    @Override
    public void onNoInternetConnection() {
        footerViewHolder.mButtonLogPkgIn.setBackgroundResource(R.drawable.bg_button);
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showNoNetworkAlert(getActivity());
    }

    @Override
    public void onErrorCode(String s) {
        footerViewHolder.mButtonLogPkgIn.setBackgroundResource(R.drawable.bg_button);
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(getActivity(), "", s, null);
    }

    class OCRBase64Task extends AsyncTask<Void, Void, Void> {
        String base64 = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Log.d("OCRIMAGEEE", "OCRBase64Task started");
                ocrImageIsInProgress = true;
                if (ocrBitmap != null) {
                    base64 = BitmapHelper.getBase64String(ocrBitmap);
                }
            } catch (Exception e) {
                Log.d("OCRIMAGEEE", e.getMessage());
                ocrImageIsInProgress = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                JSONArray jsonArray = SingleTon.getInstance().getOcrArray();
                Log.d("OCRIMAGEEE", "ocr base64 length:" + base64.length());
                jsonArray.getJSONObject(SingleTon.getInstance().getOcrArray().length() - 1)
                        .put("ocr_image", base64);
                SingleTon.getInstance().setOcrArray(jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("OCRIMAGEEE", e.getMessage());
            } finally {
                Log.d("OCRIMAGEEE", "OCRBase64Task completed");
                Log.d("OCRIMAGEEE", SingleTon.getInstance().getOcrArray().toString());
                ocrImageIsInProgress = false;
                if (pkgLoginStarted) {
                    Log.d("OCRIMAGEEE", "doLoginPkg from ocr image process completed");
                    pkgLoginStarted = false;
                    doLoginPkg();
                }
            }
        }
    }

    public void setCarrier(String trackingNumber) {
        String resultcarrier = NTF_Utils.detect_shipping_carrier(trackingNumber);
        SpinnerData.resetData(mCarrierList);
        if (trackingNumber.isEmpty()) {
            SpinnerMarkAdapter adapter = new SpinnerMarkAdapter(getActivity(), mCarrierList);
            carrierViewHolder.spinnerCarrier.setAdapter(new SpinnerHintAdapter(adapter, R.layout.spinner_hint, getActivity()));
            carrierViewHolder.mCarrierText.setText("");
            mCarrier = "";
        } else {
            boolean isCarrierIdentified = false;
            for (SpinnerData carrier : mCarrierList) {
                if (carrier.getName().equalsIgnoreCase(resultcarrier)) {
                    carrier.setSelected(true);
                    SpinnerMarkAdapter adapter = new SpinnerMarkAdapter(getActivity(), mCarrierList);
                    carrierViewHolder.spinnerCarrier.setAdapter(new SpinnerHintAdapter(adapter, R.layout.spinner_hint, getActivity()));
                    carrierViewHolder.mCarrierText.setText(carrier.getName());
                    mCarrier = carrier.getValue();
                    isCarrierIdentified = true;
                }
            }
            if (!isCarrierIdentified) {
                SpinnerMarkAdapter adapter = new SpinnerMarkAdapter(getActivity(), mCarrierList);
                carrierViewHolder.spinnerCarrier.setAdapter(new SpinnerHintAdapter(adapter, R.layout.spinner_hint, getActivity()));
                carrierViewHolder.mCarrierText.setText(resultcarrier);
                mCarrier = resultcarrier;
            }
        }
    }

    private BroadcastReceiver finishBackgroundCall = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (!ntf_Preferences.get(Prefs_Keys.PRIVILEGE_TRACK_PACKAGES).equalsIgnoreCase("f")) {
                    ((MainActivity) (getActivity() != null ? getActivity() : activity)).enableTransparentLayer();
                    parentLayout.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
                    NTF_Utils.showInSufficientPrivelegeAlert(getActivity() != null ? getActivity() : activity);
                    return;
                } else {
                    parentLayout.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
                    ((MainActivity) (getActivity() != null ? getActivity() : activity)).disableTransparentLayer();
                }
                SingleTon.getInstance().setLogPkgInNeedRefesh(true);
                Fragment frg = NTF_Utils.getCurrentFragment(LogPackageInFragment.this);
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(frg);
                ft.attach(frg);
                ft.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null) {
            if ((getActivity()).isFinishing()) {
                NTF_Utils.hideProgressDialog();
                NTF_Utils.hideKeyboard(getActivity());
            }
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(finishBackgroundCall);
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        }
    }

    public void openCamera(int requestCode) {

        NTF_Utils.checkPermissionToProgress(getActivity(), new Runnable() {
            @Override
            public void run() {
                Intent intent = NTF_Utils.getCameraIntent(getActivity());
                intent.putExtra(Extras_Keys.KEY_PHOT_POS, requestCode);
                startActivityForResult(intent, requestCode);
            }
        });
    }

    @Override
    public void onFirstPositionTextChanged(String beforeText, String afterText) {
        try {
            isCarrierManuallySelected = false;
            JSONArray jsonArr = SingleTon.getInstance().getOcrArray();
            if (isOcrTriggered && jsonArr.length() > 0 && Math.abs(beforeText.length() - afterText.length()) == 1
                    && !jsonArr.getJSONObject(jsonArr.length() - 1).getString("tracking_number_match_status").equalsIgnoreCase("0")) {
                jsonArr.getJSONObject(jsonArr.length() - 1).put("tracking_number_match_status", "2");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onTrackingNumberTouched() {
        headerViewHolder.errorTrackingNumberLL.setVisibility(View.INVISIBLE);
    }

    public void scanTrackingNumber() {
        headerViewHolder.errorTrackingNumberLL.setVisibility(View.INVISIBLE);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ManateeBarcodeScannerActivity.isQrAlso = false;
                startActivityForResult(new Intent(getActivity(), ManateeBarcodeScannerActivity.class), ManateeBarcodeScannerActivity.BARCODE_REQUEST_CODE);
            }
        };
        NTF_Utils.checkCameraPermissionToProgress(getActivity() != null ? getActivity() : activity, runnable);
    }

    public class CarrierViewHolder {

        @BindView(R.id.carrier_text)
        TextView mCarrierText;
        @BindView(R.id.carrierButtonDownArrow)
        ImageView carrierDownArrow;
        @BindView(R.id.spinnerCarrier)
        public Spinner spinnerCarrier;

        @BindView(R.id.startCarrier)
        ImageView mStartCarrier;

        CarrierViewHolder(View view) {

            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.carrier_flayout)
        void doSpinnerPopUp() {
            NTF_Utils.hideKeyboard(getActivity() != null ? getActivity() : activity);
            spinnerCarrier.performClick();
        }

        @OnItemSelected(R.id.spinnerCarrier)
        public void spinnerItemSelected(Spinner spinner, int position) {
            if (spinner.getSelectedItem() != null) {
                isCarrierManuallySelected = true;
                mCarrier = ((SpinnerData) spinnerCarrier.getSelectedItem()).getValue();
                SpinnerData.resetData(mCarrierList);
                ((SpinnerData) spinnerCarrier.getSelectedItem()).setSelected(true);
                mCarrierText.setText(((SpinnerData) spinnerCarrier.getSelectedItem()).getName());
            }
        }
    }

    class SenderViewHolder {

        @BindView(R.id.editTextSender)
        AutoCompleteTextView mEditTextSender;
        @BindView(R.id.starSender)
        ImageView mStarSender;
        @BindView(R.id.senderClear)
        ImageView senderClear;

        SenderViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        @OnFocusChange(R.id.editTextSender)
        void onFocusChanged(boolean focused) {
            if (focused && mEditTextSender.isFocused() && mEditTextSender.getText().toString().length() >= 2) {
                senderClear.setVisibility(View.VISIBLE);
            } else {
                senderClear.setVisibility(View.GONE);
            }
        }

        @OnTextChanged(R.id.editTextSender)
        void onTextChanged(CharSequence text) {
            onFocusChanged(true);
            senderData = text.toString();
        }

        @OnClick(R.id.senderClear)
        void clearSender() {
            mEditTextSender.setText("");
            senderClear.setVisibility(View.GONE);
        }
    }

    class ServiceTypeViewHolder {

        @BindView(R.id.servicetype_text)
        TextView servicetypeText;
        @BindView(R.id.spinnerServiceType)
        Spinner mSpinnerServiceType;
        @BindView(R.id.starServiceType)
        ImageView mStarServiceType;
        @BindView(R.id.serviceTypeButtonDownArrow)
        ImageView serviceDownArrow;

        ServiceTypeViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.servicetype_flayout)
        void doSpinnerPopUp() {
            NTF_Utils.hideKeyboard(getActivity() != null ? getActivity() : activity);
            mSpinnerServiceType.performClick();
        }

        @OnItemSelected(R.id.spinnerServiceType)
        public void spinnerItemSelected(Spinner spinner, int position) {
            if (spinner.getSelectedItem() != null) {
                mServiceType = ((SpinnerData) spinner.getSelectedItem()).getValue();
                SpinnerData.resetData(mServiceTypeList);
                ((SpinnerData) mSpinnerServiceType.getSelectedItem()).setSelected(true);
                servicetypeText.setText(((SpinnerData) mSpinnerServiceType.getSelectedItem()).getName());
            }
        }
    }

    class PackageTypeViewHolder {

        @BindView(R.id.spinnerPackageType)
        Spinner mSpinnerPackageType;
        @BindView(R.id.starPkgType)
        ImageView mStarPkgType;
        @BindView(R.id.pkgTypeButtonDownArrow)
        ImageView pkgTypeDownArrow;
        @BindView(R.id.pkgTypeText)
        TextView pkgTypeText;

        PackageTypeViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.packagetype_flayout)
        void doSpinnerPopUp() {
            NTF_Utils.hideKeyboard(getActivity() != null ? getActivity() : activity);
            mSpinnerPackageType.performClick();
        }

        @OnItemSelected(R.id.spinnerPackageType)
        public void spinnerItemSelected(Spinner spinner, int position) {
            if (spinner.getSelectedItem() != null) {
                mPkgType = ((SpinnerData) spinner.getSelectedItem()).getValue();
                SpinnerData.resetData(mPkgTypeList);
                ((SpinnerData) mSpinnerPackageType.getSelectedItem()).setSelected(true);
                pkgTypeText.setText(((SpinnerData) mSpinnerPackageType.getSelectedItem()).getName());
            }
        }
    }

    class PkgConditionViewHolder {
        @BindView(R.id.spinnerPackageCondition)
        Spinner mSpinnerPackageCondition;
        @BindView(R.id.starCondition)
        ImageView mStarCondition;
        @BindView(R.id.pkgCondButtonDownArrow)
        ImageView pkgCondDownArrow;
        @BindView(R.id.pkgConditionText)
        TextView pkgConditionText;

        PkgConditionViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.pkgcond_flayout)
        void doSpinnerPopUp() {
            NTF_Utils.hideKeyboard(getActivity() != null ? getActivity() : activity);
            mSpinnerPackageCondition.performClick();
        }

        @OnItemSelected(R.id.spinnerPackageCondition)
        public void spinnerItemSelected(Spinner spinner, int position) {
            if (spinner.getSelectedItem() != null) {
                mPkgCondition = ((SpinnerData) spinner.getSelectedItem()).getValue();
                SpinnerData.resetData(mPkgConditionsList);
                ((SpinnerData) mSpinnerPackageCondition.getSelectedItem()).setSelected(true);
                pkgConditionText.setText(((SpinnerData) mSpinnerPackageCondition.getSelectedItem()).getName());
            }
        }

    }

    class WeightViewHolder {

        @BindView(R.id.editTextWeight)
        EditText mEditTextWeight;
        @BindView(R.id.starWeight)
        ImageView mStarWeight;

        WeightViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        @OnTextChanged(R.id.editTextWeight)
        void onTextChanged(CharSequence text) {
            weightData = text.toString();
        }

    }

    class DimensionsViewHolder {

        @BindView(R.id.editTextDimensions)
        EditText mEditTextDimensions;
        @BindView(R.id.starDimensions)
        ImageView mStarDimensions;

        DimensionsViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        @OnTextChanged(R.id.editTextDimensions)
        void onTextChanged(CharSequence text) {
            dimensionsData = text.toString();
        }

    }

    class PONumberViewHolder {

        @BindView(R.id.editTextPONumber)
        EditText mEditTextPONumber;
        @BindView(R.id.starPONumber)
        ImageView mStarPONumber;

        PONumberViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        @OnTextChanged(R.id.editTextPONumber)
        void onTextChanged(CharSequence text) {
            poNumberData = text.toString();
        }

    }

    class ShelfViewHolder {

        @BindView(R.id.editShelfLocation)
        AutoCompleteTextView mEditShelfLocation;
        @BindView(R.id.starShelf)
        ImageView mStarShelf;
        @BindView(R.id.shelfClear)
        ImageView shelfClear;

        ShelfViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        @OnTextChanged(R.id.editShelfLocation)
        void onTextChanged(CharSequence text) {
            onFocusChanged(true);
            shelfData = text.toString();
        }

        @OnFocusChange(R.id.editShelfLocation)
        void onFocusChanged(boolean focused) {
            if (focused && mEditShelfLocation.isFocused() && mEditShelfLocation.getText().toString().length() >= 2) {
                shelfClear.setVisibility(View.VISIBLE);
            } else {
                shelfClear.setVisibility(View.GONE);
            }
        }

        @OnClick(R.id.shelfClear)
        void clearShelf() {
            mEditShelfLocation.setText("");
            shelfClear.setVisibility(View.GONE);
        }
    }

//    class SpecialHandlingViewHolder {
//
//        @BindView(R.id.specialhandlingsRV)
//        RecyclerView specialHandlingRecyclerView;
//        @BindView(R.id.dummy_layout)
//        View specialhandlingdummylayout;
//
//        SpecialHandlingViewHolder(View view) {
//            ButterKnife.bind(this, view);
//        }
//
//        @OnTouch(R.id.specialhandlingsRV)
//        boolean onRVTouch(View parent, MotionEvent event) {
//            parent.getParent().requestDisallowInterceptTouchEvent(false);
//            return false;
//        }
//    }

    class SpecialHandlingHolder {

        @BindView(R.id.shPL)
        SpecialHandlingLinearLayout shPL;
        @BindView(R.id.star_special_handling)
        ImageView star_SH;

        SpecialHandlingHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class CustomMessageViewHolder {

        @BindView(R.id.editTextCustomMessage)
        EditText mEditTextCustomMessage;
        @BindView(R.id.star_custom_message)
        ImageView mStarCustomMessage;

        CustomMessageViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        @OnTextChanged(R.id.editTextCustomMessage)
        void onTextChanged(CharSequence text) {
            customMessageData = text.toString();
        }

    }

    class LoginPicturesViewHolder {

        @BindView(R.id.image_pick1)
        ImageView imgView1;
        @BindView(R.id.image_pick2)
        ImageView imgView2;
        @BindView(R.id.image_pick3)
        ImageView imgView3;
        @BindView(R.id.imageButtonClose1)
        View viewPicClose1;
        @BindView(R.id.imageButtonClose2)
        View viewPicClose2;
        @BindView(R.id.imageButtonClose3)
        View viewPicClose3;
        @BindView(R.id.fl_1)
        View flView1;
        @BindView(R.id.fl_2)
        View flView2;
        @BindView(R.id.fl_3)
        View flView3;
        @BindView(R.id.gap1)
        View mGap1;
        @BindView(R.id.gap2)
        View mGap2;
        @BindView(R.id.gap3)
        View mGap3;
        @BindView(R.id.star_take_picture)
        ImageView mStarTakePicture;

        LoginPicturesViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.imageButtonClose1)
        void onClearImage1() {
            isFirstImageRemoved = true;
            bitmap1 = null;
            mGap1.setVisibility(View.VISIBLE);
            imgView1.setImageResource(R.drawable.ic_camera_icon);
            viewPicClose1.setVisibility(View.GONE);
            SingleTon.getInstance().setBitmap1(null);
        }

        @OnClick(R.id.imageButtonClose2)
        void onClearImage2() {
            bitmap2 = null;
            mGap2.setVisibility(View.VISIBLE);
            imgView2.setImageResource(R.drawable.ic_camera_icon);
            viewPicClose2.setVisibility(View.GONE);
            SingleTon.getInstance().setBitmap2(null);
        }

        @OnClick(R.id.imageButtonClose3)
        void onClearImage3() {
            bitmap3 = null;
            mGap3.setVisibility(View.VISIBLE);
            imgView3.setImageResource(R.drawable.ic_camera_icon);
            viewPicClose3.setVisibility(View.GONE);
            SingleTon.getInstance().setBitmap3(null);
        }

        @OnClick(R.id.fl_1)
        void pickFirstImage() {
            if (System.currentTimeMillis() - mLastClickTime < 500) {
                return;
            }
            mLastClickTime = System.currentTimeMillis();
            if (bitmap1 == null) {
                openCamera(CLICK_PHOTO_REQUEST_CODE_1);
            } else {
                NTF_Utils.showImageAlert(bitmap1, getActivity() != null ? getActivity() : activity);
            }
        }

        @OnClick(R.id.fl_2)
        void pickSecondImage() {
            if (System.currentTimeMillis() - mLastClickTime < 500) {
                return;
            }
            mLastClickTime = System.currentTimeMillis();
            if (bitmap2 == null) {
                openCamera(CLICK_PHOTO_REQUEST_CODE_2);
            } else {
                NTF_Utils.showImageAlert(bitmap2, getActivity() != null ? getActivity() : activity);
            }
        }

        @OnClick(R.id.fl_3)
        void pickThirdImage() {
            if (System.currentTimeMillis() - mLastClickTime < 500) {
                return;
            }
            mLastClickTime = System.currentTimeMillis();
            if (bitmap3 == null) {
                openCamera(CLICK_PHOTO_REQUEST_CODE_3);
            } else {
                NTF_Utils.showImageAlert(bitmap3, getActivity() != null ? getActivity() : activity);
            }
        }

    }

    private void onRefreshCalled() {
        if (bitmap1 != null) {
            isFirstImageRemoved = true;
        }
        mRecipient = null;
        removeRecipient();
        dataSet = new ArrayList<>();
        addTrackingNumberRow();
        setAllSpinnersData();
        senderViewHolder.mEditTextSender.setText("");
        weightViewHolder.mEditTextWeight.setText("");
        dimensionsViewHolder.mEditTextDimensions.setText("");
        poNumberViewHolder.mEditTextPONumber.setText("");
        shelfViewHolder.mEditShelfLocation.setText("");
        specialHandlings = null;
        customMessageViewHolder.mEditTextCustomMessage.setText("");
        loginPicturesViewHolder.onClearImage1();
        loginPicturesViewHolder.onClearImage2();
        loginPicturesViewHolder.onClearImage3();
        pkgOtherViewsViewHolder.mStaffNotes.setText("");
        configureViews();
        setAllSpinnersData();
    }


    class HeaderViewHolder {

        @BindView(R.id.editTextRecipientName)
        CustomAutoCompleteTextView editTextRecipientName;
        @BindView(R.id.ll_recipientName)
        LinearLayout ll_recipientName;
        @BindView(R.id.textViewAutoCompleteRecipient)
        TextView textViewAutoCompleteRecipient;
        @BindView(R.id.errorTrackingNumberLL)
        LinearLayout errorTrackingNumberLL;
        @BindView(R.id.errorPkgLoginRecipientLL)
        LinearLayout errorPkgLoginRecipientLL;
        @BindView(R.id.recipientNameFL)
        FrameLayout recipientNameFL;
        @BindView(R.id.recipientNameClear)
        ImageView recipientNameClear;

        @OnTextChanged(R.id.editTextRecipientName)
        void onTextChanged(CharSequence text) {
            onFocusChanged(true);
        }

        @OnClick(R.id.recipientNameClear)
        void clearSender() {
            editTextRecipientName.setText("");
            recipientNameClear.setVisibility(View.GONE);
        }

        @OnFocusChange(R.id.editTextRecipientName)
        void onFocusChanged(boolean focused) {
            if (focused && editTextRecipientName.isFocused() && editTextRecipientName.getText().toString().trim().length() >= 2) {
                recipientNameClear.setVisibility(View.VISIBLE);
            } else {
                recipientNameClear.setVisibility(View.GONE);
            }
        }

        @OnTouch(R.id.editTextRecipientName)
        boolean onEdittextTouched(View parent, MotionEvent event) {
            errorPkgLoginRecipientLL.setVisibility(View.INVISIBLE);
            return false;
        }

        @OnClick(R.id.errorTrackingNumberLL)
        void onbarcodeErrorClicked() {
            NTF_Utils.showAlert(getActivity(), "Warning!", OCRActivity.OCR_TNUM_BOUNCE_ALERT, null);
        }

        @OnClick(R.id.errorPkgLoginRecipientLL)
        void onrecipientErrorClicked() {
            NTF_Utils.showAlert(getActivity(), "Warning!", OCRActivity.OCR_RECIPIENT_BOUNCE_ALERT, null);
        }

        @OnClick(R.id.ocrIv)
        void takeOCRScan() {
            if (System.currentTimeMillis() - mLastClickTime < 500) {
                return;
            }
            mLastClickTime = System.currentTimeMillis();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    SingleTon.getInstance().setmRecipientListForOcr(mRecipientArrayList);
                    startActivityForResult(new Intent(getActivity(), OCRActivity.class), OCR_REQUEST_CODE);
                }
            };
            NTF_Utils.checkCameraPermissionToProgress(getActivity(), runnable);
        }

        @OnClick(R.id.imageButtonClear)
        void onRecipientClear() {
            removeRecipient();
        }

        public HeaderViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public void setDropDownHeight(int count) {
        NTF_Utils.setDropDownHeight(count, headerViewHolder.editTextRecipientName, getActivity() != null ? getActivity() : activity);
    }

    class DataFetching extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                JSONObject recipientJsonObj = NTF_Utils.getRecipientsData(getActivity());
                if (recipientJsonObj != null) {
                    mRecipientArrayList = Recipient.getRecipientsList(recipientJsonObj.getJSONArray("recipients"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            setRecipientList();
        }
    }

    public void setRecipientList() {
        if (mRecipientArrayList != null && !mRecipientArrayList.isEmpty()) {
            ArrayList<Recipient> list = new ArrayList<>();
            list.addAll(mRecipientArrayList);
            final RecipientAdapter recipientAdapter = new RecipientAdapter(getActivity() != null ? getActivity() : activity,
                    list, LogPackageInFragment.this);
            headerViewHolder.editTextRecipientName.setAdapter(recipientAdapter);
            recipientAdapter.setOnRecipientItemClickListener(new RecipientAdapter.RecipientItemClickNoticeListener() {
                @Override
                public void onItemClicked(Recipient recipient) {
                    NTF_Utils.hideKeyboard(getActivity() != null ? getActivity() : activity);
                    setRecipient(recipient);
                    try {
                        JSONArray jsonArr = SingleTon.getInstance().getOcrArray();
                        if (isOcrTriggered && jsonArr.length() > 0 && !jsonArr.getJSONObject(jsonArr.length() - 1)
                                .getString("recipient_match_status").equalsIgnoreCase("0")) {
                            jsonArr.getJSONObject(jsonArr.length() - 1).put("recipient_match_status", "2");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void removeRecipient() {
        headerViewHolder.ll_recipientName.setVisibility(View.GONE);
        headerViewHolder.recipientNameFL.setVisibility(View.VISIBLE);
        headerViewHolder.editTextRecipientName.setText("");
        mRecipient = null;
    }

    private void setRecipient(Recipient recipient) {
        headerViewHolder.ll_recipientName.setVisibility(View.VISIBLE);
        headerViewHolder.recipientNameFL.setVisibility(View.GONE);
        String nametext = recipient.getFirstName() + " " + recipient.getLastName();
        if (recipient.getAddress1() != null && !recipient.getAddress1().equalsIgnoreCase("") && !recipient.getAddress1().equalsIgnoreCase("null")) {
            nametext = nametext + " -- " + recipient.getAddress1();
        }
        headerViewHolder.textViewAutoCompleteRecipient.setText(nametext);
        mRecipient = recipient;
    }

    private void doLoginPkg() {
        try {
            Log.d("OCRIMAGEEE", "doLoginPkg entered");
            ArrayList<String> trackingNumbers = new ArrayList<>();
            ArrayList<String> carrierList = new ArrayList<>();
            ArrayList<String> handlings = new ArrayList<>();
            for (TrackingNumber trackingNumber : dataSet) {
                if (trackingNumber.getTrackingNumber() != null && !trackingNumber.getTrackingNumber().trim().isEmpty()) {
                    trackingNumbers.add(trackingNumber.getTrackingNumber().trim());
                }
            }
            for (String trackingNumber : trackingNumbers) {
                carrierList.add(NTF_Utils.detect_shipping_carrier(trackingNumber));
            }
            for (SpecialHandling specialHandling : specialHandlings) {
                if (specialHandling.isSelected()) {
                    handlings.add(specialHandling.getValue());
                }
            }
            String listOfTrackingNo = TextUtils.join("#&#", trackingNumbers);
            String listOfCarriers = TextUtils.join("#&#", carrierList);
            String listOfSpecialHandlings = TextUtils.join(",", handlings);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("session_id", ntf_Preferences.get(Prefs_Keys.SESSION_ID));
            jsonObject.put("authentication_token", ntf_Preferences.get(Prefs_Keys.AUTHENTICATION_TOKEN));
            jsonObject.put("account_id", ntf_Preferences.get(Prefs_Keys.ACCOUNT_ID));
            jsonObject.put("mailroom_id", mMailRoom);
            jsonObject.put("tracking_number", listOfTrackingNo);
            jsonObject.put("shipping_carrier", isCarrierManuallySelected ? mCarrier : listOfCarriers);
            jsonObject.put("recipient_id", mRecipient.getRecipientId());
            jsonObject.put("user_id", ntf_Preferences.get(Prefs_Keys.USER_ID));
            jsonObject.put("special_handlings", listOfSpecialHandlings);
            jsonObject.put("sender", senderViewHolder.mEditTextSender.getText().toString().trim());
            jsonObject.put("service_type", mServiceType);
            jsonObject.put("package_type", mPkgType);
            jsonObject.put("package_condition", mPkgCondition);
            jsonObject.put("shelf", shelfViewHolder.mEditShelfLocation.getText().toString().trim());
            jsonObject.put("po_number", poNumberViewHolder.mEditTextPONumber.getText().toString().trim());
            jsonObject.put("custom_message", customMessageViewHolder.mEditTextCustomMessage.getText().toString().trim());
            jsonObject.put("staff_note", pkgOtherViewsViewHolder.mStaffNotes.getText().toString().trim());
            jsonObject.put("logout_code_id", mPkgStatus);
            jsonObject.put("advanced_send_notification", mSendNoti);
            jsonObject.put("weight", weightViewHolder.mEditTextWeight.getText().toString().trim());
            jsonObject.put("dimensions", dimensionsViewHolder.mEditTextDimensions.getText().toString().trim());
            if (base64Str1 == null && base64Str2 == null && base64Str3 == null) {
                jsonObject.put("package_pictures", "");
            } else {
                JSONArray jsonArray = new JSONArray();
                if (base64Str1 != null) {
                    JSONObject jsonImg1 = new JSONObject();
                    jsonImg1.put("picture_data", base64Str1);
                    jsonArray.put(jsonImg1);
                }
                if (base64Str2 != null) {
                    JSONObject jsonImg2 = new JSONObject();
                    jsonImg2.put("picture_data", base64Str2);
                    jsonArray.put(jsonImg2);
                }
                if (base64Str3 != null) {
                    JSONObject jsonImg3 = new JSONObject();
                    jsonImg3.put("picture_data", base64Str3);
                    jsonArray.put(jsonImg3);
                }
                jsonObject.put("package_pictures", jsonArray);
            }
            jsonObject.put("ocr_triggered", isOcrTriggered ? "1" : "0");
            JSONArray ocrArray = SingleTon.getInstance().getOcrArray();
            if (isOcrTriggered && !isFirstImageRemoved && ocrArray.length() > 0
                    && !ocrArray.getJSONObject(ocrArray.length() - 1).getString("tracking_number_match_status").equalsIgnoreCase("0")
                    && !ocrArray.getJSONObject(ocrArray.length() - 1).getString("recipient_match_status").equalsIgnoreCase("0")
                    && !ocrArray.getJSONObject(ocrArray.length() - 1).getString("ocr_image").equalsIgnoreCase(package_pictured)) {
                ocrArray.getJSONObject(ocrArray.length() - 1).put("ocr_image", package_pictured);
            }
            if (ntf_Preferences.get(Prefs_Keys.PKG_LOGIN_PICTURES).equalsIgnoreCase("0")) {//pictures hide case
                if ((base64Str1 != null || base64Str2 != null || base64Str3 != null)
                        && jsonObject.optJSONArray("package_pictures").length() > 0) {
                    JSONObject pic1 = jsonObject.getJSONArray("package_pictures").getJSONObject(0);
                    String base64 = pic1.getString("picture_data");
                    ocrArray.getJSONObject(ocrArray.length() - 1).put("ocr_image", base64);
                    jsonObject.put("package_pictures", "");
                }
            }
            jsonObject.put("ocr_metrics_data", isOcrTriggered ? ocrArray : null);
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
            presenter.doPkgLogin(null, body);
            Log.d("Initiated", "yes");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class CovertBase64Task extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            NTF_Utils.showProgressDialog(getActivity());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (bitmap1 != null) {
                base64Str1 = BitmapHelper.getBase64String(bitmap1);
            }
            if (bitmap2 != null) {
                base64Str2 = BitmapHelper.getBase64String(bitmap2);
            }
            if (bitmap3 != null) {
                base64Str3 = BitmapHelper.getBase64String(bitmap3);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (ocrImageIsInProgress) {
                pkgLoginStarted = true;
            } else {
                doLoginPkg();
            }
        }
    }

    class FooterViewHolder {
        @BindView(R.id.spinnerMailRoom)
        Spinner spinnerMailRoom;
        @BindView(R.id.ll_mailRoom)
        LinearLayout ll_mailRoom;
        @BindView(R.id.mailroomText)
        TextView mailroomText;
        @BindView(R.id.ll_advanced_options)
        LinearLayout mLlAdvancedOptions;
        @BindView(R.id.ll_basic_options)
        LinearLayout mLlBasicView;
        @BindView(R.id.buttonLogPkgIn)
        Button mButtonLogPkgIn;
        @BindView(R.id.buttonAdvancedOption)
        TextView buttonAdvancedOption;


        public FooterViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.buttonLogPkgIn)
        void onSubmitClicked() {
            if (!NTF_Utils.isOnline(getActivity())) {
                NTF_Utils.showNoNetworkAlert(getActivity());
                ((MainActivity) getActivity()).registerReceiver();
            } else if (isFieldsValid()) {
                mButtonLogPkgIn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.button_disable));
                new CovertBase64Task().execute();
            }
        }

        @OnItemSelected(R.id.spinnerMailRoom)
        public void spinnerItemSelected(Spinner spinner, int position) {
            if (spinner.getSelectedItem() != null) {
                mMailRoom = ((SpinnerData) spinner.getSelectedItem()).getValue();
                SpinnerData.resetData(mMailRoomList);
                ((SpinnerData) spinnerMailRoom.getSelectedItem()).setSelected(true);
                mailroomText.setText(((SpinnerData) spinnerMailRoom.getSelectedItem()).getName());
            }
        }

        @OnClick(R.id.buttonAdvancedOption)
        void onClearImage1() {
            if (mLlAdvancedOptions.getTag().toString().equals("0")) {
                mLlAdvancedOptions.setVisibility(View.VISIBLE);
                mLlAdvancedOptions.setTag("1");
                buttonAdvancedOption.setContentDescription("collapsed advanced options");
            } else {
                mLlAdvancedOptions.setVisibility(View.GONE);
                mLlAdvancedOptions.setTag("0");
                buttonAdvancedOption.setContentDescription("expand advanced options");
            }
        }

        @OnClick(R.id.mailroom_flayout)
        void onMailroomSpinnerClicked() {
            NTF_Utils.hideKeyboard(getActivity() != null ? getActivity() : activity);
            spinnerMailRoom.performClick();
        }

    }


    class PkgOtherViewsViewHolder {

        @BindView(R.id.spinnerPackageStatus)
        Spinner mSpinnerPackageStatus;
        @BindView(R.id.spinnerSendNotification)
        Spinner mSpinnerSendNotification;
        @BindView(R.id.staff_notes)
        EditText mStaffNotes;
        @BindView(R.id.pkgStatusButtonDownArrow)
        ImageView pkgStatusButtonDownArrow;
        @BindView(R.id.sendNotifiButtonDownArrow)
        ImageView sendNotifiidownArrow;
        @BindView(R.id.sendNotificationText)
        TextView sendNotificationText;
        @BindView(R.id.pkgStatusText)
        TextView pkgStatusText;

        @OnTextChanged(R.id.staff_notes)
        void onTextChanged(CharSequence text) {
            staffNotesData = text.toString();
        }

        @OnItemSelected(R.id.spinnerPackageStatus)
        public void spinnerItemSelected(Spinner spinner, int position) {
            if (spinner.getSelectedItem() != null) {
                mPkgStatus = ((SpinnerData) spinner.getSelectedItem()).getValue();
                SpinnerData.resetData(mPackageStatusList);
                ((SpinnerData) mSpinnerPackageStatus.getSelectedItem()).setSelected(true);
                pkgStatusText.setText(((SpinnerData) mSpinnerPackageStatus.getSelectedItem()).getName());
            }
        }

        @OnItemSelected(R.id.spinnerSendNotification)
        public void sendNotificationItemSelected(Spinner spinner, int position) {
            if (spinner.getSelectedItem() != null) {
                mSendNoti = ((SpinnerData) spinner.getSelectedItem()).getValue();
                SpinnerData.resetData(mSendNoificationList);
                ((SpinnerData) mSpinnerSendNotification.getSelectedItem()).setSelected(true);
                sendNotificationText.setText(((SpinnerData) mSpinnerSendNotification.getSelectedItem()).getName());
            }
        }

        @OnClick(R.id.pkgstatus_flayout)
        void onPkgStatusClicked() {
            NTF_Utils.hideKeyboard(getActivity() != null ? getActivity() : activity);
            mSpinnerPackageStatus.performClick();
        }

        @OnClick(R.id.sendnotifii_flayout)
        void onSendNotificationClicked() {
            NTF_Utils.hideKeyboard(getActivity() != null ? getActivity() : activity);
            mSpinnerSendNotification.performClick();
        }

        PkgOtherViewsViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private void configureViews() {
        if (footerViewHolder.mLlAdvancedOptions.getChildCount() > 0) {
            footerViewHolder.mLlAdvancedOptions.removeAllViews();
        }
        if (footerViewHolder.mLlBasicView.getChildCount() > 0) {
            footerViewHolder.mLlBasicView.removeAllViews();
        }
        /****************Carrier View****************/
        View viewCarrier = null;
        pkgLoginCarrier = ntf_Preferences.get(Prefs_Keys.PKG_LOGIN_CARRIER);
        try {
            if (pkgLoginCarrier.equalsIgnoreCase("0")) {//do not show field
                viewCarrier = LayoutInflater.from(getActivity()).inflate(R.layout.layout_carrier, footerViewHolder.mLlBasicView, false);
                footerViewHolder.mLlBasicView.addView(viewCarrier);
                viewCarrier.setVisibility(View.GONE);
            } else if (pkgLoginCarrier.equalsIgnoreCase("1")) {// show field, input required
                viewCarrier = LayoutInflater.from(getActivity()).inflate(R.layout.layout_carrier, footerViewHolder.mLlBasicView, false);
                footerViewHolder.mLlBasicView.addView(viewCarrier);
            } else if (pkgLoginCarrier.equalsIgnoreCase("2")) {//show field in “basic options” section
                viewCarrier = LayoutInflater.from(getActivity()).inflate(R.layout.layout_carrier, footerViewHolder.mLlBasicView, false);
            } else if (pkgLoginCarrier.equalsIgnoreCase("3")) {// show field in “advanced options” section
                viewCarrier = LayoutInflater.from(getActivity()).inflate(R.layout.layout_carrier, footerViewHolder.mLlAdvancedOptions, false);
                footerViewHolder.mLlAdvancedOptions.addView(viewCarrier);
            }
            carrierViewHolder = new CarrierViewHolder(viewCarrier);
            carrierViewHolder.mStartCarrier.setVisibility(pkgLoginCarrier.equalsIgnoreCase("1") ? View.VISIBLE : View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /****************Sender View*************************/
        View viewSender = null;
        pkgLoginSender = ntf_Preferences.get(Prefs_Keys.PKG_LOGIN_SENDER);
        int viewIdSender = R.layout.layout_sender;
        try {
            if (pkgLoginSender.equalsIgnoreCase("0")) {//do not show field
                viewSender = LayoutInflater.from(getActivity()).inflate(viewIdSender, footerViewHolder.mLlBasicView, false);
                footerViewHolder.mLlBasicView.addView(viewSender);
                viewSender.setVisibility(View.GONE);
            } else if (pkgLoginSender.equalsIgnoreCase("1")) {// show field, input required
                viewSender = LayoutInflater.from(getActivity()).inflate(viewIdSender, footerViewHolder.mLlBasicView, false);
                footerViewHolder.mLlBasicView.addView(viewSender);
                viewSender.setVisibility(View.VISIBLE);
            } else if (pkgLoginSender.equalsIgnoreCase("2")) {//show field in “basic options” section
                viewSender = LayoutInflater.from(getActivity()).inflate(viewIdSender, footerViewHolder.mLlBasicView, false);
                viewSender.setVisibility(View.VISIBLE);
            } else if (pkgLoginSender.equalsIgnoreCase("3")) {// show field in “advanced options” section
                viewSender = LayoutInflater.from(getActivity()).inflate(viewIdSender, footerViewHolder.mLlAdvancedOptions, false);
                footerViewHolder.mLlAdvancedOptions.addView(viewSender);
                viewSender.setVisibility(View.VISIBLE);
            }
            senderViewHolder = new SenderViewHolder(viewSender);
            senderViewHolder.mStarSender.setVisibility(pkgLoginSender.equalsIgnoreCase("1") ? View.VISIBLE : View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /****************ServiceType View*************************/

        View viewServiceType = null;
        pkgLoginServiceType = ntf_Preferences.get(Prefs_Keys.PKG_LOGIN_SERVICETYPE);
        int viewIdServiceType = R.layout.layout_service_type;
        try {
            if (pkgLoginServiceType.equalsIgnoreCase("0")) {//do not show field
                viewServiceType = LayoutInflater.from(getActivity()).inflate(viewIdServiceType, footerViewHolder.mLlBasicView, false);
                footerViewHolder.mLlBasicView.addView(viewServiceType);
                viewServiceType.setVisibility(View.GONE);
            } else if (pkgLoginServiceType.equalsIgnoreCase("1")) {// show field, input required
                viewServiceType = LayoutInflater.from(getActivity()).inflate(viewIdServiceType, footerViewHolder.mLlBasicView, false);
                footerViewHolder.mLlBasicView.addView(viewServiceType);
            } else if (pkgLoginServiceType.equalsIgnoreCase("2")) {//show field in “basic options” section
                viewServiceType = LayoutInflater.from(getActivity()).inflate(viewIdServiceType, footerViewHolder.mLlBasicView, false);
            } else if (pkgLoginServiceType.equalsIgnoreCase("3")) {// show field in “advanced options” section
                viewServiceType = LayoutInflater.from(getActivity()).inflate(viewIdServiceType, footerViewHolder.mLlAdvancedOptions, false);
                footerViewHolder.mLlAdvancedOptions.addView(viewServiceType);
            }
            serviceTypeViewHolder = new ServiceTypeViewHolder(viewServiceType);
            serviceTypeViewHolder.mStarServiceType.setVisibility(pkgLoginServiceType.equalsIgnoreCase("1") ? View.VISIBLE : View.INVISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
        }

        /****************PackageType View*************************/
        View viewPkgTypeStatus = null;
        pkgLoginPkgType = ntf_Preferences.get(Prefs_Keys.PKG_LOGIN_PACKAGETYPE);
        try {
            if (pkgLoginPkgType.equalsIgnoreCase("0")) {//do not show field
                viewPkgTypeStatus = LayoutInflater.from(getActivity()).inflate(R.layout.layout_pkg_type, footerViewHolder.mLlBasicView, false);
                footerViewHolder.mLlBasicView.addView(viewPkgTypeStatus);
                viewPkgTypeStatus.setVisibility(View.GONE);
            } else if (pkgLoginPkgType.equalsIgnoreCase("1")) {// show field, input required
                viewPkgTypeStatus = LayoutInflater.from(getActivity()).inflate(R.layout.layout_pkg_type, footerViewHolder.mLlBasicView, false);
                footerViewHolder.mLlBasicView.addView(viewPkgTypeStatus);
            } else if (pkgLoginPkgType.equalsIgnoreCase("2")) {//show field in “basic options” section
                viewPkgTypeStatus = LayoutInflater.from(getActivity()).inflate(R.layout.layout_pkg_type, footerViewHolder.mLlBasicView, false);
            } else if (pkgLoginPkgType.equalsIgnoreCase("3")) {// show field in “advanced options” section
                viewPkgTypeStatus = LayoutInflater.from(getActivity()).inflate(R.layout.layout_pkg_type, footerViewHolder.mLlAdvancedOptions, false);
                footerViewHolder.mLlAdvancedOptions.addView(viewPkgTypeStatus);
            }
            packageTypeViewHolder = new PackageTypeViewHolder(viewPkgTypeStatus);
            packageTypeViewHolder.mStarPkgType.setVisibility(pkgLoginPkgType.equalsIgnoreCase("1") ? View.VISIBLE : View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /****************Condition View*************************/
        View viewCondition = null;
        pkgLoginCondition = ntf_Preferences.get(Prefs_Keys.PKG_LOGIN_CONDITION);
        int viewIdCondition = R.layout.layout_condition;
        try {
            if (pkgLoginCondition.equalsIgnoreCase("0")) {//do not show field
                viewCondition = LayoutInflater.from(getActivity()).inflate(viewIdCondition, footerViewHolder.mLlBasicView, false);
                footerViewHolder.mLlBasicView.addView(viewCondition);
                viewCondition.setVisibility(View.GONE);
            } else if (pkgLoginCondition.equalsIgnoreCase("1")) {// show field, input required
                viewCondition = LayoutInflater.from(getActivity()).inflate(viewIdCondition, footerViewHolder.mLlBasicView, false);
                footerViewHolder.mLlBasicView.addView(viewCondition);
            } else if (pkgLoginCondition.equalsIgnoreCase("2")) {//show field in “basic options” section
                viewCondition = LayoutInflater.from(getActivity()).inflate(viewIdCondition, footerViewHolder.mLlBasicView, false);
            } else if (pkgLoginCondition.equalsIgnoreCase("3")) {// show field in “advanced options” section
                viewCondition = LayoutInflater.from(getActivity()).inflate(viewIdCondition, footerViewHolder.mLlAdvancedOptions, false);
                footerViewHolder.mLlAdvancedOptions.addView(viewCondition);
            }
            pkgConditionViewHolder = new PkgConditionViewHolder(viewCondition);
            pkgConditionViewHolder.mStarCondition.setVisibility(pkgLoginCondition.equalsIgnoreCase("1") ? View.VISIBLE : View.INVISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
        }


        /****************login weight*************************/
        View viewWeight = null;
        pkgLoginWeight = ntf_Preferences.get(Prefs_Keys.PKG_LOGIN_WEIGHT, "2");
        int viewIdWeight = R.layout.layout_weight;
        try {
            if (pkgLoginWeight.equalsIgnoreCase("0")) {//do not show field
                viewWeight = LayoutInflater.from(getActivity()).inflate(viewIdWeight, footerViewHolder.mLlBasicView, false);
                footerViewHolder.mLlBasicView.addView(viewWeight);
                viewWeight.setVisibility(View.GONE);
            } else if (pkgLoginWeight.equalsIgnoreCase("1")) {// show field, input required
                viewWeight = LayoutInflater.from(getActivity()).inflate(viewIdWeight, footerViewHolder.mLlBasicView, false);
                footerViewHolder.mLlBasicView.addView(viewWeight);
            } else if (pkgLoginWeight.equalsIgnoreCase("2")) {//show field in “basic options” section
                viewWeight = LayoutInflater.from(getActivity()).inflate(viewIdWeight, footerViewHolder.mLlBasicView, false);
            } else if (pkgLoginWeight.equalsIgnoreCase("3")) {// show field in “advanced options” section
                viewWeight = LayoutInflater.from(getActivity()).inflate(viewIdWeight, footerViewHolder.mLlAdvancedOptions, false);
                footerViewHolder.mLlAdvancedOptions.addView(viewWeight);
            }
            weightViewHolder = new WeightViewHolder(viewWeight);
            weightViewHolder.mStarWeight.setVisibility(pkgLoginWeight.equalsIgnoreCase("1") ? View.VISIBLE : View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /****************login Dimensions*************************/
        View viewDimensions = null;
        pkgLoginDimensions = ntf_Preferences.get(Prefs_Keys.PKG_LOGIN_DIMENSIONS, "2");
        int viewIdDimensions = R.layout.layout_dimensions;
        try {
            if (pkgLoginDimensions.equalsIgnoreCase("0")) {//do not show field
                viewDimensions = LayoutInflater.from(getActivity()).inflate(viewIdDimensions, footerViewHolder.mLlBasicView, false);
                footerViewHolder.mLlBasicView.addView(viewDimensions);
                viewDimensions.setVisibility(View.GONE);
            } else if (pkgLoginDimensions.equalsIgnoreCase("1")) {// show field, input required
                viewDimensions = LayoutInflater.from(getActivity()).inflate(viewIdDimensions, footerViewHolder.mLlBasicView, false);
                footerViewHolder.mLlBasicView.addView(viewDimensions);
            } else if (pkgLoginDimensions.equalsIgnoreCase("2")) {//show field in “basic options” section
                viewDimensions = LayoutInflater.from(getActivity()).inflate(viewIdDimensions, footerViewHolder.mLlBasicView, false);
            } else if (pkgLoginDimensions.equalsIgnoreCase("3")) {// show field in “advanced options” section
                viewDimensions = LayoutInflater.from(getActivity()).inflate(viewIdDimensions, footerViewHolder.mLlAdvancedOptions, false);
                footerViewHolder.mLlAdvancedOptions.addView(viewDimensions);
            }
            dimensionsViewHolder = new DimensionsViewHolder(viewDimensions);
            dimensionsViewHolder.mStarDimensions.setVisibility(pkgLoginDimensions.equalsIgnoreCase("1") ? View.VISIBLE : View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*********************************PO Number*************/

        View viewPoNumber = null;
        pkgLoginPONumber = ntf_Preferences.get(Prefs_Keys.PKG_LOGIN_PONUMBER);
        int viewIdPoNumber = R.layout.layout_po_number;
        try {
            if (pkgLoginPONumber.equalsIgnoreCase("0")) {//do not show field
                viewPoNumber = LayoutInflater.from(getActivity()).inflate(viewIdPoNumber, footerViewHolder.mLlBasicView, false);
                footerViewHolder.mLlBasicView.addView(viewPoNumber);
                viewPoNumber.setVisibility(View.GONE);
            } else if (pkgLoginPONumber.equalsIgnoreCase("1")) {// show field, input required
                viewPoNumber = LayoutInflater.from(getActivity()).inflate(viewIdPoNumber, footerViewHolder.mLlBasicView, false);
                footerViewHolder.mLlBasicView.addView(viewPoNumber);
            } else if (pkgLoginPONumber.equalsIgnoreCase("2")) {//show field in “basic options” section
                viewPoNumber = LayoutInflater.from(getActivity()).inflate(viewIdPoNumber, footerViewHolder.mLlBasicView, false);
            } else if (pkgLoginPONumber.equalsIgnoreCase("3")) {// show field in “advanced options” section
                viewPoNumber = LayoutInflater.from(getActivity()).inflate(viewIdPoNumber, footerViewHolder.mLlAdvancedOptions, false);
                footerViewHolder.mLlAdvancedOptions.addView(viewPoNumber);
            }
            poNumberViewHolder = new PONumberViewHolder(viewPoNumber);
            poNumberViewHolder.mStarPONumber.setVisibility(pkgLoginPONumber.equalsIgnoreCase("1") ? View.VISIBLE : View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*********************************Shelf*************/
        View viewShelf = null;
        pkgLoginShelf = ntf_Preferences.get(Prefs_Keys.PKG_LOGIN_SHELF);
        int viewIdShelf = R.layout.layout_shelf;
        try {
            if (pkgLoginShelf.equalsIgnoreCase("0")) {//do not show field
                viewShelf = LayoutInflater.from(getActivity()).inflate(viewIdShelf, footerViewHolder.mLlBasicView, false);
                footerViewHolder.mLlBasicView.addView(viewShelf);
                viewShelf.setVisibility(View.GONE);
            } else if (pkgLoginShelf.equalsIgnoreCase("1")) {// show field, input required
                viewShelf = LayoutInflater.from(getActivity()).inflate(viewIdShelf, footerViewHolder.mLlBasicView, false);
                footerViewHolder.mLlBasicView.addView(viewShelf);
            } else if (pkgLoginShelf.equalsIgnoreCase("2")) {//show field in “basic options” section
                viewShelf = LayoutInflater.from(getActivity()).inflate(viewIdShelf, footerViewHolder.mLlBasicView, false);
            } else if (pkgLoginShelf.equalsIgnoreCase("3")) {// show field in “advanced options” section
                viewShelf = LayoutInflater.from(getActivity()).inflate(viewIdShelf, footerViewHolder.mLlAdvancedOptions, false);
                footerViewHolder.mLlAdvancedOptions.addView(viewShelf);
            }
            shelfViewHolder = new ShelfViewHolder(viewShelf);
            shelfViewHolder.mStarShelf.setVisibility(pkgLoginShelf.equalsIgnoreCase("1") ? View.VISIBLE : View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();

        }

        /*********************************Special Handling*************/

        View viewSpecialHandling = null;
        if (ntf_Preferences.get(Prefs_Keys.PKG_LOGIN_SPECIAL_HANDLING) == null) {
            ntf_Preferences.save(Prefs_Keys.PKG_LOGIN_SPECIAL_HANDLING, "");
        }
        pkgLoginSpecialHandling = ntf_Preferences.get(Prefs_Keys.PKG_LOGIN_SPECIAL_HANDLING);
        int viewIdSpecialHandling = R.layout.specialhandlings_layout;
        if (specialHandlings == null || specialHandlings.size() == 0) {
            specialHandlings = SpecialHandling.getSpecialHandling(NTF_Utils.getGlobalData(getActivity()), getActivity()!= null?getActivity():activity);
        }
        try {
            if (pkgLoginSpecialHandling.equalsIgnoreCase("0")) {//do not show field
                viewSpecialHandling = LayoutInflater.from(getActivity()).inflate(viewIdSpecialHandling, footerViewHolder.mLlBasicView, false);
                footerViewHolder.mLlBasicView.addView(viewSpecialHandling);
                viewSpecialHandling.setVisibility(View.GONE);
            } else if (pkgLoginSpecialHandling.equalsIgnoreCase("1")) {// show field, input required
                viewSpecialHandling = LayoutInflater.from(getActivity()).inflate(viewIdSpecialHandling, footerViewHolder.mLlBasicView, false);
                footerViewHolder.mLlBasicView.addView(viewSpecialHandling);
            } else if (pkgLoginSpecialHandling.equalsIgnoreCase("2")) {//show field in “basic options” section
                viewSpecialHandling = LayoutInflater.from(getActivity()).inflate(viewIdSpecialHandling, footerViewHolder.mLlBasicView, false);
            } else if (pkgLoginSpecialHandling.equalsIgnoreCase("3")) {// show field in “advanced options” section
                viewSpecialHandling = LayoutInflater.from(getActivity()).inflate(viewIdSpecialHandling, footerViewHolder.mLlAdvancedOptions, false);
                footerViewHolder.mLlAdvancedOptions.addView(viewSpecialHandling);
            }
            specialHandlingViewHolder = new SpecialHandlingHolder(viewSpecialHandling);
            updateSpecialHandlings();
//            SpecialHandlingsAdapter specialHandlingsAdapter = new SpecialHandlingsAdapter(getActivity(), specialHandlings);
//            GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), isTablet ? 4 : 2);
//            specialHandlingViewHolder.specialHandlingRecyclerView.setLayoutManager(layoutManager);
//            specialHandlingViewHolder.specialHandlingRecyclerView.setAdapter(specialHandlingsAdapter);
            specialHandlingViewHolder.star_SH.setVisibility(pkgLoginSpecialHandling.equalsIgnoreCase("1") ? View.VISIBLE : View.INVISIBLE);
//            if (specialHandlings.size() == 0 && specialHandlingViewHolder.specialhandlingdummylayout != null) {
//                specialHandlingViewHolder.specialhandlingdummylayout.setVisibility(View.VISIBLE);
//            } else if (specialHandlingViewHolder.specialhandlingdummylayout != null) {
//                specialHandlingViewHolder.specialhandlingdummylayout.setVisibility(View.GONE);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*********************************Custom Message*************/

        View viewCustomMessage = null;
        if (ntf_Preferences.get(Prefs_Keys.PKG_LOGIN_CUSTOM_MESSAGE) == null) {
            ntf_Preferences.save(Prefs_Keys.PKG_LOGIN_CUSTOM_MESSAGE, "");
        }
        pkgLoginCustomMessage = ntf_Preferences.get(Prefs_Keys.PKG_LOGIN_CUSTOM_MESSAGE);
        int viewIdCustomMessage = R.layout.layout_custom_message;
        try {
            if (pkgLoginCustomMessage.equalsIgnoreCase("0")) {//do not show field
                viewCustomMessage = LayoutInflater.from(getActivity()).inflate(viewIdCustomMessage, footerViewHolder.mLlBasicView, false);
                footerViewHolder.mLlBasicView.addView(viewCustomMessage);
                viewCustomMessage.setVisibility(View.GONE);
            } else if (pkgLoginCustomMessage.equalsIgnoreCase("1")) {// show field, input required
                viewCustomMessage = LayoutInflater.from(getActivity()).inflate(viewIdCustomMessage, footerViewHolder.mLlBasicView, false);
                footerViewHolder.mLlBasicView.addView(viewCustomMessage);
            } else if (pkgLoginCustomMessage.equalsIgnoreCase("2")) {//show field in “basic options” section
                viewCustomMessage = LayoutInflater.from(getActivity()).inflate(viewIdCustomMessage, footerViewHolder.mLlBasicView, false);
            } else if (pkgLoginCustomMessage.equalsIgnoreCase("3")) {// show field in “advanced options” section
                viewCustomMessage = LayoutInflater.from(getActivity()).inflate(viewIdCustomMessage, footerViewHolder.mLlAdvancedOptions, false);
                footerViewHolder.mLlAdvancedOptions.addView(viewCustomMessage);
            }
            customMessageViewHolder = new CustomMessageViewHolder(viewCustomMessage);
            customMessageViewHolder.mStarCustomMessage.setVisibility(pkgLoginCustomMessage.equalsIgnoreCase("1") ? View.VISIBLE : View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*********************************Login Pictures*************/

        View viewLoginPictures = null;
        if (ntf_Preferences.get(Prefs_Keys.PKG_LOGIN_PICTURES) == null) {
            ntf_Preferences.save(Prefs_Keys.PKG_LOGIN_PICTURES, "");
        }
        pkgLoginPictures = ntf_Preferences.get(Prefs_Keys.PKG_LOGIN_PICTURES);
        int viewIdLoginPictures = R.layout.layout_take_picture;
        try {
            if (pkgLoginPictures.equalsIgnoreCase("0")) {//do not show field
                viewLoginPictures = LayoutInflater.from(getActivity()).inflate(viewIdLoginPictures, footerViewHolder.mLlBasicView, false);
                footerViewHolder.mLlBasicView.addView(viewLoginPictures);
                viewLoginPictures.setVisibility(View.GONE);
            } else if (pkgLoginPictures.equalsIgnoreCase("1")) {// show field, input required
                viewLoginPictures = LayoutInflater.from(getActivity()).inflate(viewIdLoginPictures, footerViewHolder.mLlBasicView, false);
                footerViewHolder.mLlBasicView.addView(viewLoginPictures);
            } else if (pkgLoginPictures.equalsIgnoreCase("2")) {//show field in “basic options” section
                viewLoginPictures = LayoutInflater.from(getActivity()).inflate(viewIdLoginPictures, footerViewHolder.mLlBasicView, false);
            } else if (pkgLoginPictures.equalsIgnoreCase("3")) {// show field in “advanced options” section
                viewLoginPictures = LayoutInflater.from(getActivity()).inflate(viewIdLoginPictures, footerViewHolder.mLlAdvancedOptions, false);
                footerViewHolder.mLlAdvancedOptions.addView(viewLoginPictures);
            }
            loginPicturesViewHolder = new LoginPicturesViewHolder(viewLoginPictures);
            loginPicturesViewHolder.mStarTakePicture.setVisibility(pkgLoginPictures.equalsIgnoreCase("1") ? View.VISIBLE : View.INVISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (pkgLoginCarrier != null && !pkgLoginCarrier.isEmpty()) {
            if (pkgLoginCarrier.equalsIgnoreCase("2")) {//show field in “basic options” section
                footerViewHolder.mLlBasicView.addView(viewCarrier);
            }
        }

        if (pkgLoginSender != null && !pkgLoginSender.isEmpty()) {
            if (pkgLoginSender.equalsIgnoreCase("2")) {//show field in “basic options” section
                footerViewHolder.mLlBasicView.addView(viewSender);
                viewSender.setVisibility(View.VISIBLE);
            }
        }

        if (pkgLoginServiceType != null && !pkgLoginServiceType.isEmpty()) {
            if (pkgLoginServiceType.equalsIgnoreCase("2")) {//show field in “basic options” section
                footerViewHolder.mLlBasicView.addView(viewServiceType);
            }
        }

        if (pkgLoginPkgType != null && !pkgLoginPkgType.isEmpty()) {
            if (pkgLoginPkgType.equalsIgnoreCase("2")) {//show field in “basic options” section
                footerViewHolder.mLlBasicView.addView(viewPkgTypeStatus);
                viewPkgTypeStatus.setVisibility(View.VISIBLE);
            }
        }

        if (pkgLoginCondition != null && !pkgLoginCondition.isEmpty()) {
            if (pkgLoginCondition.equalsIgnoreCase("2")) {//show field in “basic options” section
                footerViewHolder.mLlBasicView.addView(viewCondition);
            }
        }

        if (pkgLoginWeight != null && !pkgLoginWeight.isEmpty()) {
            if (pkgLoginWeight.equalsIgnoreCase("2")) {//show field in “basic options” section
                footerViewHolder.mLlBasicView.addView(viewWeight);
            }
        }

        if (pkgLoginDimensions != null && !pkgLoginDimensions.isEmpty()) {
            if (pkgLoginDimensions.equalsIgnoreCase("2")) {//show field in “basic options” section
                footerViewHolder.mLlBasicView.addView(viewDimensions);
            }
        }

        if (pkgLoginPONumber != null && !pkgLoginPONumber.isEmpty()) {
            if (pkgLoginPONumber.equalsIgnoreCase("2")) {//show field in “basic options” section
                footerViewHolder.mLlBasicView.addView(viewPoNumber);
            }
        }

        if (pkgLoginShelf != null && !pkgLoginShelf.isEmpty()) {
            if (pkgLoginShelf.equalsIgnoreCase("2")) {//show field in “basic options” section
                footerViewHolder.mLlBasicView.addView(viewShelf);
            }
        }

        if (pkgLoginSpecialHandling != null && !pkgLoginSpecialHandling.isEmpty()) {
            if (pkgLoginSpecialHandling.equalsIgnoreCase("2")) {//show field in “basic options” section
                footerViewHolder.mLlBasicView.addView(viewSpecialHandling);
            }
        }

        if (pkgLoginCustomMessage != null && !pkgLoginCustomMessage.isEmpty()) {
            if (pkgLoginCustomMessage.equalsIgnoreCase("2")) {//show field in “basic options” section
                footerViewHolder.mLlBasicView.addView(viewCustomMessage);
            }
        }

        if (pkgLoginPictures != null && !pkgLoginPictures.isEmpty()) {
            if (pkgLoginPictures.equalsIgnoreCase("2")) {//show field in “basic options” section
                footerViewHolder.mLlBasicView.addView(viewLoginPictures);
            }
        }
        try {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_pakg_other_views, footerViewHolder.mLlAdvancedOptions, false);
            footerViewHolder.mLlAdvancedOptions.addView(view);
            pkgOtherViewsViewHolder = new PkgOtherViewsViewHolder(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getSpinnerSelectedValue(Spinner spinner) {
        String value = "";
        if (spinner != null && spinner.getSelectedItem() != null) {
            value = ((SpinnerData) spinner.getSelectedItem()).getValue();
        }
        return value;
    }

    private boolean isFieldsValid() {
        try {
            mMailRoom = getSpinnerSelectedValue(footerViewHolder.spinnerMailRoom);
            mCarrier = (mCarrier == null || mCarrier.isEmpty()) ? getSpinnerSelectedValue(carrierViewHolder.spinnerCarrier) : mCarrier;
            mServiceType = getSpinnerSelectedValue(serviceTypeViewHolder.mSpinnerServiceType);
            mPkgType = getSpinnerSelectedValue(packageTypeViewHolder.mSpinnerPackageType);
            mPkgCondition = getSpinnerSelectedValue(pkgConditionViewHolder.mSpinnerPackageCondition);
            mPkgStatus = getSpinnerSelectedValue(pkgOtherViewsViewHolder.mSpinnerPackageStatus);
            mSendNoti = getSpinnerSelectedValue(pkgOtherViewsViewHolder.mSpinnerSendNotification);
            if (mRecipient == null && TextUtils.isEmpty(headerViewHolder.editTextRecipientName.getText().toString().trim())
                    && TextUtils.isEmpty(headerViewHolder.editTextRecipientName.getText().toString().trim())) {
                NTF_Utils.showAlert(getActivity(), "", "Please enter recipient name.", null);
                return false;
            }
            if (mRecipient == null || mRecipient.getRecipientId() == null) {
                NTF_Utils.showAlert(getActivity(), "", "Please enter valid recipient name.", null);
                return false;
            }
            if (dataSet.get(0).getTrackingNumber() == null || dataSet.get(0).getTrackingNumber().trim().isEmpty()) {
                NTF_Utils.showAlert(getActivity(), "", "Please enter or scan tracking number.", null);
                return false;
            }
            /*if (footerViewHolder.ll_mailRoom.getVisibility() == View.VISIBLE && footerViewHolder.spinnerMailRoom.getSelectedItem() == null) {
                NTF_Utils.showAlert(getActivity(), "", "Please select mailroom.", null);
                return false;
            }*/
            if (pkgLoginCarrier.equalsIgnoreCase("1") && (mCarrier == null || mCarrier.trim().isEmpty())) {
                NTF_Utils.showAlert(getActivity(), "", "Please select carrier.", null);
                return false;
            }
            if (pkgLoginSender.equalsIgnoreCase("1") && TextUtils.isEmpty(senderViewHolder.mEditTextSender.getText().toString().trim())) {
                NTF_Utils.showAlert(getActivity(), "", "Please enter sender.", null);
                return false;
            }
            if (pkgLoginServiceType.equalsIgnoreCase("1") && (mServiceType == null || mServiceType.trim().isEmpty())) {
                NTF_Utils.showAlert(getActivity(), "", "Please select service type.", null);
                return false;
            }
            if (pkgLoginPkgType.equalsIgnoreCase("1") && (mPkgType == null || mPkgType.trim().isEmpty())) {
                NTF_Utils.showAlert(getActivity(), "", "Please select package type.", null);
                return false;
            }
            if (pkgLoginCondition.equalsIgnoreCase("1") && (mPkgCondition == null || mPkgCondition.trim().isEmpty())) {
                NTF_Utils.showAlert(getActivity(), "", "Please select package condition.", null);
                return false;
            }
            if (pkgLoginWeight.equalsIgnoreCase("1") && TextUtils.isEmpty(weightViewHolder.mEditTextWeight.getText().toString().trim())) {
                NTF_Utils.showAlert(getActivity(), "", "Please enter weight.", null);
                return false;
            }
            if (pkgLoginDimensions.equalsIgnoreCase("1") && TextUtils.isEmpty(dimensionsViewHolder.mEditTextDimensions.getText().toString().trim())) {
                NTF_Utils.showAlert(getActivity(), "", "Please enter dimensions.", null);
                return false;
            }
            if (pkgLoginPONumber.equalsIgnoreCase("1") && TextUtils.isEmpty(poNumberViewHolder.mEditTextPONumber.getText().toString().trim())) {
                NTF_Utils.showAlert(getActivity(), "", "Please enter PO number.", null);
                return false;
            }
            if (pkgLoginShelf.equalsIgnoreCase("1") && TextUtils.isEmpty(shelfViewHolder.mEditShelfLocation.getText().toString().trim())) {
                NTF_Utils.showAlert(getActivity(), "", "Please enter shelf location.", null);
                return false;
            }
            if (pkgLoginSpecialHandling.equalsIgnoreCase("1")) {
                boolean isSelected = false;
                for (SpecialHandling specialHandling : specialHandlings) {
                    if (specialHandling.isSelected()) {
                        isSelected = specialHandling.isSelected();
                        break;
                    }
                }
                if (!isSelected) {
                    NTF_Utils.showAlert(getActivity(), "", "Please select a special handling.", null);
                    return false;
                }
            }
            if (pkgLoginCustomMessage.equalsIgnoreCase("1") && customMessageViewHolder.mEditTextCustomMessage.getText().toString().trim().isEmpty()) {
                NTF_Utils.showAlert(getActivity(), "", "Please enter custom message.", null);
                return false;
            }
            if (pkgLoginPictures.equalsIgnoreCase("1") && bitmap1 == null && bitmap2 == null && bitmap3 == null) {
                NTF_Utils.showAlert(getActivity(), "", "Please take a picture.", null);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            NTF_Utils.showAlert(getActivity(), "", "Some thing went wrong, please try again later.", null);
            return false;
        }
        return true;
    }

    private void setSenderData() {
        try {
            mSenderList = SenderData.getSenderList(mGobalJsonData.optJSONArray("all_sender_details"));
            if (mSenderList != null && !mSenderList.isEmpty()) {
                SenderAdapter senderDataAdapter = new SenderAdapter(getActivity(), R.layout.spinner_item, mSenderList);
                senderViewHolder.mEditTextSender.setAdapter(senderDataAdapter);
                senderViewHolder.mEditTextSender.setThreshold(2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setShelfData() {
        try {
            mShelfList = ShelfData.getShelfDetailsList(mGobalJsonData.optJSONArray("all_shelf_details"));
            if (mShelfList != null && !mShelfList.isEmpty()) {
                ShelfAdapter shelfDataAdapter = new ShelfAdapter(getActivity(), R.layout.spinner_item, mShelfList);
                shelfViewHolder.mEditShelfLocation.setAdapter(shelfDataAdapter);
                shelfViewHolder.mEditShelfLocation.setThreshold(2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
