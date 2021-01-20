package com.notifii.notifiiapp.fragments;


import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnItemSelected;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.adapters.SortListAdapter;
import com.notifii.notifiiapp.adapters.SpinnerHintAdapter;
import com.notifii.notifiiapp.adapters.SpinnerMarkAdapter;
import com.notifii.notifiiapp.helpers.NTF_PrefsManager;
import com.notifii.notifiiapp.helpers.SingleTon;
import com.notifii.notifiiapp.models.SpinnerData;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SortDialogFragment extends DialogFragment {

    @BindView(R.id.spinnerMailRoom)
    Spinner spinnerMailRoom;
    @BindView(R.id.ll_mailRoom)
    LinearLayout ll_mailRoom;
    @BindView(R.id.mailroomText)
    TextView mailroomText;
    @BindView(R.id.filter_listView)
    ListView listview;
    @BindView(R.id.title_filter_by)
    TextView titleTV;
    @BindView(R.id.title_sort_by)
    TextView sortBtTV;

    ArrayList<SpinnerData> mMailRoomList = new ArrayList<>();
    private ArrayList<SpinnerData> sortByList = new ArrayList<>();
    NTF_PrefsManager ntf_prefsManager;
    String mMailroom;
    SortListAdapter adapter;
    String previousValue;
    private LogPackageOutFragment logPkgOutFragment;
    public static final String DATE_RECEIVED_VALUE = "date_received";
    private Dialog dialog;

    public SortDialogFragment(NTF_PrefsManager ntf_prefsManager, String previousValue, LogPackageOutFragment logPkgOutFragment) {
        this.ntf_prefsManager = ntf_prefsManager;
        this.previousValue = previousValue;
        this.logPkgOutFragment = logPkgOutFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sort_dialog, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.dialog = getDialog();
        setMailRoomSpinnerData();
        prepareList();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
    }

    private void setMailRoomSpinnerData() {
        try {
            mMailroom = ntf_prefsManager.get(NTF_Constants.Prefs_Keys.DEFAULT_MAILROOM_ID);
            mMailRoomList.clear();
            mMailRoomList.addAll(SingleTon.getInstance().getmMailRoomList(getActivity()));
            boolean isMailRoomFound=false;
            for (SpinnerData mailroomData :mMailRoomList) {
                if (ntf_prefsManager.get(NTF_Constants.Prefs_Keys.DEFAULT_MAILROOM_ID).equals(mailroomData.getValue())){
                    isMailRoomFound=true;
                }
            }
            if (!isMailRoomFound){
                mMailroom="all";
            }
            if (mMailRoomList.size() <= 1) {
                ll_mailRoom.setVisibility(View.GONE);
                titleTV.setText(R.string.text_sort_by);
                sortBtTV.setVisibility(View.GONE);
            } else {
                ll_mailRoom.setVisibility(View.VISIBLE);
                SpinnerData spinnerData = new SpinnerData();
                spinnerData.setName("All Mailrooms");
                spinnerData.setValue("all");
                mMailRoomList.add(0, spinnerData);
            }
            if (mMailRoomList.size() >= 10) {
                NTF_Utils.setSpinnerHeight(getActivity(), spinnerMailRoom);
            }
            SpinnerMarkAdapter adapter = new SpinnerMarkAdapter(getActivity(), mMailRoomList);
            spinnerMailRoom.setAdapter(new SpinnerHintAdapter(adapter, R.layout.spinner_hint, getActivity()));
            for (SpinnerData spinnerData : mMailRoomList) {
                if (spinnerData.getValue().equals(mMailroom)) {
                    spinnerData.setSelected(true);
                    mailroomText.setText(spinnerData.getName());
                    spinnerMailRoom.setSelection(mMailRoomList.indexOf(spinnerData) + 1);
                } else {
                    spinnerData.setSelected(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.button_close)
    void onCliseDialogClicked() {
        getDialog().dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        int width = getResources().getDisplayMetrics().widthPixels;
        if (dialog == null || dialog.getWindow() == null) {
            return;
        }
        int dialogWidth = (int) (width * 0.90f);
        if (getResources().getBoolean(R.bool.isDeviceTablet)) {
            dialogWidth = (int) (width * 0.85f);
        }
        int dialogHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setLayout(dialogWidth, dialogHeight);
    }

    private void prepareList() {
        sortByList.clear();
        if (ntf_prefsManager.get(NTF_Constants.Prefs_Keys.PKG_LOGOUT_ADDRESS1).equals("1")) {
            SpinnerData spinnerData = new SpinnerData();
            spinnerData.setName(NTF_Utils.getRecipientAddress1Label(ntf_prefsManager.get(NTF_Constants.Prefs_Keys.ACCOUNT_TYPE)));
            spinnerData.setValue("recipient_address1");
            sortByList.add(spinnerData);
        }
        SpinnerData spinnerData1 = new SpinnerData();
        spinnerData1.setName(NTF_Utils.getRecipientTypeLabel(ntf_prefsManager.get(NTF_Constants.Prefs_Keys.ACCOUNT_TYPE)) + " Name");
        spinnerData1.setValue("recipient_name");
        sortByList.add(spinnerData1);
        SpinnerData spinnerData2 = new SpinnerData();
        spinnerData2.setName("Tracking Number");
        spinnerData2.setValue("tracking_number");
        sortByList.add(spinnerData2);
        if (ntf_prefsManager.get(NTF_Constants.Prefs_Keys.PKG_LOGOUT_TAGNUMBER).equals("1")) {
            SpinnerData spinnerData = new SpinnerData();
            spinnerData.setName("Tag Number");
            spinnerData.setValue("tag_number");
            sortByList.add(spinnerData);
        }
        if (ntf_prefsManager.get(NTF_Constants.Prefs_Keys.PKG_LOGOUT_PACKAGETYPE).equals("1")) {
            SpinnerData spinnerData = new SpinnerData();
            spinnerData.setName("Package Type");
            spinnerData.setValue("package_type");
            sortByList.add(spinnerData);
        }
        if (ntf_prefsManager.get(NTF_Constants.Prefs_Keys.PKG_LOGOUT_SHELF).equals("1")) {
            SpinnerData spinnerData = new SpinnerData();
            spinnerData.setName("Shelf");
            spinnerData.setValue("shelf");
            sortByList.add(spinnerData);
        }
        if (ntf_prefsManager.get(NTF_Constants.Prefs_Keys.PKG_LOGOUT_SENDER).equals("1")) {
            SpinnerData spinnerData = new SpinnerData();
            spinnerData.setName("Sender");
            spinnerData.setValue("sender");
            sortByList.add(spinnerData);
        }
        if (ntf_prefsManager.get(NTF_Constants.Prefs_Keys.PKG_LOGOUT_MAILROOM).equals("1") &&
                (mMailRoomList.size() != 1 || ntf_prefsManager.get(NTF_Constants.Prefs_Keys.PENDING_PACKAGES_PRIMARY_SORT_COLUMN).equalsIgnoreCase("mailroom_id"))) {
            SpinnerData spinnerData = new SpinnerData();
            spinnerData.setName("Mailroom");
            spinnerData.setValue("mailroom_id");
            sortByList.add(spinnerData);
        }
        SpinnerData spinnerData = new SpinnerData();
        spinnerData.setName("Date Received");
        spinnerData.setValue(DATE_RECEIVED_VALUE);
        sortByList.add(spinnerData);
        boolean isItemFound = false;
        for (SpinnerData data : sortByList) {
            if (data.getValue().equals(previousValue)) {
                data.setSelected(true);
                isItemFound = true;
                break;
            }
        }
        if (!isItemFound) {
            sortByList.get(0).setSelected(true);
        }
        adapter = new SortListAdapter(getActivity(), sortByList);
        listview.setAdapter(adapter);
    }

    @OnItemClick(R.id.filter_listView)
    void onItemSelected(int position) {
        SpinnerData.resetData(sortByList);
        sortByList.get(position).setSelected(true);
        adapter.notifyDataSetChanged();
    }

    @OnItemSelected(R.id.spinnerMailRoom)
    public void spinnerItemSelected(Spinner spinner, int position) {
        if (spinner.getSelectedItem() != null) {
            mMailroom = ((SpinnerData) spinner.getSelectedItem()).getValue();
            SpinnerData.resetData(mMailRoomList);
            ((SpinnerData) spinner.getSelectedItem()).setSelected(true);
            mailroomText.setText(((SpinnerData) spinner.getSelectedItem()).getName());
        }
    }

    @OnClick(R.id.mailroomSpinnerFL)
    void onSpinnerClicked() {
        spinnerMailRoom.performClick();
    }

    @OnClick(R.id.buttonDone)
    void onDoneClicked() {
        String sortby = "", sortByText = "";
        for (SpinnerData spinnerData : sortByList) {
            if (spinnerData.isSelected()) {
                sortby = spinnerData.getValue();
                sortByText = spinnerData.getName();
                break;
            }
        }
        logPkgOutFragment.onSortDoneClicked(sortby, mMailroom, sortByText);
        dismiss();
    }
}
