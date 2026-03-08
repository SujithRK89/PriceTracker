package com.srk.pricetracker.domain.model

data class StockDomainModel(
    val symbol: String,
    val price: Double,
    val previousPrice: Double = 0.0,
    val description: String = ""
)
