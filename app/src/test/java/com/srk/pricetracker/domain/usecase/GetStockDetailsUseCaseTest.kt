package com.srk.pricetracker.domain.usecase

import com.srk.pricetracker.core.network.NetworkResult
import com.srk.pricetracker.domain.model.StockDomainModel
import com.srk.pricetracker.domain.repository.PriceRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetStockDetailsUseCaseTest {

    private val repository: PriceRepository = mockk()
    private val useCase = GetStockDetailsUseCase(repository)

    @Test
    fun `invoke filters stocks correctly for a specific symbol`() = runTest {
        val symbol = "AAPL"
        val stocks = listOf(
            StockDomainModel(symbol = "AAPL", price = 150.0),
            StockDomainModel(symbol = "TSLA", price = 700.0)
        )
        every { repository.stocks } returns flowOf(NetworkResult.Success(stocks))

        val result = useCase(symbol).first()

        assertTrue(result is NetworkResult.Success)
        assertEquals(symbol, (result as NetworkResult.Success).data?.symbol)
    }

    @Test
    fun `invoke returns null when symbol is not found`() = runTest {
        val symbol = "GOOG"
        val stocks = listOf(StockDomainModel(symbol = "AAPL", price = 150.0))
        every { repository.stocks } returns flowOf(NetworkResult.Success(stocks))

        val result = useCase(symbol).first()

        assertTrue(result is NetworkResult.Success)
        assertEquals(null, (result as NetworkResult.Success).data)
    }

    @Test
    fun `invoke propagates error state`() = runTest {
        val errorMessage = "Network Error"
        every { repository.stocks } returns flowOf(NetworkResult.Error(errorMessage))

        val result = useCase("AAPL").first()

        assertTrue(result is NetworkResult.Error)
        assertEquals(errorMessage, (result as NetworkResult.Error).message)
    }

    @Test
    fun `invoke propagates loading state`() = runTest {
        every { repository.stocks } returns flowOf(NetworkResult.Loading)

        val result = useCase("AAPL").first()

        assertTrue(result is NetworkResult.Loading)
    }
}
