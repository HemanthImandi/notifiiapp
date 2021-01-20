package com.notifii.notifiiapp.fragments;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.activities.MainActivity;
import com.notifii.notifiiapp.base.NTF_BaseFragment;
import com.notifii.notifiiapp.models.SpinnerData;
import com.notifii.notifiiapp.mvp.models.GetPackageDetailsRequest;
import com.notifii.notifiiapp.mvp.presenters.GetPkgDetailsPresenter;
import com.notifii.notifiiapp.mvp.views.GetPkgDetailsView;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class PackageDetailsFragment extends NTF_BaseFragment implements GetPkgDetailsView {

    @BindView(R.id.textViewPkgDetailsRecipientLabel)
    TextView mTextViewPkgDetailsRecipientLabel;
    @BindView(R.id.textview_recipient_name)
    TextView mTextviewRecipientName;
    @BindView(R.id.textViewPkgDetailsRecipientAddr1Label)
    TextView mTextViewPkgDetailsRecipientAddr1Label;
    @BindView(R.id.textview_unitnumber)
    TextView mTextviewUnitnumber;
    @BindView(R.id.textview_tracking_number)
    TextView mTextviewTrackingNumber;
    @BindView(R.id.textview_log_in)
    TextView mTextviewReceived;
    @BindView(R.id.textview_log_out)
    TextView mTextviewPickedUp;
    @BindView(R.id.textview_carrier)
    TextView mTextviewCarrier;
    @BindView(R.id.textview_shelf_location)
    TextView mTextviewShelfLocation;
    @BindView(R.id.textview_sender)
    TextView mTextviewSender;
    @BindView(R.id.textview_service_type)
    TextView mTextviewServiceType;
    @BindView(R.id.textview_package_type)
    TextView mTextviewPackageType;
    @BindView(R.id.textview_tag_number)
    TextView mTextviewTagNumber;
    @BindView(R.id.textview_package_condition)
    TextView mTextviewPackageCondition;
    @BindView(R.id.textview_special_handling)
    TextView mTextviewSpecialHandling;
    @BindView(R.id.textview_custom_message)
    TextView mTextviewCustomMessage;
    @BindView(R.id.textview_status)
    TextView mTextviewStatus;
    @BindView(R.id.textview_staff_note)
    TextView mTextviewStaffNote;
    @BindView(R.id.imageVieweSignature)
    ImageView mImageVieweSignature;
    @BindView(R.id.textview_po_number)
    TextView mTextViewPoNumber;
    @BindView(R.id.textview_weight)
    TextView mTextViewWeight;
    @BindView(R.id.textview_dimensions)
    TextView mTextViewDimensions;
    @BindView(R.id.progressBarPkgDetails)
    View mProgressBar;
    @BindView(R.id.main_linearLayout)
    LinearLayout mMainLinearLayout;
    @BindView(R.id.login_images_linearLayout)
    LinearLayout mLoginImagesLinearLayout;
    @BindView(R.id.logout_images_linearLayout)
    LinearLayout mLogoutIamgesLinearLayout;
    @BindView(R.id.signature_linearLayout)
    LinearLayout mSignatureLinearLayout;
    @BindView(R.id.signature_frameLayout)
    FrameLayout mSignatureFrameLayout;
    @BindView(R.id.imageView1loginImage)
    ImageView mLoginImageView1;
    @BindView(R.id.imageView2loginImage)
    ImageView mLoginImageView2;
    @BindView(R.id.imageView3loginImage)
    ImageView mLoginImageView3;
    @BindView(R.id.imageView1logoutImage)
    ImageView mLogoutImageView1;
    @BindView(R.id.imageView2logoutImage)
    ImageView mLogoutImageView2;
    @BindView(R.id.imageView3logoutImage)
    ImageView mLogoutImageView3;
    @BindView(R.id.textview_log_in_by)
    TextView mTextViewLoginBy;
    @BindView(R.id.textview_log_out_by)
    TextView mTextViewLogOutBy;
    @BindView(R.id.textView_no_sign)
    TextView mTextViewNoSign;
    @BindView(R.id.login_no_picture_view)
    TextView mTextViewLoginNoImage;
    @BindView(R.id.logout_no_picture_view)
    TextView mtextViewLogOutNoImage;
    @BindView(R.id.textview_mailroom)
    TextView mTextViewMailroom;
    @BindView(R.id.po_view)
    View poView;
    @BindView(R.id.parentLayout)
    LinearLayout parentLayout;
    @BindView(R.id.shelf_layout)
    LinearLayout shelflayout;
    @BindView(R.id.ll_po_number)
    LinearLayout mLlPoView;
    @BindView(R.id.layout_staff_note)
    LinearLayout staffNoteLayout;
    @BindView(R.id.layout_custom_message)
    LinearLayout customMessageLayout;
    @BindView(R.id.name_layout)
    LinearLayout namelayout;
    @BindView(R.id.unit_layout)
    LinearLayout unitlayout;
    @BindView(R.id.carrier_layout)
    LinearLayout carrierlayout;
    @BindView(R.id.tracking_no_layout)
    LinearLayout trackingNolayout;
    @BindView(R.id.status_layout)
    LinearLayout statuslayout;
    @BindView(R.id.log_in_layout)
    LinearLayout logInlayout;
    @BindView(R.id.login_by_layout)
    LinearLayout loginBylayout;
    @BindView(R.id.log_out_layout)
    LinearLayout logoutlayout;
    @BindView(R.id.logout_by_layout)
    LinearLayout logoutBylayout;
    @BindView(R.id.tag_layout)
    LinearLayout taglayout;
    @BindView(R.id.mailroom_layout)
    LinearLayout mailroomlayout;
    @BindView(R.id.special_handling_layout)
    LinearLayout specialHandlinglayout;
    @BindView(R.id.pkg_cond_layout)
    LinearLayout pkgCondlayout;
    @BindView(R.id.service_type_layout)
    LinearLayout serviceTypelayout;
    @BindView(R.id.pkg_type_layout)
    LinearLayout pkgTypelayout;
    @BindView(R.id.sender_layout)
    LinearLayout senderlayout;
    @BindView(R.id.ll_weight)
    LinearLayout llWeight;
    @BindView(R.id.ll_dimensions)
    LinearLayout llDimensions;
    @BindView(R.id.mailroom_view)
    View mailroomView;
    @BindView(R.id.frame_login_image1)
    FrameLayout mFrameLoginImage1;
    @BindView(R.id.frame_login_image2)
    FrameLayout mFrameLoginImage2;
    @BindView(R.id.frame_login_image3)
    FrameLayout mFrameLoginImage3;
    @BindView(R.id.frame_logout_image1)
    FrameLayout mFrameLogoutImage1;
    @BindView(R.id.frame_logout_image2)
    FrameLayout mFrameLogoutImage2;
    @BindView(R.id.frame_logout_image3)
    FrameLayout mFrameLogoutImage3;
    @BindView(R.id.login_image1_progressbar)
    ProgressBar mLoginImage1ProgressBar;
    @BindView(R.id.login_image2_progressbar)
    ProgressBar mLoginImage2ProgressBar;
    @BindView(R.id.login_image3_progressbar)
    ProgressBar mLoginImage3ProgressBar;
    @BindView(R.id.logout_image1_progressbar)
    ProgressBar mLogoutImage1ProgressBar;
    @BindView(R.id.logout_image2_progressbar)
    ProgressBar mLogoutImage2ProgressBar;
    @BindView(R.id.logout_image3_progressbar)
    ProgressBar mLogoutImage3ProgressBar;
    public static boolean isSearchPkgFragment;
    public static boolean isFromPkgDetails;
    private String mPkgId = "";
    private String mRecipientId = "";
//    String dateFormat = "MMMM d, yyyy h:mm a";
    private ArrayList<String> loginImagesList = new ArrayList<>();
    private ArrayList<String> logoutImagesList = new ArrayList<>();
    private ArrayList<ImageView> mLoginImageViewsArrayList = new ArrayList<>();
    private ArrayList<ImageView> mLogoutImageViewsArrayList = new ArrayList<>();
    private ArrayList<ProgressBar> mLoginProgressBar = new ArrayList<>();
    private ArrayList<ProgressBar> mLogoutProgressBar = new ArrayList<>();
    private ArrayList<FrameLayout> mLoginFrameLayoutList = new ArrayList<>();
    private ArrayList<FrameLayout> mLogoutFrameLayoutList = new ArrayList<>();
    private ArrayList<LinearLayout> viewsArrayList = new ArrayList<>();
    private JSONArray notification_jsonArray;
    private LinearLayout mSecondLinear;
    private boolean isTablet;
    private String recipientAddress1Label;
    private JSONObject mGobalJsonData;
    GetPkgDetailsPresenter getPkgDetailsPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_package_details, container, false);
        ButterKnife.bind(this, mainView);
        return mainView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getPkgDetailsPresenter = new GetPkgDetailsPresenter();
        getPkgDetailsPresenter.attachMvpView(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(view);
        mGobalJsonData = NTF_Utils.getGlobalData(getActivity());
        Bundle bundle = getArguments();
        if (bundle != null) {
            mPkgId = bundle.getString(Extras_Keys.KEY_PKG_ID);
            getPackage();
        }
    }

    private void bindViews(View rootView) {
        setToolbarTitle(getString(R.string.title_pkg_details));
        setToolbarActionButtons(true, false, 0);
        isTablet = getResources().getBoolean(R.bool.isDeviceTablet);
        if (isTablet) {
            mSecondLinear = (LinearLayout) rootView.findViewById(R.id.second_linear);
        }
        mSignatureLinearLayout.setVisibility(View.INVISIBLE);
        mSignatureFrameLayout.setVisibility(View.GONE);
        String accountType = ntf_Preferences.get(Prefs_Keys.ACCOUNT_TYPE);
        String recipientLabel = NTF_Utils.getRecipientTypeLabel(accountType);
        if (accountType.toLowerCase().equalsIgnoreCase("apt")) {
            recipientAddress1Label = "Unit #";
        } else {
            recipientAddress1Label = NTF_Utils.getRecipientAddress1Label(accountType);
        }

        mTextViewPkgDetailsRecipientAddr1Label.setText(recipientAddress1Label);
        mLoginImageViewsArrayList.add(mLoginImageView1);
        mLoginImageViewsArrayList.add(mLoginImageView2);
        mLoginImageViewsArrayList.add(mLoginImageView3);
        mLogoutImageViewsArrayList.add(mLogoutImageView1);
        mLogoutImageViewsArrayList.add(mLogoutImageView2);
        mLogoutImageViewsArrayList.add(mLogoutImageView3);
        mLoginProgressBar.add(mLoginImage1ProgressBar);
        mLoginProgressBar.add(mLoginImage2ProgressBar);
        mLoginProgressBar.add(mLoginImage3ProgressBar);
        mLogoutProgressBar.add(mLogoutImage1ProgressBar);
        mLogoutProgressBar.add(mLogoutImage2ProgressBar);
        mLogoutProgressBar.add(mLogoutImage3ProgressBar);
        mLoginFrameLayoutList.add(mFrameLoginImage1);
        mLoginFrameLayoutList.add(mFrameLoginImage2);
        mLoginFrameLayoutList.add(mFrameLoginImage3);
        mLogoutFrameLayoutList.add(mFrameLogoutImage1);
        mLogoutFrameLayoutList.add(mFrameLogoutImage2);
        mLogoutFrameLayoutList.add(mFrameLogoutImage3);
    }

    public static PackageDetailsFragment getInstance(String pkgId, boolean isSearchPkgFrag) {
        isSearchPkgFragment = isSearchPkgFrag;
        PackageDetailsFragment fragment = new PackageDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(NTF_Constants.Extras_Keys.KEY_PKG_ID, pkgId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @OnClick(R.id.imageView1loginImage)
    void onLoginImage1Clicked() {
        NTF_Utils.showImageAlert(getActivity(), mLoginImageView1.getTag() != null ? (String) mLoginImageView1.getTag() : "");
    }

    @OnClick(R.id.imageView2loginImage)
    void onLoginImage2Clicked() {
        NTF_Utils.showImageAlert(getActivity(), mLoginImageView2.getTag() != null ? (String) mLoginImageView2.getTag() : "");
    }

    @OnClick(R.id.imageView3loginImage)
    void onLoginImage3Clicked() {
        NTF_Utils.showImageAlert(getActivity(), mLoginImageView3.getTag() != null ? (String) mLoginImageView3.getTag() : "");
    }

    @OnClick(R.id.imageView1logoutImage)
    void onLogoutImage1Clicked() {
        NTF_Utils.showImageAlert(getActivity(), mLogoutImageView1.getTag() != null ? (String) mLogoutImageView1.getTag() : "");
    }

    @OnClick(R.id.imageView2logoutImage)
    void onLogoutImage2Clicked() {
        NTF_Utils.showImageAlert(getActivity(), mLogoutImageView2.getTag() != null ? (String) mLogoutImageView2.getTag() : "");
    }

    @OnClick(R.id.imageView3logoutImage)
    void onLogoutImage3Clicked() {
        NTF_Utils.showImageAlert(getActivity(), mLogoutImageView3.getTag() != null ? (String) mLogoutImageView3.getTag() : "");
    }

    @Override
    public void onStackChanged() {
        super.onStackChanged();
        if (NTF_Utils.getCurrentFragment(this) != null && NTF_Utils.getCurrentFragment(this) instanceof PackageDetailsFragment) {
            onFragmentPopup(true);
        } else {
            onFragmentPopup(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        PackageHistoryFragment.isFromPkgHistory = false;
    }

    // Preparing the Service request for getting package details...
    private void getPackage() {
        Context context = getActivity();
        if (NTF_Utils.isOnline(context)) {
            if (!TextUtils.isEmpty(mPkgId)) {
                GetPackageDetailsRequest getPackageDetailsRequest = new GetPackageDetailsRequest();
                getPackageDetailsRequest.setSessionId(ntf_Preferences.get(Prefs_Keys.SESSION_ID));
                getPackageDetailsRequest.setUserId(ntf_Preferences.get(Prefs_Keys.USER_ID));
                getPackageDetailsRequest.setAuthenticationToken(ntf_Preferences.get(Prefs_Keys.AUTHENTICATION_TOKEN));
                getPackageDetailsRequest.setAccountId(ntf_Preferences.get(Prefs_Keys.ACCOUNT_ID));
                getPackageDetailsRequest.setPackageId(mPkgId);
                NTF_Utils.showProgressDialog(getActivity());
                getPkgDetailsPresenter.gettingAllPkgDetails(null, getPackageDetailsRequest);
            } else {
                NTF_Utils.showAlert(getActivity(), "", "Unable to process your request.", null);
            }
        } else {
            NTF_Utils.showNoNetworkAlert(getActivity());
            ((MainActivity) getActivity()).registerReceiver();
        }
    }

    @Override
    public void onPackageFound(JSONObject jsonObject) {
        NTF_Utils.hideProgressDialog();
        bindToViews(jsonObject);
    }

    // Binding the data to corresponding views...
    private void bindToViews(JSONObject jsonObject) {
        try {
            mRecipientId = jsonObject.optString("recipient_id");
            String recipientName = jsonObject.optString("recipient_name");
            String addr1 = jsonObject.optString("recipient_address1");
            String trackingNumber = jsonObject.optString("tracking_number");
            String shippingCarrier = jsonObject.optString("shipping_carrier");
            String handling = jsonObject.optString("handling");
            String shelf = jsonObject.optString("shelf");
            String sender = jsonObject.optString("sender");
            String service_type = jsonObject.optString("service_type");
            String package_type = jsonObject.optString("package_type");
            String tag_number = jsonObject.optString("tag_number");
            String package_condition = jsonObject.optString("package_condition");
            String staff_note = jsonObject.optString("staff_note");
            String logout_description = jsonObject.optString("logout_description");
            String custom_message = jsonObject.optString("custom_message");
            String mailroom = jsonObject.optString("mailroom_id");

            viewsArrayList.add(namelayout);
            viewsArrayList.add(unitlayout);
            viewsArrayList.add(trackingNolayout);
            viewsArrayList.add(carrierlayout);
            viewsArrayList.add(statuslayout);
            viewsArrayList.add(logInlayout);
            viewsArrayList.add(loginBylayout);
            viewsArrayList.add(logoutlayout);
            viewsArrayList.add(logoutBylayout);
            viewsArrayList.add(taglayout);
            if (mailroom != null && !mailroom.equalsIgnoreCase("null") && !mailroom.isEmpty()) {
                ArrayList<SpinnerData> mMailRoomList = SpinnerData.getList(mGobalJsonData.getJSONArray("mailrooms"), null).getList();
                if (mMailRoomList.size() > 1) {
                    for (int i = 0, len = mMailRoomList.size(); i < len; i++) {
                        if (mMailRoomList.get(i).getValue().equalsIgnoreCase(mailroom)) {
                            mailroomView.setVisibility(View.VISIBLE);
                            mailroomlayout.setVisibility(View.VISIBLE);
                            mTextViewMailroom.setText(mMailRoomList.get(i).getName());
                            viewsArrayList.add(mailroomlayout);
                            break;
                        }
                    }
                }
            }

            viewsArrayList.add(senderlayout);
            viewsArrayList.add(serviceTypelayout);
            viewsArrayList.add(pkgTypelayout);
            viewsArrayList.add(pkgCondlayout);
            viewsArrayList.add(llWeight);
            viewsArrayList.add(llDimensions);
            viewsArrayList.add(mLlPoView);
            viewsArrayList.add(shelflayout);
            viewsArrayList.add(specialHandlinglayout);
            viewsArrayList.add(customMessageLayout);
            viewsArrayList.add(staffNoteLayout);

            String poNumber = jsonObject.optString("po_number");
            if (!poNumber.equalsIgnoreCase("null")) {
                mTextViewPoNumber.setText(poNumber);
            }

            if (!jsonObject.optString("weight").equalsIgnoreCase("null")) {
                mTextViewWeight.setText(jsonObject.optString("weight"));
            }

            if (!jsonObject.optString("dimensions").equalsIgnoreCase("null")) {
                mTextViewDimensions.setText(jsonObject.optString("dimensions"));
            }

            for (int i = 0; i < viewsArrayList.size(); i++) {
                if (i % 2 == 0) {
                    viewsArrayList.get(i).setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.background_light_color));
                } else {
                    viewsArrayList.get(i).setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
                }
            }

            if (jsonObject.has("package_notification_log") && jsonObject.opt("package_notification_log") instanceof JSONArray) {
                notification_jsonArray = jsonObject.getJSONArray("package_notification_log");
                if (notification_jsonArray.length() > 0) {
                    //     mNotificationTitleTextView.setVisibility(View.VISIBLE);
                    //      mNotificationEmptyLinear.setVisibility(View.GONE);
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    for (int i = 0, len = notification_jsonArray.length(); i < len; ++i) {
                        JSONObject notificationJsonObject = notification_jsonArray.getJSONObject(i);
                        String date = notificationJsonObject.optString("date_posted");
                        String notificationType = notificationJsonObject.optString("notification_type");
                        String mode = notificationJsonObject.optString("mode");
                        LinearLayout subLinearLayout = (LinearLayout) inflater.inflate(R.layout.row_fragment_pkg_details, null);
                        LinearLayout parentLayout = (LinearLayout) subLinearLayout.findViewById(R.id.parentLayout);
                        ((TextView) subLinearLayout.findViewById(R.id.textview_date)).setText(date);
                        ((TextView) subLinearLayout.findViewById(R.id.textview_notification_type)).setText(notificationType);
                        ((TextView) subLinearLayout.findViewById(R.id.textview_mode)).setText(mode);
                        if (i % 2 == 0) {
                            parentLayout.setBackgroundColor(getResources().getColor(R.color.background_light_color));
                        }
                        if (isTablet) {
                            mSecondLinear.addView(subLinearLayout);
                        } else {
                            mMainLinearLayout.addView(subLinearLayout);
                        }
                    }
                }
            }

            mTextviewRecipientName.setText(recipientName);
            mTextviewUnitnumber.setText(addr1.equalsIgnoreCase("null") ? "" : addr1);
            mTextviewTrackingNumber.setText(trackingNumber.equalsIgnoreCase("null") ? "" : trackingNumber);

            setDate(jsonObject.optString("date_received"), mTextviewReceived);
            setDate(jsonObject.optString("date_pickedup"), mTextviewPickedUp);


            String logInByString = jsonObject.optString("logged_in_by");
            String logOutByString = jsonObject.optString("logged_out_by");

            if (logInByString != null && !logInByString.isEmpty() && !logInByString.equalsIgnoreCase("null")) {
                mTextViewLoginBy.setText(logInByString);
            }

            if (logOutByString != null && !logOutByString.isEmpty() && !logOutByString.equalsIgnoreCase("null")) {
                mTextViewLogOutBy.setText(logOutByString);
            }

            mTextviewCarrier.setText(shippingCarrier.equalsIgnoreCase("null") ? "" : shippingCarrier);
            mTextviewSpecialHandling.setText(handling.equalsIgnoreCase("null") ? "" : handling);
            mTextviewShelfLocation.setText(shelf.equalsIgnoreCase("null") ? "" : shelf);
            mTextviewSender.setText(sender.equalsIgnoreCase("null") ? "" : sender);
            mTextviewServiceType.setText(service_type.equalsIgnoreCase("null") ? "" : service_type);
            mTextviewPackageType.setText(package_type.equalsIgnoreCase("null") ? "" : package_type);
            mTextviewTagNumber.setText(tag_number.equalsIgnoreCase("null") ? "" : tag_number);
            mTextviewPackageCondition.setText(package_condition.equalsIgnoreCase("null") ? "" : package_condition);
            mTextviewStaffNote.setText(staff_note.equalsIgnoreCase("null") ? "" : staff_note);
            mTextviewStatus.setText(logout_description.equalsIgnoreCase("null") ? "" : logout_description);
            Log.v("status: ", logout_description);
            mTextviewCustomMessage.setText(custom_message.equalsIgnoreCase("null") ? "" : custom_message);

            String eSign = jsonObject.optString("esignature");

            if (eSign != null && !eSign.equalsIgnoreCase("null") && !eSign.isEmpty()) {
                mSignatureLinearLayout.setVisibility(View.VISIBLE);
                mSignatureFrameLayout.setVisibility(View.VISIBLE);
                mTextViewNoSign.setVisibility(View.GONE);

                try {
                    Picasso.with(getActivity()).load(jsonObject.optString("esignature")).into(mImageVieweSignature, new
                            Callback() {
                                @Override
                                public void onSuccess() {
                                    onSignatureSuccessfullyLoaded(mImageVieweSignature);
                                }

                                @Override
                                public void onError() {

                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                mSignatureLinearLayout.setVisibility(View.VISIBLE);
                mSignatureFrameLayout.setVisibility(View.VISIBLE);
                mTextViewNoSign.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            }

            JSONObject packagesImagesJsonObject = jsonObject.optJSONObject("package_images");

            JSONArray loginImagesJsonArray = packagesImagesJsonObject.optJSONArray("login_images");

            if (loginImagesJsonArray != null && loginImagesJsonArray.length() > 0) {

                for (int i = 0, len = loginImagesJsonArray.length(); i < len; i++) {
                    // new LoadLoginAndLogoutImages().execute(loginImagesJsonArray.getString(i));
                    loginImagesList.add((String) loginImagesJsonArray.get(i));
                }
            }

            JSONArray logoutImagesJsonArray = packagesImagesJsonObject.optJSONArray("logout_images");

            if (logoutImagesJsonArray != null && logoutImagesJsonArray.length() > 0) {
                for (int i = 0, len = logoutImagesJsonArray.length(); i < len; i++) {
                    logoutImagesList.add((String) logoutImagesJsonArray.get(i));
                }
            }

            if (loginImagesList.size() > 0) {

                mLoginImagesLinearLayout.setVisibility(View.VISIBLE);
                mTextViewLoginNoImage.setVisibility(View.GONE);

                for (int i = 0, len = loginImagesList.size(); i < len; ++i) {
                    if (i < 3) {

                        final int j = i;
                        mLoginFrameLayoutList.get(i).setVisibility(View.VISIBLE);
                        Glide.with(this)
                                .load(loginImagesList.get(i))
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        mLoginProgressBar.get(j).setVisibility(View.GONE);
                                        return false;
                                    }
                                }).into(mLoginImageViewsArrayList.get(i));
                        mLoginImageViewsArrayList.get(i).setTag(loginImagesList.get(i));
                    }
                }
            } else {
                mLoginImagesLinearLayout.setVisibility(View.VISIBLE);
                mTextViewLoginNoImage.setVisibility(View.VISIBLE);
            }

            if (logoutImagesList.size() > 0) {

                mLogoutIamgesLinearLayout.setVisibility(View.VISIBLE);
                mtextViewLogOutNoImage.setVisibility(View.GONE);
                for (int i = 0, len = logoutImagesList.size(); i < len; ++i) {
                    if (i < 3) {

                        final int j = i;
                        mLogoutFrameLayoutList.get(i).setVisibility(View.VISIBLE);
                        Glide.with(this)
                                .load(logoutImagesList.get(i))
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        mLogoutProgressBar.get(j).setVisibility(View.GONE);
                                        return false;
                                    }
                                }).into(mLogoutImageViewsArrayList.get(i));
                        mLogoutImageViewsArrayList.get(i).setTag(logoutImagesList.get(i));
                    }
                }
            } else {
                mLogoutIamgesLinearLayout.setVisibility(View.VISIBLE);
                mtextViewLogOutNoImage.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onSignatureSuccessfullyLoaded(ImageView imageView){
        try{
            final ViewTreeObserver observer= imageView.getViewTreeObserver();
            observer.addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            try {
                                int height=imageView.getHeight();//228
                                int width=imageView.getWidth();//388
                                int reqWidth = mSignatureFrameLayout.getWidth() - 4;
                                float ratio = ((float) height) / width;;
                                int reqHeight = Math.round(reqWidth * ratio);
                                imageView.getLayoutParams().height = (int) reqHeight;
                                imageView.getLayoutParams().width = reqWidth;
                                mSignatureFrameLayout.getLayoutParams().height = (int) reqHeight+4;
                                imageView.requestLayout();
                                mSignatureFrameLayout.requestLayout();
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });}catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onPackageNotFound(String message) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(getActivity(), "", message, null);
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

    @OnClick({R.id.backImage, R.id.mobile_left_linear})
    public void onBackClick() {
        NTF_Utils.hideKeyboard(getActivity());
        getActivity().onBackPressed();
    }

    public String getLocalDate_and_Time(String serverDateTimeString) {
        String localDateTime;
        try {
            String dateFormat = "MMMM d, yyyy hh:mm a";
            if (DateFormat.is24HourFormat(getActivity())){
                dateFormat = "MMMM d, yyyy HH:mm";
            }

            SimpleDateFormat serverFormatter = new SimpleDateFormat(DATE_TIME_FORMATE);
            serverFormatter.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
            Date serverDateTime = serverFormatter.parse(serverDateTimeString);

            SimpleDateFormat localFormatter = new SimpleDateFormat(dateFormat);
            localFormatter.setTimeZone(TimeZone.getTimeZone(ntf_Preferences.get(Prefs_Keys.TIMEZONE)));
            localDateTime = localFormatter.format(serverDateTime);


        } catch (Exception e) {
            e.printStackTrace();
            localDateTime = "";
        }
        return localDateTime;
    }


    // Set the Date to required format...
    private void setDate(String dateInString, TextView view) {
        try {
            if (dateInString.equalsIgnoreCase("0000-00-00 00:00:00")) {
                view.setText("");
            } else {
                view.setText(getLocalDate_and_Time(dateInString).replace("a.m.", "AM").replace("am", "AM").replace("p.m.", "PM").replace("pm", "PM") + " " + NTF_Utils.getTimeZoneTYPE(ntf_Preferences.get(Prefs_Keys.TIMEZONE), dateInString, ntf_Preferences));
//                view.setText(getLocalDate_and_Time(dateInString).replace("a.m.", "AM").replace("am", "AM").replace("p.m.", "PM").replace("pm", "PM") + " " + ntf_Preferences.get(NTF_Constants.Prefs_Keys.TIMEZONE_SHORTCODE));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
