package com.srk.pricetracker.presentation.pricefeed.details

import androidx.lifecycle.SavedStateHandle
import com.srk.pricetracker.core.network.NetworkResult
import com.srk.pricetracker.domain.model.StockDomainModel
import com.srk.pricetracker.domain.usecase.GetStockDetailsUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FeedDetailsViewModelTest {

    private val getStockDetailsUseCase: GetStockDetailsUseCase = mockk()
    private lateinit var viewModel: FeedDetailsViewModel
    private val testDispatcher = UnconfinedTestDispatcher()
    
    private val symbol = "AAPL"
    // SavedStateHandle needs the "id" key to match FeedDetailsScreen(val id: String) properties
    private val savedStateHandle = SavedStateHandle(mapOf("id" to symbol))

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { getStockDetailsUseCase(symbol) } returns flowOf(NetworkResult.Loading)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        viewModel = FeedDetailsViewModel(savedStateHandle, getStockDetailsUseCase)
        
        assertEquals(true, viewModel.state.value.isLoading)
    }

    @Test
    fun `state reflects stock details on success`() = runTest {
        val stock = StockDomainModel(symbol = symbol, price = 150.0, description = "Apple Inc.")
        every { getStockDetailsUseCase(symbol) } returns flowOf(NetworkResult.Success(stock))
        
        viewModel = FeedDetailsViewModel(savedStateHandle, getStockDetailsUseCase)
        
        val state = viewModel.state.value
        assertEquals(false, state.isLoading)
        assertEquals(symbol, state.stock?.symbol)
        assertEquals(150.0, state.stock?.price ?: 0.0, 0.0)
        assertEquals("Apple Inc.", state.description)
        assertNull(state.error)
    }

    @Test
    fun `state reflects error message on failure`() = runTest {
        val errorMessage = "Network Error"
        every { getStockDetailsUseCase(symbol) } returns flowOf(NetworkResult.Error(errorMessage))
        
        viewModel = FeedDetailsViewModel(savedStateHandle, getStockDetailsUseCase)
        
        val state = viewModel.state.value
        assertEquals(false, state.isLoading)
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `DismissError intent clears error in state`() = runTest {
        every { getStockDetailsUseCase(symbol) } returns flowOf(NetworkResult.Error("Error"))
        
        viewModel = FeedDetailsViewModel(savedStateHandle, getStockDetailsUseCase)
        
        // Ensure error is set
        assertEquals("Error", viewModel.state.value.error)
        
        viewModel.onIntent(FeedDetailsContract.Intent.DismissError)
        
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `OnBackClicked intent emits NavigateBack effect`() = runTest {
        viewModel = FeedDetailsViewModel(savedStateHandle, getStockDetailsUseCase)
        
        val effects = mutableListOf<FeedDetailsContract.Effect>()
        val job = backgroundScope.launch {
            viewModel.effect.collect { effects.add(it) }
        }
        
        viewModel.onIntent(FeedDetailsContract.Intent.OnBackClicked)
        
        assertEquals(1, effects.size)
        assertEquals(FeedDetailsContract.Effect.NavigateBack, effects[0])
        
        job.cancel()
    }
}
