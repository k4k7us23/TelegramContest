package org.telegram.demo;

import android.opengl.GLES20;

class AvatarBlurProgram {

    public final AvatarBlurVertexShader vertexShader;
    public final AvatarBlurFragmentShader blurFragmentShader;
    public final int glProgram;

    public AvatarBlurProgram(int avatarBlurVertexShaderPtr, int avatarBlurFragmentShaderPtr) {
        glProgram = GLES20.glCreateProgram();

        GLES20.glAttachShader(glProgram, avatarBlurVertexShaderPtr);
        GLES20.glAttachShader(glProgram, avatarBlurFragmentShaderPtr);
        GLES20.glLinkProgram(glProgram);

        vertexShader = new AvatarBlurVertexShader(glProgram);
        blurFragmentShader = new AvatarBlurFragmentShader(glProgram);
    }
}
