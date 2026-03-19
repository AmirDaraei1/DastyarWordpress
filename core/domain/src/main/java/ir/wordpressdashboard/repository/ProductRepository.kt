package ir.wordpressdashboard.repository

import ir.wordpressdashboard.model.ProductCategory
import ir.wordpressdashboard.model.ProductTag
import ir.wordpressdashboard.model.Products

interface ProductRepository {
    suspend fun getProducts(page: Int = 1, perPage: Int = 10): List<Products>
    fun getCachedProducts(page: Int = 1, perPage: Int = 10): List<Products>?
    suspend fun createProduct(
        name: String,
        description: String,
        price: String,
        stockStatus: String,
        imageUris: List<String> = emptyList(),
        imageIds: List<Int> = emptyList(),
        categoryIds: List<Int> = emptyList(),
        tagIds: List<Int> = emptyList()
    ): Products
    suspend fun deleteProduct(id: Int)
    suspend fun updateProduct(
        id: Int,
        name: String,
        description: String,
        price: String,
        stockStatus: String,
        imageUrls: List<String>
    ): Products
    suspend fun getCategories(): List<ProductCategory>
    suspend fun getTags(): List<ProductTag>
    suspend fun createCategory(name: String): ProductCategory
    suspend fun createTag(name: String): ProductTag
}
