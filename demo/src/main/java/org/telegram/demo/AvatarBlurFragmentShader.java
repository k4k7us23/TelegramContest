package org.telegram.demo;

import android.opengl.GLES20;

class AvatarBlurFragmentShader {

    private final int glProgram;

    public int uTextureHandle;
    public int uTexSizeHandle;
    public int uSigmaHandle;
    public int uDirHandle;

    public AvatarBlurFragmentShader(int glProgram) {
        this.glProgram = glProgram;

        uTextureHandle = GLES20.glGetUniformLocation(glProgram, "uTexture");
        uTexSizeHandle = GLES20.glGetUniformLocation(glProgram, "uTexSize");
        uSigmaHandle = GLES20.glGetUniformLocation(glProgram, "uSigma");
        uDirHandle = GLES20.glGetUniformLocation(glProgram, "uDir");
    }
}
