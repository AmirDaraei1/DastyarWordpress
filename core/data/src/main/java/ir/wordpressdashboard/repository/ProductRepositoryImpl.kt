package ir.wordpressdashboard.repository

import ir.wordpressdashboard.datasource.ProductLocalDataSource
import ir.wordpressdashboard.datasource.ProductRemoteDataSource
import ir.wordpressdashboard.model.CreateProductRequest
import ir.wordpressdashboard.model.ProductImage
import ir.wordpressdashboard.model.ProductImageRequest
import ir.wordpressdashboard.model.Products
import ir.wordpressdashboard.model.ProductsDto
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val remoteDataSource: ProductRemoteDataSource,
    private val localDataSource: ProductLocalDataSource,
) : ProductRepository {

    override suspend fun getProducts(page: Int, perPage: Int): List<Products> {
        return try {
            val result = remoteDataSource.getProducts(page = page, perPage = perPage)
                .map { it.toDomain() }
            localDataSource.saveProducts(page, result)
            result
        } catch (e: Exception) {
            val cached = localDataSource.getProducts(page)
            if (cached.isNotEmpty()) cached else throw e
        }
    }

    override fun getCachedProducts(page: Int, perPage: Int): List<Products>? = null

    override suspend fun createProduct(
        name: String,
        description: String,
        price: String,
        stockStatus: String,
        imageUris: List<String>,
        imageIds: List<Int>
    ): Products {
        // اگر imageIds موجود باشه، فقط id می‌فرستیم — WooCommerce کپی نمی‌گیره
        val imageRequests = if (imageIds.isNotEmpty()) {
            imageIds.map { id -> ProductImageRequest(id = id) }
        } else {
            imageUris.map { src -> ProductImageRequest(src = src) }
        }

        val request = CreateProductRequest(
            name = name,
            description = description,
            regularPrice = price,
            stockStatus = stockStatus,
            images = imageRequests
        )
        return remoteDataSource.createProduct(request).toDomain()
    }

    override suspend fun deleteProduct(id: Int) {
        remoteDataSource.deleteProduct(id)
        localDataSource.deleteProduct(id)
    }

    override suspend fun updateProduct(
        id: Int,
        name: String,
        description: String,
        price: String,
        stockStatus: String,
        imageUrls: List<String>
    ): Products {
        val request = CreateProductRequest(
            name = name,
            description = description,
            regularPrice = price,
            stockStatus = stockStatus,
            images = imageUrls.map { ProductImageRequest(src = it) }
        )
        val updated = remoteDataSource.updateProduct(id, request).toDomain()
        localDataSource.updateProduct(updated)
        return updated
    }

    private fun ProductsDto.toDomain(): Products = Products(
        id = id,
        name = name,
        price = price,
        description = description,
        permalink = permalink,
        images = images.map { ProductImage(it.id, it.src) },
        stock_status = stock_status
    )
}
