/////////////////////////////////////////////////////////////////
// MatchingRecipientAdapter.java
//
// Created by Annapoorna
// Notifii Project
//
//Copyright (c) 2016 Notifii, LLC. All rights reserved
/////////////////////////////////////////////////////////////////
package com.notifii.notifiiapp.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.sorting.SortRecipientAddressAscending;
import com.notifii.notifiiapp.sorting.SortRecipientNameAscending;
import com.notifii.notifiiapp.models.Recipient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This class deals with the Fetching the data for Recipients List
 */
public class MatchingRecipientAdapter extends ArrayAdapter<Recipient> {

    private boolean isTablet;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            View view = mInflater.inflate(R.layout.list_item_matching_recipient_adapter, parent, false);
            vh = new ViewHolder((LinearLayout) view);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        Recipient item = getItem(position);
        if (currentFragment.equals("RecipientFragment")) {
            if(!item.getAddress1().equals("")) {
                vh.textViewSecondLable.setText(item.getAddress1());
            }else {
                vh.textViewSecondLable.setText("");
            }
            if (!item.getFirstName().isEmpty()) {
                vh.textViewFirstLable.setText(item.getFirstName() + " " + item.getLastName());
            } else {
                vh.textViewFirstLable.setText(item.getLastName());
            }
        } else if (currentFragment.equals("UnitFragment")) {
            if(!item.getAddress1().equals("")) {
                vh.textViewFirstLable.setText(item.getAddress1());
            } else {
                vh.textViewFirstLable.setText("");
            }
            if (!item.getFirstName().isEmpty()) {
                vh.textViewSecondLable.setText(item.getFirstName() + " " + item.getLastName());
            } else {
                vh.textViewSecondLable.setText(item.getLastName());
            }
        }
        // Setting the alternate background color for list
        if (!isTablet) {
            if (position % 2 == 0) {
                vh.rootView.setBackgroundResource(R.drawable.list_background_alternate);
            } else {
                vh.rootView.setBackgroundResource(R.drawable.list_backgroundcolor);
            }
        } else {
            if (position % 2 == 0) {
                vh.mainLinear.setBackgroundResource(R.drawable.bg_edittext);
            } else {
                vh.mainLinear.setBackgroundResource(R.drawable.bg_et);
            }
        }

        return vh.rootView;
    }

    private LayoutInflater mInflater;
    private String currentFragment;
    private ArrayList<Recipient> mRecipientList = new ArrayList<>();
    private ArrayList<Recipient> mOrinalList;

    // Constructors
    public MatchingRecipientAdapter(Activity context, List<Recipient> objects, String currentFragment) {
        super(context, 0, objects);
        this.mInflater = LayoutInflater.from(context);
        this.currentFragment = currentFragment;
        this.mRecipientList = (ArrayList<Recipient>) objects;
        this.mOrinalList = new ArrayList<>();
        this.mOrinalList.addAll(objects);
        isTablet = context.getResources().getBoolean(R.bool.isDeviceTablet);
    }

    // Filter Class
    public long filter(String charText) {
        boolean isName = false;
        charText = charText.toLowerCase(Locale.getDefault());
        mRecipientList.clear();
        if (charText.length() == 0) {
            mRecipientList.addAll(mOrinalList);
        } else {

            for (Recipient recipient : mOrinalList) {
                if (recipient.getFullName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    isName = true;
                    mRecipientList.add(recipient);
                } else if (recipient.getAddress1().toLowerCase(Locale.getDefault()).contains(charText)) {
                    isName = false;
                    mRecipientList.add(recipient);
                }
            }

            if (isName) {
                Collections.sort(mRecipientList, new SortRecipientNameAscending());
            } else {
                Collections.sort(mRecipientList, new SortRecipientAddressAscending());
            }
        }
        notifyDataSetChanged();
        return mRecipientList.size();
    }

    public static class ViewHolder {

        public LinearLayout rootView;
        @BindView(R.id.main_linear)
        public LinearLayout mainLinear;
        @BindView(R.id.firstLable)
        public TextView textViewFirstLable;
        @BindView(R.id.secondLable)
        public TextView textViewSecondLable;

        public ViewHolder(LinearLayout rootView) {
            this.rootView = rootView;
            ButterKnife.bind(this,rootView);
        }
    }
}
