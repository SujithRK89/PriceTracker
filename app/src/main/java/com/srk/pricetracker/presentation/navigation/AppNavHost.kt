package com.srk.pricetracker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.srk.pricetracker.presentation.pricefeed.navigation.FeedDestinations
import com.srk.pricetracker.presentation.pricefeed.navigation.feedNavGraph

/**
 * The main navigation host for the application.
 */
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val navigator = remember { AppNavigator(navController) }
    CompositionLocalProvider(LocalNavigator provides navigator) {
        NavHost(
            navController = navController,
            startDestination = FeedDestinations.FeedGraph
        ) {
            feedNavGraph()
        }
    }
}
