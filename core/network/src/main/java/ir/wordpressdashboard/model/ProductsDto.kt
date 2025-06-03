package ir.wordpressdashboard.model
import kotlinx.serialization.Serializable

@Serializable
data class ProductsDto(
    val id: Int,
    val name: String,
    val price: String,
    val description: String,
    val permalink: String,
    val images: List<ProductImageDto>,
    val stock_status: String
)

@Serializable
data class ProductImageDto(
    val id: Int,
    val src: String
)
