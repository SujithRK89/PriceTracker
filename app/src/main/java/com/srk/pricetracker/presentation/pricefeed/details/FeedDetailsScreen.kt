package com.srk.pricetracker.presentation.pricefeed.details

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.srk.pricetracker.ui.theme.PriceTrackerTheme

@Composable
fun FeedDetailsScreen() {
    FeedDetailsContent()
}

@Composable
private fun FeedDetailsContent() {

}

@Preview
@Composable
private fun FeedDetailsContentPreview() {
    PriceTrackerTheme {
        FeedDetailsContent()
    }
}