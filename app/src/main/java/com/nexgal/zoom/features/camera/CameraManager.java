package com.nexgal.zoom.features.camera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class CameraManager {
    private static CameraManager cameraManager;
    private static int maxCamera;
    private static int currentCamera = 0;

    private CameraManager() {
    }

    public static CameraManager getCameraManager() {
        if (cameraManager == null) {
            cameraManager = new CameraManager();
        }
        maxCamera = Camera.getNumberOfCameras();
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

        try {
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

    public Camera getNextCamera() {
        Camera camera = null;
        try {
            currentCamera = (currentCamera + 1) % maxCamera;

            camera = Camera.open(currentCamera);
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

    private Camera.PictureCallback getTakePictureCallback() {
        return new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                camera.startPreview();
                File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                if (pictureFile == null) {
                    Log.e("CameraManager", "파일 생성 실패");
                    return;
                }

                try {
                    FileOutputStream tos = new FileOutputStream(pictureFile);
                    tos.write(data);
                    tos.close();
                } catch (FileNotFoundException e) {
                    Log.e("CameraManager", "파일 찾기 실패: " + e.getMessage());
                } catch (IOException e) {
                    Log.e("CameraManager", "파일 찾기 실패: " + e.getMessage());
                } catch (Exception e) {
                    Log.e("CameraManager", "파일 찾기 실패: " + e.getMessage());
                }
            }
        };

    }

    private static File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "ZoomPictures"
        );

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdir()) {
                Log.e("CameraManager", "파일 디렉토리 생성 실패");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("YYYYMMDDHHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG" + timeStamp + ".jpg");
        } else {
            return null;
        }
        return mediaFile;
    }

    public void takeAndSaveImage(Camera camera) {
        camera.takePicture(null, null, getTakePictureCallback());
    }
}
