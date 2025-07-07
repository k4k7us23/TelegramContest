package org.telegram.ui.Components.Avatar.rendering;

import android.graphics.Bitmap;

public interface ProfileAvatarRenderer {

    void onSurfaceCreated();

    void onSurfaceChanged(int width, int height);

    void onDrawFrame();

    void onBitmapUpdate(Bitmap bitmap);

    void onZoomUpdate(float zoom);

    void onCornerRadiusUpdate(float cornerRadius);

    void onBlurRadiusUpdate(int blurRadius);

    void onVerticalBlurLimitUpdate(float verticalBlurLimit);

    void onBlurAlphaUpdate(float blurAlpha);

    void onVerticalBlurLimitBorderSize(float verticalBlurLimitBorderSize);

    void onBlackOverlayAlphaUpdate(float blackOverlayAlpha);

    void releaseResources();
}
