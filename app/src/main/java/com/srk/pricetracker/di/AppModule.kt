package com.srk.pricetracker.di

import com.srk.pricetracker.core.network.StockWebSocketService
import com.srk.pricetracker.core.network.StockWebSocketServiceImpl
import com.srk.pricetracker.data.remote.PriceDataSource
import com.srk.pricetracker.data.remote.PriceDataSourceImpl
import com.srk.pricetracker.data.repository.PriceRepositoryImpl
import com.srk.pricetracker.domain.repository.PriceRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindStockWebSocketService(
        stockWebSocketServiceImpl: StockWebSocketServiceImpl
    ): StockWebSocketService

    @Binds
    @Singleton
    abstract fun bindPriceDataSource(
        priceDataSourceImpl: PriceDataSourceImpl
    ): PriceDataSource

    @Binds
    @Singleton
    abstract fun bindPriceRepository(
        priceRepositoryImpl: PriceRepositoryImpl
    ): PriceRepository

    companion object {
        @Provides
        @Singleton
        fun provideOkHttpClient(): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .build()
        }
    }
}
