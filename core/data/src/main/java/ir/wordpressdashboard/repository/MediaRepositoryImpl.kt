package ir.wordpressdashboard.repository

import ir.wordpressdashboard.datasource.MediaRemoteDataSource
import ir.wordpressdashboard.model.Media
import ir.wordpressdashboard.model.MediaDto
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val remoteDataSource: MediaRemoteDataSource
) : MediaRepository {
    override suspend fun getMedia(): List<Media> =
        remoteDataSource.getMedia().map { it.toDomain() }

    private fun MediaDto.toDomain(): Media = Media(
        id = id,
        title = title.rendered,
        sourceUrl = sourceUrl,
        mediaType = mediaType,
        mimeType = mimeType
    )
}
