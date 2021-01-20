package com.notifii.notifiiapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.models.SpinnerData;
import java.util.ArrayList;

public class SortListAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private ArrayList<SpinnerData> filtersItemsList;
    private Resources resources;

    public SortListAdapter(Activity activity, ArrayList<SpinnerData> filtersList) {
        this.filtersItemsList = filtersList;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        resources = activity.getResources();
    }
    @Override
    public int getCount() {
        return filtersItemsList.size();
    }

    @Override
    public SpinnerData getItem(int i) {
        return filtersItemsList.get(i);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.list_item_filter_dialog, null);
        }
        LinearLayout filterListItemLinear = view.findViewById(R.id.filter_list_item_linear);
        TextView mTextView = view.findViewById(R.id.filter_list_item_textView);
        mTextView.setText(filtersItemsList.get(position).getName());
        if (filtersItemsList.get(position).isSelected()) {
            filterListItemLinear.setContentDescription(filtersItemsList.get(position) + " is selected");
            filterListItemLinear.setBackgroundResource(R.drawable.bg_filter_list_item_selected);
            mTextView.setTextColor(resources.getColor(R.color.white));
        } else {
            filterListItemLinear.setContentDescription(filtersItemsList.get(position).getValue());
            filterListItemLinear.setBackgroundResource(R.drawable.bg_edittext);
            mTextView.setTextColor(resources.getColor(R.color.text));
        }
        return view;
    }
}
