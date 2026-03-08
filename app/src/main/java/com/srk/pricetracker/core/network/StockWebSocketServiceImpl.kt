package com.srk.pricetracker.core.network

import com.srk.pricetracker.data.remote.PriceEvent
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [StockWebSocketService] using OkHttp's [WebSocket].
 * This service manages the connection to a WebSocket server and exposes
 * the connection events and incoming messages as a [Flow] of [PriceEvent].
 *
 * @param client The [OkHttpClient] used to create the WebSocket connection.
 */
@Singleton
class StockWebSocketServiceImpl @Inject constructor(
    private val client: OkHttpClient
) : StockWebSocketService {
    private var webSocket: WebSocket? = null

    override fun openConnection(url: String): Flow<PriceEvent> = callbackFlow {
        val request = Request.Builder().url(url).build()
        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                trySend(PriceEvent.Connected)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                trySend(PriceEvent.MessageReceived(text))
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                trySend(PriceEvent.Disconnected)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                trySend(PriceEvent.Error(t))
            }
        }
        webSocket = client.newWebSocket(request, listener)
        awaitClose {
            webSocket?.close(NORMAL_CLOSURE_STATUS, REASON_FLOW_CLOSED)
        }
    }

    override fun send(message: String): Boolean {
        return webSocket?.send(message) ?: false
    }

    override fun closeConnection() {
        webSocket?.close(NORMAL_CLOSURE_STATUS, REASON_CLOSED_BY_SERVICE)
        webSocket = null
    }

    companion object {
        private const val NORMAL_CLOSURE_STATUS = 1000
        private const val REASON_FLOW_CLOSED = "Flow closed"
        private const val REASON_CLOSED_BY_SERVICE = "Closed by service"
    }
}
