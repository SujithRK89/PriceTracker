package com.srk.pricetracker.core.network

import com.srk.pricetracker.data.remote.PriceEvent
import kotlinx.coroutines.flow.Flow

interface StockWebSocketService {
    fun openConnection(url: String): Flow<PriceEvent>
    fun send(message: String): Boolean
    fun closeConnection()
}
