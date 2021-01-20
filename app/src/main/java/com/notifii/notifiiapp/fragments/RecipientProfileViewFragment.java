package com.notifii.notifiiapp.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.activities.MainActivity;
import com.notifii.notifiiapp.adapters.RecipientPkgAdapter;
import com.notifii.notifiiapp.base.NTF_BaseFragment;
import com.notifii.notifiiapp.sorting.SortByDateAscending;
import com.notifii.notifiiapp.sorting.SortByDateDescending;
import com.notifii.notifiiapp.sorting.SortPkgPickedUpDateAscending;
import com.notifii.notifiiapp.sorting.SortPkgPickedUpDateDescending;
import com.notifii.notifiiapp.models.Recipient;
import com.notifii.notifiiapp.models.Package;
import com.notifii.notifiiapp.mvp.models.PackageListforRecipientRequest;
import com.notifii.notifiiapp.mvp.models.RecipientDetailsRequest;
import com.notifii.notifiiapp.mvp.presenters.PackageListForRecipientPresenter;
import com.notifii.notifiiapp.mvp.presenters.RecipientDetailsPresenter;
import com.notifii.notifiiapp.mvp.views.PackageListforRecipientView;
import com.notifii.notifiiapp.mvp.views.RecipientDetailsView;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * This class deals with the fetching the data to the RecipientPackage List...
 */
public class RecipientProfileViewFragment extends NTF_BaseFragment implements RecipientDetailsView, PackageListforRecipientView {

    @BindView(R.id.recipientPkgList)
    public ListView mListViewRecipientPkg;

