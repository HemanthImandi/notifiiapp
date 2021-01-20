package com.notifii.notifiiapp.activities;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.manateeworks.BarcodeScanner;
import com.manateeworks.BarcodeScanner.MWResult;
import com.manateeworks.CameraManager;
import com.manateeworks.MWOverlay;
import com.manateeworks.MWParser;
import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.fragments.LogPackageInFragment;
import com.notifii.notifiiapp.receivers.OrientationManager;
import com.notifii.notifiiapp.utils.NTF_Constants;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * The barcode reader activity itself. This is loosely based on the
 * CameraPreview example included in the Android SDK.
 */
public class ManateeBarcodeScannerActivity extends Activity implements SurfaceHolder.Callback, OrientationManager.OrientationListener {

    public static final boolean PDF_OPTIMIZED = false;
    private MediaPlayer player;
    public static final int PARSER_MASK = MWParser.MWP_PARSER_MASK_NONE;
    public static final String KEY_SCAN_RESULT="scan_result";
    public static final int BARCODE_REQUEST_CODE =444;
    public static boolean isQrAlso=false;

    public static final int USE_RESULT_TYPE = BarcodeScanner.MWB_RESULT_TYPE_MW;

    public static final OverlayMode OVERLAY_MODE = OverlayMode.OM_MWOVERLAY;

    // !!! Rects are in format: x, y, width, height !!!
    public static final Rect RECT_LANDSCAPE_1D = new Rect(3, 20, 94, 60);
    public static final Rect RECT_LANDSCAPE_2D = new Rect(20, 5, 60, 90);
    public static final Rect RECT_PORTRAIT_1D = new Rect(20, 3, 60, 94);
    public static final Rect RECT_PORTRAIT_2D = new Rect(20, 5, 60, 90);
    public static final Rect RECT_FULL_1D = new Rect(3, 3, 94, 94);
    public static final Rect RECT_FULL_2D = new Rect(20, 5, 60, 90);
    public static final Rect RECT_DOTCODE = new Rect(30, 20, 40, 60);

    private static final String MSG_CAMERA_FRAMEWORK_BUG = "Sorry, the Android camera encountered a problem: ";

    public static final int ID_AUTO_FOCUS = 0x01;
    public static final int ID_DECODE = 0x02;
    public static final int ID_RESTART_PREVIEW = 0x04;
    public static final int ID_DECODE_SUCCEED = 0x08;
    public static final int ID_DECODE_FAILED = 0x10;

    private Handler decodeHandler;
    private boolean hasSurface;
    private String package_name;
    private int activeThreads = 0;
    public static int MAX_THREADS = Runtime.getRuntime().availableProcessors();
    private ImageButton zoomButton;
    private ImageButton buttonFlash;
    boolean flashOn = false;
    private int zoomLevel = 0;
    private int firstZoom = 150;
    private int secondZoom = 300;
    private SurfaceHolder surfaceHolder;
    private ImageView imageOverlay;

    @Override
    public void onOrientationChange(OrientationManager.ScreenOrientation screenOrientation) {
        switch (screenOrientation) {
            case PORTRAIT:
                Rect RECT_FULL_1D = new Rect(20, 0, 45, 100);
                Rect RECT_FULL_2D = new Rect(20, 0, 45, 100);
                setRectangleSize(RECT_FULL_1D, RECT_FULL_2D);
                break;
            case LANDSCAPE:
                Rect RECT_FULL_1 = new Rect(5, 0, 85, 100);
                Rect RECT_FULL_2 = new Rect(5, 0, 85, 100);
                setRectangleSize(RECT_FULL_1, RECT_FULL_2);
                break;
        }
    }

    private enum State {
        STOPPED, PREVIEW, DECODING
    }

    private enum OverlayMode {
        OM_IMAGE, OM_MWOVERLAY, OM_NONE
    }

    State state = State.STOPPED;

