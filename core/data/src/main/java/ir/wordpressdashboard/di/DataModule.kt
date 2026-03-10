package ir.wordpressdashboard.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.wordpressdashboard.datasource.MediaRemoteDataSource
import ir.wordpressdashboard.datasource.PostRemoteDataSource
import ir.wordpressdashboard.datasource.ProductRemoteDataSource
import ir.wordpressdashboard.repository.MediaRepository
import ir.wordpressdashboard.repository.MediaRepositoryImpl
import ir.wordpressdashboard.repository.PostRepository
import ir.wordpressdashboard.repository.PostRepositoryImpl
import ir.wordpressdashboard.repository.ProductRepository
import ir.wordpressdashboard.repository.ProductRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    fun provideProductRepository(
        remoteDataSource: ProductRemoteDataSource
    ): ProductRepository = ProductRepositoryImpl(remoteDataSource)

    @Provides
    fun providePostRepository(
        remoteDataSource: PostRemoteDataSource
    ): PostRepository = PostRepositoryImpl(remoteDataSource)

    @Provides
    fun provideMediaRepository(
        remoteDataSource: MediaRemoteDataSource
    ): MediaRepository = MediaRepositoryImpl(remoteDataSource)
}