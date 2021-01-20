package com.notifii.notifiiapp.adapters;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.activities.IdentifyUserActivity;
import com.notifii.notifiiapp.sorting.SortRecipientAddressAscending;
import com.notifii.notifiiapp.sorting.SortRecipientNameAscending;
import com.notifii.notifiiapp.models.Recipient;
import com.notifii.notifiiapp.refresh.RecipientItemClickNoticeListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class KioskAutoCompleteCustomAdapter extends ArrayAdapter<Recipient> {

    private final LayoutInflater inflater;
    private ArrayList<Recipient> items;
    private ArrayList<Recipient> itemsAll;
    private ArrayList<Recipient> suggestions;
    private RecipientItemClickNoticeListener mListener;
    private IdentifyUserActivity activity;
    private Handler handler;
    private ArrayList<Recipient> tempList = new ArrayList<>();
    public static final String noMatches = "There are no matches!";
    private String displayformat = "ccc";



    public void setOnRecipientItemClickListener(RecipientItemClickNoticeListener listener) {
        mListener = listener;
    }

    public KioskAutoCompleteCustomAdapter(ArrayList<Recipient> items, IdentifyUserActivity activity, String displayformat) {
        super(activity.getApplicationContext(), R.layout.receipientname_list_item, items);
        this.items = items;
        this.itemsAll = (ArrayList<Recipient>) items.clone();
        this.suggestions = new ArrayList<>();
        this.activity = activity;
        this.displayformat = displayformat;
        handler = new Handler();
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.receipientname_list_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Recipient customer = items.get(position);
        viewHolder.customerNameLabel.setTextColor(activity.getResources().getColor(R.color.black));
        viewHolder.recipientAddress1.setTextColor(activity.getResources().getColor(R.color.black));
        if (displayformat == null || displayformat.length() != 3) {
            displayformat = "ccc";
        }
        if (customer != null) {
            if (viewHolder.customerNameLabel != null) {
                if (!displayformat.endsWith("h")) {
                    viewHolder.recipientAddress1.setText(customer.getAddress1());
                } else {
                    viewHolder.recipientAddress1.setText("");
                }
                String name = "";
                if (!customer.getFirstName().equalsIgnoreCase(noMatches)) {
                    if (displayformat.startsWith("c")) {
                        name = customer.getFirstName();
                    } else if (displayformat.startsWith("i") && customer.getFirstName() != null
                            && !customer.getFirstName().equalsIgnoreCase("null") && customer.getFirstName().length() > 0) {
                        name = String.valueOf(customer.getFirstName().charAt(0) + ". ");
                    }

                    if ((String.valueOf(displayformat.charAt(1))).equalsIgnoreCase("c")) {
                        name = name + " " + customer.getLastName();
                    } else if ((String.valueOf(displayformat.charAt(1))).equalsIgnoreCase("i")
                            && customer.getLastName() != null && !customer.getLastName().equalsIgnoreCase("null")
                            && customer.getLastName().length() > 0) {
                        name = name + " " + String.valueOf(customer.getLastName().charAt(0) + ". ");
                    }
                } else {
                    name = customer.getFirstName();
                }

                viewHolder.customerNameLabel.setText(name);
            }
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onItemClicked(customer);
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
                            if (displayformat != null && displayformat.length() == 3 && displayformat.endsWith("h")) {
                                if (customer.isMatchedExeptAddress(constraint.toString().trim(), customer)) {
                                    isName = true;
                                    suggestions.add(customer);
                                    tempList.add(customer);
                                }
                            } else {
                                if (customer.isMatched(constraint.toString().trim(), customer)) {
                                    isName = true;
                                    suggestions.add(customer);
                                    tempList.add(customer);
                                }
                            }
                        }
                    }
                    if (suggestions.size() == 0) {
                        Recipient recipient = new Recipient();
                        recipient.setFirstName(noMatches);
                        recipient.setLastName("");
                        recipient.setAddress1("");
                        suggestions.add(recipient);
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
                    activity.setDropDownHeight(filteredList.size());
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
    public static class ViewHolder {
        @BindView(R.id.tvListItemResidentName)
        TextView customerNameLabel;
        @BindView(R.id.tvListitemUnitNumber)
        TextView recipientAddress1;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}