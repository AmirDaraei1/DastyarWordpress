package ir.wordpressdashboard.model

import kotlinx.serialization.Serializable

@Serializable
data class PostDto(
    val id: Int,
    val title: TitleDto
)

@Serializable
data class TitleDto(
    val rendered: String
)
