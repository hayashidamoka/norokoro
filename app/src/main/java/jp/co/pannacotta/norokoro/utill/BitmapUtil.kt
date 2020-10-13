package jp.co.pannacotta.norokoro.utill

import android.graphics.Bitmap
import android.graphics.Matrix

/**
 * 写真を保存する
 */
object BitmapUtil {
    private const val DEFAULT_IMAGE_WIDTH = 500
    private const val DEFAULT_IMAGE_HEIGHT = 500
    fun resize(bitmap: Bitmap?): Bitmap? {
        if (bitmap == null) {
            return null
        }
        val oldWidth = bitmap.width
        val oldHeight = bitmap.height
        if (oldWidth < DEFAULT_IMAGE_WIDTH && oldHeight < DEFAULT_IMAGE_HEIGHT) {
            // 縦も横も指定サイズより小さい場合は何もしない
            return bitmap
        }
        val scaleWidth = DEFAULT_IMAGE_WIDTH.toFloat() / oldWidth
        val scaleHeight = DEFAULT_IMAGE_HEIGHT.toFloat() / oldHeight
        val scaleFactor = Math.min(scaleWidth, scaleHeight)
        val scale = Matrix()
        scale.postScale(scaleFactor, scaleFactor)
        return Bitmap.createBitmap(bitmap, 0, 0, oldWidth, oldHeight, scale, false)
    }
}