package ir.wordpressdashboard.repository
import ir.wordpressdashboard.model.Products

interface ProductRepository {
    suspend fun getProducts() : List<Products>
}