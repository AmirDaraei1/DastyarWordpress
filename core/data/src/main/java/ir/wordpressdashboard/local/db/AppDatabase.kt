package ir.wordpressdashboard.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ir.wordpressdashboard.local.dao.MediaDao
import ir.wordpressdashboard.local.dao.PostDao
import ir.wordpressdashboard.local.dao.ProductDao
import ir.wordpressdashboard.local.entity.MediaEntity
import ir.wordpressdashboard.local.entity.PostEntity
import ir.wordpressdashboard.local.entity.ProductEntity
import ir.wordpressdashboard.local.entity.ProductImageEntity

@Database(
    entities = [
        ProductEntity::class,
        ProductImageEntity::class,
        PostEntity::class,
        MediaEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun postDao(): PostDao
    abstract fun mediaDao(): MediaDao
}
