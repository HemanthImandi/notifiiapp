package com.notifii.notifiiapp.adapters;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.fragments.LogPackageInFragment;
import com.notifii.notifiiapp.models.TrackingNumber;
import com.notifii.notifiiapp.utils.NTF_Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExpandableItemAdapter extends BaseAdapter {

    public ArrayList<TrackingNumber> itemsList;
    private LayoutInflater inflater;
    private LogPackageInFragment fragment;
    public int currentPosition = 0;

    public ExpandableItemAdapter(LogPackageInFragment logPackageInFragment, ArrayList<TrackingNumber> itemsList) {
        inflater = logPackageInFragment.getActivity().getLayoutInflater();
        this.itemsList = itemsList;
        this.fragment = logPackageInFragment;
    }


    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public Object getItem(int position) {
        return itemsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_add_barcode_log_pkg_in, null);
            viewHolder = new ViewHolder(convertView);
//            viewHolder.trackingNumberET.setTag(position);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

//        viewHolder.trackingNumberET.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Toast.makeText(fragment.getActivity(),"track",Toast.LENGTH_SHORT).show();
//                InputMethodManager inputMethodManager =
//                        (InputMethodManager)fragment.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                inputMethodManager.toggleSoftInputFromWindow(
//                        viewHolder.trackingNumberET.getApplicationWindowToken(),
//                        InputMethodManager.SHOW_FORCED, 0);
//                return false;
//            }
//        });
        return convertView;
    }

    public static class ViewHolder {

        // @BindView(R.id.mEditTextTrackingNumber)
        EditText trackingNumberET;
/*        @BindView(R.id.addIcon)
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
        ImageView barcodeImage;*/

        public int id;

        public ViewHolder(View view) {
//            trackingNumberET = (EditText) view.findViewById(R.id.mEditTextTrackingNumber);

//            ButterKnife.bind(this, view);
        }
    }

    public int getCurrentPosition() {
        return currentPosition;
    }
}
