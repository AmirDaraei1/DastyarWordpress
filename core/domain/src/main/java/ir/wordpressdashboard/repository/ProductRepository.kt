package ir.wordpressdashboard.repository

import ir.wordpressdashboard.model.Products

interface ProductRepository {
    suspend fun getProducts(page: Int = 1, perPage: Int = 10): List<Products>
    fun getCachedProducts(page: Int = 1, perPage: Int = 10): List<Products>?
    suspend fun createProduct(
        name: String,
        description: String,
        price: String,
        stockStatus: String,
        imageUris: List<String> = emptyList()
    ): Products
}

