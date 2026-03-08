package com.srk.pricetracker.domain.repository

import com.srk.pricetracker.core.network.NetworkResult
import com.srk.pricetracker.domain.model.StockDomainModel
import kotlinx.coroutines.flow.Flow

interface PriceRepository {
    val stocks: Flow<NetworkResult<List<StockDomainModel>>>
    val isConnected: Flow<Boolean>
    val isRunning: Flow<Boolean>

    suspend fun startFeed()
    suspend fun stopFeed()
    fun getStockDetails(symbol: String): Flow<NetworkResult<StockDomainModel?>>
}
