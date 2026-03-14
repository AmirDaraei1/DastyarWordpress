package ir.wordpressdashboard.di

import android.util.Base64
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.wordpressdashboard.api.MediaApi
import ir.wordpressdashboard.api.PostApi
import ir.wordpressdashboard.api.ProductApi
import ir.wordpressdashboard.provider.CredentialsManager
import ir.wordpressdashboard.repository.CredentialsRepository
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import retrofit2.Retrofit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WpRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UploadRetrofit

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    @Singleton
    abstract fun bindCredentialsRepository(
        credentialsManager: CredentialsManager
    ): CredentialsRepository

    companion object {

        /** Basic Auth با UTF-8 — سازگار با وردپرس/WooCommerce */
        fun buildBasicAuthHeader(consumerKey: String, consumerSecret: String): String {
            val credentials = "$consumerKey:$consumerSecret"
            val encoded = Base64.encodeToString(
                credentials.toByteArray(Charsets.UTF_8),
                Base64.NO_WRAP
            )
            return "Basic $encoded"
        }

        @Provides
        @Singleton
        fun provideOkHttpClient(credentialsManager: CredentialsManager): OkHttpClient {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            val authInterceptor = Interceptor { chain ->
                // فقط اگر request هنوز Authorization نداره، اضافه کن
                val original = chain.request()
                val req = if (original.header("Authorization") == null) {
                    val auth = buildBasicAuthHeader(
                        credentialsManager.consumerKey,
                        credentialsManager.secretKey
                    )
                    original.newBuilder().addHeader("Authorization", auth).build()
                } else original
                chain.proceed(req)
            }

            return OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .build()
        }

        // ── OkHttpClient مخصوص آپلود/حذف media — با WP Application Password ──
        @Provides
        @Singleton
        @UploadRetrofit
        fun provideUploadOkHttpClient(credentialsManager: CredentialsManager): OkHttpClient {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            val authInterceptor = Interceptor { chain ->
                val original = chain.request()
                val req = if (original.header("Authorization") == null) {
                    // اول WP Application Password، اگر نبود consumer key
                    val auth = if (credentialsManager.hasWpCredentials()) {
                        val cleanPass = credentialsManager.wpAppPassword.replace(" ", "")
                        buildBasicAuthHeader(credentialsManager.wpUsername, cleanPass)
                    } else {
                        buildBasicAuthHeader(credentialsManager.consumerKey, credentialsManager.secretKey)
                    }
                    original.newBuilder().addHeader("Authorization", auth).build()
                } else {
                    original
                }
                chain.proceed(req)
            }

            return OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(180, TimeUnit.SECONDS)
                .writeTimeout(180, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .build()
        }

        @Provides
        @Singleton
        fun provideJson(): Json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }

        @Provides
        @Singleton
        fun provideRetrofit(
            okHttpClient: OkHttpClient,
            credentialsManager: CredentialsManager,
            json: Json
        ): Retrofit {
            val contentType = "application/json".toMediaType()
            return Retrofit.Builder()
                .baseUrl(credentialsManager.baseUrl)
                .client(okHttpClient)
                .addConverterFactory(json.asConverterFactory(contentType))
                .build()
        }

        @Provides
        @Singleton
        fun provideProductApi(retrofit: Retrofit): ProductApi =
            retrofit.create(ProductApi::class.java)

        @Provides
        @Singleton
        @WpRetrofit
        fun provideWpRetrofit(
            okHttpClient: OkHttpClient,
            credentialsManager: CredentialsManager,
            json: Json
        ): Retrofit {
            val wcBase = credentialsManager.baseUrl
            val wpBase = if (wcBase.contains("wc/v3")) {
                wcBase.substringBefore("wc/v3")
            } else {
                wcBase
            }
            val contentType = "application/json".toMediaType()
            return Retrofit.Builder()
                .baseUrl(wpBase)
                .client(okHttpClient)
                .addConverterFactory(json.asConverterFactory(contentType))
                .build()
        }

        @Provides
        @Singleton
        fun providePostApi(
            @UploadRetrofit uploadOkHttpClient: OkHttpClient,
            credentialsManager: CredentialsManager,
            json: Json
        ): PostApi {
            val wcBase = credentialsManager.baseUrl
            val wpBase = if (wcBase.contains("wc/v3")) wcBase.substringBefore("wc/v3") else wcBase
            val contentType = "application/json".toMediaType()
            return Retrofit.Builder()
                .baseUrl(wpBase)
                .client(uploadOkHttpClient)
                .addConverterFactory(json.asConverterFactory(contentType))
                .build()
                .create(PostApi::class.java)
        }

        @Provides
        @Singleton
        fun provideMediaApi(
            @UploadRetrofit uploadOkHttpClient: OkHttpClient,
            credentialsManager: CredentialsManager,
            json: Json
        ): MediaApi {
            val wcBase = credentialsManager.baseUrl
            val wpBase = if (wcBase.contains("wc/v3")) {
                wcBase.substringBefore("wc/v3")
            } else {
                wcBase
            }
            val contentType = "application/json".toMediaType()
            val wpAuthRetrofit = Retrofit.Builder()
                .baseUrl(wpBase)
                .client(uploadOkHttpClient)
                .addConverterFactory(json.asConverterFactory(contentType))
                .build()
            return wpAuthRetrofit.create(MediaApi::class.java)
        }

        @Provides
        @Singleton
        fun provideUploadMediaApi(
            @UploadRetrofit uploadOkHttpClient: OkHttpClient,
            credentialsManager: CredentialsManager,
            json: Json
        ): ir.wordpressdashboard.datasource.MediaUploadDataSource {
            val wcBase = credentialsManager.baseUrl
            val wpBase = if (wcBase.contains("wc/v3")) {
                wcBase.substringBefore("wc/v3")
            } else {
                wcBase
            }
            val contentType = "application/json".toMediaType()
            val uploadRetrofit = Retrofit.Builder()
                .baseUrl(wpBase)
                .client(uploadOkHttpClient)
                .addConverterFactory(json.asConverterFactory(contentType))
                .build()
            val uploadApi = uploadRetrofit.create(MediaApi::class.java)
            return ir.wordpressdashboard.datasource.MediaUploadDataSource(uploadApi, credentialsManager)
        }
    }
}