package ir.wordpressdashboard.api

import ir.wordpressdashboard.model.MediaDto
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface MediaApi {
    @GET("wp/v2/media")
    suspend fun getMedia(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): List<MediaDto>

    // آپلود — سرور body خالی برمی‌گرداند، ID در header x-wp-upload-attachment-id است
    @Multipart
    @POST("api/upload_media")
    suspend fun uploadMediaCustom(
        @Part file: MultipartBody.Part
    ): Response<ResponseBody>

    // گرفتن اطلاعات media با ID برای بدست آوردن source_url
    @GET("wp/v2/media/{id}")
    suspend fun getMediaById(
        @Path("id") id: Int
    ): MediaDto
}
