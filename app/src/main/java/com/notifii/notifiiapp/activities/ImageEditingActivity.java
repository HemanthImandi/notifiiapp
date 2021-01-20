package com.notifii.notifiiapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.customui.MyImageView;
import com.notifii.notifiiapp.fragments.LogPackageInFragment;
import com.notifii.notifiiapp.helpers.BitmapHelper;
import com.notifii.notifiiapp.helpers.SingleTon;
import com.notifii.notifiiapp.utils.NTF_Constants;

public class ImageEditingActivity extends AppCompatActivity {

    @BindView(R.id.imageView_preview)
    ImageView imagePreview;
    @BindView(R.id.ll_retake)
    View retakeView;
    @BindView(R.id.ll_crop_view)
    View cropingView;
    @BindView(R.id.cropImageView)
    MyImageView cropImageView;

    private Bitmap mBitmap;
    private int requestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_editing);
        ButterKnife.bind(this);
        if (getIntent().hasExtra(NTF_Constants.Extras_Keys.KEY_PHOT_POS))
            requestCode = getIntent().getIntExtra(NTF_Constants.Extras_Keys.KEY_PHOT_POS, LogPackageInFragment.CLICK_PHOTO_REQUEST_CODE_1);
        if (SingleTon.getInstance().getCapturedBitmap() == null) {
            finish();
        } else {
            try {
                mBitmap = SingleTon.getInstance().getCapturedBitmap();
                Glide.with(this)
                        .load(BitmapHelper.getByteArrayOfBitmap(SingleTon.getInstance().getCapturedBitmap()))
                        .into(imagePreview);

                 /*Glide.with(this)
                        .load(SingleTon.getInstance().getCapturedImageBytrArray())
                        .asBitmap()
                        .into(imagePreview);*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @OnClick(R.id.button_crop_cancel)
    void onCropCancelClicked() {
        mBitmap = cropImageView.getUnCropedBitmap();
        if (retakeView.getTag().equals("1")) {
            mBitmap = BitmapHelper.getRotatedBitmap(mBitmap, 90);
        } else if (retakeView.getTag().equals("2")) {
            mBitmap = BitmapHelper.getRotatedBitmap(mBitmap, 180);
        } else if (retakeView.getTag().equals("3")) {
            mBitmap = BitmapHelper.getRotatedBitmap(mBitmap, 270);
        } else {
            mBitmap = BitmapHelper.getRotatedBitmap(mBitmap, 0);
        }
        cropingView.setVisibility(View.GONE);
        retakeView.setVisibility(View.VISIBLE);
        imagePreview.setImageBitmap(mBitmap);
    }

    @OnClick(R.id.button_retake)
    void retakeImage() {
        Intent intent = new Intent(this, CameraPreviewActivity2.class);
        intent.putExtra(NTF_Constants.Extras_Keys.KEY_PHOT_POS, requestCode);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.button_retake_cancel)
    void cancleImage() {
        finish();
    }

    @OnClick(R.id.button_use_photo)
    void onUsePhoto() {
        if (retakeView.getTag().equals("1")) {
            mBitmap = BitmapHelper.getRotatedBitmap(((BitmapDrawable) imagePreview.getDrawable()).getBitmap(), 270);
        } else if (retakeView.getTag().equals("2")) {
            mBitmap = BitmapHelper.getRotatedBitmap(((BitmapDrawable) imagePreview.getDrawable()).getBitmap(), 180);
        } else if (retakeView.getTag().equals("3")) {
            mBitmap = BitmapHelper.getRotatedBitmap(((BitmapDrawable) imagePreview.getDrawable()).getBitmap(), 90);
        }
        if (requestCode == LogPackageInFragment.CLICK_PHOTO_REQUEST_CODE_1) {
            SingleTon.getInstance().setBitmap1(BitmapHelper.getResizeBitmap(mBitmap));
        } else if (requestCode == LogPackageInFragment.CLICK_PHOTO_REQUEST_CODE_2) {
            SingleTon.getInstance().setBitmap2(BitmapHelper.getResizeBitmap(mBitmap));
        } else {
            SingleTon.getInstance().setBitmap3(BitmapHelper.getResizeBitmap(mBitmap));
        }
        SingleTon.getInstance().setRequestCode(requestCode);
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @OnClick(R.id.button_counter_clockwise)
    void onClockwiseCounterClicked() {
        //tag 0 at 0 degrees
        //tag 1 at 270 degrees
        //tag 2 at 180 degrees
        //tag 3 at 90 degrees
        if (retakeView.getTag().equals("0")) {
            retakeView.setTag("1");
            imagePreview.setRotation(270);
        } else if (retakeView.getTag().equals("1")) {
            retakeView.setTag("2");
            imagePreview.setRotation(180);
        } else if (retakeView.getTag().equals("2")) {
            retakeView.setTag("3");
            imagePreview.setRotation(90);
        } else if (retakeView.getTag().equals("3")) {
            retakeView.setTag("0");
            imagePreview.setRotation(0);
        }
    }

    @OnClick(R.id.button_clockwise)
    void onClockWiseClicked() {
        if (retakeView.getTag().equals("0")) {
            retakeView.setTag("3");
            imagePreview.setRotation(90);
        } else if (retakeView.getTag().equals("3")) {
            retakeView.setTag("2");
            imagePreview.setRotation(180);
        } else if (retakeView.getTag().equals("2")) {
            retakeView.setTag("1");
            imagePreview.setRotation(270);
        } else if (retakeView.getTag().equals("1")) {
            retakeView.setTag("0");
            imagePreview.setRotation(0);
        }
    }

    @OnClick(R.id.button_crop)
    void onCropClicked() {
        Bitmap bitmap = null;
        if (retakeView.getTag().equals("1")) {
            bitmap = BitmapHelper.getRotatedBitmap(((BitmapDrawable) imagePreview.getDrawable()).getBitmap(), 270);
        } else if (retakeView.getTag().equals("2")) {
            bitmap = BitmapHelper.getRotatedBitmap(((BitmapDrawable) imagePreview.getDrawable()).getBitmap(), 180);
        } else if (retakeView.getTag().equals("3")) {
            bitmap = BitmapHelper.getRotatedBitmap(((BitmapDrawable) imagePreview.getDrawable()).getBitmap(), 90);
        } else {
            bitmap = mBitmap;
        }
        Glide.with(this)
                .load(BitmapHelper.getByteArrayOfBitmap(bitmap))
                .into(cropImageView);
        imagePreview.setImageResource(0);
        retakeView.setVisibility(View.GONE);
        cropingView.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.button_crop_done)
    void onCropDoneClicked() {
        imagePreview.setRotation(0);
        mBitmap = cropImageView.getCroppedBitmap();
        retakeView.setTag("0");
        cropImageView.setImageResource(0);
        Glide.with(this)
                .load(BitmapHelper.getByteArrayOfBitmap(mBitmap))
                .into(imagePreview);
        cropingView.setVisibility(View.GONE);
        retakeView.setVisibility(View.VISIBLE);
        System.gc();
    }

}
