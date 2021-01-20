/////////////////////////////////////////////////////////////////
// NTF_BaseFragment.java
//
// Created by Annapoorna
// Notifii Project
//
//Copyright (c) 2016 Notifii, LLC. All rights reserved
/////////////////////////////////////////////////////////////////
package com.notifii.notifiiapp.base;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.helpers.NTF_PrefsManager;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * This is the base Fragment for all fragments used in this project.
 */
public class NTF_BaseFragment extends Fragment implements NTF_Constants {

    public NTF_PrefsManager ntf_Preferences;
    public ImageButton tabActionLeftImageBtn;
    public View mainView;
    public long mLastClickTime;
    @BindView(R.id.mobile_left_linear)
    LinearLayout mobileLeftLinear;
    @BindView(R.id.backImage)
    ImageView backImage;
    @BindView(R.id.textViewActionTitle)
    public TextView mTextViewActionTitle;
    @BindView(R.id.actionImageRight)
    ImageView actionImageRight;
    @BindView(R.id.buttonActionRight)
    Button actionButtonRight;
    @BindView(R.id.iv_app_icon)
    ImageView ivAppIcon;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ntf_Preferences = new NTF_PrefsManager(getActivity());
        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                onStackChanged();
            }
        });
    }

    public void onStackChanged() {
    }

    public void changeDetailsFragment(Fragment fragment) {//Class<?> clss
        if(!NTF_Utils.isOnline(getActivity())) {
            NTF_Utils.showNoNetworkAlert(getActivity());
            return;
        }
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.add(R.id.realtabcontent, fragment);
        transaction.commitAllowingStateLoss();
        getActivity().getSupportFragmentManager().executePendingTransactions();
    }


    //to handle the accessibility
    public void onFragmentPopup(boolean isPopUp) {
        if (mainView == null) {
            return;
        }
        if (isPopUp) {
            mainView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mainView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
            } else {
                mainView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
            }
        }
    }

    /*Common method for Displaying Toast Message in all fragments*/
    public void showToast(final String msg) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    public void setAppIcon(boolean isVisible) {
        if (isVisible) {
            ivAppIcon.setVisibility(View.VISIBLE);
        } else {
            ivAppIcon.setVisibility(View.GONE);
        }
    }

    public void setToolbarTitle(String title) {
        if (mTextViewActionTitle != null)
            mTextViewActionTitle.setText(title);
    }

    public void setRightButtonContent(String content) {
        if (actionImageRight != null) {
            actionImageRight.setContentDescription(content);
        }
    }

    public void setKioskContent() {
        if (backImage != null) {
            backImage.setContentDescription("kiosk login button");
        }
    }

    public void setToolbarActionButtons(boolean shouldShowBackImage, boolean shouldShowRightActionImage, int rightActionImageDrawable) {
        mobileLeftLinear.setVisibility(View.VISIBLE);
        if (backImage != null) {
            backImage.setVisibility(shouldShowBackImage ? View.VISIBLE : View.GONE);
        }

        if (actionImageRight != null) {
            actionImageRight.setVisibility(shouldShowRightActionImage ? View.VISIBLE : View.GONE);
            if (shouldShowRightActionImage && rightActionImageDrawable != 0) {
                actionImageRight.setImageResource(rightActionImageDrawable);
            }
        }
    }

    public void changeBackBTNIcon(int id) {
        backImage.setImageDrawable(getActivity().getResources().getDrawable(id));
        backImage.setVisibility(View.VISIBLE);
        ivAppIcon.setVisibility(View.GONE);
        backImage.setBackgroundColor(Color.TRANSPARENT);
        backImage.setContentDescription("kiosk login");
    }


    /*Setting the toolbar Background color for Tablets*/
    public void setLogo(int drawable) {
        if (mTextViewActionTitle != null) {
            mTextViewActionTitle.setBackgroundResource(drawable);
        }
    }
}
