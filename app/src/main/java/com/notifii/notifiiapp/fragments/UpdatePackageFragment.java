

package com.notifii.notifiiapp.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.activities.MainActivity;
import com.notifii.notifiiapp.adapters.PkgLogoutDetailsAdapter;
import com.notifii.notifiiapp.adapters.SenderAdapter;
import com.notifii.notifiiapp.adapters.ShelfAdapter;
import com.notifii.notifiiapp.adapters.SpecialHandlingsAdapter;
import com.notifii.notifiiapp.adapters.SpinnerHintAdapter;
import com.notifii.notifiiapp.adapters.SpinnerMarkAdapter;
import com.notifii.notifiiapp.base.NTF_BaseFragment;
import com.notifii.notifiiapp.customui.SpecialHandlingLinearLayout;
import com.notifii.notifiiapp.helpers.BitmapHelper;
import com.notifii.notifiiapp.helpers.SingleTon;
import com.notifii.notifiiapp.models.ListModel;
import com.notifii.notifiiapp.models.Package;
import com.notifii.notifiiapp.models.SenderData;
import com.notifii.notifiiapp.models.ShelfData;
import com.notifii.notifiiapp.models.SpecialHandling;
import com.notifii.notifiiapp.models.SpinnerData;
import com.notifii.notifiiapp.mvp.models.GetPackageDetailsRequest;
import com.notifii.notifiiapp.mvp.models.PackagePicture;
import com.notifii.notifiiapp.mvp.models.UpdatePackageRequest;
import com.notifii.notifiiapp.mvp.presenters.GetPkgDetailsPresenter;
import com.notifii.notifiiapp.mvp.presenters.UpdatePackagePresenter;
import com.notifii.notifiiapp.mvp.views.GetPkgDetailsView;
import com.notifii.notifiiapp.mvp.views.UpdatePackageView;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTouch;
import butterknife.Optional;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.notifii.notifiiapp.helpers.BitmapHelper.getBase64String;

public class UpdatePackageFragment extends NTF_BaseFragment implements UpdatePackageView, GetPkgDetailsView {

    @BindView(R.id.name_with_unit)
    TextView recipientName_with_unit_no;
    @BindView(R.id.tracking_no)
    TextView trackingNumber;
    @BindView(R.id.received)
    TextView dateRecievedTV;
    @BindView(R.id.custom_message)
    TextView customMessageTV;
    @BindView(R.id.change_history)
    TextView changeHistoryTV;
    @BindView(R.id.tag)
    TextView tagNumberTV;
    @BindView(R.id.parent_layout)
    LinearLayout parentLayout;
    @BindView(R.id.frame_login_image1)
    FrameLayout frameImage1;
    @BindView(R.id.frame_login_image2)
    FrameLayout frameImage2;
    @BindView(R.id.frame_login_image3)
    FrameLayout frameImage3;
    @BindView(R.id.login_image1_progressbar)
    ProgressBar progressBarImage1;
    @BindView(R.id.login_image2_progressbar)
    ProgressBar progressBarImage2;
    @BindView(R.id.login_image3_progressbar)
    ProgressBar progressBarImage3;
    @BindView(R.id.imageView1loginImage)
    ImageView image1;
    @BindView(R.id.imageView2loginImage)
    ImageView image2;
    @BindView(R.id.imageView3loginImage)
    ImageView image3;
    @BindView(R.id.single_edit_layout)
    LinearLayout singleEditLayout;
    @Nullable
    @BindView(R.id.carrier_spinner)
    Spinner spinnerCarrier;
    @BindView(R.id.editTextSender)
    AutoCompleteTextView senderEdit;
    @BindView(R.id.edit_shelf_location)
    AutoCompleteTextView mEditShelfLocation;
    @Nullable
    @BindView(R.id.service_text)
    TextView servicetypeText;
    @BindView(R.id.service_spinner)
    Spinner serviceSpinner;
    @BindView(R.id.Package_type_text)
    TextView packageTypeText;
    @BindView((R.id.package_type_spinner))
    Spinner packageTypeSpinner;
    @BindView(R.id.package_Condition_spinner)
    Spinner packageConditionSpinner;
    @BindView(R.id.Package_condition_text)
    TextView packageConditionText;
    @BindView(R.id.saveBtn)
    Button buttonUpdatePackage;
    @BindView(R.id.mailRoomSpinner)
    Spinner mSpinnerMailRoom;
    @BindView(R.id.mailroomTv)
    TextView mailRoomTv;
    @BindView(R.id.loginpicturesLayout)
    LinearLayout imagesLinearLayout;
    @BindView(R.id.mobile_left_linear)
    LinearLayout backLayout;
    @BindView(R.id.login_no_picture_view)
    TextView loginNoPictureView;
    @BindView(R.id.log_in_by)
    TextView log_in_by_TV;
    @BindView(R.id.image_pick1)
    ImageView imgView1;
    @BindView(R.id.image_pick2)
    ImageView imgView2;
    @BindView(R.id.image_pick3)
    ImageView imgView3;
    @BindView(R.id.imageButtonClose1)
    ImageView viewPicClose1;
    @BindView(R.id.imageButtonClose2)
    ImageView viewPicClose2;
    @BindView(R.id.imageButtonClose3)
    ImageView viewPicClose3;
    @BindView(R.id.fl_1)
    FrameLayout flView1;
    @BindView(R.id.fl_2)
    FrameLayout flView2;
    @BindView(R.id.fl_3)
    FrameLayout flView3;
    @BindView(R.id.gap1)
    LinearLayout mGap1;
    @BindView(R.id.gap2)
    LinearLayout mGap2;
    @BindView(R.id.gap3)
    LinearLayout mGap3;
    @BindView(R.id.edit_login_pictures)
    LinearLayout editLoginPicturesLayout;
    @BindView(R.id.editTextPONumber)
    EditText poNumberET;
    @BindView(R.id.editTextWeight)
    EditText weightET;
    @BindView(R.id.editTextDimensions)
    EditText dimensionsET;
    @BindView(R.id.editTextStaffNotes)
    EditText staffNotesET;
    @BindView(R.id.mailroomLayout)
    LinearLayout mailroomLayout;
    @BindView(R.id.carrier_text)
    TextView carrierTextView;
    @BindView(R.id.multiple_pkgs_listview)
    RecyclerView multipleEditListview;
    @BindView(R.id.shPL)
    SpecialHandlingLinearLayout shPL;
//    @BindView(R.id.specialhandlingsRV)
//    RecyclerView specialhandlingsRV;
//    @BindView(R.id.dummy_layout)
//    View specialhandlingdummylayout;

