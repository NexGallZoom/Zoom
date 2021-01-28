package com.nexgal.zoom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.nexgal.zoom.features.camera.CameraManager;
import com.nexgal.zoom.features.camera.CameraPreview;
import com.nexgal.zoom.features.camera.CameraStreamView;
import com.nexgal.zoom.features.chat.ChatTextAdapter;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CAMERA = 100001;
    private static final int PERMISSION_REQUEST_SAVE_FILE = 100002;


    private CameraPreview cameraPreview;
    private Camera camera;

    private List<CameraStreamView> streamViewList = new ArrayList<>();
    private ChatTextAdapter chatTextAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
                return;
            }
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_SAVE_FILE);
                return;
            }

        }

        CameraManager manager = CameraManager.getCameraManager();
        if (!manager.checkCameraUsable(this)) {
            new AlertDialog.Builder(this)
                    .setMessage("카메라가 사용 불가합니다.")
                    .setNeutralButton("종료", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            System.exit(0);
                        }
                    })
                    .show();
        }

        Camera camera = manager.getCamera();

        cameraPreview = new CameraPreview(this, camera);
        FrameLayout preview = findViewById(R.id.camera_preview);
        preview.addView(cameraPreview);

        this.addStreamView(null);

        camera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                MainActivity.this.updateStreamView(data, camera);
            }
        });

        this.camera = camera;

        this.chatTextAdapter = new ChatTextAdapter(this);
        this.chatTextAdapter.addMessage("hello");

        ListView chatList = new ListView(this);
        chatList.setAdapter(this.chatTextAdapter);

        preview.addView(chatList);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_SAVE_FILE:
            case PERMISSION_REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한이 승인된 경우 그리기
                    recreate();
                } else {
                    // 권한 승인 안된 경우 종료
                    finish();
                }
                break;
            default:
                break;
        }
    }

    public void changeCamera(View view) {
        CameraManager manager = CameraManager.getCameraManager();
        Camera camera = manager.getNextCamera();
        this.camera = camera;
        cameraPreview.changeCamera(camera);

        camera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                MainActivity.this.updateStreamView(data, camera);
            }
        });
    }

    public void takePicture(View view) {
        CameraManager cameraManager = CameraManager.getCameraManager();
        cameraManager.takeAndSaveImage(this.camera);
        Toast.makeText(this, "저장 완료", Toast.LENGTH_LONG).show();
    }

    public void addStreamView(View view) {
        final CameraStreamView streamView = new CameraStreamView(this);
        this.streamViewList.add(streamView);
        LinearLayout streamLayout = findViewById(R.id.stream_list);
        final LinearLayout userView = new LinearLayout(this);
        userView.setOrientation(LinearLayout.VERTICAL);
        Button closeButton = new Button(this);
        userView.addView(streamView);
        userView.addView(closeButton);
        streamLayout.addView(userView);
        closeButton.setText("종료");
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.removeStreamView(userView, streamView);
            }
        });
    }

    public void updateStreamView(byte[] data, Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        int width = parameters.getPreviewSize().width;
        int height = parameters.getPreviewSize().height;

        YuvImage yuv = new YuvImage(data, parameters.getPreviewFormat(), width, height, null);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuv.compressToJpeg(new Rect(0, 0, width, height), 50, out);

        byte[] bytes = out.toByteArray();
        CameraManager manager = CameraManager.getCameraManager();
        for (CameraStreamView stream : this.streamViewList) {
            stream.drawStream(bytes, parameters.getJpegThumbnailSize(), manager.isFrontCamera());
        }
    }

    public void removeStreamView(LinearLayout view, CameraStreamView streamView) {
        LinearLayout streamLayout = findViewById(R.id.stream_list);
        streamLayout.removeViewInLayout(view);
        this.streamViewList.remove(streamView);
    }

    public void sendMessage(View view){
        EditText text = (EditText)findViewById(R.id.message_edit);
        chatTextAdapter.addMessage(text.getText().toString());
        text.setText("");
    }
}