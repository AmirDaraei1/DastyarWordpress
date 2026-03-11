package ir.wordpressdashboard.api

import ir.wordpressdashboard.model.PostDto
import retrofit2.http.GET
import retrofit2.http.Query

interface PostApi {
    @GET("wp/v2/posts")
    suspend fun getPosts(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 10
    ): List<PostDto>
}
