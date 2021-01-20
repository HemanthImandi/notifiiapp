package com.notifii.notifiiapp.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.adapters.PkgHistoryCalendarViewAdapter;
import com.notifii.notifiiapp.adapters.SpinnerHintAdapter;
import com.notifii.notifiiapp.adapters.SpinnerMarkAdapter;
import com.notifii.notifiiapp.models.SpinnerData;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnItemSelected;

/**
 * Created by Administrator on 8/4/2016.
 */
public class FilterDialogFragment extends DialogFragment {

    @BindView(R.id.iv_close)
    public ImageView ivClose;
    @BindView(R.id.spinner_pkgs_type)
    public Spinner spinnerPkgsType;
    @BindView(R.id.filterResultsButtonDownArrow)
    public ImageView filterResultsButtonDownArrow;
    @BindView(R.id.tv_from_date)
    public TextView tvFromDate;
    @BindView(R.id.tv_to_date)
    public TextView tvToDate;
    @BindView(R.id.spinner_filter)
    public Spinner spinnerFilter;
    @BindView(R.id.listAllPkgsButtonDownArrow)
    public ImageView listAllPkgsButtonDownArrow;
    @BindView(R.id.mail_room_linear_layout)
    public LinearLayout mailRoomLinearLayout;
    @BindView(R.id.mail_room_spinner)
    public Spinner mMailRoomSpinner;
    @BindView(R.id.btn_update)
    public Button mBtnUpdate;
    @BindView(R.id.tv_filter)
    public TextView tvFilter;
    @BindView(R.id.tv_mail_room)
    public TextView tv_mail_room;
    @BindView(R.id.tv_pkg_type)
    public TextView tv_pkg_type;

    Spinner mMonthSpinner = null;
    Spinner mYearSpinner = null;
    GridView mgridView = null;
    TextView mTextViewPrevious = null;
    TextView textViewMonth = null;
    FrameLayout startDateFramelayout, endDateFarmeLayout = null;
    TextView textViewYear = null;
    TextView mTextViewNext = null;
    private PackageHistoryFragment packageHistoryFragment;
    private Dialog dialog;
    private ArrayList<SpinnerData> mFilterByList = new ArrayList<>();
    private ArrayList<SpinnerData> mShowPackagesList = new ArrayList<>();
    private Dialog mCalendarDialog;
    private int mButtonId;
    private Handler handler;
    public Calendar mCurrentCalendar;
    private ArrayList<String> yearList = new ArrayList<>();
    private String[] mYearList;
    private String mDay;
    private PkgHistoryCalendarViewAdapter mCalendarAdapter;
    private Integer mMonth;
    private Integer mYear;
    private Calendar mPreviousCalendar;
    private Calendar mNextCalendar;
    private String mStartDate;
    private SimpleDateFormat date;
    private String mEndDate;
    private ArrayList<SpinnerData> mMailRoomList;
    private boolean isTablet = false;
    private  Calendar temp ;
    private String[] mMonthList;

    @SuppressLint("ValidFragment")
    public FilterDialogFragment(PackageHistoryFragment packageHistoryFragment) {
        this.packageHistoryFragment = packageHistoryFragment;
    }

    public FilterDialogFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isTablet = getResources().getBoolean(R.bool.isDeviceTablet);
        View view = inflater.inflate(R.layout.pop_up_pkg_history, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.dialog = getDialog();
        temp = Calendar.getInstance();
        mMonthList = getResources().getStringArray(R.array.month);;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mCurrentCalendar = Calendar.getInstance();
        mPreviousCalendar = Calendar.getInstance();
        mNextCalendar = Calendar.getInstance();
        items = new ArrayList<>();
        mFilterByList = SpinnerData.getFilterList(getActivity());
        SpinnerMarkAdapter  filterListAdapter = new SpinnerMarkAdapter(getActivity(), mFilterByList);
        spinnerFilter.setAdapter(filterListAdapter);
        spinnerFilter.setSelection(0);
        mShowPackagesList = SpinnerData.getPackageTypes(getActivity());
        SpinnerMarkAdapter packageListAdapter = new SpinnerMarkAdapter(getActivity(), mShowPackagesList);
        spinnerPkgsType.setAdapter(packageListAdapter);
        spinnerPkgsType.setSelection(0);

        setMailRoomData();
        if (mButtonId == 0) {
            getMethod();
        }
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
    }


    @OnClick(R.id.tv_from_date)
    public void onClickFromDate() {
        mButtonId = 1;
        showCalendarDialog();
    }

