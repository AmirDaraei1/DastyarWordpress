package ir.wordpressdashboard.usecase

import ir.wordpressdashboard.repository.ProductRepository

class GetProductUseCase @Inject constructor(
    private val repository: ProductRepository
){
}