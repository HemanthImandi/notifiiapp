package com.notifii.notifiiapp.adapters;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.fragments.LogPackageInFragment;
import com.notifii.notifiiapp.models.TrackingNumber;
import com.notifii.notifiiapp.refresh.TextChangeListener;
import com.notifii.notifiiapp.utils.NTF_Utils;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TrackingRVAdapter extends RecyclerView.Adapter<TrackingRVAdapter.ViewHolder> {

    ArrayList<TrackingNumber> trackingNumbers;
    View headerView, footerView;
    LogPackageInFragment fragment;
    int currentPosition = 0;
    private String beforeTextChanged = "";
    private String afterTextChanged = "";
    TextChangeListener textChangeListener;
    private long mLastClickTime;

    public void setTextChangeListener(TextChangeListener textChangeListener) {
        this.textChangeListener = textChangeListener;
    }

    public void setFragment(LogPackageInFragment fragment) {
        this.fragment = fragment;
    }

    public TrackingRVAdapter(ArrayList<TrackingNumber> trackingNumbers, View headerView, View footerView) {
        this.trackingNumbers = trackingNumbers;
        this.headerView = headerView;
        this.footerView = footerView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_add_barcode_log_pkg_in, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return VIEW_TYPES.Header;
        else if (position == getItemCount() - 1)
            return VIEW_TYPES.Footer;
        else
            return VIEW_TYPES.Normal;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        int viewType = getItemViewType(position);
        viewHolder.extralayout.removeAllViews();
        switch (viewType) {
            case VIEW_TYPES.Header:
                viewHolder.extralayout.addView(headerView);
                viewHolder.main_linear.setVisibility(View.GONE);
                break;
            case VIEW_TYPES.Footer:
                viewHolder.extralayout.addView(footerView);
                viewHolder.main_linear.setVisibility(View.GONE);
                break;
            case VIEW_TYPES.Normal:
                viewHolder.main_linear.setVisibility(View.VISIBLE);
                handleItemView(viewHolder, position - 1);
                break;
        }
    }

    private void handleItemView(ViewHolder viewHolder, int position) {
        viewHolder.id = position;
        final TrackingNumber item = trackingNumbers.get(viewHolder.id);
        viewHolder.listsize = trackingNumbers.size();
        viewHolder.trackingNumberET.setText(item.getTrackingNumber());
        viewHolder.trackingNumberET.setSelection(viewHolder.trackingNumberET.getText().toString().length());
        if (trackingNumbers.size() > 1) {
            viewHolder.countTV.setVisibility(View.VISIBLE);
            viewHolder.countTV.setText(String.valueOf(viewHolder.id + 1));
            viewHolder.countLineView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.countTV.setVisibility(View.GONE);
            viewHolder.countLineView.setVisibility(View.GONE);
        }

        //plus button handling
        if (!TextUtils.isEmpty(item.getTrackingNumber().trim()) && viewHolder.id == trackingNumbers.size() - 1) {
            viewHolder.addButton.setVisibility(View.VISIBLE);
        } else {
            viewHolder.addButton.setVisibility(View.INVISIBLE);
        }

        //minus button handling
        if (viewHolder.id == 0) {
            if (trackingNumbers.size() > 1) {
                viewHolder.minusButton.setVisibility(View.VISIBLE);
                viewHolder.extraIcon.setVisibility(View.GONE);
            } else if (viewHolder.addButton.getVisibility() == View.VISIBLE || TextUtils.isEmpty(item.getTrackingNumber().trim())) {
                viewHolder.minusButton.setVisibility(View.GONE);
                viewHolder.extraIcon.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.minusButton.setVisibility(View.VISIBLE);
                viewHolder.extraIcon.setVisibility(View.GONE);
            }
        } else {
            viewHolder.minusButton.setVisibility(View.VISIBLE);
            viewHolder.extraIcon.setVisibility(View.GONE);
        }
        viewHolder.trackingNumberET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    beforeTextChanged = trackingNumbers.get(viewHolder.id).getTrackingNumber();
                    afterTextChanged = s.toString();
                    if (beforeTextChanged.length() != afterTextChanged.length() && viewHolder.id == 0) {
                        textChangeListener.onFirstPositionTextChanged(beforeTextChanged, afterTextChanged);
                    }
                    trackingNumbers.get(viewHolder.id).setTrackingNumber(s.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        viewHolder.trackingNumberET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                for (TrackingNumber trackingNumber : trackingNumbers) {
                    if (viewHolder.id != trackingNumbers.indexOf(trackingNumber)
                            && trackingNumber.getTrackingNumber().trim().equals(((EditText) v).getText().toString().trim())
                            && !((EditText) v).getText().toString().trim().isEmpty()
                            && trackingNumbers.size() == viewHolder.listsize) {
                        ((EditText) v).setText("");
                        NTF_Utils.showAlert(fragment.getActivity(), "", "This package was already scanned.", null);
                    }
                }
            }
        });

        viewHolder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NTF_Utils.hideKeyboard(fragment.getActivity());
                if (!item.getTrackingNumber().isEmpty()) {
                    TrackingNumber trackingNumber = new TrackingNumber();
                    trackingNumber.setFirstScanConpleted(false);
                    trackingNumbers.add(trackingNumber);
                    notifyDataSetChanged();
                }
            }
        });

        viewHolder.minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NTF_Utils.hideKeyboard(fragment.getActivity());
                if (viewHolder.id == 0 && trackingNumbers.size() > 1) {
                    fragment.setCarrier(trackingNumbers.get(1).getTrackingNumber());
                }
                if (viewHolder.id == 0) {
                    fragment.onFirstTrackingNumberRemoved();
                }
                trackingNumbers.remove(viewHolder.id);
                notifyDataSetChanged();
            }
        });

        viewHolder.barcodeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (System.currentTimeMillis() - mLastClickTime < 500) {
                    return;
                }
                mLastClickTime = System.currentTimeMillis();
                currentPosition = viewHolder.id;
                fragment.scanTrackingNumber();
            }
        });

        viewHolder.trackingNumberET.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                fragment.onTrackingNumberTouched();
                return false;
            }
        });

        if (!item.isFirstScanConpleted()) {
            viewHolder.barcodeImage.performClick();
            item.setFirstScanConpleted(true);
        } else {
            item.setFirstScanConpleted(true);
        }
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    @Override
    public int getItemCount() {
        return trackingNumbers.size() + 2;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.mEditTextTrackingNumber)
        EditText trackingNumberET;
        @BindView(R.id.addIcon)
        ImageView addButton;
        @BindView(R.id.removeIcon)
        ImageView minusButton;
        @BindView(R.id.parentLayout)
        LinearLayout parentLayout;
        @BindView(R.id.count)
        TextView countTV;
        @BindView(R.id.line_view)
        View countLineView;
        @BindView(R.id.extraIcon)
        ImageView extraIcon;
        @BindView(R.id.imageButtonCamera)
        ImageView barcodeImage;
        @BindView(R.id.extralayout)
        LinearLayout extralayout;
        @BindView(R.id.main_linear)
        LinearLayout main_linear;

        public int id;
        public int listsize;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private class VIEW_TYPES {
        public static final int Header = 1;
        public static final int Normal = 2;
        public static final int Footer = 3;
    }
}
