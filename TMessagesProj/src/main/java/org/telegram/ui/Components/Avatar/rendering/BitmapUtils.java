package org.telegram.ui.Components.Avatar.rendering;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class BitmapUtils {

    public Bitmap copySaveMemoryIfPossible(Bitmap src, Bitmap dst) {
        if (src == null) {
            return null;
        }
        Bitmap realDst = dst;
        if (dst == null || dst.getHeight() != src.getHeight() || dst.getWidth() != src.getWidth() || dst.getConfig() != src.getConfig()) {
            realDst = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        }

        Canvas canvas = new Canvas(realDst);
        canvas.drawBitmap(src, 0, 0, null);

        return realDst;
    }
}
