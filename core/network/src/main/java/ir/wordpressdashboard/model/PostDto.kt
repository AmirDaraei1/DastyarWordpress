package ir.wordpressdashboard.model

import kotlinx.serialization.Serializable

@Serializable
data class PostDto(
    val id: Int,
    val title: TitleDto,
    val content: ContentDto? = null,
    val excerpt: ExcerptDto? = null,
    val status: String? = null,
    val date: String? = null
)

@Serializable
data class TitleDto(val rendered: String)

@Serializable
data class ContentDto(val rendered: String)

@Serializable
data class ExcerptDto(val rendered: String)

@Serializable
data class UpdatePostRequest(
    val title: String,
    val content: String,
    val status: String
)

@Serializable
data class CreatePostRequest(
    val title: String,
    val content: String,
    val excerpt: String = "",
    val status: String = "draft"
)

