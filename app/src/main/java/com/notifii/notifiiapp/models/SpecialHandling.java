package com.notifii.notifiiapp.models;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.notifii.notifiiapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SpecialHandling {
    private String name;
    private String value;
    private boolean isSelected;
    private SpecialHandlingsViewHolder holder;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public SpecialHandlingsViewHolder getHolder() {
        return holder;
    }

    public void setHolder(SpecialHandlingsViewHolder holder) {
        this.holder = holder;
    }

    public static ArrayList<SpecialHandling> getSpecialHandling(JSONObject jsonObject, Activity activity) {
        ArrayList<SpecialHandling> specialHandlings = new ArrayList<>();
        try {
            if (jsonObject.optJSONArray("special_handlings") != null) {
                for (int i = 0; i < jsonObject.getJSONArray("special_handlings").length(); i++) {
                    SpecialHandling handling = new SpecialHandling();
                    handling.setName(jsonObject.getJSONArray("special_handlings").getJSONObject(i).getString("name"));
                    handling.setValue(jsonObject.getJSONArray("special_handlings").getJSONObject(i).getString("value"));
                    handling.setSelected(false);
                    View view = activity.getLayoutInflater().inflate(R.layout.row_special_handlings, null);
                    SpecialHandlingsViewHolder viewHolder = new SpecialHandlingsViewHolder(view);
                    handling.setHolder(viewHolder);
                    specialHandlings.add(handling);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return specialHandlings;
    }

    public static class SpecialHandlingsViewHolder {
        @BindView(R.id.special_handling_layout)
        public LinearLayout parentLayout;
        @BindView(R.id.checkbox_1)
        public ImageView checkbox1;
        @BindView(R.id.specialHandlingTV)
        public TextView specialHandlingTV;
        public View view;

        public SpecialHandlingsViewHolder(View itemView) {
            this.view = itemView;
            ButterKnife.bind(this, itemView);
        }
    }

}