    private ArrayList<Package> packageArrayList = new ArrayList<>();
    private RecipientDetailsPresenter recipientDetailsPresenter;
    private PackageListForRecipientPresenter packageforRecipientPresenter;
    private View headerView;
    private String mRecipientId = "";
    private String recipientLabel;
    private String recipientAddress1Label;
    HeaderViewHolder headerViewHolder;
    private RecipientPkgAdapter adapter;
    Recipient mRecipient;
    private String email_bounced;
    private String email_bounce_alert;
    private String email_bounce_reason;
    private String phone_bounced;
    private String phone_bounce_alert;
    private String phone_bounce_reason;
    private String phone_type_error;
    private String phone_type_reason;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_recipient_profile_fragment_new, container, false);
        ButterKnife.bind(this, mainView);
        headerView = inflater.inflate(R.layout.recipient_profile_header, mListViewRecipientPkg, false);
        headerViewHolder = new HeaderViewHolder(headerView);
        recipientDetailsPresenter = new RecipientDetailsPresenter();
        packageforRecipientPresenter = new PackageListForRecipientPresenter();
        recipientDetailsPresenter.attachMvpView(this);
        packageforRecipientPresenter.attachMvpView(this);
        return mainView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bindViews();
        clearData();
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mRecipientId = bundle.getString(Extras_Keys.KEY_RECIPIENT_ID);
            headerViewHolder.parent_layout.setVisibility(View.GONE);
            getRecipient(getActivity());
        }
    }

    // Instance creation of this Fragment..
    public static RecipientProfileViewFragment getInstance(String recipientId) {
        RecipientProfileViewFragment fragment = new RecipientProfileViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Extras_Keys.KEY_RECIPIENT_ID, recipientId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onStackChanged() {
        super.onStackChanged();
        if (NTF_Utils.getCurrentFragment(this) != null && NTF_Utils.getCurrentFragment(this) instanceof RecipientProfileViewFragment) {
            onFragmentPopup(true);
        } else {
            onFragmentPopup(false);
        }
    }

    public void bindViews() {
        setRightButtonContent("Edit Resident profile");
        setToolbarActionButtons(true, true, R.drawable.ic_edit_icon);
        setToolbarTitle(NTF_Utils.getRecipientTypeLabel(ntf_Preferences.get(Prefs_Keys.ACCOUNT_TYPE)) + " Profile");
        mListViewRecipientPkg.addHeaderView(headerView, null, false);
        mListViewRecipientPkg.setAdapter(null);
        mListViewRecipientPkg.smoothScrollToPosition(0);
        headerViewHolder.parent_layout.setVisibility(View.GONE);
        headerViewHolder.mTextViewTrackingNumber.setTag("0");
        headerViewHolder.mIvAscendingSort.setTag("0");
        headerViewHolder.mIvDescendingSort.setTag("0");
        String accountType = ntf_Preferences.get(Prefs_Keys.ACCOUNT_TYPE);
        recipientLabel = NTF_Utils.getRecipientTypeLabel(accountType);
        if (accountType.toLowerCase().equalsIgnoreCase("apt")) {
            recipientAddress1Label = " ";
        } else {
            recipientAddress1Label = NTF_Utils.getRecipientAddress1Label(accountType);
        }

    }

    public void getRecipient(Activity activity) {
        clearData();
        mListViewRecipientPkg.setVisibility(View.GONE);
        if (activity != null && NTF_Utils.isOnline(activity)) {
            getRecipient(
                    ntf_Preferences.get(Prefs_Keys.SESSION_ID),
                    ntf_Preferences.get(Prefs_Keys.AUTHENTICATION_TOKEN),
                    ntf_Preferences.get(Prefs_Keys.ACCOUNT_ID),
                    mRecipientId, ntf_Preferences.get(Prefs_Keys.USER_ID));
        } else {
            if (activity != null) {
                NTF_Utils.showNoNetworkAlert(getActivity());
                ((MainActivity) getActivity()).registerReceiver();
            }
        }
    }

    // Service request for getting recipient profile details...
    public void getRecipient(String session_id, String authentication_token, String account_id, String recipient_id, String userID) {
        try {
            //Let's scroll the listview to begining when we EDIT and come back.
            if (mListViewRecipientPkg != null && !ntf_Preferences.hasKey(Prefs_Keys.RECIPIENT_PKG_LIST_SELECTED_ITEM2)) {
                mListViewRecipientPkg.setSelection(0);
            }
            NTF_Utils.showProgressDialog(getActivity());
            RecipientDetailsRequest recipientDetailsRequest = new RecipientDetailsRequest();
            recipientDetailsRequest.setAccountId(account_id);
            recipientDetailsRequest.setAuthenticationToken(authentication_token);
            recipientDetailsRequest.setRecipientId(recipient_id);
            recipientDetailsRequest.setSessionId(session_id);
            recipientDetailsRequest.setUserId(userID);
            // Service request for getting recipient profile details...
            recipientDetailsPresenter.getRecipientDetails(null, recipientDetailsRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRecipienttSuccess(JSONObject jsonObject) {
        try {
            bindRecipientDataToViews(jsonObject);
            if (!ntf_Preferences.get(Prefs_Keys.PRIVILEGE_TRACK_PACKAGES).equalsIgnoreCase("n")) {
                getPackageForRecipient();
            } else {
                NTF_Utils.hideProgressDialog();
                headerViewHolder.mIvDescendingSort.setClickable(false);
                headerViewHolder.mIvAscendingSort.setClickable(false);
                headerViewHolder.mTvNoPackages.setVisibility(View.VISIBLE);
                headerViewHolder.mTvNoPackages.setText(getActivity().getString(R.string.insufficient_privileges));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Binding recipient details to the views..
    private void bindRecipientDataToViews(JSONObject jsonObject) {
        try {
            mListViewRecipientPkg.setVisibility(View.VISIBLE);
            headerViewHolder.parent_layout.setVisibility(View.VISIBLE);
            String updatedString=jsonObject.toString().replaceAll("\"null\"","");
            String first_name = !jsonObject.optString("first_name").equals("null")?jsonObject.optString("first_name"):"";
            String last_name = !jsonObject.optString("last_name").equals("null")?jsonObject.optString("last_name"):"";
            String address1 = !jsonObject.optString("address1").equals("null")?jsonObject.optString("address1"):"";
            String email = !jsonObject.optString("email").equals("null")?jsonObject.optString("email"):"";
            String cellphone = jsonObject.optString("cellphone");
            String wireless_carrier = jsonObject.optString("wireless_carrier");
            String pe_alert = jsonObject.optString("pe_alert");
            String pt_alert = jsonObject.optString("pt_alert");
            String pe_alert_override_optout = jsonObject.optString("pe_alert_override_optout");
            String pt_alert_override_optout = jsonObject.optString("pt_alert_override_optout");
            String recipient_status = jsonObject.optString("recipient_status");
            String recipient_type = jsonObject.optString("recipient_type");
            String send_track_nonmarketing_email = jsonObject.optString("send_track_nonmarketing_email");
            String send_connect_nonmarketing_email = jsonObject.optString("send_connect_nonmarketing_email");
            String send_connect_marketing_email = jsonObject.optString("send_connect_marketing_email");
            String send_track_nonmarketing_text = jsonObject.optString("send_track_nonmarketing_text");
            String send_connect_nonmarketing_text = jsonObject.optString("send_connect_nonmarketing_text");
            String send_connect_marketing_text = jsonObject.optString("send_connect_marketing_text");
            String send_track_nonmarketing_push = jsonObject.optString("send_track_nonmarketing_push");
            String send_connect_nonmarketing_push = jsonObject.optString("send_connect_nonmarketing_push");
            String send_connect_marketing_push = jsonObject.optString("send_connect_marketing_push");

            String stop_track_nonmarketing_email = jsonObject.optString("stop_track_nonmarketing_email");
            String stop_connect_nonmarketing_email = jsonObject.optString("stop_connect_nonmarketing_email");
            String stop_connect_marketing_email = jsonObject.optString("stop_connect_marketing_email");
            String stop_track_nonmarketing_text = jsonObject.optString("stop_track_nonmarketing_text");
            String stop_connect_nonmarketing_text = jsonObject.optString("stop_connect_nonmarketing_text");
            String stop_connect_marketing_text = jsonObject.optString("stop_connect_marketing_text");
            String stop_track_nonmarketing_push = jsonObject.optString("stop_track_nonmarketing_push");
            String stop_connect_nonmarketing_push = jsonObject.optString("stop_connect_nonmarketing_push");
            String stop_connect_marketing_push = jsonObject.optString("stop_connect_marketing_push");
            String special_track_instructions_flag = jsonObject.optString("special_track_instructions_flag");
            String special_track_instructions = jsonObject.optString("special_track_instructions");
            String vacation_status = jsonObject.optString("vacation_status");
            String vacation_start_date = jsonObject.optString("vacation_start_date");
            String vacation_end_date = jsonObject.optString("vacation_end_date");
            String move_in_date = jsonObject.optString("move_in_date");
            String move_out_date = jsonObject.optString("move_out_date");
            String lease_start_date = jsonObject.optString("lease_start_date");
            String lease_end_date = jsonObject.optString("lease_end_date");
            String birth_date = jsonObject.optString("birth_date");
            String send_pkg_login_notification = jsonObject.optString("send_pkg_login_notification");
            String send_pkg_logout_notification = jsonObject.optString("send_pkg_logout_notification");
            String vacation_message = jsonObject.optString("vacation_message");

            email_bounced = jsonObject.optString("email_bounced");
            email_bounce_alert = jsonObject.optString("email_bounced_alert");
            email_bounce_reason = jsonObject.optString("email_bounce_reason");
            phone_bounced = jsonObject.optString("cellphone_bounced");
            phone_bounce_alert = jsonObject.optString("cellphone_bounced_alert");
            phone_bounce_reason = jsonObject.optString("cellphone_bounce_reason");
            phone_type_error = jsonObject.optString("phone_type_error");
            phone_type_reason = jsonObject.optString("phone_type_reason");

            JSONObject globalJsonObject = NTF_Utils.getGlobalData(getActivity());
            String recipientTypeTag = "";
            if (recipient_status.equals("0")) {
                recipientTypeTag = "Former ";
            } else if (recipient_status.equals("1")) {
                recipientTypeTag = "Current ";
            }

            headerViewHolder.mTextviewName.setText((first_name.equalsIgnoreCase("null") ? "" : first_name) + " " + (last_name.equalsIgnoreCase("null") ? "" : last_name));

            if (recipient_type != null) {
                recipient_type = recipient_type.equalsIgnoreCase("null") ? " " : recipient_type;
                address1 = address1.equalsIgnoreCase("null") ? " " : address1;
                if (!address1.isEmpty()) {
                    headerViewHolder.mTextViewAddr1Label.setText(address1 + ", " + recipientTypeTag + NTF_Utils.getRecipientTypeName(recipient_type, globalJsonObject));
                } else {
                    headerViewHolder.mTextViewAddr1Label.setText(recipientTypeTag + NTF_Utils.getRecipientTypeName(recipient_type, globalJsonObject));
                    if (headerViewHolder.mTextViewAddr1Label.getText().toString().isEmpty()) {
                        headerViewHolder.mTextViewAddr1Label.setVisibility(View.GONE);
                    } else {
                        headerViewHolder.mTextViewAddr1Label.setVisibility(View.VISIBLE);
                    }
                }
            }
            // If email_bounced_alert is "1" - need to display the alert and reason
            //                           "0" - no need to display the views
            if (email_bounced.equals("1")) {
                headerViewHolder.mTvBouncingEmail.setVisibility(View.VISIBLE);
            } else {
                headerViewHolder.mTvBouncingEmail.setVisibility(View.INVISIBLE);
            }

            if (phone_bounced.equals("1") || phone_type_error.equals("1")) {
                headerViewHolder.mTvBouncingPhone.setVisibility(View.VISIBLE);
            } else {
                headerViewHolder.mTvBouncingPhone.setVisibility(View.INVISIBLE);
            }

            if (email != null) {
                headerViewHolder.mTextviewEmail.setText(email.equalsIgnoreCase("null") || email.isEmpty() ? "No Email" : email);
            }
            if (cellphone != null)
                headerViewHolder.mTextviewMobilePhone.setText(cellphone.equalsIgnoreCase("null") || cellphone.isEmpty() ? "No Cellphone" : cellphone);

            if (send_track_nonmarketing_email.equals("1") && stop_track_nonmarketing_email.equals("0")) {
                //  mTextviewEmailNotify.setText("Yes (This person WILL get an email for package delivery)");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    headerViewHolder.mTvOptTypeEmail.setBackground(getResources().getDrawable(R.drawable.bg_sample));
                }
            } else {
                // mTextviewEmailNotify.setText("No (This person WILL NOT get an email for package delivery)");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    headerViewHolder.mTvOptTypeEmail.setBackground(getResources().getDrawable(R.drawable.bg_sample_red));
                }
            }

            if (send_track_nonmarketing_text.equals("1") && stop_track_nonmarketing_text.equals("0")) {
                // mTextviewTextNotify.setText("Yes (This person WILL get a text-message for package delivery)");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    headerViewHolder.mTvOptTypePhone.setBackground(getResources().getDrawable(R.drawable.bg_sample));
                }
            } else {
                // mTextviewTextNotify.setText("No (This person WILL NOT get a text-message for package delivery)");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    headerViewHolder.mTvOptTypePhone.setBackground(getResources().getDrawable(R.drawable.bg_sample_red));
                }
            }

            mRecipient = new Recipient();
            mRecipient.setFirstName(first_name);
            mRecipient.setLastName(last_name);
            mRecipient.setRecipientId(mRecipientId);
            mRecipient.setEmail(email);
            mRecipient.setCellphone(cellphone);
            mRecipient.setAddress1(address1);
            mRecipient.setPe_alert(pe_alert);
            mRecipient.setPt_alert(pt_alert);
            mRecipient.setPe_alert_optOut(pe_alert_override_optout);
            mRecipient.setPt_alert_optOut(pt_alert_override_optout);
            mRecipient.setRecipient_status_value(recipient_status);
            mRecipient.setRecipient_type_value(recipient_type);
            mRecipient.setWireless_carrier(wireless_carrier);
            mRecipient.setSend_track_nonmarketing_email(send_track_nonmarketing_email);
            mRecipient.setSend_track_nonmarketing_text(send_track_nonmarketing_text);
            mRecipient.setSend_track_nonmarketing_push(send_track_nonmarketing_push);
            mRecipient.setSend_connect_marketing_email(send_connect_marketing_email);
            mRecipient.setSend_connect_marketing_text(send_connect_marketing_text);
            mRecipient.setSend_connect_marketing_push(send_connect_marketing_push);
            mRecipient.setSend_connect_nonmarketing_email(send_connect_nonmarketing_email);
            mRecipient.setSend_connect_nonmarketing_text(send_connect_nonmarketing_text);
            mRecipient.setSend_connect_nonmarketing_push(send_connect_nonmarketing_push);

            mRecipient.setStop_track_nonmarketing_email(stop_track_nonmarketing_email);
            mRecipient.setStop_track_nonmarketing_text(stop_track_nonmarketing_text);
            mRecipient.setStop_track_nonmarketing_push(stop_track_nonmarketing_push);
            mRecipient.setStop_connect_marketing_email(stop_connect_marketing_email);
            mRecipient.setStop_connect_marketing_text(stop_connect_marketing_text);
            mRecipient.setStop_connect_marketing_push(stop_connect_marketing_push);
            mRecipient.setStop_connect_nonmarketing_email(stop_connect_nonmarketing_email);
            mRecipient.setStop_connect_nonmarketing_text(stop_connect_nonmarketing_text);
            mRecipient.setStop_connect_nonmarketing_push(stop_connect_nonmarketing_push);

            mRecipient.setEmail_bounced(email_bounced);
            mRecipient.setEmail_bounce_alert(email_bounce_alert);
            mRecipient.setEmail_bounce_reason(email_bounce_reason);
            mRecipient.setPhone_bounced(phone_bounced);
            mRecipient.setPhone_bounce_alert(phone_bounce_alert);
            mRecipient.setPhone_bounce_reason(phone_bounce_reason);
            mRecipient.setPhone_type_error(phone_type_error);
            mRecipient.setPhone_type_reason(phone_type_reason);

            mRecipient.setSpecial_track_instructions(special_track_instructions);
            mRecipient.setSpecial_track_instructions_flag(special_track_instructions_flag);
            mRecipient.setVacation_status(vacation_status);
            mRecipient.setVacation_start_date(vacation_start_date);
            mRecipient.setVacation_end_date(vacation_end_date);
            mRecipient.setMove_in_date(move_in_date);
            mRecipient.setMove_out_date(move_out_date);
            mRecipient.setVacation_message(vacation_message);
            mRecipient.setRecipientTitle(jsonObject.optString("recipient_title"));
            if (move_out_date != null) {
                Log.v("Profile move out: ", "" + move_out_date);
            } else {
                Log.v("Profile move out: ", "null");
            }
            mRecipient.setLease_start_date(lease_start_date);
            mRecipient.setLease_end_date(lease_end_date);
            mRecipient.setBirth_date(birth_date);
            mRecipient.setSend_pkg_login_notification(send_pkg_login_notification);
            mRecipient.setSend_pkg_logout_notification(send_pkg_logout_notification);

            //   String text = null;
            headerViewHolder.specialInstructionsLayout.setVisibility(View.VISIBLE);

            if (special_track_instructions != null && !special_track_instructions.isEmpty() && !special_track_instructions.equalsIgnoreCase("null") && vacation_message != null && !vacation_message.isEmpty() && !vacation_message.equalsIgnoreCase("null")) {
                if (!special_track_instructions_flag.equalsIgnoreCase("none")) {
                    //   text = "Special Instructions: " + special_track_instructions + "\n" + "\n" + vacation_message;
                    headerViewHolder.specialInstructionsText.setVisibility(View.VISIBLE);
                    headerViewHolder.specialInstructionsText.setText("Special Instructions: " + special_track_instructions);
                    headerViewHolder.specialInstructionsText.setMaxLines(1);
                    headerViewHolder.vacationInstructionText.setText(vacation_message);
                    headerViewHolder.vacationInstructionText.setVisibility(View.VISIBLE);
                    headerViewHolder.vacationInstructionText.setMaxLines(1);
                } else {
                    headerViewHolder.vacationInstructionText.setText(vacation_message);
                    headerViewHolder.vacationInstructionText.setVisibility(View.VISIBLE);
                    headerViewHolder.vacationInstructionText.setMaxLines(3);
                    headerViewHolder.specialInstructionsText.setVisibility(View.GONE);
                }
            } else if (special_track_instructions != null && !special_track_instructions.isEmpty() && !special_track_instructions.equalsIgnoreCase("null") && !special_track_instructions_flag.equalsIgnoreCase("none")) {
                headerViewHolder.specialInstructionsText.setText("Special Instructions: " + special_track_instructions);
                headerViewHolder.specialInstructionsText.setVisibility(View.VISIBLE);
                headerViewHolder.specialInstructionsText.setMaxLines(3);
                headerViewHolder.vacationInstructionText.setVisibility(View.GONE);
            } else if (vacation_message != null && !vacation_message.isEmpty() && !vacation_message.equalsIgnoreCase("null")) {
                headerViewHolder.vacationInstructionText.setText(vacation_message);
                headerViewHolder.vacationInstructionText.setVisibility(View.VISIBLE);
                headerViewHolder.vacationInstructionText.setMaxLines(3);
                headerViewHolder.specialInstructionsText.setVisibility(View.GONE);
            } else {
                headerViewHolder.specialInstructionsLayout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Preparing service request for getting packages related to recipient...
    private void getPackageForRecipient() {
        Context context = getActivity();
        if (context != null && NTF_Utils.isOnline(context)) {
            getPackagesForRecipient(
                    ntf_Preferences.get(Prefs_Keys.SESSION_ID),
                    ntf_Preferences.get(Prefs_Keys.AUTHENTICATION_TOKEN),
                    ntf_Preferences.get(Prefs_Keys.ACCOUNT_ID),
                    mRecipientId, ntf_Preferences.get(Prefs_Keys.USER_ID));
        } else {
            NTF_Utils.hideProgressDialog();
            if (context != null)
                NTF_Utils.showNoNetworkAlert(getActivity());
        }

    }

    // Service request for getting packages of recipient..
    public void getPackagesForRecipient(String session_id, String authentication_token, String account_id, String recipient_id, String userID) {
        PackageListforRecipientRequest packageListforRecipientRequest = new PackageListforRecipientRequest();
        packageListforRecipientRequest.setSessionId(session_id);
        packageListforRecipientRequest.setAuthenticationToken(authentication_token);
        packageListforRecipientRequest.setAccountId(account_id);
        packageListforRecipientRequest.setRecipientId(recipient_id);
        packageListforRecipientRequest.setUserId(userID);
        packageforRecipientPresenter.getPackageListforRecipient(null, packageListforRecipientRequest);
    }

    @Override
    public void onPackageforRecipienttSuccess(JSONObject jsonObject) {
        NTF_Utils.hideProgressDialog();
        bindRecipientPkgsToList(jsonObject);
    }

    // Binding packages list to the ListView..
    private void bindRecipientPkgsToList(Object object) {
        try {
            if (object != null) {
                JSONObject jsonObject = new JSONObject(object.toString());
                packageArrayList.clear();
                packageArrayList = Package.getPackagesPending(jsonObject.getJSONArray("packages"), false, packageArrayList);
                mListViewRecipientPkg.setVisibility(View.VISIBLE);
                if (packageArrayList != null && !packageArrayList.isEmpty()) {
                    sortByDateReceivedDescending();
                    //Collections.reverse(packageArrayList);
                    adapter = new RecipientPkgAdapter(getActivity(), packageArrayList, ntf_Preferences.get(NTF_Constants.Prefs_Keys.TIMEZONE));
                    mListViewRecipientPkg.setAdapter(adapter);
                 /*   if (ntf_Preferences.hasKey(Prefs_Keys.RECIPIENT_PKG_LIST_SELECTED_ITEM2)) {
                        mListViewRecipientPkg.setSelection(Integer.parseInt(ntf_Preferences.get(Prefs_Keys.RECIPIENT_PKG_LIST_SELECTED_ITEM2)));
                    }*/
                    //mTextViewMatchingTrackingNumberLabel.performClick();
                } else {
                    headerViewHolder.mTvNoPackages.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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


    private void sortByDateDeliveredAscending() {
        Collections.sort(packageArrayList, new SortPkgPickedUpDateAscending());
        notifyAdapter();
    }

    private void sortByDateDeliveredDescending() {
        Collections.sort(packageArrayList, new SortPkgPickedUpDateDescending());
        notifyAdapter();
    }

    private void sortByDateReceivedAscending() {
        Collections.sort(packageArrayList, new SortByDateAscending());
        notifyAdapter();
    }

    private void sortByDateReceivedDescending() {
        Collections.sort(packageArrayList, new SortByDateDescending());
        notifyAdapter();
    }

    private void notifyAdapter() {
        try {
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This method used to clear the text fields...
    private void clearData() {
        try {
            headerViewHolder.mTextviewName.setText("");
            headerViewHolder.mTextviewEmail.setText("");
            headerViewHolder.mTextviewEmail.setText("");
            headerViewHolder.mTextviewMobilePhone.setText("");
            RecipientPkgAdapter recipientPkgAdapter = (RecipientPkgAdapter) mListViewRecipientPkg.getAdapter();
            if (recipientPkgAdapter != null) {
                recipientPkgAdapter.clear();
                recipientPkgAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class HeaderViewHolder {
        @BindView(R.id.profilePicImageView)
        public ImageView mProfileImageView;
        @BindView(R.id.tv_profile_name)
        public TextView mTextviewName;
        @BindView(R.id.tv_address1)
        public TextView mTextViewAddr1Label;
        @BindView(R.id.tv_phone)
        public TextView mTextviewMobilePhone;
        @BindView(R.id.tv_email)
        public TextView mTextviewEmail;
        @BindView(R.id.tv_opt_type_phone)
        public TextView mTvOptTypePhone;
        @BindView(R.id.tv_opt_type_email)
        public TextView mTvOptTypeEmail;
        @BindView(R.id.iv_descending_sort)
        public ImageView mIvDescendingSort;
        @BindView(R.id.iv_ascending_sort)
        public ImageView mIvAscendingSort;
        @BindView(R.id.iv_bouncing_alert_phone)
        public TextView mTvBouncingPhone;
        @BindView(R.id.iv_bouncing_alert_email)
        public TextView mTvBouncingEmail;
        @BindView(R.id.tv_tracking_number)
        public TextView mTextViewTrackingNumber;
        @BindView(R.id.textView_no_packages)
        public TextView mTvNoPackages;
        @BindView(R.id.special_ins_layout)
        public LinearLayout specialInstructionsLayout;
        @BindView(R.id.special_ins_text)
        public TextView specialInstructionsText;
        @BindView(R.id.vacation_ins_text)
        public TextView vacationInstructionText;
        @BindView(R.id.special_ins_expand_img)
        public ImageView specialInsExpandImageview;
        @BindView(R.id.parentLayout)
        public LinearLayout parent_layout;

        HeaderViewHolder(View headerView) {
            ButterKnife.bind(this, headerView);
        }


        @OnClick(R.id.iv_descending_sort)
        public void onClickDescendingSort(View view) {
            if (packageArrayList != null) {
                if (mIvDescendingSort.getTag().toString().equals("0")) {
                    sortByDateReceivedAscending();
                } else {
                    sortByDateReceivedDescending();
                }
                unCheckViews(mIvDescendingSort);
            }
        }

        @OnClick(R.id.iv_ascending_sort)
        public void onClickAscendingSor(View view) {
            if (packageArrayList != null) {
                if (mIvAscendingSort.getTag().toString().equals("0")) {
                    sortByDateDeliveredAscending();
                } else {
                    sortByDateDeliveredDescending();
                }
                unCheckViews(mIvAscendingSort);
            }
        }

        @OnClick(R.id.iv_bouncing_alert_email)
        public void onClickBouncingAlertEmail(View view) {
            boolean isEmail = true;
            NTF_Utils.showBouncingAlertDialog(getActivity(), isEmail, mRecipient);
        }

        @OnClick(R.id.iv_bouncing_alert_phone)
        public void onClickBouncingAlertPhone(View view) {
            boolean isEmail = false;
            NTF_Utils.showBouncingAlertDialog(getActivity(), isEmail, mRecipient);
        }

    /*    @OnClick(R.id.tv_tracking_number)
        public void onClickTrackingNumber(View view) {
            if (mTextViewTrackingNumber.getTag().toString().equals("0")) {
                sortByTrackingAscending();
            } else {
                sortByTrackingDescending();
            }
            unCheckViews(mTextViewTrackingNumber);
        }*/

        @OnClick(R.id.special_ins_expand_img)
        public void onClickSpecialInsExpand(View view) {
            NTF_Utils.showSpecialTrackInsAlert(getActivity(), false, mRecipient.getSpecial_track_instructions(), mRecipient.getVacation_message(), mRecipient.getSpecial_track_instructions_flag());
        }
    }

    // Setting the tags and drawables to the Views when changing the sorting from Ascending to descending..
    public void unCheckViews(View view) {
        if (view.getTag().toString().equals("0")) {
            view.setTag(1);
        } else {
            view.setTag(0);
        }
        view.invalidate();
    }

    @OnClick(R.id.actionImageRight)
    public void onClickImageRight() {
        if (!(NTF_Utils.getCurrentFragment(this) instanceof RecipientProfileViewFragment)) {
            return;
        }
        if (getActivity() != null && NTF_Utils.isOnline(getActivity())) {
            if (mRecipient != null) {
                if (ntf_Preferences.get(Prefs_Keys.PRIVILEGE_CORE_RECIPIENTS) != null) {
                    if (ntf_Preferences.get(Prefs_Keys.PRIVILEGE_CORE_RECIPIENTS).equalsIgnoreCase("f")) {
                        ///  ntf_Preferences.removeKey(Prefs_Keys.RECIPIENT_PKG_LIST_SELECTED_ITEM2);
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.addToBackStack(null);
                        AddAndAndEditRecipientProfile fragment = AddAndAndEditRecipientProfile.getInstance(mRecipient);
                        transaction.add(R.id.realtabcontent, fragment);
                        transaction.commit();
                        getActivity().getSupportFragmentManager().executePendingTransactions();
                    } else {
                        NTF_Utils.showInSufficientPrivelegeAlert(getActivity());
                    }
                } else {
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.addToBackStack(null);
                    AddAndAndEditRecipientProfile fragment = AddAndAndEditRecipientProfile.getInstance(mRecipient);
                    transaction.add(R.id.realtabcontent, fragment);
                    transaction.commit();
                    getActivity().getSupportFragmentManager().executePendingTransactions();
                }
            } else {
                NTF_Utils.showAlert(getActivity(), "", "Unable to get Recipient details", null);
            }
        } else {
            if (getActivity() != null) {
                NTF_Utils.showNoNetworkAlert(getActivity());
                ((MainActivity) getActivity()).registerReceiver();
            }
        }

    }

    @OnClick({R.id.backImage, R.id.mobile_left_linear})
    public void onBackClick() {
        NTF_Utils.hideKeyboard(getActivity());
        getActivity().onBackPressed();
    }

    @OnItemClick(R.id.recipientPkgList)
    public void onRecipientPkgList(int position) {
        try {
            if (!(NTF_Utils.getCurrentFragment(this) instanceof RecipientProfileViewFragment)) {
                return;
            }
            ntf_Preferences.save(Prefs_Keys.RECIPIENT_PKG_LIST_SELECTED_ITEM2, String.valueOf(position));
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.addToBackStack(null);
            transaction.add(R.id.realtabcontent, PackageDetailsFragment.getInstance(packageArrayList.get(position - 1).getPackageId(), true));
            transaction.commit();
            getActivity().getSupportFragmentManager().executePendingTransactions();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
