package com.notifii.notifiiapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.activities.IdentifyUserActivity;
import com.notifii.notifiiapp.models.Recipient;

import java.util.ArrayList;

public class KioskAutoCompleteUnitAdapter extends ArrayAdapter<String> {
    private final LayoutInflater inflater;
    private ArrayList<String> items;
    private ArrayList<String> itemsAll;
    private ArrayList<String> suggestions;
    private IdentifyUserActivity.UnitNumberClickListener mListener;
    private IdentifyUserActivity activity;
    private ArrayList<String> tempList = new ArrayList<>();
    public static final String noMatches = "There are no matches!";


    public void setOnRecipientItemClickListener(IdentifyUserActivity.UnitNumberClickListener listener) {
        mListener = listener;
    }

    public KioskAutoCompleteUnitAdapter(ArrayList<String> items, IdentifyUserActivity activity, ArrayList<Recipient> recipients) {
        super(activity.getApplicationContext(), R.layout.receipientname_list_item, items);
        this.items = items;
        this.itemsAll = (ArrayList<String>) items.clone();
        this.suggestions = new ArrayList<>();
        this.activity = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final KioskAutoCompleteCustomAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.receipientname_list_item, null);
            viewHolder = new KioskAutoCompleteCustomAdapter.ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (KioskAutoCompleteCustomAdapter.ViewHolder) convertView.getTag();
        }
        final String unitno = items.get(position);
        viewHolder.customerNameLabel.setTextColor(activity.getResources().getColor(R.color.black));
        viewHolder.recipientAddress1.setTextColor(activity.getResources().getColor(R.color.black));
        if (unitno != null) {
            if (viewHolder.customerNameLabel != null) {
                viewHolder.recipientAddress1.setText("");
                viewHolder.customerNameLabel.setText(unitno);
            }
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onItemClicked(unitno);
            }
        });

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            suggestions.clear();
            tempList.clear();
            if (constraint != null) {
                constraint = constraint.toString().toLowerCase().replace("(", "").replace(")", "");
                if (constraint.length() >= 1) {

                    int size = itemsAll.size();
                    if (size > 0) {
                        for (int i = 0; i < size; ++i) {
                            String fullText = itemsAll.get(i);
                            if (Recipient.isMatchedOnlyAddress(constraint.toString().trim(), fullText)) {
                                suggestions.add(fullText);
                                tempList.add(fullText);
                            }
                        }
                    }
                    if (suggestions.size() == 0) {
                        suggestions.add(noMatches);
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
                        for (int i = 0; i < size; ++i) {
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
                    activity.setDropDownHeight(filteredList.size());
                    if (resultsCount > 0 && !((ArrayList<Recipient>) results.values).isEmpty()) {
                        notifyDataSetChanged();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
