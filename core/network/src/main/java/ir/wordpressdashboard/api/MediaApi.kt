package ir.wordpressdashboard.api

import ir.wordpressdashboard.model.MediaDto
import retrofit2.http.GET
import retrofit2.http.Query

interface MediaApi {
    @GET("wp/v2/media")
    suspend fun getMedia(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): List<MediaDto>
}
