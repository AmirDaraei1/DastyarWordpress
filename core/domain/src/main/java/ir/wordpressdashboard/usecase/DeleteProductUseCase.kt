package ir.wordpressdashboard.usecase

import ir.wordpressdashboard.repository.ProductRepository
import javax.inject.Inject

class DeleteProductUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(id: Int) = repository.deleteProduct(id)
}
