package org.telegram.ui.Components.Avatar;

import android.view.View;

import org.telegram.messenger.ImageReceiver;

/**
 * Common interface for ProfileActivity.ProfileAvatarContainer and  ProfileActivity.AvatarImageView
 */
public interface IAvatarView extends ImageReceiverProvider{

    View getRootView();

    void hideView();

    void showView();
}
