package org.telegram.demo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.FrameLayout;

import org.telegram.messenger.AndroidUtilities;

public class DemoActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout containerLayout = new FrameLayout(this);
        MyGLTextureView textureView = new MyGLTextureView(this);
        FrameLayout.LayoutParams textureViewLp = new FrameLayout.LayoutParams(AndroidUtilities.dp(100), AndroidUtilities.dp(100));
        textureViewLp.topMargin = AndroidUtilities.dp(100);
        textureViewLp.leftMargin = AndroidUtilities.dp(100);
        textureView.setAlpha(0.5f);
        containerLayout.addView(textureView, textureViewLp);

        setContentView(containerLayout);

        new Handler().postDelayed(() -> {
            textureViewLp.width = textureViewLp.height = AndroidUtilities.dp(200);
            textureView.setLayoutParams(textureViewLp);
        }, 5000);
    }
}
