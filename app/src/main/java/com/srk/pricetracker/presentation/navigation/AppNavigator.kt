package com.srk.pricetracker.presentation.navigation

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController
import com.srk.pricetracker.core.navigation.Destination
import com.srk.pricetracker.core.navigation.Navigator

/**
 * Implementation of [Navigator] that uses [NavController] for navigation.
 *
 * @param navController The [NavController] to use for navigation.
 */
class AppNavigator(
    private val navController: NavController
) : Navigator {
    override fun navigateUp() {
        navController.navigateUp()
    }

    override fun navigate(destination: Destination) {
        navController.navigate(destination)
    }
}

val LocalNavigator = compositionLocalOf<Navigator> {
    error("Navigator not provided")
}
