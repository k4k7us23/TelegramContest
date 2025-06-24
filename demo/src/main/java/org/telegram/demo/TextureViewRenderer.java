package org.telegram.demo;

import android.graphics.Bitmap;

public interface TextureViewRenderer {

    void onSurfaceCreated();

    void onSurfaceChanged(int width, int height);

    void onDrawFrame();

    void onBitmapUpdate(Bitmap bitmap);
}
