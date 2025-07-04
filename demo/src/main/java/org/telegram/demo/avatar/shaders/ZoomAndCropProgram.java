package org.telegram.demo.avatar.shaders;

import android.opengl.GLES20;

public class ZoomAndCropProgram {

    public final int glProgram;

    public final AvatarFragmentShader fragmentShader;
    public final AvatarVertexShader vertexShader;

    ZoomAndCropProgram(int avatarVertexShaderPtr, int avatarFragmentShaderPtr) {
        glProgram = GLES20.glCreateProgram();

        GLES20.glAttachShader(glProgram, avatarVertexShaderPtr);
        GLES20.glAttachShader(glProgram, avatarFragmentShaderPtr);
        GLES20.glLinkProgram(glProgram);

        vertexShader = new AvatarVertexShader(glProgram);
        fragmentShader = new AvatarFragmentShader(glProgram);
    }
}
