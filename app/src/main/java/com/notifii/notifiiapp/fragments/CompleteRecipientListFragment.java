package com.notifii.notifiiapp.fragments;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.activities.MainActivity;
import com.notifii.notifiiapp.adapters.RecipientListViewPagerAdapter;
import com.notifii.notifiiapp.asynctasks.RecipientJsonToModel;
import com.notifii.notifiiapp.base.NTF_BaseFragment;
import com.notifii.notifiiapp.customui.NonSwipeableViewPager;
import com.notifii.notifiiapp.events.RefreshRecipientList;
import com.notifii.notifiiapp.models.Recipient;
import com.notifii.notifiiapp.mvp.models.RecipientListRequest;
import com.notifii.notifiiapp.mvp.models.SearchResidentRequest;
import com.notifii.notifiiapp.mvp.presenters.RecipientsListPresenter;
import com.notifii.notifiiapp.mvp.presenters.SearchRecipientPresenter;
import com.notifii.notifiiapp.mvp.views.RecipientsListView;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.OnTouch;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompleteRecipientListFragment extends NTF_BaseFragment implements RecipientsListView {

    @BindView(R.id.editTextSearch)
    public EditText mEditTextSearch;
    @BindView(R.id.imageButtonClear)
    public ImageButton mImageButtonClear;
    @BindView(R.id.viewpager)
    public NonSwipeableViewPager viewPager;
    @BindView(R.id.tabs)
    public TabLayout tabLayout;
    @BindView(R.id.tab_linear)
    public LinearLayout tabLinear;
    @BindView(R.id.list_count)
    public TextView totalListCount;
    @BindView(R.id.search_layout)
    public LinearLayout searchLayout;
    @BindView(R.id.view_search)
    public View searchView;
    @BindView(R.id.parentLayout)
    public LinearLayout parentLayout;

    private RecipientListViewPagerAdapter adapter;
    private RecipientFragment recipientFragment;
    private UnitFragment unitFragment;
    private long listSize;
    private boolean isfromSearchResident;
    private String first_name, last_name, email_id, cellphone, unit_number, searchLogic, statusFilter, recipientAddress1Label;
    private TabLayout.Tab tab1;
    private ArrayList<Recipient> mRecipientList = new ArrayList<>();
    private ArrayList<TextView> mRecipientCheckTextViewList = new ArrayList<>();
    RecipientsListPresenter recipientsListPresenter;
    SearchRecipientPresenter searchRecipientPresenter;
    private boolean isListenerActivated = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_complete_recipient_list_fragment_new, container, false);
        ButterKnife.bind(this, mainView);
        return mainView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recipientsListPresenter = new RecipientsListPresenter();
        recipientsListPresenter.attachMvpView(this);
        searchRecipientPresenter = new SearchRecipientPresenter();
        searchRecipientPresenter.attachMvpView(this);
        bindViews();
        NTF_Utils.handleDoneButton(mEditTextSearch, getActivity());
    }

    @Override
    public void onStackChanged() {
        super.onStackChanged();
        if (NTF_Utils.getCurrentFragment(this) != null && NTF_Utils.getCurrentFragment(this) instanceof CompleteRecipientListFragment) {
            onFragmentPopup(true);
        } else {
            onFragmentPopup(false);
        }
    }

    private void bindViews() {
        try {
            isfromSearchResident = getArguments().getBoolean(Extras_Keys.IS_FROM_SEARCH_RESIDENT);
            String accountType = ntf_Preferences.get(Prefs_Keys.ACCOUNT_TYPE);
            if (accountType.toLowerCase().equalsIgnoreCase("apt")) {
                recipientAddress1Label = "Unit #";
            } else {
                recipientAddress1Label = NTF_Utils.getRecipientAddress1Label(accountType);
            }

            if (isfromSearchResident) {
                setToolbarTitle("Search Results");
                parentLayout.setContentDescription("Search Results");
                searchLayout.setVisibility(View.GONE);
                searchView.setVisibility(View.GONE);
                first_name = getArguments().getString(Extras_Keys.FIRST_NAME);
                last_name = getArguments().getString(Extras_Keys.LAST_NAME);
                unit_number = getArguments().getString(Extras_Keys.UNIT_NUMBER);
                email_id = getArguments().getString(Extras_Keys.EMAIL);
                cellphone = getArguments().getString(Extras_Keys.CELLPHONE);
                searchLogic = getArguments().getString(Extras_Keys.SEARCH_LOGIC);
                statusFilter = getArguments().getString(Extras_Keys.STATUS_FILTER);
            } else {
                setToolbarTitle(NTF_Utils.getRecipientTypeLabel(ntf_Preferences.get(Prefs_Keys.ACCOUNT_TYPE)) + " List");
                mEditTextSearch.setText("");
                String accTypeName = NTF_Utils.getRecipientTypeLabel(ntf_Preferences.get(Prefs_Keys.ACCOUNT_TYPE));
                if (accountType.toLowerCase().equalsIgnoreCase("apt")) {
                    mEditTextSearch.setHint("Filter by " + accTypeName.toLowerCase() + " name or unit number");
                    searchLayout.setContentDescription("Filter by " + accTypeName.toLowerCase() + " name or unit number" + "Edit Box");
                } else {
                    mEditTextSearch.setHint("Filter by " + accTypeName.toLowerCase() + " name or " + recipientAddress1Label.toLowerCase());
                    searchLayout.setContentDescription("Filter by " + accTypeName.toLowerCase() + " name or " + recipientAddress1Label.toLowerCase() + "Edit Box");
                }
            }
            setToolbarActionButtons(true, false, 0);
            if (!getResources().getBoolean(R.bool.isDeviceTablet))
                mTextViewActionTitle.setTextSize(18);
            tabLayout.setTabTextColors(Color.parseColor("#445C92"), Color.parseColor("#445C92"));
            tabLayout.setTag(R.string.avenir_medium);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (isfromSearchResident) {
                getSearchResults();
            } else {
                doRefreshList();
            }
        }
        //Matching by Recipient
        for (TextView view : mRecipientCheckTextViewList) {
            view.setTag(0);
        }
        if (ntf_Preferences.getBoolean(Prefs_Keys.RECIPIENT_ADDED_OR_UPDATED)) {
            ntf_Preferences.save(Prefs_Keys.RECIPIENT_ADDED_OR_UPDATED, false);
        }
    }

    private void getSearchResults() {
        if (NTF_Utils.isOnline(getActivity())) {
            clear();
            NTF_Utils.showProgressDialog(getActivity());
            SearchResidentRequest request = new SearchResidentRequest();
            request.setSessionId(ntf_Preferences.get(Prefs_Keys.SESSION_ID));
            request.setAuthenticationToken(ntf_Preferences.get(Prefs_Keys.AUTHENTICATION_TOKEN));
            request.setAccountId(ntf_Preferences.get(Prefs_Keys.ACCOUNT_ID));
            request.setUserId(ntf_Preferences.get(Prefs_Keys.USER_ID));
            request.setFirstName(first_name);
            request.setLastName(last_name);
            request.setAddress1(unit_number);
            request.setEmail(email_id);
            request.setCellphone(cellphone);
            request.setMatchLogic(searchLogic);
            request.setRecipientStatus(statusFilter);

            searchRecipientPresenter.searchRecipientList(null, request);
        } else {
            NTF_Utils.showNoNetworkAlert(getActivity());
            ((MainActivity) getActivity()).registerReceiver();
        }
    }

    // Preparing the service request for Getting Recipient List....
    public void doRefreshList() {
        if (!isfromSearchResident) {
            mEditTextSearch.setText("");
        }
        if (NTF_Utils.isOnline(getActivity())) {
            clear();
            NTF_Utils.showProgressDialog(getActivity());
            RecipientListRequest request = new RecipientListRequest();
            request.setSessionId(ntf_Preferences.get(Prefs_Keys.SESSION_ID));
            request.setAuthenticationToken(ntf_Preferences.get(Prefs_Keys.AUTHENTICATION_TOKEN));
            request.setAccountId(ntf_Preferences.get(Prefs_Keys.ACCOUNT_ID));
            request.setUserId(ntf_Preferences.get(NTF_Constants.Prefs_Keys.USER_ID));
            recipientsListPresenter.getRecipientList(null, request);
        } else {
            NTF_Utils.showNoNetworkAlert(getActivity());
            ((MainActivity) getActivity()).registerReceiver();
        }
    }

    @Override
    public void onRecipientsListSuccess(JSONObject jsonObject) {
        if (!isfromSearchResident) {
            NTF_Utils.saveRecipientsData(getActivity(), jsonObject);
        }
        bindToListView(jsonObject);
    }

    // Binding the Recipient list data received from the service response to the ListView
    private void bindToListView(Object object) {
        try {
            JSONObject jsonObject = new JSONObject(object.toString());
            int noofResidents = isfromSearchResident ? jsonObject.optInt("number_matching_recipients") : jsonObject.optInt("number_recipients");
            Runnable bgRun = new Runnable() {
                @Override
                public void run() {
                    ArrayList<Recipient> recipientArrayList = Recipient.getRecipientsList(
                            isfromSearchResident ? jsonObject.optJSONArray("matching_recipients")
                                    : jsonObject.optJSONArray("recipients"));
                    mRecipientList.clear();
                    mRecipientList.addAll(recipientArrayList);
                }
            };
            Runnable uiRun = new Runnable() {
                @Override
                public void run() {
                    NTF_Utils.hideProgressDialog();
                    tabLinear.setVisibility(View.VISIBLE);
                    viewPager.setVisibility(View.VISIBLE);
                    setupViewPager(viewPager);
                    tabLayout.setupWithViewPager(viewPager);
//                    tabLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT);
                    if (noofResidents > 0) {
                        setListToAdapter();
                    } else {
                        tabLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT);
//                        NTF_Utils.showAlert(getActivity(), "", "No data available in table", null);
                    }
                }
            };
            new RecipientJsonToModel(bgRun, uiRun).execute();
            setCountLables(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            NTF_Utils.showAlert(getActivity(), "", "Unable to refresh list", null);
        }
    }

    private void setListToAdapter() {
        try {
            if (mRecipientList != null && !mRecipientList.isEmpty()) {
                if (adapter == null) {
                    tabLinear.setVisibility(View.VISIBLE);
                    viewPager.setVisibility(View.VISIBLE);
                    setupViewPager(viewPager);
                    tabLayout.setupWithViewPager(viewPager);
                } else {
                    EventBus.getDefault().post(new RefreshRecipientList(mRecipientList), "newDataSetAvailable");
                }
                setUpTabClickListener();
                recipientFragment.sortRecipientNameAscending();
                adapter.notifyDataSetChanged();
            } else {
                setupViewPager(viewPager);
                tabLayout.setupWithViewPager(viewPager);
                adapter.notifyDataSetChanged();
                tabLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT);
                NTF_Utils.showAlert(getActivity(), "", "Your residents list is not available. Please go to More and hit the Refresh button.", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setupViewPager(ViewPager viewPager) {
        try{
            adapter = new RecipientListViewPagerAdapter(getChildFragmentManager());
            unitFragment = new UnitFragment();
            unitFragment.setCurrentFragment("CompleteRecipientListFragment");
            unitFragment.setmRecipientList(mRecipientList);
            recipientFragment = new RecipientFragment();
            recipientFragment.setCurrentFragment("CompleteRecipientListFragment");
            recipientFragment.setmRecipientList(mRecipientList);

            adapter.addFragment(recipientFragment, "Name");
            adapter.addFragment(unitFragment, recipientAddress1Label.equals("Mailbox Number") ? "Mailbox #" : recipientAddress1Label);
            viewPager.setAdapter(adapter);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setUpTabClickListener() {
        try {
            setUpTabDescriptions();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!isListenerActivated) {
            tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    super.onTabUnselected(tab);
                    tab.setTag("0");
                }

                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    super.onTabSelected(tab);
                    tab.setTag("0");
                    setUpTabDescriptions();
                    if (mRecipientList.size() > 0) {
                        if (tab.getPosition() == 1 && tab.getTag().toString().equals("0")) {
                            unitFragment.sortRecipientAddressAscending();
                            tab.setTag("1");
                        } else {
                            recipientFragment.sortRecipientNameAscending();
                        }
                    }

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    super.onTabReselected(tab);
                    tab1 = tab;
                    if (tab.getPosition() == 0) {
                        if (tab.getTag().toString().equals("0")) {
                            recipientFragment.sortRecipientNameByDescending();
                            tab.setTag("1");
                        } else {
                            recipientFragment.sortRecipientNameAscending();
                            tab.setTag("0");
                        }
                    } else if (tab.getPosition() == 1) {
                        if (tab.getTag().toString().equals("0")) {
                            unitFragment.sortRecipientAddressAscending();
                            tab.setTag("1");
                        } else {
                            unitFragment.sortRecipientAddressByDescending();
                            tab.setTag("0");
                        }
                    }
                }
            });
            isListenerActivated = true;
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
    public void onRecipientListError(String message) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(getActivity(), "", message, null);
        tabLinear.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.VISIBLE);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT);
        totalListCount.setText(listSize + " Results");
        totalListCount.setContentDescription(listSize + " Results");
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


    // This method used to set the total count of matching recipients with search data...
    private void setCountLables(JSONObject jsonObject) {
        int noofResidents = isfromSearchResident ? jsonObject.optInt("number_matching_recipients") : jsonObject.optInt("number_recipients");
        totalListCount.setText("" + noofResidents + " Results");
        totalListCount.setContentDescription("" + noofResidents + " Results");
    }

    private void clear() {
        try {
            if (mRecipientList != null) {
                mRecipientList.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnTouch(R.id.editTextSearch)
    public boolean onTouch() {
        mEditTextSearch.setCursorVisible(true);
        return false;
    }

    @OnTextChanged(value = R.id.editTextSearch, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged(CharSequence s) {
        String text = s.toString().trim();
        if (!isfromSearchResident) {
            if (adapter != null) {
                if (TextUtils.isEmpty(text)) {
                    mEditTextSearch.setCursorVisible(true);
                    mImageButtonClear.setVisibility(View.GONE);
                    listSize = recipientFragment.doSortList(text);
                    unitFragment.doSortList(text);
                    if (viewPager.getCurrentItem() == 0) {
                        recipientFragment.sortRecipientNameAscending();
                        if (tab1 != null) {
                            tab1.setTag("0");
                        }
                    } else if (viewPager.getCurrentItem() == 1) {
                        unitFragment.sortRecipientAddressAscending();
                        if (tab1 != null) {
                            tab1.setTag("1");
                        }
                    }
                } else {
                    mEditTextSearch.setCursorVisible(true);
                    mImageButtonClear.setVisibility(View.VISIBLE);
                    listSize = recipientFragment.doSortList(text);
                    unitFragment.doSortList(text);
                }
                totalListCount.setText(listSize + " Results");
                totalListCount.setContentDescription(listSize + " Results");
            }
        }
    }

    @OnClick(R.id.imageButtonClear)
    public void onClearSubmit() {
        mEditTextSearch.setText("");
        mImageButtonClear.setVisibility(View.GONE);
    }

    @OnClick(R.id.editTextSearch)
    public void onSearchClick() {
        mEditTextSearch.setCursorVisible(true);
    }

    @OnClick({R.id.backImage, R.id.mobile_left_linear})
    public void onBackClick() {
        NTF_Utils.hideKeyboard(getActivity());
        getActivity().onBackPressed();
    }

    public void setUpTabDescriptions() {
        tabLayout.getTabAt(0).setContentDescription("Sort by " + tabLayout.getTabAt(0).getText() + "button");
        tabLayout.getTabAt(1).setContentDescription("Sort by" + tabLayout.getTabAt(1).getText() + "button");
    }
}
