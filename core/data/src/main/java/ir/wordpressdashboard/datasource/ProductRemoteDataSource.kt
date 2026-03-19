package ir.wordpressdashboard.datasource

import ir.wordpressdashboard.api.ProductApi
import ir.wordpressdashboard.model.CreateProductRequest
import ir.wordpressdashboard.model.ProductCategoryDto
import ir.wordpressdashboard.model.ProductTagDto
import ir.wordpressdashboard.model.ProductsDto
import ir.wordpressdashboard.model.TaxonomyCreateRequest
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

    suspend fun getCategories(): List<ProductCategoryDto> =
        api.getCategories()

    suspend fun getTags(): List<ProductTagDto> =
        api.getTags()

    suspend fun createCategory(name: String): ProductCategoryDto =
        api.createCategory(TaxonomyCreateRequest(name = name))

    suspend fun createTag(name: String): ProductTagDto =
        api.createTag(TaxonomyCreateRequest(name = name))
}
