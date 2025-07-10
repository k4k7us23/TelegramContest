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

    // region: state for send
    private Integer stateBlurRadius = null;
    private Float stateZoom = null;
    private Float stateCornerRadius = null;
    private Float stateVerticalBlurLimit = null;
    private Float stateVerticalBlurBorderSize = null;
    private Float stateVerticalBlurAlpha = null;
    private Float stateBlackOverlayAlpha = null;
    // endregion

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
                stateBlurRadius = Math.max(DEFAULT_BLUR_RADIUS, newBlurRadius.intValue());
                profileAvatarGlThread.updateBlurRadius(stateBlurRadius);
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
                stateBlurRadius = Math.max(DEFAULT_BLUR_RADIUS, newBlurRadius.intValue());
                profileAvatarGlThread.updateBlurRadius(stateBlurRadius);
            }
            profileAvatarGlThread.updateBitmap(bitmap);
        });
    }

    public void updateZoom(float zoom) {
        executeWhenGlThreadIsReady(() -> {
            this.stateZoom = zoom;
            profileAvatarGlThread.updateZoom(this.stateZoom);
        });
    }

    public void updateCornerRadius(float cornerRadius) {
        executeWhenGlThreadIsReady(() -> {
            this.stateCornerRadius = cornerRadius;
            profileAvatarGlThread.updateCornerRadius(this.stateCornerRadius);
        });
    }

    public void updateBlurRadius(int blurRadius) {
        relativeBlurRadius = -1f;
        executeWhenGlThreadIsReady(() -> {
            this.stateBlurRadius = blurRadius;
            profileAvatarGlThread.updateBlurRadius(this.stateBlurRadius);
        });
    }

    public void updateVerticalBlurLimit(float verticalBlurLimit) {
        executeWhenGlThreadIsReady(() -> {
            this.stateVerticalBlurLimit = verticalBlurLimit;
            profileAvatarGlThread.updateVerticalBlurLimit(this.stateVerticalBlurLimit);
        });
    }

    public void updateBlurAlpha(float blurAlpha) {
        executeWhenGlThreadIsReady(() -> {
            this.stateVerticalBlurAlpha = blurAlpha;
            profileAvatarGlThread.updateBlurAlpha(this.stateVerticalBlurAlpha);
        });
    }

    public void updateVerticalBlurLimitBorderSize(float verticalBlurLimitBorderSize) {
        executeWhenGlThreadIsReady(() -> {
            this.stateVerticalBlurBorderSize = verticalBlurLimitBorderSize;
            profileAvatarGlThread.updateVerticalBlurLimitBorderSize(this.stateVerticalBlurBorderSize);
        });
    }

    public void updateBlackOverlayAlpha(float overlayAlpha) {
        executeWhenGlThreadIsReady(() -> {
            this.stateBlackOverlayAlpha = overlayAlpha;
            profileAvatarGlThread.updateBlackOverlayAlpha(this.stateBlackOverlayAlpha);
        });
    }

    private void sendExistingStateToNewThread() {
        if (profileAvatarGlThread == null) {
            return;
        }
        if (originalBitmap != null) {
            updateBitmapInternal(originalBitmap);
        }

        if (stateBlurRadius != null) {
            profileAvatarGlThread.updateBlurRadius(stateBlurRadius);
        }

        if (stateZoom != null) {
            profileAvatarGlThread.updateZoom(stateZoom);
        }

        if (stateCornerRadius != null) {
            profileAvatarGlThread.updateCornerRadius(stateCornerRadius);
        }

        if (stateVerticalBlurLimit != null) {
            profileAvatarGlThread.updateVerticalBlurLimit(stateVerticalBlurLimit);
        }

        if (stateVerticalBlurBorderSize != null) {
            profileAvatarGlThread.updateVerticalBlurLimitBorderSize(stateVerticalBlurBorderSize);
        }

        if (stateBlackOverlayAlpha != null) {
            profileAvatarGlThread.updateBlackOverlayAlpha(stateBlackOverlayAlpha);
        }

        if (stateVerticalBlurAlpha != null) {
            profileAvatarGlThread.updateBlurAlpha(stateVerticalBlurAlpha);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        try {
            profileAvatarRendererImpl = new ProfileAvatarRendererImpl(new AvatarProgramFactory(shaderLoader), glErrorChecker);

            profileAvatarGlThread = new ProfileAvatarGLThread(new Surface(surfaceTexture), profileAvatarRendererImpl, width, height);

            sendExistingStateToNewThread();

            while (!glThreadActionsQueue.isEmpty()) {
                glThreadActionsQueue.poll().run();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
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
