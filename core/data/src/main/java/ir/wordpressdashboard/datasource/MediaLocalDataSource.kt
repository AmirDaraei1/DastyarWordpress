package ir.wordpressdashboard.datasource

import ir.wordpressdashboard.local.dao.MediaDao
import ir.wordpressdashboard.local.entity.MediaEntity
import ir.wordpressdashboard.model.Media
import javax.inject.Inject

class MediaLocalDataSource @Inject constructor(
    private val dao: MediaDao
) {
    suspend fun getMedia(page: Int): List<Media> =
        dao.getMediaByPage(page).map {
            Media(
                id = it.id,
                title = it.title,
                sourceUrl = it.sourceUrl,
                mediaType = it.mediaType,
                mimeType = it.mimeType
            )
        }

    suspend fun saveMedia(page: Int, mediaList: List<Media>) {
        dao.deleteMediaByPage(page)
        dao.insertMedia(mediaList.map {
            MediaEntity(
                id = it.id,
                title = it.title,
                sourceUrl = it.sourceUrl,
                mediaType = it.mediaType,
                mimeType = it.mimeType,
                page = page
            )
        })
    }
}
