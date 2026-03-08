package com.srk.pricetracker.data.remote

import kotlinx.coroutines.flow.Flow

interface PriceDataSource {
    fun connect(url: String): Flow<PriceEvent>
    fun sendMessage(message: String): Boolean
    fun disconnect()
}

sealed interface PriceEvent {
    data object Connected : PriceEvent
    data object Disconnected : PriceEvent
    data class MessageReceived(val text: String) : PriceEvent
    data class Error(val throwable: Throwable) : PriceEvent
}
