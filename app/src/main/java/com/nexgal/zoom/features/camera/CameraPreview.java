package com.nexgal.zoom.features.camera;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import androidx.annotation.NonNull;


public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private Camera camera;
    private SurfaceHolder holder;

    private int getDegree() {
        Activity currentActivity = (Activity) (this.getContext());
        int rotation = currentActivity.getWindowManager().getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_90:
                return 180;
            case Surface.ROTATION_180:
                return 270;
            case Surface.ROTATION_270:
                return 0;
            default:
                return 90;
        }
    }

    public CameraPreview(Context context, Camera camera) {
        super(context);
        this.camera = camera;

        this.holder = getHolder();
        this.holder.addCallback(this);
        this.holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            this.camera.setPreviewDisplay(holder);
            this.camera.setDisplayOrientation(getDegree());
            this.camera.startPreview();
        } catch (IOException e) {
            Log.d("CameraPreview", "미리보기 생성 실패: " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (this.holder.getSurface() == null) {
            return;
        }

        try {
            this.camera.stopPreview();
        } catch (Exception e) {

        }

        try {
            this.camera.setPreviewDisplay(this.holder);
            this.camera.startPreview();
        } catch (Exception e) {
            Log.d("CameraPreview", "미리보기 생성 실패: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            this.camera.stopPreview();
            this.camera.setPreviewCallback(null);
            this.camera.release();
            this.camera = null;
        } catch (Exception e) {

        }
    }

    public void changeCamera(Camera newCamera) {
        try {
            this.camera.stopPreview();
            this.camera.setPreviewCallback(null);
            this.camera.release();
            this.camera = null;
        } catch (Exception e) {

        }
        try {
            newCamera.setPreviewDisplay(this.holder);
            newCamera.setDisplayOrientation(getDegree());
            newCamera.startPreview();
            this.camera = newCamera;
        } catch (Exception e) {
            Log.d("CameraPreview", "미리보기 변경 실패: " + e.getMessage());
        }
    }
}
