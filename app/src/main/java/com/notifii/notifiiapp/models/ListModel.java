package com.notifii.notifiiapp.models;

import java.util.ArrayList;

public class ListModel {

    ArrayList<SpinnerData> list;
    int position=-1;

    public ArrayList<SpinnerData> getList() {
        return list;
    }

    public void setList(ArrayList<SpinnerData> list) {
        this.list = list;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
