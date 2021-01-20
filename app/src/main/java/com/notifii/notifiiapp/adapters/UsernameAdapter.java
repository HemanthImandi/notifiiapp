package com.notifii.notifiiapp.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.models.ShelfData;

import java.util.ArrayList;

public class UsernameAdapter extends ArrayAdapter<ShelfData> {
    private LayoutInflater mInflater;
    private Activity activity;
    private ArrayList<ShelfData> arrayList;

    public UsernameAdapter(Activity activity, int spinner_item, ArrayList<ShelfData> list) {
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

/*
package com.notifii.notifiiapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.customui.CustomAutoCompleteTextView;
import com.notifii.notifiiapp.sorting.SortUserName;
import com.notifii.notifiiapp.utils.NTF_Utils;

import java.util.ArrayList;

public class UsernameAdapter extends ArrayAdapter<String> {

    private final LayoutInflater inflater;
    private ArrayList<String> items;
    private ArrayList<String> itemsAll;
    private ArrayList<String> suggestions;
    private int viewResourceId;
    private ItemClickNoticeListener mListener;
    private ArrayList<String> tempList = new ArrayList<>();
    private CustomAutoCompleteTextView actv;
    private Activity mActivity;

    public interface ItemClickNoticeListener {
        void onItemClicked(String recipient);
    }

    public void setOnUsernameClickListener(ItemClickNoticeListener listener) {
        mListener = listener;
    }

    public UsernameAdapter(ArrayList<String> items, CustomAutoCompleteTextView actv, Activity mActivity) {
        super(mActivity.getApplicationContext(), R.layout.receipientname_list_item, items);
        this.items = items;
        this.itemsAll = (ArrayList<String>) items.clone();
        this.suggestions = new ArrayList<>();
        this.viewResourceId = R.layout.receipientname_list_item;
        inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.actv=actv;
        this.mActivity=mActivity;
        NTF_Utils.setTypefaceForACTV(actv,mActivity);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(viewResourceId, null);
        }
        final String userName = items.get(position);
        TextView customerNameLabel = convertView.findViewById(R.id.tvListItemResidentName);
        TextView dummyLabel = convertView.findViewById(R.id.tvListitemUnitNumber);

        if (userName != null) {
            if (customerNameLabel != null) {
                customerNameLabel.setText(userName);
                dummyLabel.setVisibility(View.GONE);
            }
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mListener != null)
                        mListener.onItemClicked(userName);
                    if (actv != null) {
                        actv.dismissDropDown();
                        actv.setSelection(userName.length());
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    private Filter nameFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            suggestions.clear();
            tempList.clear();
            if (constraint != null) {
                if (constraint.length() >= 1) {
                    int size = itemsAll.size();
                    if (size > 0) {
                        for (int i = 0; i < size; ++i) {
                            String customer = itemsAll.get(i);
                            if (customer.startsWith(constraint.toString())) {
                                suggestions.add(customer);
                                tempList.add(customer);
                            }
                        }
                    }
                    filterResults.values = suggestions;
                    filterResults.count = suggestions.size();
                    return filterResults;
                } else {
                    return new FilterResults();
                }
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(final CharSequence constraint, FilterResults results) {
            ArrayList<String> filteredList = (ArrayList<String>) results.values;
            int resultsCount = results.count;
            try {
                if (resultsCount > 0) {
                    clear();
                    if (filteredList.size() == 0 && tempList != null) {
                        filteredList = tempList;
                        int size = filteredList.size();
                        tempList.clear();
                        for (int i = 0; i < size; ++i) {//Recipient c : filteredList) {
                            if (i == 0 || i == size - 1) {
                                add(filteredList.get(i));
                            } else if (i < (size - 1) && !filteredList.get(i).equals(filteredList.get(i + 1))) {
                                add(filteredList.get(i));
                            }
                        }
                    } else {
                        for (String c : filteredList) {
                            add(c);
                        }
                    }
                    NTF_Utils.setUsernameDropdownHeight(resultsCount,actv,mActivity);
                    sort(new SortUserName());
                    if (resultsCount > 0 && !((ArrayList<String>) results.values).isEmpty()) {
                        notifyDataSetChanged();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
*/
