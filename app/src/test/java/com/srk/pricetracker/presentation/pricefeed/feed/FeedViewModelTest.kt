package com.srk.pricetracker.presentation.pricefeed.feed

import com.srk.pricetracker.core.network.NetworkResult
import com.srk.pricetracker.domain.model.StockDomainModel
import com.srk.pricetracker.domain.usecase.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FeedViewModelTest {

    private val getStocksUseCase: GetStocksUseCase = mockk()
    private val startFeedUseCase: StartFeedUseCase = mockk()
    private val stopFeedUseCase: StopFeedUseCase = mockk()
    private val getConnectionStatusUseCase: GetConnectionStatusUseCase = mockk()
    private val getIsRunningUseCase: GetIsRunningUseCase = mockk()

    private lateinit var viewModel: FeedViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        every { getStocksUseCase() } returns flowOf(NetworkResult.Loading)
        every { getConnectionStatusUseCase() } returns flowOf(false)
        every { getIsRunningUseCase() } returns flowOf(false)
        
        viewModel = FeedViewModel(
            getStocksUseCase,
            startFeedUseCase,
            stopFeedUseCase,
            getConnectionStatusUseCase,
            getIsRunningUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `state reflects stocks and sorting correctly`() = runTest {
        val stocks = listOf(
            StockDomainModel("AAPL", 100.0),
            StockDomainModel("GOOG", 200.0)
        )
        every { getStocksUseCase() } returns flowOf(NetworkResult.Success(stocks))
        
        // Re-init to trigger new flow
        viewModel = FeedViewModel(
            getStocksUseCase,
            startFeedUseCase,
            stopFeedUseCase,
            getConnectionStatusUseCase,
            getIsRunningUseCase
        )
        
        val state = viewModel.state.value
        assertEquals(2, state.stocks.size)
        assertEquals("GOOG", state.stocks[0].symbol) // Sorted by price descending
        assertEquals("AAPL", state.stocks[1].symbol)
    }

    @Test
    fun `OnStart intent calls StartFeedUseCase`() = runTest {
        coEvery { startFeedUseCase() } returns Unit
        
        viewModel.onIntent(FeedContract.Intent.OnStart)
        
        coVerify { startFeedUseCase() }
    }

    @Test
    fun `OnStop intent calls StopFeedUseCase`() = runTest {
        coEvery { stopFeedUseCase() } returns Unit
        
        viewModel.onIntent(FeedContract.Intent.OnStop)
        
        coVerify { stopFeedUseCase() }
    }
}
