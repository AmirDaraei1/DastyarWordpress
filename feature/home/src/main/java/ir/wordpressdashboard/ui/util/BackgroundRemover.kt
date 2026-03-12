package ir.wordpressdashboard.ui.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.sqrt

object BackgroundRemover {

    /**
     * پس‌زمینه عکس را با روش native حذف می‌کند.
     * الگوریتم: نمونه‌برداری از گوشه‌های تصویر به عنوان رنگ پس‌زمینه،
     * سپس حذف پیکسل‌هایی که رنگ مشابه دارند (با flood fill از لبه‌ها).
     */
    suspend fun removeBackground(context: Context, uri: Uri): Bitmap =
        withContext(Dispatchers.Default) {
            val inputStream = context.contentResolver.openInputStream(uri)
            val original = BitmapFactory.decodeStream(inputStream)
                ?: throw Exception("تصویر قابل خواندن نیست")

            // کوچک کردن برای سرعت بیشتر (حداکثر ۱۲۸۰ پیکسل)
            val scaled = scaleBitmap(original, maxSize = 1280)
            val width = scaled.width
            val height = scaled.height

            val pixels = IntArray(width * height)
            scaled.getPixels(pixels, 0, width, 0, 0, width, height)

            // نمونه‌برداری رنگ پس‌زمینه از ۴ گوشه + لبه‌های تصویر
            val bgColors = sampleEdgeColors(pixels, width, height)

            // ساخت mask: true = پس‌زمینه، false = موضوع
            val isBg = BooleanArray(width * height) { false }

            // flood fill از ۴ گوشه
            val threshold = 55.0
            floodFill(pixels, isBg, width, height, bgColors, threshold)

            // اعمال mask روی پیکسل‌ها
            val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val outPixels = IntArray(width * height)
            for (i in pixels.indices) {
                outPixels[i] = if (isBg[i]) 0x00000000.toInt() else pixels[i]
            }
            output.setPixels(outPixels, 0, width, 0, 0, width, height)
            output
        }

    private fun scaleBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        val w = bitmap.width
        val h = bitmap.height
        if (w <= maxSize && h <= maxSize) return bitmap
        val scale = maxSize.toFloat() / maxOf(w, h)
        return Bitmap.createScaledBitmap(bitmap, (w * scale).toInt(), (h * scale).toInt(), true)
    }

    /** نمونه‌برداری از لبه‌های تصویر برای تعیین رنگ پس‌زمینه */
    private fun sampleEdgeColors(pixels: IntArray, width: Int, height: Int): List<IntArray> {
        val samples = mutableListOf<IntArray>()
        val step = 5
        // لبه بالا و پایین
        for (x in 0 until width step step) {
            samples.add(toRgb(pixels[x]))
            samples.add(toRgb(pixels[(height - 1) * width + x]))
        }
        // لبه چپ و راست
        for (y in 0 until height step step) {
            samples.add(toRgb(pixels[y * width]))
            samples.add(toRgb(pixels[y * width + width - 1]))
        }
        return samples
    }

    /** Flood fill از ۴ گوشه برای یافتن پس‌زمینه */
    private fun floodFill(
        pixels: IntArray, isBg: BooleanArray,
        width: Int, height: Int,
        bgColors: List<IntArray>, threshold: Double
    ) {
        val queue = ArrayDeque<Int>()
        val startPoints = listOf(
            0, width - 1,
            (height - 1) * width, (height - 1) * width + width - 1
        )
        startPoints.forEach { idx ->
            if (!isBg[idx] && isBackgroundColor(pixels[idx], bgColors, threshold)) {
                queue.add(idx)
                isBg[idx] = true
            }
        }

        while (queue.isNotEmpty()) {
            val idx = queue.removeFirst()
            val x = idx % width
            val y = idx / width

            // ۴ همسایه
            val neighbors = listOf(
                if (x > 0) idx - 1 else -1,
                if (x < width - 1) idx + 1 else -1,
                if (y > 0) idx - width else -1,
                if (y < height - 1) idx + width else -1
            )
            for (n in neighbors) {
                if (n >= 0 && !isBg[n] && isBackgroundColor(pixels[n], bgColors, threshold)) {
                    isBg[n] = true
                    queue.add(n)
                }
            }
        }
    }

    /** بررسی می‌کند که آیا رنگ پیکسل به پس‌زمینه نزدیک است */
    private fun isBackgroundColor(
        pixel: Int, bgColors: List<IntArray>, threshold: Double
    ): Boolean {
        val rgb = toRgb(pixel)
        return bgColors.any { bg -> colorDistance(rgb, bg) < threshold }
    }

    private fun toRgb(color: Int): IntArray = intArrayOf(
        (color shr 16) and 0xFF,
        (color shr 8) and 0xFF,
        color and 0xFF
    )

    private fun colorDistance(a: IntArray, b: IntArray): Double {
        val dr = (a[0] - b[0]).toDouble()
        val dg = (a[1] - b[1]).toDouble()
        val db = (a[2] - b[2]).toDouble()
        return sqrt(dr * dr + dg * dg + db * db)
    }

    /**
     * Bitmap را به صورت PNG در cache ذخیره می‌کند (PNG شفافیت را حفظ می‌کند)
     */
    fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri {
        val file = java.io.File(
            context.cacheDir,
            "bg_removed_${System.currentTimeMillis()}.png"
        )
        file.outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return Uri.fromFile(file)
    }
}
