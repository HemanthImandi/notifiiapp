package com.notifii.notifiiapp.fragments;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTouch;
import butterknife.Optional;

import android.os.Handler;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.activities.DrawSignatureActivity;
import com.notifii.notifiiapp.activities.MainActivity;
import com.notifii.notifiiapp.adapters.PkgLogoutDetailsAdapter;
import com.notifii.notifiiapp.adapters.SpinnerAdapter;
import com.notifii.notifiiapp.adapters.SpinnerHintAdapter;
import com.notifii.notifiiapp.adapters.SpinnerMarkAdapter;
import com.notifii.notifiiapp.base.NTF_BaseFragment;
import com.notifii.notifiiapp.base.NotifiiApplication;
import com.notifii.notifiiapp.customui.SignaturePad;
import com.notifii.notifiiapp.helpers.BitmapHelper;
import com.notifii.notifiiapp.helpers.SingleTon;
import com.notifii.notifiiapp.models.Package;
import com.notifii.notifiiapp.models.SpinnerData;
import com.notifii.notifiiapp.mvp.models.LogPackageDataRequest;
import com.notifii.notifiiapp.mvp.models.LogPkgImagesRequest;
import com.notifii.notifiiapp.mvp.models.PackagePicture;
import com.notifii.notifiiapp.mvp.presenters.LogOutPackageDetailsPresenter;
import com.notifii.notifiiapp.mvp.views.LogPkgDataView;
import com.notifii.notifiiapp.utils.NTF_Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PackageLogoutFragment extends NTF_BaseFragment implements LogPkgDataView {

    @BindView(R.id.PkgrecyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.textViewNoteLabel)
    TextView mTextViewNoteLabel;
    @BindView(R.id.editTextNote)
    EditText mEditTextNote;
    @BindView(R.id.buttonIamDone)
    Button mButtonIamDone;
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
    @BindView(R.id.spinnerPkcg)
    Spinner mSpinnerPkcg;
    @BindView(R.id.pkgStatusButtonDownArrow)
    ImageView mButtonDownArrow;
    private JSONObject mGobalJsonData;
    @BindView(R.id.gap1)
    LinearLayout mGap1;
    @BindView(R.id.gap2)
    LinearLayout mGap2;
    @BindView(R.id.gap3)
    LinearLayout mGap3;
    @BindView(R.id.sendNotificationsLL)
    LinearLayout sendNotificationsLL;
    @BindView(R.id.buttonAdvancedOption)
    TextView buttonAdvancedOption;
    @BindView(R.id.spinnerSendNotification)
    Spinner sendNotiSpinnner;
    private TextView mTextViewSelectedPackages;
    private LayoutInflater inflater;
    @Nullable
    @BindView(R.id.btn_click_to_sign)
    TextView mBtnClickToSign;
    @Nullable
    @BindView(R.id.signatureImageView)
    ImageView mSignatureImageView;
    @BindView(R.id.main_scrollview)
    ScrollView mScrollView;
    @Nullable
    @BindView(R.id.tab_sign_layout)
    FrameLayout mTabSignLayout;
    @BindView(R.id.mobile_sign_layout)
    FrameLayout mMobileSignLayout;
    @BindView(R.id.lable_signature)
    TextView mLableSignature;
    @BindView(R.id.mobile_clear_sign)
    ImageView mIvMobileClearSignature;
    @Nullable
    @BindView(R.id.tab_clear_sign)
    ImageView mIvTabClearSignature;
    @Nullable
    @BindView(R.id.signature_pad)
    SignaturePad mSignaturePad;
    private int width;
    @BindView(R.id.textViewPhotosLabel)
    TextView photosLable;
    @BindView(R.id.images_linear)
    LinearLayout mImagesLinear;
    @Nullable
    @BindView(R.id.textView_tab_pictures_label)
    TextView tabPicturesLabel;
    @Nullable
    @BindView(R.id.textView_sign_here)
    TextView mTvSignHere;
    @BindView(R.id.pkgStatusTV)
    TextView pkgStatusTV;
    @BindView(R.id.sendNotificationsTV)
    TextView sendNotificationsTV;

    private String base64Str1;
    private String base64Str2;
    private String base64Str3;
    private int clickPhotoRequestCode;
    private SpinnerData spinnerData;
    private String str_signature_base64string = "";
    private boolean isTablet;
    LogOutPackageDetailsPresenter logOutPackageDetailsPresenter;
    private ArrayList<Package> mPkgList;
    private static final int DRAW_SIGNATURE_ACTIVITY_CONSTANT = 2;
    private PkgLogoutDetailsAdapter pkgLogoutDetailsAdapter;
    private Bitmap mSignatureBitmap;
    private ArrayList<TextView> mPkgCheckTextViewList = new ArrayList<>();
    private Bitmap bitmap1;
    private Bitmap bitmap2;
    private Bitmap bitmap3;
    private boolean isTenSecondsCompleted=false;
    private ArrayList<SpinnerData> pkgStatusList;
    private ArrayList<SpinnerData> mSendNoificationList;

    public static PackageLogoutFragment getInstance(ArrayList<Package> packages) {
        PackageLogoutFragment fragment = new PackageLogoutFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Extras_Keys.KEY_PACKAGE_LIST, packages);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        isTablet = getResources().getBoolean(R.bool.isDeviceTablet);
        mainView = inflater.inflate(R.layout.fragment_log_package_out_details, container, false);
        ButterKnife.bind(this, mainView);
        mGobalJsonData = NTF_Utils.getGlobalData(getActivity());
        mPkgList = bundle.getParcelableArrayList(Extras_Keys.KEY_PACKAGE_LIST);
        width = getResources().getDisplayMetrics().widthPixels;
        isTenSecondsCompleted=false;
        return mainView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        if (isTablet) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 10, 0, 0);
            view.findViewById(R.id.packagestatusTV).setLayoutParams(params);

            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, 0, 1.0f
            );
            params1.setMargins(10, 0, 10, 0);
            //recyclerView.setLayoutParams(params1);
        }

        setSpinnerData();
        bindToListView();
        mTextViewNoteLabel.setText("Staff Notes ");
        logOutPackageDetailsPresenter = new LogOutPackageDetailsPresenter();
        logOutPackageDetailsPresenter.attachMvpView(this);
    }

    @Override
    public void onStackChanged() {
        super.onStackChanged();
        if (NTF_Utils.getCurrentFragment(this) != null && NTF_Utils.getCurrentFragment(this) instanceof PackageLogoutFragment) {
            onFragmentPopup(true);
        } else {
            onFragmentPopup(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (isTenSecondsCompleted) {
                isTenSecondsCompleted=false;
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.remove(NTF_Utils.getCurrentFragment(this));
                ft.commit();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // Identifying the views in layout..
    private void findViews(View rootView) {
        // findToolbarViews(rootView);
        setToolbarTitle("Log Package Out");
        setToolbarActionButtons(true, false, 0);


        mTextViewSelectedPackages = (TextView) rootView.findViewById(R.id.textViewSelectedPackages);

        if (mPkgList != null && !mPkgList.isEmpty()) {
            if (mPkgList.size() == 1) {
                mTextViewSelectedPackages.setText(mPkgList.size() + " " + getResources().getString(R.string.packageSelected));
            } else if (mPkgList.size() > 1) {
                mTextViewSelectedPackages.setText(mPkgList.size() + " " + getResources().getString(R.string.packagesSelected));
            }
        }

        if (isTablet) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                recyclerView.setBackground(getResources().getDrawable(R.drawable.bg_et));
            }
        }

        if (!isTablet && mPkgList.size() >= 4) {
            recyclerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (getResources().getDimension(R.dimen.pkg_details_listview_height))));
        } else if (!isTablet && mPkgList.size() > 0 && mPkgList.size() < 4) {
            recyclerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) ((mPkgList.size() * getResources().getDimension(R.dimen.pkg_details_list_item_height)) + 60)));
        } else if (isTablet) {
            recyclerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) ((2 * getResources().getDimension(R.dimen.pkg_details_list_item_height)) + 10)));
        }

        if (isTablet) {
            mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
                @Override
                public void onStartSigning() {
                    mTvSignHere.setVisibility(View.GONE);
                }

                @Override
                public void onSigned() {
                }

                @Override
                public void onClear() {
                }
            });
        }
    }


    // Binding the packages list to the ListView
    public void bindToListView() {
        if (mPkgList != null && !mPkgList.isEmpty()) {
            pkgLogoutDetailsAdapter = new PkgLogoutDetailsAdapter(getActivity(), mPkgList, isTablet ? 2 : 1);
            if (isTablet) {
                recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            } else {
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            }
            recyclerView.setAdapter(pkgLogoutDetailsAdapter);
            pkgLogoutDetailsAdapter.setNoticeCloseListener(new PkgLogoutDetailsAdapter.NoticeItemCloseListener() {
                @Override
                public void onCloseClicked(int position) {
                    confirmAlert(position);
                }
            });
        }
    }

    @OnTouch(R.id.main_scrollview)
    boolean onTouchScrollView(View view) {
        recyclerView.getParent().requestDisallowInterceptTouchEvent(false);
        return false;
    }

    @OnTouch(R.id.PkgrecyclerView)
    boolean ontouchRecyclerView(View view) {
        // Disallow the touch request for parent scroll on touch of
        // child view
        view.getParent().requestDisallowInterceptTouchEvent(true);
        return false;
    }

    @OnItemSelected(R.id.spinnerPkcg)
    public void spinnerItemSelected(Spinner spinner, int position) {
        if (spinner.getSelectedItem() != null) {
            SpinnerData.resetData(pkgStatusList);
            ((SpinnerData) spinner.getSelectedItem()).setSelected(true);
            pkgStatusTV.setText(((SpinnerData) spinner.getSelectedItem()).getName());
        }
    }

    @OnItemSelected(R.id.spinnerSendNotification)
    public void sendNotificationsSelected(Spinner spinner, int position) {
        if (spinner.getSelectedItem() != null) {
            SpinnerData.resetData(mSendNoificationList);
            ((SpinnerData) spinner.getSelectedItem()).setSelected(true);
            sendNotificationsTV.setText(((SpinnerData) spinner.getSelectedItem()).getName());
        }
    }

    @OnClick(R.id.pkgStatusFL)
    void pkgStatusClicked(View view) {
        mSpinnerPkcg.performClick();
    }

    @OnClick(R.id.sendnotifii_flayout)
    void sendNotiClicked(View view) {
        sendNotiSpinnner.performClick();
    }


    @OnClick(R.id.buttonIamDone)
    void onClickBtnIamDne(View view) {
        attemptUpdatePackage();
    }

    @OnClick({R.id.backImage, R.id.mobile_left_linear})
    void onClickBackButton() {
        try{
            NTF_Utils.hideKeyboard(getActivity());
            getActivity().onBackPressed();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @OnClick(R.id.image_pick1)
    void onClickImagePick1(View view) {
        if (System.currentTimeMillis() - mLastClickTime < 500) {
            return;
        }
        mLastClickTime = System.currentTimeMillis();
        if (bitmap1 != null) {
            NTF_Utils.showImageAlert(bitmap1, getActivity());
        } else {
            showAlert(1);
        }
    }

    @OnClick(R.id.image_pick2)
    void onClickImagePick2(View view) {
        if (System.currentTimeMillis() - mLastClickTime < 500) {
            return;
        }
        mLastClickTime = System.currentTimeMillis();
        if (bitmap2 != null) {
            NTF_Utils.showImageAlert(bitmap2, getActivity());
        } else {
            showAlert(2);
        }
    }

    @OnClick(R.id.image_pick3)
    void onClickImagePick3(View view) {
        if (System.currentTimeMillis() - mLastClickTime < 500) {
            return;
        }
        mLastClickTime = System.currentTimeMillis();
        if (bitmap3 != null) {
            NTF_Utils.showImageAlert(bitmap3, getActivity());
        } else {
            showAlert(3);
        }
    }

    @OnClick(R.id.imageButtonClose1)
    void onClickBtnClose1(View view) {
        deleteImage(1);
    }

    @OnClick(R.id.imageButtonClose2)
    void onClickBtnClose2(View view) {
        deleteImage(2);
    }

    @OnClick(R.id.imageButtonClose3)
    void onClickBtnClose3(View v) {
        deleteImage(3);
    }

    @OnClick(R.id.pkgStatusButtonDownArrow)
    void onClickDownArrow(View view) {
        // Handle clicks for mButtonGo
        mSpinnerPkcg.performClick();
    }

    @Optional
    @OnClick(R.id.btn_click_to_sign)
    void BtnClickToSign(View view) {
        Intent i = new Intent(getActivity(), DrawSignatureActivity.class);
        startActivityForResult(i, DRAW_SIGNATURE_ACTIVITY_CONSTANT);
    }

    @OnClick(R.id.mobile_clear_sign)
    void onClickClearSign(View view) {
        mSignatureBitmap = null;
        mSignatureImageView.setVisibility(View.GONE);
        mLableSignature.setVisibility(View.GONE);
        mBtnClickToSign.setVisibility(View.VISIBLE);
    }

    @Optional
    @OnClick(R.id.tab_clear_sign)
    void onClickTabClearSign(View view) {
        mSignaturePad.clear();
        mTvSignHere.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.buttonAdvancedOption)
    void onClickBtnAdvncdOptn(View view) {
        buttonAdvancedOption.setContentDescription(sendNotificationsLL.getVisibility() != View.VISIBLE ? "collapse advanced options" : "expand advanced options");
        sendNotificationsLL.setVisibility(sendNotificationsLL.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);//9704674607
    }

    // Confirm alert for Packages logout..
    private void confirmAlert(final int position) {


        try {
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
                    if (mPkgList != null && !mPkgList.isEmpty()) {
                        if (!isTablet && mPkgList.size() > 0 && mPkgList.size() < 4) {
                            recyclerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, (int) ((mPkgList.size() * getResources().getDimension(R.dimen.pkg_details_list_item_height)) + 60)));
                        }
                        if (mPkgList.size() == 1) {
                            mTextViewSelectedPackages.setText(mPkgList.size() + " " + getString(R.string.packageSelected));
                        } else if (mPkgList.size() > 1) {
                            mTextViewSelectedPackages.setText(mPkgList.size() + " " + getString(R.string.packagesSelected));
                        }
                    } else if (mPkgList != null && mPkgList.size() == 0) {
                        mTextViewSelectedPackages.setText(getString(R.string.no_packages_found));
                        recyclerView.setVisibility(View.GONE);
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

    // This method deals with the removing packages form List...
    private void removeFromList(int position) {
        try {
            mPkgList.remove(position);
            pkgLogoutDetailsAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Input fields validation for package logout...
    private void attemptUpdatePackage() {
        try {
            NTF_Utils.hideKeyboard(getActivity());
            if (mPkgList != null && !mPkgList.isEmpty()) {
                // clearBitmap();
                int spinnerSelectedPos = mSpinnerPkcg.getSelectedItemPosition();
                if (spinnerSelectedPos == -1) {
                    NTF_Utils.showAlert(getActivity(), "", "Please select any package log out option.", null);
                } else if (!isTablet && isSignatureNeeded() && (mSignatureBitmap == null || BitmapHelper.getBase64String(mSignatureBitmap).isEmpty())) {
                    NTF_Utils.showAlert(getActivity(), "", "Please sign to log out selected package(s).", null);
                } else if (isTablet && isSignatureNeeded() && mSignaturePad.isEmpty()) {
                    NTF_Utils.showAlert(getActivity(), "", "Please sign to log out selected package(s).", null);
                } else {
                    if (isTablet) {
                        if (!mSignaturePad.isEmpty()) {
                            mSignatureBitmap = mSignaturePad.getTransparentSignatureBitmap();
                            mSignatureBitmap = overlay(mSignatureBitmap);
                            if (mSignatureBitmap == null)
                                return;
                            str_signature_base64string = signatureBase64String(mSignatureBitmap);
                        } else {
                            str_signature_base64string = "";
                        }
                        //do Pkg log service call
                        if (bitmap1 == null && bitmap2 == null && bitmap3 == null) {
                            logPackageOut();
                        } else {
                            new CovertBase64Task().execute();
                        }
                    } else {
                        if (mSignatureBitmap != null) {
                            Canvas canvas = new Canvas(mSignatureBitmap);
                            canvas.drawBitmap(mSignatureBitmap, new Matrix(), null);
                            Paint paintStroke = new Paint();
                            paintStroke.setStrokeWidth(5);
                            paintStroke.setStyle(Paint.Style.STROKE);
                            paintStroke.setColor(Color.LTGRAY);
                            paintStroke.setAntiAlias(true);
                            final Rect rect = new Rect(0, 0, mSignatureBitmap.getWidth(), mSignatureBitmap.getHeight());
                            final RectF rectF = new RectF(rect);
                            mSignatureImageView.setPadding(0, 0, 0, 0);
                            canvas.drawRoundRect(rectF, 0, 0, paintStroke);

                            str_signature_base64string = signatureBase64String(mSignatureBitmap);
                        } else {
                            str_signature_base64string = "";
                        }
                        //do Pkg log service call
                        if (bitmap1 == null && bitmap2 == null && bitmap3 == null) {
                            logPackageOut();
                        } else {
                            new CovertBase64Task().execute();
                        }
                    }
                }
            } else {
                NTF_Utils.showAlert(getActivity(), "", "Please select a package.", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // preparing service request for package logout...
    private void logPackageOut() {
        if (NTF_Utils.isOnline(getActivity())) {
            //10020 fixed if we have signature
            if (isSignatureNeeded()) {
                // If is Signature needed the signature should not be null
                if (str_signature_base64string != null) {
                    logPackageData(
                            ntf_Preferences.get(Prefs_Keys.SESSION_ID),
                            ntf_Preferences.get(Prefs_Keys.AUTHENTICATION_TOKEN),
                            ntf_Preferences.get(Prefs_Keys.ACCOUNT_ID),
                            //getPackageIds(), spinnerData.getValue(),
                            getPackageIds(),
                            ntf_Preferences.get(Prefs_Keys.USER_ID),
                            mEditTextNote.getText().toString());
                    //mEditTextNote.getText().toString(), str_signature_base64string, base64Str1, base64Str2, base64Str3);
                } else {
                    NTF_Utils.showAlert(getActivity(), "", "Some thing went wrong, Please try again later", null);
                }
            } else {
                // If there is no need of signature signaturebse64 string should be empty.
                logPackageData(
                        ntf_Preferences.get(Prefs_Keys.SESSION_ID),
                        ntf_Preferences.get(Prefs_Keys.AUTHENTICATION_TOKEN),
                        ntf_Preferences.get(Prefs_Keys.ACCOUNT_ID),
                        //getPackageIds(), spinnerData.getValue(),
                        getPackageIds(),
                        ntf_Preferences.get(Prefs_Keys.USER_ID),
                        mEditTextNote.getText().toString());
            }
        } else {
            NTF_Utils.showNoNetworkAlert(getActivity());
            if (getActivity() != null) {
                ((MainActivity) getActivity()).registerReceiver();
            }
        }
    }

    // This method returns the package ID's form the packages list..
    private String getPackageIds() {
        String pkgIds = "";
        try {
            for (int i = 0, len = mPkgList.size(); i < len; i++) {
                if (pkgIds.equalsIgnoreCase("")) {
                    pkgIds = mPkgList.get(i).getPackageId();
                } else {
                    pkgIds += "," + mPkgList.get(i).getPackageId();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pkgIds;
    }

    // Service request for package logout..
    public void logPackageData(String session_id, String authentication_token, String account_id,
                               String package_id, String user_id, String staff_notes) {
        try {
            LogPackageDataRequest logPackageDataRequest = new LogPackageDataRequest();
            logPackageDataRequest.setSessionId(session_id);
            logPackageDataRequest.setAuthenticationToken(authentication_token);
            logPackageDataRequest.setAccountId(account_id);
            logPackageDataRequest.setUserId(user_id);
            logPackageDataRequest.setPackageId(package_id);
            logPackageDataRequest.setLogoutCodeId(spinnerData.getValue());
            logPackageDataRequest.setStaffNote(staff_notes);
            NTF_Utils.showProgressDialog(getActivity());
            logOutPackageDetailsPresenter.doPackageDataLogout(null, logPackageDataRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void logPackageImages() {
        try {
            LogPkgImagesRequest logPkgImagesRequest = new LogPkgImagesRequest();
            logPkgImagesRequest.setSessionId(ntf_Preferences.get(Prefs_Keys.SESSION_ID));
            logPkgImagesRequest.setAuthenticationToken(ntf_Preferences.get(Prefs_Keys.AUTHENTICATION_TOKEN));
            logPkgImagesRequest.setAccountId(ntf_Preferences.get(Prefs_Keys.ACCOUNT_ID));
            logPkgImagesRequest.setUserId(ntf_Preferences.get(Prefs_Keys.USER_ID));
            logPkgImagesRequest.setPackageId(getPackageIds());
            logPkgImagesRequest.setLogoutCodeId(spinnerData.getValue());
            logPkgImagesRequest.setEsignatureBase64(str_signature_base64string);


            if (base64Str1 == null && base64Str2 == null && base64Str3 == null) {
                logPkgImagesRequest.setPackagePictures(null);
                // jsonObject.put("package_pictures", "");
            } else {
                PackagePicture packagePicture;
                List<PackagePicture> packagePictures = new ArrayList();
                if (base64Str1 != null) {
                    packagePicture = new PackagePicture();
                    packagePicture.setPictureData(base64Str1);
                    packagePictures.add(packagePicture);
                }
                if (base64Str2 != null) {
                    packagePicture = new PackagePicture();
                    packagePicture.setPictureData(base64Str2);
                    packagePictures.add(packagePicture);

                }
                if (base64Str3 != null) {
                    packagePicture = new PackagePicture();
                    packagePicture.setPictureData(base64Str3);
                    packagePictures.add(packagePicture);
                }
                logPkgImagesRequest.setPackagePictures(packagePictures);
            }
            String sendNotiKey = ((SpinnerData) sendNotiSpinnner.getSelectedItem()).getValue();
            logPkgImagesRequest.setAdvancedSendNotification(sendNotiKey);
            logOutPackageDetailsPresenter.doPackageImagesLogout(null, logPkgImagesRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap getOverlayImage(Bitmap signaturebmp) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Bitmap bitmap = decodeSampledBitmapFromResource(getResources(), R.drawable.bg_grid_latest, 100, 100);
        bitmap = Bitmap.createScaledBitmap(bitmap, signaturebmp.getWidth(), displayMetrics.widthPixels / 3, true);

        Bitmap bmOverlay = Bitmap.createBitmap(signaturebmp.getWidth(), signaturebmp.getHeight(), signaturebmp.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(signaturebmp, new Matrix(), null);
        canvas.drawBitmap(bitmap, (signaturebmp.getWidth() - bitmap.getWidth()) / 2, (signaturebmp.getHeight() - bitmap.getHeight()) / 2, null);
        return bmOverlay;
    }

    private Bitmap overlay(Bitmap signaturebmp) {
        try {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            Bitmap overlayBitmap = Bitmap.createBitmap(signaturebmp.getWidth(), signaturebmp.getHeight(), signaturebmp.getConfig());
            Canvas canvas = new Canvas(overlayBitmap);
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_grid_latest);
            bitmap = Bitmap.createScaledBitmap(bitmap, signaturebmp.getWidth(), displayMetrics.widthPixels / 3, true);
            canvas.drawBitmap(bitmap, 0, 0, null);
            canvas.drawBitmap(signaturebmp, new Matrix(), null);

            Paint paintStroke = new Paint();
            paintStroke.setStrokeWidth(2);
            paintStroke.setStyle(Paint.Style.STROKE);
            paintStroke.setColor(Color.LTGRAY);
            paintStroke.setAntiAlias(true);

            final Rect rect = new Rect(0, 0, overlayBitmap.getWidth(), overlayBitmap.getHeight());
            final RectF rectF = new RectF(rect);
            canvas.drawRoundRect(rectF, 0, 0, paintStroke);
            if (bitmap != null) {
                bitmap.recycle();
            }
            return overlayBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

   /* private Bitmap overlay(Bitmap signaturebmp) {
        Bitmap bitmap = null;
        Bitmap overlayBitmap = Bitmap.createBitmap(signaturebmp.getWidth(), signaturebmp.getHeight(), signaturebmp.getConfig());
        Canvas canvas = new Canvas(overlayBitmap);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height1 = displayMetrics.heightPixels;
        int width1 = displayMetrics.widthPixels;
        if (bitmap != null) {
            bitmap.recycle();
        }
        //+++++++++++
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.bg_grid_latest, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        String imageType = options.outMimeType;
        bitmap = decodeSampledBitmapFromResource(getResources(), R.drawable.bg_grid_latest, 100, 100);
        //+++++++++++
        //bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_grid_latest);
        bitmap = Bitmap.createScaledBitmap(bitmap, width1, signaturebmp.getHeight(), true);
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.drawBitmap(signaturebmp, new Matrix(), null);

        Paint paintStroke = new Paint();
        paintStroke.setStrokeWidth(2);
        paintStroke.setStyle(Paint.Style.STROKE);
        paintStroke.setColor(Color.LTGRAY);
        paintStroke.setAntiAlias(true);

        final Rect rect = new Rect(0, 0, overlayBitmap.getWidth(), overlayBitmap.getHeight());
        final RectF rectF = new RectF(rect);
        canvas.drawRoundRect(rectF, 0, 0, paintStroke);
        if (bitmap != null) {
            bitmap.recycle();
        }
        return overlayBitmap;
    }*/


    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    // This method returns the base64 string with width of 600...
    private String signatureBase64String(Bitmap bitmap) {
        try {
            Bitmap converetdImage = getResizedBitmap(bitmap, 600, (600 * bitmap.getHeight()) / bitmap.getWidth());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            converetdImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onPkgDataLogoutSuccess(String message) {
        logPackageImages();
    }

    @Override
    public void onPkgImagesLogoutSuccess() {
        NTF_Utils.hideProgressDialog();
        showSuccesAlert();
        mButtonIamDone.setBackgroundResource(R.drawable.bg_button);
        mButtonIamDone.setClickable(true);
    }

    @Override
    public void onGettingWarning(String response) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlertForFinish(getActivity(), ALERT_WARNING_TITLE, response);
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

    // This will return the base64 string from bitmap...
    class CovertBase64Task extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            NTF_Utils.showProgressDialog(getActivity());
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (bitmap1 != null) {
                base64Str1 = getBase64String(bitmap1);
            }
            if (bitmap2 != null) {
                base64Str2 = getBase64String(bitmap2);
            }
            if (bitmap3 != null) {
                base64Str3 = getBase64String(bitmap3);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            NTF_Utils.hideProgressDialog();
            logPackageOut();
        }
    }

    private void showSuccesAlert() {
        try {
            //ntf_Preferences.save(Prefs_Keys.IS_FROM_LOG_PKG_OUT_DETAILS, true);
            final Dialog dialog = new Dialog(getActivity(), R.style.DialogTheme);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_alert);
            dialog.setCanceledOnTouchOutside(false);
            LinearLayout parentLayout = (LinearLayout) dialog.findViewById(R.id.parentLayout);
            // set the custom dialog components - text, image and button
            ImageView imageView = (ImageView) dialog.findViewById(R.id.imageView);
            TextView mTitle = (TextView) dialog.findViewById(R.id.title);
            TextView mMessage = (TextView) dialog.findViewById(R.id.message);

            mTitle.setText(ALERT_SUCCESS_TITLE);
            mTitle.setTextColor(ActivityCompat.getColor(getActivity(), R.color.pkg_success_color));
            mMessage.setText("This package has been successfully logged out.");
            imageView.setImageResource(R.drawable.ic_success_icon);

            final int interval = 10 * 1000;
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            };
            handler.postAtTime(runnable, System.currentTimeMillis() + interval);
            handler.postDelayed(runnable, interval);
            parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            if (dialog == null || dialog.getWindow() == null) {
                return;
            }

            int dialogWidth = (int) (width * 0.90f);
            if (getResources().getBoolean(R.bool.isTablet)) {
                dialogWidth = (int) (width * 0.75f);
            }

            int dialogHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(dialogWidth, dialogHeight);
            dialog.show();
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    Activity activity = getActivity();
                    if (activity instanceof MainActivity) {
                        isTenSecondsCompleted=true;
                        MainActivity mainActivity = (MainActivity) activity;
                        mainActivity.clearAllFrgaments();
                        getTargetFragment().onActivityResult(
                                getTargetRequestCode(),
                                Activity.RESULT_OK,
                                new Intent().putExtra(Extras_Keys.REFRESH_LOG_PACKAGE_OUT_LIST, true));
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // This method returns the base64string with width of 600...
    private String getBase64String(Bitmap bitmap) {
        try {
            Bitmap converetdImage = getResizedBitmap(bitmap, 600, (600 * bitmap.getHeight()) / bitmap.getWidth());
            File tempFile = new File(NTF_Utils.getAppDirPath(getActivity()), "temp.jpg");

            byte[] byteArray3 = getFileSize(converetdImage, tempFile);
            String base64 = Base64.encodeToString(byteArray3, Base64.DEFAULT);
            return base64;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // This method returns the fileSize Image file...
    public byte[] getFileSize(Bitmap bitmap, File tempFile) {
        int fileSizeInKB = getBitmapSizeInKB(bitmap);
        byte[] byteArray3 = new byte[0];
        try {
            if (fileSizeInKB >= 512) {
                int quality = 90;
                if (fileSizeInKB >= 2048) {
                    quality = 40;
                } else if (fileSizeInKB >= 1024) {
                    quality = 45;
                } else if (fileSizeInKB >= 750) {
                    quality = 55;
                }

                FileOutputStream fos;
                fos = new FileOutputStream(tempFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
                fos.flush();
                fos.close();
                MediaScannerConnection.scanFile(getActivity(), new String[]{tempFile.getAbsolutePath()}, null, null);
                bitmap = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
                byteArray3 = BitmapHelper.getByteArrayOfBitmap(bitmap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray3;
    }


    // This method returns the bitmap size in KB
    public static int getBitmapSizeInKB(Bitmap data) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
            return (data.getRowBytes() * data.getHeight()) / 1024;
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return data.getByteCount() / 1024;
        } else {
            return data.getAllocationByteCount() / 1024;
        }
    }


    // This method returns the resized bitmap...
    public static Bitmap getResizedBitmap(Bitmap source, int newWidth, int newHeight) throws NullPointerException {
        if (source != null) {
            int width = source.getWidth();
            int height = source.getHeight();
            // create a matrix for the manipulation
            Matrix matrix = new Matrix();
            // resize the bitmap
            matrix.postScale(((float) newWidth) / width, ((float) newHeight) / height);
            // recreate the new Bitmap
            return Bitmap.createBitmap(source, 0, 0, width, height, matrix, true);
        } else {
            throw new NullPointerException("Inputted Bitmap is NULL");
        }
    }

    // For some package status there is no need of signature
    // for this we have to check if signature is required or not
    // if require_app_signature is "1" - sign is required or else no need.
    public boolean isSignatureNeeded() {
        spinnerData = (SpinnerData) mSpinnerPkcg.getSelectedItem();
        if (spinnerData.getRequire_app_signature().equals("1")) {
            return true;
        }
        return false;
    }


    Dialog dialog = null;

    // This method used for deleting the image..
    private void deleteImage(int position) {

        if (position == 1) {
            bitmap1 = null;
            base64Str1 = null;

        } else if (position == 2) {
            bitmap2 = null;
            base64Str2 = null;
        } else if (position == 3) {
            bitmap3 = null;
            base64Str3 = null;
        }
        if (bitmap1 == null) {
            mGap1.setVisibility(View.VISIBLE);
            imgView1.setImageResource(R.drawable.ic_camera_icon);
            viewPicClose1.setVisibility(View.GONE);
        }
        if (bitmap2 == null) {
            imgView2.setImageResource(R.drawable.ic_camera_icon);
            viewPicClose2.setVisibility(View.GONE);
        }
        if (bitmap3 == null) {
            imgView3.setImageResource(R.drawable.ic_camera_icon);
            viewPicClose3.setVisibility(View.GONE);
        }
    }

    //Setting spinner data....
    public void setSpinnerData() {
        try {
            if (mGobalJsonData == null)
                mGobalJsonData = NTF_Utils.getGlobalData(getActivity());
            if (mGobalJsonData == null)
                return;
            pkgStatusList = NTF_Utils.filterPKGStatus(SpinnerData.getList(mGobalJsonData.getJSONArray("logout_code_id"), null).getList());
            pkgStatusList.remove(0);
            if (pkgStatusList != null && pkgStatusList.size() > 0) {
                String app_default_logout_code_id = "";
                if (mGobalJsonData.getJSONArray("app_default_logout_code_id").length() > 0) {
                    app_default_logout_code_id = mGobalJsonData.getJSONArray("app_default_logout_code_id").getJSONObject(0).getString("app_default_logout_code_id");
                }
                SpinnerMarkAdapter adapter = new SpinnerMarkAdapter(getActivity(), pkgStatusList);
                mSpinnerPkcg.setAdapter(new SpinnerHintAdapter(adapter, R.layout.spinner_hint, getActivity()));

//               /**Default by Package up by recipient, logout_code_id=10020*/
                for (int i = 0, len = pkgStatusList.size(); i < len; i++) {
                    if (pkgStatusList.get(i).getValue().equalsIgnoreCase(app_default_logout_code_id)) {
                        mSpinnerPkcg.setSelection(i+1);
                        pkgStatusList.get(i).setSelected(true);
                        break;
                    }
                }
            }

            mSendNoificationList = SpinnerData.getList(mGobalJsonData.getJSONArray("advanced_logout_send_notification"), null).getList();
            if (mSendNoificationList != null && !mSendNoificationList.isEmpty()) {
                if (mSendNoificationList.size() >= 10) {
                    NTF_Utils.setSpinnerHeight(getActivity(), sendNotiSpinnner);
                }
                SpinnerMarkAdapter facilitiesAdapter = new SpinnerMarkAdapter(getActivity(), mSendNoificationList);
                sendNotiSpinnner.setAdapter(new SpinnerHintAdapter(facilitiesAdapter, R.layout.spinner_hint, getActivity()));
                sendNotiSpinnner.setSelection(1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // This method opens the camera for corresponding image button click..
    private void showAlert(final int imgPos) {
        if (imgPos == 1) {
            openCameraPickPhoto(LogPackageInFragment.CLICK_PHOTO_REQUEST_CODE_1);
        } else if (imgPos == 2) {
            openCameraPickPhoto(LogPackageInFragment.CLICK_PHOTO_REQUEST_CODE_2);
        } else {
            openCameraPickPhoto(LogPackageInFragment.CLICK_PHOTO_REQUEST_CODE_3);
        }
    }

    private void openCameraPickPhoto(int requestCode) {
        // Handling the runtime permissions for Marshmallow
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == LogPackageInFragment.CLICK_PHOTO_REQUEST_CODE_1) {
                bitmap1 = SingleTon.getInstance().getBitmap1();
                imgView1.setImageBitmap(bitmap1);
                // visibleViews(2);
                viewPicClose1.setVisibility(View.VISIBLE);
            } else if (requestCode == LogPackageInFragment.CLICK_PHOTO_REQUEST_CODE_2) {
                bitmap2 = SingleTon.getInstance().getBitmap2();
                //visibleViews(3);
                imgView2.setImageBitmap(bitmap2);
                viewPicClose2.setVisibility(View.VISIBLE);
            } else if (requestCode == LogPackageInFragment.CLICK_PHOTO_REQUEST_CODE_3) {
                bitmap3 = SingleTon.getInstance().getBitmap3();
                imgView3.setImageBitmap(bitmap3);
                viewPicClose3.setVisibility(View.VISIBLE);
            } else if (requestCode == DRAW_SIGNATURE_ACTIVITY_CONSTANT) {
                try {
                    mSignatureBitmap = Extras_Keys.IMAGE;
                    mIvMobileClearSignature.setVisibility(View.VISIBLE);
                    mBtnClickToSign.setVisibility(View.GONE);
                    mSignatureImageView.setVisibility(View.VISIBLE);
                    mLableSignature.setVisibility(View.VISIBLE);
                    if (mSignatureBitmap != null) {
                        mSignatureImageView.setImageBitmap(mSignatureBitmap);
                    }
                } catch (Exception e) {
                    showToast("Something has gone wrong");
                    e.printStackTrace();
                }
            }
        } else if (SingleTon.getInstance().getCapturedBitmap() != null && SingleTon.getInstance().getRequestCode() == CLICK_PHOTO_REQUEST_CODE_1) {
            bitmap1 = SingleTon.getInstance().getBitmap1();
            Glide.with(getActivity())
                    .load(BitmapHelper.getByteArrayOfBitmap(bitmap1))
                    .into(imgView1);
            viewPicClose1.setVisibility(View.VISIBLE);
        } else if (SingleTon.getInstance().getCapturedBitmap() != null && SingleTon.getInstance().getRequestCode() == CLICK_PHOTO_REQUEST_CODE_2) {
            bitmap2 = SingleTon.getInstance().getBitmap2();
            Glide.with(getActivity())
                    .load(BitmapHelper.getByteArrayOfBitmap(bitmap2))
                    .into(imgView2);
            viewPicClose2.setVisibility(View.VISIBLE);
        } else if (SingleTon.getInstance().getCapturedBitmap() != null && SingleTon.getInstance().getRequestCode() == CLICK_PHOTO_REQUEST_CODE_3) {
            bitmap3 = SingleTon.getInstance().getBitmap3();
            Glide.with(getActivity())
                    .load(BitmapHelper.getByteArrayOfBitmap(bitmap3))
                    .into(imgView3);
            viewPicClose3.setVisibility(View.VISIBLE);
        }
        SingleTon.getInstance().setRequestCode(0);

    }

    public void handleBackButton() {
        try{
//            if (getFragmentManager().getBackStackEntryCount()<=2){
            if (NTF_Utils.getCurrentFragment(this) instanceof PackageLogoutFragment){
                NTF_Utils.hideKeyboard(getActivity());
                getTargetFragment().onActivityResult(
                        getTargetRequestCode(),
                        Activity.RESULT_OK,
                        new Intent().putExtra(Extras_Keys.REFRESH_LOG_PACKAGE_OUT_LIST, true));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
