package ir.wordpressdashboard.datasource

import ir.wordpressdashboard.api.ProductApi
import ir.wordpressdashboard.model.Products
import ir.wordpressdashboard.model.ProductsDto

import javax.inject.Inject

class ProductRemoteDataSource @Inject constructor(
    private val api: ProductApi
) {
    suspend fun getProducts() : List<ProductsDto> = api.getProduct()
}