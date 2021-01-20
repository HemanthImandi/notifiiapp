package com.notifii.notifiiapp.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.models.SpinnerData;

import java.util.ArrayList;

/**
 * Created by Administrator on 8/10/2016.
 */
public class SpinnerAdapter extends ArrayAdapter<SpinnerData> {
    private LayoutInflater mInflater;
    private Activity activity;
    private ArrayList<SpinnerData> arrayList;

    public SpinnerAdapter(Activity activity, int spinner_item, ArrayList<SpinnerData> list) {
        super(activity,spinner_item,list);
        this.mInflater = LayoutInflater.from(activity);
        this.activity = activity;
        this.arrayList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.spinner_item, parent,false);
        }
        TextView textView = (TextView)convertView.findViewById(R.id.textViewSpinnerItem);
        textView.setText(arrayList.get(position).getName());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        return view;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }
}
