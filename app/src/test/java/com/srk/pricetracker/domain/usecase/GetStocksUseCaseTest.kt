package com.srk.pricetracker.domain.usecase

import com.srk.pricetracker.core.network.NetworkResult
import com.srk.pricetracker.domain.model.StockDomainModel
import com.srk.pricetracker.domain.repository.PriceRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetStocksUseCaseTest {

    private val repository: PriceRepository = mockk()
    private val useCase = GetStocksUseCase(repository)

    @Test
    fun `invoke calls repository stocks`() = runBlocking {
        val stocks = listOf(StockDomainModel("AAPL", 150.0))
        val expectedResult = NetworkResult.Success(stocks)
        every { repository.stocks } returns flowOf(expectedResult)

        val result = useCase().first()

        assertEquals(expectedResult, result)
        verify { repository.stocks }
    }
}
