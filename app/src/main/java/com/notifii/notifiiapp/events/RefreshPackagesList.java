package com.notifii.notifiiapp.events;

import java.util.ArrayList;
import java.util.List;

import com.notifii.notifiiapp.models.Package;

/**
 * Created by Administrator on 8/5/2016.
 */
public class RefreshPackagesList {
    public List<Package> packageArrayList;
    public RefreshPackagesList(List<Package> mPakgList) {
        this.packageArrayList = mPakgList;
    }
}
