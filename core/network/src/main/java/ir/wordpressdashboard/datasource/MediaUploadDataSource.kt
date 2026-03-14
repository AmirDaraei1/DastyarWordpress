package ir.wordpressdashboard.datasource

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import ir.wordpressdashboard.api.MediaApi
import ir.wordpressdashboard.provider.CredentialsManager
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class MediaUploadDataSource @Inject constructor(
    private val api: MediaApi,
    private val credentialsManager: CredentialsManager
) {
    companion object {
        private const val MAX_IMAGE_SIZE = 1024
        private const val COMPRESS_QUALITY = 80
        private const val MAX_FILE_SIZE_BYTES = 500 * 1024
    }

    // جلوگیری از آپلود همزمان یک URI
    private val inFlightMutex = Mutex()
    private val inFlightUris = mutableSetOf<String>()

    /** برمی‌گردونه Pair<mediaId, sourceUrl> */
    suspend fun uploadMedia(context: Context, uri: Uri): Pair<Int, String> {
        val uriKey = uri.toString()

        inFlightMutex.withLock {
            if (uriKey in inFlightUris) {
                Log.w("MediaUpload", "Duplicate upload attempt blocked for: $uriKey")
                throw Exception("این تصویر در حال آپلود است — لطفاً صبر کنید")
            }
            inFlightUris.add(uriKey)
        }

        try {
            // UUID تضمین می‌کنه هیچ‌وقت نام تکراری با فایل قبلی روی سرور نشه
            val uniqueId = java.util.UUID.randomUUID().toString().replace("-", "").take(16)
            val fileName  = "img_${uniqueId}.webp"
            val tempFile  = File(context.cacheDir, fileName)
            if (tempFile.exists()) tempFile.delete()

            val compressed = compressImage(context, uri, tempFile)
            Log.d("MediaUpload", "Original size: ${getFileSize(context, uri)} bytes")
            Log.d("MediaUpload", "Compressed (WebP) size: ${compressed.length()} bytes")

            // wp/v2/media نیاز به WordPress Application Password دارد (نه WooCommerce key)
            val authHeader = if (credentialsManager.hasWpCredentials()) {
                Log.d("MediaUpload", "Using WP Application Password for upload")
                // WP اغلب App Password را با فاصله نشون می‌ده — باید حذف بشه قبل از encode
                val cleanPassword = credentialsManager.wpAppPassword.replace(" ", "")
                buildAuthHeader(credentialsManager.wpUsername, cleanPassword)
            } else {
                Log.w("MediaUpload", "WP Application Password not set — trying consumer key (may fail)")
                buildAuthHeader(credentialsManager.consumerKey, credentialsManager.secretKey)
            }
            Log.d("MediaUpload", "wpUsername length: ${credentialsManager.wpUsername.length}")
            Log.d("MediaUpload", "wpAppPassword length (after strip): ${credentialsManager.wpAppPassword.replace(" ", "").length}")

            // ── wp/v2/media — raw binary body ─────────────────────────────
            val requestBody = compressed.asRequestBody("image/webp".toMediaType())

            val response = api.uploadMedia(
                authHeader         = authHeader,
                contentDisposition = "attachment; filename=\"$fileName\"",
                contentType        = "image/webp",
                body               = requestBody
            )

            Log.d("MediaUpload", "Upload response code: ${response.code()}")

            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string() ?: ""
                Log.e("MediaUpload", "Upload failed body: $errorBody")
                throw Exception("آپلود تصویر ناموفق: ${response.code()}")
            }

            val mediaDto  = response.body()
            val mediaId   = mediaDto?.id ?: 0
            val sourceUrl = mediaDto?.sourceUrl ?: ""
            Log.d("MediaUpload", "Uploaded URL: $sourceUrl  id=$mediaId")

            if (sourceUrl.isBlank()) throw Exception("آپلود موفق بود اما URL تصویر دریافت نشد")

            return Pair(mediaId, sourceUrl)

        } finally {
            inFlightMutex.withLock { inFlightUris.remove(uriKey) }
        }
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

    /**
     * حذف تصویر از سرور از طریق WP REST API
     * Authorization از OkHttp interceptor اضافه می‌شه (WP Application Password)
     */
    suspend fun deleteMedia(mediaId: Int) {
        Log.d("MediaUpload", ">>> Sending DELETE wp/v2/media/$mediaId")
        val response = api.deleteMediaWp(id = mediaId, force = true)
        Log.d("MediaUpload", "Delete response code: ${response.code()}")

        when {
            response.isSuccessful ->
                Log.d("MediaUpload", "<<< Delete success id=$mediaId")
            response.code() == 401 -> throw Exception("اعتبارسنجی حذف ناموفق (401)")
            response.code() == 404 -> throw Exception("تصویر پیدا نشد (404)")
            response.code() == 403 -> throw Exception("دسترسی حذف مجاز نیست (403)")
            else -> throw Exception("حذف تصویر ناموفق: ${response.code()}")
        }
    }

    /**
     * ساخت هدر Authorization به صورت Basic Auth
     */
    private fun buildAuthHeader(consumerKey: String, consumerSecret: String): String {
        val credentials = "$consumerKey:$consumerSecret"
        val encoded = Base64.encodeToString(
            credentials.toByteArray(Charsets.UTF_8),
            Base64.NO_WRAP
        )
        return "Basic $encoded"
    }
}
