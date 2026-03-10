package ir.wordpressdashboard.api

import ir.wordpressdashboard.model.MediaDto
import retrofit2.http.GET

interface MediaApi {
    @GET("wp/v2/media?per_page=20")
    suspend fun getMedia(): List<MediaDto>
}
