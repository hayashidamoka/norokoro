package jp.co.pannacotta.norokoro.utill;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * 写真を保存する
 */
public class BitmapUtil {
    private static final int DEFAULT_IMAGE_WIDTH = 500;
    private static final int DEFAULT_IMAGE_HEIGHT = 500;

    public static Bitmap resize(Bitmap bitmap) {

        if (bitmap == null) {
            return null;
        }

        int oldWidth = bitmap.getWidth();
        int oldHeight = bitmap.getHeight();

        if (oldWidth < DEFAULT_IMAGE_WIDTH && oldHeight < DEFAULT_IMAGE_HEIGHT) {
            // 縦も横も指定サイズより小さい場合は何もしない
            return bitmap;
        }

        float scaleWidth = ((float) DEFAULT_IMAGE_WIDTH) / oldWidth;
        float scaleHeight = ((float) DEFAULT_IMAGE_HEIGHT) / oldHeight;
        float scaleFactor = Math.min(scaleWidth, scaleHeight);

        Matrix scale = new Matrix();
        scale.postScale(scaleFactor, scaleFactor);

        return Bitmap.createBitmap(bitmap, 0, 0, oldWidth, oldHeight, scale, false);
    }
}
