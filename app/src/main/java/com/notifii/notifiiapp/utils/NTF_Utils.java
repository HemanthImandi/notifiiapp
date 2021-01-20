package com.notifii.notifiiapp.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.activities.CameraPreviewActivity;
import com.notifii.notifiiapp.activities.CameraPreviewActivity2;
import com.notifii.notifiiapp.activities.LoginActivity;
import com.notifii.notifiiapp.activities.MainActivity;
import com.notifii.notifiiapp.asynctasks.RecipientJsonToModel;
import com.notifii.notifiiapp.models.Recipient;
import com.notifii.notifiiapp.models.SpinnerData;
import com.notifii.notifiiapp.customui.ProgressHUD;
import com.notifii.notifiiapp.helpers.NTF_PrefsManager;
import com.notifii.notifiiapp.helpers.SingleTon;
import com.notifii.notifiiapp.mvp.models.GetGlobalConstantsRequest;
import com.notifii.notifiiapp.mvp.models.LogPkgInResponse;
import com.notifii.notifiiapp.mvp.models.LoginResponse;
import com.notifii.notifiiapp.mvp.models.RecipientListRequest;
import com.notifii.notifiiapp.receivers.AlarmReceiver;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.notifii.notifiiapp.utils.NTF_Constants.ResponseKeys.ERROR;
import static com.notifii.notifiiapp.utils.NTF_Constants.ResponseKeys.SESSION_EXPIRED;

public class NTF_Utils implements NTF_Constants {

    private static NTF_PrefsManager ntf_Preferences;
    public static final SimpleDateFormat dateFormate = new SimpleDateFormat(DATE_TIME_FORMATE);
    private static boolean isAlertShown = false;
    private static ProgressHUD progressHUD;

