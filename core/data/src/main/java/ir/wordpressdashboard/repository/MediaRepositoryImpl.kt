package ir.wordpressdashboard.repository

import ir.wordpressdashboard.datasource.MediaLocalDataSource
import ir.wordpressdashboard.datasource.MediaRemoteDataSource
import ir.wordpressdashboard.model.Media
import ir.wordpressdashboard.model.MediaDto
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val remoteDataSource: MediaRemoteDataSource,
    private val localDataSource: MediaLocalDataSource
) : MediaRepository {
    override suspend fun getMedia(page: Int, perPage: Int): List<Media> {
        return try {
            val result = remoteDataSource.getMedia(page = page, perPage = perPage).map { it.toDomain() }
            localDataSource.saveMedia(page, result)
            result
        } catch (e: Exception) {
            val cached = localDataSource.getMedia(page)
            if (cached.isNotEmpty()) cached else throw e
        }
    }

    private fun MediaDto.toDomain(): Media = Media(
        id = id,
        title = title.rendered,
        sourceUrl = sourceUrl,
        mediaType = mediaType,
        mimeType = mimeType
    )
}
