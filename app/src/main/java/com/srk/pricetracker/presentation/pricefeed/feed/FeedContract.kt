package com.srk.pricetracker.presentation.pricefeed.feed

import androidx.compose.runtime.Immutable
import com.srk.pricetracker.presentation.pricefeed.model.StockUiModel

object FeedContract {

    @Immutable
    data class UiState(
        val isLoading: Boolean = false,
        val connected: Boolean = true,
        val running: Boolean = false,
        val stocks: List<StockUiModel> = emptyList(),
        val error: String? = null
    )

    sealed interface Intent {
        data object OnStart: Intent
        data object OnStop: Intent
        data object OnBackClicked: Intent
        data class OnFeedClicked(val id: String): Intent
        data object DismissError : Intent
    }

    sealed interface Effect {
        data object NavigateBack: Effect
        data class NavigateToFeedDetails(val id: String): Effect
    }
}
