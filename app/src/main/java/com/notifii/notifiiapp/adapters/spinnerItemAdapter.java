package com.notifii.notifiiapp.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.utils.NTF_Utils;

/**
 * Created by Administrator on 8/19/2016.
 */
public class spinnerItemAdapter extends ArrayAdapter {
    private LayoutInflater mInflater;
    private Activity activity;
    private String[] list;

    public spinnerItemAdapter(Activity activity, int spinner_item, String[] list) {
        super(activity,spinner_item);
        this.mInflater = LayoutInflater.from(activity);
        this.activity = activity;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.length;
    }

    @Override
    public Object getItem(int position) {
        return list[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.filter_dialog_spinner_item,parent,false);
        }
        TextView textView = (TextView)convertView.findViewById(R.id.textViewSpinnerItem);
        textView.setText(list[position]);

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        return view;
    }


}
