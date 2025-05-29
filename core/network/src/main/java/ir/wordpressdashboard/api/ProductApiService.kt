package ir.wordpressdashboard.network

import ir.wordpressdashboard.model.Product

interface ProductApiService {
    suspend fun getProduct():Product
}