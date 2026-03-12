@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)
package ir.wordpressdashboard.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateProductRequest(
    val name: String,
    val description: String,
    @SerialName("regular_price") val regularPrice: String,
    val status: String = "publish",
    @SerialName("stock_status") val stockStatus: String = "instock",
    @SerialName("manage_stock") val manageStock: Boolean = false,
    val images: List<ProductImageRequest> = emptyList()
)

@Serializable
data class ProductImageRequest(
    val src: String
)
