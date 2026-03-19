package ir.wordpressdashboard.repository

import ir.wordpressdashboard.model.Post

interface PostRepository {
    suspend fun getPosts(page: Int = 1, perPage: Int = 10): List<Post>
    suspend fun createPost(title: String, content: String, excerpt: String, status: String): Post
    suspend fun updatePost(id: Int, title: String, content: String, status: String): Post
    suspend fun deletePost(id: Int)
}
