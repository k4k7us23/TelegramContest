package org.telegram.demo;

import android.opengl.GLES20;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Surface;

public class GLThread extends HandlerThread {
    private final EGLHelper eglHelper;
    private final TextureViewRenderer renderer;

    private final Handler glThreadHandler;

    private int width, height;

    public GLThread(Surface surface, TextureViewRenderer renderer, int w, int h) {
        super("GlThread");

        this.renderer = renderer;
        this.width = w;
        this.height = h;

        eglHelper = new EGLHelper(surface);

        start();

        glThreadHandler = new Handler(getLooper());
        glThreadHandler.post(this::handleInitDrawing);
        glThreadHandler.post(this::handleDrawFrame);
    }

    public void requestStop() {
        glThreadHandler.post(this::handleRequestStop);
    }

    public void onSurfaceChanged(int w, int h) {
        glThreadHandler.post(() -> handleOnSurfaceChangedImpl(w, h));
    }

    private void handleOnSurfaceChangedImpl(int w, int h) {
        width = w;
        height = h;
        renderer.onSurfaceChanged(w, h);
    }

    private void handleInitDrawing() {
        eglHelper.initEGL();
        renderer.onSurfaceCreated();
        renderer.onSurfaceChanged(width, height);
    }

    private void handleDrawFrame() {
        eglHelper.makeCurrent();

        // TODO remove
        GLES20.glViewport(0, 0, width, height);
        GLES20.glClearColor(0.3f, 0.8f, 0.6f, 1f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        // TODO remove

        renderer.onDrawFrame();
        eglHelper.swapBuffers();

        glThreadHandler.post(this::handleDrawFrame);
    }

    private void handleRequestStop() {
        eglHelper.releaseEGL();
        quitSafely();
    }
}
