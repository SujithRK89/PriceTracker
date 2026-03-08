package com.srk.pricetracker.data.remote

import com.srk.pricetracker.core.network.StockWebSocketService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PriceDataSourceImpl @Inject constructor(
    private val webSocketService: StockWebSocketService
) : PriceDataSource {

    override fun connect(url: String): Flow<PriceEvent> {
        return webSocketService.openConnection(url)
    }

    override fun sendMessage(message: String): Boolean {
        return webSocketService.send(message)
    }

    override fun disconnect() {
        webSocketService.closeConnection()
    }
}
