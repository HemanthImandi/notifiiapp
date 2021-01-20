package com.notifii.notifiiapp.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnTextChanged;

import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.activities.IdentifyUserActivity;
import com.notifii.notifiiapp.activities.MainActivity;
import com.notifii.notifiiapp.activities.SelectMailroomActivity;
import com.notifii.notifiiapp.adapters.LogPackageOutAdapter;
import com.notifii.notifiiapp.adapters.SelectedPackagesAdapter;
import com.notifii.notifiiapp.helpers.SingleTon;
import com.notifii.notifiiapp.models.Package;
import com.notifii.notifiiapp.base.NTF_BaseFragment;
import com.notifii.notifiiapp.models.SpinnerData;
import com.notifii.notifiiapp.mvp.models.KioskLoginRequest;
import com.notifii.notifiiapp.mvp.models.KioskLoginResponse;
import com.notifii.notifiiapp.mvp.models.PendingPackagesRequest;
import com.notifii.notifiiapp.mvp.presenters.KioskLoginPresenter;
import com.notifii.notifiiapp.mvp.presenters.PendingPackagesPresenter;
import com.notifii.notifiiapp.mvp.views.KioskLoginView;
import com.notifii.notifiiapp.mvp.views.PendingPackagesView;
import com.notifii.notifiiapp.sorting.AlphaNueralSortAscending;
import com.notifii.notifiiapp.sorting.AlphaNueralSortDecending;
import com.notifii.notifiiapp.sorting.SortByAddressAsc;
import com.notifii.notifiiapp.sorting.SortByAddressDesc;
import com.notifii.notifiiapp.sorting.SortByDateAscending;
import com.notifii.notifiiapp.sorting.SortByDateDescending;
import com.notifii.notifiiapp.sorting.SortByMainRoomAscending;
import com.notifii.notifiiapp.sorting.SortByMainRoomDescending;
import com.notifii.notifiiapp.sorting.SortByNameAscending;
import com.notifii.notifiiapp.sorting.SortByNameDescending;
import com.notifii.notifiiapp.sorting.SortByPkgTypeAscending;
import com.notifii.notifiiapp.sorting.SortByPkgTypeDescending;
import com.notifii.notifiiapp.sorting.SortBySenderAscending;
import com.notifii.notifiiapp.sorting.SortBySenderDescending;
import com.notifii.notifiiapp.sorting.SortByShelfAscending;
import com.notifii.notifiiapp.sorting.SortByShelfDescending;
import com.notifii.notifiiapp.sorting.SortByTagNumberAscending;
import com.notifii.notifiiapp.sorting.SortByTagNumberDescending;
import com.notifii.notifiiapp.sorting.TrackingAscending;
import com.notifii.notifiiapp.sorting.TrackingDescending;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class LogPackageOutFragment extends NTF_BaseFragment implements KioskLoginView, PendingPackagesView {

    @BindView(R.id.rv_packages)
    RecyclerView rvPackages; //tag 0 for pending pkgs list, tag 1 for selected pkgs list
    @BindView(R.id.pending_pkgs_count)
    TextView pendingPkgsCount;
    @BindView(R.id.selected_pkgs_count)
    TextView selectedPkgsCount;
    @BindView(R.id.editTextSearch)
    EditText editTextSearch;
    @BindView(R.id.searchClear)
    ImageButton searchClear;
    @BindView(R.id.checkall)
    ImageView checkall;
    @BindView(R.id.multiple_edit_iv)
    ImageView multipleEdit;
    @BindView(R.id.no_selected_packagesTV)
    TextView noSelectedPackagesTV;
    @BindView(R.id.buttonSignOutPkg)
    View buttonSignOutPkg;
    @BindView(R.id.parentLayout)
    LinearLayout parentLayout;

    private KioskLoginPresenter kioskLoginPresenter;
    private PendingPackagesPresenter packagesPresenter;
    private PendingPackagesRequest preRequest;
    private LogPackageOutAdapter mAdapter;
    private ArrayList<Package> mPackagelist;
    private Activity context;
    private View footerView;
    private FooterViewHolder footerViewHolder;
    private ArrayList<Package> selectedPackages;
    private Handler handler;
    private boolean isThereMorePackages = false;
    private boolean isProgressbarShown = false;
    private Timer timer;
    private TimerTask timerTask;
    private SelectedPackagesAdapter selectedPackagesAdapter;
    public static final int UPDATEPACKAGEREQUESTCODE = 21, LOGOUTPKGREQUESTCODE = 22;
    private BackPressedRecevicer recevicer;
    private boolean isResponseHandled = false, textchangedAccessgranted = false;
    private int pendingPkgLimit=100;

    public LogPackageOutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStackChanged() {
        super.onStackChanged();
        if (NTF_Utils.getCurrentFragment(this) != null && NTF_Utils.getCurrentFragment(this) instanceof LogPackageOutFragment) {
            onFragmentPopup(true);
        } else {
            onFragmentPopup(false);
        }
    }

    class FooterViewHolder {

        @BindView(R.id.spinnerImageView)
        ImageView spinnerImageView;

        private FooterViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        private void showSpinner() {
            try {
                if (NTF_Utils.isOnline(getActivity())){
                    spinnerImageView.setVisibility(View.VISIBLE);
                    AnimationDrawable spinner = (AnimationDrawable) spinnerImageView.getBackground();
                    spinner.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void hideSpinner() {
            spinnerImageView.setVisibility(View.GONE);
            AnimationDrawable spinner = (AnimationDrawable) spinnerImageView.getBackground();
            if (spinner.isRunning())
                spinner.stop();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_log_package_out, container, false);
        ButterKnife.bind(this, mainView);
        rvPackages.setLayoutManager(new LinearLayoutManager(getActivity()));
        footerView = inflater.inflate(R.layout.log_pkg_out_footer, rvPackages, false);
        return mainView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setToolbarActionButtons(false, true, R.drawable.ic_scanner_white_icon);
        changeBackBTNIcon(R.drawable.ic_kiosk);
        setLogo(R.drawable.ic_notifii_track_white_logo);
        setRightButtonContent("Barcode scanning");
        String accountType = ntf_Preferences.get(Prefs_Keys.ACCOUNT_TYPE);
        String accTypeName = NTF_Utils.getRecipientTypeLabel(accountType);
        if (accountType != null) {
            editTextSearch.setHint("Filter by " + accTypeName.toLowerCase() + " name or " + NTF_Utils.getRecipientAddress1Label(accountType).toLowerCase());
        }
        if (!ntf_Preferences.get(Prefs_Keys.PRIVILEGE_TRACK_PACKAGES).equalsIgnoreCase("f")) {
            ((MainActivity) (getActivity() != null ? getActivity() : context)).enableTransparentLayer();
            parentLayout.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
            NTF_Utils.showInSufficientPrivelegeAlert(getActivity() != null ? getActivity() : context);
            return;
        } else {
            ((MainActivity) (getActivity() != null ? getActivity() : context)).disableTransparentLayer();
        }
        footerViewHolder = new FooterViewHolder(footerView);
        kioskLoginPresenter = new KioskLoginPresenter();
        kioskLoginPresenter.attachMvpView(this);
        packagesPresenter = new PendingPackagesPresenter();
        packagesPresenter.attachMvpView(this);
        mPackagelist = new ArrayList<>();
        selectedPackages = new ArrayList<>();
        handler = new Handler();
        mAdapter = new LogPackageOutAdapter(getActivity() != null ? getActivity() : context,
                mPackagelist, ntf_Preferences.get(NTF_Constants.Prefs_Keys.TIMEZONE));
        mAdapter.setFooterView(footerView);
        selectedPackagesAdapter = new SelectedPackagesAdapter(getActivity() != null ? getActivity() : context,
                selectedPackages, ntf_Preferences.get(NTF_Constants.Prefs_Keys.TIMEZONE));
        setSettingsToAdapter(mAdapter, selectedPackagesAdapter);
        rvPackages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    startAutoRefreshTimer();
                }
                if (rvPackages.getTag().equals("1")) {
                    return;
                }
                if (!recyclerView.canScrollVertically(1)) {//direction integers: -1 for up, 1 for down, 0 will always return false.
                    try {
                        if (!NTF_Utils.isOnline(getActivity())) {
                            NTF_Utils.showNoNetworkAlert(getActivity());
                            return;
                        }
                        if (preRequest != null && isThereMorePackages && footerViewHolder.spinnerImageView.getVisibility() == View.GONE) {
                            footerViewHolder.showSpinner();
                            preRequest.setLimitCycle(String.valueOf(Integer.parseInt(preRequest.getLimitCycle()) + 1));
                            getPendingPackages();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        try {
            ((SimpleItemAnimator) rvPackages.getItemAnimator()).setSupportsChangeAnimations(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        rvPackages.setAdapter(mAdapter);
        isProgressbarShown = true;
        NTF_Utils.showProgressDialog(getActivity() != null ? getActivity() : context);
        preRequest = null;
        isResponseHandled = false;
        editTextSearch.post(new Runnable() {
            @Override
            public void run() {
                textchangedAccessgranted = false;
                editTextSearch.setText("");
                textchangedAccessgranted = true;
            }
        });
        getPendingPackages();
        startAutoRefreshTimer();
    }

    // to get the callback from single package edit an multiple package edit and log out pkg screen
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            makeScreenBlank();
            if (!NTF_Utils.isOnline(getActivity())) {
                ((MainActivity) getActivity()).registerReceiver();
                NTF_Utils.showNoNetworkAlert(getActivity());
                return;
            }
            if (resultCode == getActivity().RESULT_OK && requestCode == UPDATEPACKAGEREQUESTCODE) {
                preRequest.setLimitCycle("0");
                NTF_Utils.showProgressDialog(getActivity() != null ? getActivity() : context);
                getPendingPackages();
            } else if (resultCode == getActivity().RESULT_OK && requestCode == LOGOUTPKGREQUESTCODE) {
                isProgressbarShown = true;
                if (preRequest != null){
                    preRequest.setLimitCycle("0");
                }
//                editTextSearch.setText("");
                NTF_Utils.showProgressDialog(getActivity() != null ? getActivity() : context);
//                if (Integer.valueOf(pendingPkgsCount.getText().toString().trim()) < 100) {
                    getPendingPackages();
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(finishBackgroundCall);
            getActivity().unregisterReceiver(recevicer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onKeyBoardStateChanged(boolean isOpen) {
        if (isOpen) {
            buttonSignOutPkg.setVisibility(View.GONE);
        } else
            buttonSignOutPkg.post(new Runnable() {
                @Override
                public void run() {
                    buttonSignOutPkg.setVisibility(View.VISIBLE);
                }
            });
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(finishBackgroundCall,
                    new IntentFilter(Extras_Keys.LOCAL_BROADCAST_END_ACTION));
            IntentFilter ifilter_receiver = new IntentFilter(NTF_Constants.LPOFTABCLICKED);
            recevicer = new BackPressedRecevicer();
            getActivity().registerReceiver(recevicer, ifilter_receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //after getting the callback from log pkg out screen make screen empty(no items in list)
    private void makeScreenBlank() {
        isResponseHandled=false;
        isThereMorePackages=false;
        multipleEdit.setVisibility(View.GONE);
        checkall.setVisibility(View.GONE);
        pendingPkgsCount.setText("0");
        selectedPkgsCount.setText("0");
        selectedPackages.clear();
        selectedPackagesAdapter.notifyDataSetChanged();
        mPackagelist.clear();
        mAdapter.notifyDataSetChanged();
        mAdapter.getmOrinalList().clear();
    }

    @OnEditorAction(R.id.editTextSearch)
    boolean onEditorAction(TextView v, int actionId, KeyEvent key) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            NTF_Utils.hideKeyboard(getActivity() != null ? getActivity() : context);
            return true;
        }
        return false;
    }


    @OnClick(R.id.multiple_edit_iv)
    void onMultipleEditClicked() {
        if (!(NTF_Utils.getCurrentFragment(this) instanceof LogPackageOutFragment)) {
            return;
        }
        onKeyBoardStateChanged(false);
        UpdatePackageFragment fragment = new UpdatePackageFragment();
        fragment.setList(selectedPackages);
        fragment.setTargetFragment(this, UPDATEPACKAGEREQUESTCODE);
        changeDetailsFragment(fragment);
    }

    //to resolve the ripple effect on list make everything clickable required
    @OnClick({R.id.searchLinear, R.id.parentLayout, R.id.packagescountlinear, R.id.buttonlinear})
    void onParentLayoutClicked() {

    }

    @OnClick({R.id.pendingPackagesTV, R.id.pending_pkgs_count})
    void onPendingPackagesClicked() {
        if (editTextSearch.getTag().toString().length() > 0 && mPackagelist.size() > 1) {
            checkall.setVisibility(View.VISIBLE);
        }
        rvPackages.setVisibility(View.VISIBLE);
        noSelectedPackagesTV.setVisibility(View.GONE);
        rvPackages.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        rvPackages.setTag("0");
    }

    @OnClick({R.id.selectedPackagesTV, R.id.selected_pkgs_count})
    void selectedPackagesClicked() {
        if (selectedPackages.size() == 0)
            return;
        rvPackages.setAdapter(selectedPackagesAdapter);
        rvPackages.setTag("1");
    }

    private BroadcastReceiver finishBackgroundCall = new BroadcastReceiver() {
        @Override
        public void onReceive(Context contextt, Intent intent) {
            if (!ntf_Preferences.get(Prefs_Keys.PRIVILEGE_TRACK_PACKAGES).equalsIgnoreCase("f")) {
                ((MainActivity) (getActivity() != null ? getActivity() : context)).enableTransparentLayer();
                parentLayout.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
                NTF_Utils.showInSufficientPrivelegeAlert(getActivity() != null ? getActivity() : context);
                return;
            }
            parentLayout.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
            ((MainActivity) (getActivity() != null ? getActivity() : context)).disableTransparentLayer();
            setSettingsToAdapter(mAdapter, selectedPackagesAdapter);
            if (rvPackages.getTag().equals("1")) {
                selectedPackagesAdapter.notifyDataSetChanged();
            } else {
                mAdapter.notifyDataSetChanged();
            }
            setMailroomInRequest();
            onActivityResult(LOGOUTPKGREQUESTCODE, Activity.RESULT_OK, new Intent().putExtra(Extras_Keys.REFRESH_LOG_PACKAGE_OUT_LIST, true));
        }
    };


    @OnClick(R.id.buttonSignOutPkg)
    void buttonSignOutPkg() {
        if (!(NTF_Utils.getCurrentFragment(this) instanceof LogPackageOutFragment)) {
            return;
        }
        if (selectedPackages.size() == 0) {
            NTF_Utils.showAlert(getActivity(), "", "Please select a package.", null);
        } else {
            onKeyBoardStateChanged(false);
            PackageLogoutFragment packageLogoutFragment = PackageLogoutFragment.getInstance(selectedPackages);
            packageLogoutFragment.setTargetFragment(this, LOGOUTPKGREQUESTCODE);
            changeDetailsFragment(packageLogoutFragment);
        }
    }

    //to check all packages based on boolean value
    private void checkAllPkgs(boolean check) {
        selectedPackages.clear();
        if (check) {
            for (Package aPackage : mPackagelist) {
                aPackage.setIsChecked(check);
                selectedPackages.add(aPackage);
            }
        } else {
            for (Package aPackage : mPackagelist) {
                aPackage.setIsChecked(check);
            }
        }
        mAdapter.notifyDataSetChanged();
        updateSelectedPackagesCount();
    }

    private class BackPressedRecevicer extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getExtras().getString(NTF_Constants.LPOFTABCLICKEDFROM, "").equals("package_logout")) {
                    onActivityResult(LOGOUTPKGREQUESTCODE, Activity.RESULT_OK, new Intent().putExtra(Extras_Keys.REFRESH_LOG_PACKAGE_OUT_LIST, true));
                } else if (intent.getExtras().getString(NTF_Constants.LPOFTABCLICKEDFROM, "").equals("update_package")) {
                    onActivityResult(LOGOUTPKGREQUESTCODE, Activity.RESULT_OK, new Intent().putExtra(Extras_Keys.REFRESH_LOG_PACKAGE_OUT_LIST, true));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @OnClick(R.id.checkall)
    void onCheckAllClicked() {
        if (((String) rvPackages.getTag()).equals("1")) {
            checkall.setTag("0");
            checkAllPkgs(false);
            checkall.setVisibility(View.GONE);
            rvPackages.setVisibility(View.GONE);
            noSelectedPackagesTV.setVisibility(View.VISIBLE);
            selectedPackagesAdapter.notifyDataSetChanged();
        } else {
            if (checkall.getTag().toString().equals("0")) {
                checkall.setTag("1");
                checkAllPkgs(true);
            } else {
                checkall.setTag("0");
                checkAllPkgs(false);
            }
        }
    }

    @OnClick(R.id.sort_dialog_imageButton)
    void showSortDialogFragment() {
        if (!NTF_Utils.isOnline(getActivity())) {
            ((MainActivity) getActivity()).registerReceiver();
            NTF_Utils.showNoNetworkAlert(getActivity());
            return;
        }
        initializePreRequest();
        SortDialogFragment filterDialogFragment = new SortDialogFragment(ntf_Preferences, preRequest.getPendingPackagesPrimarySortColumn(), LogPackageOutFragment.this);
        filterDialogFragment.show(getFragmentManager(), "Sort Dialog Fragment");
    }

    @OnClick(R.id.searchClear)
    void clearSearch() {
        searchClear.setVisibility(View.GONE);
        editTextSearch.setText("");
    }

    @OnTextChanged(R.id.editTextSearch)
    protected void onTextChanged(CharSequence text) {
        if (!textchangedAccessgranted){
            return;
        }
        initializePreRequest();
        if (text.toString().trim().length() > 0) {
            searchClear.setVisibility(View.VISIBLE);
        } else {
            searchClear.setVisibility(View.GONE);
        }
        //to avoid empty spaces service hittng
        if (text.toString().trim().length() == 0 && text.toString().length() != text.toString().trim().length()) {
            return;
        }

        if (!NTF_Utils.isOnline(getActivity()) && Integer.valueOf(pendingPkgsCount.getText().toString().trim()) >= pendingPkgLimit) {
            NTF_Utils.showNoNetworkAlert(getActivity()!=null?getActivity():context);
            makeScreenBlank();
           return;
        }

        if (rvPackages.getTag().toString().equals("1")) {
            onPendingPackagesClicked();
        }
        //when search  string length not equal to one
        if (text.toString().trim().length() != 1) {
            editTextSearch.setTag(text.toString().trim());
            if (Integer.valueOf(pendingPkgsCount.getText().toString().trim()) < pendingPkgLimit && isResponseHandled) {
                doInternalSearch(text.toString().trim());
            } else {
                preRequest.setLimitCycle("0");
                sendSearchRequest();
            }
        } else {
            //when search  string length equal to one
            checkall.setTag("0");
            checkAllPkgs(false);
        }
    }

    //to do internal search
    private void doInternalSearch(String text) {
        if (mAdapter != null)
            mAdapter.filter(text);
    }

    //to rend request for search field
    private void sendSearchRequest() {
        handler.removeCallbacksAndMessages(null);
        packagesPresenter.cancelRequests();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                makeScreenBlank();
                mPackagelist.clear();
                selectedPackages.clear();
                updateSelectedPackagesCount();
                mAdapter.notifyDataSetChanged();
                if (!isProgressbarShown) {
                    footerViewHolder.showSpinner();
                }
                packagesPresenter.cancelRequests();
                getPendingPackages();
            }
        }, 1000);
    }

    //to avoid getActivity() null case
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = getActivity();
    }

    //to send pending packages with images api request for whole app
    private void getPendingPackages() {
        try {
            if (!NTF_Utils.isOnline(getActivity())) {
                ((MainActivity) getActivity()).registerReceiver();
                NTF_Utils.showNoNetworkAlert(getActivity());
                return;
            }
            initializePreRequest();
            preRequest.setSearchBy(editTextSearch.getTag().toString().trim());
            preRequest.setApiMode(SingleTon.getInstance().getPendingPackagesAPIMode());
            Log.d("APIACTION", preRequest.getApiMode());
            SingleTon.getInstance().setPendingPackagesAPIMode("general_action");
            packagesPresenter.getPendingPackages(null, preRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setMailroomInRequest() {
        if (preRequest == null){
            return;
        }
        boolean isMailRoomFound=false;
        ArrayList<SpinnerData> mailrooms=SingleTon.getInstance().getmMailRoomList(getActivity());
        for (SpinnerData mailroomData :mailrooms) {
            if (ntf_Preferences.get(Prefs_Keys.DEFAULT_MAILROOM_ID).equals(mailroomData.getValue())){
                isMailRoomFound=true;
            }
        }
        if (isMailRoomFound){
            preRequest.setMailroomId(ntf_Preferences.get(Prefs_Keys.DEFAULT_MAILROOM_ID));
        } else {
            preRequest.setMailroomId("all");
        }
    }

    private void initializePreRequest() {
        try {
            if (preRequest == null) {
                preRequest = new PendingPackagesRequest();
                preRequest.setSessionId(ntf_Preferences.get(Prefs_Keys.SESSION_ID));
                preRequest.setUserId(ntf_Preferences.get(Prefs_Keys.USER_ID));
                preRequest.setAuthenticationToken(ntf_Preferences.get(Prefs_Keys.AUTHENTICATION_TOKEN));
                preRequest.setAccountId(ntf_Preferences.get(Prefs_Keys.ACCOUNT_ID));
                preRequest.setAccountType(ntf_Preferences.get(Prefs_Keys.ACCOUNT_TYPE));
                preRequest.setLimitCycle("0");
                setMailroomInRequest();
                if (ntf_Preferences.get(Prefs_Keys.PENDING_PACKAGES_PRIMARY_SORT_COLUMN).isEmpty()){
                    NTF_Utils.doSaveAccountSettings(ntf_Preferences,NTF_Utils.getGlobalData(getActivity()).optJSONArray("account_settings").optJSONObject(0));
                }
                preRequest.setPendingPackagesPrimarySortColumn(ntf_Preferences.get(Prefs_Keys.PENDING_PACKAGES_PRIMARY_SORT_COLUMN));
                preRequest.setPendingPackagesPrimarySortOrder(ntf_Preferences.get(Prefs_Keys.PENDING_PACKAGES_PRIMARY_SORT_ORDER));
                preRequest.setPendingPackagesSecondarySortColumn(ntf_Preferences.get(Prefs_Keys.PENDING_PACKAGES_SECONDARY_SORT_COLUMN));
                preRequest.setPendingPackagesSecondarySortOrder(ntf_Preferences.get(Prefs_Keys.PENDING_PACKAGES_SECONDARY_SORT_ORDER));
                preRequest.setAppVersion(getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName);
                preRequest.setMailroomSpecificSorting("1");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // to send kiosk request
    @OnClick(R.id.backImage)
    void onKioskClick() {
        try {
            if (!NTF_Utils.isOnline(getActivity())) {
                ((MainActivity) getActivity()).registerReceiver();
                NTF_Utils.showNoNetworkAlert(getActivity());
                return;
            }
            if (SingleTon.getInstance().getmMailRoomList(getActivity()).size() == 1){
                ntf_Preferences.save(Prefs_Keys.SELECTED_MAILROOM, SingleTon.getInstance().getmMailRoomList(getActivity()).get(0).getValue());
                NTF_Utils.showProgressDialog(getActivity() != null ? getActivity() : context);
                ntf_Preferences.save(NTF_Constants.Prefs_Keys.IS_KIOSK_MODE, true);
                KioskLoginRequest request = new KioskLoginRequest();
                request.setSessionId(ntf_Preferences.get(NTF_Constants.Prefs_Keys.SESSION_ID));
                request.setAuthenticationToken(ntf_Preferences.get(NTF_Constants.Prefs_Keys.AUTHENTICATION_TOKEN));
                request.setAccountId(ntf_Preferences.get(NTF_Constants.Prefs_Keys.ACCOUNT_ID));
                request.setUserId(ntf_Preferences.get(NTF_Constants.Prefs_Keys.USER_ID));
                request.setDeviceUniqueId(NTF_Utils.getUUID(getActivity()));
                String header = NTF_Utils.getHeader(ntf_Preferences, getActivity()!=null?getActivity():context);
                kioskLoginPresenter.doKioskLogin(header, request,ntf_Preferences);
            } else {
                Intent identifyUserActivity = new Intent(getActivity(), SelectMailroomActivity.class);
                identifyUserActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(identifyUserActivity);
            }

        } catch (Exception e) {
            ntf_Preferences.save(NTF_Constants.Prefs_Keys.IS_KIOSK_MODE, false);
            e.printStackTrace();
        }
    }

    //to handle kiosk success case
    @Override
    public void onKioskLoginSuccess(KioskLoginResponse loginResponse) {
        try {
            ntf_Preferences.save(Prefs_Keys.KIOSK_DISPLAY_RECIPIENT_FORMAT, loginResponse.getKioskDisplayRecipientFormat());
            ntf_Preferences.save(Prefs_Keys.SESSION_ID, loginResponse.getSessionId());
            ntf_Preferences.save(Prefs_Keys.AUTHENTICATION_TOKEN, loginResponse.getAuthenticationToken());
            ntf_Preferences.save(Prefs_Keys.EXPIRATION, loginResponse.getExpiration());
            ntf_Preferences.save(Prefs_Keys.ACCOUNT_ID, loginResponse.getAccountId());
            ntf_Preferences.save(Prefs_Keys.USER_ID, loginResponse.getUserId());
            ntf_Preferences.save(Prefs_Keys.TIMEZONE, loginResponse.getTimezone());
            ntf_Preferences.save(Prefs_Keys.KIOSK_MODE_DISCLAIMER, loginResponse.getKioskModeDisclaimer());
            ntf_Preferences.save(Prefs_Keys.TIMEZONE_SHORTCODE, loginResponse.getTimezoneidShortcode());
            ntf_Preferences.save(Prefs_Keys.ACCOUNT_TYPE, loginResponse.getAccountType());
            ntf_Preferences.save(Prefs_Keys.USE_FRONT_CAMERA, loginResponse.getUseFrontCamera());
            Intent identifyUserActivity = new Intent(getActivity(), IdentifyUserActivity.class);
            identifyUserActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(identifyUserActivity);
            getActivity().finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //to handle session expire for both kiosk and pending package request
    @Override
    public void onSessionExpired(String message) {
        try {
            cancelAutoRefreshTimer();
            NTF_Utils.hideProgressDialog();
            NTF_Utils.showSessionExpireAlert(message, getActivity(), ntf_Preferences);
            footerViewHolder.hideSpinner();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //to handle server error
    @Override
    public void onServerError() {
        NTF_Utils.hideProgressDialog();
    }

    @Override
    public Context getMvpContext() {
        return getActivity();
    }

    // to handle error from server
    @Override
    public void onError(Throwable throwable) {
        try {
            NTF_Utils.hideProgressDialog();
            NTF_Utils.showAlert(getActivity(), "", NTF_Utils.getErrorMessage(throwable), null);
            footerViewHolder.hideSpinner();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //to handle no internet case
    @Override
    public void onNoInternetConnection() {
        try {
            NTF_Utils.hideProgressDialog();
            NTF_Utils.showNoNetworkAlert(getActivity());
            footerViewHolder.hideSpinner();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // to handle error case in api response in this screen
    @Override
    public void onErrorCode(String s) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(getActivity(), "", s, null);
    }

    //to set the dynamic view to list items
    private void setSettingsToAdapter(LogPackageOutAdapter adapter, SelectedPackagesAdapter adapter2) {
        if (adapter != null) {
            adapter.resetDynamicViews();
            adapter.setmIsShowAddress1(ntf_Preferences.get(Prefs_Keys.PKG_LOGOUT_ADDRESS1).equals("1"));
            adapter.setmIsShowTagNumber(ntf_Preferences.get(Prefs_Keys.PKG_LOGOUT_TAGNUMBER).equals("1"));
            adapter.setmIsShowPackageType(ntf_Preferences.get(Prefs_Keys.PKG_LOGOUT_PACKAGETYPE).equals("1"));
            adapter.setmIsShowShelf(ntf_Preferences.get(Prefs_Keys.PKG_LOGOUT_SHELF).equals("1"));
            adapter.setmIsShowSender(ntf_Preferences.get(Prefs_Keys.PKG_LOGOUT_SENDER).equals("1"));
            adapter.setmIsShowMailRoom(ntf_Preferences.get(Prefs_Keys.PKG_LOGOUT_MAILROOM).equals("1"));
            adapter.setFragment(LogPackageOutFragment.this);
        }
        if (adapter2 != null) {
            adapter2.resetDynamicViews();
            adapter2.setmIsShowAddress1(ntf_Preferences.get(Prefs_Keys.PKG_LOGOUT_ADDRESS1).equals("1"));
            adapter2.setmIsShowTagNumber(ntf_Preferences.get(Prefs_Keys.PKG_LOGOUT_TAGNUMBER).equals("1"));
            adapter2.setmIsShowPackageType(ntf_Preferences.get(Prefs_Keys.PKG_LOGOUT_PACKAGETYPE).equals("1"));
            adapter2.setmIsShowShelf(ntf_Preferences.get(Prefs_Keys.PKG_LOGOUT_SHELF).equals("1"));
            adapter2.setmIsShowSender(ntf_Preferences.get(Prefs_Keys.PKG_LOGOUT_SENDER).equals("1"));
            adapter2.setmIsShowMailRoom(ntf_Preferences.get(Prefs_Keys.PKG_LOGOUT_MAILROOM).equals("1"));
            adapter2.setFragment(LogPackageOutFragment.this);
        }
    }

    @OnClick(R.id.actionImageRight)
    void scanPkg() {
        if (!(NTF_Utils.getCurrentFragment(LogPackageOutFragment.this) instanceof LogPackageOutFragment)) {
            return;
        }
        NTF_Utils.checkCameraPermissionToProgress(getActivity(), new Runnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - mLastClickTime < 500) {
                    return;
                }
                mLastClickTime = System.currentTimeMillis();
                onKeyBoardStateChanged(false);
                ScanModeFragment fragment = new ScanModeFragment();
                fragment.setTargetFragment(LogPackageOutFragment.this, LOGOUTPKGREQUESTCODE);
                changeDetailsFragment(fragment);
            }
        });
    }

    //to handle the pending packages successs case
    @Override
    public void onPendingPackagesSuccess(JSONObject jsonObject) {
        if (preRequest == null) {
            return;
        }
        JSONArray jsonArray = jsonObject.optJSONArray("packages");
        if (editTextSearch.getTag().toString().length() > 0 &&
                (!preRequest.getLimitCycle().equals("0") || jsonArray.length() > 1)) {
            checkall.setVisibility(View.VISIBLE);
            if (preRequest.getLimitCycle().equals("0")) {
                checkall.setTag("0");
            }
        } else {
            checkall.setVisibility(View.GONE);
        }
        pendingPkgsCount.setText(String.valueOf(jsonObject.optInt("total_pending_packages")));
        footerViewHolder.hideSpinner();
        if (jsonArray.length() < jsonObject.optInt("pending_packages_limit")){
            isThereMorePackages = false;
        } else if (jsonArray.length() == jsonObject.optInt("pending_packages_limit")
        && ((Integer.valueOf(preRequest.getLimitCycle()) + 1) * jsonObject.optInt("pending_packages_limit"))>=jsonObject.optInt("total_pending_packages")){
            isThereMorePackages = false;
        } else {
            isThereMorePackages = true;
        }
        if (preRequest.getLimitCycle().equals("0")) {
            mPackagelist.clear();
            selectedPackages.clear();
        }
        mPackagelist.addAll(Package.getPackagesPending(jsonArray));
        updateSelectedPackagesCount();
        if (jsonObject.optInt("total_pending_packages") < jsonObject.optInt("pending_packages_limit")) {
            mAdapter.setOriginalList(mPackagelist);
            doInternalSort(preRequest.getPendingPackagesPrimarySortColumn(), getSortByName(preRequest.getPendingPackagesPrimarySortColumn()), true);
        }
        mAdapter.notifyDataSetChanged();
        NTF_Utils.hideProgressDialog();
        isProgressbarShown = false;
        if (jsonArray.length() == 0) {
            NTF_Utils.showAlert(getActivity(), "", "No packages available.", null);
        }
        if (rvPackages.getTag().toString().equals("1")) {
            onPendingPackagesClicked();
        }
        isResponseHandled = true;
        System.gc();
    }

    @Override
    public void onPendingPackagesFail(String message) {
        footerViewHolder.hideSpinner();
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showAlert(getActivity(), "", message, null);
        if (!preRequest.getLimitCycle().equals("0")){
            preRequest.setLimitCycle(String.valueOf(Integer.parseInt(preRequest.getLimitCycle()) - 1));
        }
    }

    //to handle the sort popup done button
    void onSortDoneClicked(String sortBy, String mailroomId, String sortByText) {
        if (preRequest != null){
            preRequest.setPendingPackagesSecondarySortColumn("");
            preRequest.setPendingPackagesSecondarySortOrder("");
        }
        if (preRequest.getMailroomId().equals(mailroomId) && Integer.valueOf(pendingPkgsCount.getText().toString().trim()) < 100 && isResponseHandled) {
            if (rvPackages.getTag().equals("1")) {
                onPendingPackagesClicked();
            }
            doInternalSort(sortBy, sortByText, false);
            return;
        }
        if (!NTF_Utils.isOnline(getActivity())) {
            ((MainActivity) getActivity()).registerReceiver();
            NTF_Utils.showNoNetworkAlert(getActivity());
            return;
        }
        if (!preRequest.getMailroomId().equals(mailroomId)){
            if (sortBy.equals(SortDialogFragment.DATE_RECEIVED_VALUE)) {
                preRequest.setPendingPackagesPrimarySortOrder("DESC");
            } else {
                preRequest.setPendingPackagesPrimarySortOrder("ASC");
            }
        } else {
            if (preRequest.getPendingPackagesPrimarySortColumn().equals(sortBy)) {
                preRequest.setPendingPackagesPrimarySortOrder(preRequest.getPendingPackagesPrimarySortOrder().equalsIgnoreCase("ASC") ? "DESC" : "ASC");
            } else {
                if (sortBy.equals(SortDialogFragment.DATE_RECEIVED_VALUE)) {
                    preRequest.setPendingPackagesPrimarySortOrder("DESC");
                } else {
                    preRequest.setPendingPackagesPrimarySortOrder("ASC");
                }
            }
        }
        preRequest.setPendingPackagesPrimarySortColumn(sortBy);
        preRequest.setLimitCycle("0");
        preRequest.setMailroomId(mailroomId);
        NTF_Utils.showProgressDialog(getActivity() != null ? getActivity() : context);
        if (preRequest.getMailroomId() != null && !preRequest.getMailroomId().isEmpty()) {
            ntf_Preferences.save(Prefs_Keys.DEFAULT_MAILROOM_ID, preRequest.getMailroomId());
        }
        makeScreenBlank();
        getPendingPackages();
    }

    //to handle the internal sorting
    private void doInternalSort(String sortByValue, String sortBy, boolean isFromSuccessResponse) {
        try {
            mPackagelist.clear();
            mPackagelist.addAll(mAdapter.getmOrinalList());
            for (Package wp : mPackagelist) {
                wp.setIsChecked(false);
                wp.setItemExpanded(false);
            }
            if (!isFromSuccessResponse) {
                if (preRequest.getPendingPackagesPrimarySortColumn().equals(sortByValue)) {
                    preRequest.setPendingPackagesPrimarySortOrder(preRequest.getPendingPackagesPrimarySortOrder().equals("ASC") ? "DESC" : "ASC");
                } else if (sortByValue.equals(SortDialogFragment.DATE_RECEIVED_VALUE)) {
                    preRequest.setPendingPackagesPrimarySortOrder("DESC");
                } else {
                    preRequest.setPendingPackagesPrimarySortOrder("ASC");
                }
            }

            preRequest.setPendingPackagesPrimarySortColumn(sortByValue);
            if (preRequest.getPendingPackagesPrimarySortOrder().equals("DESC") && sortBy.equalsIgnoreCase(NTF_Utils.getRecipientAddress1Label(ntf_Preferences.get(NTF_Constants.Prefs_Keys.ACCOUNT_TYPE)))) {
                Collections.sort(mPackagelist, new AlphaNueralSortDecending());
            } else if (sortBy.equalsIgnoreCase(NTF_Utils.getRecipientAddress1Label(ntf_Preferences.get(NTF_Constants.Prefs_Keys.ACCOUNT_TYPE)))) {
                Collections.sort(mPackagelist, new AlphaNueralSortAscending());
            } else if (preRequest.getPendingPackagesPrimarySortOrder().equals("DESC") && sortBy.equalsIgnoreCase(NTF_Utils.getRecipientTypeLabel(ntf_Preferences.get(Prefs_Keys.ACCOUNT_TYPE)) + " Name")) {
                Collections.sort(mPackagelist, new SortByNameDescending());
            } else if (sortBy.equalsIgnoreCase(NTF_Utils.getRecipientTypeLabel(ntf_Preferences.get(Prefs_Keys.ACCOUNT_TYPE)) + " Name")) {
                Collections.sort(mPackagelist, new SortByNameAscending());
            } else if (preRequest.getPendingPackagesPrimarySortOrder().equals("DESC") && sortBy.equalsIgnoreCase("Tracking Number")) {
                Collections.sort(mPackagelist, new TrackingDescending());
            } else if (sortBy.equalsIgnoreCase("Tracking Number")) {
                Collections.sort(mPackagelist, new TrackingAscending());
            } else if (preRequest.getPendingPackagesPrimarySortOrder().equals("DESC") && sortBy.equalsIgnoreCase("Tag Number")) {
                Collections.sort(mPackagelist, new SortByTagNumberDescending());
            } else if (sortBy.equalsIgnoreCase("Tag Number")) {
                Collections.sort(mPackagelist, new SortByTagNumberAscending());
            } else if (preRequest.getPendingPackagesPrimarySortOrder().equals("DESC") && sortBy.equalsIgnoreCase("Package Type")) {
                Collections.sort(mPackagelist, new SortByPkgTypeDescending());
            } else if (sortBy.equalsIgnoreCase("Package Type")) {
                Collections.sort(mPackagelist, new SortByPkgTypeAscending());
            } else if (preRequest.getPendingPackagesPrimarySortOrder().equals("DESC") && sortBy.equalsIgnoreCase("Shelf")) {
                Collections.sort(mPackagelist, new SortByShelfDescending());
            } else if (sortBy.equalsIgnoreCase("Shelf")) {
                Collections.sort(mPackagelist, new SortByShelfAscending());
            } else if (preRequest.getPendingPackagesPrimarySortOrder().equals("DESC") && sortBy.equalsIgnoreCase("Sender")) {
                Collections.sort(mPackagelist, new SortBySenderDescending());
            } else if (sortBy.equalsIgnoreCase("Sender")) {
                Collections.sort(mPackagelist, new SortBySenderAscending());
            } else if (preRequest.getPendingPackagesPrimarySortOrder().equals("DESC") && sortBy.equalsIgnoreCase("Mailroom")) {
                Collections.sort(mPackagelist, new SortByMainRoomDescending());
            } else if (sortBy.equalsIgnoreCase("Mailroom")) {
                Collections.sort(mPackagelist, new SortByMainRoomAscending());
            } else if (preRequest.getPendingPackagesPrimarySortOrder().equals("DESC") && sortBy.equalsIgnoreCase("Date Received")) {
                Collections.sort(mPackagelist, new SortByDateAscending());
            } else if (sortBy.equalsIgnoreCase("Date Received")) {
                Collections.sort(mPackagelist, new SortByDateDescending());
            }
            mAdapter.notifyDataSetChanged();
            mAdapter.setOriginalList(mPackagelist);
            mAdapter.filter(editTextSearch.getTag().toString().trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //to update selected packages list from adapter callback
    public void updateSelectedPackagesList(Package aPackage) {
        try {
            if (aPackage.isChecked()) {
                selectedPackages.add(aPackage);
            } else {
                selectedPackages.remove(aPackage);
            }
            if (((String) rvPackages.getTag()).equals("1") && selectedPackages.size() == 0) {
                rvPackages.setVisibility(View.GONE);
                noSelectedPackagesTV.setVisibility(View.VISIBLE);
            }
            updateSelectedPackagesCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCheckAllIcon() {
        if (selectedPackages.size() <= 1) {
            checkall.setVisibility(View.GONE);
        }
    }

    //to update selected packages count
    private void updateSelectedPackagesCount() {
        try {
            if (selectedPackages.size() == mPackagelist.size()) {
                checkall.setTag("1");
            } else if (selectedPackages.size() == 0) {
                checkall.setTag("0");
            }
            selectedPkgsCount.setText(String.valueOf(selectedPackages.size()));
            multipleEdit.setVisibility(selectedPackages.size() > 1 ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //to start auto refresh timer
    private void startAutoRefreshTimer() {
        cancelAutoRefreshTimer();
        timer = new Timer();
        initializeAutorefreshTimerTask();
        timer.schedule(timerTask, SCREEN_AUTO_REFRESH_INTERVAL, SCREEN_AUTO_REFRESH_INTERVAL);
    }

    //to stop unwanted auto refresh timer
    private void cancelAutoRefreshTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    //to initialize auto refreshtimer
    private void initializeAutorefreshTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.d("TIMER:", System.currentTimeMillis() + "");
                rvPackages.post(new Runnable() {
                    @Override
                    public void run() {
                        if (NTF_Utils.isOnline(getActivity() != null ? getActivity() : context)){
                            SingleTon.getInstance().setPendingPackagesAPIMode("background_action");
                            NTF_Utils.showProgressDialog(getActivity() != null ? getActivity() : context);
                            if (preRequest != null){
                                preRequest.setLimitCycle("0");
                            }
                            getPendingPackages();
                        }
                    }
                });
            }
        };
    }

    //internal search completed callback from adapter
    public void internalSearchCompleted(boolean show) {
        checkall.setVisibility(show ? View.VISIBLE : View.GONE);
        selectedPackages.clear();
        updateSelectedPackagesCount();
    }

    //to get the sortby Name belongs to respective sortby value
    private String getSortByName(String sortByValue) {
        if (sortByValue.equals("recipient_address1"))
            return NTF_Utils.getRecipientAddress1Label(ntf_Preferences.get(NTF_Constants.Prefs_Keys.ACCOUNT_TYPE));
        else if (sortByValue.equals("recipient_name"))
            return NTF_Utils.getRecipientTypeLabel(ntf_Preferences.get(NTF_Constants.Prefs_Keys.ACCOUNT_TYPE)) + " Name";
        else if (sortByValue.equals("tracking_number"))
            return "Tracking Number";
        else if (sortByValue.equals("tag_number"))
            return "Tag Number";
        else if (sortByValue.equals("package_type"))
            return "Package Type";
        else if (sortByValue.equals("shelf"))
            return "Shelf";
        else if (sortByValue.equals("sender"))
            return "Sender";
        else if (sortByValue.equals("mailroom_id"))
            return "Mailroom";
        else if (sortByValue.equals(SortDialogFragment.DATE_RECEIVED_VALUE))
            return "Date Received";
        return "recipient_address1";
    }
}
