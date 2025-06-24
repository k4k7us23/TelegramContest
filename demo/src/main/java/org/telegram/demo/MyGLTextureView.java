package org.telegram.demo;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;

import org.telegram.demo.utils.GlErrorChecker;
import org.telegram.demo.utils.ShaderLoader;

public class MyGLTextureView extends TextureView implements TextureView.SurfaceTextureListener {
    private final ShaderLoader shaderLoader = new ShaderLoader(ApplicationLoaderImpl.applicationLoaderInstance);
    private final GlErrorChecker glErrorChecker = new GlErrorChecker();
    private final MyRenderer myRenderer = new MyRenderer(shaderLoader, glErrorChecker);
    private GLThread glThread;

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
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        glThread = new GLThread(new Surface(surfaceTexture), myRenderer, width, height);
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
}
