package com.notifii.notifiiapp.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.models.SpinnerData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
/**
 * Created by Administrator on 1/9/2018.
 */

public class SpinnerMarkAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Activity activity;
    private ArrayList<SpinnerData> arrayList;

    public SpinnerMarkAdapter(Activity activity, ArrayList<SpinnerData> list) {
        this.mInflater = LayoutInflater.from(activity);
        this.activity = activity;
        this.arrayList = list;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = mInflater.inflate(R.layout.spinner_mark_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        viewHolder.textView.setText(arrayList.get(position).getName());

        if (arrayList.get(position).isSelected()) {
            viewHolder.selectedMarkIV.setVisibility(View.VISIBLE);
        } else {
            viewHolder.selectedMarkIV.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.selected_mark)
        ImageView selectedMarkIV;
        @BindView(R.id.text)
        TextView textView;

        public ViewHolder(View convertView) {
            ButterKnife.bind(this, convertView);
        }
    }
}
