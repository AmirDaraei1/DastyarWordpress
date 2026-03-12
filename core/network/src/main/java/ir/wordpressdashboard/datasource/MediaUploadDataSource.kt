package ir.wordpressdashboard.datasource

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import ir.wordpressdashboard.api.MediaApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class MediaUploadDataSource @Inject constructor(
    private val api: MediaApi
) {
    companion object {
        private const val MAX_IMAGE_SIZE = 1024  // حداکثر ۱۰۲۴ پیکسل
        private const val COMPRESS_QUALITY = 80  // کیفیت ۸۰٪
        private const val MAX_FILE_SIZE_BYTES = 500 * 1024  // ۵۰۰KB حداکثر
    }

    /**
     * ① آپلود تصویر به: /wp-json/api/upload_media
     *    سرور body خالی برمی‌گرداند اما ID را در header x-wp-upload-attachment-id می‌دهد
     * ② با آن ID، source_url را از wp/v2/media/{id} می‌گیریم
     */
    suspend fun uploadMedia(context: Context, uri: Uri): String {
        val fileName = "product_image_${System.currentTimeMillis()}.webp"
        val tempFile = File(context.cacheDir, fileName)

        // compress و resize عکس به فرمت WebP قبل از آپلود
        val compressed = compressImage(context, uri, tempFile)
        Log.d("MediaUpload", "Original size: ${getFileSize(context, uri)} bytes")
        Log.d("MediaUpload", "Compressed (WebP) size: ${compressed.length()} bytes")

        val requestBody = compressed.asRequestBody("image/webp".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("file", fileName, requestBody)

        // ① آپلود
        val response = api.uploadMediaCustom(file = filePart)
        Log.d("MediaUpload", "Upload response code: ${response.code()}")
        Log.d("MediaUpload", "Upload headers: ${response.headers()}")

        if (!response.isSuccessful) {
            throw Exception("آپلود تصویر ناموفق: ${response.code()}")
        }

        // ② خواندن attachment ID از header
        val attachmentId = response.headers()["x-wp-upload-attachment-id"]
            ?.trim()
            ?.toIntOrNull()

        Log.d("MediaUpload", "Attachment ID from header: $attachmentId")

        if (attachmentId != null && attachmentId > 0) {
            // ③ گرفتن source_url با ID
            val mediaDto = api.getMediaById(attachmentId)
            Log.d("MediaUpload", "Got media URL: ${mediaDto.sourceUrl}")
            if (mediaDto.sourceUrl.isNotBlank()) return mediaDto.sourceUrl
        }

        throw Exception("آپلود موفق بود اما URL تصویر دریافت نشد (id=$attachmentId)")
    }

    /**
     * عکس را resize و compress می‌کند تا حجم آن کاهش پیدا کند
     */
    private fun compressImage(context: Context, uri: Uri, outputFile: File): File {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw Exception("فایل قابل خواندن نیست")

        val original = BitmapFactory.decodeStream(inputStream)
            ?: throw Exception("تصویر قابل decode نیست")

        // resize اگر بزرگتر از MAX_IMAGE_SIZE بود
        val scaled = if (original.width > MAX_IMAGE_SIZE || original.height > MAX_IMAGE_SIZE) {
            val scale = MAX_IMAGE_SIZE.toFloat() / maxOf(original.width, original.height)
            val newW = (original.width * scale).toInt()
            val newH = (original.height * scale).toInt()
            Log.d("MediaUpload", "Resizing from ${original.width}x${original.height} to ${newW}x${newH}")
            Bitmap.createScaledBitmap(original, newW, newH, true)
        } else {
            original
        }

        // compress به فرمت WebP با کیفیت متغیر
        val webpFormat = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            Bitmap.CompressFormat.WEBP_LOSSY
        } else {
            @Suppress("DEPRECATION")
            Bitmap.CompressFormat.WEBP
        }

        var quality = COMPRESS_QUALITY
        FileOutputStream(outputFile).use { out ->
            scaled.compress(webpFormat, quality, out)
        }

        // اگر هنوز بزرگ بود، کیفیت را کم کن
        while (outputFile.length() > MAX_FILE_SIZE_BYTES && quality > 40) {
            quality -= 10
            FileOutputStream(outputFile).use { out ->
                scaled.compress(webpFormat, quality, out)
            }
            Log.d("MediaUpload", "Re-compressed WebP at quality=$quality, size=${outputFile.length()}")
        }

        if (scaled != original) scaled.recycle()
        original.recycle()

        return outputFile
    }

    private fun getFileSize(context: Context, uri: Uri): Long {
        return try {
            context.contentResolver.openFileDescriptor(uri, "r")?.use { it.statSize } ?: 0L
        } catch (e: Exception) { 0L }
    }
}
