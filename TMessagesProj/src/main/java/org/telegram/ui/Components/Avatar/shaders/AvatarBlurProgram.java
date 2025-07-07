package org.telegram.ui.Components.Avatar.shaders;

import android.opengl.GLES20;

public class AvatarBlurProgram {

    public final AvatarBlurVertexShader vertexShader;
    public final AvatarBlurFragmentShader blurFragmentShader;
    public final int glProgram;

    AvatarBlurProgram(int avatarBlurVertexShaderPtr, int avatarBlurFragmentShaderPtr) {
        glProgram = GLES20.glCreateProgram();

        GLES20.glAttachShader(glProgram, avatarBlurVertexShaderPtr);
        GLES20.glAttachShader(glProgram, avatarBlurFragmentShaderPtr);
        GLES20.glLinkProgram(glProgram);

        vertexShader = new AvatarBlurVertexShader(glProgram);
        blurFragmentShader = new AvatarBlurFragmentShader(glProgram);
    }

    void releaseResources() {
        GLES20.glDeleteProgram(glProgram);
    }
}
