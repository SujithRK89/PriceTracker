package com.srk.pricetracker.presentation.pricefeed.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.srk.pricetracker.presentation.navigation.LocalNavigator
import com.srk.pricetracker.presentation.pricefeed.details.FeedDetailsScreen
import com.srk.pricetracker.presentation.pricefeed.feed.FeedScreen

/**
 * Extension function to define the navigation graph for the price feed.
 */
fun NavGraphBuilder.feedNavGraph() {
    navigation<FeedDestinations.FeedGraph>(
        startDestination = FeedDestinations.FeedScreen
    ) {
        composable<FeedDestinations.FeedScreen> {
            val navigator = LocalNavigator.current
            FeedScreen(
                onNavigateBack = navigator::navigateUp,
                onNavigateToFeedDetails = { id ->
                    navigator.navigate(FeedDestinations.FeedDetailsScreen(id = id))
                }
            )
        }
        composable<FeedDestinations.FeedDetailsScreen> {
            val navigator = LocalNavigator.current
            FeedDetailsScreen()
        }
    }
}
