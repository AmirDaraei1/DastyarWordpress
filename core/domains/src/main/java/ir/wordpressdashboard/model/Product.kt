package ir.wordpressdashboard.model

data class Product (
    val id: Int,
    val name: String,
    val price: String,
    val description: String,
    val permalink: String,
    val images: List<ProductImage>,
    val stock_status: String
)

data class ProductImage(
    val id: Int,
    val src: String
)