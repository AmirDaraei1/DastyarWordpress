package ir.wordpressdashboard.usecase

import ir.wordpressdashboard.model.Products
import ir.wordpressdashboard.repository.ProductRepository
import javax.inject.Inject

class UpdateProductUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(
        id: Int,
        name: String,
        description: String,
        price: String,
        stockStatus: String,
        imageUrls: List<String>
    ): Products = repository.updateProduct(
        id = id,
        name = name,
        description = description,
        price = price,
        stockStatus = stockStatus,
        imageUrls = imageUrls
    )
}
