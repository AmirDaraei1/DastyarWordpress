@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)
package ir.wordpressdashboard.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductCategoryDto(
    val id: Int,
    val name: String,
    val slug: String,
    val count: Int = 0
)

@Serializable
data class ProductTagDto(
    val id: Int,
    val name: String,
    val slug: String,
    val count: Int = 0
)

@Serializable
data class TaxonomyCreateRequest(
    val name: String,
    @SerialName("slug") val slug: String = ""
)

