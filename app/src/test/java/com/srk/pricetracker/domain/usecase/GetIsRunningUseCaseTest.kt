package com.srk.pricetracker.domain.usecase

import com.srk.pricetracker.domain.repository.PriceRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetIsRunningUseCaseTest {

    private val repository: PriceRepository = mockk()
    private val useCase = GetIsRunningUseCase(repository)

    @Test
    fun `invoke returns repository isRunning flow`() = runTest {
        val expected = true
        every { repository.isRunning } returns flowOf(expected)

        val result = useCase().first()

        assertEquals(expected, result)
    }
}
