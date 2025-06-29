package org.telegram.demo;

import android.opengl.GLES20;

class ZoomAndCropProgram {

    public final int glProgram;

    public final AvatarFragmentShader fragmentShader;
    public final AvatarVertexShader vertexShader;

    public ZoomAndCropProgram(int avatarVertexShaderPtr, int avatarFragmentShaderPtr) {
        glProgram = GLES20.glCreateProgram();

        GLES20.glAttachShader(glProgram, avatarVertexShaderPtr);
        GLES20.glAttachShader(glProgram, avatarFragmentShaderPtr);
        GLES20.glLinkProgram(glProgram);

        vertexShader = new AvatarVertexShader(glProgram);
        fragmentShader = new AvatarFragmentShader(glProgram);
    }
}
