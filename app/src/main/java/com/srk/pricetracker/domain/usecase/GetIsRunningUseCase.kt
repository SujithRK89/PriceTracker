package com.srk.pricetracker.domain.usecase

import com.srk.pricetracker.domain.repository.PriceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetIsRunningUseCase @Inject constructor(
    private val repository: PriceRepository
) {
    operator fun invoke(): Flow<Boolean> = repository.isRunning
}
