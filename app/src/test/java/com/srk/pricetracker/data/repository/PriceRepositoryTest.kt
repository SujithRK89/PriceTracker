package com.srk.pricetracker.data.repository

import com.srk.pricetracker.core.network.NetworkResult
import com.srk.pricetracker.data.remote.PriceDataSource
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PriceRepositoryTest {

    private val dataSource: PriceDataSource = mockk()
    private val repository = PriceRepositoryImpl(dataSource)

    @Test
    fun `stocks flow returns list of stocks on start`() = runTest {
        val result = repository.stocks.first()

        // Since it's a StateFlow with initial values, it should be a Success
        assertTrue(result is NetworkResult.Success)
        val stocks = (result as NetworkResult.Success).data
        assertEquals(25, stocks.size)
        assertTrue(stocks.any { it.symbol == "AAPL" })
    }

    @Test
    fun `stopFeed calls dataSource disconnect`() = runTest {
        every { dataSource.disconnect() } returns Unit

        repository.stopFeed()

        verify { dataSource.disconnect() }
    }
}
