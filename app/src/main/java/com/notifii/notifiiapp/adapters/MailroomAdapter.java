package com.notifii.notifiiapp.adapters;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.helpers.NTF_PrefsManager;
import com.notifii.notifiiapp.models.SpinnerData;
import com.notifii.notifiiapp.utils.NTF_Constants;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MailroomAdapter extends BaseAdapter {

    private ArrayList<SpinnerData> list;
    private Activity context;

    public MailroomAdapter(ArrayList<SpinnerData> list, Activity context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = context.getLayoutInflater().inflate(R.layout.mailroom_model, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        SpinnerData model = list.get(position);
        viewHolder.mailroom.setText(model.getName());
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                model.setSelected(isChecked);
            }
        });
        if (model.isSelected()) {
            viewHolder.checkBox.setChecked(true);
        } else {
            viewHolder.checkBox.setChecked(false);
        }
        return convertView;
    }

    public class ViewHolder {

        @BindView(R.id.checkBox)
        CheckBox checkBox;
        @BindView(R.id.mailroom)
        TextView mailroom;

        private ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }
}