package com.srk.pricetracker.domain.usecase

import com.srk.pricetracker.core.network.NetworkResult
import com.srk.pricetracker.domain.model.StockDomainModel
import com.srk.pricetracker.domain.repository.PriceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetStockDetailsUseCase @Inject constructor(
    private val repository: PriceRepository
) {
    operator fun invoke(symbol: String): Flow<NetworkResult<StockDomainModel?>> {
        return repository.stocks.map { result ->
            when (result) {
                is NetworkResult.Success -> NetworkResult.Success(result.data.find { it.symbol == symbol })
                is NetworkResult.Error -> NetworkResult.Error(result.message, result.throwable)
                is NetworkResult.Loading -> NetworkResult.Loading
            }
        }
    }
}
