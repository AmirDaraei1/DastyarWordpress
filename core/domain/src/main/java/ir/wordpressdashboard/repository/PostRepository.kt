package ir.wordpressdashboard.repository

import ir.wordpressdashboard.model.Post

interface PostRepository {
    suspend fun getPosts(page: Int = 1, perPage: Int = 10): List<Post>
}
