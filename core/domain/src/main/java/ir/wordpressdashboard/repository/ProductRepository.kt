package ir.wordpressdashboard.repository
import ir.wordpressdashboard.model.Products

interface ProductRepository {
    suspend fun getProducts(page: Int = 1, perPage: Int = 10): List<Products>
    fun getCachedProducts(page: Int = 1, perPage: Int = 10): List<Products>?
}