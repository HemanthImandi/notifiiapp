/////////////////////////////////////////////////////////////////
// ScanModeFragment.java
//
// Created by Annapoorna
// Notifii Project
//
//Copyright (c) 2016 Notifii, LLC. All rights reserved
/////////////////////////////////////////////////////////////////
package com.notifii.notifiiapp.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.activities.ManateeBarcodeScannerActivity;
import com.notifii.notifiiapp.adapters.PkgLogoutDetailsAdapter;
import com.notifii.notifiiapp.base.NTF_BaseFragment;
import com.notifii.notifiiapp.models.Package;
import com.notifii.notifiiapp.mvp.models.PackageLookUpRequest;
import com.notifii.notifiiapp.mvp.models.PackageLookUpResponse;
import com.notifii.notifiiapp.mvp.presenters.PkgLookUpPresenter;
import com.notifii.notifiiapp.mvp.views.PkgLookUpView;
import com.notifii.notifiiapp.utils.NTF_Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This class deals with the Scanning functionality...
 */
public class ScanModeFragment extends NTF_BaseFragment implements PkgLookUpView {

    @BindView(R.id.ScannnedrecyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.text_layout)
    public LinearLayout textLayout;

    public final int REQUSET_SCAN = 1;
    private ArrayList<Package> mPkgList = new ArrayList<>();
    PkgLogoutDetailsAdapter mAdapter;
    PkgLookUpPresenter pkgLookUpPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_scan_mode, null);
        ButterKnife.bind(this, mainView);
        return mainView;
    }

    @Override
    public void onStackChanged() {
        super.onStackChanged();
        if (NTF_Utils.getCurrentFragment(this) != null && NTF_Utils.getCurrentFragment(this) instanceof ScanModeFragment) {
            onFragmentPopup(true);
        } else {
            onFragmentPopup(false);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setToolbarTitle("Log Package Out");
        setToolbarActionButtons(true, false, 0);
        pkgLookUpPresenter = new PkgLookUpPresenter();
        pkgLookUpPresenter.attachMvpView(this);
        doScanAnotherPkg();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
    }

    @OnClick(R.id.buttonScanAnotherPkg)
    void onClickScanAnotherPkg(View view) {
        doScanAnotherPkg();
    }



    @OnClick(R.id.buttonSignOutPkg)
    void onClickSignOutPkg(View view) {
        if (mPkgList != null && !mPkgList.isEmpty()) {
            ArrayList<Package> pkgs = new ArrayList<Package>(mPkgList);
            PackageLogoutFragment fragment = PackageLogoutFragment.getInstance(pkgs);
            fragment.setTargetFragment(getTargetFragment(), LogPackageOutFragment.LOGOUTPKGREQUESTCODE);
            changeDetailsFragment(fragment);
        } else {
            NTF_Utils.showAlert(getActivity(), "", "Please scan a package", null);
        }
    }

    @OnClick({R.id.backImage, R.id.mobile_left_linear})
    void onClickBack(View view) {
        try {
            NTF_Utils.hideKeyboard(getActivity());
            getActivity().onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (data != null && resultCode == getActivity().RESULT_OK) {
                String scanResult = data.getExtras().getString(ManateeBarcodeScannerActivity.KEY_SCAN_RESULT);
                if (scanResult != null) {
                    packageLookUp(scanResult);
                } else {
                    NTF_Utils.showAlert(getActivity(), "", "Unable to get Scanned result", null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Preparing service request for getting packages..
    private void packageLookUp(String scannedData) {
        if (NTF_Utils.isOnline(getActivity())) {
            try {
                PackageLookUpRequest packageLookUpRequest = new PackageLookUpRequest();
                packageLookUpRequest.setSessionId(ntf_Preferences.get(Prefs_Keys.SESSION_ID));
                packageLookUpRequest.setUserId(ntf_Preferences.get(Prefs_Keys.USER_ID));
                packageLookUpRequest.setAuthenticationToken(ntf_Preferences.get(Prefs_Keys.AUTHENTICATION_TOKEN));
                packageLookUpRequest.setAccountId(ntf_Preferences.get(Prefs_Keys.ACCOUNT_ID));
                packageLookUpRequest.setScannedData(scannedData);
                NTF_Utils.showProgressDialog(getActivity());
                pkgLookUpPresenter.doPkgLookUp(null, packageLookUpRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            NTF_Utils.showNoNetworkAlert(getActivity());
        }
    }


    // Confirm alert for Logout Packages...
    private void confirmAlert(final int position) {
        try {
            int width = getResources().getDisplayMetrics().widthPixels;
            final Dialog dialog = new Dialog(getActivity(), R.style.DialogTheme);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.confirm_alert_layout);
            dialog.setCanceledOnTouchOutside(false);
            LinearLayout parentLayout = (LinearLayout) dialog.findViewById(R.id.parentLayout);
            // set the custom dialog components - text, image and button
            TextView mTitle = (TextView) dialog.findViewById(R.id.textView_title);
            TextView mMessage = (TextView) dialog.findViewById(R.id.textView_message);
            TextView positiveBtn = (TextView) dialog.findViewById(R.id.positive_button);
            TextView negetiveBtn = (TextView) dialog.findViewById(R.id.negetive_button);

            mTitle.setText("Umm.");
            mTitle.setTextColor(getResources().getColor(R.color.info_color));
            mMessage.setText("Remove this package from the list?");
            negetiveBtn.setText("Remove");
            positiveBtn.setText("Cancel");

            negetiveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    removeFromList(position);
                    if (mPkgList.size() == 0) {
                        textLayout.setVisibility(View.VISIBLE);
                    }
                }
            });

            positiveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            int dialogWidth = (int) (width * 0.90f);
            if (getResources().getBoolean(R.bool.isTablet)) {
                dialogWidth = (int) (width * 0.85f);
            }
            int dialogHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(dialogWidth, dialogHeight);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This method used to remove the packages from list...
    private void removeFromList(int position) {
        try {
            if (mPkgList.size() == 0) {
                Log.v("mPkgList", "" + mPkgList.size() + " " + position);
                return;
            }
            mPkgList.remove(position);
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This method used to restart the Scanner if need to scan another packages...
    private void doScanAnotherPkg() {
        if (System.currentTimeMillis() - mLastClickTime < 500) {
            return;
        }
        mLastClickTime = System.currentTimeMillis();
        NTF_Utils.checkCameraPermissionToProgress(getActivity(), new Runnable() {
            @Override
            public void run() {
                ManateeBarcodeScannerActivity.isQrAlso=true;
                startActivityForResult(new Intent(getActivity(), ManateeBarcodeScannerActivity.class), REQUSET_SCAN);
            }
        });
    }


    @Override
    public void onSessionExpired(String message) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showSessionExpireAlert(message, getActivity(), ntf_Preferences);
    }

    @Override
    public void onPackageFound(PackageLookUpResponse response) {
        try {
            NTF_Utils.hideProgressDialog();
            Package mPkg = new Package();
            mPkg.setPackageId(response.getPackageId());
            mPkg.setRecipientName(response.getRecipientName());
            mPkg.setTrackingNumber(response.getTrackingNumber());
            mPkg.setRecipientAddress1(response.getRecipientAddress1());

            boolean isPackageAlreadyAdded = false;
            for (int i = 0, len = mPkgList.size(); i < len; i++) {
                if (mPkgList.get(i).getPackageId().equals(mPkg.getPackageId())) {
                    isPackageAlreadyAdded = true;
                }
            }
            if (!isPackageAlreadyAdded) {
                mPkgList.add(mPkg);
            } else {
                NTF_Utils.showAlert(getActivity(), "", "Package already added to list.", null);
            }
            if (mPkgList.size() > 0) {
                textLayout.setVisibility(View.GONE);
            } else {
                textLayout.setVisibility(View.VISIBLE);
            }
            if (mAdapter == null) {
                mAdapter = new PkgLogoutDetailsAdapter(getActivity(), mPkgList, 1);
                mAdapter.setNoticeCloseListener(new PkgLogoutDetailsAdapter.NoticeItemCloseListener() {
                    @Override
                    public void onCloseClicked(int position) {
                        confirmAlert(position);
                    }
                });
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPackageNotFound(String message) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(getActivity(), "", message, null);
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
    
    public void handleBackButton(){
        try {
            NTF_Utils.hideKeyboard(getActivity());
            getTargetFragment().onActivityResult(
                    getTargetRequestCode(),
                    Activity.RESULT_OK,
                    new Intent().putExtra(Extras_Keys.REFRESH_LOG_PACKAGE_OUT_LIST, true));
        }catch (Exception e){e.printStackTrace();}
    }

}