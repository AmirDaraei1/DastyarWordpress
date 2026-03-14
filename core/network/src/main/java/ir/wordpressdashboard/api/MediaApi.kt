package ir.wordpressdashboard.api

import ir.wordpressdashboard.model.DeleteMediaResponseDto
import ir.wordpressdashboard.model.MediaDto
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MediaApi {
    @GET("wp/v2/media")
    suspend fun getMedia(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): List<MediaDto>

    // آپلود تصویر از طریق WP REST API — raw binary body
    // Content-Disposition: attachment; filename="image.webp"
    // Content-Type: image/webp
    @POST("wp/v2/media")
    suspend fun uploadMedia(
        @Header("Authorization")       authHeader:          String,
        @Header("Content-Disposition") contentDisposition:  String,
        @Header("Content-Type")        contentType:         String,
        @Body                          body:                 RequestBody
    ): Response<MediaDto>

    // گرفتن اطلاعات media با ID برای بدست آوردن source_url
    @GET("wp/v2/media/{id}")
    suspend fun getMediaById(
        @Path("id") id: Int
    ): MediaDto

    // حذف تصویر از طریق WP REST API — Authorization از interceptor اضافه می‌شه
    @DELETE("wp/v2/media/{id}")
    suspend fun deleteMediaWp(
        @Path("id")     id:    Int,
        @Query("force") force: Boolean = true
    ): Response<ResponseBody>

    // حذف تصویر از طریق API سفارشی پلاگین
    @DELETE("api/delete_media/{id}")
    suspend fun deleteMediaCustom(
        @Path("id") id: Int,
        @Header("Authorization") authHeader: String
    ): Response<DeleteMediaResponseDto>
}
