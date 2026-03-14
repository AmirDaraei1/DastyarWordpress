package ir.wordpressdashboard.datasource

import ir.wordpressdashboard.api.PostApi
import ir.wordpressdashboard.model.PostDto
import ir.wordpressdashboard.model.UpdatePostRequest
import javax.inject.Inject

class PostRemoteDataSource @Inject constructor(
    private val api: PostApi
) {
    suspend fun getPosts(page: Int = 1, perPage: Int = 10): List<PostDto> =
        api.getPosts(page = page, perPage = perPage)

    suspend fun getPost(id: Int): PostDto = api.getPost(id)

    suspend fun updatePost(id: Int, title: String, content: String, status: String): PostDto =
        api.updatePost(id, UpdatePostRequest(title = title, content = content, status = status))
}
