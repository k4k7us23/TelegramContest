package org.telegram.ui.Components.Avatar.shaders;

import android.opengl.GLES20;

public class ZoomAndCropFragmentShader {

    private final int glProgram;

    public int uTextureHandle;
    public int uOriginalImageTextureHandle;
    public int uViewSizeHandle;
    public int uCornerRadiusHandle;
    public int uVerticalBlurLimit;
    public int uBlurAlpha;
    public int uVerticalBlurLimitBorderSize;
    public int uBlackOverlayAlpha;

    ZoomAndCropFragmentShader(int glProgram) {
        this.glProgram = glProgram;

        uTextureHandle = GLES20.glGetUniformLocation(glProgram, "uTexture");
        uOriginalImageTextureHandle = GLES20.glGetUniformLocation(glProgram, "uOriginalImageTexture");
        uViewSizeHandle = GLES20.glGetUniformLocation(glProgram, "uViewSize");
        uCornerRadiusHandle = GLES20.glGetUniformLocation(glProgram, "uCornerRadius");
        uVerticalBlurLimit = GLES20.glGetUniformLocation(glProgram, "uVerticalBlurLimit");
        uBlurAlpha = GLES20.glGetUniformLocation(glProgram, "uBlurAlpha");
        uVerticalBlurLimitBorderSize = GLES20.glGetUniformLocation(glProgram, "uVerticalBlurLimitBorderSize");
        uBlackOverlayAlpha = GLES20.glGetUniformLocation(glProgram, "uBlackOverlayAlpha");
    }

}
