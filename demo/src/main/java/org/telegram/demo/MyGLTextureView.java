package org.telegram.demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;

import org.telegram.demo.utils.GlErrorChecker;
import org.telegram.demo.utils.ShaderLoader;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

public class MyGLTextureView extends TextureView implements TextureView.SurfaceTextureListener {

    static final float DEFAULT_ZOOM = 1f;

    private final ShaderLoader shaderLoader = new ShaderLoader(ApplicationLoaderImpl.applicationLoaderInstance);
    private final GlErrorChecker glErrorChecker = new GlErrorChecker();
    private MyRenderer myRenderer;
    private GLThread glThread;
    private Queue<Runnable> glThreadActionsQueue = new ArrayDeque<>();

    public MyGLTextureView(Context context) {
        super(context);
        init();
    }

    public MyGLTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyGLTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setSurfaceTextureListener(this);
        try {
            myRenderer = new MyRenderer(shaderLoader, glErrorChecker);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateBitmap(Bitmap bitmap) {
        executeWhenGlThreadIsReady(() -> {
            glThread.updateBitmap(bitmap);
        });
    }

    public void updateZoom(float zoom) {
        executeWhenGlThreadIsReady(() -> {
            glThread.updateZoom(zoom);
        });
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        glThread = new GLThread(new Surface(surfaceTexture), myRenderer, width, height);
        while (!glThreadActionsQueue.isEmpty()) {
            glThreadActionsQueue.poll().run();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        if (glThread != null) glThread.onSurfaceChanged(width, height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (glThread != null) {
            glThread.requestStop();
            glThread = null;
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    private void executeWhenGlThreadIsReady(Runnable action) {
        if (glThread != null) {
            action.run();
        } else {
            glThreadActionsQueue.add(action);
        }
    }
}
