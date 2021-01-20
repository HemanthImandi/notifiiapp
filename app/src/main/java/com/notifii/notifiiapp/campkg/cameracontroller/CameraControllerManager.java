package com.notifii.notifiiapp.campkg.cameracontroller;


public abstract class CameraControllerManager {
    public abstract int getNumberOfCameras();
    public abstract boolean isFrontFacing(int cameraId);
}
