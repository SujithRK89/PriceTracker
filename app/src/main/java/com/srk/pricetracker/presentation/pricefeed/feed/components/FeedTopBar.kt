package com.srk.pricetracker.presentation.pricefeed.feed.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.srk.pricetracker.ui.theme.PriceTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedTopBar(
    connected: Boolean,
    running: Boolean,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ConnectionIndicator(connected = connected)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Stock Feed")
            }
        },
        actions = {
            Button(
                onClick = { if (running) onStopClick() else onStartClick() },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(if (running) "Stop" else "Start")
            }
        }
    )
}

@Composable
private fun ConnectionIndicator(connected: Boolean) {
    val color = if (connected) Color.Green else Color.Red
    Box(
        modifier = Modifier
            .size(12.dp)
            .background(color, CircleShape)
    )
}

@Preview
@Composable
private fun FeedTopBarConnectedRunningPreview() {
    PriceTrackerTheme {
        Surface {
            FeedTopBar(
                connected = true,
                running = true,
                onStartClick = {},
                onStopClick = {}
            )
        }
    }
}

@Preview
@Composable
private fun FeedTopBarDisconnectedStoppedPreview() {
    PriceTrackerTheme {
        Surface {
            FeedTopBar(
                connected = false,
                running = false,
                onStartClick = {},
                onStopClick = {}
            )
        }
    }
}
