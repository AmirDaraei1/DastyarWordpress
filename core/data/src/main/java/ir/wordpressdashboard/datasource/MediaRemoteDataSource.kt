package ir.wordpressdashboard.datasource

import ir.wordpressdashboard.api.MediaApi
import ir.wordpressdashboard.model.MediaDto
import javax.inject.Inject

class MediaRemoteDataSource @Inject constructor(
    private val api: MediaApi
) {
    suspend fun getMedia(): List<MediaDto> = api.getMedia()
}
