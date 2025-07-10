package org.telegram.ui.Profile;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ProfileActivity;

public class ProfileAvatarCollapseAnimation {

    private static final float PROGRESS_PART_1 = 0.42f;
    private static final float PROGRESS_PART_2 = 0.48f;
    private static final float PROGRESS_PART_3 = 0.78f;
    private static final float AVATAR_SIZE_END_FACTOR_INITIAL = 1f;
    private static final float AVATAR_SIZE_END_FACTOR_PART_1 = 0.8f;
    private static final float AVATAR_SIZE_END_FACTOR_PART_2 = 0.62f;
    private static final float BLUR_RELATIVE_RADIUS_START_VALUE = 0.0001f;
    private static final float BLUR_RELATIVE_RADIUS_END_VALUE = 0.8f;
    private static final float AVATAR_BLACK_OVERLAY_START_VALUE = 0f;
    private static final float AVATAR_BLACK_OVERLAY_END_VALUE = 1f;
    private static final float AVATAR_PROGRESS_FACTOR = 1.388f;

    private final ProfileActivity profileActivity;
    private final ProfileAvatarInitialAnimation profileAvatarInitialAnimation;

    private Interpolator blackAlphaInterpolator = new AccelerateDecelerateInterpolator();

    public ProfileAvatarCollapseAnimation(
            final ProfileActivity profileActivity,
            final ProfileAvatarInitialAnimation profileAvatarInitialAnimation
    ) {
        this.profileActivity = profileActivity;
        this.profileAvatarInitialAnimation = profileAvatarInitialAnimation;
    }

    public float getAvatarScale(float progress) {
        if (progress < 0.0) {
            return AVATAR_SIZE_END_FACTOR_INITIAL;
        } else if (progress < PROGRESS_PART_1) {
            return AndroidUtilities.lerp(AVATAR_SIZE_END_FACTOR_INITIAL, AVATAR_SIZE_END_FACTOR_PART_1, getPart1Progress(progress));
        } else if (progress < PROGRESS_PART_2) {
            return AndroidUtilities.lerp(AVATAR_SIZE_END_FACTOR_PART_1, AVATAR_SIZE_END_FACTOR_PART_2, (progress - PROGRESS_PART_1) / (PROGRESS_PART_2 - PROGRESS_PART_1));
        } else {
            return AVATAR_SIZE_END_FACTOR_PART_2;
        }
    }

    public float getAvatarTranslationY(float progress) {
        float start = profileAvatarInitialAnimation.getAvatarTranslationYEnd();
        float mid = profileAvatarInitialAnimation.getAvatarTranslationYEnd() * 0.4f;
        float end = -AndroidUtilities.dp(ProfileAvatarInitialAnimation.INITIAL_AVATAR_SIZE_DP);
        if (progress < PROGRESS_PART_1) {
            return AndroidUtilities.lerp(start, mid, getPart1Progress(progress));
        } else if (progress < PROGRESS_PART_3) {
            return AndroidUtilities.lerp(mid, end, getPart3Progress(progress));
        } else {
            return end;
        }
    }

    public float getAvatarBlurRelativeRadius(float progress) {
        if (progress < PROGRESS_PART_1) {
            return BLUR_RELATIVE_RADIUS_START_VALUE;
        } else if (progress < PROGRESS_PART_3) {
            return AndroidUtilities.lerp(BLUR_RELATIVE_RADIUS_START_VALUE, BLUR_RELATIVE_RADIUS_END_VALUE, getPart3Progress(progress));
        } else {
            return BLUR_RELATIVE_RADIUS_END_VALUE;
        }
    }

    public float getAvatarBlackOverlayAlpha(float progress) {
        if (progress < PROGRESS_PART_1) {
            return AVATAR_BLACK_OVERLAY_START_VALUE;
        } else if (progress < PROGRESS_PART_3) {
            float part3Progress = getPart3Progress(progress);
            return AndroidUtilities.lerp(AVATAR_BLACK_OVERLAY_START_VALUE, AVATAR_BLACK_OVERLAY_END_VALUE, blackAlphaInterpolator.getInterpolation(part3Progress));
        } else {
            return AVATAR_BLACK_OVERLAY_END_VALUE;
        }
    }

    public float getAvatarTranslationX(float progress) {
        return profileAvatarInitialAnimation.getAvatarTranslationXEnd();
    }

    public float getNameTranslationX(int nameInd, float progress) {
        float start = profileAvatarInitialAnimation.getNameTranslationXEnd(nameInd);
        float end = -AndroidUtilities.dp(ProfileActivity.NAME_LEFT_MARGIN_DP - 65);
        float globalProgress = getGlobalProgress(progress);
        return AndroidUtilities.lerp(start, end, globalProgress);
    }

    public float getOnlineTranslationX(int onlineIndex, float progress) {
        float start = profileAvatarInitialAnimation.getOnlineTranslationXEnd(onlineIndex);
        float end = -AndroidUtilities.dp(ProfileActivity.NAME_LEFT_MARGIN_DP - 65);
        float globalProgress = getGlobalProgress(progress);
        return AndroidUtilities.lerp(start, end, globalProgress);
    }

    public float getNameTranslationY(float progress) {
        float start = profileAvatarInitialAnimation.getNameTranslationYEnd();
        float end = (profileActivity.getActionBar().getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0);
        float globalProgress = getGlobalProgress(progress);
        return AndroidUtilities.lerp(start, end, globalProgress);
    }

    public float getOnlineTranslationY(float progress) {
        return getNameTranslationY(progress) + AndroidUtilities.dp(ProfileAvatarInitialAnimation.ONLINE_VERTICAL_MARGIN_DP);
    }

    private float getPart1Progress(float progress) {
        return Utilities.clamp(progress / PROGRESS_PART_1, 1f, 0f);
    }

    private float getPart3Progress(float progress) {
        return Utilities.clamp((progress - PROGRESS_PART_1) / (PROGRESS_PART_3 - PROGRESS_PART_1), 1f, 0f);
    }

    private float getGlobalProgress(float progress) {
        return Utilities.clamp(progress / PROGRESS_PART_3, 1f, 0f);
    }

}
