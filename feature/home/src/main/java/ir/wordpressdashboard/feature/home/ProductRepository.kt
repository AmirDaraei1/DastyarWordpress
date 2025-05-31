package ir.wordpressdashboard.feature.home

import ir.wordpressdashboard.api.ProductApi
import ir.wordpressdashboard.model.Products

class ProductRepository(private val apiService: ProductApi) {
    suspend fun getProducts(): List<Products> = apiService.getProduct()
}