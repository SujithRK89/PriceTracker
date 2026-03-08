package com.srk.pricetracker.presentation.pricefeed.details

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.srk.pricetracker.core.components.AppShell
import com.srk.pricetracker.presentation.pricefeed.model.StockUiModel
import com.srk.pricetracker.ui.theme.PriceTrackerTheme
import kotlinx.coroutines.flow.collectLatest

/**
 * Screen that displays details for a specific stock symbol.
 */
@Composable
fun FeedDetailsScreen(
    viewModel: FeedDetailsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                FeedDetailsContract.Effect.NavigateBack -> onNavigateBack()
            }
        }
    }

    FeedDetailsContent(
        uiState = uiState,
        onIntent = viewModel::onIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeedDetailsContent(
    uiState: FeedDetailsContract.UiState = FeedDetailsContract.UiState(),
    onIntent: (FeedDetailsContract.Intent) -> Unit = {}
) {
    AppShell(
        topContent = {
            CenterAlignedTopAppBar(
                title = { Text(uiState.stock?.symbol ?: "Details") },
                navigationIcon = {
                    IconButton(onClick = { onIntent(FeedDetailsContract.Intent.OnBackClicked) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        isLoading = uiState.isLoading,
        errorMessage = uiState.error,
        onErrorDismiss = { onIntent(FeedDetailsContract.Intent.DismissError) },
        screenContent = {
            uiState.stock?.let { stock ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    // Price Section in a Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = String.format("$%.2f", stock.price),
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontSize = 48.sp,
                                    letterSpacing = (-1).sp
                                ),
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Hide indicator if price hasn't changed from initial
                            Box(modifier = Modifier.alpha(if (stock.hasChanged) 1f else 0f)) {
                                PriceChangeIndicator(isUp = stock.isUp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Description Section
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "About ${stock.symbol}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = uiState.description,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 24.sp,
                            textAlign = TextAlign.Justify,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun PriceChangeIndicator(isUp: Boolean) {
    val (text, color) = if (isUp) {
        "↑ Price is up" to Color.Green
    } else {
        "↓ Price is down" to Color.Red
    }
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = CircleShape
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                color = color,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun FeedDetailsContentPreview() {
    PriceTrackerTheme {
        FeedDetailsContent(
            uiState = FeedDetailsContract.UiState(
                stock = StockUiModel("AAPL", 150.0, 145.0),
                description = "Apple Inc. designs, manufactures, and markets smartphones, personal computers, tablets, wearables, and accessories worldwide. The company offers iPhone, a line of smartphones; Mac, a line of personal computers; iPad, a line of multi-purpose tablets; and wearables, home, and accessories comprising AirPods, Apple TV, Apple Watch, Beats products, and HomePod."
            )
        )
    }
}
