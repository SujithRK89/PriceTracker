package com.srk.pricetracker.domain.usecase

import com.srk.pricetracker.domain.repository.PriceRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetConnectionStatusUseCaseTest {

    private val repository: PriceRepository = mockk()
    private val useCase = GetConnectionStatusUseCase(repository)

    @Test
    fun `invoke returns repository isConnected flow`() = runTest {
        val expected = true
        every { repository.isConnected } returns flowOf(expected)

        val result = useCase().first()

        assertEquals(expected, result)
    }
}
