package ir.wordpressdashboard.datasource

import ir.wordpressdashboard.local.dao.PostDao
import ir.wordpressdashboard.local.entity.PostEntity
import ir.wordpressdashboard.model.Post
import javax.inject.Inject

class PostLocalDataSource @Inject constructor(
    private val dao: PostDao
) {
    suspend fun getPosts(page: Int): List<Post> =
        dao.getPostsByPage(page).map { it.toDomain() }

    suspend fun savePosts(page: Int, posts: List<Post>) {
        dao.deletePostsByPage(page)
        dao.insertPosts(posts.map { it.toEntity(page) })
    }

    suspend fun savePost(post: Post) {
        dao.insertPost(post.toEntity())
    }

    suspend fun deletePost(id: Int) {
        dao.deletePostById(id)
    }

    private fun PostEntity.toDomain() = Post(
        id = id,
        title = title,
        content = content,
        excerpt = excerpt,
        status = status,
        date = date
    )

    private fun Post.toEntity(page: Int = 0) = PostEntity(
        id = id,
        title = title,
        content = content,
        excerpt = excerpt,
        status = status,
        date = date,
        page = page
    )
}
