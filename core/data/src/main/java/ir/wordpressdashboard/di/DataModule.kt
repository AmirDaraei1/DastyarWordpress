package ir.wordpressdashboard.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.wordpressdashboard.datasource.MediaLocalDataSource
import ir.wordpressdashboard.datasource.MediaRemoteDataSource
import ir.wordpressdashboard.datasource.PostLocalDataSource
import ir.wordpressdashboard.datasource.PostRemoteDataSource
import ir.wordpressdashboard.datasource.ProductLocalDataSource
import ir.wordpressdashboard.datasource.ProductRemoteDataSource
import ir.wordpressdashboard.local.dao.MediaDao
import ir.wordpressdashboard.local.dao.PostDao
import ir.wordpressdashboard.local.dao.ProductDao
import ir.wordpressdashboard.local.db.AppDatabase
import ir.wordpressdashboard.repository.MediaRepository
import ir.wordpressdashboard.repository.MediaRepositoryImpl
import ir.wordpressdashboard.repository.PostRepository
import ir.wordpressdashboard.repository.PostRepositoryImpl
import ir.wordpressdashboard.repository.ProductRepository
import ir.wordpressdashboard.repository.ProductRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    // ── Database ──────────────────────────────────────────────────────────
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "wp_dashboard.db")
            .fallbackToDestructiveMigration()
            .build()

    // ── DAOs ──────────────────────────────────────────────────────────────
    @Provides
    fun provideProductDao(db: AppDatabase): ProductDao = db.productDao()

    @Provides
    fun providePostDao(db: AppDatabase): PostDao = db.postDao()

    @Provides
    fun provideMediaDao(db: AppDatabase): MediaDao = db.mediaDao()

    // ── Repositories ──────────────────────────────────────────────────────
    @Provides
    fun provideProductRepository(
        remoteDataSource: ProductRemoteDataSource,
        localDataSource: ProductLocalDataSource
    ): ProductRepository = ProductRepositoryImpl(remoteDataSource, localDataSource)

    @Provides
    fun providePostRepository(
        remoteDataSource: PostRemoteDataSource,
        localDataSource: PostLocalDataSource
    ): PostRepository = PostRepositoryImpl(remoteDataSource, localDataSource)

    @Provides
    fun provideMediaRepository(
        remoteDataSource: MediaRemoteDataSource,
        localDataSource: MediaLocalDataSource
    ): MediaRepository = MediaRepositoryImpl(remoteDataSource, localDataSource)
}