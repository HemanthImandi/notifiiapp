package com.notifii.notifiiapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.fragments.LogPackageInFragment;
import com.notifii.notifiiapp.sorting.SortRecipientAddressAscending;
import com.notifii.notifiiapp.sorting.SortRecipientNameAscending;
import com.notifii.notifiiapp.models.Recipient;

import java.util.ArrayList;

public class RecipientAdapter extends ArrayAdapter<Recipient> {

    private final LayoutInflater inflater;
    private ArrayList<Recipient> items;
    private ArrayList<Recipient> itemsAll;
    private ArrayList<Recipient> suggestions;
    private int viewResourceId;
    private RecipientItemClickNoticeListener mListener;
    private LogPackageInFragment logPackageInFragment;
    private ArrayList<Recipient> tempList = new ArrayList<>();

    public interface RecipientItemClickNoticeListener {
        void onItemClicked(Recipient recipient);
    }

    public void setOnRecipientItemClickListener(RecipientItemClickNoticeListener listener) {
        mListener = listener;
    }

    public RecipientAdapter(Context activity, ArrayList<Recipient> items, LogPackageInFragment logPackageInFragment) {
        super(activity.getApplicationContext(), R.layout.receipientname_list_item, items);
        this.items = items;
        this.itemsAll = (ArrayList<Recipient>) items.clone();
        this.suggestions = new ArrayList<>();
        this.viewResourceId = R.layout.receipientname_list_item;
        this.logPackageInFragment = logPackageInFragment;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(viewResourceId, null);
        }
        final Recipient recipient = items.get(position);
        TextView customerNameLabel = convertView.findViewById(R.id.tvListItemResidentName);
        TextView recipientAddress1 = convertView.findViewById(R.id.tvListitemUnitNumber);

        if (recipient != null) {
            if (customerNameLabel != null) {
                recipientAddress1.setText(recipient.getAddress1());
                customerNameLabel.setText(recipient.getFirstName() + " " + recipient.getLastName());
            }
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onItemClicked(recipient);
            }
        });
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        boolean isName;

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
                            Recipient customer = itemsAll.get(i);
                            if (customer.isMatched(constraint.toString().trim(), customer)) {
                                isName = true;
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
            ArrayList<Recipient> filteredList = (ArrayList<Recipient>) results.values;
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
                        for (Recipient c : filteredList) {
                            add(c);
                        }
                    }
                    logPackageInFragment.setDropDownHeight(filteredList.size());
                    if (isName) {
                        sort(new SortRecipientNameAscending());
                    } else {
                        sort(new SortRecipientAddressAscending());
                    }
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