package ir.wordpressdashboard.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ir.wordpressdashboard.local.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM posts ORDER BY id DESC")
    suspend fun getAllPosts(): List<PostEntity>

    @Query("SELECT * FROM posts WHERE page = :page ORDER BY id DESC")
    suspend fun getPostsByPage(page: Int): List<PostEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Query("DELETE FROM posts WHERE page = :page")
    suspend fun deletePostsByPage(page: Int)

    @Query("DELETE FROM posts")
    suspend fun deleteAllPosts()
}
