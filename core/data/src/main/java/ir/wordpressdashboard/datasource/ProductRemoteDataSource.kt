package ir.wordpressdashboard.datasource

import ir.wordpressdashboard.api.ProductApi
import ir.wordpressdashboard.model.CreateProductRequest
import ir.wordpressdashboard.model.ProductsDto
import javax.inject.Inject

class ProductRemoteDataSource @Inject constructor(
    private val api: ProductApi
) {
    suspend fun getProducts(page: Int = 1, perPage: Int = 10): List<ProductsDto> =
        api.getProduct(page = page, perPage = perPage)

    suspend fun createProduct(request: CreateProductRequest): ProductsDto =
        api.createProduct(request)

    suspend fun updateProduct(id: Int, request: CreateProductRequest): ProductsDto =
        api.updateProduct(id, request)

    suspend fun deleteProduct(id: Int) {
        api.deleteProduct(id = id, force = true)
    }
}

