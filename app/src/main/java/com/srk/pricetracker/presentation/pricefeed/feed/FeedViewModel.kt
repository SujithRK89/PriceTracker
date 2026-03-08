package com.srk.pricetracker.presentation.pricefeed.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.srk.pricetracker.core.network.NetworkResult
import com.srk.pricetracker.domain.usecase.*
import com.srk.pricetracker.presentation.pricefeed.model.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getStocksUseCase: GetStocksUseCase,
    private val startFeedUseCase: StartFeedUseCase,
    private val stopFeedUseCase: StopFeedUseCase,
    private val getConnectionStatusUseCase: GetConnectionStatusUseCase,
    private val getIsRunningUseCase: GetIsRunningUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FeedContract.UiState())
    
    val state: StateFlow<FeedContract.UiState> = combine(
        getStocksUseCase(),
        getConnectionStatusUseCase(),
        getIsRunningUseCase(),
        _state
    ) { stocksResult, connected, running, state ->
        when (stocksResult) {
            is NetworkResult.Success -> {
                state.copy(
                    stocks = stocksResult.data.map { it.toUi() }.sortedByDescending { it.price },
                    connected = connected,
                    running = running,
                    isLoading = false,
                    error = null
                )
            }
            is NetworkResult.Error -> {
                state.copy(
                    error = stocksResult.message,
                    isLoading = false,
                    connected = connected,
                    running = running
                )
            }
            is NetworkResult.Loading -> {
                state.copy(
                    isLoading = true,
                    connected = connected,
                    running = running
                )
            }
        }
    }
    .flowOn(Dispatchers.Default)
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FeedContract.UiState(isLoading = true)
    )

    private val _effect = MutableSharedFlow<FeedContract.Effect>()
    val effect = _effect.asSharedFlow()

    fun onIntent(intent: FeedContract.Intent) {
        viewModelScope.launch {
            when (intent) {
                FeedContract.Intent.OnBackClicked -> {
                    _effect.emit(FeedContract.Effect.NavigateBack)
                }
                is FeedContract.Intent.OnFeedClicked -> {
                    _effect.emit(FeedContract.Effect.NavigateToFeedDetails(intent.id))
                }
                FeedContract.Intent.OnStart -> {
                    startFeedUseCase()
                }
                FeedContract.Intent.OnStop -> {
                    stopFeedUseCase()
                }
                FeedContract.Intent.DismissError -> {
                    _state.update { it.copy(error = null) }
                }
            }
        }
    }
}
