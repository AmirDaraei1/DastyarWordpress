package ir.wordpressdashboard.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ir.wordpressdashboard.local.entity.MediaEntity

@Dao
interface MediaDao {
    @Query("SELECT * FROM media ORDER BY id DESC")
    suspend fun getAllMedia(): List<MediaEntity>

    @Query("SELECT * FROM media WHERE page = :page ORDER BY id DESC")
    suspend fun getMediaByPage(page: Int): List<MediaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(media: List<MediaEntity>)

    @Query("DELETE FROM media WHERE page = :page")
    suspend fun deleteMediaByPage(page: Int)

    @Query("DELETE FROM media WHERE id = :id")
    suspend fun deleteMediaById(id: Int)

    @Query("DELETE FROM media")
    suspend fun deleteAllMedia()
}
