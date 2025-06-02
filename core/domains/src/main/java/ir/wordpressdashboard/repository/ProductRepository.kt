package ir.wordpressdashboard.repository

import ir.wordpressdashboard.model.Product

interface ProductRepository {
    suspend fun getProducts() : List<Product>
}