package com.notifii.notifiiapp.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.mvp.models.AllLinkedAccount;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MultipleAccountsAdapter extends BaseAdapter {

    ArrayList<AllLinkedAccount> accounts;
    LayoutInflater inflater;

    public MultipleAccountsAdapter(ArrayList<AllLinkedAccount> accounts, Activity activity) {
        this.accounts = accounts;
        this.inflater = activity.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return accounts.size();
    }

    @Override
    public Object getItem(int i) {
        return accounts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup group) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_multiple_accounts, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.radioButton.setChecked(accounts.get(position).isSelected());
        viewHolder.accountName.setText(accounts.get(position).getAccountName());
        viewHolder.accountName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(position);
            }
        });
        viewHolder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(position);
            }
        });
        return convertView;
    }


    public void onItemClick(int position) {
        for (AllLinkedAccount account : accounts) {
            account.setSelected(false);
        }
        accounts.get(position).setSelected(true);
        MultipleAccountsAdapter.this.notifyDataSetChanged();
    }

    public class ViewHolder {

        @BindView(R.id.checkbox)
        RadioButton radioButton;
        @BindView(R.id.accountName)
        TextView accountName;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
