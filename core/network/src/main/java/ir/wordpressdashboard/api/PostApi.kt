package ir.wordpressdashboard.api

import ir.wordpressdashboard.model.CreatePostRequest
import ir.wordpressdashboard.model.DeletePostResponse
import ir.wordpressdashboard.model.PostDto
import ir.wordpressdashboard.model.UpdatePostRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PostApi {
    @GET("wp/v2/posts")
    suspend fun getPosts(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 10,
        @Query("status") status: String = "any"
    ): List<PostDto>

    @GET("wp/v2/posts/{id}")
    suspend fun getPost(@Path("id") id: Int): PostDto

    @POST("wp/v2/posts")
    suspend fun createPost(@Body request: CreatePostRequest): PostDto

    @POST("wp/v2/posts/{id}")
    suspend fun updatePost(
        @Path("id") id: Int,
        @Body request: UpdatePostRequest
    ): PostDto

    @DELETE("wp/v2/posts/{id}")
    suspend fun deletePost(
        @Path("id") id: Int,
        @Query("force") force: Boolean = true
    ): DeletePostResponse
}
