package org.telegram.demo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Outline;
import android.os.Bundle;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.telegram.demo.utils.BitmapUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.AvatarDrawable;

public class DemoActivity extends Activity implements ImageReceiver.ImageReceiverDelegate {

    private ImageView imageView;
    private MyGLTextureView textureView;

    private int currentAccount = UserConfig.selectedAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final int cornerRadius = AndroidUtilities.dp(100);
        final int imageSize = AndroidUtilities.dp(200);
        final float zoom = 1.1f;
        final int blurRadius = 23;

        FrameLayout containerLayout = new FrameLayout(this);

        imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        FrameLayout.LayoutParams imageViewLayoutParams = new FrameLayout.LayoutParams(imageSize, imageSize);
        imageViewLayoutParams.leftMargin = AndroidUtilities.dp(10);
        imageViewLayoutParams.topMargin = AndroidUtilities.dp(10);
        imageView.setLayoutParams(imageViewLayoutParams);
        containerLayout.addView(imageView);


        imageView.setClipToOutline(true);
        imageView.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(
                        0, 0, view.getWidth(), view.getHeight(), cornerRadius);
            }
        });


        textureView = new MyGLTextureView(this);
        textureView.updateZoom(zoom);
        textureView.updateCornerRadius(cornerRadius);
        textureView.updateBlurRadius(blurRadius);
        FrameLayout.LayoutParams textureViewLp = new FrameLayout.LayoutParams(imageSize, imageSize);
        textureViewLp.topMargin = AndroidUtilities.dp(350);
        textureViewLp.leftMargin = AndroidUtilities.dp(10);
        containerLayout.addView(textureView, textureViewLp);

        setContentView(containerLayout);

        //loadUserAvatarIntoImageView();
        loadStaticImageIntoImageView();
    }

    private void loadStaticImageIntoImageView() {
        Bitmap bitmap = BitmapUtils.loadBitmapFromAssets(this, "1.jpg");
        textureView.updateBitmap(bitmap);
        imageView.setImageBitmap(bitmap);
    }

    private void loadUserAvatarIntoImageView() {
        final UserConfig userConfig = UserConfig.getInstance(currentAccount);

        userConfig.loadConfig();
        TLRPC.User user = userConfig.getCurrentUser();
        AvatarDrawable avatarDrawable = new AvatarDrawable(user);

        ImageReceiver imageReceiver = new ImageReceiver();
        imageReceiver.setDelegate(this);
        imageReceiver.setForUserOrChat(user, avatarDrawable, null, true, 0, true);
    }

    @Override
    public void didSetImage(ImageReceiver imageReceiver, boolean set, boolean thumb, boolean memCache) {
        Bitmap bitmap = imageReceiver.getBitmap();
        imageView.setImageBitmap(bitmap);
        textureView.updateBitmap(bitmap);
    }

}
