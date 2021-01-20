package com.notifii.notifiiapp.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.tabs.TabLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.activities.MainActivity;
import com.notifii.notifiiapp.adapters.PackageHistoryViewPagerAdapter;
import com.notifii.notifiiapp.base.NTF_BaseFragment;
import com.notifii.notifiiapp.customui.NonSwipeableViewPager;
import com.notifii.notifiiapp.events.RefreshPackagesList;
import com.notifii.notifiiapp.models.Package;
import com.notifii.notifiiapp.mvp.models.PackageHistoryRequest;
import com.notifii.notifiiapp.mvp.models.SearchPackageRequest;
import com.notifii.notifiiapp.mvp.presenters.PackageHistoryforAccountPresenter;
import com.notifii.notifiiapp.mvp.presenters.SearchPackageListPresenter;
import com.notifii.notifiiapp.mvp.views.PackagHistoryView;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Created by Administrator on 8/4/2016.
 */
public class PackageHistoryFragment extends NTF_BaseFragment implements PackagHistoryView {

    @BindView(R.id.editTextSearch)
    public EditText mEtSearch;
    @BindView(R.id.filter_dialog_imageButton)
    public ImageView mIvFilterDialog;
    @BindView(R.id.tabs)
    public TabLayout mTabLayout;
    @BindView(R.id.iv_descending_sort)
    public ImageView receivedImageView;
    @BindView(R.id.iv_ascending_sort)
    public ImageView pickedUpImageView;
    @BindView(R.id.viewpager)
    public NonSwipeableViewPager mViewPager;
    @BindView(R.id.imageButtonClear)
    public ImageButton mImageButtonClear;
    @BindView(R.id.tab_linear)
    public LinearLayout tabLinear;
    @BindView(R.id.received_layout)
    public LinearLayout receivedLayout;
    @BindView(R.id.pickedup_layout)
    public LinearLayout pickedUpLayout;
    @BindView(R.id.pickedup_view)
    public View pickedupView;
    @BindView(R.id.received_view)
    public View receivedView;
    @BindView(R.id.search_layout)
    LinearLayout searchLayout;
    @BindView(R.id.filter_sort_divider_view)
    View searchView;

