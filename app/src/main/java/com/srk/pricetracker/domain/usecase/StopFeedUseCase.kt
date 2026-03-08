package com.srk.pricetracker.domain.usecase

import com.srk.pricetracker.domain.repository.PriceRepository
import javax.inject.Inject

class StopFeedUseCase @Inject constructor(
    private val repository: PriceRepository
) {
    suspend operator fun invoke() = repository.stopFeed()
}
