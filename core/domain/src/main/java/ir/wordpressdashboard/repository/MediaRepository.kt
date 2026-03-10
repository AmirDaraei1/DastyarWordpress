package ir.wordpressdashboard.repository

import ir.wordpressdashboard.model.Media

interface MediaRepository {
    suspend fun getMedia(): List<Media>
}
