package org.telegram.ui.Components.Avatar.rendering;

import android.opengl.GLES20;
import android.util.Log;

public class ProfileAvatarGlErrorChecker {

    private static final String TAG = "GLErrors";

    public void checkGlError(String glOperation) {
        int error;
        if ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
        }
    }
}