    public Handler getHandler() {
        return decodeHandler;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        try {
            package_name = getPackageName();
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
            boolean isDeviceMobile = getResources().getBoolean(R.bool.isDeviceMobile);
            Rect RECT_FULL_1D, RECT_FULL_2D;
            if (isDeviceMobile) {
                RECT_FULL_1D = new Rect(20, 0, 45, 100);
                RECT_FULL_2D = new Rect(20, 0, 45, 100);
            } else if (tabletSize) {
                RECT_FULL_1D = new Rect(0, 15, 90, 70);
                RECT_FULL_2D = new Rect(5, 20, 95, 70);
                Display display = getWindowManager().getDefaultDisplay();
                if (!getResources().getString(R.string.screen).equalsIgnoreCase("mobile")) {
                    RECT_FULL_1D = new Rect(20, 3, 60, 94);
                    RECT_FULL_2D = new Rect(20, 5, 60, 90);
                }
            } else {
                RECT_FULL_1D = new Rect(30, 0, 40, 100);
                RECT_FULL_2D = new Rect(30, 0, 40, 100);
            }
            setContentView(R.layout.capture);
            (findViewById(R.id.mobile_left_linear)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.textViewActionTitle)).setText("Scan");
            ImageView cancelButton = (ImageView) findViewById(R.id.actionImageRight);
            cancelButton.setVisibility(View.GONE);
            TextView cancelText = (TextView) findViewById(R.id.action_right_text);
            cancelText.setVisibility(View.VISIBLE);
            cancelText.setText("Cancel");
            cancelText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ManateeBarcodeScannerActivity.this.finish();
                }
            });
            if (PDF_OPTIMIZED) {
                BarcodeScanner.MWBsetDirection(BarcodeScanner.MWB_SCANDIRECTION_HORIZONTAL);
                BarcodeScanner.MWBsetActiveCodes(BarcodeScanner.MWB_CODE_MASK_PDF);
                BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_PDF, RECT_LANDSCAPE_1D);
            } else {
                BarcodeScanner.MWBsetDirection(BarcodeScanner.MWB_SCANDIRECTION_HORIZONTAL | BarcodeScanner.MWB_SCANDIRECTION_VERTICAL);
                if (isQrAlso){
                    BarcodeScanner.MWBsetActiveCodes(
                                 BarcodeScanner.MWB_CODE_MASK_QR|            //include qr
                                    BarcodeScanner.MWB_CODE_MASK_25 |
                                    BarcodeScanner.MWB_CODE_MASK_39 |
                                    BarcodeScanner.MWB_CODE_MASK_93 |
                                    BarcodeScanner.MWB_CODE_MASK_128 |
                                    BarcodeScanner.MWB_CODE_MASK_AZTEC |
                                    BarcodeScanner.MWB_CODE_MASK_DM |
                                    BarcodeScanner.MWB_CODE_MASK_EANUPC |
                                    BarcodeScanner.MWB_CODE_MASK_PDF |
                                    BarcodeScanner.MWB_CODE_MASK_CODABAR |
                                    BarcodeScanner.MWB_CODE_MASK_11 |
                                    BarcodeScanner.MWB_CODE_MASK_MSI |
                                         BarcodeScanner.MWB_CODE_MASK_RSS |
                                         BarcodeScanner.MWB_CODE_MASK_MAXICODE
                    );
                }else {
                    BarcodeScanner.MWBsetActiveCodes(
                                    BarcodeScanner.MWB_CODE_MASK_25 |
                                    BarcodeScanner.MWB_CODE_MASK_39 |
                                    BarcodeScanner.MWB_CODE_MASK_93 |
                                    BarcodeScanner.MWB_CODE_MASK_128 |
                                    BarcodeScanner.MWB_CODE_MASK_AZTEC |
                                    BarcodeScanner.MWB_CODE_MASK_DM |
                                    BarcodeScanner.MWB_CODE_MASK_EANUPC |
                                    BarcodeScanner.MWB_CODE_MASK_PDF |
                                    BarcodeScanner.MWB_CODE_MASK_CODABAR |
                                    BarcodeScanner.MWB_CODE_MASK_11 |
                                    BarcodeScanner.MWB_CODE_MASK_MSI |
                                    BarcodeScanner.MWB_CODE_MASK_RSS |
                                    BarcodeScanner.MWB_CODE_MASK_MAXICODE
                    );
                }


                int registerResult = BarcodeScanner.MWBregisterSDK("D4ZUxUo0Qlq2S2OoVms5i/otW4vjdzRqNaP9USXd9p4=", this);

                switch (registerResult) {
                    case BarcodeScanner.MWB_RTREG_OK:
                        Log.i("MWBregisterSDK", "Registration OK");
                        break;
                    case BarcodeScanner.MWB_RTREG_INVALID_KEY:
                        Log.e("MWBregisterSDK", "Registration Invalid Key");
                        break;
                    case BarcodeScanner.MWB_RTREG_INVALID_CHECKSUM:
                        Log.e("MWBregisterSDK", "Registration Invalid Checksum");
                        break;
                    case BarcodeScanner.MWB_RTREG_INVALID_APPLICATION:
                        Log.e("MWBregisterSDK", "Registration Invalid Application");
                        break;
                    case BarcodeScanner.MWB_RTREG_INVALID_SDK_VERSION:
                        Log.e("MWBregisterSDK", "Registration Invalid SDK Version");
                        break;
                    case BarcodeScanner.MWB_RTREG_INVALID_KEY_VERSION:
                        Log.e("MWBregisterSDK", "Registration Invalid Key Version");
                        break;
                    case BarcodeScanner.MWB_RTREG_INVALID_PLATFORM:
                        Log.e("MWBregisterSDK", "Registration Invalid Platform");
                        break;
                    case BarcodeScanner.MWB_RTREG_KEY_EXPIRED:
                        Log.e("MWBregisterSDK", "Registration Key Expired");
                        break;

                    default:
                        Log.e("MWBregisterSDK", "Registration Unknown Error");
                        break;
                }

                setRectangleSize(RECT_FULL_1D, RECT_FULL_2D);
            }

            if (OVERLAY_MODE == OverlayMode.OM_IMAGE) {
                imageOverlay = (ImageView) findViewById(R.id.imageOverlay);
                imageOverlay.setVisibility(View.VISIBLE);
            }
            BarcodeScanner.MWBsetLevel(2);
            BarcodeScanner.MWBsetResultType(USE_RESULT_TYPE);
            // Set minimum result length for low-protected barcode types
            BarcodeScanner.MWBsetMinLength(BarcodeScanner.MWB_CODE_MASK_25, 5);
            BarcodeScanner.MWBsetMinLength(BarcodeScanner.MWB_CODE_MASK_MSI, 5);
            BarcodeScanner.MWBsetMinLength(BarcodeScanner.MWB_CODE_MASK_39, 5);
            BarcodeScanner.MWBsetMinLength(BarcodeScanner.MWB_CODE_MASK_CODABAR, 5);
            BarcodeScanner.MWBsetMinLength(BarcodeScanner.MWB_CODE_MASK_11, 5);
            CameraManager.init(getApplication());

            hasSurface = false;
            state = State.STOPPED;
            decodeHandler = new Handler(new Handler.Callback() {

                @Override
                public boolean handleMessage(Message msg) {
                    // TODO Auto-generated method stub

                    switch (msg.what) {
                        case ID_DECODE:
                            try {
                                decode((byte[]) msg.obj, msg.arg1, msg.arg2);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;

                        case ID_AUTO_FOCUS:
                            if (state == State.PREVIEW || state == State.DECODING) {
                                CameraManager.get().requestAutoFocus(decodeHandler, ID_AUTO_FOCUS);
                            }
                            break;
                        case ID_RESTART_PREVIEW:
                            restartPreviewAndDecode();
                            break;
                        case ID_DECODE_SUCCEED:
                            state = State.STOPPED;
                            handleDecode((MWResult) msg.obj);
                            break;
                        case ID_DECODE_FAILED:
                            break;

                    }

                    return false;
                }
            });

            zoomButton = (ImageButton) findViewById(R.id.zoomButton);

            zoomButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    zoomLevel++;
                    if (zoomLevel > 2) {
                        zoomLevel = 0;
                    }

                    switch (zoomLevel) {
                        case 0:
                            CameraManager.get().setZoom(100);
                            break;
                        case 1:
                            CameraManager.get().setZoom(firstZoom);
                            break;
                        case 2:
                            CameraManager.get().setZoom(secondZoom);
                            break;

                        default:
                            break;
                    }
                }
            });
            buttonFlash = (ImageButton) findViewById(R.id.flashButton);
            buttonFlash.setOnClickListener(new OnClickListener() {
                // @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                @Override
                public void onClick(View v) {
                    toggleFlash();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setRectangleSize(Rect RECT_FULL_1D, Rect RECT_FULL_2D) {
        if(isQrAlso){
            BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_QR, RECT_FULL_1D);
        }
        BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_25, RECT_FULL_1D);
        BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_39, RECT_FULL_1D);
        BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_93, RECT_FULL_1D);
        BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_128, RECT_FULL_1D);
        BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_AZTEC, RECT_FULL_2D);
        BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_DM, RECT_FULL_2D);
        BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_EANUPC, RECT_FULL_1D);
        BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_PDF, RECT_FULL_1D);
        BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_RSS, RECT_FULL_1D);
        BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_CODABAR, RECT_FULL_1D);
        BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_DOTCODE, RECT_DOTCODE);
        BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_11, RECT_FULL_1D);
        BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_MSI, RECT_FULL_1D);
        BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_MAXICODE, RECT_FULL_2D);

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            SurfaceView surfaceView = (SurfaceView) findViewById(getResources().getIdentifier("preview_view", "id", package_name));
            surfaceHolder = surfaceView.getHolder();
            recycleOverlayImage();
            if (OVERLAY_MODE == OverlayMode.OM_MWOVERLAY) {
                MWOverlay.addOverlay(this, surfaceView);
            }
            if (hasSurface) {
                Log.i("Init Camera", "On resume");
                initCamera();
                // }
            } else {
                surfaceHolder.addCallback(this);
                surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void recycleOverlayImage() {
        imageOverlay = (ImageView) findViewById(R.id.imageOverlay);
        if (OVERLAY_MODE == OverlayMode.OM_IMAGE) {
            ((ImageView) imageOverlay.findViewById(R.id.imageOverlay)).setImageDrawable(getResources().getDrawable(R.drawable.overlay));
        } else {
            Drawable imageDrawable = imageOverlay.getDrawable();
            imageOverlay.setImageDrawable(null);
            if (imageDrawable != null && imageDrawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = ((BitmapDrawable) imageDrawable);

                if (!bitmapDrawable.getBitmap().isRecycled()) {
                    bitmapDrawable.getBitmap().recycle();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (OVERLAY_MODE == OverlayMode.OM_MWOVERLAY) {
                MWOverlay.removeOverlay();
            }
            imageOverlay.setImageDrawable(null);
            CameraManager.get().stopPreview();
            CameraManager.get().closeDriver();
            state = State.STOPPED;
            flashOn = false;
            updateFlash();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();
        CameraManager.get().updateCameraOrientation(rotation);
        super.onConfigurationChanged(config);
    }

    private void toggleFlash() {
        flashOn = !flashOn;
        updateFlash();
    }

    private void updateFlash() {
        if (!CameraManager.get().isTorchAvailable()) {
            buttonFlash.setVisibility(View.GONE);
            return;
        } else {
            buttonFlash.setVisibility(View.VISIBLE);
        }
        CameraManager.get().setTorch(flashOn);
        buttonFlash.postInvalidate();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i("Init Camera", "On Surface changed");
        initCamera();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_FOCUS || keyCode == KeyEvent.KEYCODE_CAMERA) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void decode(final byte[] data, final int width, final int height) {
        if (activeThreads >= MAX_THREADS || state == State.STOPPED) {
            return;
        }
        new Thread(new Runnable() {
            public void run() {
                activeThreads++;
                byte[] rawResult = null;
                rawResult = BarcodeScanner.MWBscanGrayscaleImage(data, width, height);

                if (state == State.STOPPED) {
                    activeThreads--;
                    return;
                }

                MWResult mwResult = null;

                if (rawResult != null && BarcodeScanner.MWBgetResultType() == BarcodeScanner.MWB_RESULT_TYPE_MW) {

                    BarcodeScanner.MWResults results = new BarcodeScanner.MWResults(rawResult);

                    if (results.count > 0) {
                        mwResult = results.getResult(0);
                        rawResult = mwResult.bytes;
                    }

                } else if (rawResult != null && BarcodeScanner.MWBgetResultType() == BarcodeScanner.MWB_RESULT_TYPE_RAW) {
                    mwResult = new BarcodeScanner.MWResult();
                    mwResult.bytes = rawResult;
                    mwResult.text = rawResult.toString();
                    mwResult.type = BarcodeScanner.MWBgetLastType();
                    mwResult.bytesLength = rawResult.length;
                }

                if (mwResult != null) {
                    Log.i("Found Result", "Something");
                    state = State.STOPPED;

                    Message message = Message.obtain(ManateeBarcodeScannerActivity.this.getHandler(), ID_DECODE_SUCCEED, mwResult);

                    message.arg1 = mwResult.type;

                    message.sendToTarget();

                } else {
                    Message message = Message.obtain(ManateeBarcodeScannerActivity.this.getHandler(), ID_DECODE_FAILED);
                    message.sendToTarget();
                }

                activeThreads--;
            }
        }).start();
    }

    private void restartPreviewAndDecode() {
        if (state == State.STOPPED) {
            state = State.PREVIEW;
            Log.i("preview", "requestPreviewFrame.");
            CameraManager.get().requestPreviewFrame(getHandler(), ID_DECODE);
            CameraManager.get().requestAutoFocus(getHandler(), ID_AUTO_FOCUS);
        }
    }

    public void handleDecode(MWResult result) {
        try {
            byte[] rawResult = null;
            if (result != null && result.bytes != null) {
                rawResult = result.bytes;
            }

            String barcode = "";

            if (PARSER_MASK != MWParser.MWP_PARSER_MASK_NONE && BarcodeScanner.MWBgetResultType() == BarcodeScanner.MWB_RESULT_TYPE_MW
                    && !(PARSER_MASK == MWParser.MWP_PARSER_MASK_GS1 && !result.isGS1)) {

                barcode = MWParser.MWPgetJSON(PARSER_MASK, result.encryptedResult.getBytes());
                if (barcode == null) {
                    String parserMask = "parser";
                    switch (PARSER_MASK) {
                        case MWParser.MWP_PARSER_MASK_AAMVA:
                            parserMask = "AAMVA";
                            break;
                        case MWParser.MWP_PARSER_MASK_ISBT:
                            parserMask = "ISBT";
                            break;
                        case MWParser.MWP_PARSER_MASK_IUID:
                            parserMask = "IUID";
                            break;
                        case MWParser.MWP_PARSER_MASK_HIBC:
                            parserMask = "HIBC";
                            break;
                        case MWParser.MWP_PARSER_MASK_GS1:
                            parserMask = "GS1";
                            break;
                        case MWParser.MWP_PARSER_MASK_SCM:
                            parserMask = "SCM";
                            break;
                        default:
                            break;
                    }
                    barcode = result.text + "\n*Not a valid " + parserMask + " formatted barcode";
                }
            } else {
                //barcode = result.text;
                try {
                    barcode = new String(rawResult, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    barcode = "";
                    for (int i = 0; i < rawResult.length; i++)
                        barcode = barcode + (char) rawResult[i];
                    e.printStackTrace();
                }
            }

            int bcType = result.type;
            /*String typeName = "";
            switch (bcType) {
                case BarcodeScanner.FOUND_25_INTERLEAVED:
                    typeName = "Code 25 Interleaved";
                    break;
                case BarcodeScanner.FOUND_25_STANDARD:
                    typeName = "Code 25 Standard";
                    break;
                case BarcodeScanner.FOUND_128:
                    typeName = "Code 128";
                    break;
                case BarcodeScanner.FOUND_39:
                    typeName = "Code 39";
                    break;
                case BarcodeScanner.FOUND_93:
                    typeName = "Code 93";
                    break;
                case BarcodeScanner.FOUND_AZTEC:
                    typeName = "AZTEC";
                    break;
                case BarcodeScanner.FOUND_DM:
                    typeName = "Datamatrix";
                    break;
                case BarcodeScanner.FOUND_EAN_13:
                    typeName = "EAN 13";
                    break;
                case BarcodeScanner.FOUND_EAN_8:
                    typeName = "EAN 8";
                    break;
                case BarcodeScanner.FOUND_NONE:
                    typeName = "None";
                    break;
                case BarcodeScanner.FOUND_RSS_14:
                    typeName = "Databar 14";
                    break;
                case BarcodeScanner.FOUND_RSS_14_STACK:
                    typeName = "Databar 14 Stacked";
                    break;
                case BarcodeScanner.FOUND_RSS_EXP:
                    typeName = "Databar Expanded";
                    break;
                case BarcodeScanner.FOUND_RSS_LIM:
                    typeName = "Databar Limited";
                    break;
                case BarcodeScanner.FOUND_UPC_A:
                    typeName = "UPC A";
                    break;
                case BarcodeScanner.FOUND_UPC_E:
                    typeName = "UPC E";
                    break;
                case BarcodeScanner.FOUND_PDF:
                    typeName = "PDF417";
                    break;
                case BarcodeScanner.FOUND_QR:
                    typeName = "QR";
                    break;
                case BarcodeScanner.FOUND_CODABAR:
                    typeName = "Codabar";
                    break;
                case BarcodeScanner.FOUND_128_GS1:
                    typeName = "Code 128 GS1";
                    break;
                case BarcodeScanner.FOUND_ITF14:
                    typeName = "ITF 14";
                    break;
                case BarcodeScanner.FOUND_11:
                    typeName = "Code 11";
                    break;
                case BarcodeScanner.FOUND_MSI:
                    typeName = "MSI Plessey";
                    break;
                case BarcodeScanner.FOUND_25_IATA:
                    typeName = "IATA Code 25";
                    break;
            }*/

            if (result.locationPoints != null && CameraManager.get().getCurrentResolution() != null && OVERLAY_MODE == OverlayMode.OM_MWOVERLAY) {
                // MWOverlay.showLocation(result.locationPoints.points, result.imageWidth, result.imageHeight);
            }

            if (result.isGS1) {
                result.typeName += " (GS1)";
            }

            if (bcType >= 0) {
                if (bcType == BarcodeScanner.FOUND_PDF) {
                    onResume();
                    return;
                }
                if (barcode.length() > 0 && bcType != BarcodeScanner.FOUND_PDF) {
                    try {
                        AssetFileDescriptor afd = getApplicationContext().getAssets().openFd("beep.mp3");
                        player = new MediaPlayer();
                        player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                        afd.close();
                        player.prepare();
                        player.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    barcode = barcode.replaceAll("[\u0000-\u001f]", "");
                    /*new AlertDialog.Builder(ManateeBarcodeScannerActivity.this)
                            .setTitle(typeName).setMessage(barcode).setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                decodeHandler.sendEmptyMessage(ID_RESTART_PREVIEW);
                                return false;
                            }
                            return false;
                        }
                    }).setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (decodeHandler != null) {
                                decodeHandler.sendEmptyMessage(ID_RESTART_PREVIEW);
                            }
                        }
                    }).show();*/
                    //  new Handler().postDelayed(new Runnable() {
                    //     @Override
                    //     public void run() {
                    Intent intent = new Intent();
                    intent.putExtra(KEY_SCAN_RESULT, barcode.trim());
                    setResult(RESULT_OK, intent);
                    finish();
                    //    }
                    //   }, 300);
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Error");
                builder.setMessage("Unable to read");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
            }

            /*new AlertDialog.Builder(this).setTitle(result.typeName).setMessage(barcode).setOnKeyListener(new OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        decodeHandler.sendEmptyMessage(ID_RESTART_PREVIEW);
                        return false;
                    }
                    return false;
                }

            }).setNegativeButton("Close", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (decodeHandler != null) {
                        decodeHandler.sendEmptyMessage(ID_RESTART_PREVIEW);
                    }

                }
            })

                    .show();
*/
           /* if (USE_MWANALYTICS) {
                MWBAnalytics.MWB_sendReport(result.encryptedResult, result.typeName, ANALYTICS_TAG);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void initCamera() {
        if (!isFinishing()) {

            try {
                // Select desired camera resoloution. Not all devices
                // supports all
                // resolutions, closest available will be chosen
                // If not selected, closest match to screen resolution will
                // be
                // chosen
                // High resolutions will slow down scanning proccess on
                // slower
                // devices

                if (MAX_THREADS > 2 || PDF_OPTIMIZED) {
                    CameraManager.setDesiredPreviewSize(1280, 720);
                } else {
                    CameraManager.setDesiredPreviewSize(800, 480);
                }

                CameraManager.get().openDriver(surfaceHolder, (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT));

                int maxZoom = CameraManager.get().getMaxZoom();
                if (maxZoom < 100) {
                    zoomButton.setVisibility(View.GONE);
                } else {
                    zoomButton.setVisibility(View.VISIBLE);
                    if (maxZoom < 300) {
                        secondZoom = maxZoom;
                        firstZoom = (maxZoom - 100) / 2 + 100;
                    }
                }
            } catch (IOException ioe) {
                displayFrameworkBugMessageAndExit(ioe.getMessage());
                return;
            } catch (RuntimeException e) {
                // Barcode Scanner has seen crashes in the wild of this
                // variety:
                // java.?lang.?RuntimeException: Fail to connect to camera
                // service
                displayFrameworkBugMessageAndExit(e.getMessage());
                return;
            } catch (Exception e) {
                displayFrameworkBugMessageAndExit(e.getMessage());
                return;
            }
            Log.i("preview", "start preview.");

            flashOn = false;

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    switch (zoomLevel) {
                        case 0:
                            CameraManager.get().setZoom(100);
                            break;
                        case 1:
                            CameraManager.get().setZoom(firstZoom);
                            break;
                        case 2:
                            CameraManager.get().setZoom(secondZoom);
                            break;

                        default:
                            break;
                    }

                }
            }, 300);

            //Fix for camera sensor rotation bug
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(CameraManager.USE_FRONT_CAMERA ? 1 : 0, cameraInfo);
            if (cameraInfo.orientation == 270) {
                BarcodeScanner.MWBsetFlags(0, BarcodeScanner.MWB_CFG_GLOBAL_ROTATE180);
            }

            CameraManager.get().startPreview();
            restartPreviewAndDecode();
            updateFlash();
        }

    }

    private void displayFrameworkBugMessageAndExit(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getIdentifier("app_name", "string", package_name));
        builder.setMessage(MSG_CAMERA_FRAMEWORK_BUG + message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.show();
    }

    /*private void setDetector(){
        if (isQrAlso){
            barcodeDetector = new BarcodeDetector.Builder(this)
                    .setBarcodeFormats(Barcode.CODE_128

                            | Barcode.QR_CODE                       //for qr code
                            | Barcode.PDF417

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
        } else {
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
        }
    }*/

}

/*
package com.notifii.notifiiapp.activities;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.manateeworks.BarcodeScanner;
import com.manateeworks.BarcodeScanner.MWResult;
import com.manateeworks.CameraManager;
import com.manateeworks.MWOverlay;
import com.manateeworks.MWParser;
import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.receivers.OrientationManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ManateeBarcodeScannerActivity extends Activity implements SurfaceHolder.Callback, OrientationManager.OrientationListener {

    @BindView(R.id.mobile_left_linear)
    View view;
    @BindView(R.id.actionImageRight)
    ImageView cancelButton;
    @BindView(R.id.action_right_text)
    TextView cancelText;
    @BindView(R.id.imageOverlay)
    ImageView imageOverlay;
    @BindView(R.id.zoomButton)
    ImageButton zoomButton;
    @BindView(R.id.textViewActionTitle)
    TextView textViewActionTitle;
    @BindView(R.id.flashButton)
    ImageButton buttonFlash;


    public static final boolean PDF_OPTIMIZED = false;
    private MediaPlayer player;
    public static final int PARSER_MASK = MWParser.MWP_PARSER_MASK_NONE;
    public static final int USE_RESULT_TYPE = BarcodeScanner.MWB_RESULT_TYPE_MW;
    public static final OverlayMode OVERLAY_MODE = OverlayMode.OM_MWOVERLAY;
    public static final Rect RECT_LANDSCAPE_1D = new Rect(3, 20, 94, 60);
    public static final Rect RECT_DOTCODE = new Rect(30, 20, 40, 60);
    private static final String MSG_CAMERA_FRAMEWORK_BUG = "Sorry, the Android camera encountered a problem: ";
    public static final int ID_AUTO_FOCUS = 0x01;
    public static final int ID_DECODE = 0x02;
    public static final int ID_RESTART_PREVIEW = 0x04;
    public static final int ID_DECODE_SUCCEED = 0x08;
    public static final int ID_DECODE_FAILED = 0x10;
    private Handler decodeHandler;
    private boolean hasSurface;
    private String package_name;
    private int activeThreads = 0;
    public static int MAX_THREADS = Runtime.getRuntime().availableProcessors();
    boolean flashOn = false;
    private int zoomLevel = 0;
    private int firstZoom = 150;
    private int secondZoom = 300;
    private SurfaceHolder surfaceHolder;
    public static final int BARCODE_REQUEST_CODE =444;
    public static final String KEY_SCAN_RESULT="scan_result";

    @Override
    public void onOrientationChange(OrientationManager.ScreenOrientation screenOrientation) {
        switch (screenOrientation) {
            case PORTRAIT:
                Rect RECT_FULL_1D = new Rect(20, 0, 45, 100);
                Rect RECT_FULL_2D = new Rect(20, 0, 45, 100);
                setRectangleSize(RECT_FULL_1D, RECT_FULL_2D);
                break;
            case LANDSCAPE:
                Rect RECT_FULL_1 = new Rect(5, 0, 85, 100);
                Rect RECT_FULL_2 = new Rect(5, 0, 85, 100);
                setRectangleSize(RECT_FULL_1, RECT_FULL_2);
                break;
        }
    }

    private enum State {
        STOPPED, PREVIEW, DECODING
    }

    private enum OverlayMode {
        OM_IMAGE, OM_MWOVERLAY, OM_NONE
    }

    State state = State.STOPPED;

    public Handler getHandler() {
        return decodeHandler;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        try {
            package_name = getPackageName();

            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
            boolean isDeviceMobile = getResources().getBoolean(R.bool.isDeviceMobile);

            Rect RECT_FULL_1D, RECT_FULL_2D;
            if (isDeviceMobile) {
                RECT_FULL_1D = new Rect(20, 0, 45, 100);
                RECT_FULL_2D = new Rect(20, 0, 45, 100);
            } else if (tabletSize) {

                RECT_FULL_1D = new Rect(0, 15, 90, 70);
                RECT_FULL_2D = new Rect(5, 20, 95, 70);
                Display display = getWindowManager().getDefaultDisplay();
                int width = display.getWidth();
//                if (getResources().getString(R.string.screen).equalsIgnoreCase("7_inches_Tab")) {

                    RECT_FULL_1D = new Rect(20, 3, 60, 94);
                    RECT_FULL_2D = new Rect(20, 5, 60, 90);
//                }
            } else {
                RECT_FULL_1D = new Rect(30, 0, 40, 100);
                RECT_FULL_2D = new Rect(30, 0, 40, 100);
            }

            setContentView(R.layout.capture);
            ButterKnife.bind(this);
            view.setVisibility(View.VISIBLE);
            textViewActionTitle.setText("Scan");
            cancelButton.setVisibility(View.GONE);
            cancelText.setVisibility(View.VISIBLE);
            cancelText.setText("Cancel");
            cancelText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ManateeBarcodeScannerActivity.this.finish();
                }
            });

            // choose code type or types you want to search for

            if (PDF_OPTIMIZED) {
                BarcodeScanner.MWBsetDirection(BarcodeScanner.MWB_SCANDIRECTION_HORIZONTAL);
                BarcodeScanner.MWBsetActiveCodes(BarcodeScanner.MWB_CODE_MASK_PDF);
                BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_PDF, RECT_LANDSCAPE_1D);
            } else {
                BarcodeScanner.MWBsetDirection(BarcodeScanner.MWB_SCANDIRECTION_HORIZONTAL | BarcodeScanner.MWB_SCANDIRECTION_VERTICAL);
                BarcodeScanner.MWBsetActiveCodes(
                        BarcodeScanner.MWB_CODE_MASK_25 |
                                BarcodeScanner.MWB_CODE_MASK_39 |
                                BarcodeScanner.MWB_CODE_MASK_93 |
                                BarcodeScanner.MWB_CODE_MASK_128 |
                                BarcodeScanner.MWB_CODE_MASK_AZTEC |
                                BarcodeScanner.MWB_CODE_MASK_DM |
                                BarcodeScanner.MWB_CODE_MASK_EANUPC |
                                BarcodeScanner.MWB_CODE_MASK_PDF |
                                BarcodeScanner.MWB_CODE_MASK_QR |//extra in log pkg in
                                BarcodeScanner.MWB_CODE_MASK_CODABAR |
                                BarcodeScanner.MWB_CODE_MASK_11 |
                                BarcodeScanner.MWB_CODE_MASK_MSI |
                                BarcodeScanner.MWB_CODE_MASK_RSS |
                                BarcodeScanner.MWB_CODE_MASK_MAXICODE
                );
                int registerResult = BarcodeScanner.MWBregisterSDK("D4ZUxUo0Qlq2S2OoVms5i/otW4vjdzRqNaP9USXd9p4=", this);

                switch (registerResult) {
                    case BarcodeScanner.MWB_RTREG_OK:
                        Log.i("MWBregisterSDK", "Registration OK");
                        break;
                    case BarcodeScanner.MWB_RTREG_INVALID_KEY:
                        Log.e("MWBregisterSDK", "Registration Invalid Key");
                        break;
                    case BarcodeScanner.MWB_RTREG_INVALID_CHECKSUM:
                        Log.e("MWBregisterSDK", "Registration Invalid Checksum");
                        break;
                    case BarcodeScanner.MWB_RTREG_INVALID_APPLICATION:
                        Log.e("MWBregisterSDK", "Registration Invalid Application");
                        break;
                    case BarcodeScanner.MWB_RTREG_INVALID_SDK_VERSION:
                        Log.e("MWBregisterSDK", "Registration Invalid SDK Version");
                        break;
                    case BarcodeScanner.MWB_RTREG_INVALID_KEY_VERSION:
                        Log.e("MWBregisterSDK", "Registration Invalid Key Version");
                        break;
                    case BarcodeScanner.MWB_RTREG_INVALID_PLATFORM:
                        Log.e("MWBregisterSDK", "Registration Invalid Platform");
                        break;
                    case BarcodeScanner.MWB_RTREG_KEY_EXPIRED:
                        Log.e("MWBregisterSDK", "Registration Key Expired");
                        break;

                    default:
                        Log.e("MWBregisterSDK", "Registration Unknown Error");
                        break;
                }

                setRectangleSize(RECT_FULL_1D, RECT_FULL_2D);
            }

            if (OVERLAY_MODE == OverlayMode.OM_IMAGE) {
                imageOverlay.setVisibility(View.VISIBLE);
            }
            BarcodeScanner.MWBsetLevel(2);
            BarcodeScanner.MWBsetResultType(USE_RESULT_TYPE);


            // Set minimum result length for low-protected barcode types
            BarcodeScanner.MWBsetMinLength(BarcodeScanner.MWB_CODE_MASK_25, 5);
            BarcodeScanner.MWBsetMinLength(BarcodeScanner.MWB_CODE_MASK_MSI, 5);
            BarcodeScanner.MWBsetMinLength(BarcodeScanner.MWB_CODE_MASK_39, 5);
            BarcodeScanner.MWBsetMinLength(BarcodeScanner.MWB_CODE_MASK_CODABAR, 5);
            BarcodeScanner.MWBsetMinLength(BarcodeScanner.MWB_CODE_MASK_11, 5);

            CameraManager.init(getApplication());

            hasSurface = false;
            state = State.STOPPED;
            decodeHandler = new Handler(new Handler.Callback() {

                @Override
                public boolean handleMessage(Message msg) {
                    // TODO Auto-generated method stub

                    switch (msg.what) {
                        case ID_DECODE:
                            try {
                                decode((byte[]) msg.obj, msg.arg1, msg.arg2);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;

                        case ID_AUTO_FOCUS:
                            if (state == State.PREVIEW || state == State.DECODING) {
                                CameraManager.get().requestAutoFocus(decodeHandler, ID_AUTO_FOCUS);
                            }
                            break;
                        case ID_RESTART_PREVIEW:
                            restartPreviewAndDecode();
                            break;
                        case ID_DECODE_SUCCEED:

                            state = State.STOPPED;
                            handleDecode((MWResult) msg.obj);

                            break;
                        case ID_DECODE_FAILED:
                            break;

                    }

                    return false;
                }
            });


            zoomButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    zoomLevel++;
                    if (zoomLevel > 2) {
                        zoomLevel = 0;
                    }

                    switch (zoomLevel) {
                        case 0:
                            CameraManager.get().setZoom(100);
                            break;
                        case 1:
                            CameraManager.get().setZoom(firstZoom);
                            break;
                        case 2:
                            CameraManager.get().setZoom(secondZoom);
                            break;

                        default:
                            break;
                    }
                }
            });
            buttonFlash.setOnClickListener(new OnClickListener() {
                // @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                @Override
                public void onClick(View v) {
                    toggleFlash();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setRectangleSize(Rect RECT_FULL_1D, Rect RECT_FULL_2D) {
        BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_25, RECT_FULL_1D);
        BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_39, RECT_FULL_1D);
        BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_93, RECT_FULL_1D);
        BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_128, RECT_FULL_1D);
        BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_AZTEC, RECT_FULL_2D);
        BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_DM, RECT_FULL_2D);
        BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_EANUPC, RECT_FULL_1D);
        BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_PDF, RECT_FULL_1D);
//        if (!LogPackageInFragment.isFromLogPackageInFrag) {
        BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_QR, RECT_FULL_2D);
//        }
        BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_RSS, RECT_FULL_1D);
        BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_CODABAR, RECT_FULL_1D);
        BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_DOTCODE, RECT_DOTCODE);
        BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_11, RECT_FULL_1D);
        BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_MSI, RECT_FULL_1D);
        BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_MAXICODE, RECT_FULL_2D);

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            SurfaceView surfaceView = (SurfaceView) findViewById(getResources().getIdentifier("preview_view", "id", package_name));
            surfaceHolder = surfaceView.getHolder();
            recycleOverlayImage();
            if (OVERLAY_MODE == OverlayMode.OM_MWOVERLAY) {
                MWOverlay.addOverlay(this, surfaceView);
            }


            if (hasSurface) {
                Log.i("Init Camera", "On resume");
                initCamera();
            } else {
                surfaceHolder.addCallback(this);
                surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            }

            int ver = BarcodeScanner.MWBgetLibVersion();
            int v1 = (ver >> 16);
            int v2 = (ver >> 8) & 0xff;
            int v3 = (ver & 0xff);

            String libVersion = "Lib version: " + String.valueOf(v1) + "." + String.valueOf(v2) + "." + String.valueOf(v3);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void recycleOverlayImage() {
        if (OVERLAY_MODE == OverlayMode.OM_IMAGE) {
            ((ImageView) imageOverlay.findViewById(R.id.imageOverlay)).setImageDrawable(getResources().getDrawable(R.drawable.overlay));
        } else {
            Drawable imageDrawable = imageOverlay.getDrawable();
            imageOverlay.setImageDrawable(null);

            if (imageDrawable != null && imageDrawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = ((BitmapDrawable) imageDrawable);

                if (!bitmapDrawable.getBitmap().isRecycled()) {
                    bitmapDrawable.getBitmap().recycle();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (OVERLAY_MODE == OverlayMode.OM_MWOVERLAY) {
                MWOverlay.removeOverlay();
            }
            imageOverlay.setImageDrawable(null);
            CameraManager.get().stopPreview();
            CameraManager.get().closeDriver();
            state = State.STOPPED;

            flashOn = false;

            updateFlash();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration config) {

        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();

        CameraManager.get().updateCameraOrientation(rotation);

        super.onConfigurationChanged(config);
    }

    private void toggleFlash() {
        flashOn = !flashOn;
        updateFlash();
    }

    private void updateFlash() {

        if (!CameraManager.get().isTorchAvailable()) {
            buttonFlash.setVisibility(View.GONE);
            return;

        } else {
            buttonFlash.setVisibility(View.VISIBLE);
        }

        CameraManager.get().setTorch(flashOn);

        buttonFlash.postInvalidate();

    }

    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i("Init Camera", "On Surface changed");
        initCamera();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_FOCUS || keyCode == KeyEvent.KEYCODE_CAMERA) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void decode(final byte[] data, final int width, final int height) {
        if (activeThreads >= MAX_THREADS || state == State.STOPPED) {
            return;
        }
        new Thread(new Runnable() {
            public void run() {
                activeThreads++;
                long start = System.currentTimeMillis();
                byte[] rawResult = null;
                rawResult = BarcodeScanner.MWBscanGrayscaleImage(data, width, height);

                if (state == State.STOPPED) {
                    activeThreads--;
                    return;
                }

                MWResult mwResult = null;

                if (rawResult != null && BarcodeScanner.MWBgetResultType() == BarcodeScanner.MWB_RESULT_TYPE_MW) {

                    BarcodeScanner.MWResults results = new BarcodeScanner.MWResults(rawResult);

                    if (results.count > 0) {
                        mwResult = results.getResult(0);
                        rawResult = mwResult.bytes;
                    }

                } else if (rawResult != null && BarcodeScanner.MWBgetResultType() == BarcodeScanner.MWB_RESULT_TYPE_RAW) {
                    mwResult = new BarcodeScanner.MWResult();
                    mwResult.bytes = rawResult;
                    mwResult.text = rawResult.toString();
                    mwResult.type = BarcodeScanner.MWBgetLastType();
                    mwResult.bytesLength = rawResult.length;
                }

                if (mwResult != null) {
                    Log.i("Found Result", "Something");
                    state = State.STOPPED;

                    Message message = Message.obtain(ManateeBarcodeScannerActivity.this.getHandler(), ID_DECODE_SUCCEED, mwResult);

                    message.arg1 = mwResult.type;

                    message.sendToTarget();

                } else {
                    Message message = Message.obtain(ManateeBarcodeScannerActivity.this.getHandler(), ID_DECODE_FAILED);
                    message.sendToTarget();
                }

                activeThreads--;
            }
        }).start();
    }

    private void restartPreviewAndDecode() {
        if (state == State.STOPPED) {
            state = State.PREVIEW;
            Log.i("preview", "requestPreviewFrame.");
            CameraManager.get().requestPreviewFrame(getHandler(), ID_DECODE);
            CameraManager.get().requestAutoFocus(getHandler(), ID_AUTO_FOCUS);
        }
    }

    public void handleDecode(MWResult result) {
        try {
            byte[] rawResult = null;
            if (result != null && result.bytes != null) {
                rawResult = result.bytes;
            }

            String barcode = "";

            if (PARSER_MASK != MWParser.MWP_PARSER_MASK_NONE && BarcodeScanner.MWBgetResultType() == BarcodeScanner.MWB_RESULT_TYPE_MW
                    && !(PARSER_MASK == MWParser.MWP_PARSER_MASK_GS1 && !result.isGS1)) {

                barcode = MWParser.MWPgetJSON(PARSER_MASK, result.encryptedResult.getBytes());
                if (barcode == null) {
                    String parserMask = "parser";
                    switch (PARSER_MASK) {
                        case MWParser.MWP_PARSER_MASK_AAMVA:
                            parserMask = "AAMVA";
                            break;
                        case MWParser.MWP_PARSER_MASK_ISBT:
                            parserMask = "ISBT";
                            break;
                        case MWParser.MWP_PARSER_MASK_IUID:
                            parserMask = "IUID";
                            break;
                        case MWParser.MWP_PARSER_MASK_HIBC:
                            parserMask = "HIBC";
                            break;
                        case MWParser.MWP_PARSER_MASK_GS1:
                            parserMask = "GS1";
                            break;
                        case MWParser.MWP_PARSER_MASK_SCM:
                            parserMask = "SCM";
                            break;
                        default:
                            break;
                    }
                    barcode = result.text + "\n*Not a valid " + parserMask + " formatted barcode";
                }
            } else {
                //barcode = result.text;
                try {
                    barcode = new String(rawResult, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    barcode = "";
                    for (int i = 0; i < rawResult.length; i++)
                        barcode = barcode + (char) rawResult[i];
                    e.printStackTrace();
                }
            }

            int bcType = result.type;
            if (result.locationPoints != null && CameraManager.get().getCurrentResolution() != null && OVERLAY_MODE == OverlayMode.OM_MWOVERLAY) {
                // MWOverlay.showLocation(result.locationPoints.points, result.imageWidth, result.imageHeight);
            }

            if (result.isGS1) {
                result.typeName += " (GS1)";
            }

            if (bcType >= 0) {
                if (bcType == BarcodeScanner.FOUND_PDF) {
                    onResume();
                    return;
                }
                if (barcode.length() > 0 && bcType != BarcodeScanner.FOUND_PDF) {
                    try {
                        AssetFileDescriptor afd = getApplicationContext().getAssets().openFd("beep.mp3");
                        player = new MediaPlayer();
                        player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                        afd.close();
                        player.prepare();
                        player.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    barcode = barcode.replaceAll("[\u0000-\u001f]", "");
                    Intent intent = new Intent();
                    intent.putExtra(KEY_SCAN_RESULT, barcode);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Error");
                builder.setMessage("Unable to read");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void initCamera() {
        if (!isFinishing()) {

            try {
                if (MAX_THREADS > 2 || PDF_OPTIMIZED) {
                    CameraManager.setDesiredPreviewSize(1280, 720);
                } else {
                    CameraManager.setDesiredPreviewSize(800, 480);
                }

                CameraManager.get().openDriver(surfaceHolder, (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT));

                int maxZoom = CameraManager.get().getMaxZoom();
                if (maxZoom < 100) {
                    zoomButton.setVisibility(View.GONE);
                } else {
                    zoomButton.setVisibility(View.VISIBLE);
                    if (maxZoom < 300) {
                        secondZoom = maxZoom;
                        firstZoom = (maxZoom - 100) / 2 + 100;
                    }
                }
            } catch (IOException ioe) {
                displayFrameworkBugMessageAndExit(ioe.getMessage());
                return;
            } catch (RuntimeException e) {
                displayFrameworkBugMessageAndExit(e.getMessage());
                return;
            } catch (Exception e) {
                displayFrameworkBugMessageAndExit(e.getMessage());
                return;
            }
            Log.i("preview", "start preview.");

            flashOn = false;

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    switch (zoomLevel) {
                        case 0:
                            CameraManager.get().setZoom(100);
                            break;
                        case 1:
                            CameraManager.get().setZoom(firstZoom);
                            break;
                        case 2:
                            CameraManager.get().setZoom(secondZoom);
                            break;

                        default:
                            break;
                    }

                }
            }, 300);

            //Fix for camera sensor rotation bug
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(CameraManager.USE_FRONT_CAMERA ? 1 : 0, cameraInfo);
            if (cameraInfo.orientation == 270) {
                BarcodeScanner.MWBsetFlags(0, BarcodeScanner.MWB_CFG_GLOBAL_ROTATE180);
            }

            CameraManager.get().startPreview();
            restartPreviewAndDecode();
            updateFlash();
        }

    }

    private void displayFrameworkBugMessageAndExit(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getIdentifier("app_name", "string", package_name));
        builder.setMessage(MSG_CAMERA_FRAMEWORK_BUG + message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.show();
    }


}
*/
