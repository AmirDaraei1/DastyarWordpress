package ir.wordpressdashboard.api
import ir.wordpressdashboard.model.ProductsDto
import retrofit2.http.GET

interface ProductApi {
    @GET("products")
    suspend fun getProduct(): List<ProductsDto>
}