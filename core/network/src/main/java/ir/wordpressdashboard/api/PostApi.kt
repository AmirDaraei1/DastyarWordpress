package ir.wordpressdashboard.api

import ir.wordpressdashboard.model.PostDto
import retrofit2.http.GET

interface PostApi {
    @GET("wp/v2/posts?filter[posts_per_page]=10&fields=id,title")
    suspend fun getPosts(): List<PostDto>
}
