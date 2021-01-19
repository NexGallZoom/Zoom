package com.nexgal.zoom.features.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.Gravity;
import android.view.TextureView;
import android.widget.FrameLayout;

public class CameraStreamView extends TextureView implements CameraStreamCallback {
    private BitmapFactory.Options bitmapOption;

    public CameraStreamView(Context context) {
        super(context);
        this.bitmapOption = new BitmapFactory.Options();
    }

    @Override
    public void drawStream(byte[] buffer) {
        Bitmap image = BitmapFactory.decodeByteArray(buffer, 0, buffer.length, this.bitmapOption);
        if (image == null) {
            return;
        }
        Bitmap drawableImage = image.copy(Bitmap.Config.ARGB_8888, true);

        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        Bitmap rotatedImage = Bitmap.createBitmap(drawableImage, 0, 0, drawableImage.getWidth(),
                drawableImage.getHeight(), matrix, false);

        Canvas canvas = this.lockCanvas();
        canvas.drawBitmap(rotatedImage, 0, 0, null);
        this.unlockCanvasAndPost(canvas);
    }

    public void initialize() {
        this.setLayoutParams(new FrameLayout.LayoutParams(360, 270, Gravity.LEFT));
    }
}
