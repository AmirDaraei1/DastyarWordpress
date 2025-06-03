package ir.wordpressdashboard.repository

import ir.wordpressdashboard.datasource.ProductRemoteDataSource
import ir.wordpressdashboard.model.ProductImage
import ir.wordpressdashboard.model.Products
import ir.wordpressdashboard.model.ProductsDto
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val remoteDataSource: ProductRemoteDataSource,
    // Inside here add localDataSource
) : ProductRepository {
    override suspend fun getProducts(): List<Products> =
           remoteDataSource.getProducts().map { it.toDomain()  }
       }
    // Mapping extension
    fun ProductsDto.toDomain(): Products = Products(
        id = id,
        name = name,
        price = price,
        description = description,
        permalink = permalink,
        images = images.map { ProductImage(it.id,it.src) },
        stock_status = stock_status
    )