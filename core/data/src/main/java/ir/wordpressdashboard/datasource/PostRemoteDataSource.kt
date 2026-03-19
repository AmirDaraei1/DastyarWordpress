package ir.wordpressdashboard.datasource

import ir.wordpressdashboard.api.PostApi
import ir.wordpressdashboard.model.CreatePostRequest
import ir.wordpressdashboard.model.PostDto
import ir.wordpressdashboard.model.UpdatePostRequest
import javax.inject.Inject

class PostRemoteDataSource @Inject constructor(
    private val api: PostApi
) {
    suspend fun getPosts(page: Int = 1, perPage: Int = 10, status: String = "any"): List<PostDto> =
        api.getPosts(page = page, perPage = perPage, status = status)

    suspend fun getPost(id: Int): PostDto = api.getPost(id)

    suspend fun createPost(title: String, content: String, excerpt: String, status: String): PostDto =
        api.createPost(CreatePostRequest(title = title, content = content, excerpt = excerpt, status = status))

    suspend fun updatePost(id: Int, title: String, content: String, status: String): PostDto =
        api.updatePost(id, UpdatePostRequest(title = title, content = content, status = status))

    suspend fun deletePost(id: Int): PostDto = api.deletePost(id)
}