    @OnClick(R.id.tv_to_date)
    public void onClickEndDate() {
        mButtonId = 2;
        showCalendarDialog();
    }


    @OnClick(R.id.iv_close)
    public void onClickClose() {
        dialog.dismiss();
    }

    @OnClick(R.id.btn_update)
    public void onClickUpdate() {
        doUpdate();
        dialog.dismiss();
    }

    @OnClick(R.id.filterResultsButtonDownArrow)
    public void onClickFilterResultDownArrow() {
        spinnerPkgsType.performClick();
    }

    @OnClick(R.id.listAllPkgsButtonDownArrow)
    public void onClickListallPkgsButtonDownArrow() {
        spinnerFilter.performClick();
    }

    @OnItemSelected(R.id.spinner_pkgs_type)
    public void onItemPkgTypeSelected(int position){
        if (spinnerPkgsType.getSelectedItem() != null) {
            SpinnerData.resetData(mShowPackagesList);
            ((SpinnerData) spinnerPkgsType.getSelectedItem()).setSelected(true);
            tv_pkg_type.setText(((SpinnerData) spinnerPkgsType.getSelectedItem()).getName());
        }
    }

    @OnClick(R.id.tv_filter)
    void performFilterSpinnerClick(){
        spinnerFilter.performClick();
    }

    @OnClick(R.id.tv_mail_room)
    void performMailRoomSpinnerClick(){
        mMailRoomSpinner.performClick();
    }

    @OnClick(R.id.tv_pkg_type)
    void performPackageSpinnerClick(){
        spinnerPkgsType.performClick();
    }

    @OnItemSelected(R.id.spinner_filter)
    public void onItemFilterSelected(int position){
        if (spinnerFilter.getSelectedItem() != null) {
            SpinnerData.resetData(mFilterByList);
            ((SpinnerData) spinnerFilter.getSelectedItem()).setSelected(true);
            tvFilter.setText(((SpinnerData) spinnerFilter.getSelectedItem()).getName());
        }
    }

    @OnItemSelected(R.id.mail_room_spinner)
    public void onItemMailRoomSelected(int position){
        if (mMailRoomSpinner.getSelectedItem() != null) {
            SpinnerData.resetData(mMailRoomList);
            ((SpinnerData) mMailRoomSpinner.getSelectedItem()).setSelected(true);
            tv_mail_room.setText(((SpinnerData) mMailRoomSpinner.getSelectedItem()).getName());
        }
    }

    private void doUpdate() {
        String mailRoomId = "all";
        if (mMailRoomList != null && mMailRoomList.size() > 1) {
            mailRoomId = ((SpinnerData) mMailRoomSpinner.getSelectedItem()).getValue();
            Log.v("SelectedMailRoom: ", "" + ((SpinnerData) mMailRoomSpinner.getSelectedItem()).getName());
        } else if (mMailRoomList != null && mMailRoomList.size() == 1) {
            mailRoomId = mMailRoomList.get(0).getValue();
        }
        String strFilterBy = "all";
        if (spinnerFilter.getSelectedItemPosition() == 1) {
            strFilterBy = "picked_up";
        } else if (spinnerFilter.getSelectedItemPosition() == 2) {
            strFilterBy = "not_picked_up";
        }
        String startDate = tvFromDate.getText().toString();
        startDate = startDate.replaceAll("/", "-");
        String endDate = tvToDate.getText().toString();
        String mEndDate = endDate.replaceAll("/", "-");
        String showPkgs = "received";
        if (spinnerPkgsType.getSelectedItemPosition() == 1) {
            showPkgs = "picked_up";
        }
        if (packageHistoryFragment != null) {
            packageHistoryFragment.packageHistoryRequest(showPkgs, mailRoomId, startDate, mEndDate, strFilterBy);
        }
    }

