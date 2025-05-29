package ir.wordpressdashboard.model

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Int,
    val name: String,
    val price: String,
    val description: String,
    val permalink: String,
    val images: List<ProductImage>,
    val stock_status: String
)

@Serializable
data class ProductImage(
    val id: Int,
    val src: String
)
