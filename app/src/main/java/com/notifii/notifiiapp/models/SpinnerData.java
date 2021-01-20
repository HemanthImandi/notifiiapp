package com.notifii.notifiiapp.models;

import android.app.Activity;

import com.notifii.notifiiapp.R;
import com.notifii.notifiiapp.utils.NTF_Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SpinnerData {

    private String name;
    private String value;
    private String require_app_signature = "";
    boolean selected;

    public static void resetData(ArrayList<SpinnerData> list) {
        try{
            if(list != null) {
                for (SpinnerData data : list) {
                    data.setSelected(false);
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getRequire_app_signature() {
        return require_app_signature;
    }

    public void setRequire_app_signature(String require_app_signature) {
        this.require_app_signature = require_app_signature;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    //This method deals with the Response Parsing of Corresponding Spinner values.
    public static ListModel getList(JSONArray jsonArray, String selectValue) {
        ListModel listModel=new ListModel();
        ArrayList<SpinnerData> list = null;
        String selected = selectValue != null ? selectValue : "";
        try {
            if (jsonArray != null && jsonArray.length() > 0) {
                list = new ArrayList<>();
                for (int i = 0, len = jsonArray.length(); i < len; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    SpinnerData spinnerData = new SpinnerData();
                    spinnerData.setName(jsonObject.optString("name"));
                    spinnerData.setValue(jsonObject.optString("value"));
                    spinnerData.setRequire_app_signature(jsonObject.optString("require_app_signature"));
                    if (selected.equalsIgnoreCase(spinnerData.getValue())) {
                        spinnerData.setSelected(true);
                        listModel.setPosition(i);
                    } else {
                        spinnerData.setSelected(false);
                    }
                    list.add(spinnerData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        listModel.setList(list);
        return listModel;
    }

    public static ArrayList<SpinnerData> getListSearchLogic(boolean enablePlaceHolder) {
        ArrayList<SpinnerData> searchLogicList = new ArrayList<>();
        SpinnerData anySpinnerData = new SpinnerData();
        anySpinnerData.setName("Find packages that match ANY search criterion");
        anySpinnerData.setValue("0");
        SpinnerData allSpinnerData = new SpinnerData();
        allSpinnerData.setName("Find packages that match ALL search criteria");
        allSpinnerData.setValue("1");
        searchLogicList.add(0,anySpinnerData);
        searchLogicList.add(1,allSpinnerData);
        return searchLogicList;
    }

    public static ArrayList<SpinnerData> getListDateRange() {
        ArrayList<SpinnerData> searchLogicList = new ArrayList<>();

        SpinnerData spinnerData3months = new SpinnerData();
        spinnerData3months.setName("Limit search to last 3 months");
        spinnerData3months.setValue("last_3_months");

        SpinnerData spinnerData12months = new SpinnerData();
        spinnerData12months.setName("Limit search to last 12 months");
        spinnerData12months.setValue("last_12_months");

        SpinnerData spinnerDataunlimit= new SpinnerData();
        spinnerDataunlimit.setName("Search entire account history");
        spinnerDataunlimit.setValue("all");

        searchLogicList.add(0,spinnerData3months);
        searchLogicList.add(1,spinnerData12months);
        searchLogicList.add(2,spinnerDataunlimit);
        return searchLogicList;
    }

    public static ArrayList<SpinnerData> getListStatusFilter(String accountTypeName, boolean enablePlaceHolder) {
        ArrayList<SpinnerData> statusFilterList = new ArrayList<>();
        SpinnerData currentSpinnerData = new SpinnerData();
        currentSpinnerData.setName("Search only current "+accountTypeName.toLowerCase()+"s");
        currentSpinnerData.setValue("active");
        SpinnerData formerSpinnerData = new SpinnerData();
        formerSpinnerData.setName("Search only former "+accountTypeName.toLowerCase()+"s");
        formerSpinnerData.setValue("inactive");
        SpinnerData currentFormerData = new SpinnerData();
        currentFormerData.setName("Search both current and former "+accountTypeName.toLowerCase()+"s");
        currentFormerData.setValue("active/inactive");
        statusFilterList.add(0,currentSpinnerData);
        statusFilterList.add(1,formerSpinnerData);
        statusFilterList.add(2,currentFormerData);

     /*   if (statusFilterList != null && enablePlaceHolder) {
            statusFilterList.add(0, NTF_Utils.getPlaceHolderObj());
        }*/
        return statusFilterList;
    }

    public static ArrayList<SpinnerData> getStayLoggedInSpinnerData() {
        ArrayList<SpinnerData> stayLoggedInList = new ArrayList<>();

        SpinnerData loggedInFor12HrsData = new SpinnerData();
        loggedInFor12HrsData.setName("Stay logged in for 12 hours");
        loggedInFor12HrsData.setValue("12 Hours");

        SpinnerData loggedInFor7DaysData = new SpinnerData();
        loggedInFor7DaysData.setName("Stay logged in for 7 days");
        loggedInFor7DaysData.setValue("7 Days");

        SpinnerData loggedInFor30DaysData = new SpinnerData();
        loggedInFor30DaysData.setName("Stay logged in for 30 days");
        loggedInFor30DaysData.setValue("30 Days");

        SpinnerData loggedInUntilILogOutData = new SpinnerData();
        loggedInUntilILogOutData.setName("Stay logged until I log out");
        loggedInUntilILogOutData.setValue("10 Years");

        stayLoggedInList.add(0, loggedInFor12HrsData);
        stayLoggedInList.add(1, loggedInFor7DaysData);
        stayLoggedInList.add(2, loggedInFor30DaysData);
        stayLoggedInList.add(3, loggedInUntilILogOutData);

        return stayLoggedInList;
    }

    public static ArrayList<SpinnerData> getResidentListSearchLogic(boolean enablePlaceHolder) {
        ArrayList<SpinnerData> searchLogicList = new ArrayList<>();
        SpinnerData anySpinnerData = new SpinnerData();
        anySpinnerData.setName("Find names that match ANY search criterion");
        anySpinnerData.setValue("OR");
        SpinnerData allSpinnerData = new SpinnerData();
        allSpinnerData.setName("Find names that match ALL search criteria");
        allSpinnerData.setValue("AND");
        searchLogicList.add(0,anySpinnerData);
        searchLogicList.add(1,allSpinnerData);

      /*  if (searchLogicList != null && enablePlaceHolder) {
            searchLogicList.add(0, NTF_Utils.getPlaceHolderObj());
        }*/
        return searchLogicList;
    }

    public static ArrayList<SpinnerData> getSendPkgLoginNotifiiSpinnerData(boolean enablePlaceHolder) {
        ArrayList<SpinnerData> searchLogicList = new ArrayList<>();
        SpinnerData useSpinnerData = new SpinnerData();
        useSpinnerData.setName("Use account settings");
        useSpinnerData.setValue("a");
        SpinnerData overridesendSpinnerData = new SpinnerData();
        overridesendSpinnerData.setName("Override account settings, send a notification");
        overridesendSpinnerData.setValue("y");
        SpinnerData overrideDonotSendSpinnerData = new SpinnerData();
        overrideDonotSendSpinnerData.setName("Override account settings, do not send a notification");
        overrideDonotSendSpinnerData.setValue("n");
        searchLogicList.add(0,useSpinnerData);
        searchLogicList.add(1,overridesendSpinnerData);
        searchLogicList.add(2,overrideDonotSendSpinnerData);

      /*  if (searchLogicList != null && enablePlaceHolder) {
            searchLogicList.add(0, NTF_Utils.getPlaceHolderObj());
        }*/
        return searchLogicList;
    }

    public static ArrayList<SpinnerData> getSpecialTrackInsFlagSpinnerData(boolean enablePlaceHolder) {
        ArrayList<SpinnerData> searchLogicList = new ArrayList<>();
        SpinnerData noneSpinnerData = new SpinnerData();
        noneSpinnerData.setName("None");
        noneSpinnerData.setValue("none");
        SpinnerData pkgLoginSpinnerData = new SpinnerData();
        pkgLoginSpinnerData.setName("Package Login");
        pkgLoginSpinnerData.setValue("log_in");
        SpinnerData pkgLogoutSendSpinnerData = new SpinnerData();
        pkgLogoutSendSpinnerData.setName("Package Logout");
        pkgLogoutSendSpinnerData.setValue("log_out");
        SpinnerData bothpkgLoginandLogoutSendSpinnerData = new SpinnerData();
        bothpkgLoginandLogoutSendSpinnerData.setName("Both Package Login/Logout");
        bothpkgLoginandLogoutSendSpinnerData.setValue("both");
        searchLogicList.add(0,noneSpinnerData);
        searchLogicList.add(1,pkgLoginSpinnerData);
        searchLogicList.add(2,pkgLogoutSendSpinnerData);
        searchLogicList.add(3,bothpkgLoginandLogoutSendSpinnerData);

      /*  if (searchLogicList != null && enablePlaceHolder) {
            searchLogicList.add(0, NTF_Utils.getPlaceHolderObj());
        }*/
        return searchLogicList;
    }

    public static ArrayList<SpinnerData> getPackageTypes(Activity activity) {
        ArrayList<SpinnerData> showPackagesList = new ArrayList<>();
        String[] historyTypes = activity.getResources().getStringArray(R.array.history_type);
        for (String history:
                historyTypes) {
            SpinnerData spinnerData = new SpinnerData();
            spinnerData.setName(history);
            showPackagesList.add(spinnerData);
        }
        return  showPackagesList;
    }

    public static ArrayList<SpinnerData> getFilterList(Activity activity) {
        ArrayList<SpinnerData> showFilterList = new ArrayList<>();
        String[] historyTypes = activity.getResources().getStringArray(R.array.filterByList);
        for (String history:
                historyTypes) {
            SpinnerData spinnerData = new SpinnerData();
            spinnerData.setName(history);
            showFilterList.add(spinnerData);
        }
        return  showFilterList;
    }
}

