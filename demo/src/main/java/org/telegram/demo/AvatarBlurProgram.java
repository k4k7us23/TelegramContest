package org.telegram.demo;

import android.opengl.GLES20;

class AvatarBlurProgram {

    public final AvatarVertexShader vertexShader;
    public final AvatarBlurFragmentShader blurFragmentShader;
    private final int glProgram;

    public AvatarBlurProgram(int avatarVertexShaderPtr, int avatarBlurFragmentShaderPtr) {
        glProgram = GLES20.glCreateProgram();

        GLES20.glAttachShader(glProgram, avatarVertexShaderPtr);
        GLES20.glAttachShader(glProgram, avatarBlurFragmentShaderPtr);
        GLES20.glLinkProgram(glProgram);

        vertexShader = new AvatarVertexShader(glProgram);
        blurFragmentShader = new AvatarBlurFragmentShader(glProgram);
    }
}
