package org.telegram.demo;

import android.opengl.GLES20;
import android.opengl.Matrix;

import org.telegram.demo.utils.GlErrorChecker;
import org.telegram.demo.utils.ShaderLoader;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;

public class MyRenderer implements TextureViewRenderer {
    private final float[] mMVPMatrix = new float[16];//model view projection matrix
    private final float[] mProjectionMatrix = new float[16];//projection mastrix
    private final float[] mViewMatrix = new float[16];//view matrix
    private final float[] mMVMatrix = new float[16];//model view matrix
    private final float[] mModelMatrix = new float[16];//model  matrix
    private final ShaderLoader shaderLoader;
    private final GlErrorChecker glErrorChecker;
    private Triangle mtriangle;

    public MyRenderer(ShaderLoader shaderLoader, GlErrorChecker glErrorChecker) {
        this.shaderLoader = shaderLoader;
        this.glErrorChecker = glErrorChecker;
    }

    @Override
    public void onSurfaceCreated() {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        try {
            mtriangle = new Triangle(shaderLoader, glErrorChecker);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        // Adjust the view based on view window changes, such as screen rotation
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        float left = -ratio, right = ratio;
        Matrix.frustumM(mProjectionMatrix, 0, left, right, -1.0f, 1.0f, 1.0f, 8.0f);
    }

    @Override
    public void onDrawFrame() {
        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClearDepthf(1.0f);//set up the depth buffer
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);//enable depth test (so, it will not look through the surfaces)
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);//indicate what type of depth test
        Matrix.setIdentityM(mMVPMatrix, 0);//set the model view projection matrix to an identity matrix
        Matrix.setIdentityM(mMVMatrix, 0);//set the model view  matrix to an identity matrix
        Matrix.setIdentityM(mModelMatrix, 0);//set the model matrix to an identity matrix
        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0,
                0.0f, 0f, 1.0f,//camera is at (0,0,1)
                0f, 0f, 0f,//looks at the origin
                0f, 1f, 0.0f);//head is down (set to (0,1,0) to look from the top)
        Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, -5f);//move backward for 5 units
        // Calculate the projection and view transformation
        //calculate the model view matrix
        Matrix.multiplyMM(mMVMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVMatrix, 0);

        if (mtriangle != null) {
            mtriangle.draw(mMVPMatrix);
        }
    }
}
