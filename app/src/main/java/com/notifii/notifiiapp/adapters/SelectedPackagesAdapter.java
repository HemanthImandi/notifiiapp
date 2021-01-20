package com.notifii.notifiiapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.fragments.LogPackageOutFragment;
import com.notifii.notifiiapp.fragments.UpdatePackageFragment;
import com.notifii.notifiiapp.helpers.NTF_PrefsManager;
import com.notifii.notifiiapp.helpers.SingleTon;
import com.notifii.notifiiapp.models.Package;
import com.notifii.notifiiapp.models.SpinnerData;
import com.notifii.notifiiapp.models.ViewTypes;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SelectedPackagesAdapter extends RecyclerView.Adapter<SelectedPackagesAdapter.ViewHolder> {

    public boolean mIsShowAddress1;
    private ArrayList<Package> mList;
    public NTF_PrefsManager ntf_Preferences;
    private String timezone;
    private Context mContext;
    private int noOfVisibleViews = 0;
    private ArrayList<SpinnerData> mMailroomList = new ArrayList<>();
    private final String tagLabel = "Tag#: ", shelfLabel = "Shelf: ", pkgTypeLabel = "PkgType: ", senderLabel = "Sender: ";
    private boolean isTablet;
    private boolean mIsShowMailRoom;
    private boolean mIsShowTagNumber;
    private boolean mIsShowShelf;
    private boolean mIsShowSender;
    private boolean mIsShowPackageType;
    private LogPackageOutFragment fragment;

    public void setFragment(LogPackageOutFragment fragment) {
        this.fragment = fragment;
    }

    public void resetDynamicViews(){
        noOfVisibleViews=0;
    }

    public void setmIsShowAddress1(boolean mIsShowAddress1) {
        this.mIsShowAddress1 = mIsShowAddress1;
    }

    public void setmIsShowMailRoom(boolean mIsShowMailRoom) {
        this.mIsShowMailRoom = mIsShowMailRoom;
    }

    public void setmIsShowTagNumber(boolean mIsShowTagNumber) {
        this.mIsShowTagNumber = mIsShowTagNumber;
        if (mIsShowTagNumber) {
            noOfVisibleViews = noOfVisibleViews + 1;
        }
    }

    public void setmIsShowShelf(boolean mIsShowShelf) {
        this.mIsShowShelf = mIsShowShelf;
        if (mIsShowShelf) {
            noOfVisibleViews = noOfVisibleViews + 1;
        }
    }

    public void setmIsShowSender(boolean mIsShowSender) {
        this.mIsShowSender = mIsShowSender;
        if (mIsShowSender) {
            noOfVisibleViews = noOfVisibleViews + 1;
        }
    }

    public void setmIsShowPackageType(boolean mIsShowPackageType) {
        this.mIsShowPackageType = mIsShowPackageType;
        if (mIsShowPackageType) {
            noOfVisibleViews = noOfVisibleViews + 1;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.list_item_log_pkg_out, parent, false);
        return new ViewHolder(listItem);
    }

    public SelectedPackagesAdapter(Activity context, ArrayList<Package> list, String timeZone) {
        ntf_Preferences = new NTF_PrefsManager(context);
        mList = list;
        isTablet = context.getResources().getBoolean(R.bool.isDeviceTablet);
        this.timezone = timeZone;
        this.mContext = context;
        mMailroomList.clear();
        mMailroomList.addAll(SingleTon.getInstance().getmMailRoomList(context));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        handleItemView(holder, position);
    }

    private void handleItemView(ViewHolder holder, int position) {
        Package item = mList.get(position);
        String name = (!item.getRecipientName().equals("null")) ? item.getRecipientName() : "";
        name = (mIsShowAddress1 && !item.getRecipientAddress1().equalsIgnoreCase("null") && !item.getRecipientAddress1().trim().isEmpty())
                ? name + ", " + item.getRecipientAddress1() : name;

        holder.tvRecipientName.setText(name);
        holder.tvTrackingNumber.setText(!item.getTrackingNumber().equals("null") ? " " + item.getTrackingNumber() : "");
        holder.tvDateReceived.setText(!item.getDateReceived().equals("null") ? " " + NTF_Utils.getLocalDate_and_Time(item.getDateReceived(), timezone) : "");
        if (mIsShowTagNumber) {
            holder.firstlabelTV.setText(tagLabel);
            holder.firstValueTV.setText(!item.getTagNumber().equals("null") ? item.getTagNumber() : "");
        }
        if (mIsShowShelf) {
            if (mIsShowTagNumber) {
                holder.secondLabelTV.setText(shelfLabel);
                holder.secondValueTV.setText(!item.getShelf().equals("null") ? item.getShelf() : "");
            } else {
                holder.firstlabelTV.setText(shelfLabel);
                holder.firstValueTV.setText(!item.getShelf().equals("null") ? item.getShelf() : "");
            }
        }
        if (mIsShowPackageType) {
            if (mIsShowTagNumber && mIsShowShelf) {
                holder.thirdlabelTV.setText(pkgTypeLabel);
                holder.thirdValueTV.setText(!item.getPackageType().equals("null") ? item.getPackageType() : "");
            } else if (mIsShowTagNumber || mIsShowShelf) {
                holder.secondLabelTV.setText(pkgTypeLabel);
                holder.secondValueTV.setText(!item.getPackageType().equals("null") ? item.getPackageType() : "");
            } else {
                holder.firstlabelTV.setText(pkgTypeLabel);
                holder.firstValueTV.setText(!item.getPackageType().equals("null") ? item.getPackageType() : "");
            }
        }
        if (mIsShowSender) {
            if (mIsShowTagNumber && mIsShowShelf && mIsShowPackageType) {
                holder.fourthLabelTV.setText(senderLabel);
                holder.fourthValueTV.setText(!item.getSender().equals("null") ? item.getSender() : "");
            } else if ((mIsShowTagNumber && mIsShowShelf) || (mIsShowShelf && mIsShowPackageType) || (mIsShowTagNumber && mIsShowPackageType)) {
                holder.thirdlabelTV.setText(senderLabel);
                holder.thirdValueTV.setText(!item.getSender().equals("null") ? item.getSender() : "");
            } else if (mIsShowTagNumber || mIsShowShelf || mIsShowPackageType) {
                holder.secondLabelTV.setText(senderLabel);
                holder.secondValueTV.setText(!item.getSender().equals("null") ? item.getSender() : "");
            } else {
                holder.firstlabelTV.setText(senderLabel);
                holder.firstValueTV.setText(!item.getSender().equals("null") ? item.getSender() : "");
            }
        }
        if (noOfVisibleViews == 0) {
            holder.firstDynamicLL.setVisibility(View.GONE);
            holder.secondDynamicLL.setVisibility(View.GONE);
        } else if (noOfVisibleViews == 1 || noOfVisibleViews == 2) {
            holder.firstDynamicLL.setVisibility(View.VISIBLE);
            holder.secondDynamicLL.setVisibility(View.GONE);
        } else {
            holder.firstDynamicLL.setVisibility(View.VISIBLE);
            holder.secondDynamicLL.setVisibility(View.VISIBLE);
        }
        if (mIsShowMailRoom) {
            holder.tv_mailroomLabel.setVisibility(View.VISIBLE);
            holder.tvMailroom.setText(!item.getMailroomId().equalsIgnoreCase("null") ? " " + getMailroomName(item.getMailroomId()) : "");
        } else {
            holder.tv_mailroomLabel.setVisibility(View.GONE);
            holder.tvMailroom.setVisibility(View.GONE);
        }
        if (item.getSpecialTrackInstructions() != null && !item.getSpecialTrackInstructions().isEmpty()) {
            holder.tvSpecialIns.setVisibility(View.VISIBLE);
            holder.tvSpecialIns.setText("Special Instructions: " + item.getSpecialTrackInstructions());
        } else {
            holder.tvSpecialIns.setVisibility(View.GONE);
        }
        if (item.getVacation_message() != null && !item.getVacation_message().isEmpty()) {
            holder.tvOnVacation.setVisibility(View.VISIBLE);
            holder.tvOnVacation.setText(item.getVacation_message());
        } else {
            holder.tvOnVacation.setVisibility(View.GONE);
        }
        String customText1 = "<font color='#AFB6CB'> Custom Message: </font>";
        String customText2 = "<font color='#445C92'>" + item.getCustomMessage() + "</font>";
        if (!item.getCustomMessage().equalsIgnoreCase("null")) {
            holder.customMessage.setText(Html.fromHtml(customText1 + customText2));
        } else {
            holder.customMessage.setText(Html.fromHtml(customText1));
        }
        String staffNotes1 = "<font color='#AFB6CB'> Staff Notes: </font>";
        String staffNotes2 = "<font color='#445C92'>" + item.getStaffNotes() + "</font>";
        if (!item.getStaffNotes().equalsIgnoreCase("null")) {
            holder.staffNotes.setText(Html.fromHtml(staffNotes1 + staffNotes2));
        } else {
            holder.staffNotes.setText(Html.fromHtml(staffNotes1));
        }
        String changeHistoryText1 = "<font color='#AFB6CB'> Change History: </font>";
        String changeHistoryText2 = "<font color='#445C92'>" + item.getChangeHistory() + "</font>";
        if (!item.getChangeHistory().equalsIgnoreCase("null")) {
            holder.changeHistory.setText(Html.fromHtml(changeHistoryText1 + changeHistoryText2));
        } else {
            holder.changeHistory.setText(Html.fromHtml(changeHistoryText1));
        }
        if (item.getLoginPicturesList().size() == 0 || !item.isItemExpanded()) {
            holder.loginNoPictureView.setVisibility(View.VISIBLE);
            holder.loginpicturesLayout.setVisibility(View.GONE);
        } else {
            holder.loginNoPictureView.setVisibility(View.GONE);
            holder.loginpicturesLayout.setVisibility(View.VISIBLE);
            holder.imageFrame1.setVisibility(View.GONE);
            holder.imageFrame2.setVisibility(View.GONE);
            holder.imageFrame3.setVisibility(View.GONE);
            for (String imageurl : item.getLoginPicturesList()) {
                ImageView image;
                ProgressBar processBar;
                if (item.getLoginPicturesList().indexOf(imageurl) == 0) {
                    image = holder.pic1;
                    processBar = holder.progressbar1;
                    holder.imageFrame1.setVisibility(View.VISIBLE);
                } else if (item.getLoginPicturesList().indexOf(imageurl) == 1) {
                    image = holder.pic2;
                    processBar = holder.progressbar2;
                    holder.imageFrame2.setVisibility(View.VISIBLE);
                } else {
                    image = holder.pic3;
                    processBar = holder.progressbar3;
                    holder.imageFrame3.setVisibility(View.VISIBLE);
                }
                Glide.with(mContext)
                        .load(imageurl)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                processBar.setVisibility(View.GONE);
                                return false;
                            }
                        }).into(image);
            }
        }

        if (!isTablet) {
            holder.dummyview.setVisibility(View.VISIBLE);
            if (position % 2 == 0) {
                holder.mainParentLL.setBackgroundResource(R.drawable.list_background_alternate);
            } else {
                holder.mainParentLL.setBackgroundResource(R.drawable.list_backgroundcolor);
            }
        } else {
            holder.dummyview.setVisibility(View.INVISIBLE);
            if (item.isChecked()) {
                if (position % 2 != 0) {
                    holder.parentLL.setBackground(mContext.getResources().getDrawable(R.drawable.bg_log_pkg_out_selected_white));
                } else {
                    holder.parentLL.setBackground(mContext.getResources().getDrawable(R.drawable.bg_log_pkg_out_selected));
                }
            } else {
                if (position % 2 == 0) {
                    holder.parentLL.setBackground(mContext.getResources().getDrawable(R.drawable.bg_edittext));
                } else {
                    holder.parentLL.setBackground(mContext.getResources().getDrawable(R.drawable.bg_et));
                }
            }
        }
        if (item.isItemExpanded()) {
            holder.editPackage.setVisibility(View.VISIBLE);
            holder.expandedMenuLL.setVisibility(View.VISIBLE);
            holder.down_arrow_btn.setContentDescription("Less info");
            Picasso.with(mContext).load(R.drawable.ic_up_arrow).into(holder.down_arrow_btn);
        } else {
            holder.editPackage.setVisibility(View.INVISIBLE);
            holder.expandedMenuLL.setVisibility(View.GONE);
            holder.down_arrow_btn.setContentDescription("More info");
            Picasso.with(mContext).load(R.drawable.ic_down_arrow).into(holder.down_arrow_btn);
        }
        holder.checkBox.setBackgroundResource(item.isChecked() ? (R.drawable.ic_check_selected) : (R.drawable.ic_check_default));
    }

    // Getting MailRoomName
    private String getMailroomName(String mailroomId) {
        if (mMailroomList != null && !mMailroomList.isEmpty()) {
            for (SpinnerData item : mMailroomList) {
                if (item.getValue().equalsIgnoreCase(mailroomId)) {
                    return item.getName();
                }
            }
        }
        return "";
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.mainParentLL)
        LinearLayout mainParentLL;
        @BindView(R.id.dummyview)
        View dummyview;
        @BindView(R.id.extralayout)
        LinearLayout extralayout;
        @BindView(R.id.parentLL)
        LinearLayout parentLL;
        @BindView(R.id.tv_recipientName)
        TextView tvRecipientName;
        @BindView(R.id.tv_trackingNumber)
        TextView tvTrackingNumber;
        @BindView(R.id.tv_dateReceived)
        TextView tvDateReceived;
        @BindView(R.id.tv_mailroom)
        TextView tvMailroom;
        @BindView(R.id.tv_mailroomLabel)
        TextView tv_mailroomLabel;
        @BindView(R.id.specialIns_tv)
        TextView tvSpecialIns;
        @BindView(R.id.on_vacation_tv)
        TextView tvOnVacation;
        @BindView(R.id.down_arrow_btn)
        ImageView down_arrow_btn;
        @BindView(R.id.expanded_menu)
        LinearLayout expandedMenuLL;
        @BindView(R.id.custom_message)
        TextView customMessage;
        @BindView(R.id.staff_notes)
        TextView staffNotes;
        @BindView(R.id.change_history)
        TextView changeHistory;
        @BindView(R.id.login_no_picture_view)
        TextView loginNoPictureView;
        @BindView(R.id.loginpicturesLayout)
        LinearLayout loginpicturesLayout;
        @BindView(R.id.login_image1_progressbar)
        ProgressBar progressbar1;
        @BindView(R.id.imageView1loginImage)
        ImageView pic1;
        @BindView(R.id.frame_login_image1)
        FrameLayout imageFrame1;
        @BindView(R.id.login_image2_progressbar)
        ProgressBar progressbar2;
        @BindView(R.id.imageView2loginImage)
        ImageView pic2;
        @BindView(R.id.frame_login_image2)
        FrameLayout imageFrame2;
        @BindView(R.id.login_image3_progressbar)
        ProgressBar progressbar3;
        @BindView(R.id.imageView3loginImage)
        ImageView pic3;
        @BindView(R.id.frame_login_image3)
        FrameLayout imageFrame3;
        @BindView(R.id.firstlabelTV)
        TextView firstlabelTV;
        @BindView(R.id.firstValueTV)
        TextView firstValueTV;
        @BindView(R.id.secondLabelTV)
        TextView secondLabelTV;
        @BindView(R.id.secondValueTV)
        TextView secondValueTV;
        @BindView(R.id.thirdlabelTV)
        TextView thirdlabelTV;
        @BindView(R.id.thirdValueTV)
        TextView thirdValueTV;
        @BindView(R.id.fourthLabelTV)
        TextView fourthLabelTV;
        @BindView(R.id.fourthValueTV)
        TextView fourthValueTV;
        @BindView(R.id.firstDynamicLL)
        LinearLayout firstDynamicLL;
        @BindView(R.id.secondDynamicLL)
        LinearLayout secondDynamicLL;
        @BindView(R.id.checkBox)
        ImageView checkBox;
        @BindView(R.id.edit_button)
        ImageView editPackage;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.edit_button)
        void onEditPkgClicked() {
            if (!(NTF_Utils.getCurrentFragment(fragment) instanceof LogPackageOutFragment)) {
                return;
            }
            UpdatePackageFragment updatePackageFragment = new UpdatePackageFragment();
            updatePackageFragment.setTargetFragment(fragment, LogPackageOutFragment.UPDATEPACKAGEREQUESTCODE);
            ArrayList<Package> tempArray = new ArrayList<>();
            tempArray.add(mList.get(getAdapterPosition()));
            updatePackageFragment.setList(tempArray);
            fragment.changeDetailsFragment(updatePackageFragment);
        }

        @OnClick(R.id.checkBox)
        void onCheckClicked() {
            mList.get(getAdapterPosition()).setIsChecked(!mList.get(getAdapterPosition()).isChecked());
            fragment.updateSelectedPackagesList(mList.get(getAdapterPosition()));
            fragment.updateCheckAllIcon();
            notifyDataSetChanged();
        }

        @OnClick(R.id.down_arrow_btn)
        void onMoreInfoClicked() {
            boolean isAlreadyExpanded = mList.get(getAdapterPosition()).isItemExpanded();
            for (Package aPackage : mList) {
                aPackage.setItemExpanded(false);
            }
            mList.get(getAdapterPosition()).setItemExpanded(!isAlreadyExpanded);
            notifyDataSetChanged();
        }

        @OnClick(R.id.imageView1loginImage)
        void onFirstImageClicked() {
            NTF_Utils.showImageAlert(mContext, mList.get(getAdapterPosition()).getLoginPicturesList().get(0));
        }

        @OnClick(R.id.imageView2loginImage)
        void onSecondImageClicked() {
            NTF_Utils.showImageAlert(mContext, mList.get(getAdapterPosition()).getLoginPicturesList().get(1));
        }

        @OnClick(R.id.imageView3loginImage)
        void onThirdImageClicked() {
            NTF_Utils.showImageAlert(mContext, mList.get(getAdapterPosition()).getLoginPicturesList().get(2));
        }
    }
}