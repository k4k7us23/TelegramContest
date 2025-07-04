package org.telegram.demo.avatar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;

import org.telegram.demo.ApplicationLoaderImpl;
import org.telegram.demo.avatar.rendering.ProfileAvatarGLThread;
import org.telegram.demo.avatar.rendering.ProfileAvatarRendererImpl;
import org.telegram.demo.avatar.shaders.AvatarProgramFactory;
import org.telegram.demo.avatar.rendering.ProfileAvatarGlErrorChecker;
import org.telegram.demo.avatar.shaders.ShaderLoader;
import org.telegram.messenger.AndroidUtilities;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

public class ProfileAvatarView extends TextureView implements TextureView.SurfaceTextureListener {

    // Make sure this value is synced with avatar_frag.glsl
    public static final float NO_VERTICAL_BLUR_LIMIT = -1f;

    public static final float DEFAULT_ZOOM = 1f;
    public static final float DEFAULT_CORNER_RADIUS = 0f;
    public static final int DEFAULT_BLUR_RADIUS = 1;
    public static final float DEFAULT_VERTICAL_BLUR_LIMIT = NO_VERTICAL_BLUR_LIMIT;
    public static final float DEFAULT_BLUR_ALPHA = 1f;
    public static final float DEFAULT_VERTICAL_BLUR_LIMIT_BORDER_SIZE = 0f;
    public static final float DEFAULT_BLACK_OVERLAY_ALPHA = 0f;

    private final ShaderLoader shaderLoader = new ShaderLoader(ApplicationLoaderImpl.applicationLoaderInstance);
    private final ProfileAvatarGlErrorChecker glErrorChecker = new ProfileAvatarGlErrorChecker();
    private ProfileAvatarRendererImpl profileAvatarRendererImpl;
    private ProfileAvatarGLThread profileAvatarGlThread;
    private Queue<Runnable> glThreadActionsQueue = new ArrayDeque<>();

    private final int scaleBitmapHeight = AndroidUtilities.dp(120);

    public ProfileAvatarView(Context context) {
        super(context);
        init();
    }

    public ProfileAvatarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProfileAvatarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setSurfaceTextureListener(this);
        setOpaque(false);
        try {
            profileAvatarRendererImpl = new ProfileAvatarRendererImpl(new AvatarProgramFactory(shaderLoader), glErrorChecker);
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
                profileAvatarGlThread.updateBitmap(bitmap);
            });
        }
    }

    public void updateZoom(float zoom) {
        executeWhenGlThreadIsReady(() -> {
            profileAvatarGlThread.updateZoom(zoom);
        });
    }

    public void updateCornerRadius(float cornerRadius) {
        executeWhenGlThreadIsReady(() -> {
            profileAvatarGlThread.updateCornerRadius(cornerRadius);
        });
    }

    public void updateBlurRadius(int blurRadius) {
        executeWhenGlThreadIsReady(() -> {
            profileAvatarGlThread.updateBlurRadius(blurRadius);
        });
    }

    public void updateVerticalBlurLimit(float verticalBlurLimit) {
        executeWhenGlThreadIsReady(() -> {
            profileAvatarGlThread.updateVerticalBlurLimit(verticalBlurLimit);
        });
    }

    public void updateBlurAlpha(float blurAlpha) {
        executeWhenGlThreadIsReady(() -> {
            profileAvatarGlThread.updateBlurAlpha(blurAlpha);
        });
    }

    public void updateVerticalBlurLimitBorderSize(float verticalBlurLimitBorderSize) {
        executeWhenGlThreadIsReady(() -> {
            profileAvatarGlThread.updateVerticalBlurLimitBorderSize(verticalBlurLimitBorderSize);
        });
    }

    public void updateBlackOverlayAlpha(float overlayAlpha) {
        executeWhenGlThreadIsReady(() -> {
            profileAvatarGlThread.updateBlackOverlayAlpha(overlayAlpha);
        });
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        viewSizeMn = Math.min(width, height);
        profileAvatarGlThread = new ProfileAvatarGLThread(new Surface(surfaceTexture), profileAvatarRendererImpl, width, height);
        while (!glThreadActionsQueue.isEmpty()) {
            glThreadActionsQueue.poll().run();
        }
        updateBitmapInternal(getBitmapForRendering());
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        viewSizeMn = Math.min(width, height);
        if (profileAvatarGlThread != null) {
            profileAvatarGlThread.onSurfaceChanged(width, height);
        }
        updateBitmapInternal(getBitmapForRendering());
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (profileAvatarGlThread != null) {
            profileAvatarGlThread.requestStop();
            profileAvatarGlThread = null;
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    private void executeWhenGlThreadIsReady(Runnable action) {
        if (profileAvatarGlThread != null) {
            action.run();
        } else {
            glThreadActionsQueue.add(action);
        }
    }
}
