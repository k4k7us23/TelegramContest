package org.telegram.demo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
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

        FrameLayout containerLayout = new FrameLayout(this);

        imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        FrameLayout.LayoutParams imageViewLayoutParams = new FrameLayout.LayoutParams(AndroidUtilities.dp(200), AndroidUtilities.dp(200));
        imageView.setLayoutParams(imageViewLayoutParams);
        containerLayout.addView(imageView);


        textureView = new MyGLTextureView(this);
        textureView.updateZoom(1.4f);
        FrameLayout.LayoutParams textureViewLp = new FrameLayout.LayoutParams(AndroidUtilities.dp(200), AndroidUtilities.dp(200));
        textureViewLp.topMargin = AndroidUtilities.dp(250);
        textureViewLp.leftMargin = AndroidUtilities.dp(100);
        containerLayout.addView(textureView, textureViewLp);

        setContentView(containerLayout);

        //loadUserAvatarIntoImageView();
        loadStaticImageIntoImageView();
    }

    private void loadStaticImageIntoImageView() {
        Bitmap bitmap = BitmapUtils.loadBitmapFromAssets(this, "1.jpg");
        imageView.setImageBitmap(bitmap);
        textureView.updateBitmap(bitmap);
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
