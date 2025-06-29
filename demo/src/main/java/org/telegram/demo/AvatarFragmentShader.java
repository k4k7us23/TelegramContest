package org.telegram.demo;

import android.opengl.GLES20;

public class AvatarFragmentShader {

    private final int glProgram;

    public int uTextureHandle;
    public int uViewSizeHandle;
    public int uCornerRadiusHandle;

    public AvatarFragmentShader(int glProgram) {
        this.glProgram = glProgram;

        uTextureHandle = GLES20.glGetUniformLocation(glProgram, "uTexture");
        uViewSizeHandle = GLES20.glGetUniformLocation(glProgram, "uViewSize");
        uCornerRadiusHandle = GLES20.glGetUniformLocation(glProgram, "uCornerRadius");
    }

}
