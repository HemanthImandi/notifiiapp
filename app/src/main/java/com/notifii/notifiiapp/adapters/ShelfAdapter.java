package com.notifii.notifiiapp.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.models.ShelfData;
import com.notifii.notifiiapp.utils.NTF_Utils;

import java.util.ArrayList;

public class ShelfAdapter extends ArrayAdapter<ShelfData> {
    private LayoutInflater mInflater;
    private Activity activity;
    private ArrayList<ShelfData> arrayList;

    public ShelfAdapter(Activity activity, int spinner_item, ArrayList<ShelfData> list) {
        super(activity,spinner_item,list);
        this.mInflater = LayoutInflater.from(activity);
        this.activity = activity;
        this.arrayList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.spinner_item,parent,false);
        }
        ShelfData shelfData = getItem(position);
        TextView textView = (TextView)convertView.findViewById(R.id.textViewSpinnerItem);
        textView.setText(shelfData.getShelfDetails());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        return view;
    }

}
