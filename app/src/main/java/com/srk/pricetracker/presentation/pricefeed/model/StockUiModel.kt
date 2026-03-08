package com.srk.pricetracker.presentation.pricefeed.model

import androidx.compose.runtime.Immutable
import com.srk.pricetracker.domain.model.StockDomainModel
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class StockUiModel(
    val symbol: String,
    val price: Double,
    val previousPrice: Double = 0.0,
    val description: String = ""
) {
    val isUp: Boolean
        get() = price > previousPrice

    val hasChanged: Boolean
        get() = price != previousPrice && previousPrice != 0.0
}

fun StockDomainModel.toUi(): StockUiModel {
    return StockUiModel(
        symbol = symbol,
        price = price,
        previousPrice = previousPrice,
        description = description
    )
}