    private Activity activity;
    private ArrayList<SpinnerData> mServiceTypeList;
    private String mPkgType;
    private String mCarrier;
    private String mPkgCondition;
    private String mServiceType;
    private boolean isMailroomVisible;
    private ArrayList<SpecialHandling> specialHandlings;
    private boolean isMultipleEdit;
    private boolean isTablet;
    private String base64Str1, base64Str2, base64Str3;
    private Bitmap bitmap1, bitmap2, bitmap3;
    private int clickPhotoRequestCode;
    private ArrayList<SpinnerData> mCarrierList;
    private ArrayList<SpinnerData> mPkgTypeList;
    private ArrayList<SpinnerData> mPkgConditionsList;
    private ArrayList<String> packageImagesList = new ArrayList<>();
    private ArrayList<ProgressBar> ProgessBarimagesList = new ArrayList<>();
    private ArrayList<FrameLayout> frameImagesList = new ArrayList<>();
    private ArrayList<ImageView> ImageViewImagesList = new ArrayList<>();
    private ArrayList<SpinnerData> mMailRoomList;
    private JSONObject mGobalJsonData;
    private UpdatePackagePresenter updatePackagePresenter;
    private GetPkgDetailsPresenter packageDetailsPresenter;
    private int width;
    private String packageId = "";
    private PkgLogoutDetailsAdapter pkgLogoutDetailsAdapter;
    private ArrayList<Package> pkgList;
    private String mailroom_id, response_package_id, logout_code_id, recipient_id;
    private ArrayList<ShelfData> mShelfList;
    private ArrayList<SenderData> mSenderList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_update_package, container, false);
        ButterKnife.bind(this, mainView);
        return mainView;
    }

    @Override
    public void onStackChanged() {
        super.onStackChanged();
        if (NTF_Utils.getCurrentFragment(this) != null && NTF_Utils.getCurrentFragment(this) instanceof UpdatePackageFragment) {
            onFragmentPopup(true);
        } else {
            onFragmentPopup(false);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = getActivity();
    }

    //to get selected packages list from previous screen
    public void setList(ArrayList<Package> list) {
        this.pkgList = list;
        try {
            if (pkgList.size() > 1) {
                isMultipleEdit = true;
            } else {
                isMultipleEdit = false;
                packageId = list.get(0).getPackageId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        doBackButtonHandle();
        updatePackagePresenter = new UpdatePackagePresenter();
        updatePackagePresenter.attachMvpView(this);
        packageDetailsPresenter = new GetPkgDetailsPresenter();
        packageDetailsPresenter.attachMvpView(this);
        setSpecialHandlingData(null);

        if (!isMultipleEdit) {//FOR SINGLE EDIT
            setToolbarTitle("Update Package");
            getPackage();
            singleEditLayout.setVisibility(View.VISIBLE);
            frameImagesList.add(frameImage1);
            frameImagesList.add(frameImage2);
            frameImagesList.add(frameImage3);
            ProgessBarimagesList.add(progressBarImage1);
            ProgessBarimagesList.add(progressBarImage2);
            ProgessBarimagesList.add(progressBarImage3);
            ImageViewImagesList.add(image1);
            ImageViewImagesList.add(image2);
            ImageViewImagesList.add(image3);
        } else {//for multiple edit
            setToolbarTitle("Update Multiple Packages (" + pkgList.size() + ")");
            setCarrierSpinnerData("");
            setServiceTypeSpinnerData("");
            setPackageTypeSpinnerData("");
            setPkgConditionSpinnerData("");
            setMailRoomSpinnerData("");
            multipleEditListview.setVisibility(View.VISIBLE);
            bindToListView();
            setSenderData("");
            setShelfData("");
        }
        setToolbarActionButtons(true, false, 0);
    }

    @OnClick(R.id.carrierLayout)
    void onClick() {
        NTF_Utils.hideKeyboard(getActivity() != null ? getActivity() : activity);
        spinnerCarrier.performClick();
    }

    @OnItemSelected(R.id.carrier_spinner)
    public void carrierspinnerItemSelected(Spinner spinner, int position) {
        if (spinner.getSelectedItem() != null) {
            mCarrier = ((SpinnerData) spinnerCarrier.getSelectedItem()).getValue();
            SpinnerData.resetData(mCarrierList);
            ((SpinnerData) spinnerCarrier.getSelectedItem()).setSelected(true);
            carrierTextView.setText(((SpinnerData) spinnerCarrier.getSelectedItem()).getName());
        }
    }


    @OnClick(R.id.service_spinner_layout)
    void onClick(View view) {
        NTF_Utils.hideKeyboard(getActivity() != null ? getActivity() : activity);
        serviceSpinner.performClick();
    }

    @OnItemSelected(R.id.service_spinner)
    void onSelectService(Spinner spinner, int position) {
        if (spinner.getSelectedItem() != null) {
            mServiceType = ((SpinnerData) spinner.getSelectedItem()).getValue();
            SpinnerData.resetData(mServiceTypeList);
            ((SpinnerData) serviceSpinner.getSelectedItem()).setSelected(true);
            servicetypeText.setText(((SpinnerData) serviceSpinner.getSelectedItem()).getName());
        }
    }

    @OnClick(R.id.package_type_lyt)
    void onClickPkgType(View view) {
        NTF_Utils.hideKeyboard(getActivity() != null ? getActivity() : activity);
        packageTypeSpinner.performClick();
    }

    @OnItemSelected(R.id.package_type_spinner)
    public void pkgspinnerItemSelected(Spinner spinner, int position) {
        if (spinner.getSelectedItem() != null) {
            mPkgType = ((SpinnerData) spinner.getSelectedItem()).getValue();
            SpinnerData.resetData(mPkgTypeList);
            ((SpinnerData) packageTypeSpinner.getSelectedItem()).setSelected(true);
            packageTypeText.setText(((SpinnerData) packageTypeSpinner.getSelectedItem()).getName());
        }
    }

    @OnClick(R.id.package_conditn_lyt)
    void onClickPkgCodtnLyt(View view) {
        NTF_Utils.hideKeyboard(getActivity() != null ? getActivity() : activity);
        packageConditionSpinner.performClick();
    }

    @OnItemSelected(R.id.package_Condition_spinner)
    void PkgCondtnItemSelected(Spinner spinner, int position) {
        if (spinner.getSelectedItem() != null) {
            mPkgCondition = ((SpinnerData) spinner.getSelectedItem()).getValue();
            SpinnerData.resetData(mPkgConditionsList);
            ((SpinnerData) packageConditionSpinner.getSelectedItem()).setSelected(true);
            packageConditionText.setText(((SpinnerData) packageConditionSpinner.getSelectedItem()).getName());
        }
    }

    private void setServiceTypeSpinnerData(String serviceType) {
        try {
            ListModel listModel = SpinnerData.getList(mGobalJsonData.getJSONArray("service_types"), serviceType);
            mServiceTypeList = listModel.getList();
            if (mServiceTypeList != null && !mServiceTypeList.isEmpty()) {
                if (mServiceTypeList.size() >= 10) {
                    NTF_Utils.setSpinnerHeight(getActivity(), serviceSpinner);
                }
                SpinnerMarkAdapter adapter = new SpinnerMarkAdapter(getActivity(), mServiceTypeList);
                serviceSpinner.setAdapter(new SpinnerHintAdapter(adapter, R.layout.spinner_hint, getActivity()));
                if (listModel.getPosition() != -1) {
                    serviceSpinner.setSelection(listModel.getPosition() + 1);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Preparing the Service request for getting package details...
    private void getPackage() {
        Context context = getActivity();
        if (NTF_Utils.isOnline(context)) {
            if (!TextUtils.isEmpty(packageId)) {
                getPackage(ntf_Preferences.get(Prefs_Keys.SESSION_ID),
                        ntf_Preferences.get(Prefs_Keys.AUTHENTICATION_TOKEN),
                        ntf_Preferences.get(Prefs_Keys.ACCOUNT_ID),
                        packageId, ntf_Preferences.get(Prefs_Keys.USER_ID));
            } else {
                NTF_Utils.showAlert(getActivity(), "", "Unable to process your request.", null);
            }
        } else {
            NTF_Utils.showNoNetworkAlert(getActivity());
            ((MainActivity) getActivity()).registerReceiver();
        }
    }


    // Service request for getting the package details...
    public void getPackage(String session_id, String authentication_token, String account_id, String package_id, String userID) {
        try {
            NTF_Utils.showProgressDialog(getActivity());
            GetPackageDetailsRequest request = new GetPackageDetailsRequest();
            request.setSessionId(session_id);
            request.setUserId(userID);
            request.setAuthenticationToken(authentication_token);
            request.setAccountId(account_id);
            request.setPackageId(package_id);
            packageDetailsPresenter.gettingAllPkgDetails(null, request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPkgConditionSpinnerData(String packageCondition) {
        try {
            ListModel listModel = SpinnerData.getList(mGobalJsonData.getJSONArray("package_conditions"), packageCondition);
            mPkgConditionsList = listModel.getList();
            if (mPkgConditionsList != null && !mPkgConditionsList.isEmpty()) {
                if (mPkgConditionsList.size() >= 10) {
                    NTF_Utils.setSpinnerHeight(getActivity(), packageConditionSpinner);
                }
                SpinnerMarkAdapter adapter = new SpinnerMarkAdapter(getActivity(), mPkgConditionsList);
                packageConditionSpinner.setAdapter(new SpinnerHintAdapter(adapter, R.layout.spinner_hint, getActivity()));
                if (listModel.getPosition() != -1) {
                    packageConditionSpinner.setSelection(listModel.getPosition() + 1);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.mailroomLayout)
    void onClickmailroomLyt(View view) {
        NTF_Utils.hideKeyboard(getActivity() != null ? getActivity() : activity);
        mSpinnerMailRoom.performClick();
    }

    @OnItemSelected(R.id.mailRoomSpinner)
    public void spinnerItemSelected(Spinner spinner, int position) {
        if (spinner.getSelectedItem() != null) {
            SpinnerData.resetData(mMailRoomList);
            ((SpinnerData) mSpinnerMailRoom.getSelectedItem()).setSelected(true);
            mailRoomTv.setText(((SpinnerData) mSpinnerMailRoom.getSelectedItem()).getName());
        }
    }

    // Setting MailRoom Spinner Data
    private void setMailRoomSpinnerData(String mailroomId) {
        try {
            ListModel listModel = SpinnerData.getList(mGobalJsonData.getJSONArray("mailrooms"), mailroomId);
            mMailRoomList = listModel.getList();
            // If the Mailroom List is empty then there is no need to display the MailRoom column in the layout...
            if (mMailRoomList != null && !mMailRoomList.isEmpty() && mMailRoomList.size() > 1) {
                mailroomLayout.setVisibility(View.VISIBLE);
                isMailroomVisible = true;
                if (mMailRoomList.size() >= 10) {
                    NTF_Utils.setSpinnerHeight(getActivity(), mSpinnerMailRoom);
                }
                SpinnerMarkAdapter adapter = new SpinnerMarkAdapter(getActivity(), mMailRoomList);
                mSpinnerMailRoom.setAdapter(new SpinnerHintAdapter(adapter, R.layout.spinner_hint, getActivity()));
                if (listModel.getPosition() != -1) {
                    mSpinnerMailRoom.setSelection(listModel.getPosition() + 1);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @OnTouch(R.id.multiple_pkgs_listview)
    boolean onTouch(View view) {
        view.getParent().requestDisallowInterceptTouchEvent(true);
        return false;
    }

    // Binding the packages list to the ListView
    public void bindToListView() {
        if (pkgList != null && !pkgList.isEmpty()) {
            pkgLogoutDetailsAdapter = new PkgLogoutDetailsAdapter(getActivity(), pkgList, 1);
            multipleEditListview.setLayoutManager(new LinearLayoutManager(getActivity()));
            multipleEditListview.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (((3) * getResources().getDimension(R.dimen.pkg_update_list_item_height)) + 30)));
            multipleEditListview.setAdapter(pkgLogoutDetailsAdapter);
            pkgLogoutDetailsAdapter.setNoticeCloseListener(new PkgLogoutDetailsAdapter.NoticeItemCloseListener() {
                @Override
                public void onCloseClicked(int position) {
                    confirmAlert(position);
                }
            });
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGobalJsonData = NTF_Utils.getGlobalData(getActivity());
        specialHandlings = SpecialHandling.getSpecialHandling(mGobalJsonData, getActivity()!= null?getActivity():activity);
        width = getResources().getDisplayMetrics().widthPixels;
        isTablet = getActivity().getResources().getBoolean(R.bool.isTablet);
    }

    // Setting carrier spinner data
    public void setCarrierSpinnerData(String shippingCarrier) {
        try {
            NTF_Utils.hideKeyboard(getActivity());
            ListModel carrierListModel = SpinnerData.getList(mGobalJsonData.getJSONArray("shipping_carriers"), shippingCarrier);
            mCarrierList = carrierListModel.getList();
            if (mCarrierList != null && !mCarrierList.isEmpty()) {
                if (mCarrierList.size() >= 10) {
                    NTF_Utils.setSpinnerHeight(getActivity(), spinnerCarrier);
                }
                SpinnerMarkAdapter adapter = new SpinnerMarkAdapter(getActivity(), mCarrierList);
                spinnerCarrier.setAdapter(new SpinnerHintAdapter(adapter, R.layout.spinner_hint, getActivity()));
                if (carrierListModel.getPosition() != -1) {
                    spinnerCarrier.setSelection(carrierListModel.getPosition() + 1);
                }
            }
            if (carrierListModel.getPosition()==-1){
                mCarrier = shippingCarrier;
                carrierTextView.setText(mCarrier);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPackageTypeSpinnerData(String package_type) {
        try {
            ListModel listModel = SpinnerData.getList(mGobalJsonData.getJSONArray("package_types"), package_type);
            mPkgTypeList = listModel.getList();
            if (mPkgTypeList != null && !mPkgTypeList.isEmpty()) {
                if (mPkgTypeList.size() >= 10) {
                    NTF_Utils.setSpinnerHeight(getActivity(), packageTypeSpinner);
                }
                SpinnerMarkAdapter adapter = new SpinnerMarkAdapter(getActivity(), mPkgTypeList);
                packageTypeSpinner.setAdapter(new SpinnerHintAdapter(adapter, R.layout.spinner_hint, getActivity()));
                if (listModel.getPosition() != -1) {
                    packageTypeSpinner.setSelection(listModel.getPosition() + 1);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Optional
    @OnItemSelected(R.id.spinnerCarrier)
    public void carrierSpinnerItemSelected(Spinner spinner, int position) {
        if (spinner.getSelectedItem() != null) {
            mCarrier = ((SpinnerData) spinnerCarrier.getSelectedItem()).getValue();
            SpinnerData.resetData(mCarrierList);
            ((SpinnerData) spinnerCarrier.getSelectedItem()).setSelected(true);
            carrierTextView.setText(((SpinnerData) spinnerCarrier.getSelectedItem()).getName());
        }
    }


    private void setSpecialHandlingData(String handling) {
        if (handling != null) {
            String[] handlings = handling.split(",");
            for (SpecialHandling specialHandling : specialHandlings) {
                for (int i = 0; i < handlings.length; i++) {
                    if (specialHandling.getValue().equalsIgnoreCase(handlings[i].trim())) {
                        specialHandling.setSelected(true);
                        break;
                    }
                }
            }
        }
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
                shPL.addView(specialHandling.getHolder().parentLayout);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        SpecialHandlingsAdapter specialHandlingsAdapter = new SpecialHandlingsAdapter(getActivity(), specialHandlings);
//        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), isTablet ? 4 : 2);
//        specialhandlingsRV.setLayoutManager(layoutManager);
//        specialhandlingsRV.setAdapter(specialHandlingsAdapter);
//        if (specialHandlings.size() == 0 && specialhandlingdummylayout != null) {
//            specialhandlingdummylayout.setVisibility(View.VISIBLE);
//        } else if (specialhandlingdummylayout != null) {
//            specialhandlingdummylayout.setVisibility(View.GONE);
//        }
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
                    removePackage(position);
                    if (pkgList != null) {
                        setToolbarTitle("Update Multiple Packages (" + pkgList.size() + ")");
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
            if (isTablet) {
                dialogWidth = (int) (width * 0.85f);
            }
            int dialogHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(dialogWidth, dialogHeight);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removePackage(int position) {
        pkgList.remove(position);
        pkgLogoutDetailsAdapter.notifyDataSetChanged();
    }


    @OnClick({R.id.backImage, R.id.mobile_left_linear})
    void onClickmobileLeftLinear() {
        navigateToBack();
    }

    @OnClick(R.id.image_pick1)
    void onClickImagePick1(View view) {
        if (System.currentTimeMillis() - mLastClickTime < 500) {
            return;
        }
        mLastClickTime = System.currentTimeMillis();
        if (bitmap1 != null) {
            NTF_Utils.showImageAlert(bitmap1, getActivity() != null ? getActivity() : activity);
        } else {
            showAlert(1);
        }
        Log.d("TRACKING", "executed");
    }

    @OnClick(R.id.image_pick2)
    void onClickImagePick2(View view) {
        if (System.currentTimeMillis() - mLastClickTime < 500) {
            return;
        }
        mLastClickTime = System.currentTimeMillis();
        if (bitmap2 != null) {
            NTF_Utils.showImageAlert(bitmap2, getActivity() != null ? getActivity() : activity);
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
            NTF_Utils.showImageAlert(bitmap3, getActivity() != null ? getActivity() : activity);
        } else {
            showAlert(3);
        }
    }

    @OnClick(R.id.imageView1loginImage)
    void onClickImageView1LognImg(View view) {
        if (packageImagesList.get(0) != null)
            NTF_Utils.showImageAlert(getActivity(), packageImagesList.get(0));
    }

    @OnClick(R.id.imageView2loginImage)
    void onClickImageView2LoginImg(View view) {
        if (packageImagesList.get(1) != null)
            NTF_Utils.showImageAlert(getActivity(), packageImagesList.get(1));
    }

    @OnClick(R.id.imageView3loginImage)
    void onClickImageView3LognImg(View view) {
        if (packageImagesList.get(2) != null)
            NTF_Utils.showImageAlert(getActivity(), packageImagesList.get(2));
    }

    @OnClick(R.id.imageButtonClose1)
    void onClickImgBtnClose1(View v) {
        deleteImage(1);
    }

    @OnClick(R.id.imageButtonClose2)
    void onClickImgBtnClose2(View view) {
        deleteImage(2);
    }

    @OnClick(R.id.imageButtonClose3)
    void onClickImgBtnClose3(View view) {
        deleteImage(3);
    }


    @OnClick(R.id.saveBtn)
    void onClickSaveBtn(View view) {
        new CovertBase64Task().execute();
    }

    // This method used for opening the camera for corresponding image button click...
    private void showAlert(final int imgPos) {
        if (imgPos == 1) {
            openCameraPickPhoto(CLICK_PHOTO_REQUEST_CODE_1);
        } else if (imgPos == 2) {
            openCameraPickPhoto(CLICK_PHOTO_REQUEST_CODE_2);
        } else {
            openCameraPickPhoto(CLICK_PHOTO_REQUEST_CODE_3);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK && requestCode == CLICK_PHOTO_REQUEST_CODE_1) {
            bitmap1 = SingleTon.getInstance().getBitmap1();
            imgView1.setImageBitmap(bitmap1);
            viewPicClose1.setVisibility(View.VISIBLE);
        } else if (resultCode == getActivity().RESULT_OK && requestCode == CLICK_PHOTO_REQUEST_CODE_2) {
            bitmap2 = SingleTon.getInstance().getBitmap2();
            imgView2.setImageBitmap(bitmap2);
            viewPicClose2.setVisibility(View.VISIBLE);
        } else if (resultCode == getActivity().RESULT_OK && requestCode == CLICK_PHOTO_REQUEST_CODE_3) {
            bitmap3 = SingleTon.getInstance().getBitmap3();
            imgView3.setImageBitmap(bitmap3);
            viewPicClose3.setVisibility(View.VISIBLE);
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


    @Override
    public void onEmptyAllParameters() {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(getActivity(), "", "Please enter at least one value.", null);
    }

    @Override
    public void multipleEditRequestValidated(JSONObject request) {
        try {
            NTF_Utils.showProgressDialog(getActivity());
            request.put("session_id", ntf_Preferences.get(Prefs_Keys.SESSION_ID));
            request.put("authentication_token", ntf_Preferences.get(Prefs_Keys.AUTHENTICATION_TOKEN));
            request.put("account_id", ntf_Preferences.get(Prefs_Keys.ACCOUNT_ID));
            request.put("user_id", ntf_Preferences.get(Prefs_Keys.USER_ID));
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), request.toString());
            updatePackagePresenter.updatingMultiplePackages("", requestBody);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdatingSinglePkgSuccess(JSONObject jsonObject) {
        NTF_Utils.hideProgressDialog();
        navigateToBack();
    }

    private void navigateToBack() {
        try {
            if (getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
            }
            getTargetFragment().onActivityResult(
                    getTargetRequestCode(),
                    Activity.RESULT_OK,
                    new Intent().putExtra(Extras_Keys.REFRESH_LOG_PACKAGE_OUT_LIST, true));
            NTF_Utils.hideKeyboard(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdatingMultiplePkgsSuccess() {
        NTF_Utils.hideProgressDialog();
        navigateToBack();

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

    private void bindToViews(JSONObject jsonObject) {
        try {
            response_package_id = jsonObject.optString("package_id");
            recipient_id = jsonObject.optString("recipient_id");
            String recipient_name = jsonObject.optString("recipient_name");
            String recipient_address1 = jsonObject.optString("recipient_address1");
            String tracking_number = jsonObject.optString("tracking_number");
            String shippingCarrier = jsonObject.optString("shipping_carrier");
            String date_received = jsonObject.optString("date_received");
            String handling = jsonObject.optString("handling");
            String shelf = jsonObject.optString("shelf");
            String sender = jsonObject.optString("sender");
            String service_type = jsonObject.optString("service_type");
            String package_type = jsonObject.optString("package_type");
            String tag_number = jsonObject.optString("tag_number");
            String po_number = jsonObject.optString("po_number");
            String package_condition = jsonObject.optString("package_condition");
            String staff_note = jsonObject.optString("staff_note");
            String change_history = jsonObject.optString("change_history");
            logout_code_id = jsonObject.optString("logout_code_id");
            String logged_in_by = jsonObject.optString("logged_in_by");
            String custom_message = jsonObject.optString("custom_message");
            String weight = jsonObject.optString("weight");
            String dimensions = jsonObject.optString("dimensions");
            mailroom_id = jsonObject.optString("mailroom_id");

            setShelfData(shelf);
            setSenderData(sender);
            if (log_in_by_TV != null && logged_in_by != null && !logged_in_by.equalsIgnoreCase("null")) {
                log_in_by_TV.setText(" " + logged_in_by);
            }
            if (recipientName_with_unit_no != null) {
                String nametext = "";
                if (recipient_name != null && !recipient_name.equalsIgnoreCase("null")) {
                    nametext = recipient_name;
                }
                if (recipient_address1 != null && !recipient_address1.equalsIgnoreCase("null") && !recipient_address1.isEmpty()) {
                    nametext = nametext + ", " + recipient_address1;
                }
                recipientName_with_unit_no.setText(nametext);
            }

            if (tracking_number != null && !tracking_number.equalsIgnoreCase("null")) {
                trackingNumber.setText(tracking_number);
            }


            if (tag_number != null && !tag_number.equalsIgnoreCase("null")) {
                tagNumberTV.setText(tag_number);
            }

            if (shelf != null && !shelf.equalsIgnoreCase("null")) {
                mEditShelfLocation.setText(shelf);
            }
            if (sender != null && !sender.equalsIgnoreCase("null")) {
                senderEdit.setText(sender);
            }

            if (date_received != null && !date_received.equalsIgnoreCase("null")) {
                setDate(date_received, dateRecievedTV);
            }

            if (po_number != null && !po_number.equalsIgnoreCase("null")) {
                poNumberET.setText(po_number);
            }

            if (shelf != null && !shelf.equalsIgnoreCase("null")) {
                mEditShelfLocation.setText(shelf);
            }
            if (weight != null && !weight.equalsIgnoreCase("null")) {
                weightET.setText(weight);
            }

            if (dimensions != null && !dimensions.equalsIgnoreCase("null")) {
                dimensionsET.setText(dimensions);
            }

            String customText1 = "<font color='#AFB6CB'> Custom Message: </font>";
            if (!custom_message.equalsIgnoreCase("null") && custom_message != null) {
                String customText2 = "<font color='#445C92'>" + custom_message + "</font>";
                customMessageTV.setText(Html.fromHtml(customText1 + customText2));
            } else {
                customMessageTV.setText(Html.fromHtml(customText1));
            }

            if (!staff_note.equalsIgnoreCase("null") && staff_note != null) {
                staffNotesET.setText(staff_note);
            }

            String changeHistoryText1 = "<font color='#AFB6CB'> Change History: </font>";
            if (!change_history.equals("null") && change_history != null) {
                String changeHistoryText2 = null;
                if (change_history.endsWith("<br>")) {
                    int endIndex = change_history.lastIndexOf("<br>");
                    if (endIndex != -1) {
                        String endText = change_history.substring(0, endIndex); // not forgot to put check if(endIndex != -1)
                        changeHistoryText2 = "<font color='#445C92'>" + endText + "</font>";
                    }
                } else {
                    changeHistoryText2 = "<font color='#445C92'>" + change_history + "</font>";
                }
                changeHistoryTV.setText(Html.fromHtml(changeHistoryText1 + changeHistoryText2));
            } else {
                changeHistoryTV.setText(Html.fromHtml(changeHistoryText1));
            }

            setCarrierSpinnerData(shippingCarrier);
            setPackageTypeSpinnerData(package_type);
            setSpecialHandlingData(handling);
            setServiceTypeSpinnerData(service_type);
            setPkgConditionSpinnerData(package_condition);
            setMailRoomSpinnerData(mailroom_id);

            NTF_Utils.hideProgressDialog();
            JSONObject imagesJsonObject = jsonObject.optJSONObject("package_images");
            if (imagesJsonObject != null) {
                JSONArray loginImagesJsonArray = imagesJsonObject.optJSONArray("login_images");
                if (loginImagesJsonArray != null && loginImagesJsonArray.length() > 0) {

                    for (int i = 0, len = loginImagesJsonArray.length(); i < len; i++) {
                        packageImagesList.add((String) loginImagesJsonArray.get(i));
                    }
                }

                if (packageImagesList.size() > 0) {

                    imagesLinearLayout.setVisibility(View.VISIBLE);
                    loginNoPictureView.setVisibility(View.GONE);
                    editLoginPicturesLayout.setVisibility(View.GONE);

                    for (int i = 0, len = packageImagesList.size(); i < len; ++i) {
                        if (packageImagesList.size() > i) {
                            final int j = i;
                            frameImagesList.get(i).setVisibility(View.VISIBLE);
                            Glide.with(getActivity())
                                    .load(packageImagesList.get(i))
                                    .listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            ProgessBarimagesList.get(j).setVisibility(View.GONE);
                                            return false;
                                        }
                                    }).into(ImageViewImagesList.get(i));
                        }
                    }
                } else {
                    imagesLinearLayout.setVisibility(View.VISIBLE);
                    loginNoPictureView.setVisibility(View.VISIBLE);
                }
            }
            if (packageImagesList.size() < 3) {
                editLoginPicturesLayout.setVisibility(View.VISIBLE);
                if (packageImagesList.size() == 0) {
                    flView1.setVisibility(View.VISIBLE);
                    flView2.setVisibility(View.VISIBLE);
                    flView3.setVisibility(View.VISIBLE);
                } else if (packageImagesList.size() == 1) {
                    flView1.setVisibility(View.VISIBLE);
                    flView2.setVisibility(View.VISIBLE);
                    flView3.setVisibility(View.GONE);
                } else if (packageImagesList.size() == 2) {
                    flView1.setVisibility(View.VISIBLE);
                    flView2.setVisibility(View.GONE);
                    flView3.setVisibility(View.GONE);
                }
            } else {
                editLoginPicturesLayout.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Set the Date to required format...
    private void setDate(String dateInString, TextView view) {
        try {
            if (dateInString.equalsIgnoreCase("0000-00-00 00:00:00")) {
                view.setText("");
            } else {
                view.setText((getLocalDate_and_Time(dateInString).replace("a.m.", "AM").replace("am", "AM").replace("pm", "PM").replace("p.m.", "PM") + " " + NTF_Utils.getTimeZoneTYPE(ntf_Preferences.get(Prefs_Keys.TIMEZONE), dateInString, ntf_Preferences)));
//                view.setText((getLocalDate_and_Time(dateInString).replace("a.m.", "AM").replace("am", "AM").replace("pm", "PM").replace("p.m.", "PM") + " " + ntf_Preferences.get(NTF_Constants.Prefs_Keys.TIMEZONE_SHORTCODE)));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getLocalDate_and_Time(String serverDateTimeString) {
        String localDateTime;
        try {
            SimpleDateFormat serverFormatter = new SimpleDateFormat(DATE_TIME_FORMATE);
            serverFormatter.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
            Date serverDateTime = serverFormatter.parse(serverDateTimeString);


            String dateFormat = "EEEE, MMMM d, yyyy - h:mm a";
            if (DateFormat.is24HourFormat(getActivity())){
                dateFormat = "EEEE, MMMM d, yyyy - HH:mm";
            }




            SimpleDateFormat localFormatter = new SimpleDateFormat(dateFormat);
            localFormatter.setTimeZone(TimeZone.getTimeZone(ntf_Preferences.get(Prefs_Keys.TIMEZONE)));
            localDateTime = localFormatter.format(serverDateTime);

        } catch (Exception e) {
            e.printStackTrace();
            localDateTime = "";
        }
        return localDateTime;
    }

    @Override
    public void onPackageFound(JSONObject jsonObject) {
        NTF_Utils.hideProgressDialog();
        bindToViews(jsonObject);
    }

    @Override
    public void onPackageNotFound(String message) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(getActivity(), "", message, null);
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
            doUpdatePackage();
        }

    }


    public boolean checkWhetherPackageIdExists() {
        if (!isMultipleEdit) {
            return !TextUtils.isEmpty(packageId);
        } else return pkgList.size() > 0;
    }

    private void doUpdatePackage() {
        Context context = getActivity();
        try {
            if (NTF_Utils.isOnline(context)) {
                if (checkWhetherPackageIdExists()) {
                    String shelf = !mEditShelfLocation.getText().toString().trim().isEmpty() ? mEditShelfLocation.getText().toString().trim() : null;
                    String sender = !senderEdit.getText().toString().trim().isEmpty() ? senderEdit.getText().toString().trim() : null;
                    String staffNotes = !staffNotesET.getText().toString().trim().isEmpty() ? staffNotesET.getText().toString().trim() : null;
                    String poNumber = !poNumberET.getText().toString().trim().isEmpty() ? poNumberET.getText().toString().trim() : null;
                    String weight = !weightET.getText().toString().trim().isEmpty() ? weightET.getText().toString().trim() : null;
                    String dimensions = !dimensionsET.getText().toString().trim().isEmpty() ? dimensionsET.getText().toString().trim() : null;
                    String mailroom = "";
                    String specialHandling = "";
                    for (SpecialHandling handling : specialHandlings) {
                        if (handling.isSelected()) {
                            specialHandling = specialHandling + "," + handling.getValue();
                        }
                    }
                    if (specialHandling.startsWith(",")) {
                        specialHandling = specialHandling.substring(1);
                    }
                    JSONObject request;
                    if (isMailroomVisible) {
                        if (mSpinnerMailRoom.getSelectedItem() != null) {
                            mailroom = ((SpinnerData) mSpinnerMailRoom.getSelectedItem()).getValue();
                        }
                        if (isMultipleEdit) {
                            request=new JSONObject();
                            if (mailroom != null && !mailroom.isEmpty()) {
                                request.put("mailroom_id", mailroom);
                            }
                            request.put("shelf", shelf);
                            request.put("shipping_carrier", !mCarrier.isEmpty()?mCarrier:null);
                            request.put("sender", sender);
                            request.put("service_type", mServiceType);
                            request.put("package_type", mPkgType);
                            request.put("package_condition", mPkgCondition);
                            request.put("staff_note_replace", staffNotes);
                            request.put("logout_code_id", "10000");
                            JSONArray pictureArray = new JSONArray();
                            if (base64Str1 != null && !base64Str1.isEmpty()) {
                                JSONObject jsonImage1 = new JSONObject();
                                jsonImage1.put("picture_data", base64Str1);
                                pictureArray.put(jsonImage1);
                            }
                            if (base64Str2 != null && !base64Str2.isEmpty()) {
                                JSONObject jsonImage1 = new JSONObject();
                                jsonImage1.put("picture_data", base64Str2);
                                pictureArray.put(jsonImage1);
                            }
                            if (base64Str3 != null && !base64Str3.isEmpty()) {
                                JSONObject jsonImage1 = new JSONObject();
                                jsonImage1.put("picture_data", base64Str3);
                                pictureArray.put(jsonImage1);
                            }
                            if (pictureArray.length() > 0) {
                                request.put("package_pictures", pictureArray);
                            }
                            if (specialHandling != null && !specialHandling.isEmpty()) {
                                request.put("special_handlings", specialHandling);
                            }
                            request.put("po_number", poNumber);
                            request.put("weight", weight);
                            request.put("dimensions", dimensions);
                            ArrayList<String> packageIdList = new ArrayList<>();
                            for (int i = 0; i < pkgList.size(); i++) {
                                packageIdList.add(pkgList.get(i).getPackageId());
                            }
                            packageId = (TextUtils.join(",", packageIdList));
                            request.put("package_id", packageId);
                            updatePackagePresenter.checkMultipleEditRequest(request, isMailroomVisible);
                        }
                    } else {
                        mailroom = mailroom_id;
                        if (isMultipleEdit) {
                            request=new JSONObject();
                            request.put("shelf", shelf);
                            request.put("shipping_carrier", !mCarrier.isEmpty()?mCarrier:null);
                            request.put("sender", sender);
                            request.put("service_type", mServiceType);
                            request.put("package_type", mPkgType);
                            request.put("package_condition", mPkgCondition);
                            request.put("staff_note_replace", staffNotes);
                            request.put("logout_code_id", "10000");
                            JSONArray pictureArray = new JSONArray();
                            if (base64Str1 != null && !base64Str1.isEmpty()) {
                                JSONObject jsonImage1 = new JSONObject();
                                jsonImage1.put("picture_data", base64Str1);
                                pictureArray.put(jsonImage1);
                            }
                            if (base64Str2 != null && !base64Str2.isEmpty()) {
                                JSONObject jsonImage1 = new JSONObject();
                                jsonImage1.put("picture_data", base64Str2);
                                pictureArray.put(jsonImage1);
                            }
                            if (base64Str3 != null && !base64Str3.isEmpty()) {
                                JSONObject jsonImage1 = new JSONObject();
                                jsonImage1.put("picture_data", base64Str3);
                                pictureArray.put(jsonImage1);
                            }
                            ArrayList<String> packageIdList = new ArrayList<>();
                            for (int i = 0; i < pkgList.size(); i++) {
                                packageIdList.add(pkgList.get(i).getPackageId());
                            }
                            packageId = (TextUtils.join(",", packageIdList));
                            request.put("package_id", packageId);
                            if (pictureArray.length() > 0) {
                                request.put("package_pictures", pictureArray);
                            }
                            if (specialHandling != null && !specialHandling.isEmpty()) {
                                request.put("special_handlings", specialHandling);
                            }
                            request.put("po_number", poNumber);
                            request.put("weight", weight);
                            request.put("dimensions", dimensions);
                            updatePackagePresenter.checkMultipleEditRequest(request, isMailroomVisible);
                        }
                    }
                    if (!isMultipleEdit) {
                        updatePackage(ntf_Preferences.get(Prefs_Keys.SESSION_ID),
                                ntf_Preferences.get(Prefs_Keys.AUTHENTICATION_TOKEN),
                                ntf_Preferences.get(Prefs_Keys.ACCOUNT_ID), ntf_Preferences.get(Prefs_Keys.USER_ID), mailroom,
                                shelf, mCarrier, sender, mServiceType, mPkgType, mPkgCondition, staffNotes, logout_code_id,
                                response_package_id, recipient_id, base64Str1, base64Str2, base64Str3,
                                specialHandling, poNumber, weight, dimensions);
                    }
                } else {
                    NTF_Utils.showAlert(getActivity(), "", "Please select a package.", null);
                }
            } else {
                NTF_Utils.showNoNetworkAlert(getActivity());
                ((MainActivity) getActivity()).registerReceiver();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updatePackage(String sessionId, String authenticationToken, String accountId, String userId, String mailRoom, String shelf, String carrier, String sender, String serviceType, String pkgType,
                               String pkgCondition, String staffNotes, String logoutCodeId, String package_Id, String recipient_id, String image1, String image2, String image3,
                               String special_handlings, String poNumber, String weight, String dimensions) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("session_id", sessionId);
            jsonObject.put("authentication_token", authenticationToken);
            jsonObject.put("account_id", accountId);
            jsonObject.put("user_id", userId);
            jsonObject.put("mailroom_id", mailRoom);
            jsonObject.put("shelf", shelf != null ? shelf : "");
            jsonObject.put("shipping_carrier", carrier != null ? carrier : "");
            jsonObject.put("sender", sender != null ? sender : "");
            jsonObject.put("service_type", serviceType != null ? serviceType : "");
            jsonObject.put("package_type", pkgType != null ? pkgType : "");
            jsonObject.put("package_condition", pkgCondition != null ? pkgCondition : "");
            jsonObject.put("po_number", poNumber != null ? poNumber : "");
            jsonObject.put("weight", weight != null ? weight : "");
            jsonObject.put("dimensions", dimensions != null ? dimensions : "");
            jsonObject.put("staff_note_replace", staffNotes != null ? staffNotes : "");
            jsonObject.put("logout_code_id", logoutCodeId);
            jsonObject.put("package_id", package_Id);
            jsonObject.put("recipient_id", recipient_id);
            jsonObject.put("special_handlings", special_handlings != null ? special_handlings : "");

            if (image1 == null && image2 == null && image3 == null) {
                jsonObject.put("package_pictures", "");
            } else {
                JSONArray pictureArray = new JSONArray();
                if (image1 != null) {
                    JSONObject jsonImage1 = new JSONObject();
                    jsonImage1.put("picture_data", image1);
                    pictureArray.put(jsonImage1);
                }
                if (image2 != null) {
                    JSONObject jsonImage2 = new JSONObject();
                    jsonImage2.put("picture_data", image2);
                    pictureArray.put(jsonImage2);
                }
                if (image3 != null) {
                    JSONObject jsonImage3 = new JSONObject();
                    jsonImage3.put("picture_data", image3);
                    pictureArray.put(jsonImage3);
                }
                jsonObject.put("package_pictures", pictureArray);
            }
            NTF_Utils.showProgressDialog(getActivity());
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
            updatePackagePresenter.updatingSinglePackage("", requestBody);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    // This method handling the deleting of image based on corresponding delete button click...
    private void deleteImage(int position) {
        if (position == 1) {
            base64Str1 = null;
            bitmap1 = null;
            imgView1.setImageResource(R.drawable.ic_camera_icon);
            viewPicClose1.setVisibility(View.GONE);
            SingleTon.getInstance().setBitmap1(null);
        } else if (position == 2) {
            base64Str2 = null;
            bitmap2 = null;
            imgView2.setImageResource(R.drawable.ic_camera_icon);
            viewPicClose2.setVisibility(View.GONE);
            SingleTon.getInstance().setBitmap2(null);

        } else if (position == 3) {
            base64Str3 = null;
            bitmap3 = null;
            imgView3.setImageResource(R.drawable.ic_camera_icon);
            viewPicClose3.setVisibility(View.GONE);
            SingleTon.getInstance().setBitmap3(null);
        }
    }

    private void openCameraPickPhoto(int requestCode) {
        clickPhotoRequestCode = requestCode;
        NTF_Utils.checkPermissionToProgress(getActivity(), new Runnable() {
            @Override
            public void run() {
                launchCameraActivity();
            }
        });
    }

    private void launchCameraActivity() {
        Intent intent = NTF_Utils.getCameraIntent(getActivity());
        intent.putExtra(Extras_Keys.KEY_PHOT_POS, clickPhotoRequestCode);
        startActivityForResult(intent, clickPhotoRequestCode);
    }

    //Setting shelf data...
    private void setShelfData(String shelf) {
        try {
            mShelfList = ShelfData.getShelfDetailsList(mGobalJsonData.getJSONArray("all_shelf_details"));
            if (mShelfList != null && !mShelfList.isEmpty()) {
                ShelfAdapter shelfDataAdapter = new ShelfAdapter(getActivity(), R.layout.spinner_item, mShelfList);
                //mEditShelfLocation.setAdapter(shelfDataAdapter);
                //mEditShelfLocation.setThreshold(2);
                if (shelf != null && !shelf.equalsIgnoreCase("null")) {
                    mEditShelfLocation.setText(shelf);
                }
            }

            if (shelf != null && !shelf.equalsIgnoreCase("null")) {
                mEditShelfLocation.setText(shelf);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Setting Sender data...
    private void setSenderData(String sender) {
        try {
            if (sender != null && !sender.equalsIgnoreCase("null")) {
                senderEdit.setText(sender);
            }
            mSenderList = SenderData.getSenderList(mGobalJsonData.getJSONArray("all_sender_details"));
            Log.d("SENDER", mSenderList.toString());
            if (mSenderList != null && !mSenderList.isEmpty()) {
                SenderAdapter senderDataAdapter = new SenderAdapter(getActivity(), R.layout.spinner_item, mSenderList);
                //senderEdit.setAdapter(senderDataAdapter);
                //senderEdit.setThreshold(2);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleBackButton() {
        getTargetFragment().onActivityResult(
                getTargetRequestCode(),
                Activity.RESULT_OK,
                new Intent().putExtra(Extras_Keys.REFRESH_LOG_PACKAGE_OUT_LIST, true));
    }
}
