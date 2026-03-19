package ir.wordpressdashboard.repository

import ir.wordpressdashboard.datasource.ProductLocalDataSource
import ir.wordpressdashboard.datasource.ProductRemoteDataSource
import ir.wordpressdashboard.model.CreateProductRequest
import ir.wordpressdashboard.model.ProductCategory
import ir.wordpressdashboard.model.ProductImage
import ir.wordpressdashboard.model.ProductImageRequest
import ir.wordpressdashboard.model.ProductTag
import ir.wordpressdashboard.model.Products
import ir.wordpressdashboard.model.ProductsDto
import ir.wordpressdashboard.model.TaxonomyIdRequest
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
        imageIds: List<Int>,
        categoryIds: List<Int>,
        tagIds: List<Int>
    ): Products {
        val idRequests  = imageIds.map  { id  -> ProductImageRequest(id = id) }
        val srcRequests = imageUris.map { src -> ProductImageRequest(src = src) }
        val isPreOrder = stockStatus == "onbackorder"
        val request = CreateProductRequest(
            name = name,
            description = description,
            regularPrice = price,
            // پیش‌فروش: manage_stock=true + backorders=yes + stock_quantity=0
            stockStatus = if (isPreOrder) "onbackorder" else stockStatus,
            manageStock = isPreOrder,
            backorders = if (isPreOrder) "yes" else "no",
            stockQuantity = if (isPreOrder) 0 else null,
            images = idRequests + srcRequests,
            categories = categoryIds.map { TaxonomyIdRequest(it) },
            tags = tagIds.map { TaxonomyIdRequest(it) }
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
        val isPreOrder = stockStatus == "onbackorder"
        val request = CreateProductRequest(
            name = name,
            description = description,
            regularPrice = price,
            stockStatus = if (isPreOrder) "onbackorder" else stockStatus,
            manageStock = isPreOrder,
            backorders = if (isPreOrder) "yes" else "no",
            stockQuantity = if (isPreOrder) 0 else null,
            images = imageUrls.map { ProductImageRequest(src = it) }
        )
        val updated = remoteDataSource.updateProduct(id, request).toDomain()
        localDataSource.updateProduct(updated)
        return updated
    }

    override suspend fun getCategories(): List<ProductCategory> =
        remoteDataSource.getCategories().map { ProductCategory(it.id, it.name, it.slug, it.count) }

    override suspend fun getTags(): List<ProductTag> =
        remoteDataSource.getTags().map { ProductTag(it.id, it.name, it.slug, it.count) }

    override suspend fun createCategory(name: String): ProductCategory {
        val dto = remoteDataSource.createCategory(name)
        return ProductCategory(dto.id, dto.name, dto.slug, dto.count)
    }

    override suspend fun createTag(name: String): ProductTag {
        val dto = remoteDataSource.createTag(name)
        return ProductTag(dto.id, dto.name, dto.slug, dto.count)
    }

    private fun ProductsDto.toDomain(): Products = Products(
        id = id,
        name = name,
        price = price,
        description = description,
        permalink = permalink,
        images = images.map { ProductImage(it.id, it.src) },
        // پیش‌فروش: اگر stock_status=onbackorder بود یا manage_stock=true + backorders=yes بود
        stock_status = when {
            stock_status == "onbackorder" -> "onbackorder"
            manageStock && backorders == "yes" -> "onbackorder"
            else -> stock_status
        }
    )
}
