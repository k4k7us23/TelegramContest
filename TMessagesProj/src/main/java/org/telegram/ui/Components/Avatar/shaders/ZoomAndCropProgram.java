package org.telegram.ui.Components.Avatar.shaders;

import android.opengl.GLES20;

public class ZoomAndCropProgram {

    public final int glProgram;

    public final ZoomAndCropFragmentShader fragmentShader;
    public final ZoomAndCropVertexShader vertexShader;

    ZoomAndCropProgram(int avatarVertexShaderPtr, int avatarFragmentShaderPtr) {
        glProgram = GLES20.glCreateProgram();

        GLES20.glAttachShader(glProgram, avatarVertexShaderPtr);
        GLES20.glAttachShader(glProgram, avatarFragmentShaderPtr);
        GLES20.glLinkProgram(glProgram);

        vertexShader = new ZoomAndCropVertexShader(glProgram);
        fragmentShader = new ZoomAndCropFragmentShader(glProgram);
    }

    void releaseResources() {
        GLES20.glDeleteProgram(glProgram);
    }
}
