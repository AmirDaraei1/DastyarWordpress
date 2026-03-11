package ir.wordpressdashboard.repository

import ir.wordpressdashboard.datasource.ProductLocalDataSource
import ir.wordpressdashboard.datasource.ProductRemoteDataSource
import ir.wordpressdashboard.model.ProductImage
import ir.wordpressdashboard.model.Products
import ir.wordpressdashboard.model.ProductsDto
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val remoteDataSource: ProductRemoteDataSource,
    private val localDataSource: ProductLocalDataSource,
) : ProductRepository {

    override suspend fun getProducts(page: Int, perPage: Int): List<Products> {
        return try {
            // ① همیشه از شبکه بخواه
            val result = remoteDataSource.getProducts(page = page, perPage = perPage)
                .map { it.toDomain() }
            // ② موفق شد → در دیتابیس ذخیره کن
            localDataSource.saveProducts(page, result)
            result
        } catch (e: Exception) {
            // ③ خطای شبکه → از دیتابیس بخواه
            val cached = localDataSource.getProducts(page)
            if (cached.isNotEmpty()) cached else throw e
        }
    }

    override fun getCachedProducts(page: Int, perPage: Int): List<Products>? = null

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
