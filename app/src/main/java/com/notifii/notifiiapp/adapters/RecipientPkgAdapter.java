/////////////////////////////////////////////////////////////////
// RecipientPkgAdapter.java
//
// Created by Annapoorna
// Notifii Project
//
//Copyright (c) 2016 Notifii, LLC. All rights reserved
/////////////////////////////////////////////////////////////////
package com.notifii.notifiiapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.models.Package;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * This class deals with the fetching the data to the RecipientPackage List...
 */
public class RecipientPkgAdapter extends ArrayAdapter<Package> implements NTF_Constants {

    private LayoutInflater mInflater;
    private Context context;
    private String timezone;

    // Constructors
    public RecipientPkgAdapter(Context context, List<Package> objects, String timezone) {
        super(context, 0, objects);
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.timezone = timezone;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            View view = mInflater.inflate(R.layout.list_item_recipient_profile_pkgs, parent, false);
            vh = new ViewHolder(view);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        Package item = getItem(position);

        // TODOBind your data to the views here
        vh.textViewTrackingNumber.setText(item.getTrackingNumber());
        // String dateReceived = item.getDateReceived() != null ? NTF_Utils.diaplayDateFormate.format( item.getDateReceived()) : "";
        Log.v("dateReceived: ", "" + NTF_Utils.getLocalDate_and_Time(item.getDatePickedup(), timezone));
        String dateReceived = item.getDateReceived() != null ? NTF_Utils.getLocalDate_and_Time(item.getDateReceived(), timezone) : "";
        if (dateReceived.isEmpty()) {
            vh.textViewDateReceived.setVisibility(View.INVISIBLE);
        } else {
            vh.textViewDateReceived.setVisibility(View.VISIBLE);
            vh.textViewDateReceived.setText(dateReceived);
        }


        // String datePickedUp = item.getDatePickedUp() != null ? NTF_Utils.diaplayDateFormate.format( item.getDatePickedUp()) : "";
        String datePickedUp = item.getDatePickedup() != null ? NTF_Utils.getLocalDate_and_Time(item.getDatePickedup(), timezone) : "";
        if (datePickedUp.isEmpty()) {
            vh.textViewDatePicked.setVisibility(View.INVISIBLE);
        } else {
            vh.textViewDatePicked.setVisibility(View.VISIBLE);
            vh.textViewDatePicked.setText(datePickedUp);
        }


        if (position % 2 == 0) {
            vh.rootView.setBackgroundResource(R.drawable.list_background_alternate);
        } else {
            vh.rootView.setBackgroundResource(R.drawable.list_backgroundcolor);
        }
        return vh.rootView;
    }

    public class ViewHolder {
        @BindView(R.id.parentLayout)
        public LinearLayout rootView;
        @BindView(R.id.tv_tracking_number)
        public TextView textViewTrackingNumber;
        @BindView(R.id.tv_received_date)
        public TextView textViewDateReceived;
        @BindView(R.id.tv_delivered_date)
        public TextView textViewDatePicked;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
