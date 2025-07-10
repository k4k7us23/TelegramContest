package org.telegram.ui.Profile;

import static org.telegram.messenger.AndroidUtilities.lerp;

import android.view.View;
import android.widget.FrameLayout;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ProfileActivity;

public class ProfileAvatarInitialAnimation {

    public static final int INITIAL_AVATAR_SIZE_DP = 100;
    public static final int INITIAL_AVATAR_CORNER_SIZE_DP = INITIAL_AVATAR_SIZE_DP / 2;
    public static final int ONLINE_VERTICAL_MARGIN_DP = 28;
    public static final float AVATAR_SCALE_INITIAL = 0.42f;

    private final ProfileActivity profileActivity;
    private final FrameLayout avatarContainer2;
    private final SimpleTextView[] nameTextView;
    private final SimpleTextView[] onlineTextView;
    private final View avatarImage;

    public ProfileAvatarInitialAnimation(
            ProfileActivity profileActivity,
            FrameLayout avatarContainer2,
            SimpleTextView[] nameTextView,
            SimpleTextView[] onlineTextView,
            View avatarImage
    ) {
        this.profileActivity = profileActivity;
        this.avatarContainer2 = avatarContainer2;
        this.nameTextView = nameTextView;
        this.onlineTextView = onlineTextView;
        this.avatarImage = avatarImage;
    }

    public float getAvatarTranslationX(float progress) {
        final float targetAvatarX = (avatarContainer2.getMeasuredWidth() - AndroidUtilities.dp(INITIAL_AVATAR_SIZE_DP)) / 2f;

        return lerp(AndroidUtilities.dp(35), targetAvatarX, progress);
    }

    public float getAvatarTranslationXEnd() {
        return getAvatarTranslationX(1f);
    }

    public float getAvatarTranslationY(float progress) {
        float start = (profileActivity.getActionBar().getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) - AndroidUtilities.dp(21);
        float end = (profileActivity.getActionBar().getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) +
                (ActionBar.getCurrentActionBarHeight() / 2.0f - AndroidUtilities.dp(12)) +
                profileActivity.getActionBar().getTranslationY();

        return AndroidUtilities.lerp(start, end, progress);
    }

    private float getAvatarTranslationYWithoutScale(float progress) {
        return (profileActivity.getActionBar().getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) +
                (ActionBar.getCurrentActionBarHeight() / 2.0f - AndroidUtilities.dp(12)) * progress +
                profileActivity.getActionBar().getTranslationY();
    }

    public float getAvatarTranslationYEnd() {
        return getAvatarTranslationY(1f);
    }

    public float getNameScale(float progress) {
        return 1.0f + 0.12f * progress;
    }

    public float getNameTranslationX(final int nameIndex, float progress) {
        final SimpleTextView curNameTextView = nameTextView[nameIndex];
        final float targetNameX = (avatarContainer2.getWidth() - curNameTextView.getTextWidth2() * getNameScale(progress)) / 2f - curNameTextView.getLeft();
        return targetNameX * progress;
    }

    public float getOnlineTranslationX(final int onlineIndex, float progress) {
        final SimpleTextView curOnlineTextView = onlineTextView[onlineIndex];
        final float targetOnlineX = (avatarContainer2.getWidth() - curOnlineTextView.getTextWidth2()) / 2f - curOnlineTextView.getLeft() - curOnlineTextView.getPaddingLeft();
        return targetOnlineX * progress + profileActivity.getCustomPhotoOffset();
    }

    public float getOnlineTranslationY(float progress) {
        float nameY = getNameTranslationY(progress);
        return nameY + AndroidUtilities.dp(ONLINE_VERTICAL_MARGIN_DP);
    }

    public float getNameTranslationY(float progress) {
        float avatarY = getAvatarTranslationYWithoutScale(progress);
        return (float) Math.floor(avatarY) + (avatarImage.getHeight() * avatarImage.getScaleY() + AndroidUtilities.dp(12)) * progress;
    }

    public float getNameTranslationXEnd(final int nameIndex) {
        return getNameTranslationX(nameIndex, 1f);
    }

    public float getNameTranslationYEnd() {
        return getNameTranslationY(1f);
    }

    public float getOnlineTranslationXEnd(final int nameIndex) {
        return getOnlineTranslationX(nameIndex, 1f);
    }

    public float getOnlineTranslationYEnd() {
        return getOnlineTranslationY(1f);
    }

    public float getAvatarScale(float progress) {
        float start = AVATAR_SCALE_INITIAL;
        float end = 1f;
        return AndroidUtilities.lerp(start, end, progress);
    }
}
