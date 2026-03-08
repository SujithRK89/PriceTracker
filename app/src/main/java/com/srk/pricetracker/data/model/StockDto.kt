package com.srk.pricetracker.data.model

import kotlinx.serialization.Serializable

@Serializable
data class StockDto(
    val symbol: String,
    val price: Double,
    val previousPrice: Double = 0.0,
    val description: String = ""
)
