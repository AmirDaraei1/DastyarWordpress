package ir.wordpressdashboard.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import ir.wordpressdashboard.local.entity.ProductEntity
import ir.wordpressdashboard.local.entity.ProductImageEntity

@Dao
interface ProductDao {

    // ── Products ─────────────────────────────────────────────────────────

    @Query("SELECT * FROM products WHERE page = :page ORDER BY id ASC")
    suspend fun getProductsByPage(page: Int): List<ProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Query("DELETE FROM products WHERE page = :page")
    suspend fun deleteProductsByPage(page: Int)

    // ── Images ───────────────────────────────────────────────────────────

    @Query("SELECT * FROM product_images WHERE productId = :productId")
    suspend fun getImagesForProduct(productId: Int): List<ProductImageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImages(images: List<ProductImageEntity>)

    @Query("DELETE FROM product_images WHERE productId IN (SELECT id FROM products WHERE page = :page)")
    suspend fun deleteImagesForPage(page: Int)

    // ── Combined save ────────────────────────────────────────────────────

    @Transaction
    suspend fun saveProductsForPage(
        page: Int,
        products: List<ProductEntity>,
        images: List<ProductImageEntity>
    ) {
        deleteImagesForPage(page)
        deleteProductsByPage(page)
        insertProducts(products)
        insertImages(images)
    }
}