    // Displaying the Calender Dialog...
    private void showCalendarDialog() {
        mCalendarDialog = new Dialog(getActivity()/*,R.style.MyTheme_MySpinnerStyle*/);
        mCalendarDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mCalendarDialog.setContentView(R.layout.calendar);
        mCalendarDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams wmlp = mCalendarDialog.getWindow().getAttributes();
        mCalendarDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        CalendarDialogViewHolder vi = new CalendarDialogViewHolder(mCalendarDialog);
        mMonthSpinner = vi.mMonthSpinner;
        mYearSpinner = vi.mYearSpinner;
        mgridView = vi.mgridView;
        mTextViewPrevious = vi.mTextViewPrevious;
        textViewMonth = vi.textViewMonth;
        startDateFramelayout = vi.startDateFramelayout;
        textViewYear = vi.textViewYear;
        endDateFarmeLayout = vi.endDateFl;
        mTextViewNext = vi.mTextViewNext;
        showCalendarMethod();
        wmlp.gravity = Gravity.TOP | Gravity.LEFT;
        int location1[] = new int[2];
        tvFromDate.getLocationOnScreen(location1);   //x position
        wmlp.x = location1[0];
        wmlp.y = location1[1];   //y position
        int width = getResources().getDisplayMetrics().widthPixels;
        if (mCalendarDialog == null || mCalendarDialog.getWindow() == null) {
            return;
        }
        int dialogWidth = (int) (width * 0.85f);
        if (getResources().getBoolean(R.bool.isDeviceTablet)) {
            dialogWidth = (int) (width * 0.80f);
        }
        int dialogHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        mCalendarDialog.getWindow().setLayout(dialogWidth, dialogHeight);
        mCalendarDialog.getWindow().setAttributes(wmlp);
        mCalendarDialog.show();
    }

    // This method performs the calendar functionality...
    private void showCalendarMethod() {
        final String[] mMonthList = getResources().getStringArray(R.array.month);
        String mCurrentMonth = mMonthList[mCurrentCalendar.get(Calendar.MONTH)];
        int mCurrentYear = mCurrentCalendar.get(Calendar.YEAR);
        if (yearList != null) {
            yearList.clear();
        }
        for (int i = mCurrentYear - 15, len = mCurrentYear + 15; i <= len; i++) {
            yearList.add(String.valueOf(i));
        }
        mYearList = new String[yearList.size()];
        for (int i = 0; i < yearList.size(); i++) {
            mYearList[i] = yearList.get(i);
        }
        if (mButtonId == 1) {
            String[] split = tvFromDate.getText().toString().split("/");
            mDay = split[1];
            mCurrentMonth = split[0];
            mCurrentYear = Integer.parseInt(split[2]);
        } else if (mButtonId == 2) {
            String[] split = tvToDate.getText().toString().split("/");
            mDay = split[1];
            mCurrentMonth = split[0];
            mCurrentYear = Integer.parseInt(split[2]);
        }

        temp.set(mCurrentYear, Integer.valueOf(mCurrentMonth), Integer.valueOf(mDay));
        mCalendarAdapter = new PkgHistoryCalendarViewAdapter(getActivity(), temp, mDay);
        mgridView.setAdapter(mCalendarAdapter);
        ArrayAdapter monthAdapter = new ArrayAdapter(getActivity(), R.layout.calendar_spinner_item, mMonthList);
        NTF_Utils.setSpinnerPopUpHeight(mMonthSpinner, isTablet, mMonthList.length);
        mMonthSpinner.setAdapter(monthAdapter);
        ArrayAdapter yearAdapter = new ArrayAdapter(getActivity(), R.layout.calendar_spinner_item, mYearList);
        NTF_Utils.setSpinnerPopUpHeight(mYearSpinner, isTablet, mMonthList.length);
        mYearSpinner.setAdapter(yearAdapter);
        mMonthSpinner.setSelection(Integer.parseInt(mCurrentMonth) - 1);
        textViewMonth.setText(mMonthSpinner.getSelectedItem().toString());
        int position = yearAdapter.getPosition(String.valueOf(mCurrentYear));
        mYearSpinner.setSelection(position);
        textViewYear.setText(mYearSpinner.getSelectedItem().toString());
        handler = new Handler();
        handler.post(calendarUpdater);
    }

