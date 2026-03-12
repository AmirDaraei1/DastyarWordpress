package ir.wordpressdashboard.api

import ir.wordpressdashboard.model.CreateProductRequest
import ir.wordpressdashboard.model.ProductsDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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
}

