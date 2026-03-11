package ir.wordpressdashboard.datasource

import ir.wordpressdashboard.local.dao.PostDao
import ir.wordpressdashboard.local.entity.PostEntity
import ir.wordpressdashboard.model.Post
import javax.inject.Inject

class PostLocalDataSource @Inject constructor(
    private val dao: PostDao
) {
    suspend fun getPosts(page: Int): List<Post> =
        dao.getPostsByPage(page).map { Post(id = it.id, title = it.title) }

    suspend fun savePosts(page: Int, posts: List<Post>) {
        dao.deletePostsByPage(page)
        dao.insertPosts(posts.map { PostEntity(id = it.id, title = it.title, page = page) })
    }
}
