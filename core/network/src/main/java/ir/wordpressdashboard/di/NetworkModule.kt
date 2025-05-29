package ir.wordpressdashboard.di

import ir.wordpressdashboard.api.ProductApiService
import javax.naming.Context

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideProductApiService(
        @ApplicationContext context: Context
    ):ProductApiService = MockProductApiService(context)

    //todo it is for real api request that should complete it in future
//    @Provides
//    @Singleton
//    fun provideProductApiService(
//        retrofit: Retrofit
//    ):ProductApiService = retrofit.create(ProductApiService::class.java)
}