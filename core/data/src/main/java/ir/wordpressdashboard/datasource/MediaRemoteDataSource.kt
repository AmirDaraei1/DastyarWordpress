package ir.wordpressdashboard.datasource

import ir.wordpressdashboard.api.MediaApi
import ir.wordpressdashboard.model.MediaDto
import javax.inject.Inject

class MediaRemoteDataSource @Inject constructor(
    private val api: MediaApi,
    private val mediaUploadDataSource: MediaUploadDataSource
) {
    suspend fun getMedia(page: Int = 1, perPage: Int = 20): List<MediaDto> =
        api.getMedia(page = page, perPage = perPage)

    suspend fun deleteMedia(id: Int) {
        mediaUploadDataSource.deleteMedia(mediaId = id)
    }
}
