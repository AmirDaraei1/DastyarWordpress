package ir.wordpressdashboard.datasource

import ir.wordpressdashboard.api.PostApi
import ir.wordpressdashboard.model.PostDto
import javax.inject.Inject

class PostRemoteDataSource @Inject constructor(
    private val api: PostApi
) {
    suspend fun getPosts(page: Int = 1, perPage: Int = 10): List<PostDto> =
        api.getPosts(page = page, perPage = perPage)
}
