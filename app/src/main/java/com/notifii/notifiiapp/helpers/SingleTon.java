package com.notifii.notifiiapp.helpers;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;

import com.notifii.notifiiapp.models.Recipient;
import com.notifii.notifiiapp.models.SpinnerData;
import com.notifii.notifiiapp.utils.NTF_Constants;
import com.notifii.notifiiapp.utils.NTF_Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SingleTon {

    private static SingleTon instance = new SingleTon();
    private JSONArray ocrArray;
    private ArrayList<Recipient> mRecipientsList;
    private Recipient recipient;
    private boolean logPkgInNeedRefesh = true;
    private ArrayList<SpinnerData> mMailRoomList;
    private JSONObject mGobalJsonData;
    private String pendingPackagesAPIMode = "general_action";
    private Bitmap capturedBitmap = null;
    private int requestCode = 0;

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public Bitmap getCapturedBitmap() {
        return capturedBitmap;
    }

    public void setCapturedBitmap(Bitmap capturedBitmap) {
        this.capturedBitmap = capturedBitmap;
    }

    public boolean isLogPkgInNeedRefesh() {
        return logPkgInNeedRefesh;
    }

    public void setLogPkgInNeedRefesh(boolean logPkgInNeedRefesh) {
        this.logPkgInNeedRefesh = logPkgInNeedRefesh;
    }

    public Bitmap getBitmap1() {
        return bitmap1;
    }

    public void setBitmap1(Bitmap bitmap1) {
        this.bitmap1 = bitmap1;
    }

    public Bitmap getBitmap2() {
        return bitmap2;
    }

    public void setBitmap2(Bitmap bitmap2) {
        this.bitmap2 = bitmap2;
    }

    public Bitmap getBitmap3() {
        return bitmap3;
    }

    public void setBitmap3(Bitmap bitmap3) {
        this.bitmap3 = bitmap3;
    }

    private Bitmap bitmap1;
    private Bitmap bitmap2;
    private Bitmap bitmap3;


    public Recipient getRecipient() {
        return recipient;
    }

    public void setRecipient(Recipient recipient) {
        this.recipient = recipient;
    }

    public static SingleTon getInstance() {
        return instance;
    }

    public String getPendingPackagesAPIMode() {
        return pendingPackagesAPIMode;
    }

    public void setPendingPackagesAPIMode(String pendingPackagesAPIMode) {
        this.pendingPackagesAPIMode = pendingPackagesAPIMode;
    }

    public void setmGobalJsonData(JSONObject mGobalJsonData) {
        this.mGobalJsonData = mGobalJsonData;
    }

    public JSONObject getGlobalJson(Activity activity) {
        if (mGobalJsonData != null) {
            return mGobalJsonData;
        } else {
            mGobalJsonData = NTF_Utils.getGlobalData(activity);
            return mGobalJsonData;
        }
    }

    public void setmMailRoomList(ArrayList<SpinnerData> mMailRoomList) {
        this.mMailRoomList = mMailRoomList;
    }

    public ArrayList<SpinnerData> getmMailRoomList(Activity activity) {
        try {
            if (mMailRoomList == null || mMailRoomList.size() == 0) {
                JSONObject mGobalJsonData = SingleTon.getInstance().getGlobalJson(activity);
                mMailRoomList = SpinnerData.getList(mGobalJsonData.getJSONArray("mailrooms"), null).getList();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return mMailRoomList;
        }
    }


    public static void clearInstance() {
        instance = new SingleTon();
    }

    public ArrayList<Recipient> getmRecipientList() {
        return mRecipientsList;
    }

    public void setmRecipientListForOcr(ArrayList<Recipient> mRecipientsList) {
        this.mRecipientsList = mRecipientsList;
    }

    public JSONArray getOcrArray() {
        try {
            if (ocrArray != null) {
                return ocrArray;
            } else {
                return new JSONArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    public void setOcrArray(JSONArray ocrArray) {
        this.ocrArray = ocrArray;
    }


}
