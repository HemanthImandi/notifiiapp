/////////////////////////////////////////////////////////////////
// MainActivity.java
//
// Created by Annapoorna
// Notifii Project
//
//Copyright (c) 2016 Notifii, LLC. All rights reserved
/////////////////////////////////////////////////////////////////
package com.notifii.notifiiapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.base.NTF_BaseActivity;
import com.notifii.notifiiapp.customui.CustomFragmentTabHost;
import com.notifii.notifiiapp.fragments.CompleteRecipientListFragment;

import com.notifii.notifiiapp.fragments.LogPackageInFragment;
import com.notifii.notifiiapp.fragments.PackageLogoutFragment;
import com.notifii.notifiiapp.fragments.LogPackageOutFragment;
import com.notifii.notifiiapp.fragments.MoreFragment;
import com.notifii.notifiiapp.fragments.MyProfileFragment;
import com.notifii.notifiiapp.fragments.PackageDetailsFragment;
import com.notifii.notifiiapp.fragments.PackageHistoryFragment;

import com.notifii.notifiiapp.fragments.RecipientProfileViewFragment;
import com.notifii.notifiiapp.fragments.ScanModeFragment;
import com.notifii.notifiiapp.fragments.SearchPackageFragment;
import com.notifii.notifiiapp.fragments.UpdatePackageFragment;
import com.notifii.notifiiapp.helpers.SingleTon;
import com.notifii.notifiiapp.keyboard.KeyboardVisibilityEvent;
import com.notifii.notifiiapp.keyboard.KeyboardVisibilityEventListener;
import com.notifii.notifiiapp.receivers.ConnectionChangeReceiver;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;


//import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
//import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import static com.notifii.notifiiapp.utils.NTF_Constants.ResponseKeys.ERROR;
import static com.notifii.notifiiapp.utils.NTF_Constants.ResponseKeys.SESSION_EXPIRED;

/*
 * This is the Base class for All Tab Fragments...
 */

public class MainActivity extends NTF_BaseActivity {


    @BindView(R.id.tabhost)
    CustomFragmentTabHost mTabHost;
    @BindView(R.id.transparentLayerLL)
    LinearLayout transparentLayerLL;

