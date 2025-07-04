package org.telegram.demo.avatar.shaders;

import android.opengl.GLES20;

public class AvatarBlurFragmentShader {

    private final int glProgram;

    public int uTextureHandle;
    public int uTexSizeHandle;
    public int uSigmaHandle;
    public int uRadiusHandle;
    public int uDirHandle;

    AvatarBlurFragmentShader(int glProgram) {
        this.glProgram = glProgram;

        uTextureHandle = GLES20.glGetUniformLocation(glProgram, "uTexture");
        uTexSizeHandle = GLES20.glGetUniformLocation(glProgram, "uTexSize");
        uSigmaHandle = GLES20.glGetUniformLocation(glProgram, "uSigma");
        uRadiusHandle = GLES20.glGetUniformLocation(glProgram, "uRadius");
        uDirHandle = GLES20.glGetUniformLocation(glProgram, "uDir");
    }
}
