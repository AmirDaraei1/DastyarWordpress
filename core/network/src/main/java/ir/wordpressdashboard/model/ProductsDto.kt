@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)
package ir.wordpressdashboard.model
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductsDto(
    val id: Int,
    val name: String,
    val price: String,
    val description: String,
    val permalink: String,
    val images: List<ProductImageDto>,
    @SerialName("stock_status") val stock_status: String,
    @SerialName("manage_stock") val manageStock: Boolean = false,
    @SerialName("backorders") val backorders: String = "no"
)

@Serializable
data class ProductImageDto(
    val id: Int,
    val src: String
)
