package ir.wordpressdashboard.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.wordpressdashboard.datasource.ProductRemoteDataSource
import ir.wordpressdashboard.repository.ProductRepository
import ir.wordpressdashboard.repository.ProductRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    fun provideProductRepository(
        remoteDataSource: ProductRemoteDataSource
    ):ProductRepository = ProductRepositoryImpl(remoteDataSource)
}