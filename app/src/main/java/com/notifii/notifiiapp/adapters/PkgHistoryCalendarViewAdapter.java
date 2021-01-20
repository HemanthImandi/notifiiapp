/////////////////////////////////////////////////////////////////
// CalendarViewAdapter.java
//
// Created by Annapoorna
// Notifii Project
//
//Copyright (c) 2016 Notifii, LLC. All rights reserved
/////////////////////////////////////////////////////////////////
package com.notifii.notifiiapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.utils.NTF_Utils;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * This class deals with fetching the data to calendar
 */

public class PkgHistoryCalendarViewAdapter extends BaseAdapter {

    static final int FIRST_DAY_OF_WEEK = 0; // Sunday = 0, Monday = 1
    private Context mContext;
    private Calendar month;
    private ArrayList<String> items;
    private int firstDay;
    private String monthAString;
    public String[] days;
    public String mSelectedDate;
    private boolean check;

    public PkgHistoryCalendarViewAdapter(Context c, Calendar monthCalendar, String selectedPosition) {
        mContext = c;
        month = monthCalendar;
        month.set(Calendar.DAY_OF_MONTH, 1);
        this.items = new ArrayList<>();
        this.mSelectedDate = selectedPosition;
        refreshDays();
    }


    public void setItems(ArrayList<String> items) {
        for (int i = 0; i != items.size(); i++) {
            if (items.get(i).length() == 1) {
                items.set(i, "0" + items.get(i));
            }
        }
        this.items = items;
    }

    public int getCount() {
        return days.length;
    }

    public Object getItem(int position) {
        return days[position];
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new view for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
        LayoutInflater vi = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = vi.inflate(R.layout.calendar_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.dayView.setText(days[position]);
        String date = days[position];

        if (date.length() == 1) {
            date = "0" + date;
        }

        if (!check && mSelectedDate.equals(date)) {
            viewHolder.dayView.setTextColor(Color.parseColor("#9F705E"));
        } else {
            viewHolder.dayView.setTextColor(Color.parseColor("#477082"));
        }
        monthAString = "" + (month.get(Calendar.MONTH) + 1);
        if (monthAString.length() == 1) {
            monthAString = "0" + monthAString;
        }
        return convertView;
    }

    public static class ViewHolder {

        @BindView(R.id.date)
        TextView dayView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public void refreshDays() {
        // clear items
        items.clear();

        int lastDay = month.getActualMaximum(Calendar.DAY_OF_MONTH);
        firstDay = (int) month.get(Calendar.DAY_OF_WEEK);

        // figure size of the array
        if (firstDay == 1) {
            days = new String[lastDay + (FIRST_DAY_OF_WEEK * 6)];
        } else {
            days = new String[lastDay + firstDay - (FIRST_DAY_OF_WEEK + 1)];
        }

        int jP = FIRST_DAY_OF_WEEK;

        // populate empty days before first real day
        if (firstDay > 1) {
            for (j = 0; j < firstDay - FIRST_DAY_OF_WEEK; j++) {
                days[j] = "";
            }
        } else {
            for (j = 0; j < FIRST_DAY_OF_WEEK * 6; j++) {
                days[j] = "";
            }
            j = FIRST_DAY_OF_WEEK * 6 + 1; // sunday => 1, monday => 7
        }

        // populate days
        int dayNumber = 1;
        for (int i = j - 1; i < days.length; i++) {
            days[i] = "" + dayNumber;
            dayNumber++;
        }
        int monthPosition = month.get(Calendar.MONTH);
    }
}