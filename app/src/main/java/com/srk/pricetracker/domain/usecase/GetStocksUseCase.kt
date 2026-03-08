package com.srk.pricetracker.domain.usecase

import com.srk.pricetracker.core.network.NetworkResult
import com.srk.pricetracker.domain.model.StockDomainModel
import com.srk.pricetracker.domain.repository.PriceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStocksUseCase @Inject constructor(
    private val repository: PriceRepository
) {
    operator fun invoke(): Flow<NetworkResult<List<StockDomainModel>>> = repository.stocks
}
