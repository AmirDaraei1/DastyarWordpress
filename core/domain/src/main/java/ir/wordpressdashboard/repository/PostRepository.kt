package ir.wordpressdashboard.repository

import ir.wordpressdashboard.model.Post

interface PostRepository {
    suspend fun getPosts(): List<Post>
}
