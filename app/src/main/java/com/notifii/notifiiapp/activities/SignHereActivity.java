package com.notifii.notifiiapp.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.base.NTF_BaseActivity;
import com.notifii.notifiiapp.campkg.cameracontroller.CameraController;
import com.notifii.notifiiapp.campkg.others.ImageSaver;
import com.notifii.notifiiapp.customui.FrontCameraPreview;
import com.notifii.notifiiapp.customui.SignaturePad;
import com.notifii.notifiiapp.helpers.BitmapHelper;
import com.notifii.notifiiapp.helpers.SingleTon;
import com.notifii.notifiiapp.mvp.models.PackageLogoutDataRequest;
import com.notifii.notifiiapp.mvp.models.PackageLogoutImagesRequest;
import com.notifii.notifiiapp.mvp.models.PackagePicture;
import com.notifii.notifiiapp.mvp.presenters.PackageLogoutDataPresenter;
import com.notifii.notifiiapp.mvp.presenters.PackageLogoutImagesPresenter;
import com.notifii.notifiiapp.mvp.views.PackageLogoutDataView;
import com.notifii.notifiiapp.mvp.views.PackageLogoutImagesView;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SignHereActivity extends NTF_BaseActivity implements PackageLogoutDataView, PackageLogoutImagesView {

    @BindView(R.id.textViewActionTitle)
    TextView mTextViewActionTitle;
    @BindView(R.id.disclaimer)
    TextView disclaimer;
    @BindView(R.id.iv_app_icon)
    ImageView logo;
    @BindView(R.id.mobile_sign_layout)
    FrameLayout mMobileSignLayout;
    @BindView(R.id.tab_sign_layout)
    FrameLayout tab_sign_layout;
    @BindView(R.id.errormessageFL)
    FrameLayout errormessageFL;
    @BindView(R.id.signature_pad)
    SignaturePad mSignaturePad;
    @BindView(R.id.signatureImageView)
    ImageView signatureImageView;
    @BindView(R.id.textView_sign_here)
    TextView textView_sign_here;
    @BindView(R.id.mobile_clear_sign)
    ImageView mobile_clear_sign;
    @BindView(R.id.btn_click_to_sign)
    Button btn_click_to_sign;
    @BindView(R.id.camera_preview)
    FrameLayout mFlCameraPreview;
    @BindView(R.id.submitBTN)
    Button submitBTN;

    private boolean isTablet;
    private static final int DRAW_SIGNATURE_ACTIVITY_CONSTANT = 1;
    private Bitmap mSignatureBitmap;
    private String str_signature_base64string = "";
    private String selectedPackages = "";
    private String residentid = "";
    PackageLogoutDataPresenter dataPresenter;
    PackageLogoutImagesPresenter imagesPresenter;
    private FrontCameraPreview mCameraPreview;
    private Camera mCamera;
    private boolean safeToTakePicture;
    private Bitmap mFrontImage = null;
    private boolean cameraEnabled = false;
    private boolean isCameraOpened = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_here);
        ButterKnife.bind(this);
        setToolbar();
        if (getIntent() != null && getIntent().getStringExtra(SelectYourPackageActivity.SELECTED_PACKAGES) != null) {
            selectedPackages = getIntent().getStringExtra(SelectYourPackageActivity.SELECTED_PACKAGES);
            residentid = getIntent().getStringExtra(IdentifyUserActivity.RESIDENT_ID);
        }
        disclaimer.setMovementMethod(new ScrollingMovementMethod());
        if (ntf_Preferences.hasKey(Prefs_Keys.KIOSK_MODE_DISCLAIMER) && ntf_Preferences.get(Prefs_Keys.KIOSK_MODE_DISCLAIMER).length() == 0) {
            disclaimer.setText(R.string.kiosk_disclaimer);
        } else {
            disclaimer.setText(ntf_Preferences.get(Prefs_Keys.KIOSK_MODE_DISCLAIMER));
        }
        isTablet = getResources().getBoolean(R.bool.isDeviceTablet);
        errormessageFL.setVisibility(View.INVISIBLE);
        if (isTablet) {
            mMobileSignLayout.setVisibility(View.GONE);
            tab_sign_layout.setVisibility(View.VISIBLE);
        } else {
            tab_sign_layout.setVisibility(View.GONE);
            signatureImageView.setVisibility(View.GONE);
            mobile_clear_sign.setVisibility(View.GONE);
        }
        if (isTablet) {
            mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
                @Override
                public void onStartSigning() {
                    textView_sign_here.setVisibility(View.GONE);
                }

                @Override
                public void onSigned() {
                    textView_sign_here.setVisibility(View.GONE);
                }

                @Override
                public void onClear() {
                }
            });
        }
        dataPresenter = new PackageLogoutDataPresenter();
        dataPresenter.attachMvpView(this);
        imagesPresenter = new PackageLogoutImagesPresenter();
        imagesPresenter.attachMvpView(this);
        setCameraFunctionality();
    }

    private void setToolbar() {
        mTextViewActionTitle.setText("");
        mTextViewActionTitle.setBackgroundResource(R.drawable.ic_notifii_track_white_logo);
        logo.setVisibility(View.GONE);
    }

    @OnClick(R.id.startoverBTN)
    void onStartOverClicked() {
        Intent intent = new Intent(this, IdentifyUserActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    void updateButtonText(){
        if (cameraEnabled && isCameraOpened){
            submitBTN.setText("Capture & Submit");
        } else {
            submitBTN.setText("Submit");
        }
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
                Bitmap rotatedBitmap = null;
                if (deviceModel.contains("Nexus")) {
                    if (getScreenOrientation() == 1) {
                        rotatedBitmap = BitmapHelper.getRotatedBitmap(capturedImageBitmap, 90);
                    } else if (getScreenOrientation() == 0) {
                        rotatedBitmap = BitmapHelper.getRotatedBitmap(capturedImageBitmap, 0);
                    } else if (getScreenOrientation() == 9) {
                        rotatedBitmap = BitmapHelper.getRotatedBitmap(capturedImageBitmap, 270);
                    } else if (getScreenOrientation() == 8) {
                        rotatedBitmap = BitmapHelper.getRotatedBitmap(capturedImageBitmap, 180);
                    }
                } else {
                    if (getScreenOrientation() == 1) {
                        if (getResources().getBoolean(R.bool.isMobilePreview)) {
                            rotatedBitmap = BitmapHelper.getRotatedBitmap(capturedImageBitmap, 90);
                        } else {
                            if (Integer.parseInt(ntf_Preferences.get(Prefs_Keys.INFO_ORIENTATION)) == 90) {
                                rotatedBitmap = BitmapHelper.getRotatedBitmap(capturedImageBitmap, 90);
                            } else {
                                rotatedBitmap = BitmapHelper.getRotatedBitmap(capturedImageBitmap, 270);
                            }
                        }
                    } else if (getScreenOrientation() == 0) {
                        rotatedBitmap = BitmapHelper.getRotatedBitmap(capturedImageBitmap, 0);
                    } else if (getScreenOrientation() == 9) {
                        if (getResources().getBoolean(R.bool.isMobilePreview)) {
                            rotatedBitmap = BitmapHelper.getRotatedBitmap(capturedImageBitmap, 270);
                        } else {
                            rotatedBitmap = BitmapHelper.getRotatedBitmap(capturedImageBitmap, 90);
                        }
                    } else if (getScreenOrientation() == 8) {
                        rotatedBitmap = BitmapHelper.getRotatedBitmap(capturedImageBitmap, 180);
                    }
                }
                mFrontImage = BitmapHelper.reverseByVertical(rotatedBitmap);
            } catch (Exception e) {
                e.printStackTrace();
                mFrontImage = null;
                if (capturedImageBitmap != null && !capturedImageBitmap.isRecycled()) {
                    capturedImageBitmap.recycle();
                }
            }
            safeToTakePicture = true;
            doLogoutPkg();
            if (mCamera != null) {
                mCamera.stopPreview();
            }
        }
    };


    @OnClick(R.id.submitBTN)
    void onSubmit() {
        if (isTablet) {
            if (!mSignaturePad.isEmpty()) {
                mSignatureBitmap = mSignaturePad.getTransparentSignatureBitmap();
                mSignatureBitmap = overlay(mSignatureBitmap);
                str_signature_base64string = signatureBase64String(mSignatureBitmap);
            } else {
                str_signature_base64string = "";
            }
        }
        else {
            if (mSignatureBitmap != null) {
                Canvas canvas = new Canvas(mSignatureBitmap);
                canvas.drawBitmap(mSignatureBitmap, new Matrix(), null);
                Paint paintStroke = new Paint();
                paintStroke.setStrokeWidth(5);
                paintStroke.setStyle(Paint.Style.STROKE);
                paintStroke.setColor(Color.LTGRAY);
                paintStroke.setAntiAlias(true);
                final Rect rect = new Rect(0, 0, mSignatureBitmap.getWidth(), mSignatureBitmap.getHeight());
                final RectF rectF = new RectF(rect);
                signatureImageView.setPadding(0, 0, 0, 0);
                canvas.drawRoundRect(rectF, 0, 0, paintStroke);

                str_signature_base64string = signatureBase64String(mSignatureBitmap);
            } else {
                str_signature_base64string = "";
            }
        }
        dataPresenter.doCheckCredentials(str_signature_base64string);
    }

    private void takeImageToSubmit(){
        if (mCamera != null && mCameraPreview != null && mPicture != null && safeToTakePicture) {
            mCamera.takePicture(null, null, mPicture);
        } else {
            mCameraPreview = new FrontCameraPreview(this);
            mCamera = mCameraPreview.getCamera();
            safeToTakePicture = true;
            mFlCameraPreview.addView(mCameraPreview);
            if (safeToTakePicture) {
                mCamera.takePicture(null, null, mPicture);
                safeToTakePicture = false;
            }
        }
    }

    private void setCameraFunctionality(){
        cameraEnabled = !ntf_Preferences.get(Prefs_Keys.USE_FRONT_CAMERA, "0").equals("0");
//        cameraEnabled = true;
        if (cameraEnabled){
            NTF_Utils.checkCameraPermissionToProgress(this, new Runnable() {
                @Override
                public void run() {
                    mCameraPreview = new FrontCameraPreview(SignHereActivity.this);
                    mCamera = mCameraPreview.getCamera();
                    if (mFlCameraPreview != null) {
                        mFlCameraPreview.addView(mCameraPreview);
                    }
                    isCameraOpened = true;
                    safeToTakePicture = true;
                }
            });
            mFlCameraPreview.setVisibility(View.VISIBLE);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        } else {
            mFlCameraPreview.setVisibility(View.GONE);
            stopCamera();
        }

    }


    @OnClick(R.id.mobile_clear_sign)
    void onMobileClearSign() {
        mSignatureBitmap = null;
        signatureImageView.setVisibility(View.GONE);
        btn_click_to_sign.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btn_click_to_sign)
    void onCLickToSignIn() {
        Intent i = new Intent(this, DrawSignatureActivity.class);
        startActivityForResult(i, DRAW_SIGNATURE_ACTIVITY_CONSTANT);
    }

    @OnClick(R.id.tab_clear_sign)
    void onTabClearSign() {
        mSignaturePad.clear();
        textView_sign_here.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.closeerrorTV)
    void onCloseErrorTV() {
        errormessageFL.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DRAW_SIGNATURE_ACTIVITY_CONSTANT && resultCode == RESULT_OK) {
            try {
                mSignatureBitmap = NTF_Constants.Extras_Keys.IMAGE;
                mobile_clear_sign.setVisibility(View.VISIBLE);
                btn_click_to_sign.setVisibility(View.GONE);
                signatureImageView.setVisibility(View.VISIBLE);
                if (mSignatureBitmap != null) {
                    signatureImageView.setImageBitmap(mSignatureBitmap);
                }
            } catch (Exception e) {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }


    private Bitmap overlay(Bitmap signaturebmp) {
        Bitmap bitmap = null;
        Bitmap overlayBitmap = Bitmap.createBitmap(signaturebmp.getWidth(), signaturebmp.getHeight(), signaturebmp.getConfig());
        Canvas canvas = new Canvas(overlayBitmap);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height1 = displayMetrics.heightPixels;
        int width1 = displayMetrics.widthPixels;
        if (bitmap != null) {
            bitmap.recycle();
        }
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_grid_latest);
        bitmap = Bitmap.createScaledBitmap(bitmap, width1, signaturebmp.getHeight(), true);
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.drawBitmap(signaturebmp, new Matrix(), null);

        Paint paintStroke = new Paint();

        paintStroke.setStrokeWidth(2);
        paintStroke.setStyle(Paint.Style.STROKE);
        paintStroke.setColor(Color.LTGRAY);
        paintStroke.setAntiAlias(true);

        final Rect rect = new Rect(0, 0, overlayBitmap.getWidth(), overlayBitmap.getHeight());
        final RectF rectF = new RectF(rect);
        canvas.drawRoundRect(rectF, 0, 0, paintStroke);
        return overlayBitmap;
    }

    private String signatureBase64String(Bitmap bitmap) {
        try {
            Bitmap converetdImage = getResizedBitmap(bitmap, 600, (600 * bitmap.getHeight()) / bitmap.getWidth());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            converetdImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getResizedBitmap(Bitmap source, int newWidth, int newHeight) throws NullPointerException {
        if (source != null) {
            int width = source.getWidth();
            int height = source.getHeight();
            // create a matrix for the manipulation
            Matrix matrix = new Matrix();
            // resize the bitmap
            matrix.postScale(((float) newWidth) / width, ((float) newHeight) / height);
            // recreate the new Bitmap
            return Bitmap.createBitmap(source, 0, 0, width, height, matrix, true);
        } else {
            throw new NullPointerException("Inputted Bitmap is NULL");
        }
    }

    @Override
    public void onBackPressed() {
        //it disables the back button functionality
    }

    private BroadcastReceiver finishBackgroundCall = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NTF_Utils.handleBackgroundCallResponse(intent,SignHereActivity.this);
            NTF_Utils.hideProgressDialog();
        }
    };

    private BroadcastReceiver startBackgroundCall = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NTF_Utils.showProgressDialog(SignHereActivity.this);
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(startBackgroundCall,
                new IntentFilter(Extras_Keys.LOCAL_BROADCAST_START_ACTION));
        LocalBroadcastManager.getInstance(this).registerReceiver(finishBackgroundCall,
                new IntentFilter(Extras_Keys.LOCAL_BROADCAST_END_ACTION));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && !isCameraOpened){
            setCameraFunctionality();
        }
        updateButtonText();
    }

    private void stopCamera(){
        try {
            if (mCamera != null) {
                mCamera.stopPreview();
                mCameraPreview.getHolder().removeCallback(mCameraPreview);
                mCamera.release();
                mCamera = null;
                isCameraOpened = false;
            }
            if (mCameraPreview != null) {
                mFlCameraPreview.removeView(mCameraPreview);
                mCameraPreview = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(finishBackgroundCall);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(startBackgroundCall);
        stopCamera();
    }

    private void doLogoutPkg() {
        PackageLogoutDataRequest request = new PackageLogoutDataRequest();
        request.setSessionId(ntf_Preferences.get(NTF_Constants.Prefs_Keys.SESSION_ID));
        request.setAccountId(ntf_Preferences.get(NTF_Constants.Prefs_Keys.ACCOUNT_ID));
        request.setAuthenticationToken(ntf_Preferences.get(NTF_Constants.Prefs_Keys.AUTHENTICATION_TOKEN));
        request.setUserId(ntf_Preferences.get(Prefs_Keys.USER_ID));
        request.setPackageId(selectedPackages);
        request.setLogoutCodeId("10200");
        request.setStaffNote("");
        NTF_Utils.showProgressDialog(this);
        dataPresenter.logoutPkgData(null, request);
    }

    @Override
    public void onCredentialsValidated(String signature) {
        try {
            if (cameraEnabled && isCameraOpened){
                takeImageToSubmit() ;
            } else {
                doLogoutPkg();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEmptySignature() {
        errormessageFL.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDataSuccess() {
        try {
            PackageLogoutImagesRequest request = new PackageLogoutImagesRequest();
            request.setSessionId(ntf_Preferences.get(NTF_Constants.Prefs_Keys.SESSION_ID));
            request.setAuthenticationToken(ntf_Preferences.get(NTF_Constants.Prefs_Keys.AUTHENTICATION_TOKEN));
            request.setAccountId(ntf_Preferences.get(NTF_Constants.Prefs_Keys.ACCOUNT_ID));
            request.setUserId(ntf_Preferences.get(Prefs_Keys.USER_ID));
            request.setPackageId(selectedPackages);
            request.setEsignatureBase64(str_signature_base64string);
            request.setLogoutCodeId("10200");
            request.setRecipentId(residentid);
            if (cameraEnabled && isCameraOpened){
                List<PackagePicture> packagePictures = new ArrayList();
                PackagePicture packagePicture = new PackagePicture();
                packagePicture.setPictureData(BitmapHelper.getBase64String(mFrontImage));
                packagePictures.add(packagePicture);
                request.setPackagePictures(packagePictures);
            } else {
                request.setPackagePictures(null);
            }
            imagesPresenter.logoutPkgImages(null, request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDataWarning(String message) {
        NTF_Utils.hideProgressDialog();

        NTF_Utils.showAlert(this, ALERT_WARNING_TITLE, message, new Runnable() {
            @Override
            public void run() {
                onStartOverClicked();
            }
        });
    }

    @Override
    public void onSessionExpired(String message) {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showSessionExpireAlert(message, this, ntf_Preferences);
    }

    @Override
    public void onServerError() {
        NTF_Utils.hideProgressDialog();
    }

    @Override
    public Context getMvpContext() {
        return this;
    }

    @Override
    public void onError(Throwable throwable) {
        NTF_Utils.hideProgressDialog();
        if (cameraEnabled){
            setCameraFunctionality();
        }
        NTF_Utils.showAlert(this, "", NTF_Utils.getErrorMessage(throwable), null);
    }

    @Override
    public void onNoInternetConnection() {
        NTF_Utils.hideProgressDialog();
        NTF_Utils.showNoNetworkAlert(this);
    }

    @Override
    public void onErrorCode(String message) {
        NTF_Utils.hideProgressDialog();
        if (cameraEnabled){
            setCameraFunctionality();
        }
        NTF_Utils.showAlert(this, "", message, null);
    }

    @Override
    public void onImagesSuccess() {
        NTF_Utils.hideProgressDialog();
        startActivity(new Intent(this, KIOSKSuccessActivity.class));
        finish();
    }

    @Override
    public void onOtherResults(String message) {
        NTF_Utils.hideProgressDialog();
        if (cameraEnabled){
            setCameraFunctionality();
        }
        NTF_Utils.showAlert(this, ALERT_WARNING_TITLE, message, null);
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
