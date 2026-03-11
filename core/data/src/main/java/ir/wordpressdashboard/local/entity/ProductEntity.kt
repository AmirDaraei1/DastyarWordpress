package ir.wordpressdashboard.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val price: String,
    val description: String,
    val permalink: String,
    val stockStatus: String,
    val page: Int   // کدام صفحه API این محصول را آورده
)

@Entity(tableName = "product_images")
data class ProductImageEntity(
    @PrimaryKey(autoGenerate = true) val rowId: Int = 0,
    val imageId: Int,
    val productId: Int,   // foreign key به products
    val src: String
)
