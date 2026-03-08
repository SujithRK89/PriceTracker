package com.srk.pricetracker.domain.usecase

import com.srk.pricetracker.domain.repository.PriceRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class StopFeedUseCaseTest {

    private val repository: PriceRepository = mockk()
    private val useCase = StopFeedUseCase(repository)

    @Test
    fun `invoke calls repository stopFeed`() = runTest {
        coEvery { repository.stopFeed() } returns Unit

        useCase()

        coVerify { repository.stopFeed() }
    }
}
