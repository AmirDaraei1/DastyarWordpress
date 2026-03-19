package ir.wordpressdashboard.repository

import ir.wordpressdashboard.datasource.PostLocalDataSource
import ir.wordpressdashboard.datasource.PostRemoteDataSource
import ir.wordpressdashboard.model.Post
import ir.wordpressdashboard.model.PostDto
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val remoteDataSource: PostRemoteDataSource,
    private val localDataSource: PostLocalDataSource
) : PostRepository {

    override suspend fun getPosts(page: Int, perPage: Int): List<Post> {
        return try {
            val result = remoteDataSource.getPosts(page = page, perPage = perPage)
                .map { it.toDomain() }
            localDataSource.savePosts(page, result)
            result
        } catch (e: Exception) {
            val cached = localDataSource.getPosts(page)
            if (cached.isNotEmpty()) cached else throw e
        }
    }

    override suspend fun createPost(title: String, content: String, excerpt: String, status: String): Post {
        val dto = remoteDataSource.createPost(title = title, content = content, excerpt = excerpt, status = status)
        val post = dto.toDomain()
        localDataSource.savePost(post)
        return post
    }

    override suspend fun updatePost(id: Int, title: String, content: String, status: String): Post {
        val dto = remoteDataSource.updatePost(id = id, title = title, content = content, status = status)
        val post = dto.toDomain()
        localDataSource.savePost(post)
        return post
    }

    override suspend fun deletePost(id: Int) {
        remoteDataSource.deletePost(id)
        localDataSource.deletePost(id)
    }

    private fun PostDto.toDomain(): Post = Post(
        id = id,
        title = title.rendered,
        content = content?.rendered ?: "",
        excerpt = excerpt?.rendered ?: "",
        status = status ?: "publish",
        date = date ?: ""
    )
}
