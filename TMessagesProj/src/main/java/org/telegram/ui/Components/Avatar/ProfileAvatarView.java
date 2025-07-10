package org.telegram.ui.Components.Avatar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.ui.Components.Avatar.rendering.ProfileAvatarGLThread;
import org.telegram.ui.Components.Avatar.rendering.ProfileAvatarGlErrorChecker;
import org.telegram.ui.Components.Avatar.rendering.ProfileAvatarRendererImpl;
import org.telegram.ui.Components.Avatar.shaders.AvatarProgramFactory;
import org.telegram.ui.Components.Avatar.shaders.AvatarShaderLoader;

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
    public static final float NO_RELATIVE_BLUR_RADIUS = -1f;

    private final AvatarShaderLoader shaderLoader = new AvatarShaderLoader(ApplicationLoader.applicationLoaderInstance);
    private final ProfileAvatarGlErrorChecker glErrorChecker = new ProfileAvatarGlErrorChecker();
    private ProfileAvatarRendererImpl profileAvatarRendererImpl;
    private ProfileAvatarGLThread profileAvatarGlThread;
    private Queue<Runnable> glThreadActionsQueue = new ArrayDeque<>();

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
    }

    private Bitmap originalBitmap = null;

    private float relativeBlurRadius = NO_RELATIVE_BLUR_RADIUS;

    public void setRelativeBlurRadius(float relativeBlurRadius) {
        this.relativeBlurRadius = relativeBlurRadius;
        final Float newBlurRadius;
        if (relativeBlurRadius >= 0f && originalBitmap != null) {
            float sizeMn = Math.min(originalBitmap.getWidth(), originalBitmap.getHeight());
            newBlurRadius = (sizeMn * relativeBlurRadius);
        } else {
            newBlurRadius = null;
        }
        if (newBlurRadius != null) {
            executeWhenGlThreadIsReady(() -> {
                profileAvatarGlThread.updateBlurRadius(Math.max(DEFAULT_BLUR_RADIUS, newBlurRadius.intValue()));
            });
        }
    }

    public void disableBlur() {
        setRelativeBlurRadius(NO_RELATIVE_BLUR_RADIUS);
        updateBlurRadius(DEFAULT_BLUR_RADIUS);
    }

    public void updateBitmap(Bitmap bitmap) {
        this.originalBitmap = bitmap;

        updateBitmapInternal(originalBitmap);
    }

    private void updateBitmapInternal(Bitmap bitmap) {
        final Float newBlurRadius;
        if (relativeBlurRadius >= 0f && bitmap != null) {
            float sizeMn = Math.min(bitmap.getWidth(), bitmap.getHeight());
            newBlurRadius = (sizeMn * relativeBlurRadius);
        } else {
            newBlurRadius = null;
        }
        executeWhenGlThreadIsReady(() -> {
            if (newBlurRadius != null) {
                profileAvatarGlThread.updateBlurRadius(Math.max(DEFAULT_BLUR_RADIUS, newBlurRadius.intValue()));
            }
            profileAvatarGlThread.updateBitmap(bitmap);
        });
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
        relativeBlurRadius = -1f;
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
        try {
            profileAvatarRendererImpl = new ProfileAvatarRendererImpl(new AvatarProgramFactory(shaderLoader), glErrorChecker);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        profileAvatarGlThread = new ProfileAvatarGLThread(new Surface(surfaceTexture), profileAvatarRendererImpl, width, height);
        while (!glThreadActionsQueue.isEmpty()) {
            glThreadActionsQueue.poll().run();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        if (profileAvatarGlThread != null) {
            profileAvatarGlThread.onSurfaceChanged(width, height);
        }
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
