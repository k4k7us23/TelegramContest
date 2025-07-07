package org.telegram.ui.Profile;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ProfileActivity;

public class ProfileAvatarPullDownAnimation {

    public static final int AVATAR_MOVE_DOWN_DP = 16;
    public static final float AVATAR_PULL_DOWN_SCALE_START = 1f;
    public static final float AVATAR_PULL_DOWN_SCALE_END = 1.05f;
    public static final float AVATAR_PULL_DOWN_ZOOM_START = 1.0f;
    public static final float AVATAR_PULL_DOWN_ZOOM_END = 1.05f;
    public static final float START_PROGRESS_VALUE = 0f;
    public static final float END_PROGRESS_VALUE = ProfileActivity.AVATAR_EXPAND_PROGRESS_THRESHOLD;

    private final ProfileAvatarInitialAnimation profileAvatarInitialAnimation;

    public ProfileAvatarPullDownAnimation(ProfileAvatarInitialAnimation profileAvatarInitialAnimation) {
        this.profileAvatarInitialAnimation = profileAvatarInitialAnimation;
    }

    public float getEndAvatarRealSizeDp() {
        return ProfileAvatarInitialAnimation.INITIAL_AVATAR_SIZE_DP * AVATAR_PULL_DOWN_SCALE_END;
    }

    /**
     * @param pullDownProgress from 0.0 to ProfileActivity.AVATAR_EXPAND_PROGRESS_THRESHOLD
     */
    public float getAvatarTranslationY(float pullDownProgress) {
        return AndroidUtilities.dp(AVATAR_MOVE_DOWN_DP) * getMoveDownProgress(pullDownProgress) + profileAvatarInitialAnimation.getAvatarTranslationYInitial(1f);
    }

    public float getNameTranslationY(float pullDownProgress) {
        return profileAvatarInitialAnimation.getNameTranslationYEnd() + AndroidUtilities.dp(AVATAR_MOVE_DOWN_DP) * getMoveDownProgress(pullDownProgress);
    }

    public float getOnlineTranslationY(float pullDownProgress) {
        return profileAvatarInitialAnimation.getOnlineTranslationYEnd() + AndroidUtilities.dp(AVATAR_MOVE_DOWN_DP) * getMoveDownProgress(pullDownProgress);
    }

    public float getOnlineTranslationYEnd() {
        return getOnlineTranslationY(END_PROGRESS_VALUE);
    }

    public float getNameTranslationYEnd() {
        return getNameTranslationY(END_PROGRESS_VALUE);
    }

    public float getNameTranslationXEnd(int nameIndex) {
        return profileAvatarInitialAnimation.getNameTranslationXEnd(nameIndex);
    }

    public float getOnlineTranslationXEnd(final int onlineIndex) {
        return profileAvatarInitialAnimation.getOnlineTranslationXEnd(onlineIndex);
    }

    /**
     * @param pullDownProgress from 0.0 to ProfileActivity.AVATAR_EXPAND_PROGRESS_THRESHOLD
     */
    public float getAvatarScale(float pullDownProgress) {
        return AndroidUtilities.lerp(AVATAR_PULL_DOWN_SCALE_START, AVATAR_PULL_DOWN_SCALE_END, getMoveDownProgress(pullDownProgress));
    }

    /**
     * @param pullDownProgress from 0.0 to ProfileActivity.AVATAR_EXPAND_PROGRESS_THRESHOLD
     */
    public float getAvatarZoom(float pullDownProgress) {
        return AndroidUtilities.lerp(AVATAR_PULL_DOWN_ZOOM_START, AVATAR_PULL_DOWN_ZOOM_END, getMoveDownProgress(pullDownProgress));
    }

    /**
     * @return progress in range from 0.0 to 1.0
     */
    private float getMoveDownProgress(float pullDownProgress) {
        return AndroidUtilities.coerceIn(pullDownProgress, 0.0f, ProfileActivity.AVATAR_EXPAND_PROGRESS_THRESHOLD) / ProfileActivity.AVATAR_EXPAND_PROGRESS_THRESHOLD;
    }
}