    private PackageHistoryViewPagerAdapter packageHistoryViewPagerAdapter;
    private String recipientAddress1Label;
    public String mMailRoomId;
    private List<Package> mPakgList = new ArrayList<>();
    private PkgHistoryRecipientFragment frag1;
    private PkgHistoryUnitFragment frag2;
    PackageHistoryforAccountPresenter packageHistoryforAccountPresenter;
    SearchPackageListPresenter searchPackageListPresenter;
    private String unitNumber;
    private boolean isFromSearchPkg;
    String trackingNumber, recipientName, sender, shelf, mailboxNumber, tagNumber, customMessage, staffNotes, searchLogic, poNumber, daterange;
    public static boolean isFromPkgHistory;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_package_history_fragment_new, null);
        ButterKnife.bind(this, mainView);
        return mainView;
    }

    @Override
    public void onStackChanged() {
        super.onStackChanged();
        if (NTF_Utils.getCurrentFragment(this) != null && NTF_Utils.getCurrentFragment(this) instanceof PackageHistoryFragment) {
            onFragmentPopup(true);
        } else {
            onFragmentPopup(false);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        packageHistoryforAccountPresenter = new PackageHistoryforAccountPresenter();
        searchPackageListPresenter = new SearchPackageListPresenter();
        packageHistoryforAccountPresenter.attachMvpView(this);
        searchPackageListPresenter.attachMvpView(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView();
        NTF_Utils.handleDoneButton(mEtSearch, getActivity());
    }

    // Identifying the views in layout...
    private void bindView() {
        try {
            setToolbarActionButtons(true, false, 0);
            isFromSearchPkg = getArguments().getBoolean(Extras_Keys.IS_FROM_SEARCH_PACKAGE);

            if (isFromSearchPkg) {
                setToolbarTitle("Search Results");
                searchLayout.setVisibility(View.GONE);
                searchView.setVisibility(View.GONE);
                trackingNumber = getArguments().getString(Extras_Keys.TRACKING_NUMBER);
                recipientName = getArguments().getString(Extras_Keys.RECIPIENT_NAME);
                mailboxNumber = getArguments().getString(Extras_Keys.MAILBOX_NUMBER);
                sender = getArguments().getString(Extras_Keys.SENDER);
                shelf = getArguments().getString(Extras_Keys.SHELF);
                tagNumber = getArguments().getString(Extras_Keys.TAG_NUMBER);
                customMessage = getArguments().getString(Extras_Keys.CUSTOM_MESSAGE);
                staffNotes = getArguments().getString(Extras_Keys.STAFF_NOTES);
                poNumber = getArguments().getString(Extras_Keys.PO_NUMBER);
                searchLogic = getArguments().getString(Extras_Keys.SEARCH_LOGIC);
                daterange = getArguments().getString(Extras_Keys.DATE_RANGE);
            }
            mMailRoomId = ntf_Preferences.get(NTF_Constants.Prefs_Keys.DEFAULT_MAILROOM_ID);
            pickedupView.setVisibility(View.INVISIBLE);
            mTabLayout.setTabTextColors(Color.parseColor("#445C92"), Color.parseColor("#445C92"));
            mTabLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT);
            String accountType = ntf_Preferences.get(Prefs_Keys.ACCOUNT_TYPE);
            String recipientLabel = NTF_Utils.getRecipientTypeLabel(accountType);
            if (accountType.toLowerCase().equalsIgnoreCase("apt")) {
                recipientAddress1Label = "Unit #";
                unitNumber = "unit number";
            } else {
                recipientAddress1Label = NTF_Utils.getRecipientAddress1Label(accountType);
                unitNumber = NTF_Utils.getRecipientAddress1Label(accountType);
            }
            setupViewPager(mViewPager);
            mTabLayout.setupWithViewPager(mViewPager);
            if (!isFromSearchPkg) {
                setToolbarTitle("Package History");
                String accTypeName = NTF_Utils.getRecipientTypeLabel(ntf_Preferences.get(Prefs_Keys.ACCOUNT_TYPE));
                mEtSearch.setHint("Filter by " + accTypeName.toLowerCase() + " name or " + unitNumber.toLowerCase());
            } else {
                isFromPkgHistory = true;
            }
            receivedImageView.setTag("0");
            pickedUpImageView.setTag("0");
            if (mPakgList.size() > 0) {
                receivedView.setVisibility(View.VISIBLE);
                receivedImageView.setContentDescription("selected sort by date received");
            } else {
                receivedView.setVisibility(View.INVISIBLE);
                receivedImageView.setContentDescription("sort by date received");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!isFromSearchPkg) {

                showFilterDialogFragment();
            } else {
                searchPackageRequest();
            }
        }
    }

    private void setupViewPager(NonSwipeableViewPager viewPager) {
        packageHistoryViewPagerAdapter = new PackageHistoryViewPagerAdapter(getChildFragmentManager());
        packageHistoryViewPagerAdapter.addFragment(frag1 = new PkgHistoryRecipientFragment(mPakgList, isFromSearchPkg), "Name");
        packageHistoryViewPagerAdapter.addFragment(frag2 = new PkgHistoryUnitFragment(mPakgList, isFromSearchPkg), recipientAddress1Label);
        viewPager.setAdapter(packageHistoryViewPagerAdapter);
    }

    private void showFilterDialogFragment() {
        FilterDialogFragment filterDialogFragment = new FilterDialogFragment(this);
        filterDialogFragment.show(getActivity().getFragmentManager(), "Filter Dialog Fragment");
    }

    // Preparing the service request for getting packages...
    public void searchPackageRequest() {
        if (getActivity() != null) {
            if (NTF_Utils.isOnline(getActivity())) {
                NTF_Utils.showProgressDialog(getActivity());
                SearchPackageRequest searchPackageRequest = new SearchPackageRequest();
                searchPackageRequest.setSessionId(ntf_Preferences.get(Prefs_Keys.SESSION_ID));
                searchPackageRequest.setUserId(ntf_Preferences.get(Prefs_Keys.USER_ID));
                searchPackageRequest.setAuthenticationToken(ntf_Preferences.get(Prefs_Keys.AUTHENTICATION_TOKEN));
                searchPackageRequest.setAccountId(ntf_Preferences.get(Prefs_Keys.ACCOUNT_ID));
                searchPackageRequest.setTrackingNumber(trackingNumber);
                searchPackageRequest.setRecipientName(recipientName);
                searchPackageRequest.setRecipientAddress1(mailboxNumber);
                searchPackageRequest.setSender(sender);
                searchPackageRequest.setPoNumber(poNumber);
                searchPackageRequest.setTagNumber(tagNumber);
                searchPackageRequest.setShelf(shelf);
                searchPackageRequest.setCustomMessage(customMessage);
                searchPackageRequest.setStaffNote(staffNotes);
                if (searchLogic == null) {
                    searchPackageRequest.setSearchCriteria("any");
                } else {
                    if (searchLogic.equalsIgnoreCase("0")) {
                        searchPackageRequest.setSearchCriteria("any");
                    } else {
                        searchPackageRequest.setSearchCriteria("all");
                    }
                }
                if (daterange == null) {
                    searchPackageRequest.setPackageDateRange("last_3_months");
                } else {
                    searchPackageRequest.setPackageDateRange(daterange);
                }

                searchPackageListPresenter.getSearchPackageList(null, searchPackageRequest);
            } else {
                NTF_Utils.showNoNetworkAlert(getActivity());
                ((MainActivity) getActivity()).registerReceiver();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isFromPkgHistory = false;
    }

    // Preparing the service request for getting packages...
    public void packageHistoryRequest(String showPkgs, String mailRoomId, String startDate, String endDate, String strFilterBy) {
        if (getActivity() != null) {
            if (NTF_Utils.isOnline(getActivity())) {
                NTF_Utils.showProgressDialog(getActivity());
                this.mMailRoomId = mailRoomId;
                PackageHistoryRequest packageHistoryRequest = new PackageHistoryRequest();
                packageHistoryRequest.setAccountId(ntf_Preferences.get(Prefs_Keys.ACCOUNT_ID));
                packageHistoryRequest.setSessionId(ntf_Preferences.get(Prefs_Keys.SESSION_ID));
                packageHistoryRequest.setUserId(ntf_Preferences.get(Prefs_Keys.USER_ID));
                packageHistoryRequest.setAuthenticationToken(ntf_Preferences.get(Prefs_Keys.AUTHENTICATION_TOKEN));
                packageHistoryRequest.setHistoryType(showPkgs);
                packageHistoryRequest.setMailroomId(mailRoomId);
                packageHistoryRequest.setStartDate(getFormatedDate(startDate));
                packageHistoryRequest.setEndDate(getFormatedDate(endDate));
                packageHistoryRequest.setFilterBy(strFilterBy);
                packageHistoryforAccountPresenter.getPackageHistoryList(null, packageHistoryRequest);
            } else {
                NTF_Utils.showNoNetworkAlert(getActivity());
                ((MainActivity) getActivity()).registerReceiver();
            }
        }
    }

    private String getFormatedDate(String startDate) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm-dd-yyyy");
            SimpleDateFormat basicDateFormat = new SimpleDateFormat("yyyy-mm-dd");
            Date date = simpleDateFormat.parse(startDate);
            return basicDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void onPackageHistoryforAccountSuccess(JSONObject jsonObject) {
        NTF_Utils.hideProgressDialog();
        mEtSearch.setText("");
        bindToListView(jsonObject);
        frag1.sortDateReceivedAscending();
        receivedImageView.setTag("1");
        if (mPakgList.size() > 0) {
            receivedImageView.setContentDescription("selected sort by date received");
            receivedView.setVisibility(View.VISIBLE);
        } else {
            receivedView.setVisibility(View.INVISIBLE);
            receivedImageView.setContentDescription("sort by date received");
        }
        pickedupView.setVisibility(View.INVISIBLE);
        mTabLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT);
    }

    // Binding the packages list to the ListView...
    private void bindToListView(Object object) {
        try {
            JSONObject jsonObject = new JSONObject(object.toString());
            JSONArray jsonArray;
            if (isFromSearchPkg) {
                String no_matching_pkgs = jsonObject.getString("number_matching_packages");
                if (no_matching_pkgs.equalsIgnoreCase("0")) {
                    mTabLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT);
                    NTF_Utils.showAlert(getActivity(), "", "There are no matching packages.", null);
                    return;
                }
                jsonArray = jsonObject.getJSONArray("matching_packages");
            } else {
                jsonArray = jsonObject.getJSONArray("packages");
            }
            if (jsonArray != null && jsonArray.length() == 0) {
                mTabLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT);
                mPakgList.clear();
                if(packageHistoryViewPagerAdapter!=null)
                    packageHistoryViewPagerAdapter.notifyDataSetChanged();
                NTF_Utils.showAlert(getActivity(), "", "There are no matching packages.", null);
            } else {
                tabLinear.setVisibility(View.VISIBLE);
                mViewPager.setVisibility(View.VISIBLE);
                pickedUpImageView.setVisibility(View.VISIBLE);
                receivedImageView.setVisibility(View.VISIBLE);
                mTabLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT);
                mPakgList.clear();
                if (jsonArray.length() > 0) {
                    Log.v("Matching List Size : ", "" + jsonArray.length());
                    mPakgList.addAll(com.notifii.notifiiapp.models.Package.getPackageHistory(jsonArray));
                }

                if (mPakgList.size() == 0) {
                    receivedView.setVisibility(View.INVISIBLE);
                    pickedupView.setVisibility(View.INVISIBLE);
                }
            }

            if (mPakgList != null) {
                ((PkgHistoryRecipientFragment) packageHistoryViewPagerAdapter.getItem(0)).packageHistoryAdapter.update(mPakgList);
                ((PkgHistoryUnitFragment) packageHistoryViewPagerAdapter.getItem(1)).packageHistoryAdapter.update(mPakgList);
                if (packageHistoryViewPagerAdapter != null) {
                    setUpTabClickListener();
                } else {
                    EventBus.getDefault().post(new RefreshPackagesList(mPakgList), "newPackagesData");
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setUpTabClickListener() {
        try {
            mTabLayout.getTabAt(0).setTag("0");
            mTabLayout.getTabAt(1).setTag("0");
            mTabLayout.getTabAt(0).setContentDescription("Sort by " + mTabLayout.getTabAt(0).getText() + "button");
            mTabLayout.getTabAt(1).setContentDescription("Sort by" + mTabLayout.getTabAt(1).getText() + "button");
        } catch (Exception e) {
            e.printStackTrace();
        }

        mTabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                tab.setTag("0");
            }

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                tab.setTag("0");
                if (mPakgList.size() > 0) {
                    mTabLayout.setSelectedTabIndicatorColor(ActivityCompat.getColor(getActivity(), R.color.colorPrimary));
                }
                receivedView.setVisibility(View.INVISIBLE);
                pickedupView.setVisibility(View.INVISIBLE);
                receivedImageView.setContentDescription("sort by date received");
                pickedUpImageView.setContentDescription("sort by date picked up");
                if (tab.getPosition() == 1 && tab.getTag().toString().equals("0")) {
                    frag2.sortRecipientAddressAscending();
                    tab.setTag("1");
                } else if (tab.getPosition() == 0 && tab.getTag().toString().equals("0")) {
                    frag1.sortRecipientNameAscending(mPakgList);
                    tab.setTag("1");
                }

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                super.onTabReselected(tab);
                if (mPakgList.size() > 0) {
                    mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                }
                receivedView.setVisibility(View.INVISIBLE);
                pickedupView.setVisibility(View.INVISIBLE);
                receivedImageView.setContentDescription("sort by date received");
                pickedUpImageView.setContentDescription("sort by date picked up");
                if (tab.getPosition() == 0) {
                    Log.v("Position: ", "0");
                    if (tab.getTag().toString().equals("0")) {
                        frag1.sortRecipientNameAscending(mPakgList);
                        tab.setTag("1");
                    } else {
                        frag1.sortRecipientNameByDescending();
                        tab.setTag("0");
                    }
                } else if (tab.getPosition() == 1) {
                    if (tab.getTag().toString().equals("0")) {
                        frag2.sortRecipientAddressAscending();
                        tab.setTag("1");
                    } else {
                        frag2.sortRecipientAddressByDescending();
                        tab.setTag("0");
                    }
                }
            }
        });
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

    @OnClick(R.id.editTextSearch)
    public void onclickSearch() {
        mEtSearch.setCursorVisible(true);
    }

    @OnTextChanged(value = R.id.editTextSearch, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged(CharSequence text) {
        String s = text.toString().trim();
        try{
            if (!isFromSearchPkg) {
                if (packageHistoryViewPagerAdapter != null /*&& ((PkgHistoryRecipientFragment) adapter.getItem(0)).packageHistoryAdapter != null && ((PkgHistoryUnitFragment) adapter.getItem(1)).packageHistoryAdapter != null*/) {
                    if (TextUtils.isEmpty(s.toString())) {
                        mEtSearch.setCursorVisible(true);
                        mImageButtonClear.setVisibility(View.GONE);
                        if (mViewPager.getCurrentItem() == 0) {
                            ((PkgHistoryRecipientFragment) packageHistoryViewPagerAdapter.getItem(0)).packageHistoryAdapter.filter(s.toString());
                            frag1.sortRecipientNameAscending(mPakgList);
                            mTabLayout.getTabAt(0).setTag("1");
                        } else if (mViewPager.getCurrentItem() == 1) {
                            ((PkgHistoryUnitFragment) packageHistoryViewPagerAdapter.getItem(1)).packageHistoryAdapter.filter(s.toString());
                            frag2.sortRecipientAddressAscending();
                            mTabLayout.getTabAt(1).setTag("1");
                        }
                    } else {
                        mEtSearch.setCursorVisible(true);
                        mImageButtonClear.setVisibility(View.VISIBLE);
                        if (mViewPager.getCurrentItem() == 0) {
                            ((PkgHistoryRecipientFragment) packageHistoryViewPagerAdapter.getItem(0)).packageHistoryAdapter.filter(s.toString());
                            frag1.sortRecipientNameAscending(mPakgList);
                        } else if (mViewPager.getCurrentItem() == 1) {
                            ((PkgHistoryUnitFragment) packageHistoryViewPagerAdapter.getItem(1)).packageHistoryAdapter.filter(s.toString());
                            frag2.sortRecipientAddressAscending();
                        }
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @OnClick(R.id.filter_dialog_imageButton)
    public void onClickFilterDialog() {
        if (getActivity() != null) {
            showFilterDialogFragment();
        }
    }

    @OnClick({R.id.iv_descending_sort, R.id.received_layout})
    public void onClickDescending() {
        // mTabLayout.setSelectedTabIndicatorHeight(0);
        mTabLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT);
        if (mPakgList.size() > 0) {
            receivedView.setVisibility(View.VISIBLE);
            pickedupView.setVisibility(View.INVISIBLE);
            receivedImageView.setContentDescription("selected sort by date received");
            pickedUpImageView.setContentDescription("sort by date picked up");
        } else {
            receivedView.setVisibility(View.INVISIBLE);
            pickedupView.setVisibility(View.INVISIBLE);
            receivedImageView.setContentDescription("sort by date received");
            pickedUpImageView.setContentDescription("sort by date picked up");
        }

        if (receivedImageView.getTag().toString().equals("0")) {
            if (mViewPager.getCurrentItem() == 0) {
                frag1.sortDateReceivedAscending();
            } else {
                frag2.sortDateReceivedAscending();
            }
            receivedImageView.setTag("1");
        } else {
            if (mViewPager.getCurrentItem() == 0) {
                frag1.sortDateReceivedByDescending();
            } else {
                frag2.sortDateReceivedByDescending();
            }
            receivedImageView.setTag("0");
        }
    }

    @OnClick({R.id.iv_ascending_sort, R.id.pickedup_layout})
    public void onclickAscending() {
        //  mTabLayout.setSelectedTabIndicatorHeight(0);
        mTabLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT);
        if (mPakgList.size() > 0) {
            receivedView.setVisibility(View.INVISIBLE);
            pickedupView.setVisibility(View.VISIBLE);
            pickedUpImageView.setContentDescription("selected sort by date picked up");
            receivedImageView.setContentDescription("sort by date received");
        } else {
            receivedView.setVisibility(View.INVISIBLE);
            pickedupView.setVisibility(View.INVISIBLE);
            pickedupView.setContentDescription("sort by date picked up");
            receivedImageView.setContentDescription("sort by date received");
        }
        if (pickedUpImageView.getTag().toString().equals("0")) {
            if (mViewPager.getCurrentItem() == 0) {
                frag1.sortDateDeliveredAscending();
            } else {
                frag2.sortDateDeliveredAscending();
            }
            pickedUpImageView.setTag("1");
        } else {
            if (mViewPager.getCurrentItem() == 0) {
                frag1.sortDateDeliveredByDescending();
            } else {
                frag2.sortDateDeliveredByDescending();
            }
            pickedUpImageView.setTag("0");
        }
    }

    @OnClick(R.id.imageButtonClear)
    public void onClickClear() {
        // Handle clicks for mImageButtonClear
        mEtSearch.setText("");
        mImageButtonClear.setVisibility(View.GONE);
    }


}
