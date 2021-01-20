package com.notifii.notifiiapp.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.models.SpecialHandling;
import com.notifii.notifiiapp.utils.NTF_Utils;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SpecialHandlingsAdapter extends RecyclerView.Adapter<SpecialHandlingsAdapter.ViewHolder> {

    Activity activity;
    ArrayList<SpecialHandling> specialHandlings;

    public String getPrivilege() {
        return privilege;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    private String privilege="";

    public SpecialHandlingsAdapter(Activity activity, ArrayList<SpecialHandling> specialHandlings) {
        this.activity = activity;
        this.specialHandlings = specialHandlings;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_special_handlings, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        viewHolder.checkbox1.setBackgroundResource(specialHandlings.get(position).isSelected() ? (R.drawable.ic_check_selected) : (R.drawable.ic_check_default));
        viewHolder.specialHandlingTV.setText(specialHandlings.get(position).getName());
        if (privilege.equalsIgnoreCase("n")){
            viewHolder.checkbox1.setEnabled(false);
        } else {
            viewHolder.checkbox1.setEnabled(true);
        }
    }

    @Override
    public int getItemCount() {
        return specialHandlings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.special_handling_layout)
        public LinearLayout parentLayout;
        @BindView(R.id.checkbox_1)
        public ImageView checkbox1;
        @BindView(R.id.specialHandlingTV)
        public TextView specialHandlingTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.checkbox_1)
        void onCheckboxClicked(){
            specialHandlings.get(getAdapterPosition()).setSelected(!specialHandlings.get(getAdapterPosition()).isSelected());
            notifyDataSetChanged();
        }
    }
}
