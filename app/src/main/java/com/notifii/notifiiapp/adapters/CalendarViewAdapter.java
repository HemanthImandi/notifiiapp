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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.utils.NTF_Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/*
 * This class deals with fetching the data to calendar
 */

public class CalendarViewAdapter extends BaseAdapter {

    static final int FIRST_DAY_OF_WEEK = 0; // Sunday = 0, Monday = 1
    private String todayDate;
    private String startDateText = "";
    private Spinner mYearSpinner;
    private Spinner mMonthSpinner;
    private boolean mStartDateIsSelected;
    private int mButtonId;
    private Calendar calendar;
    private boolean highlightCurrentDate;
    private boolean disablePreviousDates;
    private boolean highlightSelectedDate;
    private Context mContext;
    private Calendar month;
    private ArrayList<String> items;
    private int firstDay;
    private String monthAString;
    public String[] days;
    public String mSelectedDate;
    private boolean check;


    public CalendarViewAdapter(Context c, Calendar updatedCalender, String selectedPosition, boolean showHighlightText,
                               boolean disablePreviousDates, boolean highlightCurrentDate, boolean startDateSelected,
                               int buttonId, Spinner mMonthSpinner, Spinner mYearSpinner, String startDateText) {
        mContext = c;
        month = updatedCalender;
        this.mMonthSpinner = mMonthSpinner;
        this.mYearSpinner = mYearSpinner;
        this.startDateText = startDateText;
        month.set(Calendar.DAY_OF_MONTH, 1);
        this.items = new ArrayList<>();
        this.mSelectedDate = selectedPosition;
        this.highlightSelectedDate = showHighlightText;
        this.disablePreviousDates = disablePreviousDates;
        this.highlightCurrentDate = highlightCurrentDate;
        calendar = Calendar.getInstance();
        todayDate = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
        mStartDateIsSelected = startDateSelected;
        mButtonId = buttonId;
        refreshDays();
    }

    public CalendarViewAdapter(FragmentActivity activity, String selectedPosition) {
        this.mContext = activity;
        mSelectedDate = selectedPosition;
    }

    public CalendarViewAdapter(FragmentActivity activity, Calendar mMonth, String selectedPosition, boolean spinnerCheck) {
        mContext = activity;
        month = mMonth;
        month.set(Calendar.DAY_OF_MONTH, 1);
        this.items = new ArrayList<>();
        this.mSelectedDate = selectedPosition;
        this.check = spinnerCheck;
        Log.v("check", "" + check);
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
        View v = convertView;
        TextView dayView;
        LinearLayout parentLayout = null;
        if (convertView == null) { // if it's not recycled, initialize some
            // attributes
            LayoutInflater vi = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.calendar_item, null);

        }
        dayView = (TextView) v.findViewById(R.id.date);
        parentLayout = (LinearLayout) v.findViewById(R.id.parentLayout);

        dayView.setText(days[position]);
        String date = days[position];

        String calendarDate = mYearSpinner.getSelectedItem() + "-" + (mMonthSpinner.getSelectedItemPosition() + 1) + "-" + date;

        if (!disablePreviousDates && !date.isEmpty()) {
            if (highlightSelectedDate && mStartDateIsSelected && highlightCurrentDate) {
                if (mButtonId == 1) {
                    if (mMonthSpinner != null && mYearSpinner != null) {
                        if (startDateText.equalsIgnoreCase(calendarDate)) {
                            parentLayout.setBackgroundColor(ActivityCompat.getColor(mContext, R.color.button_bg));
                            dayView.setTextColor(ActivityCompat.getColor(mContext, R.color.white));
                        } else if (calendarDate.equalsIgnoreCase(todayDate)) {
                            dayView.setTextColor(ActivityCompat.getColor(mContext, R.color.red));
                            if (parentLayout != null) {
                                parentLayout.setBackgroundColor(Color.parseColor("#EBEBEB"));
                            }
                        } else {
                            dayView.setTextColor(ActivityCompat.getColor(mContext, R.color.black));
                            parentLayout.setBackgroundColor(Color.parseColor("#EBEBEB"));
                        }
                    }
                }
            } else if (highlightCurrentDate) {
                if (todayDate.equalsIgnoreCase(calendarDate)) {
                    dayView.setTextColor(ActivityCompat.getColor(mContext, R.color.red));
                    if (parentLayout != null) {
                        parentLayout.setBackgroundColor(Color.parseColor("#EBEBEB"));
                    }
                } else {
                    dayView.setTextColor(ActivityCompat.getColor(mContext, R.color.black));
                    if (parentLayout != null) {
                        parentLayout.setBackgroundColor(Color.parseColor("#EBEBEB"));
                    }
                }
            }
        }

        if (disablePreviousDates) {
            if (todayDate.equalsIgnoreCase(calendarDate)) {
                dayView.setTextColor(ActivityCompat.getColor(mContext, R.color.red));
                dayView.setEnabled(false);
                if (parentLayout != null) {
                    parentLayout.setBackgroundColor(Color.parseColor("#EBEBEB"));
                }
            } else {
                try {
                    if (!date.isEmpty()) {
                        Date calendarDateFormat = new SimpleDateFormat("yyyy-MM-d").parse(calendarDate);
                        String startDateString = startDateText.isEmpty() ? new SimpleDateFormat("yyyy-MM-d").format(Calendar.getInstance().getTime()) : startDateText;
                        Date startDateFormat = new SimpleDateFormat("yyyy-MM-d").parse(startDateString);
                        Date todayDateFormat = new SimpleDateFormat("yyyy-MM-d").parse(todayDate);
                        int compareCalendarStartDate = calendarDateFormat.compareTo(startDateFormat);
                        int compareStartTodayDate = startDateFormat.compareTo(todayDateFormat);
                        int compareCalendarTodayDate = calendarDateFormat.compareTo(todayDateFormat);
                        if (compareCalendarTodayDate < 0) {
                            if (compareStartTodayDate > 0) {
                                if (compareCalendarStartDate <= 0) {
                                    dayView.setTextColor(ActivityCompat.getColor(mContext, R.color.marketing_color));
                                    dayView.setEnabled(false);
                                } else {
                                    dayView.setTextColor(ActivityCompat.getColor(mContext, R.color.black));
                                    dayView.setEnabled(true);
                                }
                            } else {
                                dayView.setTextColor(ActivityCompat.getColor(mContext, R.color.marketing_color));
                                dayView.setEnabled(false);
                            }
                        }else {
                            if (compareCalendarStartDate <= 0) {
                                dayView.setTextColor(ActivityCompat.getColor(mContext, R.color.marketing_color));
                                dayView.setEnabled(false);
                            } else {
                                dayView.setTextColor(ActivityCompat.getColor(mContext, R.color.black));
                                dayView.setEnabled(true);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        monthAString = "" + (month.get(Calendar.MONTH) + 1);
        if (monthAString.length() == 1)

        {
            monthAString = "0" + monthAString;
        }
        return v;
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

        int j = FIRST_DAY_OF_WEEK;

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