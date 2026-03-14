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

    suspend fun deleteProduct(productId: Int) {
        dao.deleteImagesForProduct(productId)
        dao.deleteProductById(productId)
    }

    suspend fun updateProduct(product: ir.wordpressdashboard.model.Products) {
        dao.insertProducts(listOf(
            ir.wordpressdashboard.local.entity.ProductEntity(
                id = product.id,
                name = product.name,
                price = product.price,
                description = product.description,
                permalink = product.permalink,
                stockStatus = product.stock_status,
                page = 1
            )
        ))
        dao.deleteImagesForProduct(product.id)
        dao.insertImages(product.images.map {
            ir.wordpressdashboard.local.entity.ProductImageEntity(
                imageId = it.id,
                productId = product.id,
                src = it.src
            )
        })
    }
}
