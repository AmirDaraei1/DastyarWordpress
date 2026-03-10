package ir.wordpressdashboard.datasource

import ir.wordpressdashboard.api.PostApi
import ir.wordpressdashboard.model.PostDto
import javax.inject.Inject

class PostRemoteDataSource @Inject constructor(
    private val api: PostApi
) {
    suspend fun getPosts(): List<PostDto> = api.getPosts()
}
