package ir.wordpressdashboard.model

data class ProductCategory(
    val id: Int,
    val name: String,
    val slug: String,
    val count: Int = 0
)

data class ProductTag(
    val id: Int,
    val name: String,
    val slug: String,
    val count: Int = 0
)
