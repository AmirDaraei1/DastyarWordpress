package ir.wordpressdashboard.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MediaDto(
    val id: Int,
    val title: MediaTitleDto,
    @SerialName("source_url") val sourceUrl: String,
    @SerialName("media_type") val mediaType: String,
    @SerialName("mime_type") val mimeType: String
)

@Serializable
data class MediaTitleDto(
    val rendered: String
)

@Serializable
data class DeleteMediaResponseDto(
    val success: Boolean,
    val message: String
)

