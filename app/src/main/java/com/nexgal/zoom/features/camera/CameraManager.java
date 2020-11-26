package com.nexgal.zoom.features.camera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;

public class CameraManager {
    private static CameraManager cameraManager;
    public boolean checkCameraUsable;

    private CameraManager(){}

    public static CameraManager getCameraManager(){
        if(cameraManager == null){
            cameraManager = new CameraManager();
        }
        return cameraManager;
    }
    public boolean checkCameraUsable(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else
            return false;
    }
    public Camera getCamera() {
        Camera camera = null;

        try { // try-catch 함수: try 부분이 error가 나면 catch 실행(카메라가 안 켜지는 경우)
            camera = Camera.open();
            Camera.Parameters cameraParameters = camera.getParameters();
            if (cameraParameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                camera.setParameters(cameraParameters);
            }
        } catch (Exception ex) {
            Log.e("CameraManager", ex.toString());
            System.exit(1);
        }
        return camera;
    }
}
