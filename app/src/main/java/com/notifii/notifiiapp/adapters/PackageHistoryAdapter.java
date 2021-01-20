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
import com.notifii.notifiiapp.utils.NTF_Utils;
import com.notifii.notifiiapp.models.Package;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 8/4/2016.
 */
public class PackageHistoryAdapter extends ArrayAdapter<Package> {

    public   class ViewHolder {

        @BindView(R.id.parentLayout)
        public LinearLayout parentLayout;
        @BindView(R.id.main_linear)
        public LinearLayout mainLinear;
        @BindView(R.id.tv_name)
        public TextView tvName;
        @BindView(R.id.tv_address1)
        public TextView tvAddress1;
        @BindView(R.id.tv_tracking_number)
        public TextView tvTrackingNumber;
        @BindView(R.id.tv_received_date)
        public TextView tvReceivedDate;
        @BindView(R.id.tv_delivered_date)
        public TextView tvDeliveredDate;

        private ViewHolder(View view) {
            ButterKnife.bind(this,view);
        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            View view = mInflater.inflate(R.layout.list_item_package_history, parent, false);

            vh = new ViewHolder(view);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        Package item = getItem(position);

        vh.tvName.setText(item.getRecipientName().equalsIgnoreCase("null") ? "" : item.getRecipientName());
        vh.tvAddress1.setText(item.getRecipientAddress1().equalsIgnoreCase("null") ? "" : item.getRecipientAddress1());
        vh.tvTrackingNumber.setText("Tracking #: " + (item.getTrackingNumber().equalsIgnoreCase("null") ? "" : item.getTrackingNumber()));
        vh.tvReceivedDate.setText(item.getDateReceived() != null ? NTF_Utils.getLocalDate_and_Time(item.getDateReceived(), timeZone) : "");
        vh.tvDeliveredDate.setText(item.getDatePickedup() != null ? NTF_Utils.getLocalDate_and_Time(item.getDatePickedup(), timeZone) : "");

        if (vh.tvReceivedDate.getText().toString().isEmpty()) {
            vh.tvReceivedDate.setVisibility(View.GONE);
        } else {
            vh.tvReceivedDate.setVisibility(View.VISIBLE);
        }

        if (vh.tvDeliveredDate.getText().toString().isEmpty()) {
            vh.tvDeliveredDate.setVisibility(View.GONE);
        } else {
            vh.tvDeliveredDate.setVisibility(View.VISIBLE);
        }

        if (!isTablet) {
            if (position % 2 == 0) {
                vh.parentLayout.setBackgroundResource(R.drawable.list_background_alternate);
            } else {
                vh.parentLayout.setBackgroundResource(R.drawable.list_backgroundcolor);
            }
        } else {
            if (position % 2 == 0) {
                vh.mainLinear.setBackgroundResource(R.drawable.bg_edittext);
            } else {
                vh.mainLinear.setBackgroundResource(R.drawable.bg_et);
            }
        }
        return vh.parentLayout;
    }

    public void update(List<Package> data) {
        this.packagesList = data;
        this.mOriginalList = new ArrayList<>();
        this.mOriginalList.addAll(data);
        Log.v("PackageList: ", "data: " + mOriginalList.size());
        notifyDataSetChanged();
    }

    private LayoutInflater mInflater;
    private List<Package> packagesList;
    private List<Package> mOriginalList;
    private String currentFragment;
    private boolean isTablet;
    private Context context;
    private String timeZone;

    // Constructors
    public PackageHistoryAdapter(Context context, List<Package> objects, String currentFragment, String timeZone) {
        super(context, 0, objects);
        this.mInflater = LayoutInflater.from(context);
        packagesList = objects;
        this.mOriginalList = new ArrayList<>();
        this.mOriginalList.addAll(objects);
        this.currentFragment = currentFragment;
        this.context = context;
        isTablet = context.getResources().getBoolean(R.bool.isDeviceTablet);
        this.timeZone = timeZone;

    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        packagesList.clear();
        if (charText.length() == 0) {
            packagesList.addAll(mOriginalList);
        } else {
            Log.v("PackageList: ", "sizeB: " + mOriginalList.size());
            for (Package pkg : mOriginalList) {
                if (pkg.getRecipientName().toLowerCase(Locale.getDefault()).contains(charText) ||
                        pkg.getRecipientAddress1().toLowerCase(Locale.getDefault()).contains(charText) ||
                        pkg.getTrackingNumber().toLowerCase(Locale.getDefault()).contains(charText)) {
                    packagesList.add(pkg);
                }
            }
        }
        Log.v("PackageList: ", "sizeA: " + packagesList.size());
        notifyDataSetChanged();
    }
}

