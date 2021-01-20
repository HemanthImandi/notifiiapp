package com.notifii.notifiiapp.adapters;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.helpers.NTF_PrefsManager;
import com.notifii.notifiiapp.helpers.SingleTon;
import com.notifii.notifiiapp.models.SpinnerData;
import com.notifii.notifiiapp.mvp.models.KioskPackage;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectPackagesAdapter extends BaseAdapter {

    private ArrayList<KioskPackage> list;
    private Activity context;
    private boolean isTablet;
    private String shelfshow = "1", tag = "1";
    private String timezone = "";
    private boolean multipleMailrooms = false;
    private ArrayList<SpinnerData> mMailroomList = new ArrayList<>();

    public SelectPackagesAdapter(ArrayList<KioskPackage> list, Activity context, NTF_PrefsManager ntf_Preferences) {
        this.list = list;
        this.context = context;
        isTablet = context.getResources().getBoolean(R.bool.isDeviceTablet);
        shelfshow = ntf_Preferences.get(NTF_Constants.Prefs_Keys.PKG_LOGOUT_SHELF);
        tag = ntf_Preferences.get(NTF_Constants.Prefs_Keys.PKG_LOGOUT_TAGNUMBER);
        timezone = ntf_Preferences.get(NTF_Constants.Prefs_Keys.TIMEZONE);
        mMailroomList = SingleTon.getInstance().getmMailRoomList(context);
    }

    public void setMultipleMailrooms(boolean multipleMailrooms) {
        this.multipleMailrooms = multipleMailrooms;
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
            convertView = context.getLayoutInflater().inflate(R.layout.list_item_selectpackage, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final KioskPackage model = list.get(position);
        viewHolder.trackingTV.setText(model.getTrackingNumber());
        viewHolder.receivedTV.setText(NTF_Utils.getLocalDate_and_Time(NTF_Utils.getDate(model.getDateReceived()), timezone));
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
        if (shelfshow.equalsIgnoreCase("1")) {
            viewHolder.shelfLL.setVisibility(View.VISIBLE);
            if (model.getShelf()!=null && !model.getShelf().equalsIgnoreCase("null")){
                viewHolder.shelfTV.setText(model.getShelf());
            }else {
                viewHolder.shelfTV.setText("");
            }
        } else {
            viewHolder.shelfLL.setVisibility(View.GONE);
        }
        if (tag.equalsIgnoreCase("1")) {
            viewHolder.tagNumberLL.setVisibility(View.VISIBLE);
            if (model.getTagNumber() != null && !model.getTagNumber().equalsIgnoreCase("null")) {
                viewHolder.tagTV.setText(model.getTagNumber());
            } else {
                viewHolder.tagTV.setText("");
            }
        } else {
            viewHolder.tagNumberLL.setVisibility(View.GONE);
        }
        if (isTablet) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if (position % 2 == 0) {
                    viewHolder.parentLayout.setBackground(context.getResources().getDrawable(R.drawable.bg_et));
                } else {
                    viewHolder.parentLayout.setBackground(context.getResources().getDrawable(R.drawable.bg_edittext));
                }
            }
        } else {
            if (position % 2 == 0) {
            } else {
                viewHolder.parentLayout.setBackgroundResource(R.drawable.list_backgroundcolor);
                viewHolder.parentLayout.setBackgroundResource(R.drawable.list_background_alternate);
            }
        }
        if (multipleMailrooms){
            viewHolder.mailroomLL.setVisibility(View.VISIBLE);
            viewHolder.mailroomTV.setText(NTF_Utils.getMailroomName(model.getMailroomId(), mMailroomList));
        } else {
            viewHolder.mailroomLL.setVisibility(View.GONE);
        }
        if (model.getPackageImages().getLoginImages().size() == 0){
            viewHolder.loginpicturesLayout.setVisibility(View.GONE);
            viewHolder.log_in_pics_label.setVisibility(View.GONE);
        } else {
            viewHolder.loginpicturesLayout.setVisibility(View.VISIBLE);
            viewHolder.log_in_pics_label.setVisibility(View.VISIBLE);
            ViewHolder finalViewHolder = viewHolder;
            loadImage(model.getPackageImages().getLoginImages().get(0),finalViewHolder.login_image1_progressbar,viewHolder.imageView1loginImage);
            viewHolder.imageView1loginImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NTF_Utils.showImageAlert(context, model.getPackageImages().getLoginImages().get(0));
                }
            });
            if (model.getPackageImages().getLoginImages().size() > 1){
                viewHolder.frame_login_image2.setVisibility(View.VISIBLE);
                loadImage(model.getPackageImages().getLoginImages().get(1),finalViewHolder.login_image2_progressbar,viewHolder.imageView2loginImage);
                viewHolder.imageView2loginImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NTF_Utils.showImageAlert(context, model.getPackageImages().getLoginImages().get(1));
                    }
                });
            } else {
                viewHolder.frame_login_image2.setVisibility(View.GONE);
            }
            if (model.getPackageImages().getLoginImages().size() > 2){
                viewHolder.frame_login_image3.setVisibility(View.VISIBLE);
                loadImage(model.getPackageImages().getLoginImages().get(2),finalViewHolder.login_image3_progressbar,viewHolder.imageView3loginImage);
                viewHolder.imageView3loginImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NTF_Utils.showImageAlert(context, model.getPackageImages().getLoginImages().get(2));
                    }
                });
            } else {
                viewHolder.frame_login_image3.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    private void loadImage(String path, View progressbar, ImageView imageView){
     /*   Picasso.with(context).load(path).into(imageView, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                progressbar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {

            }
        });*/

        Glide.with(context)
                .load(path)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressbar.setVisibility(View.GONE);
                        return false;
                    }
                }).into(imageView);
    }

    public class ViewHolder {
        @BindView(R.id.parentLayout)
        LinearLayout parentLayout;
        @BindView(R.id.trackingTV)
        TextView trackingTV;
        @BindView(R.id.tagTV)
        TextView tagTV;
        @BindView(R.id.shelfTV)
        TextView shelfTV;
        @BindView(R.id.receivedTV)
        TextView receivedTV;
        @BindView(R.id.checkBox)
        CheckBox checkBox;
        @BindView(R.id.tagNumberLL)
        LinearLayout tagNumberLL;
        @BindView(R.id.shelfLL)
        LinearLayout shelfLL;
        @BindView(R.id.mailroomLL)
        LinearLayout mailroomLL;
        @BindView(R.id.mailroomTV)
        TextView mailroomTV;
        @BindView(R.id.loginpicturesLayout)
        View loginpicturesLayout;
        @BindView(R.id.login_image1_progressbar)
        View login_image1_progressbar;
        @BindView(R.id.imageView1loginImage)
        ImageView imageView1loginImage;
        @BindView(R.id.frame_login_image2)
        FrameLayout frame_login_image2;
        @BindView(R.id.login_image2_progressbar)
        View login_image2_progressbar;
        @BindView(R.id.imageView2loginImage)
        ImageView imageView2loginImage;
        @BindView(R.id.frame_login_image3)
        FrameLayout frame_login_image3;
        @BindView(R.id.login_image3_progressbar)
        View login_image3_progressbar;
        @BindView(R.id.imageView3loginImage)
        ImageView imageView3loginImage;
        @BindView(R.id.log_in_pics_label)
        View log_in_pics_label;

        private ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }


    public void resetSettings(NTF_PrefsManager ntf_Preferences){
        shelfshow = ntf_Preferences.get(NTF_Constants.Prefs_Keys.PKG_LOGOUT_SHELF);
        tag = ntf_Preferences.get(NTF_Constants.Prefs_Keys.PKG_LOGOUT_TAGNUMBER);
        timezone = ntf_Preferences.get(NTF_Constants.Prefs_Keys.TIMEZONE);
        notifyDataSetChanged();
    }
}
