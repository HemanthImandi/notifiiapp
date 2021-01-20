package com.notifii.notifiiapp.fragments;

import android.annotation.SuppressLint;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.adapters.PackageHistoryAdapter;
import com.notifii.notifiiapp.events.RefreshPackagesList;
import com.notifii.notifiiapp.helpers.NTF_PrefsManager;
import com.notifii.notifiiapp.sorting.SortByDateAscending;
import com.notifii.notifiiapp.sorting.SortByDateDescending;
import com.notifii.notifiiapp.sorting.SortPkgAddressAscending;
import com.notifii.notifiiapp.sorting.SortPkgAddressDescending;
import com.notifii.notifiiapp.sorting.SortPkgPickedUpDateAscending;
import com.notifii.notifiiapp.sorting.SortPkgPickedUpDateDescending;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.models.Package;
import com.notifii.notifiiapp.utils.NTF_Utils;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 * Created by Administrator on 8/4/2016.
 */
public class PkgHistoryUnitFragment extends Fragment implements NTF_Constants {

    @BindView(R.id.residentsListView)
    public ListView listView;
    private boolean isFromSearchPkgFrag;
    private List<Package> mPackagesList = new ArrayList<>();
    public PackageHistoryAdapter packageHistoryAdapter;
    private String currentFragment;
    public NTF_PrefsManager ntf_Preferences;

    @SuppressLint("ValidFragment")
    public PkgHistoryUnitFragment(List<Package> mPackagesList, boolean isFromSearchPkg) {
        this.mPackagesList = mPackagesList;
        this.isFromSearchPkgFrag = isFromSearchPkg;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ntf_Preferences = new NTF_PrefsManager(getActivity());
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView = inflater.inflate(R.layout.fragment_tab_residents, null);
        ButterKnife.bind(this, mainView);
        return mainView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getResources().getBoolean(R.bool.isDeviceTablet)) {
            int[] colors = {0, 0x0, 0}; // red for the example
            listView.setDivider(new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, colors));
        }
        packageHistoryAdapter = new PackageHistoryAdapter(getActivity(), mPackagesList, NTF_Constants.Extras_Keys.PKG_HISTORY_UNIT_FRAGMENTS, ntf_Preferences.get(NTF_Constants.Prefs_Keys.TIMEZONE));
        listView.setAdapter(packageHistoryAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    // @Subscriber(tag = "filterList")
    public void doSortList(String text) {
        if (packageHistoryAdapter != null) {
            packageHistoryAdapter.filter(text);
            notifyAdapter();
        }
    }

    @Subscriber(tag = "newPackagesData") //this tag shud match with the tag we used in Step2.
    private void onUpdateAvailable(RefreshPackagesList newData) {
        //update the adapter or whatever with the latest data.
       /* mRecipientList.clear();
        mRecipientList.addAll(newData.dataset);*/
        packageHistoryAdapter.notifyDataSetChanged();
    }


    public void sortRecipientAddressAscending() {
        Collections.sort(mPackagesList, new SortPkgAddressAscending());
        notifyAdapter();
    }

    public void sortRecipientAddressByDescending() {
        Collections.sort(mPackagesList, new SortPkgAddressDescending());
        notifyAdapter();
    }

    public void sortDateReceivedAscending() {
        Collections.sort(mPackagesList, new SortByDateAscending());
        notifyAdapter();
    }

    public void sortDateReceivedByDescending() {
        Collections.sort(mPackagesList, new SortByDateDescending());
        notifyAdapter();
    }

    public void sortDateDeliveredAscending() {
        Collections.sort(mPackagesList, new SortPkgPickedUpDateAscending());
        notifyAdapter();
    }

    public void sortDateDeliveredByDescending() {
        Collections.sort(mPackagesList, new SortPkgPickedUpDateDescending());
        notifyAdapter();
    }

    private void notifyAdapter() {
        if (packageHistoryAdapter != null)
            packageHistoryAdapter.notifyDataSetChanged();
    }

    @OnItemClick(R.id.residentsListView)
    public void onItemClickResidentList(int position) {
        try {
            if (!(NTF_Utils.getCurrentFragment(getParentFragment()) instanceof PackageHistoryFragment)) {
                return;
            }
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.addToBackStack(null);
            transaction.add(R.id.realtabcontent, PackageDetailsFragment.getInstance(mPackagesList.get(position).getPackageId(), isFromSearchPkgFrag));
            transaction.commit();
            getActivity().getSupportFragmentManager().executePendingTransactions();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}