package org.telegram.demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;

import org.telegram.demo.utils.GlErrorChecker;
import org.telegram.demo.utils.ShaderLoader;
import org.telegram.messenger.AndroidUtilities;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

public class MyGLTextureView extends TextureView implements TextureView.SurfaceTextureListener {

    static final float DEFAULT_ZOOM = 1f;
    static final float DEFAULT_CORNER_RADIUS = 0f;
    static final int DEFAULT_BLUR_RADIUS = 1;

    private final ShaderLoader shaderLoader = new ShaderLoader(ApplicationLoaderImpl.applicationLoaderInstance);
    private final GlErrorChecker glErrorChecker = new GlErrorChecker();
    private MyRenderer myRenderer;
    private GLThread glThread;
    private Queue<Runnable> glThreadActionsQueue = new ArrayDeque<>();

    private final int scaleBitmapHeight = AndroidUtilities.dp(120);

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
        setOpaque(false);
        try {
            myRenderer = new MyRenderer(new AvatarProgramFactory(shaderLoader), glErrorChecker);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Integer viewSizeMn = null;

    private Bitmap sentBitmap = null;
    private Bitmap originalBitmap = null;
    private Bitmap scaledBitmap = null;

    public void updateBitmap(Bitmap bitmap) {
        this.originalBitmap = bitmap;
        this.scaledBitmap = null;
        float ratio = (float) bitmap.getWidth() / (float) bitmap.getHeight();
        if (scaleBitmapHeight < bitmap.getHeight()) {
            scaledBitmap = Bitmap.createScaledBitmap(bitmap, (int) (ratio * scaleBitmapHeight), scaleBitmapHeight, true);
        }

        updateBitmapInternal(getBitmapForRendering());
    }

    private Bitmap getBitmapForRendering() {
        if (viewSizeMn == null) {
            return originalBitmap;
        } else if (scaledBitmap != null && viewSizeMn <= scaleBitmapHeight) {
            return scaledBitmap;
        } else {
            return originalBitmap;
        }
    }

    private void updateBitmapInternal(Bitmap bitmap) {
        if (sentBitmap != bitmap) {
            sentBitmap = bitmap;
            executeWhenGlThreadIsReady(() -> {
                glThread.updateBitmap(bitmap);
            });
        }
    }

    public void updateZoom(float zoom) {
        executeWhenGlThreadIsReady(() -> {
            glThread.updateZoom(zoom);
        });
    }

    public void updateCornerRadius(float cornerRadius) {
        executeWhenGlThreadIsReady(() -> {
            glThread.updateCornerRadius(cornerRadius);
        });
    }

    public void updateBlurRadius(int blurRadius) {
        executeWhenGlThreadIsReady(() -> {
            glThread.updateBlurRadius(blurRadius);
        });
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        viewSizeMn = Math.min(width, height);
        glThread = new GLThread(new Surface(surfaceTexture), myRenderer, width, height);
        while (!glThreadActionsQueue.isEmpty()) {
            glThreadActionsQueue.poll().run();
        }
        updateBitmapInternal(getBitmapForRendering());
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        viewSizeMn = Math.min(width, height);
        if (glThread != null) {
            glThread.onSurfaceChanged(width, height);
        }
        updateBitmapInternal(getBitmapForRendering());
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
