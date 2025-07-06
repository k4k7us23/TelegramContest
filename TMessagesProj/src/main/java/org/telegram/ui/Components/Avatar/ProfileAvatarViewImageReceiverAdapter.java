package org.telegram.ui.Components.Avatar;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.Choreographer;

import org.telegram.messenger.ImageReceiver;
import org.telegram.ui.Components.AnimatedFileDrawable;

import java.util.Objects;

public class ProfileAvatarViewImageReceiverAdapter {

    private static final int VECTOR_ANIMATED_DRAWABLE_RENDER_WIDTH = 500;
    private static final int VECTOR_ANIMATED_DRAWABLE_RENDER_HEIGHT = 500;

    private final ProfileAvatarView profileAvatarView;
    private final ImageReceiver imageReceiver;

    private Integer lastUploadedImageProgress = null;
    private boolean refreshScheduled = false;
    private Canvas stubCanvas = new Canvas();
    private Bitmap bitmapForVectorAvatarDrawable = null;

    private ImageReceiver.ImageReceiverDelegate imageReceiverDelegate = new ImageReceiver.ImageReceiverDelegate() {
        @Override
        public void didSetImage(ImageReceiver imageReceiver, boolean set, boolean thumb, boolean memCache) {
            lastUploadedImageProgress = null;
            refreshBitmapInOpenGl();
        }
    };


    public ProfileAvatarViewImageReceiverAdapter(ProfileAvatarView profileAvatarView, ImageReceiver imageReceiver) {
        this.profileAvatarView = profileAvatarView;
        this.imageReceiver = imageReceiver;
        imageReceiver.setDelegate(imageReceiverDelegate);
        imageReceiver.addInvalidateListener(new ImageReceiver.InvalidateListener() {
            @Override
            public void onInvalidate() {
                refreshBitmapInOpenGl();
            }
        });
        imageReceiver.setImageCoords(0, 0, VECTOR_ANIMATED_DRAWABLE_RENDER_WIDTH, VECTOR_ANIMATED_DRAWABLE_RENDER_HEIGHT);
    }

    private Choreographer.FrameCallback frameCallback = new Choreographer.FrameCallback() {
        @Override
        public void doFrame(long frameTimeNanos) {
            refreshScheduled = false;
            imageReceiver.draw(stubCanvas);

            AnimatedFileDrawable animatedFileDrawable = imageReceiver.getAnimation();
            // TODO check for VectorAvatarThumbDrawable
            if (animatedFileDrawable != null) {
                int progress = animatedFileDrawable.getCurrentProgressMs();
                if (!Objects.equals(lastUploadedImageProgress, progress)) {
                    profileAvatarView.updateBitmap(imageReceiver.getBitmap());
                    lastUploadedImageProgress = progress;
                }
            } else {
                lastUploadedImageProgress = null;
                Bitmap bitmap = imageReceiver.getBitmap();
                if (bitmap != null) {
                    profileAvatarView.updateBitmap(imageReceiver.getBitmap());
                } else {
                    profileAvatarView.updateBitmap(renderReceiverToBitmap());
                }
            }
        }
    };

    private void refreshBitmapInOpenGl() {
        if (!refreshScheduled) {
            refreshScheduled = true;
            Choreographer.getInstance().postFrameCallback(frameCallback);
        }
    }

    private Bitmap renderReceiverToBitmap() {
        Bitmap bitmap = getBitmapForVectorAvatarDrawable();
        Canvas canvas = new Canvas(bitmap);
        bitmap.eraseColor(Color.TRANSPARENT);
        imageReceiver.draw(canvas);
        return bitmap;
    }

    private Bitmap getBitmapForVectorAvatarDrawable() {
        if (bitmapForVectorAvatarDrawable == null) {
            bitmapForVectorAvatarDrawable = Bitmap.createBitmap(VECTOR_ANIMATED_DRAWABLE_RENDER_WIDTH, VECTOR_ANIMATED_DRAWABLE_RENDER_HEIGHT, Bitmap.Config.ARGB_8888);
        }
        return bitmapForVectorAvatarDrawable;
    }
}
