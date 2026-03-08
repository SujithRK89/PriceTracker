package com.srk.pricetracker.data.repository

import com.srk.pricetracker.BuildConfig
import com.srk.pricetracker.core.network.NetworkResult
import com.srk.pricetracker.data.mapper.toDomain
import com.srk.pricetracker.data.model.StockDto
import com.srk.pricetracker.data.remote.PriceDataSource
import com.srk.pricetracker.data.remote.PriceEvent
import com.srk.pricetracker.domain.model.StockDomainModel
import com.srk.pricetracker.domain.repository.PriceRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * Implementation of [PriceRepository] that manages real-time stock price updates.
 * It uses [PriceDataSource] to connect to a WebSocket and handles the business logic
 * for generating random price updates, sending them to be echoed, and batch processing
 * the received updates to maintain smooth UI performance.
 *
 * @param dataSource The data source used for WebSocket communication.
 */
@Singleton
class PriceRepositoryImpl @Inject constructor(
    private val dataSource: PriceDataSource
) : PriceRepository {

    private val symbols = listOf(
        "AAPL", "GOOG", "TSLA", "AMZN", "MSFT", "NVDA", "META", "NFLX", "ADBE", "PYPL",
        "INTC", "CMCSA", "PEP", "CSCO", "AVGO", "COST", "TMUS", "TXN", "AMGN", "SBUX",
        "AMD", "QCOM", "ISRG", "GILD", "AMAT"
    )

    private val _stocks = MutableStateFlow(symbols.map { 
        StockDto(
            symbol = it, 
            price = INITIAL_PRICE_BASE + Random.nextDouble(0.0, INITIAL_PRICE_RANGE),
            description = "Description for $it: $DEFAULT_DESCRIPTION_SUFFIX"
        ) 
    })
    
    private val _error = MutableStateFlow<String?>(null)

    override val stocks: Flow<NetworkResult<List<StockDomainModel>>> = 
        combine(_stocks, _error) { stocks, error ->
            if (error != null) {
                NetworkResult.Error(error)
            } else {
                NetworkResult.Success(stocks.map { it.toDomain() })
            }
        }.onStart { emit(NetworkResult.Loading) }

    private val _isConnected = MutableStateFlow(false)
    override val isConnected: Flow<Boolean> = _isConnected.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    override val isRunning: Flow<Boolean> = _isRunning.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var updateJob: Job? = null
    private var connectionJob: Job? = null
    private var processingJob: Job? = null
    
    private val messageBuffer = Channel<Pair<String, Double>>(Channel.UNLIMITED)

    override suspend fun startFeed() {
        if (_isRunning.value) return
        _isRunning.value = true
        _error.value = null
        
        connectionJob = scope.launch {
            dataSource.connect(BuildConfig.WS_URL).collect { event ->
                when (event) {
                    is PriceEvent.Connected -> {
                        _isConnected.value = true
                        _error.value = null
                    }
                    is PriceEvent.Disconnected -> {
                        _isConnected.value = false
                    }
                    is PriceEvent.Error -> {
                        _isConnected.value = false
                        _error.value = "Connection Error: ${event.throwable.localizedMessage ?: "Unknown error"}"
                    }
                    is PriceEvent.MessageReceived -> {
                        val parts = event.text.split(MESSAGE_SEPARATOR)
                        if (parts.size == 2) {
                            val symbol = parts[0]
                            val price = parts[1].toDoubleOrNull()
                            if (price != null) {
                                messageBuffer.send(symbol to price)
                            }
                        }
                    }
                }
            }
        }

        // Batch process price updates
        processingJob = scope.launch {
            while (isActive) {
                val updates = mutableMapOf<String, Double>()
                var update = messageBuffer.tryReceive().getOrNull()
                while (update != null) {
                    updates[update.first] = update.second
                    update = messageBuffer.tryReceive().getOrNull()
                }

                if (updates.isNotEmpty()) {
                    _stocks.update { currentStocks ->
                        currentStocks.map { stock ->
                            updates[stock.symbol]?.let { newPrice ->
                                stock.copy(previousPrice = stock.price, price = newPrice)
                            } ?: stock
                        }
                    }
                }
                delay(UI_UPDATE_DELAY_MS)
            }
        }

        updateJob = scope.launch {
            while (isActive) {
                if (_isConnected.value) {
                    _stocks.value.forEach { stock ->
                        val change = Random.nextDouble(-PRICE_CHANGE_RANGE, PRICE_CHANGE_RANGE)
                        val newPrice = (stock.price + change).coerceAtLeast(MIN_PRICE)
                        dataSource.sendMessage("${stock.symbol}$MESSAGE_SEPARATOR${String.format(PRICE_FORMAT, newPrice)}")
                    }
                }
                delay(FEED_UPDATE_DELAY_MS)
            }
        }
    }

    override suspend fun stopFeed() {
        _isRunning.value = false
        updateJob?.cancel()
        connectionJob?.cancel()
        processingJob?.cancel()
        dataSource.disconnect()
        _isConnected.value = false
        _error.value = null
        
        while(messageBuffer.tryReceive().isSuccess) {}
    }

    companion object {
        private const val INITIAL_PRICE_BASE = 100.0
        private const val INITIAL_PRICE_RANGE = 500.0
        private const val MIN_PRICE = 1.0
        private const val PRICE_CHANGE_RANGE = 2.0
        private const val UI_UPDATE_DELAY_MS = 500L
        private const val FEED_UPDATE_DELAY_MS = 2000L
        private const val MESSAGE_SEPARATOR = ":"
        private const val PRICE_FORMAT = "%.2f"
        private const val DEFAULT_DESCRIPTION_SUFFIX = "This is a leading company in its sector, known for innovation and market leadership."
    }
}
