package ir.wordpressdashboard.datasource

import ir.wordpressdashboard.local.dao.ProductDao
import ir.wordpressdashboard.local.entity.ProductEntity
import ir.wordpressdashboard.local.entity.ProductImageEntity
import ir.wordpressdashboard.model.ProductImage
import ir.wordpressdashboard.model.Products
import javax.inject.Inject

class ProductLocalDataSource @Inject constructor(
    private val dao: ProductDao
) {
    suspend fun getProducts(page: Int): List<Products> {
        val entities = dao.getProductsByPage(page)
        return entities.map { entity ->
            val images = dao.getImagesForProduct(entity.id)
                .map { ProductImage(it.imageId, it.src) }
            Products(
                id = entity.id,
                name = entity.name,
                price = entity.price,
                description = entity.description,
                permalink = entity.permalink,
                images = images,
                stock_status = entity.stockStatus
            )
        }
    }

    suspend fun saveProducts(page: Int, products: List<Products>) {
        val entities = products.map {
            ProductEntity(
                id = it.id,
                name = it.name,
                price = it.price,
                description = it.description,
                permalink = it.permalink,
                stockStatus = it.stock_status,
                page = page
            )
        }
        val imageEntities = products.flatMap { product ->
            product.images.map { img ->
                ProductImageEntity(
                    imageId = img.id,
                    productId = product.id,
                    src = img.src
                )
            }
        }
        dao.saveProductsForPage(page, entities, imageEntities)
    }
}
