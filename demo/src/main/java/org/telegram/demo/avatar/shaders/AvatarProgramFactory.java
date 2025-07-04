package org.telegram.demo.avatar.shaders;

import android.opengl.GLES20;

import org.telegram.demo.R;

import java.io.IOException;

public class AvatarProgramFactory {

    private final ShaderLoader shaderLoader;
    public ZoomAndCropProgram zoomAndCropProgram;
    public AvatarBlurProgram avatarBlurProgram;

    public AvatarProgramFactory(ShaderLoader shaderLoader) {
        this.shaderLoader = shaderLoader;
    }

    public void onSurfaceCreated() {
        final int avatarVertexShaderPtr;
        final int avatarFragmentShaderPtr;
        final int avatarBlurFragmentShaderPtr;
        final int avatarBlurVertexShaderPtr;

        try {
            avatarVertexShaderPtr = shaderLoader.loadShader(GLES20.GL_VERTEX_SHADER, R.raw.avatar_vert);
            avatarFragmentShaderPtr = shaderLoader.loadShader(GLES20.GL_FRAGMENT_SHADER, R.raw.avatar_frag);

            avatarBlurVertexShaderPtr = shaderLoader.loadShader(GLES20.GL_VERTEX_SHADER, R.raw.avatar_blur_vert);
            avatarBlurFragmentShaderPtr = shaderLoader.loadShader(GLES20.GL_FRAGMENT_SHADER, R.raw.avatar_blur_frag);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        zoomAndCropProgram = new ZoomAndCropProgram(avatarVertexShaderPtr, avatarFragmentShaderPtr);
        avatarBlurProgram = new AvatarBlurProgram(avatarBlurVertexShaderPtr, avatarBlurFragmentShaderPtr);
    }
}
