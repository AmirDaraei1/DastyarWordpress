package ir.wordpressdashboard.api

import ir.wordpressdashboard.model.Products
import retrofit2.http.GET

//todo for use real api service complete GET
//@GET
interface ProductApi {
    @GET("Products")
    suspend fun getProduct(): List<Products>
}