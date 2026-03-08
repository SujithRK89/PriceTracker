package com.srk.pricetracker.presentation.pricefeed.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.srk.pricetracker.core.network.NetworkResult
import com.srk.pricetracker.domain.usecase.GetConnectionStatusUseCase
import com.srk.pricetracker.domain.usecase.GetIsRunningUseCase
import com.srk.pricetracker.domain.usecase.GetStockDetailsUseCase
import com.srk.pricetracker.domain.usecase.StartFeedUseCase
import com.srk.pricetracker.presentation.pricefeed.model.toUi
import com.srk.pricetracker.presentation.pricefeed.navigation.FeedDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getStockDetailsUseCase: GetStockDetailsUseCase,
    private val startFeedUseCase: StartFeedUseCase,
    private val getIsRunningUseCase: GetIsRunningUseCase,
    private val getConnectionStatusUseCase: GetConnectionStatusUseCase
) : ViewModel() {
    private val route = savedStateHandle.toRoute<FeedDestinations.FeedDetailsScreen>()
    
    private val _effect = MutableSharedFlow<FeedDetailsContract.Effect>()
    val effect = _effect.asSharedFlow()

    private val _uiState = MutableStateFlow(FeedDetailsContract.UiState())
    val state: StateFlow<FeedDetailsContract.UiState> = combine(
        getStockDetailsUseCase(route.id),
        getConnectionStatusUseCase(),
        _uiState
    ) { result, isConnected, currentState ->
        when (result) {
            is NetworkResult.Success -> {
                val stockUi = result.data?.toUi()
                currentState.copy(
                    stock = stockUi,
                    description = stockUi?.description ?: "No description available.",
                    isLoading = false,
                    error = null,
                    isConnected = isConnected
                )
            }
            is NetworkResult.Error -> {
                currentState.copy(
                    isLoading = false,
                    error = result.message,
                    isConnected = isConnected
                )
            }
            is NetworkResult.Loading -> {
                currentState.copy(
                    isLoading = true,
                    isConnected = isConnected
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FeedDetailsContract.UiState(isLoading = true)
    )

    init {
        checkAndStartFeed()
    }

    private fun checkAndStartFeed() {
        viewModelScope.launch {
            val isRunning = getIsRunningUseCase().first()
            if (!isRunning) {
                startFeedUseCase()
            }
        }
    }

    fun onIntent(intent: FeedDetailsContract.Intent) {
        when (intent) {
            FeedDetailsContract.Intent.OnBackClicked -> {
                viewModelScope.launch {
                    _effect.emit(FeedDetailsContract.Effect.NavigateBack)
                }
            }
            FeedDetailsContract.Intent.DismissError -> {
                _uiState.update { it.copy(error = null) }
            }
        }
    }
}
