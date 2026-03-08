package com.srk.pricetracker.domain.repository

import com.srk.pricetracker.core.network.NetworkResult
import com.srk.pricetracker.domain.model.StockDomainModel
import kotlinx.coroutines.flow.Flow

/**
 * Interface for managing stock price data.
 */
interface PriceRepository {
    /**
     * Flow of stock updates wrapped in [NetworkResult].
     */
    val stocks: Flow<NetworkResult<List<StockDomainModel>>>

    /**
     * Flow indicating the connection status of the WebSocket.
     */
    val isConnected: Flow<Boolean>

    /**
     * Flow indicating if the price feed is currently active.
     */
    val isRunning: Flow<Boolean>

    /**
     * Starts the WebSocket feed and price updates.
     */
    suspend fun startFeed()

    /**
     * Stops the WebSocket feed and price updates.
     */
    suspend fun stopFeed()
}
