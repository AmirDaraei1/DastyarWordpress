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
            // همیشه از شبکه بخواه (حتی آفلاین — exception می‌اندازه)
            val result = remoteDataSource.getPosts(page = page, perPage = perPage)
                .map { it.toDomain() }
            // موفق شد → در دیتابیس ذخیره کن
            localDataSource.savePosts(page, result)
            result
        } catch (e: Exception) {
            // شبکه خطا داد → از دیتابیس بخواه
            val cached = localDataSource.getPosts(page)
            if (cached.isNotEmpty()) cached else throw e
        }
    }

    private fun PostDto.toDomain(): Post = Post(id = id, title = title.rendered)
}
