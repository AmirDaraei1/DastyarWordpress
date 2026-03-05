package ir.wordpressdashboard.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.wordpressdashboard.api.ProductApi
import ir.wordpressdashboard.provider.CredentialsManager
import ir.wordpressdashboard.repository.CredentialsRepository
import kotlinx.serialization.json.Json
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    @Singleton
    abstract fun bindCredentialsRepository(
        credentialsManager: CredentialsManager
    ): CredentialsRepository

    companion object {

        @Provides
        @Singleton
        fun provideOkHttpClient(credentialsManager: CredentialsManager): OkHttpClient {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            val authInterceptor = Interceptor { chain ->
                val credentials = Credentials.basic(
                    credentialsManager.consumerKey,
                    credentialsManager.secretKey
                )
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", credentials)
                    .build()
                chain.proceed(request)
            }

            return OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(logging)
                .build()
        }

        @Provides
        @Singleton
        fun provideRetrofit(
            okHttpClient: OkHttpClient,
            credentialsManager: CredentialsManager
        ): Retrofit {
            val contentType = "application/json".toMediaType()
            return Retrofit.Builder()
                .baseUrl(credentialsManager.baseUrl)
                .client(okHttpClient)
                .addConverterFactory(Json { ignoreUnknownKeys = true }.asConverterFactory(contentType))
                .build()
        }

        @Provides
        @Singleton
        fun provideProductApi(retrofit: Retrofit): ProductApi =
            retrofit.create(ProductApi::class.java)
    }
}