    public static final String ACTION_SESSION_END = "session.end";
    private SessionEndreceiver sessionEndreceiver;
    public static final int TAB_0 = 0; // LogPackageIn Tab
    public static final int TAB_1 = 1; // LogPackageOut Tab
    public static final int TAB_2 = 2; // Search Tab
    public static final int TAB_3 = 3; //AccountTools Tab
    public static boolean isRecipietListToLoad = false;
    private final BroadcastReceiver mybroadcast = new ConnectionChangeReceiver();
    private boolean isReceiverRegistered = false;
    private long mLastClickTime;
    private boolean isTablet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        showDialog();
        isTablet = getResources().getBoolean(R.bool.isDeviceTablet);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("Log Package in").setIndicator(createTabView(R.drawable.ic_tab_log_package_in_selector, TAB_0)),
                LogPackageInFragment.class, null);

        mTabHost.addTab(mTabHost.newTabSpec("Log Package Out").setIndicator(createTabView(R.drawable.ic_tab_log_package_out_selector, TAB_1)),
                LogPackageOutFragment.class, null);

        mTabHost.addTab(mTabHost.newTabSpec("Search Package").setIndicator(createTabView(R.drawable.ic_tab_search_selector, TAB_2)),
                SearchPackageFragment.class, null);

        mTabHost.addTab(mTabHost.newTabSpec("Account Tools").setIndicator(createTabView(R.drawable.ic_tab_account_tools_selector, TAB_3)),
                MoreFragment.class, null);

        mTabHost.setCurrentTab(1); //default TAB pkg logout
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                //clearBackStack();
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                if (s.equals("Search Package")) {
                    SearchPackageFragment.needToResetSpinnerData = true;
                } else if (s.equals("Log Package in")) {
//                    LogPackageInFragment.needToResetSpinnerData = true;
                }
                try {
                    if (isTablet) {
                        ((LinearLayout) mTabHost.getTabWidget().getChildTabViewAt(0)).getChildAt(0).setContentDescription("Log Package In tab");
                        ((LinearLayout) mTabHost.getTabWidget().getChildTabViewAt(1)).getChildAt(0).setContentDescription("Log Package Out tab");
                        ((LinearLayout) mTabHost.getTabWidget().getChildTabViewAt(2)).getChildAt(0).setContentDescription("Search Package tab");
                        ((LinearLayout) mTabHost.getTabWidget().getChildTabViewAt(3)).getChildAt(0).setContentDescription("More tab");
                        for (int i = 0; i < 4; i++) {
                            if (i == mTabHost.getCurrentTab()) {
                                ((LinearLayout) mTabHost.getTabWidget().getChildTabViewAt(i)).getChildAt(0).setContentDescription("Selected " +
                                        ((LinearLayout) mTabHost.getTabWidget().getChildTabViewAt(i)).getChildAt(0).getContentDescription());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if (ntf_Preferences.hasKey(Prefs_Keys.RECIPIENT_NAME_ORDER)) {
            ntf_Preferences.save(Prefs_Keys.RECIPIENT_NAME_ORDER, "");
        }
        if (ntf_Preferences.hasKey(Prefs_Keys.UNIT_NUMBER_ORDER))
            ntf_Preferences.save(Prefs_Keys.UNIT_NUMBER_ORDER, "");
        if (ntf_Preferences.hasKey(Prefs_Keys.TRACKING_NUMBER_ORDER))
            ntf_Preferences.save(Prefs_Keys.TRACKING_NUMBER_ORDER, "");
        if (ntf_Preferences.hasKey(Prefs_Keys.TAG_NUMBER_ORDER))
            ntf_Preferences.save(Prefs_Keys.TAG_NUMBER_ORDER, "");
        if (ntf_Preferences.hasKey(Prefs_Keys.PACKAGE_TYPE_ORDER))
            ntf_Preferences.save(Prefs_Keys.PACKAGE_TYPE_ORDER, "");
        if (ntf_Preferences.hasKey(Prefs_Keys.SHELF_ORDER))
            ntf_Preferences.save(Prefs_Keys.SHELF_ORDER, "");
        if (ntf_Preferences.hasKey(Prefs_Keys.SENDER_ORDER))
            ntf_Preferences.save(Prefs_Keys.SENDER_ORDER, "");
        if (ntf_Preferences.hasKey(Prefs_Keys.DATE_RECIEVED_ORDER))
            ntf_Preferences.save(Prefs_Keys.DATE_RECIEVED_ORDER, "");
        if (ntf_Preferences.hasKey(Prefs_Keys.MAIL_ROOM_ORDER)) {
            ntf_Preferences.save(Prefs_Keys.MAIL_ROOM_ORDER, "");
        }
    }


    public void enableTransparentLayer() {
        transparentLayerLL.setVisibility(View.VISIBLE);
    }

    public void disableTransparentLayer() {
        transparentLayerLL.setVisibility(View.GONE);
    }

    @OnClick(R.id.transparentLayerLL)
    void ontransppenentLayerClicked() {

    }


    public void registerReceiver() {
        isReceiverRegistered = true;
        registerReceiver(mybroadcast, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public static class ViewHolder {
        @BindView(R.id.tab_icon)
        ImageView imageView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private View createTabView(final int id, final int tag) {
        View view = LayoutInflater.from(this).inflate(R.layout.tabs_icon, null);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.imageView.setImageDrawable(ActivityCompat.getDrawable(this, id));
        viewHolder.imageView.setTag(tag);
        if (tag == TAB_0) {
            viewHolder.imageView.setContentDescription("Log Package In tab");
        } else if (tag == TAB_1) {
            viewHolder.imageView.setContentDescription("Log Package Out tab");
        } else if (tag == TAB_2) {
            viewHolder.imageView.setContentDescription("Search Package tab");
        } else if (tag == TAB_3) {
            viewHolder.imageView.setContentDescription("More tab");
        }
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (System.currentTimeMillis() - mLastClickTime < 200) {
                        return;
                    }
                    mLastClickTime = System.currentTimeMillis();
                    int tag = (int) viewHolder.imageView.getTag();
                    mTabHost.setCurrentTab(tag);

                    if (tag == TAB_3) {
                        isRecipietListToLoad = false;
                        clearAllFrgaments();
                    } else if (tag == TAB_2) {
                        if (PackageHistoryFragment.isFromPkgHistory) {
                            NTF_Utils.hideKeyboard(MainActivity.this);
                            onBackPressed();
                        } else if (PackageDetailsFragment.isSearchPkgFragment) {
                            clearAllFrgaments();
                            PackageDetailsFragment.isSearchPkgFragment = false;
                        } else {
                            clearAllFrgaments();
                        }
                    } else if (tag == TAB_1) {
                        Fragment f = getSupportFragmentManager().findFragmentById(R.id.realtabcontent);
                        Intent intent = new Intent(NTF_Constants.LPOFTABCLICKED);
                        if (f instanceof PackageLogoutFragment) {
                            intent.putExtra(NTF_Constants.LPOFTABCLICKEDFROM, "package_logout");
                            sendBroadcast(intent);
                            clearAllFrgaments();
                            //same as logout
                        } else if (f instanceof UpdatePackageFragment) {
                            intent.putExtra(NTF_Constants.LPOFTABCLICKEDFROM, "update_package");
                            sendBroadcast(intent);
                            clearAllFrgaments();
                        }
                    } else if (tag == TAB_0) {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

    // Handling the functionalities when Back button is pressed
    @Override
    public void onBackPressed() {
        if (mTabHost.getCurrentTab() == 1) {
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.realtabcontent);
            if (f instanceof UpdatePackageFragment) {
                ((UpdatePackageFragment) f).handleBackButton();
            } else if (f instanceof PackageLogoutFragment) {
                ((PackageLogoutFragment) f).handleBackButton();
            } else if (f instanceof ScanModeFragment){
                ((ScanModeFragment) f).handleBackButton();
            }
            super.onBackPressed();
        } else if (mTabHost.getCurrentTab() == 3) {
            MainActivity.isRecipietListToLoad = true;
            super.onBackPressed();
            try {
                Fragment f = getSupportFragmentManager().findFragmentById(R.id.realtabcontent);
                if (f instanceof CompleteRecipientListFragment
                        && ntf_Preferences.getBoolean(Prefs_Keys.RECIPIENT_ADDED_OR_UPDATED)) {
                    ((CompleteRecipientListFragment) f).doRefreshList();
                    ntf_Preferences.save(Prefs_Keys.RECIPIENT_ADDED_OR_UPDATED, true);
                } else if (f instanceof RecipientProfileViewFragment) {
                    ((RecipientProfileViewFragment) f).getRecipient(MainActivity.this);
                } else if (f instanceof MyProfileFragment) {
                    ((MyProfileFragment) f).doPopulateDetails();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (mTabHost.getCurrentTab() == 2) {
            super.onBackPressed();
            try {
                Fragment f = getSupportFragmentManager().findFragmentById(R.id.realtabcontent);
                if (f instanceof RecipientProfileViewFragment) {
                    ((RecipientProfileViewFragment) f).getRecipient(MainActivity.this);
                    NTF_Utils.clearRecipientListPosition(MainActivity.this);
                } /*else if(f instanceof  SearchFragmentNew ){
                    ((SearchFragmentNew)f).clearFileds();
                }*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            super.onBackPressed();
    }

    // Clearing All Fragments from BackStack
    public void clearAllFrgaments() {
        try {
//            LogPackageOutFragment.isServiceRequ = true;
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE + WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        registerReciver();
        KeyboardVisibilityEvent.setEventListener(this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                try {
                    if (isOpen) {
                        mTabHost.getTabWidget().setVisibility(View.GONE);
                    } else {
                        mTabHost.post(new Runnable() {
                            @Override
                            public void run() {
                                mTabHost.getTabWidget().setVisibility(View.VISIBLE);
                            }
                        });
                    }
                    Fragment f = getSupportFragmentManager().findFragmentById(R.id.realtabcontent);
                    if (f instanceof LogPackageOutFragment) {
                        ((LogPackageOutFragment) f).onKeyBoardStateChanged(isOpen);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent(NTF_Constants.KEYBOARD_BROADCAST_RECEIVER_ACTION);
                    intent.putExtra(NTF_Constants.IS_KEBOARD_OPEN, isOpen);
                    LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
                }
            }
        });

    }


    private BroadcastReceiver startBackgroundCall = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NTF_Utils.showProgressDialog(MainActivity.this);
        }
    };

    private BroadcastReceiver finishBackgroundCall = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NTF_Utils.hideProgressDialog();
            NTF_Utils.handleBackgroundCallResponse(intent,MainActivity.this);
        }
    };


    @Override
    protected void onPause() {
        super.onPause();
        unRegisterReciever();
        if (this.isFinishing()) {
            NTF_Utils.hideProgressDialog();
            NTF_Utils.hideKeyboard(this);
        }
    }

    public void setToLogPackageOut() {
        mTabHost.setCurrentTab(1);
    }

    /*
     * This Broadcast receiver receive from @link LogOutSchedulingService.java when ever the session ends
     * and  app is in foreground*/
    class SessionEndreceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Log.v("Notifii Track :", "Session End Reciever");
                Intent loginActivityIntent = new Intent(MainActivity.this, LoginActivity.class);
                loginActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(loginActivityIntent);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * BroadCast Receiver for to send newly added business to favorites screen @link
     * onCreate()
     */
    private void registerReciver() {
        Log.v("Notifii Track :", "register Reciever");
        if (MainActivity.this != null) {
            IntentFilter ifilter_receiver = new IntentFilter(ACTION_SESSION_END);
            sessionEndreceiver = new SessionEndreceiver();
            registerReceiver(sessionEndreceiver, ifilter_receiver);
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(startBackgroundCall,
                new IntentFilter(Extras_Keys.LOCAL_BROADCAST_START_ACTION));
        LocalBroadcastManager.getInstance(this).registerReceiver(finishBackgroundCall,
                new IntentFilter(Extras_Keys.LOCAL_BROADCAST_END_ACTION));
    }

    /**
     * no longer live for receiver. on Register in @link onDestory()
     */
    private void unRegisterReciever() {
        try {
            Log.v("Notifii Track :", "unregister reciever");
            unregisterReceiver(sessionEndreceiver);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(startBackgroundCall);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(finishBackgroundCall);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (isReceiverRegistered) {
                isReceiverRegistered = false;
                unregisterReceiver(mybroadcast);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void showDialog() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(NTF_Constants.Extras_Keys.LOGIN_API_STATUS)) {
            String api_status = bundle.getString(NTF_Constants.Extras_Keys.LOGIN_API_STATUS, ResponseKeys.RESET_PASSWORD);
            startActivity(new Intent(MainActivity.this, UpdateUsernamePasswordActivity.class).putExtra(NTF_Constants.Extras_Keys.LOGIN_API_STATUS, api_status));
        }
    }

}
