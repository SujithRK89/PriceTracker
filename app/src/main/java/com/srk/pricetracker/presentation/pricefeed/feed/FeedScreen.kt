package com.srk.pricetracker.presentation.pricefeed.feed

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.srk.pricetracker.ui.theme.PriceTrackerTheme

@Composable
fun FeedScreen() {
    FeedContent()
}

@Composable
private fun FeedContent() {

}

@Preview
@Composable
private fun FeedContentPreview() {
    PriceTrackerTheme {
        FeedContent()
    }
}

