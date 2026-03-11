package ir.wordpressdashboard.repository

import ir.wordpressdashboard.model.Media

interface MediaRepository {
    suspend fun getMedia(page: Int = 1, perPage: Int = 20): List<Media>
}
