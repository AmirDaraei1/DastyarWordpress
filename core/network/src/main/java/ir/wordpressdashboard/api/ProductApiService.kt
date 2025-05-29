package ir.wordpressdashboard.api

import ir.wordpressdashboard.model.Product
import retrofit2.http.GET
//todo for use real api service complete GET
//@GET
interface ProductApiService {
    suspend fun getProduct():Product
}