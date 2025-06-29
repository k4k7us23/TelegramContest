package org.telegram.demo;

import android.opengl.GLES20;

import org.telegram.demo.utils.ShaderLoader;

import java.io.IOException;

class AvatarProgramFactory {

    private final ShaderLoader shaderLoader;
    public ZoomAndCropProgram zoomAndCropProgram;

    public AvatarProgramFactory(ShaderLoader shaderLoader) {
        this.shaderLoader = shaderLoader;
    }

    public void onSurfaceCreated() {
        int avatarVertexShaderPtr, avatarFragmentShaderPtr;

        try {
            avatarVertexShaderPtr = shaderLoader.loadShader(GLES20.GL_VERTEX_SHADER, R.raw.avatar_vert);
            avatarFragmentShaderPtr = shaderLoader.loadShader(GLES20.GL_FRAGMENT_SHADER, R.raw.avatar_frag);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        zoomAndCropProgram = new ZoomAndCropProgram(avatarVertexShaderPtr, avatarFragmentShaderPtr);
    }
}
