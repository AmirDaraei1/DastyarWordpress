package ir.wordpressdashboard.repository

import ir.wordpressdashboard.datasource.PostRemoteDataSource
import ir.wordpressdashboard.model.Post
import ir.wordpressdashboard.model.PostDto
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val remoteDataSource: PostRemoteDataSource
) : PostRepository {
    override suspend fun getPosts(): List<Post> =
        remoteDataSource.getPosts().map { it.toDomain() }

    private fun PostDto.toDomain(): Post = Post(
        id = id,
        title = title.rendered
    )
}
