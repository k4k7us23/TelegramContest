package org.telegram.demo;

import android.opengl.GLES20;

public class AvatarVertexShader {

    public final int aPositionHandle;
    public final int aTexCoordHandle;
    public final int uImageAspectHandle;
    public final int uViewAspectHandle;
    public final int uZoomHandle;

    private final int glProgram;

    public AvatarVertexShader(int glProgram) {
        this.glProgram = glProgram;

        aPositionHandle = GLES20.glGetAttribLocation(glProgram, "aPosition");
        aTexCoordHandle = GLES20.glGetAttribLocation(glProgram, "aTexCoord");
        uImageAspectHandle = GLES20.glGetUniformLocation(glProgram, "uImageAspect");
        uViewAspectHandle = GLES20.glGetUniformLocation(glProgram, "uViewAspect");
        uZoomHandle = GLES20.glGetUniformLocation(glProgram, "uZoom");
    }
}
