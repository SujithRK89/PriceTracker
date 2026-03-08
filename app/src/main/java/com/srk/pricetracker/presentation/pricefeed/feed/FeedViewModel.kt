package com.srk.pricetracker.presentation.pricefeed.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.srk.pricetracker.presentation.pricefeed.model.Stock
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(FeedContract.UiState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<FeedContract.Effect>()
    val effect = _effect.asSharedFlow()

    fun onIntent(intent: FeedContract.Intent) {
        when (intent) {
            FeedContract.Intent.OnBackClicked -> {
                viewModelScope.launch {
                    _effect.emit(FeedContract.Effect.NavigateBack)
                }
            }
            is FeedContract.Intent.OnFeedClicked -> {
                viewModelScope.launch {
                    _effect.emit(FeedContract.Effect.NavigateToFeedDetails(intent.id))
                }
            }
            FeedContract.Intent.OnStart -> {
                _state.update { it.copy(running = true) }
                // TODO: Start price feed updates
            }
            FeedContract.Intent.OnStop -> {
                _state.update { it.copy(running = false) }
                // TODO: Stop price feed updates
            }
        }
    }

    fun updateStocks(stocks: List<Stock>) {
        _state.update { currentState ->
            currentState.copy(
                stocks = stocks.sortedByDescending { it.price }
            )
        }
    }
}
