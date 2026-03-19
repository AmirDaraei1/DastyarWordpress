@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)
package ir.wordpressdashboard.model

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class CreateProductRequest(
    val name: String,
    val description: String,
    @SerialName("regular_price") val regularPrice: String,
    val status: String = "publish",
    @SerialName("stock_status") val stockStatus: String = "instock",
    @SerialName("manage_stock") val manageStock: Boolean = false,
    @SerialName("backorders") val backorders: String = "no",
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("stock_quantity") val stockQuantity: Int? = null,
    val images: List<ProductImageRequest> = emptyList(),
    val categories: List<TaxonomyIdRequest> = emptyList(),
    val tags: List<TaxonomyIdRequest> = emptyList()
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class ProductImageRequest(
    @EncodeDefault(EncodeDefault.Mode.NEVER) val id: Int? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER) val src: String? = null
)

@Serializable
data class TaxonomyIdRequest(val id: Int)
