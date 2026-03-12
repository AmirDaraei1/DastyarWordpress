@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)
package ir.wordpressdashboard.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UploadMediaResponse(
    // custom API معمولاً url یا src برمی‌گرداند
    val url: String = "",
    val src: String = "",
    val id: Int = 0,
    @SerialName("source_url") val sourceUrl: String = "",
    val message: String = "",
    val success: Boolean = true
) {
    // هر فیلدی که پر باشد را به عنوان URL برگردان
    fun getImageUrl(): String = when {
        sourceUrl.isNotBlank() -> sourceUrl
        url.isNotBlank() -> url
        src.isNotBlank() -> src
        else -> ""
    }
}
