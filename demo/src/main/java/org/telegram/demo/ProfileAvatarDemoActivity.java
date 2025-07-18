package org.telegram.demo;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Outline;
import android.os.Bundle;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.Avatar.ProfileAvatarView;
import org.telegram.ui.Components.AvatarDrawable;

public class ProfileAvatarDemoActivity extends Activity implements ImageReceiver.ImageReceiverDelegate {

    private ImageView imageView;
    final int cornerRadius = AndroidUtilities.dp(30);

    private int currentAccount = UserConfig.selectedAccount;
    private ProfileAvatarView profileAvatarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final int imageSize = AndroidUtilities.dp(60);
        final float zoom = 1f;

        FrameLayout containerLayout = new FrameLayout(this);

        imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        FrameLayout.LayoutParams imageViewLayoutParams = new FrameLayout.LayoutParams(imageSize, imageSize);
        imageViewLayoutParams.leftMargin = AndroidUtilities.dp(10);
        imageViewLayoutParams.topMargin = AndroidUtilities.dp(10);
        imageView.setLayoutParams(imageViewLayoutParams);
        //containerLayout.addView(imageView);


        imageView.setClipToOutline(true);
        imageView.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(
                        0, 0, view.getWidth(), view.getHeight(), cornerRadius);
            }
        });


        profileAvatarView = new ProfileAvatarView(this);
        profileAvatarView.updateZoom(zoom);
        profileAvatarView.updateCornerRadius(cornerRadius);
        profileAvatarView.setRelativeBlurRadius(0.2f);
        profileAvatarView.updateBlackOverlayAlpha(0.2f);
        FrameLayout.LayoutParams textureViewLp = new FrameLayout.LayoutParams(imageSize, imageSize);
        textureViewLp.topMargin = AndroidUtilities.dp(10);
        textureViewLp.leftMargin = AndroidUtilities.dp(10);
        containerLayout.addView(profileAvatarView, textureViewLp);

        setContentView(containerLayout);

        loadStaticImageIntoImageView();

        /*ValueAnimator blurAnimator = ValueAnimator.ofInt(1, 100).setDuration(1_000);
        blurAnimator.addUpdateListener(animation -> {
            profileAvatarView.updateBlurRadius((Integer) animation.getAnimatedValue());
        });

        ValueAnimator sizeAnimator = ValueAnimator.ofInt(AndroidUtilities.dp(200), AndroidUtilities.dp(120)).setDuration(1_000);
        sizeAnimator.addUpdateListener(animation -> {
            ViewGroup.LayoutParams layoutParams = profileAvatarView.getLayoutParams();
            layoutParams.height = layoutParams.width = (int) animation.getAnimatedValue();
            profileAvatarView.setLayoutParams(layoutParams);
        });
        sizeAnimator.setDuration(5_000);

        AnimatorSet set = new AnimatorSet();
        set.setStartDelay(1_000);
        set.playSequentially(sizeAnimator, blurAnimator);

        set.start();*/
        /*set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                ViewGroup.LayoutParams layoutParams = textureView.getLayoutParams();
                layoutParams.height = layoutParams.width = AndroidUtilities.dp(200);
                textureView.setLayoutParams(layoutParams);
                textureView.updateBlurRadius(20);
                startSizeAnimation();
            }
        });*/
    }

    private void startSizeAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(1f, 2f).setDuration(1_000);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.addUpdateListener(animation -> {
            profileAvatarView.updateZoom((Float) animation.getAnimatedValue());
            profileAvatarView.updateCornerRadius(Math.round(cornerRadius + AndroidUtilities.dp(40) * animator.getAnimatedFraction()));
        });
        animator.start();
    }

    private void loadStaticImageIntoImageView() {
        Bitmap bitmap = BitmapUtils.loadBitmapFromAssets(this, "2.png");
        profileAvatarView.updateBitmap(bitmap);
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
        profileAvatarView.updateBitmap(bitmap);
    }

}
