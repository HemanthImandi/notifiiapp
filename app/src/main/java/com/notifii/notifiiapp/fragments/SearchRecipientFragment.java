package com.notifii.notifiiapp.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.adapters.SpinnerHintAdapter;
import com.notifii.notifiiapp.adapters.SpinnerMarkAdapter;
import com.notifii.notifiiapp.base.NTF_BaseFragment;
import com.notifii.notifiiapp.models.SpinnerData;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchRecipientFragment extends NTF_BaseFragment {

    @BindView(R.id.first_name)
    public EditText firstNameET;

    @BindView(R.id.last_name)
    public EditText lastNameET;

    @BindView(R.id.unit_number)
    public EditText unitNumberET;

    @BindView(R.id.cellphone)
    public EditText cellphoneET;

    @BindView(R.id.email)
    public EditText emailET;

    @BindView(R.id.status_filter)
    public Spinner statusFilterSpinner;

    @BindView(R.id.search_logic_spinner)
    public Spinner searchLogicSpinner;

    @BindView(R.id.unit_number_text)
    public TextView unitNumberText;

    @BindView(R.id.tv_status_filter)
    public TextView tvStatusFilter;

    @BindView(R.id.tv_search_filter)
    public TextView tvSearchFilter;

    public String mSearchLogic;
    public String mStatusFilter;
    ArrayList<SpinnerData> mStatusList = new ArrayList<>();
    ArrayList<SpinnerData> mSearchLogicList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_search_resident, container, false);
        ButterKnife.bind(this, mainView);
        return mainView;
    }

    @Override
    public void onStackChanged() {
        super.onStackChanged();
        if (NTF_Utils.getCurrentFragment(this) != null && NTF_Utils.getCurrentFragment(this) instanceof SearchRecipientFragment) {
            onFragmentPopup(true);
        } else {
            onFragmentPopup(false);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews();
    }

    private void bindViews() {
        String recipientTypeLable = NTF_Utils.getRecipientTypeLabel(ntf_Preferences.get(Prefs_Keys.ACCOUNT_TYPE));
        setToolbarTitle("Search " + recipientTypeLable);
        setToolbarActionButtons(true, false, 0);
        String recipientAddressLable = NTF_Utils.getRecipientAddress1Label(ntf_Preferences.get(Prefs_Keys.ACCOUNT_TYPE));
        unitNumberText.setText("" + recipientAddressLable);
        unitNumberET.setContentDescription(recipientAddressLable);

        setStatusFilterSpinnerData();
        setSearchLogicSpinnerData();
    }

    private void setStatusFilterSpinnerData() {
        try {
            String accTypeName = NTF_Utils.getRecipientTypeLabel(ntf_Preferences.get(NTF_Constants.Prefs_Keys.ACCOUNT_TYPE));
            mStatusList = SpinnerData.getListStatusFilter(accTypeName, false);
            if (mStatusList != null && !mStatusList.isEmpty()) {
                if (mStatusList.size() >= 10) {
                    NTF_Utils.setSpinnerHeight(getActivity(), statusFilterSpinner);
                }
                SpinnerMarkAdapter serviceTyeAdapter = new SpinnerMarkAdapter(getActivity(), mStatusList);
                statusFilterSpinner.setAdapter(serviceTyeAdapter);
                statusFilterSpinner.setSelection(0);
                tvStatusFilter.setText(((SpinnerData)statusFilterSpinner.getSelectedItem()).getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSearchLogicSpinnerData() {
        try {
            mSearchLogicList = SpinnerData.getResidentListSearchLogic(false);
            if (mSearchLogicList != null && !mSearchLogicList.isEmpty()) {
                if (mSearchLogicList.size() >= 10) {
                    NTF_Utils.setSpinnerHeight(getActivity(), searchLogicSpinner);
                }
                SpinnerMarkAdapter searchLogicAdapter = new SpinnerMarkAdapter(getActivity(), mSearchLogicList);
                searchLogicSpinner.setAdapter(searchLogicAdapter);
                searchLogicSpinner.setSelection(0);
                tvSearchFilter.setText(((SpinnerData)searchLogicSpinner.getSelectedItem()).getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.backImage, R.id.mobile_left_linear})
    public void onBackClick() {
        NTF_Utils.hideKeyboard(getActivity());
        getActivity().onBackPressed();
    }

    @OnClick(R.id.tv_status_filter)
    public void onStatusSpinnerClick() {
     statusFilterSpinner.performClick();
    }

    @OnClick(R.id.tv_search_filter)
    public void onSearchSpinnerClick() {
     searchLogicSpinner.performClick();
    }

    @OnClick(R.id.saveBtn)
    public void onSaveClick() {
        if (!(NTF_Utils.getCurrentFragment(this) instanceof SearchRecipientFragment)) {
            return;
        }
        if(!NTF_Utils.isOnline(getActivity())) {
            NTF_Utils.showNoNetworkAlert(getActivity());
            return;
        }

        String firstName = firstNameET.getText().toString().trim();
        String lastName = lastNameET.getText().toString().trim();
        String unitNumber = unitNumberET.getText().toString().trim();
        String email = emailET.getText().toString().trim();
        String cellphoneNumber = cellphoneET.getText().toString().trim();
        Log.v("Mobile Number : ", cellphoneNumber);
        if (firstName.isEmpty() && lastName.isEmpty() && unitNumber.isEmpty() && email.isEmpty() && cellphoneNumber.isEmpty()) {
            NTF_Utils.showAlert(getActivity(), "", "Please provide at least one search parameter. ", null);
            return;
        }
        if (!TextUtils.isEmpty(email) && !NTF_Utils.isValidEmail(email)) {
            NTF_Utils.showAlert(getActivity(), "", "Your email address is not valid.", null);
            return;
        }
        CompleteRecipientListFragment fragment = new CompleteRecipientListFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Extras_Keys.IS_FROM_SEARCH_RESIDENT, true);
        bundle.putString(Extras_Keys.FIRST_NAME, firstName);
        bundle.putString(Extras_Keys.LAST_NAME, lastName);
        bundle.putString(Extras_Keys.UNIT_NUMBER, unitNumber);
        bundle.putString(Extras_Keys.EMAIL, email);
        bundle.putString(Extras_Keys.CELLPHONE, cellphoneNumber);
        bundle.putString(Extras_Keys.SEARCH_LOGIC, mSearchLogic);
        bundle.putString(Extras_Keys.STATUS_FILTER, mStatusFilter);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.add(R.id.realtabcontent, fragment);
        transaction.commitAllowingStateLoss();
        getActivity().getSupportFragmentManager().executePendingTransactions();
    }

    @OnItemSelected(R.id.status_filter)
    public void onItemStatusClick() {
        NTF_Utils.hideKeyboard(getActivity());
        mStatusFilter = ((SpinnerData) statusFilterSpinner.getSelectedItem()).getValue();
        if (statusFilterSpinner.getSelectedItem() != null) {
            SpinnerData.resetData(mStatusList);
            tvStatusFilter.setText(((SpinnerData) statusFilterSpinner.getSelectedItem()).getName());
            ((SpinnerData) statusFilterSpinner.getSelectedItem()).setSelected(true);
        }
    }

    @OnItemSelected(R.id.search_logic_spinner)
    public void onItemSearchClick() {
        NTF_Utils.hideKeyboard(getActivity());
        mSearchLogic = ((SpinnerData) searchLogicSpinner.getSelectedItem()).getValue();
        if (searchLogicSpinner.getSelectedItem() != null) {
            SpinnerData.resetData(mSearchLogicList);
            tvSearchFilter.setText(((SpinnerData)searchLogicSpinner.getSelectedItem()).getName());
            ((SpinnerData) searchLogicSpinner.getSelectedItem()).setSelected(true);
        }
    }
}
