package org.telegram.ui.Profile;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ProfileActivity;

public class ProfileAvatarPullDownAnimation {

    public static final int AVATAR_MOVE_DOWN_DP = 16;
    public static final float AVATAR_PULL_DOWN_START_SCALE = 1f;
    public static final float AVATAR_PULL_DOWN_END_SCALE = 1.05f;
    public static final float AVATAR_PULL_DOWN_START_ZOOM = 1.0f;
    public static final float AVATAR_PULL_DOWN_END_ZOOM = 1.05f;

    /**
     * @param pullDownProgress from 0.0 to ProfileActivity.AVATAR_EXPAND_PROGRESS_THRESHOLD
     */
    public float getAvatarAdditionalTranslationY(float pullDownProgress) {
        return AndroidUtilities.dp(AVATAR_MOVE_DOWN_DP) * getMoveDownProgress(pullDownProgress);
    }

    /**
     * @param pullDownProgress from 0.0 to ProfileActivity.AVATAR_EXPAND_PROGRESS_THRESHOLD
     */
    public float getAvatarScale(float pullDownProgress) {
        return AndroidUtilities.lerp(AVATAR_PULL_DOWN_START_SCALE, AVATAR_PULL_DOWN_END_SCALE, getMoveDownProgress(pullDownProgress));
    }

    /**
     * @param pullDownProgress from 0.0 to ProfileActivity.AVATAR_EXPAND_PROGRESS_THRESHOLD
     */
    public float getAvatarZoom(float pullDownProgress) {
        return AndroidUtilities.lerp(AVATAR_PULL_DOWN_START_ZOOM, AVATAR_PULL_DOWN_END_ZOOM, getMoveDownProgress(pullDownProgress));
    }

    /**
     * @returns range from 0.0 to 1.0
     */
    private float getMoveDownProgress(float pullDownProgress) {
        return AndroidUtilities.coerceIn(pullDownProgress, 0.0f, ProfileActivity.AVATAR_EXPAND_PROGRESS_THRESHOLD) / ProfileActivity.AVATAR_EXPAND_PROGRESS_THRESHOLD;
    }
}
