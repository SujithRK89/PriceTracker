package com.srk.pricetracker.presentation.pricefeed.details

import androidx.compose.runtime.Immutable
import com.srk.pricetracker.presentation.pricefeed.model.StockUiModel

object FeedDetailsContract {

    @Immutable
    data class UiState(
        val isLoading: Boolean = false,
        val stock: StockUiModel? = null,
        val description: String = "",
        val error: String? = null
    )

    sealed interface Intent {
        data object OnBackClicked: Intent
        data object DismissError: Intent
    }

    sealed interface Effect {
        data object NavigateBack: Effect
    }
}
