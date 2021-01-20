package com.notifii.notifiiapp.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.CompositePermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.karumi.dexter.listener.single.SnackbarOnDeniedPermissionListener;
import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.activities.MainActivity;
import com.notifii.notifiiapp.activities.ManateeBarcodeScannerActivity;
import com.notifii.notifiiapp.adapters.SpinnerHintAdapter;
import com.notifii.notifiiapp.adapters.SpinnerMarkAdapter;
import com.notifii.notifiiapp.base.NTF_BaseFragment;
import com.notifii.notifiiapp.models.SpinnerData;
import com.notifii.notifiiapp.utils.NTF_Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchPackageFragment extends NTF_BaseFragment {

    @BindView(R.id.staff_notes)
    public EditText staffNotesET;
    @BindView(R.id.recipient_name)
    public EditText recipientNameET;
    @BindView(R.id.tracking_number_search)
    public EditText trackingNumberET;
    @BindView(R.id.mailbox_no)
    public EditText mailboxNumberET;
    @BindView(R.id.custom_message)
    public EditText customMessageET;
    @BindView(R.id.tag_number)
    public EditText tagNumberET;
    @BindView(R.id.shelf)
    public EditText shelfET;
    @BindView(R.id.sender)
    public EditText senderET;
    @BindView(R.id.po_number)
    public EditText poNumberET;
    @BindView(R.id.search_logic_spinner)
    public Spinner searchLogicSpinner;
    @BindView(R.id.date_range_spinner)
    public Spinner dateRangeSpinner;
    @BindView(R.id.pkgTypeButtonDownArrow)
    public ImageView searchLogicButtonDownArrow;
    @BindView(R.id.show_advanced_tv)
    public TextView showAdvancedOptionTV;
    @BindView(R.id.show_advanced_layout)
    public LinearLayout showAdvancedLayout;
    @BindView(R.id.search_btn)
    public Button searchButton;
    @BindView(R.id.mailboxNumberText)
    public TextView mailboxNumberTV;
    @BindView(R.id.search_logic_tv)
    public TextView searchLogicTV;
    @BindView(R.id.date_range_tv)
    public TextView dateRangeTv;
    @BindView(R.id.barcode_scanner_iv)
    public ImageView barcodeScannerIV;
    @BindView(R.id.parentLayout)
    LinearLayout parentLayout;
    /*    @BindView(R.id.search_logic_linearL)
            LinearLayout searchLogicLinearLayout;*/
    @BindView(R.id.layout_shelf)
    LinearLayout shelfLayout;
    @BindView(R.id.layout_poNumber)
    LinearLayout poNumberLayout;
    @BindView(R.id.search_logic_frame)
    FrameLayout searchLogicFL;

    Activity activity;
    private static final int PERMISSIONS_REQUEST_CAMERA_CONSTANT = 1;
    public static boolean isSearchPkgFragment;
    private String mSearchLogic, mDateRange;
    private boolean showAdvLayOptions = false;
    private boolean isCameraOpen;
    private boolean isFromBarcodeScanner;
    public static boolean needToResetSpinnerData = false;
    ArrayList<SpinnerData> mSearchLogicList = new ArrayList();
    ArrayList<SpinnerData> mDateRangeList = new ArrayList();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_search_package, container, false);
        ButterKnife.bind(this, mainView);
        return mainView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews();
        clearFileds();
    }

    @Override
    public void onAttach(Context context) {
        this.activity = getActivity();
        super.onAttach(context);
    }

    @Override
    public void onStackChanged() {
        super.onStackChanged();
        if (NTF_Utils.getCurrentFragment(this) != null && NTF_Utils.getCurrentFragment(this) instanceof SearchPackageFragment) {
            onFragmentPopup(true);
        } else {
            onFragmentPopup(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (needToResetSpinnerData) {
            needToResetSpinnerData = false;
            if (dateRangeSpinner != null) {
                dateRangeSpinner.setSelection(1);
                mDateRange = ((SpinnerData) dateRangeSpinner.getSelectedItem()).getValue();
            }
            if (searchLogicSpinner != null) {
                searchLogicSpinner.setSelection(1);
                mSearchLogic = ((SpinnerData) searchLogicSpinner.getSelectedItem()).getValue();
            }
        }
        isSearchPkgFragment = true;
//        if (!isFromBarcodeScanner) {
//            clearFileds();
//        } else {
//            isFromBarcodeScanner = false;
//        }
    }

    private void bindViews() {
        setToolbarTitle("");
        setToolbarActionButtons(false, false, 0);
        setLogo(R.drawable.ic_notifii_track_white_logo);
        setAppIcon(false);
        String recipientTypeLable = NTF_Utils.getRecipientAddress1Label(ntf_Preferences.get(Prefs_Keys.ACCOUNT_TYPE));
        mailboxNumberTV.setText("" + recipientTypeLable);
        mailboxNumberET.setContentDescription(recipientTypeLable);
        showAdvancedLayout.setVisibility(View.GONE);
        searchButton.setVisibility(View.VISIBLE);
        setSearchLogicSpinnerData();
        setDateRangeSpinnerData();

        handlePrivileges();

        String strShelf = ntf_Preferences.get(Prefs_Keys.PKG_LOGIN_SHELF);
        try {
            if (strShelf.equalsIgnoreCase("0")) {//do not show field
                shelfLayout.setVisibility(View.GONE);
            } else {
                shelfLayout.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        String strPONumber = ntf_Preferences.get(Prefs_Keys.PKG_LOGIN_PONUMBER);
        try {
            if (strPONumber.equalsIgnoreCase("0")) {//do not show field
                poNumberLayout.setVisibility(View.GONE);
            } else {
                poNumberLayout.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSearchLogicSpinnerData() {
        try {
            mSearchLogicList = SpinnerData.getListSearchLogic(false);
            if (mSearchLogicList != null && !mSearchLogicList.isEmpty()) {
                if (mSearchLogicList.size() >= 10) {
                    NTF_Utils.setSpinnerHeight(getActivity(), searchLogicSpinner);
                }
                final SpinnerMarkAdapter searchLogicAdapter = new SpinnerMarkAdapter(getActivity(), mSearchLogicList);
                searchLogicSpinner.setAdapter(new SpinnerHintAdapter(searchLogicAdapter, R.layout.spinner_hint, getActivity()));
                //searchLogicSpinner.setSelection(1);
                searchLogicTV.setText(mSearchLogicList.get(0).getName());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handlePrivileges() {
        if (ntf_Preferences.get(Prefs_Keys.PRIVILEGE_TRACK_PACKAGES).equalsIgnoreCase("n")) {
            ((MainActivity) (getActivity() != null ? getActivity() : activity)).enableTransparentLayer();
            parentLayout.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
            NTF_Utils.showInSufficientPrivelegeAlert(getActivity() != null ? getActivity() : activity);
        } else {
            ((MainActivity) (getActivity() != null ? getActivity() : activity)).disableTransparentLayer();
        }
    }

    private void setDateRangeSpinnerData() {
        try {
            mDateRangeList = SpinnerData.getListDateRange();
            if (mDateRangeList != null && !mDateRangeList.isEmpty()) {
                if (mDateRangeList.size() >= 10) {
                    NTF_Utils.setSpinnerHeight(getActivity(), dateRangeSpinner);
                }
                final SpinnerMarkAdapter dateRangeAdapter = new SpinnerMarkAdapter(getActivity(), mDateRangeList);
                dateRangeSpinner.setAdapter(new SpinnerHintAdapter(dateRangeAdapter, R.layout.spinner_hint, getActivity()));
                //searchLogicSpinner.setSelection(1);
                dateRangeTv.setText(mDateRangeList.get(0).getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doAllViewsClickableFalse() {
        if (barcodeScannerIV != null) {
            barcodeScannerIV.setClickable(false);
            doOnTouchFalse(barcodeScannerIV);
        }

        if (trackingNumberET != null) {
            trackingNumberET.setClickable(false);
            doOnTouchFalse(trackingNumberET);
        }

        if (recipientNameET != null) {
            recipientNameET.setClickable(false);
            doOnTouchFalse(recipientNameET);
        }

        if (mailboxNumberET != null) {
            mailboxNumberET.setClickable(false);
            doOnTouchFalse(mailboxNumberET);
        }
        if (senderET != null) {
            senderET.setClickable(false);
            doOnTouchFalse(senderET);
        }
        if (shelfET != null) {
            shelfET.setClickable(false);
            doOnTouchFalse(shelfET);
        }
        if (tagNumberET != null) {
            tagNumberET.setClickable(false);
            doOnTouchFalse(tagNumberET);
        }
        if (customMessageET != null) {
            customMessageET.setClickable(false);
            doOnTouchFalse(customMessageET);
        }

        if (staffNotesET != null) {
            staffNotesET.setClickable(false);
            doOnTouchFalse(staffNotesET);
        }
        if (searchLogicSpinner != null) {
            searchLogicSpinner.setClickable(false);
            doOnTouchFalse(searchLogicSpinner);
        }
        if (searchLogicButtonDownArrow != null) {
            searchLogicButtonDownArrow.setClickable(false);
            doOnTouchFalse(searchLogicButtonDownArrow);
        }
        if (showAdvancedOptionTV != null) {
            showAdvancedOptionTV.setClickable(false);
            doOnTouchFalse(showAdvancedOptionTV);
        }
        if (searchButton != null) {
            searchButton.setClickable(false);
            doOnTouchFalse(searchButton);
        }
        if (poNumberET != null) {
            poNumberET.setClickable(false);
            doOnTouchFalse(poNumberET);
        }
    }

    public void doOnTouchFalse(View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    private void clearFileds() {
        try {
            if (mailboxNumberET != null)
                mailboxNumberET.post(new Runnable() {
                    @Override
                    public void run() {
                        mailboxNumberET.setText("");
                    }
                });
            if (trackingNumberET != null)
                trackingNumberET.post(new Runnable() {
                    @Override
                    public void run() {
                        trackingNumberET.setText("");
                    }
                });
            if (recipientNameET != null)
                recipientNameET.post(new Runnable() {
                    @Override
                    public void run() {
                        recipientNameET.setText("");
                    }
                });
            if (senderET != null)
                senderET.post(new Runnable() {
                    @Override
                    public void run() {
                        senderET.setText("");
                    }
                });
            if (shelfET != null)
                shelfET.post(new Runnable() {
                    @Override
                    public void run() {
                        shelfET.setText("");
                    }
                });
            if (tagNumberET != null)
                tagNumberET.post(new Runnable() {
                    @Override
                    public void run() {
                        tagNumberET.setText("");
                    }
                });
            if (customMessageET != null)
                customMessageET.post(new Runnable() {
                    @Override
                    public void run() {
                        customMessageET.setText("");
                    }
                });
            if (staffNotesET != null)
                staffNotesET.post(new Runnable() {
                    @Override
                    public void run() {
                        staffNotesET.setText("");
                    }
                });
            if (poNumberET != null)
                poNumberET.post(new Runnable() {
                    @Override
                    public void run() {
                        poNumberET.setText("");
                    }
                });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.show_advanced_tv)
    public void onclickshowAdvaced() {
        if (showAdvLayOptions) {
            showAdvLayOptions = false;
            showAdvancedLayout.setVisibility(View.GONE);
            showAdvancedOptionTV.setContentDescription("Expand Advanced Options Button");
        } else {
            showAdvLayOptions = true;
            showAdvancedLayout.setVisibility(View.VISIBLE);
            showAdvancedOptionTV.setContentDescription("Collapse Advanced Options Button");
        }
    }

    @OnClick({R.id.search_logic_tv, R.id.pkgTypeButtonDownArrow})
    public void onClickSearchlogic() {
        searchLogicSpinner.performClick();
    }

    @OnClick(R.id.date_range_linearL)
    public void onClickDateRange() {
        if (!ntf_Preferences.get(Prefs_Keys.PRIVILEGE_TRACK_PACKAGES).equalsIgnoreCase("n"))
            dateRangeSpinner.performClick();
    }

    @OnClick(R.id.search_btn)
    public void onclickSearch_btn() {
        if (!NTF_Utils.isOnline(getActivity())) {
            NTF_Utils.showNoNetworkAlert(getActivity());
            return;
        }
        if (!(NTF_Utils.getCurrentFragment(this) instanceof SearchPackageFragment)) {
            return;
        }
        String trackingNumber = trackingNumberET.getText().toString().trim();
        String recipientName = recipientNameET.getText().toString().trim();
        String mailboxNumber = mailboxNumberET.getText().toString().trim();
        String sender = senderET.getText().toString().trim();
        String shelf = "";
        if (shelfET != null) {
            shelf = shelfET.getText().toString().trim();
        }

        String tagNumber = tagNumberET.getText().toString().trim();
        String customMessage = customMessageET.getText().toString().trim();
        String staffNotes = staffNotesET.getText().toString().trim();
        String poNumber = "";
        if (poNumberET != null) {
            poNumber = poNumberET.getText().toString().trim();
        }

        if (!showAdvLayOptions) {
            if (trackingNumber.isEmpty() && recipientName.isEmpty()) {
                NTF_Utils.showAlert(getActivity(), "", "Please provide at least one search parameter. ", null);
                return;
            }
        } else {
            if (trackingNumber.isEmpty() && recipientName.isEmpty() && mailboxNumber.isEmpty() && sender.isEmpty() && shelf.isEmpty() && tagNumber.isEmpty()
                    && customMessage.isEmpty() && staffNotes.isEmpty() && poNumber.isEmpty()) {
                NTF_Utils.showAlert(getActivity(), "", "Please provide at least one search parameter. ", null);
                return;
            }
        }

        PackageHistoryFragment fragment = new PackageHistoryFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Extras_Keys.IS_FROM_SEARCH_PACKAGE, true);
        bundle.putString(Extras_Keys.TRACKING_NUMBER, trackingNumber);
        bundle.putString(Extras_Keys.RECIPIENT_NAME, recipientName);
        bundle.putString(Extras_Keys.MAILBOX_NUMBER, mailboxNumber);
        bundle.putString(Extras_Keys.SENDER, sender);
        bundle.putString(Extras_Keys.SHELF, shelf);
        bundle.putString(Extras_Keys.TAG_NUMBER, tagNumber);
        bundle.putString(Extras_Keys.CUSTOM_MESSAGE, customMessage);
        bundle.putString(Extras_Keys.STAFF_NOTES, staffNotes);
        bundle.putString(Extras_Keys.PO_NUMBER, poNumber);
        bundle.putString(Extras_Keys.SEARCH_LOGIC, mSearchLogic);
        bundle.putString(Extras_Keys.DATE_RANGE, mDateRange);
        bundle.putBoolean(Extras_Keys.SHOW_ADVANCED_OPTIONS, showAdvLayOptions);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.add(R.id.realtabcontent, fragment);
        transaction.commitAllowingStateLoss();

        getActivity().getSupportFragmentManager().executePendingTransactions();
    }

    @OnClick(R.id.barcode_scanner_iv)
    public void onClickBarCodeScanner() {
        if (System.currentTimeMillis() - mLastClickTime < 500) {
            return;
        }
        mLastClickTime = System.currentTimeMillis();
        openCamera();
    }

    @OnItemSelected(R.id.search_logic_spinner)
    public void onItemSearchLogicSelect() {
        NTF_Utils.hideKeyboard(getActivity());
        if (searchLogicSpinner.getSelectedItem() != null) {
            mSearchLogic = ((SpinnerData) searchLogicSpinner.getSelectedItem()).getValue();
            Log.v("Search Logic : ", "VALUE" + ((SpinnerData) searchLogicSpinner.getSelectedItem()).getValue());
            searchLogicTV.setText(((SpinnerData) searchLogicSpinner.getSelectedItem()).getName());
            Log.v("Search Logic : ", "Name" + ((SpinnerData) searchLogicSpinner.getSelectedItem()).getName());
            SpinnerData.resetData(mSearchLogicList);
            ((SpinnerData) searchLogicSpinner.getSelectedItem()).setSelected(true);
        }
    }

    @OnItemSelected(R.id.date_range_spinner)
    public void onItemDateRangeSelect() {
        NTF_Utils.hideKeyboard(getActivity());
        if (dateRangeSpinner.getSelectedItem() != null) {
            mDateRange = ((SpinnerData) dateRangeSpinner.getSelectedItem()).getValue();
            dateRangeTv.setText(((SpinnerData) dateRangeSpinner.getSelectedItem()).getName());
            SpinnerData.resetData(mDateRangeList);
            ((SpinnerData) dateRangeSpinner.getSelectedItem()).setSelected(true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == PERMISSIONS_REQUEST_CAMERA_CONSTANT && resultCode == Activity.RESULT_OK && data != null) {
                final String scanResult = data.getExtras().getString(ManateeBarcodeScannerActivity.KEY_SCAN_RESULT);
                if (scanResult != null) {
                    trackingNumberET.setText(scanResult);
                } else {
                    NTF_Utils.showAlert(getActivity(), "", "Unable to get Scanned result", null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openCamera() {
        NTF_Utils.checkCameraPermissionToProgress(getActivity(), new Runnable() {
            @Override
            public void run() {
                ManateeBarcodeScannerActivity.isQrAlso=true;
                Intent i = new Intent(getActivity(), ManateeBarcodeScannerActivity.class);
                startActivityForResult(i, 1);
            }
        });
    }

}
