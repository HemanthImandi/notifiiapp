package com.notifii.notifiiapp.fragments;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.adapters.MatchingRecipientAdapter;
import com.notifii.notifiiapp.events.RefreshRecipientList;
import com.notifii.notifiiapp.helpers.NTF_PrefsManager;
import com.notifii.notifiiapp.sorting.ResidentAlphaNueralSortAscending;
import com.notifii.notifiiapp.sorting.ResidentAlphaNueralSortDescending;
import com.notifii.notifiiapp.models.Recipient;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 * Created by Administrator on 7/28/2016.
 */
public class UnitFragment extends Fragment {

    @BindView(R.id.unitListView)
    public ListView listView;

    @BindView(R.id.emptyText)
    public TextView emptyText;

    private String currentFragment;
    private ArrayList<Recipient> mRecipientList = new ArrayList<>();
    private MatchingRecipientAdapter mRecipientAdapter;
    public NTF_PrefsManager ntf_Preferences;

    public void setCurrentFragment(String currentFragment) {
        this.currentFragment = currentFragment;
    }

    public void setmRecipientList(ArrayList<Recipient> mRecipientList) {
        this.mRecipientList = mRecipientList;
    }

    public UnitFragment() {
    }

    /*@SuppressLint("ValidFragment")
    public UnitFragment(ArrayList<Recipient> mRecipientList, String currentFragment) {
        // Required empty public constructor
        this.mRecipientList = mRecipientList;
        this.currentFragment = currentFragment;
    }*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ntf_Preferences = new NTF_PrefsManager(getActivity());
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab_unit, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getResources().getBoolean(R.bool.isDeviceTablet)) {
            int[] colors = {0, 0x0, 0}; // red for the example
            listView.setDivider(new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, colors));
        }
        emptyText.setVisibility(View.VISIBLE);
        listView.setEmptyView(emptyText);
        mRecipientAdapter = new MatchingRecipientAdapter(getActivity(), mRecipientList, "UnitFragment");
        listView.setAdapter(mRecipientAdapter);
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

    @Subscriber(tag = "newDataSetAvailable") //this tag shud match with the tag we used in Step2.
    private void onUpdateAvailable(RefreshRecipientList newData) {
        mRecipientAdapter.notifyDataSetChanged();
    }

    // @Subscriber(tag = "filterList")
    public long doSortList(String text) {
        long length = 0;
        if (mRecipientAdapter != null) {
            length = mRecipientAdapter.filter(text);
        }
        return length;
    }


    public void sortRecipientAddressAscending() {
        try {
            Collections.sort(mRecipientList, new ResidentAlphaNueralSortAscending());
            notifyAdapter();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sortRecipientAddressByDescending() {
        Collections.sort(mRecipientList, new ResidentAlphaNueralSortDescending());
        notifyAdapter();
    }

    private void notifyAdapter() {
        if (mRecipientAdapter != null) {
            mRecipientAdapter.notifyDataSetChanged();
        }
    }

    @OnItemClick(R.id.unitListView)
    public void onItemClickUnitList(int position) {
        if (!(NTF_Utils.getCurrentFragment(getParentFragment()) instanceof CompleteRecipientListFragment)) {
            return;
        }
        if (!NTF_Utils.isOnline(getActivity())) {
            NTF_Utils.showNoNetworkAlert(getActivity());
            return;
        }

        if (currentFragment.equals("CompleteRecipientListFragment")) {
            if (ntf_Preferences.hasKey(NTF_Constants.Prefs_Keys.RECIPIENT_PKG_LIST_SELECTED_ITEM2)) {
                NTF_Utils.clearRecipientListPosition(getActivity());
            }
        }
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.add(R.id.realtabcontent, RecipientProfileViewFragment.getInstance(mRecipientList.get(position).getRecipientId()));
        transaction.commit();
        getActivity().getSupportFragmentManager().executePendingTransactions();
    }
}
