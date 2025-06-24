package org.telegram.demo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.AvatarDrawable;

public class DemoActivity extends Activity implements ImageReceiver.ImageReceiverDelegate {

    private ImageView imageView;

    private int currentAccount = UserConfig.selectedAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageView = new ImageView(this);

        FrameLayout containerLayout = new FrameLayout(this);
        MyGLTextureView textureView = new MyGLTextureView(this);
        FrameLayout.LayoutParams textureViewLp = new FrameLayout.LayoutParams(AndroidUtilities.dp(100), AndroidUtilities.dp(100));
        textureViewLp.topMargin = AndroidUtilities.dp(250);
        textureViewLp.leftMargin = AndroidUtilities.dp(100);
        textureView.setAlpha(0.5f);
        containerLayout.addView(textureView, textureViewLp);


        FrameLayout.LayoutParams imageViewLayoutParams = new FrameLayout.LayoutParams(AndroidUtilities.dp(200), AndroidUtilities.dp(200));
        imageView.setLayoutParams(imageViewLayoutParams);
        containerLayout.addView(imageView);

        setContentView(containerLayout);

        new Handler().postDelayed(() -> {
            textureViewLp.width = textureViewLp.height = AndroidUtilities.dp(200);
            textureView.setLayoutParams(textureViewLp);
        }, 5000);

        loadUserAvatarIntoImageView();
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
    }

}
