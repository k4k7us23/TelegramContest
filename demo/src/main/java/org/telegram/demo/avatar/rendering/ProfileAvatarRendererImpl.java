package org.telegram.demo.avatar.rendering;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import org.telegram.demo.avatar.ProfileAvatarView;
import org.telegram.demo.avatar.shaders.AvatarBlurProgram;
import org.telegram.demo.avatar.shaders.AvatarBlurVertexShader;
import org.telegram.demo.avatar.shaders.AvatarFragmentShader;
import org.telegram.demo.avatar.shaders.AvatarProgramFactory;
import org.telegram.demo.avatar.shaders.AvatarVertexShader;
import org.telegram.demo.avatar.shaders.ZoomAndCropProgram;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class ProfileAvatarRendererImpl implements ProfileAvatarRenderer {
    private final AvatarProgramFactory avatarProgramFactory;
    private final ProfileAvatarGlErrorChecker glErrorChecker;

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
    private final float[] textCoordsData2 = {
            0.0f, 0.0f, // bottom-left
            1.0f, 0.0f, // bottom-right
            0.0f, 1.0f, // top-left
            1.0f, 1.0f  // top-right
    };
    private FloatBuffer vertexBuffer;
    private FloatBuffer texCoordBuffer;
    private FloatBuffer textCoord2Buffer;

    private Integer originalBitmapTextureId = null;

    private ZoomAndCropProgram zoomAndCropProgram;
    private AvatarBlurProgram avatarBlurProgram;

    private int viewWidth, viewHeight;
    private int bitmapWidth, bitmapHeight;

    // region: external params
    private float zoom = ProfileAvatarView.DEFAULT_ZOOM;
    private float cornerRadius = ProfileAvatarView.DEFAULT_CORNER_RADIUS;
    private int blurRadius = ProfileAvatarView.DEFAULT_BLUR_RADIUS;
    private float verticalBlurLimit = ProfileAvatarView.DEFAULT_VERTICAL_BLUR_LIMIT;
    private float blurAlpha = ProfileAvatarView.DEFAULT_BLUR_ALPHA;
    private float verticalBlurLimitBorderSize = ProfileAvatarView.DEFAULT_VERTICAL_BLUR_LIMIT_BORDER_SIZE;
    private float blackOverlayAlpha = ProfileAvatarView.DEFAULT_BLACK_OVERLAY_ALPHA;
    //endregion

    private CreateFBOResult horizontalBlurFBOResult;
    private CreateFBOResult verticalBlurFBOResult;
    private boolean blurCalculated = false;

    public ProfileAvatarRendererImpl(AvatarProgramFactory avatarProgramFactory, ProfileAvatarGlErrorChecker glErrorChecker) throws IOException {
        this.avatarProgramFactory = avatarProgramFactory;
        this.glErrorChecker = glErrorChecker;
    }

    @Override
    public void onSurfaceCreated() {
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        vertexBuffer = ByteBuffer.allocateDirect(verticesData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(verticesData).position(0);

        texCoordBuffer = ByteBuffer.allocateDirect(texCoordsData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        texCoordBuffer.put(texCoordsData).position(0);

        textCoord2Buffer = ByteBuffer.allocateDirect(textCoordsData2.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        textCoord2Buffer.put(textCoordsData2).position(0);

        avatarProgramFactory.onSurfaceCreated();

        zoomAndCropProgram = avatarProgramFactory.zoomAndCropProgram;
        avatarBlurProgram = avatarProgramFactory.avatarBlurProgram;

        glErrorChecker.checkGlError("onSurfaceCreated");
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        viewHeight = height;
        viewWidth = width;
    }

    @Override
    public void onDrawFrame() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        if (originalBitmapTextureId != null) {
            if (!blurCalculated) {
                blurRenderPass(horizontalBlurFBOResult.FBOId, originalBitmapTextureId, BlurDirection.Horizontal);
                blurRenderPass(verticalBlurFBOResult.FBOId, horizontalBlurFBOResult.textureId, BlurDirection.Vertical);
                blurCalculated = true;
            }

            zoomAndCropRenderPass(verticalBlurFBOResult.textureId, originalBitmapTextureId);
        }
    }

    private void blurRenderPass(int outputFboId, int inputTextureId, BlurDirection blurDirection) {
        GLES20.glViewport(0, 0, bitmapWidth, bitmapHeight);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, outputFboId);
        GLES20.glUseProgram(avatarBlurProgram.glProgram);

        AvatarBlurVertexShader vertexShader = avatarBlurProgram.vertexShader;

        GLES20.glEnableVertexAttribArray(vertexShader.aPositionHandle);
        GLES20.glVertexAttribPointer(vertexShader.aPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        glErrorChecker.checkGlError("aPositionHandle");

        GLES20.glEnableVertexAttribArray(vertexShader.aTexCoordHandle);
        GLES20.glVertexAttribPointer(vertexShader.aTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0, textCoord2Buffer);
        glErrorChecker.checkGlError("aTextCoordHandle");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, inputTextureId);
        GLES20.glUniform1i(avatarBlurProgram.blurFragmentShader.uTextureHandle, 0);

        GLES20.glUniform2f(avatarBlurProgram.blurFragmentShader.uTexSizeHandle, bitmapWidth, bitmapHeight);
        GLES20.glUniform1f(avatarBlurProgram.blurFragmentShader.uSigmaHandle, getSigma(blurRadius));
        GLES20.glUniform1i(avatarBlurProgram.blurFragmentShader.uRadiusHandle, blurRadius);

        if (blurDirection == BlurDirection.Horizontal) {
            GLES20.glUniform2f(avatarBlurProgram.blurFragmentShader.uDirHandle, 1.0f, 0.0f);
        } else if (blurDirection == BlurDirection.Vertical) {
            GLES20.glUniform2f(avatarBlurProgram.blurFragmentShader.uDirHandle, 0.0f, 1.0f);
        }

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        glErrorChecker.checkGlError("drawArrays");

        GLES20.glDisableVertexAttribArray(vertexShader.aPositionHandle);
        GLES20.glDisableVertexAttribArray(vertexShader.aTexCoordHandle);
    }

    private float getSigma(float radius) {
        return radius > 0 ? 0.3f * radius + 0.6f : 0.0f;
    }

    private int createTexture(Bitmap bitmap) {
        int[] texIds = new int[1];
        GLES20.glGenTextures(1, texIds, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texIds[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        glErrorChecker.checkGlError("createTexture");
        return texIds[0];
    }

    private void zoomAndCropRenderPass(final int bluredTextureId, final int originalImageTextureId) {
        GLES20.glViewport(0, 0, viewWidth, viewHeight);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glUseProgram(zoomAndCropProgram.glProgram);

        AvatarVertexShader vertexShader = zoomAndCropProgram.vertexShader;
        AvatarFragmentShader fragmentShader = zoomAndCropProgram.fragmentShader;

        GLES20.glEnableVertexAttribArray(vertexShader.aPositionHandle);
        GLES20.glVertexAttribPointer(vertexShader.aPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        glErrorChecker.checkGlError("aPositionHandle");

        GLES20.glEnableVertexAttribArray(vertexShader.aTexCoordHandle);
        GLES20.glVertexAttribPointer(vertexShader.aTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0, texCoordBuffer);
        glErrorChecker.checkGlError("aTextCoordHandle");

        float imageAspect = (float) bitmapWidth / (float) bitmapHeight;
        float viewAspect = (float) viewWidth / (float) viewHeight;
        GLES20.glUniform1f(vertexShader.uImageAspectHandle, imageAspect);
        GLES20.glUniform1f(vertexShader.uViewAspectHandle, viewAspect);
        GLES20.glUniform1f(vertexShader.uZoomHandle, zoom);
        glErrorChecker.checkGlError("Image Position Uniforms");

        GLES20.glUniform2f(fragmentShader.uViewSizeHandle, viewWidth, viewHeight);
        GLES20.glUniform1f(fragmentShader.uCornerRadiusHandle, cornerRadius);
        GLES20.glUniform1f(fragmentShader.uVerticalBlurLimit, 1f - verticalBlurLimit);
        GLES20.glUniform1f(fragmentShader.uBlurAlpha, blurAlpha);
        GLES20.glUniform1f(fragmentShader.uVerticalBlurLimitBorderSize, -verticalBlurLimitBorderSize);
        GLES20.glUniform1f(fragmentShader.uBlackOverlayAlpha, blackOverlayAlpha);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, bluredTextureId);
        GLES20.glUniform1i(fragmentShader.uTextureHandle, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, originalImageTextureId);
        GLES20.glUniform1i(fragmentShader.uOriginalImageTextureHandle, 1);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        glErrorChecker.checkGlError("drawArrays");
        GLES20.glDisableVertexAttribArray(vertexShader.aPositionHandle);
        GLES20.glDisableVertexAttribArray(vertexShader.aTexCoordHandle);
    }

    @Override
    public void onBitmapUpdate(Bitmap bitmap) {
        if (originalBitmapTextureId != null) {
            GLES20.glDeleteTextures(1, new int[]{originalBitmapTextureId}, 0);
            originalBitmapTextureId = null;
        }

        if (horizontalBlurFBOResult != null) {
            deleteFBOTexture(horizontalBlurFBOResult);
            horizontalBlurFBOResult = null;
        }

        if (verticalBlurFBOResult != null) {
            deleteFBOTexture(verticalBlurFBOResult);
            verticalBlurFBOResult = null;
        }
        blurCalculated = false;

        if (bitmap != null) {
            originalBitmapTextureId = createTexture(bitmap);

            bitmapWidth = bitmap.getWidth();
            bitmapHeight = bitmap.getHeight();

            horizontalBlurFBOResult = createFBOTexture(bitmapWidth, bitmapHeight);
            verticalBlurFBOResult = createFBOTexture(bitmapWidth, bitmapHeight);
        }
    }

    @Override
    public void onZoomUpdate(float zoom) {
        this.zoom = zoom;
    }

    @Override
    public void onCornerRadiusUpdate(float cornerRadius) {
        this.cornerRadius = cornerRadius;
    }

    @Override
    public void onBlurRadiusUpdate(int blurRadius) {
        this.blurRadius = blurRadius;
        blurCalculated = false;
    }

    @Override
    public void onVerticalBlurLimitUpdate(float verticalBlurLimit) {
        this.verticalBlurLimit = verticalBlurLimit;
    }

    @Override
    public void onBlurAlphaUpdate(float blurAlpha) {
        this.blurAlpha = blurAlpha;
    }

    @Override
    public void onVerticalBlurLimitBorderSize(float verticalBlurLimitBorderSize) {
        this.verticalBlurLimitBorderSize = verticalBlurLimitBorderSize;
    }

    @Override
    public void onBlackOverlayAlphaUpdate(float blackOverlayAlpha) {
        this.blackOverlayAlpha = blackOverlayAlpha;
    }

    private enum BlurDirection {
        Vertical,
        Horizontal
    }

    private CreateFBOResult createFBOTexture(int width, int height) {
        int[] textureId = new int[1];
        int[] fboId = new int[1];

        // 1. Create texture
        GLES20.glGenTextures(1, textureId, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
                width, height, 0, GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE, null);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        // 2. Create framebuffer
        GLES20.glGenFramebuffers(1, fboId, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, textureId[0], 0);

        // 3. Check status
        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("Framebuffer is not complete: status = " + status);
        }

        // 4. Unbind framebuffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        return new CreateFBOResult(textureId[0], fboId[0]);
    }

    private void deleteFBOTexture(CreateFBOResult fboResult) {
        GLES20.glDeleteTextures(1, new int[]{fboResult.textureId}, 0);
        GLES20.glDeleteFramebuffers(1, new int[]{fboResult.FBOId}, 0);
    }

    private static class CreateFBOResult {
        public int textureId;
        public int FBOId;

        public CreateFBOResult(int textureId, int FBOId) {
            this.textureId = textureId;
            this.FBOId = FBOId;
        }
    }
}
