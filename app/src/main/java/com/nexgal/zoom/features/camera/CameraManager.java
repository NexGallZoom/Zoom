package com.nexgal.zoom.features.camera;

public class CameraManager {
    private static CameraManager cameraManager;

    private CameraManager(){}

    public static CameraManager getCameraManager(){
        if(cameraManager == null){
            cameraManager = new CameraManager();

        }
        return cameraManager;
    }

}
