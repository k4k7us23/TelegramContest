package org.telegram.ui.Components.Avatar.shaders;

import android.opengl.GLES20;

public class AvatarBlurVertexShader {
    public final int aPositionHandle;
    public final int aTexCoordHandle;

    private final int glProgram;

    AvatarBlurVertexShader(int glProgram) {
        this.glProgram = glProgram;

        aPositionHandle = GLES20.glGetAttribLocation(glProgram, "aPosition");
        aTexCoordHandle = GLES20.glGetAttribLocation(glProgram, "aTexCoord");
    }
}
