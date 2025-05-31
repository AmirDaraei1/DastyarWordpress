package ir.wordpressdashboard.datasource

class ProductRemoteDataSource @Inject constructor(
    private val api: ProductApi
) {
    suspend fun getProducts() : List<ProductDto> = api.getProducts()
}