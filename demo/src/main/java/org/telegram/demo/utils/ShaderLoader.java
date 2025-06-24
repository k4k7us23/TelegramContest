package org.telegram.demo.utils;

import android.content.Context;
import android.opengl.GLES20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ShaderLoader {

    private Context context;

    public ShaderLoader(Context context) {
        this.context = context;
    }

    public int loadShader(int type, int shaderResId) throws IOException {
        String shaderCode = loaderShader(shaderResId);

        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    private String loaderShader(int shaderResId) throws IOException {
        InputStream is = context.getResources().openRawResource(shaderResId);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder result = new StringBuilder();
        while ((line = br.readLine()) != null) {
            result.append(line);
            result.append("\n");
        }
        return result.toString();
    }

}
