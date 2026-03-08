package com.srk.pricetracker.presentation.pricefeed.navigation

import com.srk.pricetracker.core.navigation.Destination
import kotlinx.serialization.Serializable

@Serializable
sealed interface FeedDestinations: Destination {
    @Serializable
    data object FeedGraph: FeedDestinations

    @Serializable
    data object FeedScreen: FeedDestinations

    @Serializable
    data class FeedDetailsScreen(val id: String): FeedDestinations
}