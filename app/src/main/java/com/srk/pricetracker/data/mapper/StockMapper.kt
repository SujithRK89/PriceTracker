package com.srk.pricetracker.data.mapper

import com.srk.pricetracker.data.model.StockDto
import com.srk.pricetracker.domain.model.StockDomainModel

fun StockDto.toDomain(): StockDomainModel {
    return StockDomainModel(
        symbol = symbol,
        price = price,
        previousPrice = previousPrice,
        description = description
    )
}

fun StockDomainModel.toDto(): StockDto {
    return StockDto(
        symbol = symbol,
        price = price,
        previousPrice = previousPrice,
        description = description
    )
}
