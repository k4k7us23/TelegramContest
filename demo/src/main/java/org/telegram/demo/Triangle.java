package org.telegram.demo;

import android.opengl.GLES20;

import org.telegram.demo.utils.GlErrorChecker;
import org.telegram.demo.utils.ShaderLoader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Triangle {
    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float triangleVertex[] = {
            -1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,
            0.0f, 1.0f, 1.0f};
    private final String vertexShaderCode =
            "attribute vec3 aVertexPosition;" + "uniform mat4 uMVPMatrix;varying vec4 vColor;" +
                    "void main() {gl_Position = uMVPMatrix *vec4(aVertexPosition,1.0);" +
                    "vColor=vec4(1.0,0.0,0.0,1.0);}";
    private final String fragmentShaderCode = "precision mediump float;varying vec4 vColor; " +
            "void main() {gl_FragColor = vColor;}";
    private final FloatBuffer vertexBuffer;
    private final int mProgram;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    private final ShaderLoader shaderLoader;
    private final GlErrorChecker glErrorChecker;
    private int mPositionHandle;
    private int mMVPMatrixHandle;
    private int vertexCount;// number of vertices

    public Triangle(ShaderLoader shaderLoader, GlErrorChecker glErrorChecker) throws IOException {
        this.shaderLoader = shaderLoader;
        this.glErrorChecker = glErrorChecker;

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(triangleVertex.length * 4);// (# of coordinate values * 4 bytes per float)
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(triangleVertex);
        vertexBuffer.position(0);
        vertexCount = triangleVertex.length / COORDS_PER_VERTEX;
        // prepare shaders and OpenGL program
        int vertexShader = shaderLoader.loadShader(GLES20.GL_VERTEX_SHADER, R.raw.triangle_vert);
        int fragmentShader = shaderLoader.loadShader(GLES20.GL_FRAGMENT_SHADER, R.raw.triangle_frag);
        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // link the  OpenGL program to create an executable
        GLES20.glUseProgram(mProgram);// Add program to OpenGL environment
        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aVertexPosition");
        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        glErrorChecker.checkGlError("glGetUniformLocation");
    }

    public void draw(float[] mvpMatrix) {
        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        glErrorChecker.checkGlError("glUniformMatrix4fv");
        //set the attribute of the vertex to point to the vertex buffer
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
    }
}
