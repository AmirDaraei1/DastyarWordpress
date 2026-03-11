package ir.wordpressdashboard.usecase
import ir.wordpressdashboard.model.Products
import ir.wordpressdashboard.repository.ProductRepository
import javax.inject.Inject

class GetProductUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(page: Int = 1, perPage: Int = 10): List<Products> =
        repository.getProducts(page = page, perPage = perPage)
}