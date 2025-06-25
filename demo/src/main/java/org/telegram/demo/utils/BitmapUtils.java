package org.telegram.demo.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

public class BitmapUtils {

    /**
     * Decode a bitmap image from the assets folder.
     *
     * @param context  Application or Activity context
     * @param fileName Name of the image file in the assets folder (e.g. "your_image.jpg")
     * @return Bitmap if successful, or null if failed
     */
    public static Bitmap loadBitmapFromAssets(Context context, String fileName) {
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open(fileName);
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }
}
