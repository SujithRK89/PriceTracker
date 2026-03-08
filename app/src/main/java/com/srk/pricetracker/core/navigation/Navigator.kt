package com.srk.pricetracker.core.navigation

import com.srk.pricetracker.core.Destination

/**
 * Interface that handles navigation within the application.
 */
interface Navigator {
    /**
     * Navigates back to the previous screen.
     */
    fun navigateUp()

    /**
     * Navigates to a specific [destination].
     *
     * @param destination The destination to navigate to.
     */
    fun navigate(destination: Destination)
}
