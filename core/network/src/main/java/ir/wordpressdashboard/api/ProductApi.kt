package ir.wordpressdashboard.api

import ir.wordpressdashboard.model.CreateProductRequest
import ir.wordpressdashboard.model.ProductCategoryDto
import ir.wordpressdashboard.model.ProductTagDto
import ir.wordpressdashboard.model.ProductsDto
import ir.wordpressdashboard.model.TaxonomyCreateRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApi {
    @GET("products")
    suspend fun getProduct(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 10
    ): List<ProductsDto>

    @POST("products")
    suspend fun createProduct(
        @Body request: CreateProductRequest
    ): ProductsDto

    @PUT("products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Int,
        @Body request: CreateProductRequest
    ): ProductsDto

    @DELETE("products/{id}")
    suspend fun deleteProduct(
        @Path("id") id: Int,
        @Query("force") force: Boolean = true
    ): ProductsDto

    @GET("products/categories")
    suspend fun getCategories(
        @Query("per_page") perPage: Int = 100,
        @Query("hide_empty") hideEmpty: Boolean = false
    ): List<ProductCategoryDto>

    @GET("products/tags")
    suspend fun getTags(
        @Query("per_page") perPage: Int = 100,
        @Query("hide_empty") hideEmpty: Boolean = false
    ): List<ProductTagDto>

    @POST("products/categories")
    suspend fun createCategory(
        @Body body: TaxonomyCreateRequest
    ): ProductCategoryDto

    @POST("products/tags")
    suspend fun createTag(
        @Body body: TaxonomyCreateRequest
    ): ProductTagDto
}