    // Setting the data to the Month and year spinners...
    private void setMethod() {
        if (mButtonId == 1) {
            mPreviousCalendar = Calendar.getInstance();
            if (mYear != null && mMonth != null) {
                mPreviousCalendar.set(mYear, mMonth, Integer.valueOf(mDay));
            } else {
                mPreviousCalendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(mDay));
            }
            mStartDate = date.format(mPreviousCalendar.getTime());
            tvFromDate.setText(mStartDate);
        } else if (mButtonId == 2) {
            mNextCalendar = Calendar.getInstance();
            Log.v("date", "" + mYear + " " + mMonth + " " + mDay);

            if (mYear != null && mMonth != null) {
                mNextCalendar.set(mYear, mMonth, Integer.valueOf(mDay));
            } else {
                mNextCalendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(mDay));
            }
            mEndDate = date.format(mNextCalendar.getTime());
            tvToDate.setText(mEndDate);
        }
    }

    // This method will execute when clicks on previous button in calendar
    private void previousMethod() {

        if (mCurrentCalendar.get(Calendar.MONTH) == mCurrentCalendar.getActualMinimum(Calendar.MONTH)) {
            mCurrentCalendar.set((mCurrentCalendar.get(Calendar.YEAR) - 1), mCurrentCalendar.getActualMaximum(Calendar.MONTH), 1);
        } else {
            mCurrentCalendar.set(Calendar.MONTH, mCurrentCalendar.get(Calendar.MONTH) - 1);
        }
        refreshCalendar();

        int mCurrentMonthItem = mMonthSpinner.getSelectedItemPosition();
        if (mCurrentMonthItem > 0) {
            mMonthSpinner.setSelection(mCurrentMonthItem - 1);
            textViewMonth.setText(mMonthSpinner.getSelectedItem().toString());
        } else {
            int mMonthSpinnerItem = mYearSpinner.getSelectedItemPosition();
            if (mMonthSpinnerItem < yearList.size()) {
                if (mMonthSpinnerItem > 0) {
                    mMonthSpinner.setSelection(11);
                    mYearSpinner.setSelection(mMonthSpinnerItem - 1);

                    textViewMonth.setText(mMonthSpinner.getSelectedItem().toString());
                    textViewYear.setText(mYearSpinner.getSelectedItem().toString());
                } else
                    mTextViewPrevious.setEnabled(false);
                mTextViewNext.setEnabled(true);
            }
        }
    }

    // This method will execute when clicks on next button in calendar
    private void nextMethod() {

        if (mCurrentCalendar.get(Calendar.MONTH) == mCurrentCalendar.getActualMaximum(Calendar.MONTH)) {
            mCurrentCalendar.set((mCurrentCalendar.get(Calendar.YEAR) + 1),
                    mCurrentCalendar.getActualMinimum(Calendar.MONTH), 1);
        } else {
            mCurrentCalendar.set(Calendar.MONTH, mCurrentCalendar.get(Calendar.MONTH) + 1);
        }
        refreshCalendar();

        int mYearSpinnerItem = mYearSpinner.getSelectedItemPosition();
        int mCurrentYearItem = mMonthSpinner.getSelectedItemPosition();
        if (mCurrentYearItem < 11) {
            mMonthSpinner.setSelection(mCurrentYearItem + 1);
            textViewMonth.setText(mMonthSpinner.getSelectedItem().toString());
        } else {
            if (mYearSpinnerItem < yearList.size() - 1) {
                mMonthSpinner.setSelection(0);
                mYearSpinner.setSelection(mYearSpinnerItem + 1);

                textViewMonth.setText(mMonthSpinner.getSelectedItem().toString());
                textViewYear.setText(mYearSpinner.getSelectedItem().toString());
            } else
                mTextViewNext.setEnabled(false);
            mTextViewPrevious.setEnabled(true);
        }
    }

    // This method performs the refreshing of calendar...
    public void refreshCalendar() {
        mCalendarAdapter.refreshDays();
        mCalendarAdapter.notifyDataSetChanged();
        handler.post(calendarUpdater); // generate some random mCurrentCalendar items
    }

    private ArrayList<String> items;
    public Runnable calendarUpdater = new Runnable() {
        @Override
        public void run() {
            items.clear();
            for (int i = 0; i < 31; i++) {
                Random r = new Random();
                if (r.nextInt(10) > 7) {
                    items.add(Integer.toString(i));
                }
            }
            mCalendarAdapter.setItems(items);
            mCalendarAdapter.notifyDataSetChanged();
        }
    };

    // Setting the start and end dates to the Views..
    private void getMethod() {
        date = new SimpleDateFormat("MM/dd/yyyy");
        mPreviousCalendar.add(Calendar.DAY_OF_MONTH, -7);
        mStartDate = date.format(mPreviousCalendar.getTime());
        mEndDate = date.format(mNextCalendar.getTime());
        tvFromDate.setText(mStartDate);
        tvToDate.setText(mEndDate);
    }

    // Setting the mailRoom Spinner data...
    private void setMailRoomData() {
        try {
            JSONObject jsonObject = NTF_Utils.getGlobalData(getActivity());
            JSONArray jsonArray = jsonObject.getJSONArray("mailrooms");
            if (jsonArray != null && jsonArray.length() > 0) {
                mMailRoomList = SpinnerData.getList(jsonArray, null).getList();

                if (mMailRoomList != null && !mMailRoomList.isEmpty()) {

                    if (mMailRoomList.size() == 1) {
                        //dont show mail room
                        mailRoomLinearLayout.setVisibility(View.GONE);
                    } else {
                        mailRoomLinearLayout.setVisibility(View.VISIBLE);
                        mMailRoomSpinner.setVisibility(View.VISIBLE);
                        SpinnerData spinnerData = new SpinnerData();
                        spinnerData.setName("All Mailrooms");
                        spinnerData.setValue("all");
                        mMailRoomList.add(0, spinnerData);
                        if (mMailRoomList.size() >= 10) {
                            NTF_Utils.setSpinnerHeight(getActivity(), mMailRoomSpinner);
                        }
                        SpinnerMarkAdapter  mailRoomListAdapter = new SpinnerMarkAdapter(getActivity(), mMailRoomList);
                        mMailRoomSpinner.setAdapter(mailRoomListAdapter);
                        mMailRoomSpinner.setSelection(0);
                        String strMailRoomId = packageHistoryFragment.mMailRoomId;

                        for (int i = 0, len = mMailRoomList.size(); i < len; i++) {
                            if (strMailRoomId.equalsIgnoreCase(mMailRoomList.get(i).getValue())) {
                                mMailRoomSpinner.setSelection(i);
                                tv_mail_room.setText(((SpinnerData) mMailRoomSpinner.getSelectedItem()).getName());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        int width = getResources().getDisplayMetrics().widthPixels;
        if (dialog == null || dialog.getWindow() == null) {
            return;
        }
        int dialogWidth = (int) (width * 1f);
        if (getResources().getBoolean(R.bool.isDeviceTablet)) {
            dialogWidth = (int) (width * 0.85f);
        }
        int dialogHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setLayout(dialogWidth, dialogHeight);
    }

    class CalendarDialogViewHolder {
        @BindView(R.id.month_spinner)
        Spinner mMonthSpinner;
        @BindView(R.id.year_spinner)
        Spinner mYearSpinner;
        @BindView(R.id.gridview)
        GridView mgridView;
        @BindView(R.id.previous)
        TextView mTextViewPrevious;
        @BindView(R.id.textView_month_spinner)
        TextView textViewMonth;
        @BindView(R.id.startDateFarmeLayout)
        FrameLayout startDateFramelayout;
        @BindView(R.id.textView_year_spinner)
        TextView textViewYear;
        @BindView(R.id.endDate_frame_layout)
        FrameLayout endDateFl;
        @BindView(R.id.next)
        TextView mTextViewNext;

        CalendarDialogViewHolder(Dialog view) {
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.textView_month_spinner)
        void onClickTextMonthSpinner(View v) {
            mMonthSpinner.performClick();
        }

        @OnClick(R.id.startDateFarmeLayout)
        void onClickStartDateFrameLayout(View view) {
            mMonthSpinner.performClick();
        }

        @OnClick(R.id.textView_year_spinner)
        void onClickTextViewYearSpinner(View view) {
            mYearSpinner.performClick();
        }

        @OnClick(R.id.endDate_frame_layout)
        void onClickEndDateFramelayout(View view) {
            mYearSpinner.performClick();
        }

        @OnClick(R.id.previous)
        void onClickPrevious(View view) {
            previousMethod();
        }

        @OnClick(R.id.next)
        void onClickNext(View view) {
            nextMethod();
        }

        @OnItemSelected(R.id.month_spinner)
        public void onItemSelectedMonth(int position) {
            temp.set(Calendar.MONTH, position);
            refreshCalendar();
            textViewMonth.setText(mMonthList[position]);
            mMonth = mMonthSpinner.getSelectedItemPosition();
            setMethod();
        }

        @OnItemSelected(R.id.year_spinner)
        public void onItemSelectedYear(int position) {
            temp.set(Calendar.YEAR, Integer.parseInt(mYearList[position]));
            refreshCalendar();
            textViewYear.setText(mYearList[position]);
            mYear = Integer.valueOf(mYearSpinner.getSelectedItem().toString());
            setMethod();
        }

        @OnItemClick(R.id.gridview)
        public void onItemClick(int position) {
            if (!mgridView.getItemAtPosition(position).toString().isEmpty()) {
                mDay = mgridView.getItemAtPosition(position).toString();
                setMethod();
                mCalendarDialog.dismiss();
            }

        }
    }
}
