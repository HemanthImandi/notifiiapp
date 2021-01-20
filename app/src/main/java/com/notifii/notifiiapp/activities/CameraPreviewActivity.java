package com.notifii.notifiiapp.activities;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.base.NTF_BaseActivity;
import com.notifii.notifiiapp.customui.CameraPreview;
import com.notifii.notifiiapp.customui.MyImageView;
import com.notifii.notifiiapp.fragments.LogPackageInFragment;
import com.notifii.notifiiapp.helpers.BitmapHelper;
import com.notifii.notifiiapp.helpers.NTF_PrefsManager;
import com.notifii.notifiiapp.helpers.SingleTon;
import com.notifii.notifiiapp.utils.NTF_Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CameraPreviewActivity extends NTF_BaseActivity {

    @BindView(R.id.ll_capture)
    LinearLayout mLlCapture;
    @BindView(R.id.camera_preview)
    FrameLayout mFlCameraPreview;
    @BindView(R.id.button_capture_cancel)
    Button mButtonCaptureCancel;
    @BindView(R.id.ll_retake)
    LinearLayout mLlRetake;
    @BindView(R.id.imageView_preview)
    ImageView mImageViewPreview;
    @BindView(R.id.button_clockwise)
    Button mButtonClockwise;
    @BindView(R.id.ll_crop_view)
    LinearLayout mLlCropView;
    @BindView(R.id.cropImageView)
    MyImageView mCropImageView;

    private Camera mCamera;
    private CameraPreview mCameraPreview;
    private boolean safeToTakePicture;
    private Bitmap mBitmap;
    private int requestCode;
    private NTF_PrefsManager ntf_prefsManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_preview);
        ButterKnife.bind(this);
        ntf_prefsManager = new NTF_PrefsManager(CameraPreviewActivity.this);
        if (getIntent().hasExtra(Extras_Keys.KEY_PHOT_POS))
            requestCode = getIntent().getIntExtra(Extras_Keys.KEY_PHOT_POS, CLICK_PHOTO_REQUEST_CODE_1);
        mButtonClockwise.setTag(0);
     //   LogPackageOutFragment.isServiceRequ = false;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
        wm.getDefaultDisplay().getMetrics(displayMetrics);
    }

    @OnClick(R.id.button_retake_cancel)
    void onRetakeCancel() {
        mButtonCaptureCancel.performClick();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mCameraPreview = new CameraPreview(this, 0);
        mCamera = mCameraPreview.getCamera();
        if (mFlCameraPreview != null) {
            mFlCameraPreview.addView(mCameraPreview);
        }
        safeToTakePicture = true;
    }

    @OnClick(R.id.button_capture)
    void onCaptureClicked() {
        if (mCamera != null && mCameraPreview != null && mPicture != null && safeToTakePicture) {
            //getCameraInstance();
            mCamera.takePicture(null, null, mPicture);
        } else {
            mCameraPreview = new CameraPreview(this, 0);
            mCamera = mCameraPreview.getCamera();
            safeToTakePicture = true;
            mFlCameraPreview.addView(mCameraPreview);
            if (safeToTakePicture) {
                mCamera.takePicture(null, null, mPicture);
                safeToTakePicture = false;
            }
        }
    }

    @OnClick(R.id.button_capture_cancel)
    void onCaptureCancel() {
        try {
            mCamera.stopPreview();
            mCamera.release();
            safeToTakePicture = false;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CameraPreviewActivity.this.finish();
        }
    }

    @OnClick(R.id.button_retake)
    void onRetake() {
        clearViews();
        mLlRetake.setVisibility(View.GONE);
        mLlCropView.setVisibility(View.GONE);
        mLlCapture.setVisibility(View.VISIBLE);
        if (mCameraPreview != null && safeToTakePicture) {
            mCameraPreview.refreshCamera();
        } else {
            mCameraPreview = new CameraPreview(this, 0);
            safeToTakePicture = true;
            mCamera = mCameraPreview.getCamera();
            if (safeToTakePicture) {
                mCameraPreview.refreshCamera();
                safeToTakePicture = false;
            }
        }
        mButtonClockwise.setTag(0);
        mImageViewPreview.setRotation(0);
    }

    @OnClick(R.id.button_crop)
    void enableCrop() {
        mLlRetake.setVisibility(View.GONE);
        mLlCapture.setVisibility(View.GONE);
        mLlCropView.setVisibility(View.VISIBLE);

        mCropImageView.setImageBitmap(mBitmap);
    }

    @OnClick(R.id.button_counter_clockwise)
    void rotateAntiClockWise() {
        mBitmap = BitmapHelper.getRotatedBitmap(mBitmap, 270);
        mImageViewPreview.setImageBitmap(mBitmap);
    }

    @OnClick(R.id.button_clockwise)
    void rotateClockWise() {
        mBitmap = BitmapHelper.getRotatedBitmap(mBitmap, 90);
        mImageViewPreview.setImageBitmap(mBitmap);
    }

    @OnClick(R.id.button_use_photo)
    void sendResult() {
        if (requestCode == LogPackageInFragment.CLICK_PHOTO_REQUEST_CODE_1) {
            SingleTon.getInstance().setBitmap1(BitmapHelper.getResizeBitmap(mBitmap));
        } else if (requestCode == LogPackageInFragment.CLICK_PHOTO_REQUEST_CODE_2) {
            SingleTon.getInstance().setBitmap2(BitmapHelper.getResizeBitmap(mBitmap));
        } else {
            SingleTon.getInstance().setBitmap3(BitmapHelper.getResizeBitmap(mBitmap));
        }
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @OnClick(R.id.button_crop_cancel)
    void cropCancel() {
        mLlCapture.setVisibility(View.GONE);
        mLlCropView.setVisibility(View.GONE);
        mLlRetake.setVisibility(View.VISIBLE);
        mImageViewPreview.setImageBitmap(mBitmap);
    }

    @OnClick(R.id.button_crop_done)
    void cropDone() {
        mBitmap = mCropImageView.getCroppedBitmap();
        mLlCapture.setVisibility(View.GONE);
        mLlCropView.setVisibility(View.GONE);
        mLlRetake.setVisibility(View.VISIBLE);
        mImageViewPreview.setImageBitmap(mBitmap);
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap capturedImageBitmap = null;
            try {
                NTF_Utils.hideProgressDialog();

                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inTempStorage = new byte[16 * 1024];
                Camera.Parameters parameters = camera.getParameters();
                Camera.Size size = parameters.getPictureSize();

                int height11 = size.height;
                int width11 = size.width;
                float mb = (width11 * height11) / 1024000;
                if (mb > 32f) {
                    opt.inSampleSize = 32;
                } else if (mb > 16f) {
                    opt.inSampleSize = 16;
                } else if (mb > 8f) {
                    opt.inSampleSize = 8;
                } else if (mb > 6f) {
                    opt.inSampleSize = 6;
                } else if (mb > 4f) {
                    opt.inSampleSize = 4;
                } else if (mb > 3f) {
                    opt.inSampleSize = 2;
                } else if (mb >= 1f) {
                    opt.inSampleSize = 1;
                }
                capturedImageBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opt);
                if (capturedImageBitmap == null) {
                    safeToTakePicture = true;
                    return;
                }

                String deviceModel = Build.MODEL;

                if (deviceModel.contains("Nexus")) {
                    if (getScreenOrientation() == 1) {
                        mBitmap = BitmapHelper.getRotatedBitmap(capturedImageBitmap, 90);
                    } else if (getScreenOrientation() == 0) {
                        mBitmap = BitmapHelper.getRotatedBitmap(capturedImageBitmap, 0);
                    } else if (getScreenOrientation() == 9) {
                        mBitmap = BitmapHelper.getRotatedBitmap(capturedImageBitmap, 270);
                    } else if (getScreenOrientation() == 8) {
                        mBitmap = BitmapHelper.getRotatedBitmap(capturedImageBitmap, 180);
                    }
                } else {
                    if (getScreenOrientation() == 1) {
                        if (getResources().getBoolean(R.bool.isMobilePreview)) {
                            mBitmap = BitmapHelper.getRotatedBitmap(capturedImageBitmap, 90);
                        } else {
                            if (Integer.parseInt(ntf_prefsManager.get(Prefs_Keys.INFO_ORIENTATION)) == 90) {
                                mBitmap = BitmapHelper.getRotatedBitmap(capturedImageBitmap, 90);
                            } else {
                                mBitmap = BitmapHelper.getRotatedBitmap(capturedImageBitmap, 270);
                            }
                        }
                    } else if (getScreenOrientation() == 0) {
                        mBitmap = BitmapHelper.getRotatedBitmap(capturedImageBitmap, 0);
                    } else if (getScreenOrientation() == 9) {
                        if (getResources().getBoolean(R.bool.isMobilePreview)) {
                            mBitmap = BitmapHelper.getRotatedBitmap(capturedImageBitmap, 270);
                        } else {
                            mBitmap = BitmapHelper.getRotatedBitmap(capturedImageBitmap, 90);
                        }
                    } else if (getScreenOrientation() == 8) {
                        mBitmap = BitmapHelper.getRotatedBitmap(capturedImageBitmap, 180);
                    }
                }
                mImageViewPreview.setImageBitmap(mBitmap);
                shootSound();
            } catch (Exception e) {
                e.printStackTrace();
                mBitmap = null;
                if (capturedImageBitmap != null && !capturedImageBitmap.isRecycled()) {
                    capturedImageBitmap.recycle();
                }
            }
            stopCameraPreview();
            safeToTakePicture = true;
        }
    };

    private void stopCameraPreview() {
        mLlCapture.setVisibility(View.GONE);
        mLlCropView.setVisibility(View.GONE);
        mLlRetake.setVisibility(View.VISIBLE);
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    private void clearViews() {
        mCropImageView.setImageResource(0);
        mCropImageView.setImageResource(0);
        mBitmap = null;
        mButtonClockwise.setTag(0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (mCamera != null) {
                mCamera.stopPreview();
                mCameraPreview.getHolder().removeCallback(mCameraPreview);
                mCamera.release();
                mCamera = null;
            }
            if (mCameraPreview != null) {
                mFlCameraPreview.removeView(mCameraPreview);
                mCameraPreview = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shootSound() {
        try {
            AudioManager meng = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            int volume = meng.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
            if (volume != 0) {
                MediaPlayer _shootMP = null;
                if (_shootMP == null)
                    _shootMP = MediaPlayer.create(this, Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
                if (_shootMP != null)
                    _shootMP.start();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private int getScreenOrientation() {
        int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        if ((rotation == Surface.ROTATION_0
                || rotation == Surface.ROTATION_180) && height > width ||
                (rotation == Surface.ROTATION_90
                        || rotation == Surface.ROTATION_270) && width > height) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    Log.e("", "Unknown screen orientation. Defaulting to " +
                            "portrait.");
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        } else {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    Log.e("", "Unknown screen orientation. Defaulting to " +
                            "landscape.");
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }
        return orientation;
    }

}

