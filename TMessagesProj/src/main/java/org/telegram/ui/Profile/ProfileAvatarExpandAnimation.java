package org.telegram.ui.Profile;

import android.view.View;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.SimpleTextView;

public class ProfileAvatarExpandAnimation {


    private final View avatarContainer2;
    private final SimpleTextView[] nameTextView;
    private final SimpleTextView[] onlineTextView;
    private final ProfileAvatarPullDownAnimation profileAvatarPullDownAnimation;

    public ProfileAvatarExpandAnimation(
            View avatarContainer2,
            SimpleTextView[] nameTextView,
            SimpleTextView[] onlineTextView,
            ProfileAvatarPullDownAnimation profileAvatarPullDownAnimation
    ) {
        this.avatarContainer2 = avatarContainer2;
        this.nameTextView = nameTextView;
        this.onlineTextView = onlineTextView;
        this.profileAvatarPullDownAnimation = profileAvatarPullDownAnimation;
    }

    public float getAvatarCornerSize(float expandProgress) {
        float startCornerSizePx = AndroidUtilities.dp(ProfileAvatarInitialAnimation.INITIAL_AVATAR_CORNER_SIZE_DP);
        return (int) AndroidUtilities.lerp(startCornerSizePx, 0f, expandProgress);
    }

    public float getAvatarTranslationX(float expandProgress) {
        int width = getAvatarWidth(expandProgress);
        return (float) (getFullAvatarWidth() - width) / 2f;
    }

    public float getAvatarTranslationY(float expandProgress) {
        float translationAtTheEndOfPullDown = profileAvatarPullDownAnimation.getAvatarTranslationY(ProfileAvatarPullDownAnimation.END_PROGRESS_VALUE);
        return translationAtTheEndOfPullDown * (1.0f - expandProgress);
    }

    public float getAvatarScale(float expandProgress) {
        return 1f;
    }

    public int getAvatarWidth(float expandProgress) {
        int from = AndroidUtilities.dp(profileAvatarPullDownAnimation.getEndAvatarRealSizeDp());
        int to = getFullAvatarWidth();

        return AndroidUtilities.lerp(from, to, expandProgress);
    }

    public int getAvatarHeight(float newTop, float extraHeight, float expandProgress) {
        float from = AndroidUtilities.dp(profileAvatarPullDownAnimation.getEndAvatarRealSizeDp());
        float to = newTop + extraHeight;

        return (int) AndroidUtilities.lerp(from, to, expandProgress);
    }

    public float getVerticalBlurLimit(float expandProgress) {
        float progress = getVerticalBlurProgress(expandProgress);
        return progress * 0.16627f;
    }

    public float getVerticalBlurBorderSize(float expandProgress) {
        return getVerticalBlurLimit(expandProgress);
    }

    public float getRelativeBlurRadius() {
        return 0.2f;
    }

    public float getNameTranslationX(float expandProgress) {
        final float nameTextViewXStart = profileAvatarPullDownAnimation.getNameTranslationXEnd(1);
        final float nameTextViewXEnd = AndroidUtilities.dpf2(18f) - nameTextView[1].getLeft();
        return nameTextViewXStart + expandProgress * (nameTextViewXEnd - nameTextViewXStart);
    }

    public float getNameTranslationY(float newTop, float extraHeight, float expandProgress) {
        final float nameTextViewYStart = profileAvatarPullDownAnimation.getNameTranslationYEnd();
        final float nameTextViewYEnd = newTop + extraHeight - AndroidUtilities.dpf2(38f) - nameTextView[1].getBottom();
        return nameTextViewYStart + expandProgress * (nameTextViewYEnd - nameTextViewYStart);
    }

    public float getOnlineTranslationX(float expandProgress) {
        final float onlineX = profileAvatarPullDownAnimation.getOnlineTranslationXEnd(1);
        final float onlineTextViewXEnd = AndroidUtilities.dpf2(16f) - onlineTextView[1].getLeft();
        return onlineX + expandProgress * (onlineTextViewXEnd - onlineX);
    }

    public float getOnlineTranslationY(float newTop, float extraHeight, float expandProgress) {
        final float onlineY = profileAvatarPullDownAnimation.getOnlineTranslationYEnd();
        final float onlineTextViewYEnd = newTop + extraHeight - AndroidUtilities.dpf2(18f) - onlineTextView[1].getBottom();
        return  onlineY + expandProgress * (onlineTextViewYEnd - onlineY);
    }

    private float getVerticalBlurProgress(float expandProgress) {
        float clampedProgress = Utilities.clamp(expandProgress, 1f, 0f);
        float speedUp = clampedProgress * (1.5f);
        return Utilities.clamp(speedUp, 1f, 0f);
    }

    private int getFullAvatarWidth() {
        return avatarContainer2.getWidth();
    }
}
