package com.notifii.notifiiapp.adapters;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.models.Package;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PkgLogoutDetailsAdapter extends RecyclerView.Adapter<PkgLogoutDetailsAdapter.ViewHolder> {
    ArrayList<Package> mPkgList;
    Activity activity;
    NoticeItemCloseListener mListener;
    int columnCount = 0;


    public PkgLogoutDetailsAdapter(Activity activity, ArrayList<Package> mPkgList, int columnCount) {
        this.activity = activity;
        this.mPkgList = mPkgList;
        this.columnCount = columnCount;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_log_packgae_out_details, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Package item = mPkgList.get(position);
        String name = item.getRecipientName().equalsIgnoreCase("null") ? "" : Html.fromHtml(item.getRecipientName()).toString();
        String address1 = "";
        if (item.getRecipientAddress1() != null) {
            address1 = item.getRecipientAddress1().equalsIgnoreCase("null") ? "" : item.getRecipientAddress1();
        }
        if (address1.isEmpty()) {
            holder.textViewName.setText(name);
        } else {
            holder.textViewName.setText(name + ", " + address1);

        }
        holder.textViewTrackNumber.setText((item.getTrackingNumber().equals("null") ? "" : item.getTrackingNumber()));

        // Setting the alternate background color for list
        if (columnCount == 2) {
            holder.rootView.setBackgroundResource(R.drawable.bg_edittext);
        } else {
            if (position % 2 == 0) {
                holder.rootView.setBackgroundResource(R.drawable.bg_edittext);
            } else {
                holder.rootView.setBackgroundResource(R.drawable.bg_et);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mPkgList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textViewTrackNumber)
        TextView textViewTrackNumber;
        @BindView(R.id.textViewName)
        TextView textViewName;
        @BindView(R.id.imageButtonClose)
        View imageButtonClose;
        @BindView(R.id.parentLayout)
        LinearLayout rootView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.imageButtonClose)
        void onClick(View view) {
            if (mListener != null)
                mListener.onCloseClicked(getAdapterPosition());
        }

    }

    public interface NoticeItemCloseListener {
        void onCloseClicked(int position);
    }

    public void setNoticeCloseListener(NoticeItemCloseListener listener) {
        this.mListener = listener;
    }
}
