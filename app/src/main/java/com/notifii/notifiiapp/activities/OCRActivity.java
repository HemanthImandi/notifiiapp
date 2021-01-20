package com.notifii.notifiiapp.activities;

import androidx.core.app.ActivityCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.base.NTF_BaseActivity;
import com.notifii.notifiiapp.helpers.SingleTon;
import com.notifii.notifiiapp.models.Recipient;
import com.notifii.notifiiapp.models.SpinnerData;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;

public class OCRActivity extends NTF_BaseActivity {

    @BindView(R.id.surfaceView)
    SurfaceView surfaceView;

    Handler handler;
    Runnable runnable;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    String barcodeValue = "";
    String confirmedBarcode = "";
    long beforeTime = 0, afterTime = 0;
    boolean isBeep = false;
    long ocr_limit_time = 5000;
    BarcodeDetector barcodeDetector;
    Recipient mRecipient = null;
    TextRecognizer textRecognizer;
    private ArrayList<Recipient> mRecipientsList;
    private String ifSuccessDetectedText = "";
    private int threadscount = 0;
    private boolean isResultSent = false;
    String completeAlgorithmTime = "0.0";
    String ocrDetectedText = "";
    public static final String BARCODEVALUE = "barcodevalue";
    public static String OCR_RECIPIENT_BOUNCE_ALERT = "OCR failed to identify the recipient. Please try the OCR again or identify the recipient manually by typing in the name.";
    public static String OCR_TNUM_BOUNCE_ALERT = "OCR failed to identify the tracking number. Please try the OCR again or click on the barcode icon to manually scan the tracking number.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);
        ButterKnife.bind(this);
        mRecipientsList = SingleTon.getInstance().getmRecipientList();
        beforeTime = System.currentTimeMillis();
        textRecognizer = new TextRecognizer.Builder(OCRActivity.this).build();
        setTimer();
    }

    @OnClick(R.id.cancelTv)
    void onCancleClick(View view) {
        handler.removeCallbacks(runnable);
        finish();
    }

    public boolean isNumeric(String strNum) {
        try {
            return StringUtils.isNumeric(strNum);
        } catch (Exception e) {
            return false;
        }
    }

    private void initialiseDetectorsAndSources() {
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(OCRActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        Dexter.withContext(OCRActivity.this)
                                .withPermission(Manifest.permission.CAMERA)
                                .withListener(new PermissionListener() {
                                    @Override
                                    public void onPermissionGranted(PermissionGrantedResponse response) {
                                        try {
                                            SurfaceHolder holder1 = surfaceView.getHolder();
                                            if (ActivityCompat.checkSelfPermission(OCRActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                                return;
                                            }
                                            cameraSource.start(holder1);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onPermissionDenied(PermissionDeniedResponse response) {
                                        Toast.makeText(OCRActivity.this, R.string.camera_permission_not_granted, Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
                                }).check();
                        ActivityCompat.requestPermissions(OCRActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.CODE_128
                        | Barcode.DATA_MATRIX
                        | Barcode.TEXT
                        | Barcode.CODE_39
                        | Barcode.CODE_93
                        | Barcode.CODABAR
                        | Barcode.EAN_13
                        | Barcode.EAN_8
                        | Barcode.UPC_A
                        | Barcode.UPC_E
                        | Barcode.AZTEC
                        | Barcode.CONTACT_INFO
                        | Barcode.EMAIL
                        | Barcode.ISBN
                        | Barcode.PHONE
                        | Barcode.PRODUCT
                        | Barcode.SMS
                        | Barcode.URL
                        | Barcode.WIFI
                        | Barcode.GEO)
                .build();
        CapturingBarcodeDetector cameraDetector = new CapturingBarcodeDetector();
        if (!cameraDetector.isOperational()) {
            Log.w("Barcode Detector", "Detector dependencies are not yet available.");
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels * 3;
            int height = displayMetrics.heightPixels * 2;
            cameraSource = new CameraSource.Builder(this, cameraDetector)
                    .setRequestedPreviewSize(width, height)
                    .setAutoFocusEnabled(true)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedFps(30.0f)
                    .build();
        }
    }

    private void setTimer() {
        String s = "5.0";
        if (ntf_Preferences.hasKey(NTF_Constants.Prefs_Keys.OCR_TIME_LIMIT)) {
            s = ntf_Preferences.get(NTF_Constants.Prefs_Keys.OCR_TIME_LIMIT) != null ? ntf_Preferences.get(NTF_Constants.Prefs_Keys.OCR_TIME_LIMIT) : "5.0";
            if (!isNumeric(s)) {
                s = "5.0";
            }
        }
        double d = Double.valueOf(s);
        ocr_limit_time = (long) (d * 1000);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                Log.v("ISFROM :", "setTimer()");
                sendResult();
            }
        };
        handler.postDelayed(runnable, ocr_limit_time);
//        handler.postDelayed(runnable, 10000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraSource != null) {
            cameraSource.release();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }

    private void detectBarcode(Frame frame) {
        SparseArray<Barcode> barcodes = barcodeDetector.detect(frame);
        if (barcodes.size() > 0) {
            for (int i = barcodes.size() - 1; i >= 0; i--) {
                String value = barcodes.valueAt(i).displayValue.trim();
                String carrier = NTF_Utils.detect_shipping_carrier(value);
                if (!carrier.equals(NTF_Constants.Extras_Keys.OTHERS) && carrier.length() != 0) {
                    barcodeValue = value;
                }
            }
        }
    }

    private void handleMultipleBarcodes(Frame frame, ArrayList<String> detectedValues) {
        SparseArray<Barcode> barcodes = barcodeDetector.detect(frame);
        ArrayList<SpinnerData> trackingnumbers = new ArrayList<>();
        for (int i = 0; i < barcodes.size(); i++) {
            String carrier = NTF_Utils.detect_shipping_carrier(barcodes.valueAt(i).displayValue);
            if (!carrier.equals(NTF_Constants.Extras_Keys.OTHERS) && carrier.length() != 0) {
                SpinnerData spinnerData = new SpinnerData();
                spinnerData.setName(carrier);                                        //carrier-name
                spinnerData.setValue(barcodes.valueAt(i).displayValue);              //barcode-value
                trackingnumbers.add(spinnerData);
            }
        }
        if (trackingnumbers.size() == 1) {
            confirmedBarcode = trackingnumbers.get(0).getValue();
            barcodeValue = trackingnumbers.get(0).getValue();
            return;
        } else if (trackingnumbers.size() == 0) {
            return;
        }

        for (SpinnerData trackingNumber : trackingnumbers) {
            int trackingNumberLength = trackingNumber.getValue().length();
            for (String block : detectedValues) {
                block = block.replaceAll("tracking", "").replaceAll(":", "").replaceAll("#", "").replaceAll(" ", "");
                int blockLength = block.length();
                if (((((blockLength == 18 && block.toUpperCase().startsWith("1Z")) || blockLength == 11)) && (((trackingNumberLength == 18 && trackingNumber.getValue().toUpperCase().startsWith("1Z")) || trackingNumberLength == 11))) ||
                        ((trackingNumber.getValue().toUpperCase().startsWith("TB")) && (block.toUpperCase().startsWith("TB"))) ||
                        (((trackingNumberLength == 15 && StringUtils.isNumeric(trackingNumber.getValue())) ||
                                (trackingNumberLength == 12 && StringUtils.isNumeric(trackingNumber.getValue())) ||
                                (trackingNumberLength == 22 && trackingNumber.getValue().startsWith("96")) ||
                                (trackingNumberLength == 34 && StringUtils.isNumeric(trackingNumber.getValue()) && !trackingNumber.getValue().startsWith("42")) ||
                                (trackingNumberLength == 32 && StringUtils.isNumeric(trackingNumber.getValue()))) && ((blockLength == 15 && StringUtils.isNumeric(block)) ||
                                (blockLength == 12 && StringUtils.isNumeric(block)) ||
                                (blockLength == 22 && block.startsWith("96")) ||
                                (blockLength == 34 && StringUtils.isNumeric(block) && !block.startsWith("42")) ||
                                (blockLength == 32 && StringUtils.isNumeric(block)))) ||
                        ((trackingNumberLength == 10 && StringUtils.isNumeric(trackingNumber.getValue()) ||
                                trackingNumber.getValue().toUpperCase().startsWith("J")) && (blockLength == 10 && StringUtils.isNumeric(block) ||
                                block.toUpperCase().startsWith("J"))) ||
                        (((trackingNumberLength == 13 && trackingNumber.getValue().toUpperCase().endsWith("CA") ||
                                (trackingNumberLength == 16 && StringUtils.isNumeric(trackingNumber.getValue())))) && ((blockLength == 13 && block.toUpperCase().endsWith("CA") ||
                                (blockLength == 16 && StringUtils.isNumeric(block))))) ||
                        (((trackingNumberLength == 20 && StringUtils.isNumeric(trackingNumber.getValue())) ||
                                (trackingNumberLength == 26 && StringUtils.isNumeric(trackingNumber.getValue())) ||
                                (trackingNumberLength == 30 && StringUtils.isNumeric(trackingNumber.getValue())) ||
                                trackingNumberLength == 13 ||
                                (trackingNumberLength == 22 && trackingNumber.getValue().startsWith("9")) ||
                                (trackingNumberLength >= 29 && StringUtils.isNumeric(trackingNumber.getValue()) && trackingNumber.getValue().startsWith("42"))) && ((blockLength == 20 && StringUtils.isNumeric(block)) ||
                                (blockLength == 26 && StringUtils.isNumeric(block)) ||
                                (blockLength == 30 && StringUtils.isNumeric(block)) ||
                                blockLength == 13 ||
                                (blockLength == 22 && block.startsWith("9")) ||
                                (blockLength >= 29 && StringUtils.isNumeric(block) && block.startsWith("42")))) ||
                        ((trackingNumberLength == 15 && trackingNumber.getValue().toUpperCase().startsWith("C")) && (blockLength == 15 && block.toUpperCase().startsWith("C"))) ||
                        ((trackingNumber.getValue().toUpperCase().startsWith("G") ||
                                (trackingNumber.getValue().toUpperCase().startsWith("C1|")) ||
                                (trackingNumberLength == 9 && StringUtils.isNumeric(trackingNumber.getValue())) ||
                                (trackingNumberLength == 16 && String.valueOf(trackingNumber.getValue().charAt(7)).equals("-")))
                                && (block.toUpperCase().startsWith("G") ||
                                (block.toUpperCase().startsWith("C1|")) ||
                                (blockLength == 9 && StringUtils.isNumeric(block)) ||
                                (blockLength == 16 && String.valueOf(block.charAt(7)).equals("-")))) ||
                        ((trackingNumberLength == 10 && trackingNumber.getValue().toUpperCase().startsWith("L")) && (blockLength == 10 && block.toUpperCase().startsWith("L")))) {
                    confirmedBarcode = trackingNumber.getValue();
                    barcodeValue = trackingNumber.getValue();
                }
            }
        }
    }

    private void setBitmap(Frame frame) {
        try {
            YuvImage yuvImage = new YuvImage(frame.getGrayscaleImageData().array(), ImageFormat.NV21, frame.getMetadata().getWidth(), frame.getMetadata().getHeight(), null);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            yuvImage.compressToJpeg(new Rect(0, 0, frame.getMetadata().getWidth(), frame.getMetadata().getHeight()), 100, byteArrayOutputStream);
            byte[] jpegArray = byteArrayOutputStream.toByteArray();
            final Bitmap TempBitmap = BitmapFactory.decodeByteArray(jpegArray, 0, jpegArray.length);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            final Bitmap bitmap = Bitmap.createBitmap(TempBitmap, 0, 0, TempBitmap.getWidth(), TempBitmap.getHeight(), matrix, true);
            SingleTon.getInstance().setBitmap1(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class CapturingBarcodeDetector extends Detector<Barcode> {
        @Override
        public SparseArray<Barcode> detect(Frame frame) {
            if (isResultSent)
                return null;
            if (threadscount < 2) {
                threadscount = threadscount + 1;
                new OcrThread(frame).execute();
            }
            return null;
        }
    }

    private synchronized void sendResult() {
        isResultSent = true;
        afterTime = System.currentTimeMillis();
        String actualTime = String.valueOf((afterTime - beforeTime) / 1000d);
        SingleTon.getInstance().setRecipient(mRecipient);
        Intent i = new Intent();
        i.putExtra(NTF_Constants.Extras_Keys.DETECTED_TEXT, !ifSuccessDetectedText.isEmpty() ? ifSuccessDetectedText : ocrDetectedText);
        i.putExtra(BARCODEVALUE, barcodeValue);
        i.putExtra(Extras_Keys.OCR_TIME, actualTime);
        i.putExtra(Extras_Keys.ALGORITHM_TIME, completeAlgorithmTime);
        setResult(RESULT_OK, i);
        if (!isBeep) {
            isBeep = true;
            NTF_Utils.getBeepSound(getApplicationContext());
        }
        finish();
    }

    class OcrThread extends AsyncTask<Void, Void, Void> {

        private Frame frame;
        private String fullDetectedText = "";
        private String addressText = "";
        private String nameText = "";
        private ArrayList<String> detectedValues;
        long algorithmBeginTime = 0, algorithmFinishedTime = 0;

        OcrThread(Frame frame) {
            this.frame = frame;
            detectedValues = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                findRecipient(frame);
                if (barcodeDetector.detect(frame).size() > 1 && confirmedBarcode.isEmpty()) {
                    handleMultipleBarcodes(frame, detectedValues);
                } else if (barcodeValue.isEmpty()) {
                    detectBarcode(frame);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            threadscount = threadscount - 1;
            if (isResultSent)
                return;
            if (mRecipient != null && !barcodeValue.isEmpty()) {
                handler.removeCallbacks(runnable);
                setBitmap(frame);
                sendResult();
            }
            super.onPostExecute(aVoid);
        }

        private void findRecipient(Frame frame) {
            try {

                algorithmBeginTime = System.currentTimeMillis();
                SparseArray<TextBlock> textBlock = textRecognizer.detect(frame);
//            if (textBlock == null || textBlock.size() == 0){
//                return;
//            }
                for (int i = 0; i < textBlock.size(); i++) {
                    TextBlock txtBlock = textBlock.valueAt(i);
                    for (Text currentText : txtBlock.getComponents()) {
                        if (currentText != null && currentText.getValue() != null) {
                            detectedValues.add(currentText.getValue().toLowerCase());
                            fullDetectedText = fullDetectedText + currentText.getValue().toLowerCase();
                            if (!currentText.getValue().matches(".*\\d+.*")) {
                                nameText = nameText + currentText.getValue().toLowerCase();
                            } else {
                                addressText = addressText + currentText.getValue().toLowerCase();
                            }
                        }
                    }
                }
                ocrDetectedText = fullDetectedText;
                if (fullDetectedText.length() < 120) {
                    algorithmBeginTime = System.currentTimeMillis();
                    algorithmFinishedTime = System.currentTimeMillis();
                    completeAlgorithmTime = String.valueOf((algorithmFinishedTime - algorithmBeginTime) / 1000d);
                    return;
                }
//                showToast("Barcode value: " + barcodeValue );
//                showToast("Name text:" + nameText);
//                showToast("Address text:" + addressText);
//                showToast("Full text:" + fullDetectedText);

                //  ABC Resident 1  A1

                if (barcodeValue.isEmpty()) {
                    for (String block : detectedValues) {
                        if (block != null && block.length() > 4 && block.startsWith("tba") && StringUtils.isNumeric(block.substring(3))) {
                            barcodeValue = block.toUpperCase();
                            break;
                        }
                    }
                }
                if (mRecipient != null) {
                    return;
                }
                fullDetectedText = fullDetectedText.replaceAll("ship to:", "").replaceAll("to:", "")
                        .replaceAll("to ", "");
                ListIterator<Recipient> iterator = mRecipientsList.listIterator();
                ArrayList<Recipient> fullNameMatchedList = new ArrayList<>();
                if (iterator.hasNext()) {
                    do {
                        Recipient recipient = iterator.next();
                        if (nameText.contains(recipient.getFullName().toLowerCase()) && addressText.toLowerCase().contains(recipient.getAddress1().toLowerCase())) {
                            ifSuccessDetectedText = fullDetectedText;
                            mRecipient = recipient;
                            algorithmFinishedTime = System.currentTimeMillis();
                            completeAlgorithmTime = String.valueOf((algorithmFinishedTime - algorithmBeginTime) / 1000d);
//                            showToast("Founded1: "+recipient.getFullName());
                            return;
                        } else if (nameText.contains(recipient.getFullName().toLowerCase())) {
//                            showToast("Founded2: "+recipient.getFullName());
                            fullNameMatchedList.add(recipient);
                        }
                    } while (iterator.hasNext());
                }
//                showToast("List size: " + mRecipientsList.size());
            /*for (Recipient recipient : mRecipientsList) {
                if (nameText.contains(recipient.getFullName().toLowerCase()) && addressText.toLowerCase().contains(recipient.getAddress1().toLowerCase())) {
                    ifSuccessDetectedText = fullDetectedText;
                    mRecipient = recipient;
                    algorithmFinishedTime = System.currentTimeMillis();
                    completeAlgorithmTime = String.valueOf((algorithmFinishedTime - algorithmBeginTime) / 1000d);
                    return;
                } else if (nameText.contains(recipient.getFullName().toLowerCase())) {
                    fullNameMatchedList.add(recipient);
                }
            }*/
//                showToast("matched list size: " + fullNameMatchedList.size());

                if (fullNameMatchedList.size() == 1) {
                    ifSuccessDetectedText = fullDetectedText;
                    mRecipient = fullNameMatchedList.get(0);
                    algorithmFinishedTime = System.currentTimeMillis();
                    completeAlgorithmTime = String.valueOf((algorithmFinishedTime - algorithmBeginTime) / 1000d);
                    return;
                }
                algorithmFinishedTime = System.currentTimeMillis();
                completeAlgorithmTime = String.valueOf((algorithmFinishedTime - algorithmBeginTime) / 1000d);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}