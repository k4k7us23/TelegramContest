package org.telegram.demo;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.view.Surface;

public class EGLHelper {
    private final Surface surface;
    private EGLDisplay eglDisplay;
    private EGLContext eglContext;
    private EGLSurface eglSurface;

    public EGLHelper(Surface surface) {
        this.surface = surface;
    }

    public void initEGL() {
        eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        EGL14.eglInitialize(eglDisplay, null, 0, null, 0);

        int[] attribList = {
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_ALPHA_SIZE, 8,
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL14.EGL_NONE
        };
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfigs = new int[1];
        EGL14.eglChooseConfig(eglDisplay, attribList, 0, configs, 0, configs.length, numConfigs, 0);
        EGLConfig eglConfig = configs[0];

        int[] ctxAttribs = {EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE};
        eglContext = EGL14.eglCreateContext(
                eglDisplay, eglConfig, EGL14.EGL_NO_CONTEXT, ctxAttribs, 0
        );

        eglSurface = EGL14.eglCreateWindowSurface(
                eglDisplay, eglConfig, surface, new int[]{EGL14.EGL_NONE}, 0
        );

        EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext);
    }

    public void makeCurrent() {
        EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext);
    }

    public void swapBuffers() {
        EGL14.eglSwapBuffers(eglDisplay, eglSurface);
    }

    public void releaseEGL() {
        if (eglContext != null) {
            EGL14.eglDestroyContext(eglDisplay, eglContext);
        }
        if (eglSurface != null) {
            EGL14.eglDestroySurface(eglDisplay, eglSurface);
        }
        if (eglDisplay != null) {
            EGL14.eglTerminate(eglDisplay);
        }
    }
}
