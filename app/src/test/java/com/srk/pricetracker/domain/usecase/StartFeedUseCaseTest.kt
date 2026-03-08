package com.srk.pricetracker.domain.usecase

import com.srk.pricetracker.domain.repository.PriceRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class StartFeedUseCaseTest {

    private val repository: PriceRepository = mockk()
    private val useCase = StartFeedUseCase(repository)

    @Test
    fun `invoke calls repository startFeed`() = runTest {
        coEvery { repository.startFeed() } returns Unit

        useCase()

        coVerify { repository.startFeed() }
    }
}
