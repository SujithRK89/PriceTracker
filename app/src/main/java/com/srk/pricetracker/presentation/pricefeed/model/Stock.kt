package com.srk.pricetracker.presentation.pricefeed.model

import androidx.compose.runtime.Immutable

@Immutable
data class Stock(
    val symbol: String,
    val price: Double,
    val previousPrice: Double
) {
    val isUp: Boolean
        get() = price > previousPrice

    val change: Double
        get() = price - previousPrice
}