    public static void startAlarmIfRequired(Context ctxt) {
        try {
            NTF_PrefsManager spManager = new NTF_PrefsManager(ctxt);
            //Set Alarm only if we haven't set it before.
            if (!spManager.hasKey(NTF_Constants.Prefs_Keys.IS_ALARM_RUNNING)) {
                AlarmManager manager = (AlarmManager) ctxt.getSystemService(Context.ALARM_SERVICE);
//                long durationInMilliSeconds  = 2 * ONE_MINUTE;
                long durationInMilliSeconds = 4 * 60 * ONE_MINUTE;
                manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + durationInMilliSeconds, durationInMilliSeconds, getPendingIntent(ctxt));
                spManager.save(NTF_Constants.Prefs_Keys.IS_ALARM_RUNNING, "Yes");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static PendingIntent getPendingIntent(Context ctxt) {
        return PendingIntent.getBroadcast(ctxt, 0, new Intent(ctxt, AlarmReceiver.class), PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public static void sendLocalMessage(Context context) {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent(Extras_Keys.LOCAL_BROADCAST_START_ACTION);
        // You can also include some extra data.
        //intent.putExtra("message", "This is my message!");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static boolean isOnline(Context activity) {
        if (activity != null) {
            NetworkInfo info = ((ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            return (info != null && info.getState() == NetworkInfo.State.CONNECTED);
        }
        return false;
    }

    public static void backgroundCallCompleted(Context context, String message, String status) {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent(Extras_Keys.LOCAL_BROADCAST_END_ACTION);
        intent.putExtra(Extras_Keys.BACKGROUNG_API_MESSAGE, message != null ? message : "");
        intent.putExtra(Extras_Keys.BACKGROUNG_API_STATUS, status != null ? status : "");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private static void setMailRoomId(JSONObject jsonObject, NTF_PrefsManager ntf_Preferences) {
        try {
            ArrayList<SpinnerData> mMailRoomList = SpinnerData.getList(jsonObject.getJSONArray("mailrooms"), null).getList();
            boolean isMailroomSet = false;
            if (mMailRoomList.size() > 1) {
                for (int i = 0; i < mMailRoomList.size(); i++) {
                    if (mMailRoomList.get(i).getValue().equalsIgnoreCase(ntf_Preferences.get(NTF_Constants.Prefs_Keys.DEFAULT_MAILROOM_ID))) {
                        isMailroomSet = true;
                        ntf_Preferences.save(NTF_Constants.Prefs_Keys.DEFAULT_MAILROOM_ID, mMailRoomList.get(i).getValue());
                    }
                }
            } else if (mMailRoomList.size() == 1) {
                ntf_Preferences.save(NTF_Constants.Prefs_Keys.DEFAULT_MAILROOM_ID, mMailRoomList.get(0).getValue());
                isMailroomSet = true;
            }
            if (!isMailroomSet) {
                ntf_Preferences.save(NTF_Constants.Prefs_Keys.DEFAULT_MAILROOM_ID, "all");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void cancelAlarm(Context ctxt) {
        try {
            AlarmManager manager = (AlarmManager) ctxt.getSystemService(Context.ALARM_SERVICE);
            manager.cancel(getPendingIntent(ctxt));

            NTF_PrefsManager spManager = new NTF_PrefsManager(ctxt);
            if (spManager.hasKey(Prefs_Keys.IS_ALARM_RUNNING)) {
                spManager.removeKey(Prefs_Keys.IS_ALARM_RUNNING);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showAlertForFinish(Activity activity, String title, String message) {
        Runnable runnable = () -> activity.onBackPressed();
        showAlert(activity, title, message, runnable);
    }

    public static void handleDoneButton(EditText editText, Activity activity) {
        if (editText == null)
            return;
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    NTF_Utils.hideKeyboard(activity);
                    return true;
                }
                return false;
            }
        });
    }

    public static String getUUID(Activity activity) {
        try {
            if (activity == null) {
                return "";
            }
            return Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    static class DialogViewHolder {
        @BindView(R.id.parentLayout)
        LinearLayout parentLayout;
        @BindView(R.id.imageView)
        ImageView imageView;
        @BindView(R.id.title)
        TextView mTitle;
        @BindView(R.id.message)
        TextView mMessage;

        DialogViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public static void showAlert(final Activity act, String title, String message, Runnable runnable) {
        if (act != null && !isAlertShown) {
            try {
                hideProgressDialog();
                int width = act.getResources().getDisplayMetrics().widthPixels;
                final Dialog dialog = new Dialog(act, R.style.DialogTheme);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                View view = act.getLayoutInflater().inflate(R.layout.dialog_alert, null);
                DialogViewHolder viewHolder = new DialogViewHolder(view);
                dialog.setContentView(view);
                dialog.setCanceledOnTouchOutside(false);
                if (title.isEmpty()) {
                    viewHolder.mTitle.setText(ALERT_ERROR_TITLE_OOPS);
                } else {
                    viewHolder.mTitle.setText(title);
                }
                if (title.equals(ALERT_SUCCESS_TITLE)) {
                    viewHolder.mTitle.setTextColor(act.getResources().getColor(R.color.success_color));
                    viewHolder.imageView.setImageResource((R.drawable.ic_success_icon));
                } else if (title.equals(ALERT_WARNING_TITLE)) {
                    viewHolder.mTitle.setTextColor(act.getResources().getColor(R.color.pkg_warning_color));
                    viewHolder.imageView.setImageResource((R.drawable.ic_warning_icon));
                } else {
                    viewHolder.mTitle.setTextColor(act.getResources().getColor(R.color.info_color));
                    viewHolder.imageView.setImageResource((R.drawable.ic_info_icon));
                }
                viewHolder.mMessage.setText(message);
                viewHolder.mTitle.setContentDescription(title);
                viewHolder.mMessage.setContentDescription(message);
                viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                viewHolder.mMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                viewHolder.mTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                if (dialog == null || dialog.getWindow() == null) {
                    return;
                }
                int dialogWidth = (int) (width * 0.90f);
                if (act.getResources().getBoolean(R.bool.isTablet)) {
                    dialogWidth = (int) (width * 0.75f);
                }
                int dialogHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setLayout(dialogWidth, dialogHeight);
                dialog.show();
                isAlertShown=true;
                dialog.setOnDismissListener(dialog1 -> {
                    isAlertShown=false;
                    if (runnable != null) {
                        runnable.run();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getPasswordInvalidMessage(String password) {
        Pattern numberPattern = Pattern.compile("[0-9]");
        Pattern capitalPattern = Pattern.compile("[A-Z]");
        Pattern lowerPattern = Pattern.compile("[a-z]");
        Pattern specialCharactersPattern = Pattern.compile("[&'><\\\"]", Pattern.CASE_INSENSITIVE);

        if (password.length() < 8) {
            return "Password must be at least 8 characters long & no spaces.";
        }
        if (password.contains(" ")) {
            return "Password must be at least 8 characters long & no spaces.";
        }
        if (!capitalPattern.matcher(password).find()) {
            return "Password must have at least one upper case letter.";
        }
        if (!lowerPattern.matcher(password).find()) {
            return "Password must have at least one lower case letter.";
        }
        if (!numberPattern.matcher(password).find()) {
            return "Password must have at least one number.";
        }
        if (specialCharactersPattern.matcher(password).find()) {
            return "Password does not allow this characters & ' > < \" ";
        }
        return null;
    }

    public static String getUsernameInvalidMessage(String username) {
        Pattern specialCharactersPattern = Pattern.compile("[&'><\\\"]", Pattern.CASE_INSENSITIVE);

        if (specialCharactersPattern.matcher(username).find()) {
            return "Username does not allow this characters & ' > < \" ";
        }

        return null;
    }

    // to zoom the image
    public static void showImageAlert(Bitmap bitmap, Activity activity) {
        try {
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_image_fullview, null);
            ImageView imageView = (ImageView) dialogView.findViewById(R.id.imageFullView);
            imageView.setImageBitmap(bitmap);
            AlertDialog.Builder builder = new AlertDialog.Builder(activity, android.R.style.Theme_Wallpaper_NoTitleBar_Fullscreen);
            builder.setView(dialogView);

            dialogView.findViewById(R.id.buttonClose).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dialog != null)
                        dialog.dismiss();
                }
            });

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog != null)
                        dialog.dismiss();
                }
            });
            dialog = builder.create();
            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JSONObject getRecipientsData(Context context) {
        File sdcard = context.getFilesDir();
        File file = new File(sdcard, RECIPIENT_DATA_FILE_NAME);
        StringBuilder text = new StringBuilder();
        JSONObject jsonObject = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
            jsonObject = new JSONObject(text.toString());
        } catch (Exception e) {
            //You'll need to add proper error handling here
            e.printStackTrace();
        } finally {
            return jsonObject;
        }
    }

    public static void showSessionExpireAlert(String msg, final Activity activity, NTF_PrefsManager ntf_Preferences) {
        try {
            ntf_Preferences.clearAllPrefs();
            SingleTon.clearInstance();
            NTF_Utils.deleteFile(activity, RECIPIENT_DATA_FILE_NAME);
            NTF_Utils.deleteFile(activity, GLOBAL_DATA_FILE_NAME);
            ntf_Preferences.save(Prefs_Keys.IS_LOGGED_IN, false);
            ntf_Preferences.save(Prefs_Keys.IS_KIOSK_MODE, false);
            Runnable runnable = () -> {
                Intent loginActivityIntent = new Intent(activity, LoginActivity.class);
                activity.startActivity(loginActivityIntent);
                activity.finishAffinity();
            };
            showAlert(activity, "", msg, runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideKeyboard(Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            View view = activity.getCurrentFocus();
            if (view == null) {
                view = new View(activity);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showNoNetworkAlert(final Activity act) {
        if (act != null) {
            showAlert(act, "", act.getString(R.string.no_network_message), null);
        }
    }

    // Saving Logged In UserNames
    public static void saveLoggedUserNames(String name, String value, Activity activity) {
        SharedPreferences settings = activity.getSharedPreferences("logged_user_names", Context.MODE_PRIVATE);
        String previousUserNames = settings.getString(name, "");
        String[] names = previousUserNames.split(",");
        boolean isNameAlreadySaved = false;
        for (String s : names) {
            if (s.equalsIgnoreCase(value)) {
                isNameAlreadySaved = true;
            }
        }
        if (!isNameAlreadySaved) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(name, value + "," + previousUserNames);
            editor.apply();
        }
    }

    public static String getLoggedUserNames(Context context, String name) {
        SharedPreferences settings = null;
        if (context != null) {
            settings = context.getSharedPreferences("logged_user_names", Context.MODE_PRIVATE);
        }
        return settings.getString(name, "");
    }

    // Getting type of resident
    public static String getRecipientTypeName(String recipient_type, JSONObject jsonObject) {

        try {
            JSONArray jsonArray = jsonObject.getJSONArray("recipient_types");
            for (int i = 0, len = jsonArray.length(); i < len; i++) {
                JSONObject recipientJsonObj = jsonArray.getJSONObject(i);
                if (recipientJsonObj.optString("value").equalsIgnoreCase(recipient_type))
                    return recipientJsonObj.optString("name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void showProgressDialog(Activity activity) {
        if (activity != null) {
            try {
                if (progressHUD != null && progressHUD.isShowing()) {
                    progressHUD.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                progressHUD = ProgressHUD.show(activity, "", false, true, null);
            }
        }
    }

    public static void hideProgressDialog() {
        try {
            Log.d("ProgressBar", "hideProgressDialog");
            if (progressHUD != null && progressHUD.isShowing()) {
                progressHUD.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveGlobalConstants(Context context, JSONObject response, NTF_PrefsManager ntf_Preferences) {
        ntf_Preferences.save(Prefs_Keys.OCR_TIME_LIMIT, response.optString("ocr_time_limit"));
        ntf_Preferences.save(Prefs_Keys.REQUIRE_EXPLICIT_SMS_OPT_IN, response.optString("require_explicit_sms_opt_in"));
        ntf_Preferences.save(Prefs_Keys.EMAIL_OPTION_ENABLED, response.optString("email_option_enabled"));
        ntf_Preferences.save(Prefs_Keys.SSO_LOGIN_OPTION, response.optString("sso_login_option"));
        ntf_Preferences.save(Prefs_Keys.SSO_STATUS, response.optString("sso_status"));
        saveGlobalData(context, response.toString());
        doSaveAccountSettings(ntf_Preferences, response.optJSONArray("account_settings").optJSONObject(0));
        doSaveUserPrivileges(ntf_Preferences, response.optJSONObject("user_privileges"));
//        NTF_Utils.setMailRoomId(response, ntf_Preferences);
    }

    private static void saveGlobalData(Context context, String data) {
        FileOutputStream outputStream;
        SingleTon.getInstance().setmGobalJsonData(null);
        try {
            deleteFile(context, GLOBAL_DATA_FILE_NAME);
            outputStream = context.openFileOutput(GLOBAL_DATA_FILE_NAME, Context.MODE_PRIVATE);
            outputStream.write(data.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteFile(Context context, String fileName) {
        try {
            File sdcard = context.getFilesDir();
            File file = new File(sdcard, fileName);
            if (file.exists()) {
                file.delete();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void doSaveAccountSettings(NTF_PrefsManager ntf_Preferences, JSONObject jsonObject) {
        if (ntf_Preferences != null) {
            ntf_Preferences.save(Prefs_Keys.PKG_LOGIN_CARRIER, jsonObject.optString("pkg_login_carrier"));
            ntf_Preferences.save(Prefs_Keys.PKG_LOGIN_SENDER, jsonObject.optString("pkg_login_sender"));
            ntf_Preferences.save(Prefs_Keys.PKG_LOGIN_SERVICETYPE, jsonObject.optString("pkg_login_servicetype"));
            ntf_Preferences.save(Prefs_Keys.PKG_LOGIN_PACKAGETYPE, jsonObject.optString("pkg_login_packagetype"));
            ntf_Preferences.save(Prefs_Keys.PKG_LOGIN_CONDITION, jsonObject.optString("pkg_login_condition"));
            ntf_Preferences.save(Prefs_Keys.PKG_LOGIN_SHELF, jsonObject.optString("pkg_login_shelf"));
            ntf_Preferences.save(Prefs_Keys.PKG_LOGOUT_ADDRESS1, jsonObject.optString("pkg_logout_address1"));
            ntf_Preferences.save(Prefs_Keys.PKG_LOGOUT_SHELF, jsonObject.optString("pkg_logout_shelf"));//1->visible
            ntf_Preferences.save(Prefs_Keys.PKG_LOGOUT_SENDER, jsonObject.optString("pkg_logout_sender"));
            ntf_Preferences.save(Prefs_Keys.PKG_LOGOUT_PACKAGETYPE, jsonObject.optString("pkg_logout_packagetype"));
            ntf_Preferences.save(Prefs_Keys.PKG_LOGOUT_TAGNUMBER, jsonObject.optString("pkg_logout_tagnumber"));
            ntf_Preferences.save(Prefs_Keys.PKG_LOGOUT_MAILROOM, jsonObject.optString("pkg_logout_mailroom"));
            ntf_Preferences.save(Prefs_Keys.PKG_LOGIN_PONUMBER, jsonObject.optString("pkg_login_ponumber"));
            ntf_Preferences.save(Prefs_Keys.PKG_LOGIN_DATA_INTEGRATION, jsonObject.optString("data_integration"));
            ntf_Preferences.save(Prefs_Keys.PKG_LOGIN_SPECIAL_HANDLING, jsonObject.optString("pkg_login_special_handling"));
            ntf_Preferences.save(Prefs_Keys.PKG_LOGIN_CUSTOM_MESSAGE, jsonObject.optString("pkg_login_custom_message"));
            ntf_Preferences.save(Prefs_Keys.PKG_LOGIN_PICTURES, jsonObject.optString("pkg_login_pictures"));
            ntf_Preferences.save(Prefs_Keys.SEND_PKG_LOGIN_NOTIFICATION, jsonObject.optString("send_pkg_login_notification"));
            ntf_Preferences.save(Prefs_Keys.SEND_PKG_LOGOUT_NOTIFICATION, jsonObject.optString("send_pkg_logout_notification"));
            ntf_Preferences.save(Prefs_Keys.KIOSK_MODE_DISCLAIMER, jsonObject.optString("kiosk_mode_disclaimer"));
            ntf_Preferences.save(Prefs_Keys.KIOSK_DISPLAY_RECIPIENT_FORMAT, jsonObject.optString("kiosk_display_recipient_format"));
            ntf_Preferences.save(Prefs_Keys.PKG_LOGIN_WEIGHT, jsonObject.optString("pkg_login_weight"));
            ntf_Preferences.save(Prefs_Keys.PKG_LOGIN_DIMENSIONS, jsonObject.optString("pkg_login_dimensions"));
            ntf_Preferences.save(Prefs_Keys.USE_FRONT_CAMERA, jsonObject.optString("kiosk_use_front_camera"));
            ntf_Preferences.save(Prefs_Keys.PENDING_PACKAGES_PRIMARY_SORT_COLUMN, jsonObject.optString("pending_packages_primary_sort_column"));
            ntf_Preferences.save(Prefs_Keys.PENDING_PACKAGES_PRIMARY_SORT_ORDER, jsonObject.optString("pending_packages_primary_sort_order").toUpperCase());
            ntf_Preferences.save(Prefs_Keys.PENDING_PACKAGES_SECONDARY_SORT_COLUMN, jsonObject.optString("pending_packages_secondary_sort_column"));
            ntf_Preferences.save(Prefs_Keys.PENDING_PACKAGES_SECONDARY_SORT_ORDER, jsonObject.optString("pending_packages_secondary_sort_order").toUpperCase());
        }
    }

    private static void doSaveUserPrivileges(NTF_PrefsManager ntf_Preferences, JSONObject jsonObject) {
        ntf_Preferences.save(Prefs_Keys.PRIVILEGE_ADMIN_ACCOUNT, jsonObject.optString("privilege_admin_account"));
        ntf_Preferences.save(Prefs_Keys.PRIVILEGE_ADMIN_USERS, jsonObject.optString("privilege_admin_users"));
        ntf_Preferences.save(Prefs_Keys.PRIVILEGE_CORE_RECIPIENTS, jsonObject.optString("privilege_core_recipients"));
        ntf_Preferences.save(Prefs_Keys.PRIVILEGE_TRACK_PACKAGES, jsonObject.optString("privilege_track_packages"));
    }

    public static String getAppDirPath(Context c) {
        String folderpath = SDCARD_PATH + File.separator + c.getString(R.string.app_name).replace(" ", "_");
        File dir = new File(folderpath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return folderpath;
    }

    public static void saveRecipientsData(Context context, JSONObject jsonObject) {
        if (jsonObject != null) {
            String data = jsonObject.toString();
            FileOutputStream outputStream;
            try {
                deleteFile(context, RECIPIENT_DATA_FILE_NAME);
                outputStream = context.openFileOutput(RECIPIENT_DATA_FILE_NAME, Context.MODE_PRIVATE);
                outputStream.write(data.getBytes());
                outputStream.close();
                new RecipientJsonToModel(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            SingleTon.getInstance().setmRecipientListForOcr(Recipient.getRecipientsList(jsonObject.getJSONArray("recipients")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, null).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void clearRecipientListPosition(Activity activity) {
        if (activity != null) {
            ntf_Preferences = new NTF_PrefsManager(activity);
            if (!ntf_Preferences.hasKey(Prefs_Keys.RECIPIENT_PKG_LIST_SELECTED_ITEM2))
                ntf_Preferences.save(Prefs_Keys.RECIPIENT_PKG_LIST_SELECTED_ITEM2, 0);
        }
    }

    public static Fragment getCurrentFragment(Fragment fragment) {
        try {
            FragmentManager fragmentManager = fragment.getFragmentManager();
            if (fragmentManager == null) {
                return null;
            } else {
                return fragmentManager.findFragmentById(R.id.realtabcontent);
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static String getRecipientTypeLabel(String accountType) {
        if (accountType != null) {
            if (accountType.toLowerCase().equalsIgnoreCase("apt")) {

                return "Resident"; //define("RECIPIENT_TYPE_LABEL", "Resident");

            } else if (accountType.toLowerCase().equalsIgnoreCase("edu")) {

                return "Recipient"; // define("RECIPIENT_TYPE_LABEL", "Staff/Student");

            } else if (accountType.toLowerCase().equalsIgnoreCase("inc")) {

                return "Employee"; //define("RECIPIENT_TYPE_LABEL", "Employee");

            } else if (accountType.toLowerCase().equalsIgnoreCase("mpc")) {

                return "Boxholder"; //define("RECIPIENT_TYPE_LABEL", "Boxholder");

            }
        }
        return "";
    }

    public static boolean isValidEmail(String emailId) {
        return emailId != null && android.util.Patterns.EMAIL_ADDRESS.matcher(emailId).matches();
    }

    public static GetGlobalConstantsRequest getGlobalConstantsRequestObject(String apimode, NTF_PrefsManager ntf_Preferences) {
        GetGlobalConstantsRequest request = new GetGlobalConstantsRequest();
        request.setSessionId(ntf_Preferences.get(Prefs_Keys.SESSION_ID));
        request.setAuthenticationToken(ntf_Preferences.get(Prefs_Keys.AUTHENTICATION_TOKEN));
        request.setAccountId(ntf_Preferences.get(Prefs_Keys.ACCOUNT_ID));
        request.setUserTypeId(ntf_Preferences.get(Prefs_Keys.USER_TYPE_ID));
        request.setApiMode(apimode);
        request.setUserId(ntf_Preferences.get(Prefs_Keys.USER_ID));
        return request;
    }

    public static RecipientListRequest getRecipientListRequestObject(String apimode, NTF_PrefsManager ntf_Preferences) {
        RecipientListRequest request = new RecipientListRequest();
        request.setSessionId(ntf_Preferences.get(Prefs_Keys.SESSION_ID));
        request.setAuthenticationToken(ntf_Preferences.get(Prefs_Keys.AUTHENTICATION_TOKEN));
        request.setAccountId(ntf_Preferences.get(Prefs_Keys.ACCOUNT_ID));
        request.setApiMode(apimode);
        request.setUserId(ntf_Preferences.get(NTF_Constants.Prefs_Keys.USER_ID));
        return request;
    }

    public static void doSaveUserDetails(LoginResponse loginResponse, NTF_PrefsManager ntf_Preferences) {
        ntf_Preferences.save(Prefs_Keys.SESSION_ID, loginResponse.getSessionId());
        ntf_Preferences.save(Prefs_Keys.AUTHENTICATION_TOKEN, loginResponse.getAuthenticationToken());
        ntf_Preferences.save(Prefs_Keys.EXPIRATION, loginResponse.getExpiration());
        ntf_Preferences.save(Prefs_Keys.USER_ID, loginResponse.getUserId());
        ntf_Preferences.save(Prefs_Keys.FULL_NAME, loginResponse.getFullName());
        ntf_Preferences.save(Prefs_Keys.ACCOUNT_ID, loginResponse.getAccountId());
        ntf_Preferences.save(Prefs_Keys.ACCOUNT_NAME, loginResponse.getAccountName());
        ntf_Preferences.save(Prefs_Keys.ACCOUNT_TYPE, loginResponse.getAccountType());
        ntf_Preferences.save(Prefs_Keys.USER_TYPE, loginResponse.getUserType());
        ntf_Preferences.save(Prefs_Keys.USER_TYPE_ID, loginResponse.getUserTypeId());
        ntf_Preferences.save(Prefs_Keys.TIMEZONE, loginResponse.getTimezone());
        ntf_Preferences.save(Prefs_Keys.DEFAULT_MAILROOM_ID, loginResponse.getDefaultMailroomId());
        ntf_Preferences.save(Prefs_Keys.TIMEZONE_SHORTCODE, loginResponse.getTimezoneidShortcode());
        if (loginResponse.getSessionTimedout()!=null && !loginResponse.getSessionTimedout().isEmpty()){
            ntf_Preferences.save(Prefs_Keys.SESSION_TIMEOUT, loginResponse.getSessionTimedout());
        }
        SingleTon.getInstance().setOcrArray(new JSONArray());
    }

    public static void doSaveKioskLogoutSettings(Activity activity, LoginResponse loginResponse) {
        if (activity != null) {
            ntf_Preferences = new NTF_PrefsManager(activity);
            ntf_Preferences.save(Prefs_Keys.SESSION_ID, loginResponse.getSessionId());
            ntf_Preferences.save(Prefs_Keys.AUTHENTICATION_TOKEN, loginResponse.getAuthenticationToken());
            ntf_Preferences.save(Prefs_Keys.EXPIRATION, loginResponse.getExpiration());
            ntf_Preferences.save(Prefs_Keys.USER_ID, loginResponse.getUserId());
            ntf_Preferences.save(Prefs_Keys.FULL_NAME, loginResponse.getFullName());
            ntf_Preferences.save(Prefs_Keys.ACCOUNT_ID, loginResponse.getAccountId());
            ntf_Preferences.save(Prefs_Keys.ACCOUNT_NAME, loginResponse.getAccountName());
            ntf_Preferences.save(Prefs_Keys.ACCOUNT_TYPE, loginResponse.getAccountType());
            ntf_Preferences.save(Prefs_Keys.USER_TYPE_ID, loginResponse.getUserTypeId());
            ntf_Preferences.save(Prefs_Keys.USER_TYPE, loginResponse.getUserType());
            ntf_Preferences.save(Prefs_Keys.DEFAULT_MAILROOM_ID, loginResponse.getDefaultMailroomId());
            ntf_Preferences.save(Prefs_Keys.TIMEZONE, loginResponse.getTimezone());
            ntf_Preferences.save(Prefs_Keys.PKG_LOGIN_CARRIER, loginResponse.getPkgLoginCarrier());
            ntf_Preferences.save(Prefs_Keys.PKG_LOGIN_SENDER, loginResponse.getPkgLoginSender());
            ntf_Preferences.save(Prefs_Keys.PKG_LOGIN_SERVICETYPE, loginResponse.getPkgLoginServicetype());
            ntf_Preferences.save(Prefs_Keys.PKG_LOGIN_PACKAGETYPE, loginResponse.getPkgLoginPackagetype());
            ntf_Preferences.save(Prefs_Keys.PKG_LOGIN_CONDITION, loginResponse.getPkgLoginCondition());
            ntf_Preferences.save(Prefs_Keys.PKG_LOGIN_SHELF, loginResponse.getPkgLoginShelf());
            ntf_Preferences.save(Prefs_Keys.PKG_LOGIN_PONUMBER, loginResponse.getPkgLoginPonumber());
            ntf_Preferences.save(Prefs_Keys.PKG_LOGOUT_ADDRESS1, loginResponse.getPkgLogoutAddress1());
            ntf_Preferences.save(Prefs_Keys.PKG_LOGOUT_SHELF, loginResponse.getPkgLogoutShelf());
            ntf_Preferences.save(Prefs_Keys.PKG_LOGOUT_PACKAGETYPE, loginResponse.getPkgLogoutPackagetype());
            ntf_Preferences.save(Prefs_Keys.PKG_LOGOUT_TAGNUMBER, loginResponse.getPkgLogoutTagnumber());
            ntf_Preferences.save(Prefs_Keys.PKG_LOGOUT_MAILROOM, loginResponse.getPkgLogoutMailroom());
            ntf_Preferences.save(Prefs_Keys.PKG_LOGOUT_SENDER, loginResponse.getPkgLogoutSender());
            ntf_Preferences.save(Prefs_Keys.TIMEZONE_SHORTCODE, loginResponse.getTimezoneidShortcode());
            if (loginResponse.getSessionTimedout()!=null && !loginResponse.getSessionTimedout().isEmpty()){
                ntf_Preferences.save(Prefs_Keys.SESSION_TIMEOUT, loginResponse.getSessionTimedout());
            }
        }
    }

    public static String getHeader(NTF_PrefsManager ntf_Preferences, Activity activity) {
        return "DEVICE=" + Build.MODEL + "; OS=" + Build.VERSION.RELEASE
                + "; APP=" + ((ntf_Preferences.hasKey(Prefs_Keys.IS_KIOSK_MODE) && ntf_Preferences.getBoolean(Prefs_Keys.IS_KIOSK_MODE)) ? "Track [Kiosk Mode]" : "Track") + "; VERSION=" + getVersion(activity);
    }

    public static String getVersion(Activity activity) {
        try {
            return activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "4.6.1";
    }

    public static Date getDate(String strDate) {
        try {
            if (strDate == null || strDate.equalsIgnoreCase("") || strDate.equalsIgnoreCase("0000-00-00 00:00:00")) {
                return null;
            } else {
                Date date = dateFormate.parse(strDate);
                return dateFormate.parse(dateFormate.format(date));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getLocalDate_and_Time(Date savedDate, String timezone) {
        String localDateTime;
        try {
            String serverDateTimeString = dateFormate.format(savedDate);
            Log.v("serverDateTimeString: ", "" + serverDateTimeString);
            SimpleDateFormat serverFormatter = new SimpleDateFormat(DATE_TIME_FORMATE);
            serverFormatter.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
            Date serverDateTime = serverFormatter.parse(serverDateTimeString);

            SimpleDateFormat localFormatter = new SimpleDateFormat(DISPLAY_DATE_FORMATE);
            localFormatter.setTimeZone(TimeZone.getTimeZone(timezone));
            localDateTime = localFormatter.format(serverDateTime);

        } catch (Exception e) {
            e.printStackTrace();
            localDateTime = "";
        }
        return localDateTime;
    }

    public static String detect_shipping_carrier(String tracking_number) {
        int trackingNumberLength = tracking_number.length();
        String shippingCarrier = "";
        // UPS
        if ((trackingNumberLength == 18 && tracking_number.toUpperCase().startsWith("1Z")) || trackingNumberLength == 11) {
            shippingCarrier = "UPS";
        } else if (tracking_number.toUpperCase().startsWith("TB")) {
            // Amazon
            shippingCarrier = "Amazon";
        } else if ((trackingNumberLength == 15 && StringUtils.isNumeric(tracking_number)) ||
                (trackingNumberLength == 12 && StringUtils.isNumeric(tracking_number)) ||
                (trackingNumberLength == 22 && tracking_number.startsWith("96")) ||
                (trackingNumberLength == 34 && StringUtils.isNumeric(tracking_number) && !tracking_number.startsWith("42")) ||
                (trackingNumberLength == 32 && StringUtils.isNumeric(tracking_number))) {
            // FedEx
            shippingCarrier = "FedEx";
        } else if (trackingNumberLength == 10 && StringUtils.isNumeric(tracking_number) ||
                tracking_number.toUpperCase().startsWith("J")) {
            // DHL
            shippingCarrier = "DHL";
        } else if ((trackingNumberLength == 13 && tracking_number.toUpperCase().endsWith("CA") ||
                (trackingNumberLength == 16 && StringUtils.isNumeric(tracking_number)))) {
            // Canada Post
            shippingCarrier = "Canada Post";
        } else if ((trackingNumberLength == 20 && StringUtils.isNumeric(tracking_number)) ||
                (trackingNumberLength == 26 && StringUtils.isNumeric(tracking_number)) ||
                (trackingNumberLength == 30 && StringUtils.isNumeric(tracking_number)) ||
                trackingNumberLength == 13 ||
                (trackingNumberLength == 22 && tracking_number.startsWith("9")) ||
                (trackingNumberLength >= 29 && StringUtils.isNumeric(tracking_number) && tracking_number.startsWith("42"))) {
            // USPS
            shippingCarrier = "US Postal Service";
        } else if ((trackingNumberLength == 15 && tracking_number.toUpperCase().startsWith("C"))) {
            //OnTrac
            shippingCarrier = "OnTrac";
        } else if ((trackingNumberLength == 10 && tracking_number.toUpperCase().startsWith("L"))) {
            //LaserShip
            shippingCarrier = "LaserShip";
        } else if ((tracking_number.toUpperCase().startsWith("G") ||
                (tracking_number.toUpperCase().startsWith("C1|")) ||
                (trackingNumberLength == 9 && StringUtils.isNumeric(tracking_number)) ||
                (trackingNumberLength == 16 && String.valueOf(tracking_number.charAt(7)).equals("-")))) {
            //Golden State Overnight
            shippingCarrier = "GLS";
        } else if (!tracking_number.trim().isEmpty()) {
            //shipping_carrier not known
            shippingCarrier = Extras_Keys.OTHERS;
        }
        return shippingCarrier;
    }

    public static String getRecipientAddress1Label(String accountType) {

        if (accountType != null) {
            if (accountType.toLowerCase().equalsIgnoreCase("apt")) {

                return "Unit Number"; //define("RECIPIENT_ADDRESS1_LABEL", "Unit Number");

            } else if (accountType.toLowerCase().equalsIgnoreCase("edu")) {

                return "Campus Address"; // define("RECIPIENT_ADDRESS1_LABEL", "Campus Address");

            } else if (accountType.toLowerCase().equalsIgnoreCase("inc")) {

                return "Mailstop"; //define("RECIPIENT_ADDRESS1_LABEL", "Mailstop");

            } else if (accountType.toLowerCase().equalsIgnoreCase("mpc")) {

                return "Mailbox Number"; //	define("RECIPIENT_ADDRESS1_LABEL", "Mailbox Number");

            }
        }
        return "";
    }

    public static void getBeepSound(Context context) {
        try {
            AssetFileDescriptor afd = context.getAssets().openFd("beep.mp3");
            MediaPlayer player = new MediaPlayer();
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            player.prepare();
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setSpinnerPopUpHeight(Spinner spinner, boolean isTablet, int itemscount) {
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);
            if (itemscount > 5) {
                android.widget.ListPopupWindow facilitiespopup = (android.widget.ListPopupWindow) popup.get(spinner);
                if (isTablet) {
                    facilitiespopup.setHeight(300);
                } else {
                    facilitiespopup.setHeight(500);
                }
            }
        } catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            Log.e("SPINNERPOPUPERROR", e.getMessage());
        }
    }

    public static void setSpinnerHeight(Activity activity, Spinner mSpinner) {
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);
            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(mSpinner);
            // Set popupWindow height to 500px
            popupWindow.setHeight(((int) activity.getResources().getDimension(R.dimen.edittext_height) * 8) + (int) ((activity.getResources().getDimension(R.dimen.edittext_height) / activity.getResources().getDisplayMetrics().density)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Getting GlobalConstants Data
    public static JSONObject getGlobalData(Context context) {
        File sdcard = context.getFilesDir();
        File file = new File(sdcard, GLOBAL_DATA_FILE_NAME);

        StringBuilder text = new StringBuilder();
        JSONObject jsonObject = null;

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();

            jsonObject = new JSONObject(text.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return jsonObject;
        }
    }

    // Getting Status List for Add And Edit Recipient Data
    public static ArrayList<SpinnerData> getStatusList() {

        ArrayList<SpinnerData> list = new ArrayList<>();
        SpinnerData spinnerDataCurrent = new SpinnerData();
        spinnerDataCurrent.setValue("1");
        spinnerDataCurrent.setName("Current");
        SpinnerData spinnerDataFormer = new SpinnerData();
        spinnerDataFormer.setValue("0");
        spinnerDataFormer.setName("Former");

        list.add(spinnerDataCurrent);
        list.add(spinnerDataFormer);

        return list;
    }

    public static void setCalendarSpinnerHeight(Activity activity, Spinner mSpinner) {
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);
            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(mSpinner);
            // Set popupWindow height to 500px
            popupWindow.setHeight(((int) activity.getResources().getDimension(R.dimen.calendar_spinner) * 10) + (int) ((activity.getResources().getDimension(R.dimen.calendar_spinner) / activity.getResources().getDisplayMetrics().density)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static AlertDialog dialog;

    public static void showImageAlert(Context context, String imageString) {
        try {

            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_image_fullview, null);
            FrameLayout frameLayout = (FrameLayout) dialogView.findViewById(R.id.image_frameLayout);
            final ImageView imageView = (ImageView) dialogView.findViewById(R.id.imageFullView);

            Glide.with(context)
                    .load(imageString)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
            AlertDialog.Builder builder = null;
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            builder.setView(dialogView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });

            frameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });

            dialog = builder.create();
            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showBouncingAlertDialog(Activity activity, boolean isEmail, Recipient response) {
        try {
            int width = activity.getResources().getDisplayMetrics().widthPixels;
            final Dialog dialog = new Dialog(activity, R.style.DialogTheme);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_bounce_layout);
            dialog.setCanceledOnTouchOutside(false);

            // set the custom dialog components - text, image and button
            LinearLayout parentLinear = (LinearLayout) dialog.findViewById(R.id.parentLayout);
            TextView mAlert = (TextView) dialog.findViewById(R.id.textView_bounce_alert);
            TextView mReason = (TextView) dialog.findViewById(R.id.textView_bounce_reason);

            if (isEmail) {
                mAlert.setVisibility(View.VISIBLE);
                mReason.setVisibility(View.VISIBLE);
                mAlert.setText("Bounce alert: \n" + response.getEmail_bounce_alert());
                mReason.setText("Bounce reason: \n" + response.getEmail_bounce_reason());
            } else {
                // If phone type error is "1" - the phone number is land line or voip number.So,they cann't receive messages
                //                              in this case only the phone_type_reason should be display on screen
                //                        "0"  - same functionality is done like email_bounced

                if (response.getPhone_bounced().equals("1")) {
                    mAlert.setVisibility(View.VISIBLE);
                    mReason.setVisibility(View.VISIBLE);
                    mAlert.setText("Bounce alert: \n" + response.getPhone_bounce_alert());
                    if (response.getPhone_type_error().equals("1")) {
                        mReason.setText("Bounce reason: \n" + response.getPhone_bounce_reason() + "\n" + response.getPhone_type_reason());
                    } else {
                        mReason.setText("Bounce reason: \n" + response.getPhone_bounce_reason());
                    }
                }
                if (response.getPhone_bounced().equals("0") && response.getPhone_type_error().equals("1")) {
                    mAlert.setVisibility(View.VISIBLE);
                    mReason.setVisibility(View.GONE);
                    mAlert.setText(response.getPhone_type_reason());
                }
            }
            parentLinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            if (dialog == null || dialog.getWindow() == null) {
                return;
            }
            int dialogWidth = (int) (width * 0.90f);
            if (activity.getResources().getBoolean(R.bool.isTablet)) {
                dialogWidth = (int) (width * 0.75f);
            }
            int dialogHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(dialogWidth, dialogHeight);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Displaying of Warning Alert in LogPackageIn Response.
    public static void showSpecialTrackInsAlert(final Activity act, final boolean isActivityFinish, String specialInstructionsText, String onVacationText, String specialInstructionsFlag) {
        if (act != null) {
            try {
                int width = act.getResources().getDisplayMetrics().widthPixels;
                int height = act.getResources().getDisplayMetrics().heightPixels;
                final Dialog dialog = new Dialog(act);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_alert_special_instructions);
                dialog.setCanceledOnTouchOutside(false);
                // set the custom dialog components - text, image and button
                LinearLayout parentLayout = (LinearLayout) dialog.findViewById(R.id.parentLayout);
                ScrollView scrollView = (ScrollView) dialog.findViewById(R.id.scroll_view);
                final TextView mMessage = (TextView) dialog.findViewById(R.id.message);
                String customText = "";
                boolean tabletSize = act.getResources().getBoolean(R.bool.isTablet);

                if (specialInstructionsText != null && !specialInstructionsText.isEmpty() && !specialInstructionsText.equalsIgnoreCase("null") && onVacationText != null && !onVacationText.isEmpty() && !onVacationText.equalsIgnoreCase("null")) {

                    if (specialInstructionsText.contains("\n")) {
                        specialInstructionsText = specialInstructionsText.replaceAll("\n", "<br>");
                    }
                    if (onVacationText.contains("\n")) {
                        onVacationText = onVacationText.replaceAll("\n", "<br>");
                    }
                    if (!specialInstructionsFlag.equalsIgnoreCase("none")) {
                        if (tabletSize) {
                            scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT, 300));
                        } else {
                            scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT, 500));
                        }
                        customText = "<font color='#f26522'>" + "Special Instructions: " + specialInstructionsText + "<br>" + "<br>" + onVacationText + "</font>";
                    } else {
                        if (tabletSize) {
                            scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT, 150));
                        } else {
                            scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT, 500));
                        }
                        customText = "<font color='#f26522'>" + onVacationText + "</font>";
                    }
                } else if (specialInstructionsText != null && !specialInstructionsText.isEmpty() && !specialInstructionsText.equalsIgnoreCase("null")) {
                    if (tabletSize) {
                        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, 200));
                    } else {
                        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, 400));
                    }

                    if (specialInstructionsText.contains("\n")) {
                        specialInstructionsText = specialInstructionsText.replaceAll("\n", "<br>");
                    }
                    if (!specialInstructionsFlag.equalsIgnoreCase("none")) {
                        customText = "<font color='#f26522'>" + "Special Instructions: " + specialInstructionsText + "</font>";
                    }
                } else if (onVacationText != null && !onVacationText.isEmpty() && !onVacationText.equalsIgnoreCase("null")) {
                    if (tabletSize) {
                        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, 150));
                    } else {
                        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, 350));
                    }
                    if (onVacationText.contains("\n")) {
                        onVacationText = onVacationText.replaceAll("\n", "<br>");
                    }
                    customText = "<font color='#f26522'>" + onVacationText + "</font>";
                }
                mMessage.setText(Html.fromHtml(customText));

                parentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (isActivityFinish) {
                            act.onBackPressed();
                        }
                    }
                });

                mMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (isActivityFinish) {
                            act.onBackPressed();
                        }
                    }
                });

                if (dialog == null || dialog.getWindow() == null) {
                    return;
                }

                int dialogWidth = (int) (width * 0.90f);
                if (tabletSize) {
                    dialogWidth = (int) (width * 0.75f);
                }

                dialog.getWindow().setGravity(Gravity.CENTER);
                WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
                dialog.getWindow().setAttributes(layoutParams);
                int dialogHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setLayout(dialogWidth, dialogHeight);
                dialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Getting MailRoomName
    public static String getMailroomName(String mailroomId, ArrayList<SpinnerData> mMailroomList) {
        if (mMailroomList != null && !mMailroomList.isEmpty()) {
            for (SpinnerData item : mMailroomList) {
                if (item.getValue().equalsIgnoreCase(mailroomId)) {
                    return item.getName();
                }
            }
        }
        return "";
    }

    public static void showInSufficientPrivelegeAlert(Activity act) {
        NTF_Utils.showAlert(act, "", act.getString(R.string.insufficient_privileges), null);
    }

    public static void showLogPkgInAlert(final Activity act, String title, LogPkgInResponse response) {
        if (act != null) {
            try {
                String specialInstructionsText = response.getSpecialTrackInstructions();
                String onVacationText = response.getVacationMessage();
                String message = response.getApiMessage();
                int width = act.getResources().getDisplayMetrics().widthPixels;
                final Dialog dialog = new Dialog(act, R.style.DialogTheme);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_alert);
                dialog.setCanceledOnTouchOutside(false);
                LinearLayout parentLayout = (LinearLayout) dialog.findViewById(R.id.parentLayout);
                ScrollView scrollView = (ScrollView) dialog.findViewById(R.id.scroll_view);
                ImageView imageView = (ImageView) dialog.findViewById(R.id.imageView);
                TextView mTitle = (TextView) dialog.findViewById(R.id.title);
                final TextView mMessage = (TextView) dialog.findViewById(R.id.message);
                mTitle.setText(title);
                mTitle.setTextColor(ActivityCompat.getColor(act, R.color.pkg_success_color));
                String text = "";
                boolean tabletSize = act.getResources().getBoolean(R.bool.isTablet);

                if (specialInstructionsText != null && !specialInstructionsText.isEmpty() && onVacationText != null && !onVacationText.isEmpty()) {
                    if (tabletSize) {
                        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, 300));
                    } else {
                        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, 500));
                    }
                    if (specialInstructionsText.contains("\n")) {
                        specialInstructionsText = specialInstructionsText.replaceAll("\n", "<br>");
                    }
                    if (onVacationText.contains("\n")) {
                        onVacationText = onVacationText.replaceAll("\n", "<br>");
                    }
                    String customText = "<font color='#f26522'>" + "<br>" + "<br>" + "Special Instructions: " + specialInstructionsText + "<br>" + "<br>" + onVacationText + "</font>";
                    text = message + customText;
                } else if (specialInstructionsText != null && !specialInstructionsText.isEmpty()) {
                    if (tabletSize) {
                        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, 200));
                    } else {
                        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, 400));
                    }

                    if (specialInstructionsText.contains("\n")) {
                        specialInstructionsText = specialInstructionsText.replaceAll("\n", "<br>");
                    }
                    mMessage.setPadding(0, 0, 20, 0);
                    String customText = "<font color='#f26522'>" + "<br>" + "<br>" + "Special Instructions: " + specialInstructionsText + "</font>";
                    text = message + customText;
                } else if (onVacationText != null && !onVacationText.isEmpty()) {
                    if (tabletSize) {
                        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, 200));
                    } else {
                        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, 350));
                    }
                    if (onVacationText.contains("\n")) {
                        onVacationText = onVacationText.replaceAll("\n", "<br>");
                    }
                    String customText = "<font color='#f26522'>" + "<br>" + "<br>" + onVacationText + "</font>";
                    text = message + customText;
                } else {
                    text = message;
                }
                mMessage.setText(Html.fromHtml(text));
                if (title.equals(ALERT_SUCCESS_TITLE) || title.equals(ALERT_SUCCESS_TITLE_AWESOME_JOB)) {
                    mTitle.setTextColor(act.getResources().getColor(R.color.success_color));
                    imageView.setImageResource((R.drawable.ic_success_icon));
                } else if (title.contains(ALERT_WARNING_TITLE)) {
                    mTitle.setTextColor(act.getResources().getColor(R.color.pkg_warning_color));
                    imageView.setImageResource((R.drawable.ic_warning_icon));
                } else {
                    mTitle.setTextColor(act.getResources().getColor(R.color.info_color));
                    imageView.setImageResource((R.drawable.ic_info_icon));
                }

                parentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                mMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                if (dialog == null || dialog.getWindow() == null) {
                    return;
                }

                int dialogWidth = (int) (width * 0.90f);
                if (tabletSize) {
                    dialogWidth = (int) (width * 0.75f);
                }

                dialog.getWindow().setGravity(Gravity.CENTER);
                WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
                dialog.getWindow().setAttributes(layoutParams);
                int dialogHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setLayout(dialogWidth, dialogHeight);
                dialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static ArrayList<SpinnerData> filterPKGStatus(ArrayList<SpinnerData> data) {
        ArrayList<SpinnerData> spinnerData = new ArrayList<SpinnerData>();
        for (SpinnerData spinnerData1 : data) {
            if (!spinnerData1.getValue().equalsIgnoreCase("10200")) {
                spinnerData.add(spinnerData1);
            }
        }
        return spinnerData;
    }

    public static void checkCameraPermissionToProgress(Activity activity, Runnable runnable) {
        try {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                if (runnable != null)
                    runnable.run();
            } else {
                Dexter.withContext(activity)
                        .withPermission(Manifest.permission.CAMERA)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                if (runnable != null)
                                    runnable.run();
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                if (response.isPermanentlyDenied()) {
                                    confirmAlert(activity);
                                } else {
                                    Toast.makeText(activity, R.string.camera_permission_not_granted, Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */
                                token.continuePermissionRequest();
                            }
                        }).check();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void checkPermissionToProgress(Activity activity, Runnable runnable) {
        try {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                runnable.run();
            } else {

                Dexter.withContext(activity)
                        .withPermissions(
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.isAnyPermissionPermanentlyDenied()) {
                                    confirmAlert(activity);
                                } else if (report.areAllPermissionsGranted()) {
                                    runnable.run();
                                } else {
                                    Toast.makeText(activity, R.string.camera_permission_not_granted, Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).
                        withErrorListener(new PermissionRequestErrorListener() {
                            @Override
                            public void onError(DexterError error) {
                                Toast.makeText(activity, "Something went wrong, please try again later. ", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .onSameThread()
                        .check();

                /*Dexter.withActivity(activity)
                        .withPermission(Manifest.permission.CAMERA)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                runnable.run();
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                Toast.makeText(activity, R.string.camera_permission_not_granted, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {*//* ... *//*
                                token.continuePermissionRequest();
                            }
                        }).check();*/

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void confirmAlert(Activity activity) {
        try {
            AlertDialog dialog = new AlertDialog.Builder(activity)
                    .setTitle("Camera/Storage Access Denied")
                    .setMessage("Notifii Track requires access to your device camera and storage.\nPlease change preferences in settings.")
                    .setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }
                            }
                    )
                    .setNegativeButton("Settings",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                                    intent.setData(uri);
                                    activity.startActivity(intent);
                                }
                            }
                    ).create();
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Intent getCameraIntent(Activity activity) {
        SingleTon.getInstance().setRequestCode(0);
        SingleTon.getInstance().setCapturedBitmap(null);
        Intent intent = null;
        try {
            String model = getDeviceName();
            model = model.replaceAll(" ", "");
            if (model.toLowerCase().contains("huawei") && model.toLowerCase().contains("x2") || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                intent = new Intent(activity, CameraPreviewActivity.class);
            } else {
//                intent = new Intent(activity, CameraPreviewActivity.class);
                intent = new Intent(activity, CameraPreviewActivity2.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return intent;
    }

    private static String getDeviceName() {
        try {
            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;
            if (model.startsWith(manufacturer)) {
                return capitalize(model);
            }
            return capitalize(manufacturer) + " " + model;

        } catch (Exception e) {
            return "";
        }
    }

    private static String capitalize(String str) {
        try {
            if (TextUtils.isEmpty(str)) {
                return str;
            }
            char[] arr = str.toCharArray();
            boolean capitalizeNext = true;
            StringBuilder phrase = new StringBuilder();
            for (char c : arr) {
                if (capitalizeNext && Character.isLetter(c)) {
                    phrase.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                    continue;
                } else if (Character.isWhitespace(c)) {
                    capitalizeNext = true;
                }
                phrase.append(c);
            }
            return phrase.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public static String getErrorMessage(Throwable error) {
        String errorResponse = error.getMessage();
        //UnknownHostException, SocketException - These Exceptions happens when device connects to wifi but no internet for that wifi.
        if (error instanceof TimeoutException || error instanceof SocketTimeoutException) {
//            errorResponse = "Your request has been timed out. Please try again, if the problem persists contact the administrator.";
            errorResponse = "Please check your internet connectivity.";
        } else if (error instanceof IOException) {
            errorResponse = "You are not connected to the Internet.";
        } else if (error instanceof JSONException) {
            errorResponse = "Unexpected response.\nPlease try again later.";
        } else if (error instanceof Exception) {
            errorResponse = "Something went wrong.\nPlease try again later.";
        }
        return errorResponse;
    }

    public static String getTimeZoneTYPE(String timeZone, String date, NTF_PrefsManager ntf_Preferences) {
        String timezone = "";
        if ("America/Puerto_Rico".equalsIgnoreCase(timeZone)) {
            timezone = "AST";//ast
        } else if ("America/New_York".equalsIgnoreCase(timeZone)) {
            timezone = "EST";//est
        } else if ("America/Chicago".equalsIgnoreCase(timeZone)) {
            timezone = "CST";//cst
        } else if ("America/Denver".equalsIgnoreCase(timeZone)) {
            timezone = "MST";//mst
        } else if ("America/Los_Angeles".equalsIgnoreCase(timeZone)) {
            timezone = "PST";//pst
        } else if ("America/Anchorage".equalsIgnoreCase(timeZone)) {
            timezone = "AKST";//akst
        } else if ("Pacific/Honolulu".equalsIgnoreCase(timeZone)) {
            timezone = "HST";//hst
        } else if ("Europe/Copenhagen".equalsIgnoreCase(timeZone)) {
            timezone = "CEST";//cest
        } else if ("Australia/Victoria".equalsIgnoreCase(timeZone)) {
            timezone = "AEST";//aest
        } else if ("Europe/London".equalsIgnoreCase(timeZone)) {
            timezone = "BST";//bst
        } else if ("Europe/Madrid".equalsIgnoreCase(timeZone)) {
            timezone = "CEST";//cest
        } else if ("Asia/Shanghai".equalsIgnoreCase(timeZone)) {
            timezone = "CST";//cst
        } else if ("Asia/Bangkok".equalsIgnoreCase(timeZone)) {
            timezone = "+07";//cst
        }
        Date date1 = getDateFromString(date, ntf_Preferences);

        /*TimeZone tz = TimeZone.getTimeZone (timeZone) ;
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date1);
        Date date3 = new GregorianCalendar(calendar.get(Calendar.YEAR)-1900, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).getTime () ;
        if (tz.inDaylightTime(date3)) {
            timezone = timezone.replace('S', 'D');
        }*/

        if (TimeZone.getTimeZone(ntf_Preferences.get(Prefs_Keys.TIMEZONE)).inDaylightTime(date1)) {
            timezone = timezone.replace('S', 'D');
        }
        return timezone;
    }

    private static Date getDateFromString(String serverDateTimeString, NTF_PrefsManager ntf_Preferences) {
        try {
            SimpleDateFormat serverFormatter = new SimpleDateFormat(DATE_TIME_FORMATE);
            serverFormatter.setTimeZone(TimeZone.getTimeZone(ntf_Preferences.get(Prefs_Keys.TIMEZONE)));
            Date serverDateTime = serverFormatter.parse(serverDateTimeString);
            return serverDateTime;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setDropDownHeight(int count, AutoCompleteTextView autoCompleteTextView, Activity activity) {
        if (count > 8) {
            autoCompleteTextView.setDropDownHeight((int) ((8 * (activity.getResources().getDimension(R.dimen.edittext_height) + 2))));
        } else {
            autoCompleteTextView.setDropDownHeight((int) ((count * (activity.getResources().getDimension(R.dimen.edittext_height) + 5))));
        }
    }

    public static void setTypefaceForACTV(AutoCompleteTextView autoCompleteTextView, Activity activity) {
        try {
            Typeface face = Typeface.createFromAsset(activity.getAssets(),
                    "font/avenir_roman.ttf");
            autoCompleteTextView.setTypeface(face);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void setUsernameDropdownHeight(int count, AutoCompleteTextView autoCompleteTextView, Activity activity) {
        try {
            if (count > 4) {
                autoCompleteTextView.setDropDownHeight((int) ((4 * (activity.getResources().getDimension(R.dimen.edittext_height) + 2))));
            } else {
                autoCompleteTextView.setDropDownHeight((int) ((count * (activity.getResources().getDimension(R.dimen.edittext_height) + 5))));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void setSpinnerDropDownHeight(int count, Spinner spinner, Activity activity) {
        try {
            if (count > 8) {
                Field popup = Spinner.class.getDeclaredField("mPopup");
                popup.setAccessible(true);
                android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(spinner);
                popupWindow.setHeight((int) ((8 * (activity.getResources().getDimension(R.dimen.edittext_height) + 2))));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class ForegroundCheckTask extends AsyncTask<Context, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Context... params) {
            final Context context = params[0].getApplicationContext();
            return isAppOnForeground(context);
        }

        private boolean isAppOnForeground(Context context) {
            try {
                ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
                if (appProcesses == null) {
                    return false;
                }
                final String packageName = context.getPackageName();
                for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                    if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                        return true;
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            return false;
        }
    }

    public static boolean isAppOnForeground(Context context){
        boolean foregroud=true;
        try {
            foregroud = new ForegroundCheckTask().execute(context).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return foregroud;
    }

    public static void handleBackgroundCallResponse(Intent intent,Activity activity){
        if (intent.getStringExtra(Extras_Keys.BACKGROUNG_API_STATUS).equals(SESSION_EXPIRED)) {
            showSessionExpireAlert(intent.getStringExtra(Extras_Keys.BACKGROUNG_API_MESSAGE), activity, ntf_Preferences);
        } else if (intent.getStringExtra(Extras_Keys.BACKGROUNG_API_STATUS).equals(ERROR)) {
            showAlert(activity, ALERT_ERROR_TITLE_OOPS, intent.getStringExtra(Extras_Keys.BACKGROUNG_API_MESSAGE), null);
        }
    }
}
