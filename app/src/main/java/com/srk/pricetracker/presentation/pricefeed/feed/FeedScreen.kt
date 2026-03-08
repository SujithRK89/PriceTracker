package com.srk.pricetracker.presentation.pricefeed.feed

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.srk.pricetracker.core.components.AppShell
import com.srk.pricetracker.presentation.pricefeed.feed.components.FeedTopBar
import com.srk.pricetracker.presentation.pricefeed.feed.components.StockItem
import com.srk.pricetracker.presentation.pricefeed.model.Stock
import com.srk.pricetracker.ui.theme.PriceTrackerTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun FeedScreen(
    viewModel: FeedViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToFeedDetails: (String) -> Unit = {}
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                FeedContract.Effect.NavigateBack -> onNavigateBack()
                is FeedContract.Effect.NavigateToFeedDetails -> onNavigateToFeedDetails(effect.id)
            }
        }
    }

    FeedContent(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@Composable
private fun FeedContent(
    uiState: FeedContract.UiState = FeedContract.UiState(),
    onIntent: (FeedContract.Intent) -> Unit = {}
) {
    AppShell(
        topContent = {
            FeedTopBar(
                connected = uiState.connected,
                running = uiState.running,
                onStartClick = { onIntent(FeedContract.Intent.OnStart) },
                onStopClick = { onIntent(FeedContract.Intent.OnStop) }
            )
        },
        isLoading = uiState.isLoading,
        screenContent = {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(
                    items = uiState.stocks,
                    key = { it.symbol }
                ) { stock ->
                    StockItem(
                        stock = stock,
                        onClick = { onIntent(FeedContract.Intent.OnFeedClicked(stock.symbol)) }
                    )
                }
            }
        }
    )
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun FeedContentPreview() {
    PriceTrackerTheme {
        FeedContent(
            uiState = FeedContract.UiState(
                stocks = listOf(
                    Stock("AAPL", 150.0, 145.0),
                    Stock("GOOG", 2800.0, 2810.0),
                    Stock("MSFT", 300.0, 295.0),
                    Stock("TSLA", 700.0, 710.0),
                    Stock("AMZN", 3300.0, 3280.0),
                    Stock("META", 500.0, 500.0)
                ).sortedByDescending { it.price }
            )
        )
    }
}
