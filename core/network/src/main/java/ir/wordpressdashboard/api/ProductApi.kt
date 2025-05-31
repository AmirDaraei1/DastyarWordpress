package ir.wordpressdashboard.api

import ir.wordpressdashboard.model.Products

//todo for use real api service complete GET
//@GET
interface ProductApiService {
    suspend fun getProduct(): List<Products>
}