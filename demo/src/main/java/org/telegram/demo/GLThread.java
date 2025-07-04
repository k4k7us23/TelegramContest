package org.telegram.demo;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Choreographer;
import android.view.Surface;

public class GLThread extends HandlerThread {
    private final EGLHelper eglHelper;
    private final TextureViewRenderer renderer;

    private final Handler glThreadHandler;

    private int width, height;
    private boolean drawScheduled = false;

    public GLThread(Surface surface, TextureViewRenderer renderer, int w, int h) {
        super("GlThread");

        this.renderer = renderer;
        this.width = w;
        this.height = h;

        eglHelper = new EGLHelper(surface);

        start();

        glThreadHandler = new Handler(getLooper());
        glThreadHandler.post(this::handleInitDrawing);
        glThreadHandler.post(this::scheduleDraw);
    }

    // region: main thread

    public void requestStop() {
        glThreadHandler.post(this::handleRequestStop);
    }

    public void onSurfaceChanged(int w, int h) {
        glThreadHandler.post(() -> handleOnSurfaceChangedImpl(w, h));
    }

    public void updateBitmap(Bitmap bitmap) {
        glThreadHandler.post(() -> handleUpdateBitmap(bitmap));
    }

    public void updateZoom(float zoom) {
        glThreadHandler.post(() -> handleUpdateZoom(zoom));
    }

    public void updateCornerRadius(float cornerRadius) {
        glThreadHandler.post(() -> handleUpdateCornerRadius(cornerRadius));
    }

    public void updateBlurRadius(int blurRadius) {
        glThreadHandler.post(() -> handleUpdateBlurRadius(blurRadius));
    }

    public void updateVerticalBlurLimit(float verticalBlurLimit) {
        glThreadHandler.post(() -> handleUpdateVerticalBlurLimit(verticalBlurLimit));
    }

    public void updateBlurAlpha(float blurAlpha) {
        glThreadHandler.post(() -> handleUpdateBlurAlpha(blurAlpha));
    }

    public void updateVerticalBlurLimitBorderSize(float verticalBlurLimitBorderSize) {
        glThreadHandler.post(() -> handleUpdateVerticalBlurLimitBorderSize(verticalBlurLimitBorderSize));
    }

    public void updateBlackOverlayAlpha(float overlayAlpha) {
        glThreadHandler.post(() -> handleUpdateOverlayAlpha(overlayAlpha));
    }

    // endregion

    // region: background thread

    private void handleOnSurfaceChangedImpl(int w, int h) {
        width = w;
        height = h;
        renderer.onSurfaceChanged(w, h);
        scheduleDraw();
    }

    private void handleInitDrawing() {
        eglHelper.initEGL();
        renderer.onSurfaceCreated();
        renderer.onSurfaceChanged(width, height);
    }

    private void handleUpdateBitmap(Bitmap bitmap) {
        renderer.onBitmapUpdate(bitmap);
        scheduleDraw();
    }

    private void handleUpdateZoom(float zoom) {
        renderer.onZoomUpdate(zoom);
        scheduleDraw();
    }

    private void handleUpdateCornerRadius(float cornerRadius) {
        renderer.onCornerRadiusUpdate(cornerRadius);
        scheduleDraw();
    }

    private void handleUpdateBlurRadius(int blurRadius) {
        renderer.onBlurRadiusUpdate(blurRadius);
        scheduleDraw();
    }

    private void handleUpdateVerticalBlurLimit(float verticalBlurLimit) {
        renderer.onVerticalBlurLimitUpdate(verticalBlurLimit);
        scheduleDraw();
    }

    private void handleUpdateBlurAlpha(float blurAlpha) {
        renderer.onBlurAlphaUpdate(blurAlpha);
        scheduleDraw();
    }

    private void handleUpdateVerticalBlurLimitBorderSize(float verticalBlurLimitBorderSize) {
        renderer.onVerticalBlurLimitBorderSize(verticalBlurLimitBorderSize);
        scheduleDraw();
    }

    private void handleUpdateOverlayAlpha(float overlayAlpha) {
        renderer.onBlackOverlayAlphaUpdate(overlayAlpha);
        scheduleDraw();
    }

    private void scheduleDraw() {
        if (!drawScheduled) {
            drawScheduled = true;
            Choreographer.getInstance().postFrameCallback(frameTimeNanos -> {
                try {
                    handleDrawFrame();
                } finally {
                    drawScheduled = false;
                }
            });
        }
    }

    private void handleDrawFrame() {
        eglHelper.makeCurrent();

        renderer.onDrawFrame();
        eglHelper.swapBuffers();
    }

    private void handleRequestStop() {
        eglHelper.releaseEGL();
        quitSafely();
    }

    // endregion
}
