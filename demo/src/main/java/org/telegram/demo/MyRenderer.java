package org.telegram.demo;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import org.telegram.demo.utils.GlErrorChecker;
import org.telegram.demo.utils.ShaderLoader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class MyRenderer implements TextureViewRenderer {
    private final ShaderLoader shaderLoader;
    private final GlErrorChecker glErrorChecker;

    // Full-screen quad (X,Y,Z)
    private final float[] verticesData = {
            -1.0f, -1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
            -1.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 0.0f
    };
    // Texture coords
    private final float[] texCoordsData = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f
    };
    private FloatBuffer vertexBuffer;
    private FloatBuffer texCoordBuffer;
    private int program;

    private int positionHandle;
    private int texCoordHandle;
    private int textureHandle;
    private Integer textureId = null;

    public MyRenderer(ShaderLoader shaderLoader, GlErrorChecker glErrorChecker) throws IOException {
        this.shaderLoader = shaderLoader;
        this.glErrorChecker = glErrorChecker;
    }

    @Override
    public void onSurfaceCreated() {
        GLES20.glClearColor(0.0f, 1.0f, 0.0f, 1.0f);

        vertexBuffer = ByteBuffer.allocateDirect(verticesData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(verticesData).position(0);

        texCoordBuffer = ByteBuffer.allocateDirect(texCoordsData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        texCoordBuffer.put(texCoordsData).position(0);

        int vertexShader, fragmentShader;
        try {
            vertexShader = shaderLoader.loadShader(GLES20.GL_VERTEX_SHADER, R.raw.avatar_vert);
            fragmentShader = shaderLoader.loadShader(GLES20.GL_FRAGMENT_SHADER, R.raw.avatar_frag);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
        GLES20.glUseProgram(program);

        positionHandle = GLES20.glGetAttribLocation(program, "aPosition");
        texCoordHandle = GLES20.glGetAttribLocation(program, "aTexCoord");
        textureHandle = GLES20.glGetUniformLocation(program, "uTexture");

        glErrorChecker.checkGlError("onSurfaceCreated");
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        if (textureId != null) {
            GLES20.glUseProgram(program);

            GLES20.glEnableVertexAttribArray(positionHandle);
            GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
            glErrorChecker.checkGlError("positionHandle");

            GLES20.glEnableVertexAttribArray(texCoordHandle);
            GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, texCoordBuffer);
            glErrorChecker.checkGlError("textCoordHandle");

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glUniform1i(textureHandle, 0);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
            glErrorChecker.checkGlError("drawArrays");
            GLES20.glDisableVertexAttribArray(positionHandle);
            GLES20.glDisableVertexAttribArray(texCoordHandle);
        }
    }

    @Override
    public void onBitmapUpdate(Bitmap bitmap) {
        if (bitmap != null) {
            if (textureId != null) {
                GLES20.glDeleteTextures(1, new int[]{textureId}, 0);
                textureId = null;
            }
            textureId = createTexture(bitmap);
        }
    }

    private int createTexture(Bitmap bitmap) {
        int[] texIds = new int[1];
        GLES20.glGenTextures(1, texIds, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texIds[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        glErrorChecker.checkGlError("createTexture");
        return texIds[0];
    }
}
