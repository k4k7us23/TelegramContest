package org.telegram.ui.Components.Avatar.rendering;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Choreographer;
import android.view.Surface;

public class ProfileAvatarGLThread extends HandlerThread {
    private final ProfileAvatarEGLHelper profileAvatarEglHelper;
    private final ProfileAvatarRenderer renderer;
    private final BitmapUtils bitmapUtils = new BitmapUtils();

    private final Handler glThreadHandler;

    private int width, height;
    private boolean drawScheduled = false;

    private final Object bitmapUpdateLock = new Object();
    private Bitmap readBitmap = null;
    private Bitmap writeBitmap = null;
    private boolean bitmapUpdateScheduled = false;

    private boolean stopping = false;

    public ProfileAvatarGLThread(Surface surface, ProfileAvatarRenderer renderer, int w, int h) {
        super("ProfileAvatarGLThread");

        this.renderer = renderer;
        this.width = w;
        this.height = h;

        profileAvatarEglHelper = new ProfileAvatarEGLHelper(surface);

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
        synchronized (bitmapUpdateLock) {
            writeBitmap = bitmapUtils.copySaveMemoryIfPossible(bitmap, writeBitmap);
            bitmapUpdateScheduled = true;
        }
        glThreadHandler.post(() -> handleUpdateBitmap());
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
        if (!stopping) {
            renderer.onSurfaceChanged(w, h);
            scheduleDraw();
        }
        scheduleDraw();
    }

    private void handleInitDrawing() {
        if (!stopping) {
            profileAvatarEglHelper.initEGL();
            renderer.onSurfaceCreated();
            renderer.onSurfaceChanged(width, height);
        }
    }

    private void handleUpdateBitmap() {
        final boolean bitmapUpdateScheduledLocal;
        synchronized (bitmapUpdateLock) {
            bitmapUpdateScheduledLocal = bitmapUpdateScheduled;
            if (bitmapUpdateScheduled) {
                Bitmap tmp = readBitmap;
                readBitmap = writeBitmap;
                writeBitmap = tmp;
                bitmapUpdateScheduled = false;
            }
        }
        if (bitmapUpdateScheduledLocal) {
            if (!stopping) {
                renderer.onBitmapUpdate(readBitmap);
            }
            scheduleDraw();
        }
    }

    private void handleUpdateZoom(float zoom) {
        if (!stopping) {
            renderer.onZoomUpdate(zoom);
        }
        scheduleDraw();
    }

    private void handleUpdateCornerRadius(float cornerRadius) {
        if (!stopping) {
            renderer.onCornerRadiusUpdate(cornerRadius);
        }
        scheduleDraw();
    }

    private void handleUpdateBlurRadius(int blurRadius) {
        if (!stopping) {
            renderer.onBlurRadiusUpdate(blurRadius);
        }
        scheduleDraw();
    }

    private void handleUpdateVerticalBlurLimit(float verticalBlurLimit) {
        if (!stopping) {
            renderer.onVerticalBlurLimitUpdate(verticalBlurLimit);
        }
        scheduleDraw();
    }

    private void handleUpdateBlurAlpha(float blurAlpha) {
        if (!stopping) {
            renderer.onBlurAlphaUpdate(blurAlpha);
        }
        scheduleDraw();
    }

    private void handleUpdateVerticalBlurLimitBorderSize(float verticalBlurLimitBorderSize) {
        if (!stopping) {
            renderer.onVerticalBlurLimitBorderSize(verticalBlurLimitBorderSize);
        }
        scheduleDraw();
    }

    private void handleUpdateOverlayAlpha(float overlayAlpha) {
        if (!stopping) {
            renderer.onBlackOverlayAlphaUpdate(overlayAlpha);
        }
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
        if (!stopping) {
            profileAvatarEglHelper.makeCurrent();
            renderer.onDrawFrame();
            profileAvatarEglHelper.swapBuffers();
        }
    }

    private void handleRequestStop() {
        stopping = true;
        renderer.releaseResources();
        profileAvatarEglHelper.releaseEGL();
        quitSafely();
    }

    // endregion
}
